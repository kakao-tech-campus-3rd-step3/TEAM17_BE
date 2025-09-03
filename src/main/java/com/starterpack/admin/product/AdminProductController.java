package com.starterpack.admin.product;

import com.starterpack.category.service.CategoryService;
import com.starterpack.product.dto.ProductCreateRequestDto;
import com.starterpack.product.dto.ProductDetailResponseDto;
import com.starterpack.product.dto.ProductUpdateRequestDto;
import com.starterpack.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.getProductsForAdmin());
        return "admin/products/list";
    }

    // 상품 상세보기 페이지를 보여주는 메서드
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        ProductDetailResponseDto product = productService.getProductDetail(id);
        model.addAttribute("product", product);
        return "admin/products/detail";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("productDto", ProductCreateRequestDto.emptyForm());
        model.addAttribute("categories", categoryService.findAllCategories());
        return "admin/products/form";
    }

    @PostMapping("/add")
    public String add(
            @Valid @ModelAttribute("productDto") ProductCreateRequestDto createDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllCategories());
            return "admin/products/form";
        }
        ProductDetailResponseDto savedProductDto = productService.createProduct(createDto);
        redirectAttributes.addFlashAttribute("message", "상품 '" + savedProductDto.name() + "' 등록 완료");
        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(
            @PathVariable Long id,
            Model model) {
        ProductDetailResponseDto productDetail = productService.getProductDetail(id);

        ProductUpdateRequestDto updateDto = new ProductUpdateRequestDto(
                productDetail.name(),
                productDetail.link(),
                productDetail.productType(),
                productDetail.src(),
                productDetail.cost(),
                productDetail.categoryId()
        );

        model.addAttribute("productDto", updateDto); // Entity -> DTO 변환
        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("productId", id);
        return "admin/products/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(
            @PathVariable Long id,
            @Valid @ModelAttribute("productDto") ProductUpdateRequestDto updateRequestDto,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllCategories());
            model.addAttribute("productId", id);
            return "admin/products/form";
        }

        ProductDetailResponseDto updateResponseDto = productService.updateProduct(id, updateRequestDto);
        redirectAttributes.addFlashAttribute("message", "상품 '" + updateResponseDto.name() + "' 수정 완료");
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("message", "상품 삭제 완료");
        return "redirect:/admin/products";
    }
}
