package com.starterpack.admin.product;

import com.starterpack.category.service.CategoryService;
import com.starterpack.product.dto.ProductRequestDto;
import com.starterpack.product.entity.Product;
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
        model.addAttribute("products", productService.findAllProducts());
        return "admin/products/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("productDto", new ProductRequestDto());
        model.addAttribute("categories", categoryService.findAllCategories());
        return "admin/products/form";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("productDto") ProductRequestDto dto,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllCategories());
            return "admin/products/form";
        }
        Product product = new Product();
        product.setName(dto.getName());
        product.setLink(dto.getLink());
        product.setProductType(dto.getProductType());
        product.setSrc(dto.getSrc());
        product.setCost(dto.getCost());

        Product savedProduct = productService.saveProduct(product, dto.getCategoryId());
        redirectAttributes.addFlashAttribute("message", "상품 '" + savedProduct.getName() + "' 등록 완료");
        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productService.findProductById(id);
        model.addAttribute("productDto", new ProductRequestDto(product)); // Entity -> DTO 변환
        model.addAttribute("categories", categoryService.findAllCategories());
        return "admin/products/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute("productDto") ProductRequestDto dto,
            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAllCategories());
            return "admin/products/form";
        }
        Product product = new Product();
        product.setId(id);
        product.setName(dto.getName());
        product.setLink(dto.getLink());
        product.setProductType(dto.getProductType());
        product.setSrc(dto.getSrc());
        product.setCost(dto.getCost());

        Product updatedProduct = productService.saveProduct(product, dto.getCategoryId());
        redirectAttributes.addFlashAttribute("message", "상품 '" + updatedProduct.getName() + "' 수정 완료");
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("message", "상품 삭제 완료");
        return "redirect:/admin/products";
    }
}
