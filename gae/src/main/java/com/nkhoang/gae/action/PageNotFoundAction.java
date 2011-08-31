package com.nkhoang.gae.action;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nkhoang.gae.view.constant.ViewConstant;

@Controller
public class PageNotFoundAction {
    @RequestMapping("/**")
    public String redirect() {
        return ViewConstant.PAGE_NOT_FOUND_VIEW;
    }
}
