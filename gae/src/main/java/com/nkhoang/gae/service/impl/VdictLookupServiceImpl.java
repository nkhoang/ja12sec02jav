package com.nkhoang.gae.service.impl;

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
import java.net.URL;
import java.util.List;

public class VdictLookupServiceImpl implements LookupService {
   private static final Logger LOG = LoggerFactory
         .getLogger(OxfordLookupServiceImpl.class.getCanonicalName());
   private static final String SERVICE_NAME = "vdict";
   private static final String VN_DICT_CONTENT_CLASS = "container";
   private static final String VN_DICT_CLASS_KIND = "phanloai";
   private static final String HTML_ATTR_CLASS = "class";
   private static final String HTML_DIV = "div";
   private static final int MAX_NUM_VN_WORD_IN_KIND = 3;


   public Word lookup(String word) {
     LOG.info("Looking up using: " + SERVICE_NAME);
     word = word.trim().toLowerCase();
      String lookupURL = "http://m.vdict.com/?word=" + word.trim() + "&dict=1&searchaction=Lookup";

      // construct word obj.
      Word aWord = new Word();
      try {
         URL url = new URL(lookupURL);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestMethod("GET");
         InputStream is = connection.getInputStream();

         Source source = new Source(is);
         if (source != null) {
            List<Element> contentEles = source.getAllElementsByClass(VN_DICT_CONTENT_CLASS);
            if (CollectionUtils.isEmpty(contentEles)) {
               return null;
            }

            Element targetContent = contentEles.get(0);
            // set description.
            aWord.setDescription(word);
            // starting to update meaning to the word.
            Sense sense = new Sense();
            // starting with " kind "
            String kind = "";
            List<Element> eles = targetContent.getChildElements();
            for (Element ele : eles) {
               if (ele.getName().equals(HTML_DIV)) {
                  kind = "";
               }
               if (ele.getAttributeValue(HTML_ATTR_CLASS) != null &&
                     ele.getAttributeValue(HTML_ATTR_CLASS).equals(VN_DICT_CLASS_KIND)) {
                  // use TextExtractor to trim unwanted content.
                  kind = ele.getTextExtractor().toString().trim();
                  if (kind != null) {
                     // may be a compond of something like: danh tu, ngoai dong tu
                     if (kind.contains(",")) {
                        // just get the first one. May be there are some more exceptional cases in the future.
                        kind = kind.split(",")[0];
                     }
                     String[] words = kind.split(" ");
                     kind = "";
                     // maximum length = " ngoai dong tu ". Composed by 3 word.
                     int limit = words.length > MAX_NUM_VN_WORD_IN_KIND ? MAX_NUM_VN_WORD_IN_KIND : words.length;
                     for (int i = 0; i < limit; i++) {
                        kind += words[i] + " ";
                     }
                     kind = kind.trim();
                  }
               } else if (StringUtils.equals(ele.getName(), "ul") && StringUtils.isNotEmpty(kind)) {
                  String className = ele.getAttributeValue(HTML_ATTR_CLASS);
                  if (StringUtils.isNotBlank(className) && StringUtils.equals(className, "list1")) {
                     List<Element> meaningLis = ele.getChildElements();
                     for (Element meaningLi : meaningLis) {
                        if (StringUtils.equals(meaningLi.getName(), "li")) {
                           List<Element> liContent = meaningLi.getChildElements();
                           for (Element content : liContent) {
                              if (StringUtils.equals(content.getName(), "b")) {
                                 String contentRaw = content.getContent().toString();
                                 sense = new Sense();
                                 sense.setKind(kind);
                                 sense.setDefinition(contentRaw);
                                 //aa meaning = new Meaning(contentRaw, aWord.getKindidmap().get(kind));
                                 // LOG.debug("content : " + contentRaw);
                              } else if (StringUtils.equals(content.getName(), "ul") &&
                                    StringUtils.isNotEmpty(sense.getDefinition())) {
                                 // should not store any meanings if content is null or blank.
                                 String example = content.getChildElements().get(0).getChildElements().get(0)
                                       .getContent().toString();
                                 if (StringUtils.isNotBlank(example)) {
                                    sense.getExamples().add(example);
                                    // meaning.addExample(example);
                                 }
                                 // LOG.debug("Example: " + example);
                              }
                           }
                        }
                        // Long kindId = aWord.getKindidmap().get(kind);
                        // Long kindId = null;
                        /*if (kindId == null) {
                           LOG.info(">>>>>>>>>>>>>>>>>> CRITICAL >>>>>>>>>>>>>>>>>>>>> Null for kind: " + kind);
                        }*/
                        if (sense != null && StringUtils.isNotBlank(sense.getDefinition())) {
                           aWord.addMeaning(kind, sense);
                           //aa aWord.addMeaning(kindId, meaning);
                        }
                     }
                  }
               }
            }
         }
         return aWord;
      } catch (Exception e) {
         LOG.error("Could not get words from: " + lookupURL, e);
      }

      return aWord;
   }

   public String getServiceName() {
      return SERVICE_NAME;
   }
}
