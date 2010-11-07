package com.nkhoang.gae.view;

import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.Map;


public class XMLView implements View {
    public XMLView() {
    }

    public String getContentType() {
        return "text/xml";
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String xmlString = (String) model.get("data");
        response.setContentType("text/xml");
        response.setContentLength(xmlString.length());

        Writer out = response.getWriter();
        out.write(xmlString);
        out.close();
    }

}
