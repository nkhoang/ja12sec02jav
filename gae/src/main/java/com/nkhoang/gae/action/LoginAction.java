package com.nkhoang.gae.action;

import com.nkhoang.gae.model.User;
import com.nkhoang.gae.view.constant.ViewConstant;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Hashtable;
import java.util.Map;

@Controller
@RequestMapping("/")
public class LoginAction {
    @RequestMapping("/" + ViewConstant.INDEX_REQUEST)
    public ModelAndView welcome() {

        // load user authentication
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal != null && principal instanceof User) {
                currentUser = (User) principal;
            }
        }
        Map<String, Object> model = new Hashtable<String, Object>();
        // enable javascript flag: isAdmin
        if (currentUser != null) {
            model.put("isAdmin", true);
        } else {
            model.put("isAdmin", false);
        }
        return new ModelAndView(ViewConstant.WELCOME_VIEW, model);
    }
}
