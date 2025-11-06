package com.starterpack.admin.pack;

import com.starterpack.auth.login.Login;
import com.starterpack.category.service.CategoryService;
import com.starterpack.hashtag.dto.HashtagResponseDto;
import com.starterpack.member.entity.Member;
import com.starterpack.pack.dto.PackCreateRequestDto;
import com.starterpack.pack.dto.PackDetailResponseDto;
import com.starterpack.pack.dto.PackItemDto;
import com.starterpack.pack.dto.PackResponseDto;
import com.starterpack.pack.dto.PackUpdateRequestDto;
import com.starterpack.pack.entity.Pack;
import com.starterpack.pack.service.PackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/packs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminPackController {

    private final PackService packService;
    private final CategoryService categoryService;

    /** 리스트 */
    @GetMapping
    public String listAll(Model model){
        model.addAttribute("categories", categoryService.findAllCategories());

        List<Pack> packList = packService.getPacksForAdmin();
        List<PackResponseDto> packs = packList.stream()
                .map(PackResponseDto::from)
                .toList();

        model.addAttribute("packs", packs);
        return "admin/packs/list";
    }

    @GetMapping("/filter")
    public String list(@RequestParam Long categoryId, Model model) {
        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("categoryId", categoryId);

        List<PackResponseDto> packs = packService.getPacksByCategoryForAdmin(categoryId).stream()
                .map(PackResponseDto::from)
                .toList();

        model.addAttribute("packs", packs);
        return "admin/packs/list";
    }

    /** 등록 폼 */
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("packDto", PackCreateRequestDto.EMPTY_FORM);
        model.addAttribute("categories", categoryService.findAllCategories());
        return "admin/packs/form";
    }

    /** 등록 처리 */
    @PostMapping("/add")
    public String add(
            @Valid @ModelAttribute("packDto") PackCreateRequestDto createDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            @Login Member member
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllCategories());
            return "admin/packs/form";
        }

        Pack created = packService.create(createDto, member);
        PackDetailResponseDto dto = PackDetailResponseDto.forAnonymous(created);

        redirectAttributes.addFlashAttribute("message", "패키지 '" + dto.name() + "' 등록 완료");
        return "redirect:/admin/packs";
    }

    /** 수정 폼 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Pack pack = packService.getPackDetailForAdmin(id);
        PackDetailResponseDto detail = PackDetailResponseDto.forAnonymous(pack);

        // PackItem을 PackItemDto로 변환
        List<PackItemDto> items = detail.items();
        List<String> hashtagNames = detail.hashtags().stream()
                .map(HashtagResponseDto::hashtagName)
                .toList();

        PackUpdateRequestDto updateDto = new PackUpdateRequestDto(
                detail.categoryId(),
                detail.name(),
                detail.price(),
                detail.mainImageUrl(),
                detail.description(),
                items,
                hashtagNames
        );

        model.addAttribute("packDto", updateDto);
        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("packId", id);
        return "admin/packs/form";
    }

    /** 수정 처리 */
    @PostMapping("/{id}/edit")
    public String edit(
            @PathVariable Long id,
            @Valid @ModelAttribute("packDto") PackUpdateRequestDto updateDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            @Login Member member
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllCategories());
            model.addAttribute("packId", id);
            return "admin/packs/form";
        }

        Pack updated = packService.update(id, updateDto, member);
        Pack packWithMember = packService.getPackDetailForAdmin(updated.getId());

        PackDetailResponseDto dto = PackDetailResponseDto.forAnonymous(packWithMember);

        redirectAttributes.addFlashAttribute("message", "패키지 '" + dto.name() + "' 수정 완료");
        return "redirect:/admin/packs";
    }

    /** 삭제 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
            RedirectAttributes redirectAttributes,
            @Login Member member) {
        packService.delete(id, member);
        redirectAttributes.addFlashAttribute("message", "패키지 삭제 완료");
        return "redirect:/admin/packs";
    }
}
