package com.starterpack.admin.product;

import com.starterpack.category.service.CategoryService;
import com.starterpack.product.dto.ProductAdminListDto;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            Model model) {
        
        // 정렬 설정
        Sort.Direction direction = "desc".equals(sortOrder) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ProductAdminListDto> productPage;
        
        // 검색과 필터링
        if (hasKeyword(keyword) && hasCategoryId(categoryId)) {
            productPage = productService.searchProductsForAdminWithCategoryAndPagination(keyword.trim(), categoryId, pageable);
        } else if (hasKeyword(keyword)) {
            productPage = productService.searchProductsForAdminWithPagination(keyword.trim(), pageable);
        } else if (hasCategoryId(categoryId)) {
            productPage = productService.getProductsForAdminByCategoryWithPagination(categoryId, pageable);
        } else {
            productPage = productService.getProductsForAdminWithPagination(pageable);
        }
        
        model.addAttribute("productPage", productPage);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("categories", categoryService.findAllCategories());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
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

    //검색을 위한 키워드가 포함되어 있는지 체크하는 메서드
    private boolean hasKeyword(String keyword) {
        return keyword != null && !keyword.trim().isEmpty();
    }
    //카테고리로 필터링을 할 categoryId가 포함되어 있는지 체크하는 메서드
    private boolean hasCategoryId(Long categoryId) {
        return categoryId != null;
    }
}
