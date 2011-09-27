package com.nkhoang.vocabulary;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nkhoang.model.Word;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class VocabularyUtils {
    private static final Logger LOG = LoggerFactory.getLogger(VocabularyUtils.class.getCanonicalName());
    private static JAXBContext context;

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

    public static List<String> getAllWords() {
        HttpClient client = new HttpClient();
        String searchUrl = "http://dictionary-misschara.appspot.com/services/vocabulary/getAll";
        GetMethod get = new GetMethod(searchUrl);
        List<String> result = new ArrayList<String>();
        try {
            client.executeMethod(get);
            String json = get.getResponseBodyAsString();
            if (StringUtils.isNotBlank(json)) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<String>>() {}.getType();
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

    public static Word lookupWord(String word) {
        Word w = null;
        HttpClient client = new HttpClient();
        String searchUrl = "http://dictionary-misschara.appspot.com/services/vocabulary/search/";
        if (StringUtils.isNotBlank(word)) {
            searchUrl += word;
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
}
