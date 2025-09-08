package com.starterpack.admin.member;

import com.starterpack.member.dto.MemberResponseDto;
import com.starterpack.member.dto.MemberUpdateRequestDto;
import com.starterpack.member.entity.Member;
import com.starterpack.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // 멤버 목록 조회
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
        
        // 정렬 설정
        Sort.Direction direction = "desc".equals(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // 검색 조건에 따른 멤버 조회
        List<MemberResponseDto> members = memberService.findAllMembers();
        
        // 간단한 필터링 (실제로는 Repository에서 처리하는 것이 좋음)
        if (keyword != null && !keyword.trim().isEmpty()) {
            members = members.stream()
                    .filter(member -> member.getName().contains(keyword) || 
                                   member.getEmail().contains(keyword))
                    .toList();
        }
        
        if (provider != null && !provider.isEmpty()) {
            members = members.stream()
                    .filter(member -> member.getProvider().toString().equals(provider))
                    .toList();
        }
        
        if (isActive != null) {
            members = members.stream()
                    .filter(member -> member.getIsActive().equals(isActive))
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

    // 멤버 상세 조회
    @GetMapping("/{userId}")
    public String detail(@PathVariable Long userId, Model model) {
        MemberResponseDto member = memberService.findMemberById(userId);
        model.addAttribute("member", member);
        return "admin/members/detail";
    }

    // 멤버 수정 폼
    @GetMapping("/{userId}/edit")
    public String editForm(@PathVariable Long userId, Model model) {
        MemberResponseDto member = memberService.findMemberById(userId);
        
        MemberUpdateRequestDto updateDto = new MemberUpdateRequestDto(
                member.getEmail(),
                null, // 비밀번호는 수정하지 않음
                member.getName(),
                member.getProfileImageUrl()
        );
        
        model.addAttribute("memberDto", updateDto);
        model.addAttribute("memberId", userId);
        model.addAttribute("originalMember", member);
        return "admin/members/form";
    }

    // 멤버 수정 처리
    @PostMapping("/{userId}/edit")
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
        redirectAttributes.addFlashAttribute("message", "멤버 '" + updatedMember.getName() + "' 수정 완료");
        return "redirect:/admin/members";
    }

    // 멤버 활성화 상태 변경
    @PostMapping("/{userId}/toggle-active")
    public String toggleActive(
            @PathVariable Long userId,
            RedirectAttributes redirectAttributes) {
        
        MemberResponseDto member = memberService.findMemberById(userId);
        Boolean newStatus = !member.getIsActive();
        
        memberService.updateMemberActiveStatus(userId, newStatus);
        
        String statusText = newStatus ? "활성화" : "비활성화";
        redirectAttributes.addFlashAttribute("message", "멤버 '" + member.getName() + "' " + statusText + " 완료");
        return "redirect:/admin/members";
    }

    // 멤버 소프트 삭제
    @PostMapping("/{userId}/delete")
    public String delete(
            @PathVariable Long userId,
            RedirectAttributes redirectAttributes) {
        
        MemberResponseDto member = memberService.findMemberById(userId);
        memberService.deleteMember(userId);
        
        redirectAttributes.addFlashAttribute("message", "멤버 '" + member.getName() + "' 삭제 완료");
        return "redirect:/admin/members";
    }
}
