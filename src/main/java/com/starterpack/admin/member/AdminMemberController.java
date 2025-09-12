package com.starterpack.admin.member;

import com.starterpack.member.dto.MemberCreationRequestDto;
import com.starterpack.member.dto.MemberResponseDto;
import com.starterpack.member.dto.MemberUpdateRequestDto;
import com.starterpack.member.entity.Member;
import com.starterpack.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    // 목록
    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String provider,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            Model model) {

        Sort.Direction direction = "desc".equals(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort); // (현재 예제에선 페이징 직접 안 씀)

        List<MemberResponseDto> members = memberService.findAllMembers();

        if (keyword != null && !keyword.trim().isEmpty()) {
            members = members.stream()
                    .filter(m -> m.name().contains(keyword) || m.email().contains(keyword))
                    .toList();
        }
        if (provider != null && !provider.isEmpty()) {
            members = members.stream()
                    .filter(m -> m.provider().toString().equals(provider))
                    .toList();
        }
        if (isActive != null) {
            Boolean finalIsActive = isActive;
            members = members.stream()
                    .filter(m -> m.isActive().equals(finalIsActive))
                    .toList();
        }

        model.addAttribute("members", members);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedProvider", provider);
        model.addAttribute("selectedIsActive", isActive);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("providers", Member.Provider.values());

        return "admin/members/list";
    }


    // 생성 폼 (뷰만 반환)
    @GetMapping("/form")
    public String createForm(Model model) {
        if (!model.containsAttribute("memberDto")) {
            model.addAttribute("memberDto", new MemberUpdateRequestDto(
                    null, // email
                    null, // password
                    null, // name
                    null  // profileImageUrl
            ));
        }
        model.addAttribute("memberId", null); // 템플릿의 isEdit 분기용
        return "admin/members/form";
    }

    // 생성 처리 (POST /admin/members)
    @PostMapping
    public String create(
            @Valid @ModelAttribute("memberDto") MemberUpdateRequestDto req,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes ra) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("memberId", null);
            return "admin/members/form";
        }

        // 필수값 간단 검증 (필요시 DTO에 @NotBlank로 검증)
        if (req.email() == null || req.email().isBlank()
                || req.password() == null || req.password().isBlank()
                || req.name() == null || req.name().isBlank()) {
            bindingResult.reject("invalid.input", "이메일/비밀번호/이름은 필수입니다.");
            model.addAttribute("memberId", null);
            return "admin/members/form";
        }

        MemberCreationRequestDto createDto = new MemberCreationRequestDto(
                req.email(),
                passwordEncoder.encode(req.password()), // encodedPassword
                req.name(),
                Member.Provider.EMAIL, // 관리자 생성은 EMAIL(또는 LOCAL)로 고정
                null, // providerId 없음
                req.profileImageUrl()
        );

        MemberResponseDto created = memberService.addMember(createDto);

        ra.addFlashAttribute("message", "멤버 '" + created.name() + "' 등록 완료");
        return "redirect:/admin/members";
    }

    // 상세 (숫자만 매칭)
    @GetMapping("/{userId:\\d+}")
    public String detail(@PathVariable Long userId, Model model) {
        MemberResponseDto member = memberService.findMemberById(userId);
        model.addAttribute("member", member);
        return "admin/members/detail";
    }

    // 수정 폼 (숫자만 매칭)
    @GetMapping("/{userId:\\d+}/edit")
    public String editForm(@PathVariable Long userId, Model model) {
        MemberResponseDto member = memberService.findMemberById(userId);

        MemberUpdateRequestDto updateDto = new MemberUpdateRequestDto(
                member.email(),
                null, // 비밀번호 미입력 시 변경 없음
                member.name(),
                member.profileImageUrl()
        );

        model.addAttribute("memberDto", updateDto);
        model.addAttribute("memberId", userId);
        model.addAttribute("originalMember", member);
        return "admin/members/form";
    }

    // 수정 처리 (숫자만 매칭)
    @PostMapping("/{userId:\\d+}/edit")
    public String edit(
            @PathVariable Long userId,
            @Valid @ModelAttribute("memberDto") MemberUpdateRequestDto updateDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            MemberResponseDto originalMember = memberService.findMemberById(userId);
            model.addAttribute("originalMember", originalMember);
            model.addAttribute("memberId", userId);
            return "admin/members/form";
        }

        MemberResponseDto updatedMember = memberService.updateMember(userId, updateDto);
        redirectAttributes.addFlashAttribute("message", "멤버 '" + updatedMember.name() + "' 수정 완료");
        return "redirect:/admin/members";
    }

    // 활성/비활성 토글 (숫자만 매칭)
    @PostMapping("/{userId:\\d+}/toggle-active")
    public String toggleActive(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        MemberResponseDto member = memberService.findMemberById(userId);
        Boolean newStatus = !member.isActive();
        memberService.updateMemberActiveStatus(userId, newStatus);

        String statusText = newStatus ? "활성화" : "비활성화";
        redirectAttributes.addFlashAttribute("message", "멤버 '" + member.name() + "' " + statusText + " 완료");
        return "redirect:/admin/members";
    }

    // 소프트 삭제 (숫자만 매칭)
    @PostMapping("/{userId:\\d+}/delete")
    public String delete(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        MemberResponseDto member = memberService.findMemberById(userId);
        memberService.deleteMember(userId);

        redirectAttributes.addFlashAttribute("message", "멤버 '" + member.name() + "' 삭제 완료");
        return "redirect:/admin/members";
    }
}
