package com.nkhoang.gae.service.impl;

import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;
import com.nkhoang.gae.dao.VocabularyDao;
import com.nkhoang.gae.dao.impl.VocabularyDaoImpl;
import com.nkhoang.gae.model.*;
import com.nkhoang.gae.service.ApplicationService;
import com.nkhoang.gae.service.VocabularyService;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.*;

@Service("vocabularyService")
public class VocabularyServiceImpl implements VocabularyService {
   private static final Logger LOG = LoggerFactory
         .getLogger(VocabularyDaoImpl.class.getCanonicalName());
   private static final String HTML_DIV = "div";
   private static final String VN_DICT_CONTENT_CLASS = "container";
   private static final String HTML_ATTR_CLASS = "class";

   private static final String ENCODING_UTF_8 = "UTF-8";
   private static final String VN_DICT_CLASS_KIND = "phanloai";
   private static final int MAX_NUM_VN_WORD_IN_KIND = 3;

   private static final String LONGMAN_DIC_CONTENT_CLASS = "Entry";
   private static final String LONGMAN_DICT_KIND_CLASS = "wordclassSelected";
   private static final String LONGMAN_DICT_CLASS_MEANING = "Sense";
   private static final String LONGMAN_DICT_CLASS_GRAM = "GRAM";
   private static final String LONGMAN_DICT_CLASS_MEANING_DEF = "ftdef";
   private static final String LONGMAN_DICT_CLASS_EXAMPLE = "ftexa";
   private static final String LONGMAN_DICT_CLASS_MEANING_EXTRA = "GramExa";

   private static final String GRAM_MEANING_TYPE = "gram";
   private static final String COLLO_MEANING_TYPE = "collo";


   @Autowired
   private ApplicationService applicationService;
   @Autowired
   private CentralLookupService centralLookupService;
   @Autowired
   private VocabularyDao vocabularyDao;
   @Autowired
   private AppCache appCache;

   /**
    * Hold the dictionary label value which will be used to lookup the configured values.
    * <br />
    * To change value of this field may involve these following files/steps:
    * <ul>
    * <li>appConfig.properties: hold the dictionary key name.</li>
    * <li>admin page: change the label name accordingly.</li>
    * </ul>
    */
   @Value("#{appConfig.dictionary}")
   private String dictionaryKeyName;

   @Value("#{appConfig.delimiter}")
   private String delimiter;

   private static final String LONGMAN_DICTIONARY_URL = "http://www.ldoceonline.com/dictionary/";

   private enum DICTIONARY_TYPE {
      CLASS,
      ID
   }


   public List<WordEntity> getAllWordEntitiesByRange(int startingIndex, int size, String direction) {
      List<WordEntity> words = vocabularyDao.getAllInRange(startingIndex, size, direction);
      return words;
   }


   public List<WordEntity> getAllWordEntitiesByRange(int startingIndex, int size) {
      List<WordEntity> words = vocabularyDao.getAllInRange(startingIndex, size);
      return words;
   }

   public List<WordEntity> getAllWordsById(List<Long> wordIds) {
      List<WordEntity> words = new ArrayList<WordEntity>();
      if (CollectionUtils.isNotEmpty(wordIds)) {
         for (Long id : wordIds) {
            WordEntity word = findWordById(id);
            if (word != null) {
               words.add(word);
            }
         }
      }
      return words;
   }


   /**
    * Use {@link VocabularyDao} to find word by <code>id</code>.
    *
    * @param id the word id.
    * @return found word.
    */
   private WordEntity findWordById(Long id) {
      WordEntity word = vocabularyDao.get(id);

      return word;
   }

   public boolean checkConfiguredDicts() {
      List<String> configValues = applicationService.getAppConfig(dictionaryKeyName, delimiter);
      return (CollectionUtils.isNotEmpty(configValues));
   }


