package com.nkhoang.gae.utils;

import net.htmlparser.jericho.Source;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: hoangnk
 * Date: Nov 5, 2010
 * Time: 12:54:07 AM
 * To change this template use File | Settings | File Templates.
 */
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

}
