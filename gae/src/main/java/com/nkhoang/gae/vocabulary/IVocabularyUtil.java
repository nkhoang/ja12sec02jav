package com.nkhoang.gae.vocabulary;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class IVocabularyUtil {
    private static final Logger LOG = LoggerFactory
            .getLogger(IVocabularyUtil.class.getCanonicalName());

    private static Configuration _cfg = null;

    private static Configuration getConfiguration(ServletContext context) {
        if (_cfg == null) {
            _cfg = new Configuration();
            _cfg.setServletContextForTemplateLoading(context, "WEB-INF/templates");
            _cfg.setObjectWrapper(new DefaultObjectWrapper());
        }

        return _cfg;
    }

    public static void buildIVocabulary(ServletContext context) throws IOException, TemplateException {
        // create root.
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("hello", "hello");

        Template template = getConfiguration(context).getTemplate("ivocabulary.ftl");
        StringWriter writer = new StringWriter();
        template.process(root, writer);
        LOG.info(writer.toString());
    }
}
