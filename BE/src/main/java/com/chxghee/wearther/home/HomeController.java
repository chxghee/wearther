package com.chxghee.wearther.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 홈 화면 컨트롤러
 * Thymeleaf 템플릿을 반환하는 단순 뷰 컨트롤러
 */
@Controller
public class HomeController {

    /**
     * 홈 화면 진입
     * 비즈니스 로직 없이 템플릿만 반환
     *
     * @return home.html 템플릿 뷰 이름
     */
    @GetMapping("/")
    public String home() {
        return "home";
    }
}
