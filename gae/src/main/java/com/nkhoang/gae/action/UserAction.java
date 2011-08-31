package com.nkhoang.gae.action;

import java.util.Hashtable;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.nkhoang.gae.manager.UserManager;
import com.nkhoang.gae.model.User;
import com.nkhoang.gae.view.constant.ViewConstant;

@Controller
@RequestMapping("/" + ViewConstant.USER_NAMESPACE)
@SessionAttributes("currentUser")
public class UserAction {
    @Autowired
    private UserManager userService;

    @RequestMapping("/" + ViewConstant.LOGIN_REQUEST)
    public ModelAndView login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal != null && principal instanceof User) {
                currentUser = (User) principal;
            }
        }
        Map<String, Object> model = new Hashtable<String, Object>();
        if (currentUser != null) {
            model.put("isAdmin", true);
            model.put("currentUser", currentUser);
        } else {
            model.put("isAdmin", false);
        }
        return new ModelAndView(ViewConstant.LOGIN_VIEW, model);
    }

    public UserManager getUserService() {
        return userService;
    }

    public void setUserService(UserManager userService) {
        this.userService = userService;
    }

}