   public Map<String, Word> lookup(String requestWord) {
      Map<String, Word> wordMap = new HashMap<String, Word>();
      if (StringUtils.isNotBlank(requestWord)) {
         // first get the configuration dictionaries.
         LOG.info("dictionaryKeyName = " + dictionaryKeyName);
         List<String> configValues = appCache.getProperty(dictionaryKeyName);

         // repopulate AppCache once.
         if (CollectionUtils.isEmpty(configValues)) {
            applicationService.getAppConfig(dictionaryKeyName, delimiter);
         }

         if (CollectionUtils.isNotEmpty(configValues)) {
            for (String dictType : configValues) {
               // first trim all spaces
               dictType = dictType.trim();
               // check DB first
               WordEntity dbWordEntity = findWordByDictType(requestWord, dictType);
               if (dbWordEntity == null) {
                  Map<String, Word> returnWords = centralLookupService.lookup(requestWord, Arrays.asList(dictType));

                  // create id for Meaning, Phrase, Sense.
                  for (Word w : returnWords.values()) {
                     int senseIndex = 0;
                     for (Sense s : w.getMeanings()) {
                        s.setId(w.getDescription() + "-sense-" + senseIndex++);
                        int meaningIndex = 0;
                        for (Meaning m : s.getSubSenses()) {
                           m.setId(s.getId() + "-meaning-" + meaningIndex++);
                        }
                     }
                     int phraseIndex = 0;
                     for (Phrase f : w.getPhraseList()) {
                        f.setId(w.getDescription() + "-phrase-" + phraseIndex++);
                        int fSenseIndex = 0;
                        for (Sense sense : f.getSenseList()) {
                           sense.setId(f.getId() + "-sense-" + fSenseIndex++);
                           int fMeaningIndex = 0;
                           for (Meaning m : sense.getSubSenses()) {
                              m.setId(f.getId() + "-meaning-" + fMeaningIndex++);
                           }
                        }
                     }
                  }

                  // save to DB
                  for (Map.Entry<String, Word> entry : returnWords.entrySet()) {
                     dbWordEntity = saveWordEntityToDatastore(entry.getValue(), entry.getKey());
                  }
               }
               if (dbWordEntity != null) {
                  Gson gson = new Gson();
                  Word dbWord = gson.fromJson(dbWordEntity.getWordJSON().getValue(), Word.class);
                  wordMap.put(dictType, dbWord);
               } else {
                  LOG.info("Could not find: [" + requestWord + "] using dictionary service : [" + dictType + "]");
               }
            }
         }
      }
      return wordMap;
   }

   private WordEntity saveWordEntityToDatastore(Word w, String dictType) {
      Gson gson = new Gson();
      WordEntity wordEntity = new WordEntity();
      wordEntity.setTimeStamp(w.getTimeStamp());
      wordEntity.setWord(w.getDescription());
      wordEntity.setDictType(dictType);
      wordEntity.setWordJSON(new Text(gson.toJson(w)));

      LOG.info("WordEntity: " + w.getDescription() + " saved!!!");
      return vocabularyDao.save(wordEntity);
   }

   public WordEntity findWord(String requestWord) {
      return vocabularyDao.lookup(requestWord);
   }

   /**
    * Find word by Dictionary Type.
    *
    * @param requestWord the word to lookup.
    * @param dictType    the dictionary type.
    * @return the {@link WordEntity}.
    */
   public WordEntity findWordByDictType(String requestWord, String dictType) {
      return vocabularyDao.lookupByDict(requestWord, dictType);
   }




