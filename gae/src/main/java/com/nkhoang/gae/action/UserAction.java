package com.nkhoang.gae.action;

import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.manager.UserManager;
import com.nkhoang.gae.model.User;
import com.nkhoang.gae.model.UserWord;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.UserService;
import com.nkhoang.gae.view.JSONView;
import com.nkhoang.gae.view.constant.ViewConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import java.util.*;

@Controller
@RequestMapping("/" + ViewConstant.USER_NAMESPACE)
@SessionAttributes("currentUser")
public class UserAction {
    private static Logger LOG = LoggerFactory.getLogger(UserAction.class.getCanonicalName());
    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("authenticationManager")
    private AuthenticationManager authenticationManager;

    @RequestMapping("/userPanel")
    public String showUserPanel() {
        return "/user/userPanel";
    }

    @RequestMapping("/saveWord")
    public ModelAndView saveWord(@RequestParam(required = false) Long wordId) {
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();

        jsonData.put("result", false);
        if (wordId != null) {
            try {
                UserWord userWord = userService.addWord(wordId);
                if (userWord == null) {
                    jsonData.put("error", "Word added before.");
                } else {
                    jsonData.put("result", true);
                }
            } catch (Exception e) {
                jsonData.put("error", "Could not save word.");
            }

        } else {
            jsonData.put("error", "Invalid parameter.");
        }
        modelAndView.addObject(GSONStrategy.DATA, jsonData);
        return modelAndView;
    }

    @RequestMapping("/authenticate")
    public ModelAndView authenticate(@RequestParam(defaultValue = "") String userName, @RequestParam(defaultValue = "") String password) {
        ModelAndView modelAndView = new ModelAndView();
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        Map<String, Object> jsonData = new HashMap<String, Object>();

        try {
            Authentication request = new UsernamePasswordAuthenticationToken(userName, password);
            Authentication authenResult = authenticationManager.authenticate(request);

            SecurityContextHolder.getContext().setAuthentication(authenResult);

            User user = (User) authenResult.getPrincipal();

            jsonData.put("result", true);
            jsonData.put("userName", user.getUsername());
        } catch (AuthenticationException aue) {
            jsonData.put("result", false);
        }
        modelAndView.addObject(GSONStrategy.DATA, jsonData);

        return modelAndView;
    }

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

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
}