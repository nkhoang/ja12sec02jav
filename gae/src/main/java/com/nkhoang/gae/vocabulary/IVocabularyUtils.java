package com.nkhoang.gae.vocabulary;

import com.nkhoang.gae.utils.TemplateUtils;
import com.nkhoang.gae.model.Word;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for building, constructing iVocabulary file.
 */
public class IVocabularyUtils {
    private static final Logger LOG = LoggerFactory
            .getLogger(IVocabularyUtils.class.getCanonicalName());

	/**
     * Build iVocabulary using Freemarker. {@code ivocabulary.ftl} is used as an template then data will be filled into
     * the template to generate the iVocabulary XML output.
     * <p/>
     * The template file have these following placeholders:
     * <p/>
     * <ul>
     * <li>{@code sourceLang}: the source language. Default to English</li>
     * <li>{@code targetLang}: the target language. Default to English</li>
     * <li>{@code author}: the author of this iVocabulary file. Default to {@code Nguyen Khanh Hoang}</li>
     * <li>{@code documentCommnet} the document description.</li>
     * <li>{@code date} the date created.</li>
     * <li>{@code documentTitle} the document title.</li>
     * </ul>
     *
     * @param context  the servlet context which need to get the {@code WEB-INF} folder path.
     * @param wordlist the word list dat.
     * @param writer   the {@link Writer} implementation which using to write the output.
     * @throws IOException       in case the template file is not found.
     * @throws TemplateException in case there is error while parsing the template.
     */
    public static void buildIVocabulary(
            String sourceLang,
            String targetLang,
            String author,
            String comment,
            String date,
            String title,
            String totalWordCount,
            String pageTitle,
            String chapterTitle,
            ServletContext context,
            List<Word> wordlist,
            Writer writer) throws IOException, TemplateException {
        // create root.
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("chapterTitle", "chapterTitle");
        root.put("pageTitle", "pageTitle");
        root.put("words", wordlist);
        root.put("sourceLang", sourceLang);
        root.put("targetLang", targetLang);
        root.put("author", author);
        root.put("documentComment", comment);
        root.put("date", date);
        root.put("documentTitle", title);
        root.put("totalWordCount", totalWordCount);
        root.put("chapterTitle", chapterTitle);
        root.put("pageTitle", pageTitle);

        Template template = TemplateUtils.getConfiguration(context).getTemplate("ivocabulary.ftl");
        template.process(root, writer);
    }
}
