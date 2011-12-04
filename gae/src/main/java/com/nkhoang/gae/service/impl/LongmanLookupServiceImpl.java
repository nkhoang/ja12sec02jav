package com.nkhoang.gae.service.impl;

import com.nkhoang.gae.model.Meaning;
import com.nkhoang.gae.model.Phrase;
import com.nkhoang.gae.model.Sense;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.LookupService;
import net.htmlparser.jericho.Element;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LongmanLookupServiceImpl implements LookupService {
   private static final Logger LOG = LoggerFactory
         .getLogger(LongmanLookupServiceImpl.class.getCanonicalName());
   private static final String SERVICE_NAME = "longman";
   private static final String OXFORD_URL_LINK = "http://oxforddictionaries.com/definition/";
   // 10 seconds is just enough, do not need to wait more.
   private static final int CONNECTION_TIMEOUT = 10000;
   private static final String HTML_ATTR_CLASS = "class";

   public String getServiceName() {
      return SERVICE_NAME;
   }

   public Word lookup(String word) {
      return null;
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
            } else if (checkElementProperty(ele, HTML_ATTR_CLASS, "wordForm")) {
               try {
                  String s = ele.getTextExtractor().toString();
                  if (StringUtils.isNotBlank(s)) {
                     m.setWordForm(s);
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

   private void processPhrase(Element e, Word w) {
      Phrase phrase = null;

      List<Element> dls = e.getAllElements("dl");
      if (CollectionUtils.isNotEmpty(dls)) {
         phrase = null;
         String phraseKind = null;
         // get phrase title
         List<Element> titleList = e.getAllElements("h2");
         if (CollectionUtils.isNotEmpty(titleList)) {
            phraseKind = titleList.get(0).getTextExtractor().toString();
         }
         // get meanings.
         List<Element> phraseEntryList = dls.get(0).getChildElements();
         for (Element child : phraseEntryList) {
            if (checkElementName(child, "dt")) {
               if (phrase != null) {
                  w.addPhrase(phraseKind, phrase);
               }
               phrase = new Phrase();
               String s = child.getTextExtractor().toString();
               if (StringUtils.isNotBlank(s)) {
                  phrase.setDescription(s);
               }
            } else if (checkElementName(child, "dd")) {
               List<Element> senseList = child.getAllElementsByClass("sense");
               if (CollectionUtils.isNotEmpty(senseList)) {
                  for (Element senseEle : senseList) {
                     Sense sense = processSense(senseEle);
                     if (sense != null) {
                        phrase.getSenseList().add(sense);
                     }
                  }
               }
            }
         }
      }
   }

   /**
    * The element <i>e</i> is the element contains the sense definition.
    *
    * @param e the element that contains the sense definition.
    * @return a constructed {@link com.nkhoang.gae.model.Sense} object.
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
      return e.getAttributeValue(propertyType) != null && e.getAttributeValue(propertyType).contains(propertyValue);
   }
}
