package com.nkhoang.gae.service.impl;

import com.nkhoang.gae.model.Meaning;
import com.nkhoang.gae.model.Sense;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.LookupService;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;

public class OxfordLookupServiceImpl implements LookupService {
    private static final Logger LOG = LoggerFactory.getLogger(OxfordLookupServiceImpl.class.getCanonicalName());
    private static final String OXFORD_URL_LINK = "http://oxforddictionaries.com/definition/";
    // 10 seconds is just enough, do not need to wait more.
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final String HTML_ATTR_CLASS = "class";

    public Word lookup(String word) {
        Word w = null;
        try {
            LOG.debug(String.format("Connecting to ... [%s] ", OXFORD_URL_LINK + word));
            URL url = new URL(OXFORD_URL_LINK + word);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(CONNECTION_TIMEOUT);
            connection.setRequestMethod("GET");
            // get inputStream
            InputStream is = connection.getInputStream();
            // create source HTML
            Source source = new Source(is);
            if (source != null) {
                List<Element> prounEles = source.getAllElementsByClass("pronunciation");
                if (CollectionUtils.isEmpty(prounEles)) {
                    w = null;
                } else {
                    w = new Word();
                    // get pronunciation.
                    List<Element> spanEles = prounEles.get(0).getAllElements("span");
                    w.setPron(spanEles.get(0).getTextExtractor().toString());
                    // start the body content.
                    List<Element> definitionBodyList = source.getAllElementsByClass("senseGroup");
                    // there is only one element like that.
                    List<Element> definitionChildEles = definitionBodyList.get(0).getChildElements();
                    String kind = null;
                    for (Element child : definitionChildEles) {
                        // collect kind.
                        if (checkElementName(child, "h3") &&
                                checkElementProperty(child, HTML_ATTR_CLASS, "partOfSpeech")) {
                            kind = child.getTextExtractor().toString();
                            if (w.getKindidmap().get(kind) == null) {
                                LOG.debug(">>>>>>>>>>>>>>>>>>>>>>>> CRITICAL >>>>>>>>>>>>>>> Kind not found in the map: " + kind);
                                continue;
                            }
                            // collect sense to start a group of meanings.
                        } else if (checkElementName(child, "ul") &&
                                checkElementProperty(child, HTML_ATTR_CLASS, "sense-entry")) {
                            List<Element> senseEntries = child.getChildElements();
                            if (CollectionUtils.isNotEmpty(senseEntries)) {
                                Sense sense = null;
                                for (Element entry : senseEntries) {
                                    if (checkElementProperty(entry, HTML_ATTR_CLASS, "sense")) {
                                        sense = processSense(entry);
                                        sense.setKind(kind);
                                    } else if (checkElementProperty(entry, HTML_ATTR_CLASS, "subSense")) {
                                        if (sense != null) {
                                            Meaning m = processSubSense(entry);
                                            sense.getSubSenses().add(m);
                                        }
                                    }
                                }
                                if (sense != null) {
                                    w.addMeaning(w.getKindidmap().get(kind), sense);
                                }
                            }
                        }
                    }
                }
            }
        } catch (SocketTimeoutException sktoe) {
            LOG.info("Time out while fetching : " + OXFORD_URL_LINK + word);
        } catch (Exception e) {
            LOG.error("Error fetching word using URL: " + OXFORD_URL_LINK + word, e);
        }
        return w;
    }

    private Meaning processSubSense(Element e) {
        Meaning m = new Meaning();
        List<Element> eles = e.getAllElements();
        if (CollectionUtils.isNotEmpty(eles)) {
            for (Element ele : eles) {
                if (checkElementProperty(ele, HTML_ATTR_CLASS, "grammarGroup")) {
                    try {
                        String s = ele.getAllElements("em").get(0).getTextExtractor().toString();
                        if (StringUtils.isNotEmpty(s)) {
                            m.setGrammarGroup(s);
                        }
                    } catch (RuntimeException re) {
                        // ignore it.
                    }
                } else if (checkElementProperty(ele, HTML_ATTR_CLASS, "languageGroup")) {
                    try {
                        String s = ele.getAllElements("em").get(0).getTextExtractor().toString();
                        if (StringUtils.isNotBlank(s)) {
                            m.setLanguageGroup(s);
                        }
                    } catch (RuntimeException re) {
                        // ignore it.
                    }
                } else if (checkElementProperty(ele, HTML_ATTR_CLASS, "definition")) {
                    try {
                        String s = ele.getTextExtractor().toString();
                        if (StringUtils.isNotEmpty(s)) {
                            m.setContent(s);
                        }
                    } catch (RuntimeException re) {
                        // ignore it.
                    }
                } else if (checkElementProperty(ele, HTML_ATTR_CLASS, "exampleGroup")) {
                    try {
                        String s = ele.getTextExtractor().toString();
                        if (StringUtils.isNotBlank(s)) {
                            m.getExamples().add(s);
                        }
                    } catch (RuntimeException re) {
                        // ignore it.
                    }
                }
            }
        }
        return m;
    }

    /**
     * The element <i>e</i> is the element contains the sense definition.
     *
     * @param e the element that contains the sense definition.
     * @return a constructed {@link Sense} object.
     */

    private Sense processSense(Element e) {
        Sense sense = new Sense();
        List<Element> eles = e.getAllElements();
        if (CollectionUtils.isNotEmpty(eles)) {
            for (Element ele : eles) {
                if (checkElementProperty(ele, HTML_ATTR_CLASS, "grammarGroup")) {
                    try {
                        String s = ele.getAllElements("em").get(0).getTextExtractor().toString();
                        if (StringUtils.isNotBlank(s)) {
                            sense.setGrammarGroup(s);
                        }
                    } catch (RuntimeException re) {
                        // ignore it.
                    }
                } else if (checkElementProperty(ele, HTML_ATTR_CLASS, "languageGroup")) {
                    try {
                        String s = ele.getAllElements("em").get(0).getTextExtractor().toString();
                        if (StringUtils.isNotBlank(s)) {
                            sense.setLanguageGroup(s);
                        }
                    } catch (RuntimeException re) {
                        // ignore it.
                    }
                } else if (checkElementProperty(ele, HTML_ATTR_CLASS, "definition")) {
                    try {
                        String s = ele.getTextExtractor().toString();
                        if (StringUtils.isNotBlank(s)) {
                            sense.setDefinition(s);
                        }
                    } catch (RuntimeException re) {
                        // ignore it.
                    }
                } else if (checkElementProperty(ele, HTML_ATTR_CLASS, "exampleGroup")) {
                    try {
                        String s = ele.getTextExtractor().toString();
                        if (StringUtils.isNotBlank(s)) {
                            sense.getExamples().add(s);
                        }
                    } catch (RuntimeException re) {
                        // ignore it.
                    }
                }
            }
        }

        return sense;
    }

    private boolean checkElementName(Element e, String type) {
        return StringUtils.equals(type, e.getName());
    }

    private boolean checkElementProperty(Element e, String propertyType, String propertyValue) {
        return e.getAttributeValue(propertyType) != null
                && e.getAttributeValue(propertyType).contains(propertyValue);
    }
}
