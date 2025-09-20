package com.starterpack.admin.category;

import com.starterpack.category.entity.Category;
import com.starterpack.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryService.findAllCategories());
        return "admin/categories/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/categories/form";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        Category savedCategory = categoryService.saveCategory(category);
        redirectAttributes.addFlashAttribute("message", "카테고리 '" + savedCategory.getName() + "' 이(가) 성공적으로 등록되었습니다.");
        return "redirect:/admin/categories";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.findCategoryById(id));
        return "admin/categories/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        category.setId(id);
        Category updatedCategory = categoryService.saveCategory(category);
        redirectAttributes.addFlashAttribute("message", "카테고리 '" + updatedCategory.getName() + "' 이(가) 성공적으로 수정되었습니다.");
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.deleteCategory(id);
        redirectAttributes.addFlashAttribute("message", "카테고리가 성공적으로 삭제되었습니다.");
        return "redirect:/admin/categories";
    }
}