   /**
    * Process Sub example for tag GramExa
    *
    * @param s       element s.
    * @param nametag the tag name.
    * @param kindId  the kind id.
    * @return Meaning object.
    */
   private Meaning processSubExampleLongman(Element s, String nametag, Long kindId) {
      Meaning m = new Meaning();

      List<Element> grams = s.getAllElementsByClass(nametag);
      if (CollectionUtils.isNotEmpty(grams)) {
         String str = grams.get(0).getTextExtractor().toString();
         m = new Meaning(str, kindId);
         LOG.debug(nametag + ": " + str);
      }

      // process example for this main meaning
      List<Element> ftexas = s.getAllElements(LONGMAN_DICT_CLASS_EXAMPLE);
      if (CollectionUtils.isNotEmpty(ftexas)) {
         for (Element ftexa : ftexas) {
            m.addExample(ftexa.getTextExtractor().toString());
            LOG.info("Example: " + ftexa.getTextExtractor().toString());
         }
      }

      return m;
   }




   /*public Word lookupIdiom(Word aWord) {
      Source source = checkWordExistence(
            CAMBRIDGE_DICT_URL_TYPE + CAMBRIDGE_DICT_IDIOM_TYPE + CAMBRIDGE_DICT_TYPE_QUERY, aWord.getDescription(),
            "search-page-results-container", DICTIONARY_TYPE.ID);

      if (source != null) {
         LOG.info(source.toString());
         LOG.info("OK");
      }
      return null;
   }         */

   /**
    * Check word exsitence.
    *
    * @return null or Source object.
    */
   /*
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
         LOG.error("Error fetching word using URL: " + urlLink + word, e);
      }
      return source;
   }                        */


   public void update(WordEntity w) {
      vocabularyDao.update(w);
   }

   public Word lookupVN(String word) throws IOException, IllegalArgumentException {
      Word aWord = null;
      URL url = new URL("http://m.vdict.com/?word=" + word + "&dict=1&searchaction=Lookup");
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
         // construct word obj.
         aWord = new Word();
         // set description.
         aWord.setDescription(word);
         // starting to update Meanings.
         Meaning meaning = new Meaning();
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
                              //aa meaning = new Meaning(contentRaw, aWord.getKindidmap().get(kind));
                              //LOG.debug("content : " + contentRaw);
                           } else if (StringUtils.equals(content.getName(), "ul") &&
                                 StringUtils.isNotEmpty(meaning.getContent())) {
                              // should not store any meanings if content is null or blank.
                              String example = content.getChildElements().get(0).getChildElements().get(0)
                                    .getContent().toString();
                              if (StringUtils.isNotBlank(example)) {
                                 meaning.addExample(example);
                              }
                              //LOG.debug("Example: " + example);
                           }
                        }
                     }
                     // Long kindId = aWord.getKindidmap().get(kind);
                     Long kindId = null;
                     if (kindId == null) {
                        LOG.info(">>>>>>>>>>>>>>>>>> CRITICAL >>>>>>>>>>>>>>>>>>>>> Null for kind: " + kind);
                     }
                     if (meaning != null && StringUtils.isNotBlank(meaning.getContent()) && kindId != null) {
                        //aa aWord.addMeaning(kindId, meaning);
                     }
                  }
               }
            }
         }
      }
      return aWord;
   }

   public VocabularyDao getVocabularyDao() {
      return vocabularyDao;
   }

   public void setVocabularyDao(VocabularyDao vocabularyDao) {
      this.vocabularyDao = vocabularyDao;
   }


   public ApplicationService getApplicationService() {
      return applicationService;
   }

   public void setApplicationService(ApplicationService applicationService) {
      this.applicationService = applicationService;
   }

   public CentralLookupService getCentralLookupService() {
      return centralLookupService;
   }

   public void setCentralLookupService(CentralLookupService centralLookupService) {
      this.centralLookupService = centralLookupService;
   }

   public String getDictionaryKeyName() {
      return dictionaryKeyName;
   }

   public void setDictionaryKeyName(String dictionaryKeyName) {
      this.dictionaryKeyName = dictionaryKeyName;
   }

   public String getDelimiter() {
      return delimiter;
   }

   public void setDelimiter(String delimiter) {
      this.delimiter = delimiter;
   }

   public AppCache getAppCache() {
      return appCache;
   }

   public void setAppCache(AppCache appCache) {
      this.appCache = appCache;
   }
}

