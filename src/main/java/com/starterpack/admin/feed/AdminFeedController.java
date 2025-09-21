package com.starterpack.admin.feed;

import com.starterpack.category.entity.Category;
import com.starterpack.category.service.CategoryService;
import com.starterpack.feed.entity.Feed;
import com.starterpack.feed.entity.FeedType;
import com.starterpack.feed.service.FeedService;
import com.starterpack.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/feeds")
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
            Model model) {

        Page<Feed> feedPage = feedService.searchFeeds(keyword, categoryId, pageable);

        List<Category> categories = categoryService.findAllCategories();

        model.addAttribute("feeds", feedPage.getContent());
        model.addAttribute("feedPage", feedPage);
        model.addAttribute("categories", categories);
        model.addAttribute("feedTypes", FeedType.values());

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);

        return "admin/feed/list";
    }

    @GetMapping("/{id}")
    public String showFeed(@PathVariable Long id, Model model) {
        Feed feed = feedService.getFeed(id);
        model.addAttribute("feed", feed);
        return "admin/feed/detail";
    }

}