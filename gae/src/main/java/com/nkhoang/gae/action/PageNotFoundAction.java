package com.nkhoang.gae.action;

import com.nkhoang.gae.view.constant.ViewConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageNotFoundAction {
    @RequestMapping("/**")
    public String redirect() {
        return ViewConstant.PAGE_NOT_FOUND_VIEW;
    }
}
