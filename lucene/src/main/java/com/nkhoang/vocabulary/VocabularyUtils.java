package com.nkhoang.vocabulary;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nkhoang.common.WebUtils;
import com.nkhoang.common.exception.GAEException;
import com.nkhoang.model.Word;
import com.nkhoang.model.WordLucene;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VocabularyUtils {
   private static final Logger LOG = LoggerFactory.getLogger(VocabularyUtils.class.getCanonicalName());
   private static final String HOST_NAME = "mini-dict.appspot.com";
   private static JAXBContext context;
   private static JAXBContext wordLuceneContext;


   private static JAXBContext getWordLuceneJAXBContext() {
      if (wordLuceneContext == null) {
         try {
            wordLuceneContext = JAXBContext.newInstance(WordLucene.class);
         } catch (JAXBException jaxbe) {
            LOG.error("Could not initialize jaxb context.");
         }
      }

      return wordLuceneContext;
   }


   private static JAXBContext getJAXBContext() {
      if (context == null) {
         try {
            context = JAXBContext.newInstance(Word.class);
         } catch (JAXBException jaxbe) {
            LOG.error("Could not initialize jaxb context.");
         }
      }

      return context;
   }

   /**
    * Get all words from server.
    *
    * @return a list of WordLucene.
    */
   public static List<WordLucene> getAllLuceneWords(String hostname) {
      if (StringUtils.isEmpty(hostname)) {
         LOG.info("Using default hostname: " + HOST_NAME);
         hostname = HOST_NAME;
      }
      HttpClient client = new HttpClient();
      String searchUrl = "http://" + hostname + "/services/vocabulary/lucene/getAll";
      GetMethod get = new GetMethod(searchUrl);
      List<WordLucene> wordLucenes = new ArrayList<WordLucene>();
      try {
         client.executeMethod(get);
         String response = get.getResponseBodyAsString();
         if (StringUtils.isNotEmpty(response)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            List<String> results = gson.fromJson(response, listType);
            if (org.apache.commons.collections.CollectionUtils.isNotEmpty(results)) {
               for (String s : results) {
                  InputStream is = IOUtils.toInputStream(s);
                  Unmarshaller unmarshaller = getWordLuceneJAXBContext().createUnmarshaller();
                  WordLucene wl = (WordLucene) unmarshaller.unmarshal(is);
                  wordLucenes.add(wl);
               }
            }
         }
      } catch (IOException ioe) {
         LOG.error("Could not communicate with server.");
      } catch (JAXBException jaxbe) {
         LOG.error("Could not parse entity.", jaxbe);
      }
      return wordLucenes;
   }


   public static List<String> getAllWordsByRange(String hostname, int offset, int size, String direction) throws Exception {
      if (StringUtils.isEmpty(hostname)) {
         hostname = HOST_NAME;
      }
      HttpClient client = new HttpClient();
      String searchUrl = "http://" + hostname + "/services/vocabulary/getAll";
      GetMethod get = new GetMethod(searchUrl);
      NameValuePair[] params = {
            new NameValuePair("offset", offset + ""),
            new NameValuePair("size", size + ""),
            new NameValuePair("direction", direction)
      };
      get.setQueryString(params);
      LOG.info(get.getURI().toString());
      List<String> result = new ArrayList<String>();
      try {
         client.executeMethod(get);
         String json = get.getResponseBodyAsString();
         if (StringUtils.isNotBlank(json)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {
            }.getType();
            result = gson.fromJson(json, listType);
         }

      } catch (IOException ioex) {
         LOG.error("Could not communicate with server to lookup word.");
      }
      return result;
   }


   public static Word lookupWordById(String id) {
      Word w = null;
      HttpClient client = new HttpClient();
      String searchUrl = "http://dictionary-misschara.appspot.com/services/vocabulary/search/id/";
      if (StringUtils.isNotBlank(id)) {
         searchUrl += id;
         GetMethod get = new GetMethod(searchUrl);

         try {
            client.executeMethod(get);
            InputStream is = get.getResponseBodyAsStream();
            if (is != null) {
               w = (Word) getJAXBContext().createUnmarshaller().unmarshal(is);
            }

         } catch (IOException ioex) {
            LOG.error("Could not communicate with server to lookup word.");
         } catch (JAXBException jaxbe) {
            LOG.error("Could not convert received stream to entity.", jaxbe);
         }
      }
      return w;

   }

   /**
    * Get the word from the main site through REST WS.
    *
    * @param hostname the hostname to get word.
    * @param word     the word description used to get the real word.
    * @return the {@link Word} entity.
    */
   public static Word lookupWord(String hostname, String word, StandardPBEStringEncryptor propertyEncryptor) throws Exception {
      Word w = null;
      try {
         URL url = new URL("http://" + hostname + "/services/vocabulary/search/" + word);
         LOG.info("URL :" + url.toString());
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setReadTimeout(10000);
         connection.setRequestMethod("GET");
         connection.setRequestProperty("signature", WebUtils.createWSSignature("GAE"));
         String encryptedTime = propertyEncryptor.encrypt("" + System.currentTimeMillis());
         connection.setRequestProperty("key", encryptedTime);

         InputStream is = connection.getInputStream();

         if (is != null) {
            w = (Word) getJAXBContext().createUnmarshaller().unmarshal(is);
         }
      } catch (MalformedURLException mfue) {
         LOG.error("The lookup word using RESTful services is provided with incorrect URL.", mfue);
         throw mfue;
      } catch (IOException ioe) {
         LOG.error("Could not communicate with the server.", ioe);
         throw ioe;
      } catch (JAXBException jaxbe) {
         LOG.error("Could not convert received stream to entity.", jaxbe);
         throw jaxbe;
      }
      return w;
   }
}
