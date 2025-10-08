package com.starterpack.admin.feed;

import com.starterpack.category.entity.Category;
import com.starterpack.category.service.CategoryService;
import com.starterpack.exception.BusinessException;
import com.starterpack.exception.ErrorCode;
import com.starterpack.feed.dto.FeedCreateRequestDto;
import com.starterpack.feed.dto.FeedUpdateRequestDto;
import com.starterpack.feed.entity.Feed;
import com.starterpack.feed.service.FeedService;
import com.starterpack.member.entity.Member;
import com.starterpack.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/feeds")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminFeedController {

    private final FeedService feedService;
    private final MemberService memberService;
    private final CategoryService categoryService;

    @GetMapping
    public String listFeeds(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false, defaultValue = "id,desc") String sort,
            Model model) {

        Page<Feed> feedPage = feedService.searchFeeds(keyword, categoryId, pageable);

        List<Category> categories = categoryService.findAllCategories();

        model.addAttribute("feeds", feedPage.getContent());
        model.addAttribute("feedPage", feedPage);
        model.addAttribute("categories", categories);

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);

        model.addAttribute("sort", sort);

        return "admin/feed/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        List<Member> members = memberService.findAllMembers();
        List<Category> categories = categoryService.findAllCategories();

        model.addAttribute("members", members);
        model.addAttribute("categories", categories);

        return "admin/feed/form";
    }

    @PostMapping("/add")
    public String addFeed(@ModelAttribute FeedCreateRequestDto request,
            @RequestParam Long userId,
            RedirectAttributes redirectAttributes) {
        Member member = memberService.findAllMembers().stream()
                .filter(m -> m.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        feedService.addFeed(member, request);

        redirectAttributes.addFlashAttribute("message", "Feed가 성공적으로 등록되었습니다.");
        return "redirect:/admin/feeds";
    }

    @GetMapping("/{id}")
    public String showFeed(@PathVariable Long id, Model model) {
        Feed feed = feedService.getFeedByAdmin(id);
        model.addAttribute("feed", feed);
        return "admin/feed/detail";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Feed feed = feedService.getFeedByAdmin(id);
        List<Category> categories = categoryService.findAllCategories();

        model.addAttribute("feed", feed);
        model.addAttribute("feedId", id);
        model.addAttribute("categories", categories);

        return "admin/feed/form";
    }

    @PostMapping("/{id}/edit")
    public String updateFeed(@PathVariable Long id,
            @ModelAttribute FeedUpdateRequestDto request,
            RedirectAttributes redirectAttributes) {

        feedService.updateFeedByAdmin(id, request);

        redirectAttributes.addFlashAttribute("message", "Feed가 성공적으로 수정되었습니다.");
        return "redirect:/admin/feeds/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteFeed(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        feedService.deleteFeedByAdmin(id);

        redirectAttributes.addFlashAttribute("message", "Feed가 성공적으로 삭제되었습니다.");
        return "redirect:/admin/feeds";
    }
}