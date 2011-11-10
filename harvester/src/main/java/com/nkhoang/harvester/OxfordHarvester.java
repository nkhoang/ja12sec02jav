package com.nkhoang.harvester;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OxfordHarvester {
    private static final Logger LOG = LoggerFactory.getLogger(OxfordHarvester.class.getCanonicalName());
    private static final String OXFORD_URL_LINK = "http://oxforddictionaries.com/definition/";
    private static final int CONNECTION_TIMEOUT = 10000;

    public static List<String> getRelatedWords(String rootWord) {
        List<String> result = new ArrayList<String>();
        try {
            URL url = new URL(OXFORD_URL_LINK + rootWord);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("GET");
            // get inputStream
            InputStream is = connection.getInputStream();
            // create source HTML
            Source source = new Source(is);
            if (source != null) {
                List<Element> relatedLinkContainers = source.getAllElementsByClass("externalLinks");
                if (CollectionUtils.isNotEmpty(relatedLinkContainers)) {
                    List<Element> relatedLinks = relatedLinkContainers.get(0).getChildElements();
                    for (Element link : relatedLinks) {
                        if (checkElementName(link, "li")) {
                            result.add(link.getTextExtractor().toString());
                        }
                    }
                } else {
                    LOG.error("No 'externalLinks' class in the requested page.");
                }
            }
        } catch (Exception e) {
            // LOG.error("Could not get related words for: " + rootWord, e);
        }

        return result;
    }

    private static boolean checkElementName(Element e, String type) {
        return StringUtils.equals(type, e.getName());
    }

}
