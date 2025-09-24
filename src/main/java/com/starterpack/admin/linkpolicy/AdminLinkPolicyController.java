package com.starterpack.admin.linkpolicy;

import com.starterpack.linkpolicy.dto.LinkPolicyCreateRequestDto;
import com.starterpack.linkpolicy.dto.LinkPolicyDeleteRequestDto;
import com.starterpack.linkpolicy.dto.LinkPolicyResponseDto;
import com.starterpack.linkpolicy.service.LinkPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/link-policies")
@RequiredArgsConstructor
public class AdminLinkPolicyController {

    private final LinkPolicyService linkPolicyService;

    // 링크 정책 목록 페이지
    @GetMapping
    public String list(Model model) {
        List<LinkPolicyResponseDto> policies = linkPolicyService.getAll();
        model.addAttribute("policies", policies);
        return "admin/link-policies/list";
    }

    // 링크 정책 추가 폼 페이지
    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("policy", new LinkPolicyCreateRequestDto("", ""));
        return "admin/link-policies/form";
    }

    // 링크 정책 추가 처리
    @PostMapping
    public String create(@ModelAttribute LinkPolicyCreateRequestDto request, RedirectAttributes redirectAttributes) {
        try {
            linkPolicyService.create(request);
            redirectAttributes.addFlashAttribute("message", "링크 정책이 성공적으로 추가되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "링크 정책 추가 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/link-policies";
    }

    // 링크 정책 삭제 처리
    @PostMapping("/delete")
    public String delete(@ModelAttribute LinkPolicyDeleteRequestDto request, RedirectAttributes redirectAttributes) {
        try {
            linkPolicyService.delete(request);
            redirectAttributes.addFlashAttribute("message", "링크 정책이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "링크 정책 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/link-policies";
    }
}
