package com.nkhoang.gae.utils;

import com.nkhoang.gae.model.User;
import net.htmlparser.jericho.Source;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebUtils {

    public static Source retrieveWebContent(String websiteURL) throws IOException {
        Source source = null;
        URL url = new URL(websiteURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        // get inputStream
        InputStream is = connection.getInputStream();
        // create source HTML
        source = new Source(is);

        return source;
    }

    /**
     * Get current logged in user.
     * @return current user or null.
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal != null && principal instanceof User) {
                currentUser = (User) principal;
            }
        }
        return currentUser;
    }
}
