package com.starterpack.admin.home;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 관리자 홈페이지 컨트롤러
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminHomeController {

    /**
     * 관리자 홈페이지 메인화면을 보여주는 메서드
     */
    @GetMapping
    public String home() {
        return "admin/home";
    }
}
