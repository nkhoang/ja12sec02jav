package com.nkhoang.gae.service.impl;

import com.nkhoang.gae.dao.MeaningDao;
import com.nkhoang.gae.dao.VocabularyDao;
import com.nkhoang.gae.dao.impl.VocabularyDaoImpl;
import com.nkhoang.gae.model.Meaning;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.VocabularyService;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

public class VocabularyServiceImpl implements VocabularyService {
    private static final Logger LOGGER = LoggerFactory.getLogger(VocabularyDaoImpl.class);
    private static final String CONSTANT_MEANING_CONTAINER = "div";
    private static final String CONSTANT_CLASS_RESULT = "result";

    private MeaningDao meaningDao;
    private VocabularyDao vocabularyDao;
    private static final String LONGMAN_DICTIONARY_URL = "http://www.ldoceonline.com/dictionary/";

    /**
     * Get a range of words from database.
     *
     * @param startingIndex starting offset.
     * @param size          size.
     * @return a list.
     */
    public List<Word> getAllWordsInRange(int startingIndex, int size) {
        List<Word> words = vocabularyDao.getAllInRange(startingIndex, size);
        List<Word> result = new ArrayList<Word>();
        int lastIndex = startingIndex + size;
        if (lastIndex > words.size()) {
            lastIndex = words.size() - 1;
        }
        for (int i = startingIndex; i < lastIndex; i++) {
            Word w = words.get(i);
            populateWord(w);

            result.add(w);
        }
        return result;
    }


    public int getWordSize() {
        List<Word> words = vocabularyDao.getAll();
        if (words == null) {
            return 0;
        }
        return words.size();
    }
    
    public List<Word> getAllWordsFromUser(List<Long> wordIds) {
        List<Word> words = new ArrayList<Word>();
        for (Long id : wordIds) {
            Word word = get(id);
            if (word != null) {
                words.add(word);
            }
        }
        return words;
    }

    private Word populateWord(Word w) {
        // populate word by Meaning
        List<Long> meaningIds = w.getMeaningIds();
        for (Long meaningId : meaningIds) {
            Meaning meaning = meaningDao.get(meaningId);
            w.addMeaning(meaning.getKindId(), meaning);
        }
        return w;
    }

    private Word get(Long id) {
        Word word = vocabularyDao.get(id);

        return populateWord(word);
    }


    public Word save(String lookupWord) throws IOException, IllegalArgumentException {
        if (vocabularyDao.find(lookupWord)) {
            LOGGER.info(">>>>>>>>>>>>>>>>>>> Found :" + lookupWord);
        }
        LOGGER.info("Saving word : " + lookupWord);
        Word word = null;
        try {
            word = lookup(lookupWord);
            lookupENLongman(word, lookupWord);
            lookupPron(word, lookupWord);
        } catch (IOException ex) {
            LOGGER.info("Failed to connect to dictionary to lookup word definition.");
            throw ex;
        } catch (IllegalArgumentException iae) {
            LOGGER.info("Failed to parse URL with wrong arguments.");
            throw iae;
        }

        if (word != null) {
            // build list of meaning
            for (int i = 0; i < Word.WORD_KINDS.length; i++) {
                List<Meaning> meanings = word.getMeaning(Long.parseLong(i + ""));
                if (meanings != null && meanings.size() > 0) {
                    //LOGGER.info("found : " + meanings.size() + " meanings for this word");
                    for (Meaning meaning : meanings) {
                        // save
                        try {
                            Meaning savedMeaning = meaningDao.save(meaning);
                            word.addMeaningId(savedMeaning.getId());
                        } catch (Exception e) {
                            LOGGER.info("Failed to save meaning to DB.");
                            LOGGER.info(meaning.toString());
                        }
                    }
                } else {
                    // log.info(word.getMeanings());
                }
            }


            try {
                word.setTimeStamp(GregorianCalendar.getInstance().getTimeInMillis());
                vocabularyDao.save(word);
            } catch (Exception e) {
                LOGGER.info("Could not save word:" + word.toString());
            }
        }
        return word;
    }

