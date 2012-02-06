package com.nkhoang.gae.service.impl;

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


public class CambridgeLookupServiceImpl implements LookupService {
   private static final Logger LOG = LoggerFactory
         .getLogger(CambridgeLookupServiceImpl.class.getCanonicalName());

   private static final String SERVICE_NAME = "vdict";

   private enum DICTIONARY_TYPE {
      CLASS,
      ID
   }

   private static final String CAMBRIDGE_DICT_CONTENT_CLASS = "cdo-section";
   private static final String CAMBRIDGE_DICT_URL_TYPE = "http://dictionary.cambridge.org/search/british/";
   private static final String CAMBRIDGE_DICT_IDIOM_TYPE = "?type=idiom";
   private static final String CAMBRIDGE_DICT_PHRASAL_VERB_TYPE = "?type=pv&";
   private static final String CAMBRIDGE_DICT_TYPE_QUERY = "&q=";
   private static final String CAMBRIDGE_DICT_URL = "http://dictionary.cambridge.org/dictionary/british/";
   private static final String CAMBRIDGE_DICT_CONTENT_CLASS_2nd = "gwblock ";
   private static final String CAMBRIDGE_DICT_KIND_CLASS = "header";

   public String getServiceName() {
      return SERVICE_NAME;
   }

   public Word lookup(String lookupWord) {
      // remove all unnecessary chars.
      lookupWord = lookupWord.trim().toLowerCase();
      LOG.info("looking up PRON for this word : " + lookupWord);
      Word w = new Word();
      w.setDescription(lookupWord);
      try {
         // lookup using normal URL form.
         Source source = checkWordExistence(
               CAMBRIDGE_DICT_URL, lookupWord, CAMBRIDGE_DICT_CONTENT_CLASS,
               DICTIONARY_TYPE.CLASS);
         int i = 1;
         // if the word has more than one meaning then the URL format is changed. We need to use different URL to fetch
         // the data.
         if (source == null) {
            // check it again
            source = checkWordExistence(
                  CAMBRIDGE_DICT_URL, lookupWord + "_" + i + "?q=" + lookupWord, CAMBRIDGE_DICT_CONTENT_CLASS,
                  DICTIONARY_TYPE.CLASS);
         }
         while (source != null) {
            // process the content
            List<Element> contentEles = source.getAllElementsByClass(CAMBRIDGE_DICT_CONTENT_CLASS_2nd);
            // LOG.info("content size = " + contentEles.size());
            if (CollectionUtils.isNotEmpty(contentEles)) {
               // should be one
               Element targetContent = contentEles.get(0);
               String kind = "";
               // get kind
               List<Element> headers = targetContent.getAllElementsByClass(CAMBRIDGE_DICT_KIND_CLASS);
               if (CollectionUtils.isNotEmpty(headers)) {
                  Element header = headers.get(0);

                  List<Element> kinds = header.getAllElementsByClass("pos");
                  if (CollectionUtils.isNotEmpty(kinds)) {
                     kind = kinds.get(0).getContent().toString().trim();
                  }
               }

               List<Element> additional_headers = targetContent.getAllElementsByClass("additional_header");
               if (CollectionUtils.isNotEmpty(additional_headers)) {
                  Element additional_header = additional_headers.get(0);
                  List<Element> prons = additional_header.getAllElementsByClass("pron");
                  // get Pron
                  if (CollectionUtils.isNotEmpty(prons)) {
                     String pron = prons.get(0).getTextExtractor().toString();
                     LOG.debug("Pron: " + pron);
                     w.setPron(pron);
                  }
                  // get mp3 file
                  List<Element> sounds = additional_header.getAllElementsByClass("sound");
                  // may have 2
                  if (CollectionUtils.isNotEmpty(sounds)) {
                     Element sound = null;
                     if (sounds.size() == 1) {
                        sound = sounds.get(0);
                     } else if (sounds.size() == 2) {
                        sound = sounds.get(1);
                     }

                     // process
                     String soundSource = sound.getAttributeValue("onclick");
                     String soundSrc = soundSource.replace("/media", "http://dictionary.cambridge.org/media");
                     // LOG.info("Found a sound source: " + soundSrc);
                     w.setSoundSource(soundSrc);
                  }
                  break;
               }
            } else {
               break;
            }
         }
      } catch (Exception e) {
         LOG.error("Exception", e);
      }
      
      return w;
   }

   /**
    * Check word exsitence.
    *
    * @return null or Source object.
    */
   private Source checkWordExistence(
         String urlLink, String word, String targetIdentifier, DICTIONARY_TYPE targetType) {
      Source source = null;
      try {
         LOG.debug("Check word existence: " + urlLink + word);
         URL url = new URL(urlLink + word);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestMethod("GET");
         // get inputStream
         InputStream is = connection.getInputStream();
         // create source HTML
         source = new Source(is);
         if (source != null) {
            boolean wordFound = true;
            switch (targetType) {
               case CLASS:
                  List<Element> contentEles = source.getAllElementsByClass(targetIdentifier);
                  if (CollectionUtils.isEmpty(contentEles)) {
                     wordFound = false;
                     source = null;
                  }
                  break;
               case ID:
                  Element content = source.getElementById(targetIdentifier);
                  if (content == null) {
                     wordFound = false;
                     source = null;
                  }
                  break;
            }
            if (wordFound) {
               if (StringUtils.equals(CAMBRIDGE_DICT_CONTENT_CLASS, targetIdentifier)) {
                  // process the coxntent
                  List<Element> contents = source.getAllElementsByClass(CAMBRIDGE_DICT_CONTENT_CLASS_2nd);
                  if (CollectionUtils.isEmpty(contents)) {
                     source = null;
                  }
               }
            }
         }
      } catch (SocketTimeoutException sktoe) {
         LOG.info("Time out while fetching : " + urlLink + word);
      } catch (Exception e) {
         LOG.error("Error fetching word using URL: " + urlLink + word);
      }
      return source;
   }


}
