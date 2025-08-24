package com.starterpack.admin.pack;

import com.starterpack.category.service.CategoryService;
import com.starterpack.pack.dto.PackCreateRequestDto;
import com.starterpack.pack.dto.PackDetailResponseDto;
import com.starterpack.pack.dto.PackUpdateRequestDto;
import com.starterpack.pack.service.PackService;
import com.starterpack.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/packs")
@RequiredArgsConstructor
public class AdminPackController {

    private final PackService packService;
    private final CategoryService categoryService;
    private final ProductService productService;

    /** 리스트 */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("packs", packService.getPacks());
        return "admin/packs/list";
    }

    /** 등록 폼 */
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("packDto", PackCreateRequestDto.emptyForm());
        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("products", productService.getProductsForAdmin());
        return "admin/packs/form";
    }

    /** 등록 처리 */
    @PostMapping("/add")
    public String add(
            @Valid @ModelAttribute("packDto") PackCreateRequestDto createDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllCategories());
            model.addAttribute("products", productService.getProductsForAdmin());
            return "admin/packs/form";
        }

        PackDetailResponseDto created = packService.create(createDto);
        redirectAttributes.addFlashAttribute("message", "패키지 '" + created.name() + "' 등록 완료");
        return "redirect:/admin/packs";
    }

    /** 수정 폼 */
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        PackDetailResponseDto detail = packService.getPackDetail(id);

        List<Long> productIds = detail.parts().stream()
                .map(PackDetailResponseDto.PartDto::productId)
                .toList();

        PackUpdateRequestDto updateDto = new PackUpdateRequestDto(
                detail.categoryId(),
                detail.name(),
                productIds,
                detail.cost(),
                detail.description(),
                detail.src()
        );

        model.addAttribute("packDto", updateDto);
        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("products", productService.getProductsForAdmin());
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
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllCategories());
            model.addAttribute("products", productService.getProductsForAdmin());
            model.addAttribute("packId", id);
            return "admin/packs/form";
        }

        PackDetailResponseDto updated = packService.update(id, updateDto);
        redirectAttributes.addFlashAttribute("message", "패키지 '" + updated.name() + "' 수정 완료");
        return "redirect:/admin/packs";
    }

    /** 삭제 */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        packService.delete(id);
        redirectAttributes.addFlashAttribute("message", "패키지 삭제 완료");
        return "redirect:/admin/packs";
    }
}