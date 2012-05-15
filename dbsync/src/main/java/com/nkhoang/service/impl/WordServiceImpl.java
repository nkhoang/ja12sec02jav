package com.nkhoang.service.impl;

import com.nkhoang.exception.ServiceException;
import com.nkhoang.exception.WebserviceException;
import com.nkhoang.model.WordEntity;
import com.nkhoang.model.WordJson;
import com.nkhoang.model.dictionary.Word;
import com.nkhoang.service.DictionaryLookupService;
import com.nkhoang.service.JsonService;
import com.nkhoang.service.WordService;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOError;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class WordServiceImpl implements WordService {

  @Autowired
  @Qualifier("dictionaryLookupService")
  private DictionaryLookupService dictionaryLookupService;
  private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryLookupService.class.getCanonicalName());

  @Autowired
  @Qualifier("jsonService")
  private JsonService jsonService;
  /**
   * The list of pre-configured server URL. It will get the next one in the list if the bandwidth available for the
   * current one is 0.
   */
  private List<String> serverUrls = new ArrayList<String>();

  private int currentServerUrlPos = 0;

  void query(String word) throws WebserviceException, ServiceException {
    if (currentServerUrlPos >= serverUrls.size()) {
      LOGGER.info("Service Url position is out of sync. Reset to 0.");
      currentServerUrlPos = 0;
    }

    String resourceUrl = serverUrls.get(currentServerUrlPos);
    if (StringUtils.isNotEmpty(resourceUrl)) {
      // query the word.
      String wordResponse = dictionaryLookupService.query(resourceUrl.trim(), word);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Response from querying url [" + resourceUrl + "]: " + wordResponse);
      }
      // De-serialize json data to Object to verify data.
      WordJson wJson = jsonService.deserializeFrom(wordResponse);
      // get oxford data first
      if (wJson.getData().get(DictionaryLookupService.DICT_OXFORD) != null) {
        WordEntity w = wJson.getData().get(DictionaryLookupService.DICT_OXFORD);
        w.setKey(null);
        w.setSourceName(DictionaryLookupService.DICT_OXFORD);
      }
      if (wJson.getData().get(DictionaryLookupService.DICT_VDICT) != null) {
        WordEntity w = wJson.getData().get(DictionaryLookupService.DICT_VDICT);
        w.setKey(null);
        w.setSourceName(DictionaryLookupService.DICT_VDICT);

        String jsonData = toJson(w);
      } else {

      }


    }

  }

  private void insertWord(String jsonData) {
    Word w = new Word();
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
}