    /**
     * Look up Longman dictionary for a specific word. Update existing word
     *
     * @param aWord word to be updated. Return null if this is null.
     * @param word  word to be looked up.
     * @return null if failed to retrieve even one of the 2 services.
     */
    private void lookupENLongman(Word aWord, String word) throws IOException {
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> looking up word EN from Longman dictionary: " + word);
        Source source = checkWordExistence(LONGMAN_DICTIONARY_URL, word.toLowerCase(), "Entry");
        int i = 1; // index for the next lookup
        if (source == null) {
            LOGGER.info("Check URL again with index [" + i + "]");
            source = checkWordExistence(LONGMAN_DICTIONARY_URL, word.toLowerCase() + "_" + i, "Entry");// check it again
        }
        while (source != null) {
            String kind = "";// get kind
            List<Element> kinds = source.getAllElementsByClass("wordclassSelected");
            if (kinds != null && kinds.size() > 0) {
                kind = kinds.get(0).getTextExtractor().toString().trim();
                LOGGER.info("Kind: " + kind);
            }

            if (aWord.getKindidmap().get(kind) == null) {
                LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>> CRITICAL >>>>>>>>>>>>>>> Kind not found in the map: " + kind);
                return;
            }

            List<Element> meaningList = source.getAllElementsByClass("Sense");// process meaning
            if (meaningList != null) {
                for (Element meaning : meaningList) {
                    Meaning mainM = new Meaning();
                    String gramStr = ""; // process GRAM: [intransitive, transitive]...
                    List<Element> grams = meaning.getAllElementsByClass("GRAM");
                    if (grams != null && grams.size() > 0) {
                        gramStr = grams.get(0).getTextExtractor().toString();
                        // LOGGER.info("GRAM: " + gramStr);
                    }

                    List<Element> ftdefs = meaning.getAllElements("ftdef"); // process main meaning
                    Element ftdef = null;
                    if (ftdefs != null && ftdefs.size() > 0) {
                        ftdef = ftdefs.get(0);
                        mainM = new Meaning(gramStr + " " + ftdef.getTextExtractor().toString(), aWord.getKindidmap().get(kind)); // create this meaning                        
                        // LOGGER.info("Meaning: " + ftdef.getTextExtractor().toString());
                    } else {
                        LOGGER.info("Could not check definition for this word: " + word);
                    }
                    List<Element> ftexas = meaning.getAllElements("ftexa"); // process example for this main meaning
                    if (ftexas != null) {
                        for (Element ftexa : ftexas) {
                            mainM.addExample(ftexa.getTextExtractor().toString());
                            // LOGGER.info("Example: " + ftexa.getTextExtractor().toString());
                        }
                    }

                    if (StringUtils.isNotBlank(mainM.getContent())) { // check to make sure content is not blank.
                        aWord.addMeaning(aWord.getKindidmap().get(kind), mainM);
                    }

                    List<Element> gramexas = meaning.getAllElementsByClass("GramExa"); // process gram example. Another type of meaning.
                    if (ftexas != null) {
                        for (Element gramexa : gramexas) {
                            Meaning mm = processSubExampleLongman(gramexa, "PROPFORM", aWord.getKindidmap().get(kind));
                            mm.setType("gram");
                            if (StringUtils.isNotEmpty(mm.getContent())) { // make sure content is not blank.
                                aWord.addMeaning(aWord.getKindidmap().get(kind), mm);
                            }
                        }
                    }

                    List<Element> colloexas = meaning.getAllElementsByClass("ColloExa"); // process gram example
                    if (ftexas != null) {
                        for (Element colloexa : colloexas) {
                            Meaning mm = processSubExampleLongman(colloexa, "COLLO", aWord.getKindidmap().get(kind));
                            mm.setType("collo");
                            if (StringUtils.isNotEmpty(mm.getContent())) { // make sure content is not blank.
                                aWord.addMeaning(aWord.getKindidmap().get(kind), mm);
                            }
                        }
                    }
                }
            }
            source = checkWordExistence(LONGMAN_DICTIONARY_URL, word + "_" + ++i, "Entry");
        }

    }


