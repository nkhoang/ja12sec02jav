package com.nkhoang.service.impl;

import com.nkhoang.dao.IDictionaryDataService;
import com.nkhoang.dao.ISoundDataService;
import com.nkhoang.dao.IWordDataService;
import com.nkhoang.exception.DictionaryLookupServiceException;
import com.nkhoang.exception.ServiceException;
import com.nkhoang.exception.WebserviceException;
import com.nkhoang.model.WordEntity;
import com.nkhoang.model.WordJson;
import com.nkhoang.model.criteria.IWordCriteria;
import com.nkhoang.model.criteria.impl.WordCriteriaImpl;
import com.nkhoang.model.dictionary.ISound;
import com.nkhoang.model.dictionary.Sound;
import com.nkhoang.model.dictionary.Word;
import com.nkhoang.service.DictionaryLookupService;
import com.nkhoang.service.JsonService;
import com.nkhoang.service.WordService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.persistence.PersistenceException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class WordServiceImpl implements WordService {

  @Autowired
  @Qualifier("dictionaryLookupService")
  private DictionaryLookupService dictionaryLookupService;
  private static final Logger LOGGER = LoggerFactory.getLogger(WordServiceImpl.class.getCanonicalName());

  @Autowired
  @Qualifier("jsonService")
  private JsonService jsonService;

  @Autowired
  @Qualifier("wordDataService")
  private IWordDataService wordDataService;

  @Autowired
  @Qualifier("soundDataService")
  private ISoundDataService soundDataService;

  @Autowired
  @Qualifier("dictionaryDataService")
  private IDictionaryDataService dictionaryDataService;
  private Properties serverUrlProperties;
  /**
   * The list of pre-configured server URL. It will get the next one in the list if the bandwidth available for the
   * current one is 0.
   */
  private List<String> serverUrls = new ArrayList<String>();

  private int currentServerUrlPos = 0;

  public void convertServerUrlProperties() {
    serverUrls = new ArrayList<String>();
    for (Enumeration en = serverUrlProperties.keys(); en.hasMoreElements(); ) {
      String key = (String) en.nextElement();
      serverUrls.add(key);
    }
  }

  public boolean checkExistence(String word, String dictName) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Checking database with criteria [word=" + word + ", dictName=" + dictName);
    }
    IWordCriteria crit = new WordCriteriaImpl();
    crit.setWord(word);
    crit.setDictName(dictName);
    if (CollectionUtils.isNotEmpty(wordDataService.find(crit))) {
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  public void query(String word) throws WebserviceException, ServiceException, PersistenceException {
    if (currentServerUrlPos >= serverUrls.size()) {
      LOGGER.info("Service Url position is out of sync. Reset to 0.");
      currentServerUrlPos = 0;
    }
    boolean shouldStop = false;
    int currentPos = currentServerUrlPos;
    do {
      // get the resource URL.
      String resourceUrl = serverUrls.get(currentServerUrlPos);
      LOGGER.info("Use server: " + resourceUrl + " to fetch data.");
      try {
        if (StringUtils.isNotEmpty(resourceUrl)) {
          // query the word.
          String wordResponse = dictionaryLookupService.query(resourceUrl.trim(), word);
          // De-serialize json data to Object to verify data.
          WordJson wJson = jsonService.deserializeFrom(wordResponse);
          // get oxford data first
          String pron = null;
          ISound soundEntity = null;
          if (wJson.getData().get(DictionaryLookupService.DICT_OXFORD) != null) {
            WordEntity w = wJson.getData().get(DictionaryLookupService.DICT_OXFORD);
            pron = w.getPron();
            w.setKey(null);
            w.setSourceName(DictionaryLookupService.DICT_OXFORD);
            String jsonData = toJson(w);

            if (w.getSoundSource() != null) {
              String downloadUrl = w.getSoundSource();
              // download sound and save to database
              downloadUrl = downloadUrl.replaceAll("playSoundFromFlash\\(\\'", "");
              downloadUrl = downloadUrl.replaceAll("\\', this\\)", "");
              downloadUrl = downloadUrl.trim();
              byte[] sound = saveFile(downloadUrl);

              soundEntity = insertSound(sound, w.getDescription());
            }
            if (!checkExistence(w.getDescription(), w.getSourceName())) {
              insertWord(w, jsonData, soundEntity);
            } else {
              if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found word: [word=" + w.getDescription() + ", dictionary=" + w.getSourceName() + "]");
              }
            }
          }
          if (wJson.getData().get(DictionaryLookupService.DICT_VDICT) != null) {
            WordEntity w = wJson.getData().get(DictionaryLookupService.DICT_VDICT);
            w.setKey(null);
            w.setSourceName(DictionaryLookupService.DICT_VDICT);
            w.setPron(pron);
            String jsonData = toJson(w);

            if (!checkExistence(w.getDescription(), w.getSourceName())) {
              insertWord(w, jsonData, soundEntity);
            }
          }

          shouldStop = true;
        }
      } catch (DictionaryLookupServiceException DLEx) {
        LOGGER.info("The server: " + resourceUrl + " is not responsive. Switching server.");
        if (currentPos == currentServerUrlPos) {
          if (currentServerUrlPos + 1 >= serverUrls.size()) {
            throw DLEx;
          } else {
            increaseServerUrlPos();
          }
        }
      }
    } while (!shouldStop);
  }

  private synchronized void increaseServerUrlPos() {
    currentServerUrlPos++;
  }

  /**
   * Save remote File to a byte array.
   *
   * @param fAddress the address URL.
   */
  private byte[] saveFile(String fAddress) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Download sound from URL: " + fAddress);
    }
    byte[] result = null;
    ByteArrayOutputStream outStream = null;
    URLConnection uCon = null;

    InputStream is = null;
    try {
      URL Url;
      byte[] buf;
      int ByteRead, ByteWritten = 0;
      Url = new URL(fAddress);
      outStream = new ByteArrayOutputStream();


      uCon = Url.openConnection();
      is = uCon.getInputStream();
      buf = new byte[1000];
      while ((ByteRead = is.read(buf)) != -1) {
        outStream.write(buf, 0, ByteRead);
        ByteWritten += ByteRead;
      }
      result = outStream.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        is.close();
        outStream.close();
      } catch (IOException e) {

      }
    }
    return result;
  }

  /**
   * Insert sound to database and get the reference.
   *
   * @param soundArr
   * @return
   */
  private ISound insertSound(byte[] soundArr, String des) {
    ISound sound = new Sound();
    sound.setDescription(des);
    sound.setSound(soundArr);

    return soundDataService.insert(sound);
  }

  /**
   * Insert a new word.
   *
   * @param jsonData
   */
  private void insertWord(WordEntity wordEntity, String jsonData, ISound sound) throws PersistenceException {
    Word w = new Word();
    w.setData(jsonData);
    w.setSound(sound);
    w.setWord(wordEntity.getDescription());
    w.setDictionary(dictionaryDataService.findByName(wordEntity.getSourceName()));

    wordDataService.insert(w);
  }

  /**
   * Convert WordEntity object to json.
   *
   * @param entity the WordEntity
   * @return the json representation.
   * @throws ServiceException the ServiceException.
   */
  private String toJson(WordEntity entity) throws ServiceException {
    try {
      StringWriter out = new StringWriter();

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.configure(SerializationConfig.Feature.WRITE_NULL_MAP_VALUES, false);
      objectMapper.configure(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS, false);

      objectMapper.writeValue(out, entity);

      return out.toString();
    } catch (JsonGenerationException JGEx) {
      throw new ServiceException("Could not generate json from WordEntity of word=" + entity.getDescription(), JGEx);
    } catch (JsonMappingException JMEx) {
      throw new ServiceException("could not mapping content from WordEntity of word=" + entity.getDescription(), JMEx);
    } catch (IOException IOEx) {
      throw new ServiceException("Could not write the output json.", IOEx);
    }
  }

  public List<String> getServerUrls() {
    return serverUrls;
  }

  public void setServerUrls(List<String> serverUrls) {
    this.serverUrls = serverUrls;
  }

  public Properties getServerUrlProperties() {
    return serverUrlProperties;
  }

  public void setServerUrlProperties(Properties serverUrlProperties) {
    this.serverUrlProperties = serverUrlProperties;
  }
}