    /**
     * Process Sub example for tag GramExa
     *
     * @param s source to process.
     * @return Meaning found.
     */
    private Meaning processSubExampleLongman(Element s, String nametag, Long kindId) {
        Meaning m = new Meaning();

        List<Element> grams = s.getAllElementsByClass(nametag);
        if (grams != null && grams.size() > 0) {
            String str = grams.get(0).getTextExtractor().toString();
            m = new Meaning(str, kindId);
            // m.setContent(str);
            LOGGER.info(nametag + ": " + str);
        }

        // process example for this main meaning
        List<Element> ftexas = s.getAllElements("ftexa");
        if (ftexas != null) {
            for (Element ftexa : ftexas) {
                m.addExample(ftexa.getTextExtractor().toString());
                LOGGER.info("Example: " + ftexa.getTextExtractor().toString());
            }
        }

        return m;
    }

    /**
     * Look up Pron for a word.
     *
     * @param aWord Word obj to be updated.
     * @param word  word in String.
     * @throws IOException connection problems.
     */
    private void lookupPron(Word aWord, String word) throws IOException {
        LOGGER.info("looking up PRON for this word : " + word);
        try {
            Source source = checkWordExistence("http://dictionary.cambridge.org/dictionary/british/", word.toLowerCase(), "definition-title");
            int i = 1;
            if (source == null) {
                // check it again
                source = checkWordExistence("http://dictionary.cambridge.org/dictionary/british/", word.toLowerCase() + "_" + i, "definition-title");
            }
            while (source != null) {
                // process the content
                List<Element> contentEles = source.getAllElementsByClass("gwblock ");
                // should be one
                Element targetContent = contentEles.get(0);
                String kind = "";
                // get kind
                List<Element> headers = targetContent.getAllElementsByClass("header");
                if (headers != null && headers.size() > 0) {
                    Element header = headers.get(0);

                    List<Element> kinds = header.getAllElementsByClass("pos");
                    if (kinds != null && kinds.size() > 0) {
                        kind = kinds.get(0).getContent().toString().trim();
                    }
                }

                List<Element> additional_headers = targetContent.getAllElementsByClass("additional_header");
                if (additional_headers != null && additional_headers.size() > 0) {
                    Element additional_header = additional_headers.get(0);
                    List<Element> prons = additional_header.getAllElementsByClass("pron");
                    // get Pron
                    if (prons != null && prons.size() > 0) {
                        String pron = prons.get(0).getTextExtractor().toString();
                        // LOGGER.info("Pron: " + pron);
                        aWord.setPron(pron);

                    }
                    // get mp3 file
                    List<Element> sounds = additional_header.getAllElementsByClass("sound");
                    // may have 2
                    if (sounds != null && sounds.size() > 0) {
                        Element sound = null;
                        if (sounds.size() == 1) {
                            sound = sounds.get(0);
                        } else if (sounds.size() == 2) {
                            sound = sounds.get(1);
                        }

                        // process
                        String soundSource = sound.getAttributeValue("onclick");
                        String soundSrc = soundSource.replace("/media", "http://dictionary.cambridge.org/media");
                        // LOGGER.info("Found a sound source: " + soundSrc);
                        aWord.setSoundSource(soundSrc);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
    }


    private Word lookupENCambridge(Word aWord, String word) {
        LOGGER.info("looking up word EN : " + word);
        try {
            Source source = checkWordExistence("http://dictionary.cambridge.org/dictionary/british/", word.toLowerCase(), "definition-title");
            int i = 1;
            if (source == null) {
                // check it again
                source = checkWordExistence("http://dictionary.cambridge.org/dictionary/british/", word.toLowerCase() + "_" + i, "definition-title");
            }
            while (source != null) {
                // process the content
                List<Element> contentEles = source.getAllElementsByClass("gwblock ");
                // should be one
                Element targetContent = contentEles.get(0);
                String kind = "";
                // get kind
                List<Element> headers = targetContent.getAllElementsByClass("header");
                if (headers != null && headers.size() > 0) {
                    Element header = headers.get(0);

                    List<Element> kinds = header.getAllElementsByClass("pos");
                    if (kinds != null && kinds.size() > 0) {
                        kind = kinds.get(0).getContent().toString().trim();
                    }
                }

                List<Element> additional_headers = targetContent.getAllElementsByClass("additional_header");
                if (additional_headers != null && additional_headers.size() > 0) {
                    Element additional_header = additional_headers.get(0);
                    List<Element> prons = additional_header.getAllElementsByClass("pron");
                    // get Pron
                    if (prons != null && prons.size() > 0) {
                        String pron = prons.get(0).getContent().toString();
                        // LOGGER.info("Pron: " + pron);
                        aWord.setPron(pron);
                    }

                    // get mp3 file
                    List<Element> sounds = additional_header.getAllElementsByClass("sound");
                    // may have 2
                    if (sounds != null && sounds.size() > 0) {
                        Element sound = null;
                        if (sounds.size() == 1) {
                            sound = sounds.get(0);
                        } else if (sounds.size() == 2) {
                            sound = sounds.get(1);
                        }

                        // process
                        String soundSource = sound.getAttributeValue("onclick");
                        String soundSrc = soundSource.replace("/media", "http://dictionary.cambridge.org/media");
                        // LOGGER.info("Found a sound source: " + soundSrc);
                        aWord.setSoundSource(soundSrc);
                    }
                }
                // get meaning
                List<Element> meaningList = targetContent.getAllElementsByClass("gwblock_b");
                if (meaningList != null && meaningList.size() > 0) {
                    Element meanings = meaningList.get(0);

                    List<Element> meaningsChildren = meanings.getChildElements();
                    for (Element aMeaningsChildren : meaningsChildren) {
                        Meaning m = new Meaning();
                        // check the class name
                        if (aMeaningsChildren.getName().equals("div")
                                && aMeaningsChildren.getAttributeValue("class").trim().equals("sense")) {
                            // get meaning
                            List<Element> meaningContent = aMeaningsChildren.getAllElementsByClass("def");
                            if (meaningContent != null && meaningContent.size() > 0) {
                                String meaningStr = meaningContent.get(0).getTextExtractor().toString();
                                m = new Meaning(meaningStr, aWord.getKindidmap()
                                        .get(kind));

                                // LOGGER.info("Meaning description: " + meaningStr);
                            }

                            // get examples
                            List<Element> examples = aMeaningsChildren.getAllElementsByClass("examp");
                            if (examples != null && examples.size() > 0) {
                                for (Element example : examples) {
                                    String ex = example.getChildElements().get(0).getTextExtractor().toString();
                                    m.addExample(ex);

                                    // LOGGER.info("Example: " + ex);

                                }
                            }
                            // add this meaning
                            aWord.addMeaning(aWord.getKindidmap().get(kind), m);
                        }
                    }
                }

                source = checkWordExistence("http://dictionary.cambridge.org/dictionary/british/", word + "_" + ++i, "definition-title");
                // log.info(aWord.getMeanings());
            }
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
        return aWord;
    }

    /**
     * Connect to URL to get the Source.
     *
     * @param urlLink       url link.
     * @param word          word to be looked for.
     * @param targetContent Tag which will be considered as Root for the look up. Class name is the indicator.
     * @return Source.
     * @throws IOException connection problem.
     */
    private Source checkWordExistence(String urlLink, String word, String targetContent) throws IOException {
        URL url = new URL(urlLink + word);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        InputStream is = connection.getInputStream();
        Source source = new Source(is);

        List<Element> contentEles = source.getAllElementsByClass(targetContent);

        if (contentEles == null || contentEles.size() == 0) {
            return null;
        } else {
            return source;
        }
    }


    /**
     * Look up VN definition for a word.
     *
     * @param word word to be looked up.
     * @return constructed Word.
     * @throws IOException              problem parsing IOException.
     * @throws IllegalArgumentException param may be incorrectly inputted.
     */
    public Word lookup(String word) throws IOException, IllegalArgumentException {
        LOGGER.info("Looking up word VN meaning: " + word);

        Word aWord = null;

        URL url = new URL("http://m.vdict.com/?word=" + word + "&dict=1&searchaction=Lookup");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        InputStream is = connection.getInputStream();

        Source source = new Source(is);
        List<Element> contentEles = source.getAllElementsByClass("result");
        if (contentEles == null || contentEles.size() == 0) {
            return null; // Source ok but no content found. => word may not exists.
        }

        Element targetContent = contentEles.get(0);
        aWord = new Word(); // construct word obj.
        aWord.setDescription(word);
        Meaning meaning = new Meaning();

        String kind = ""; // starting with " kind "
        List<Element> eles = targetContent.getChildElements();
        for (Element ele : eles) {
            if (ele.getName().equals("div")) {
                kind = "";
            }
            if (ele.getAttributeValue("class") != null && ele.getAttributeValue("class").equals("phanloai")) {
                kind = ele.getTextExtractor().toString().trim(); // use TextExtractor to trim unwanted content.
                if (kind != null) {
                    if (kind.contains(",")) { // may be a compond of something like: danh tu, ngoai dong tu
                        kind = kind.split(",")[0]; // just get the first one. May be there are some more exceptional cases in the future. 
                    }
                    String[] words = kind.split(" ");
                    kind = "";
                    int limit = words.length > 3 ? 3 : words.length; // maximum length = " ngoai dong tu ". Composed by 3 word.
                    for (int i = 0; i < limit; i++) {
                        kind += words[i] + " ";
                    }
                    kind = kind.trim();
                    LOGGER.info("Kind : " + kind);
                    LOGGER.info(Arrays.toString(kind.getBytes("UTF-8")));
                }
            }
            if (ele.getName().equals("ul") && StringUtils.isNotEmpty(kind)) {
                String className = ele.getAttributeValue("class");
                if (className != null && className.equals("list1")) {
                    List<Element> meaningLis = ele.getChildElements();
                    for (Element meaningLi : meaningLis) {
                        if (meaningLi.getName().equals("li")) {
                            List<Element> liContent = meaningLi.getChildElements();
                            for (Element content : liContent) {
                                if (content.getName().equals("b")) {
                                    String contentRaw = content.getContent().toString();
                                    meaning = new Meaning(contentRaw, aWord.getKindidmap().get(kind));
                                    // LOGGER.info("content : " + contentRaw);
                                }
                                if (content.getName().equals("ul") && StringUtils.isNotEmpty(meaning.getContent())) { // should not store any meanings if content is null or blank.
                                    String example = content.getChildElements().get(0).getChildElements().get(0)
                                            .getContent().toString();
                                    meaning.addExample(example);

                                    // LOGGER.info("Example: " + example);
                                }
                            }
                        }
                        Long kindId = aWord.getKindidmap().get(kind);
                        if (kindId == null) {
                            LOGGER.info(">>>>>>>>>>>>>>>>>> CRITICAL >>>>>>>>>>>>>>>>>>>>> Null for kind: " + kind);
                        }
                        if (meaning != null && StringUtils.isNotBlank(meaning.getContent()) && kindId != null) { // just skip this Word.
                            aWord.addMeaning(kindId, meaning);
                        }
                    }
                }
            }
        }
        // log.info(aWord.getKindidmap());
        return aWord;
    }

    public MeaningDao getMeaningDao() {
        return meaningDao;
    }

    public void setMeaningDao(MeaningDao meaningDao) {
        this.meaningDao = meaningDao;
    }

    public VocabularyDao getVocabularyDao() {
        return vocabularyDao;
    }

    public void setVocabularyDao(VocabularyDao vocabularyDao) {
        this.vocabularyDao = vocabularyDao;
    }
}
