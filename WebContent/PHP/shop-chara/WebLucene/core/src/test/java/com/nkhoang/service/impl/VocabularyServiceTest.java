package com.nkhoang.service.impl;

import com.nkhoang.constant.LuceneSearchField;
import com.nkhoang.model.Meaning;
import com.nkhoang.model.Word;
import com.nkhoang.service.VocabularyService;
import com.nkhoang.util.lucene.LuceneUtils;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;


@ContextConfiguration(locations = {
        "classpath:/applicationContext-resources.xml", "classpath:/applicationContext-dao.xml",
        "classpath:/applicationContext-service.xml", "classpath*:/**/applicationContext.xml"
})

public class VocabularyServiceTest {
    public static final Logger LOGGER = LoggerFactory.getLogger(VocabularyServiceTest.class);

    @Autowired
    private VocabularyService vocabularyService;
    @Autowired
    private SpreadsheetServiceImpl spreadsheetService;

    @Before
    public void setup() {
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"applicationContext-dao.xml", "applicationContext-resources.xml",
                "applicationContext-service.xml"});
        if (vocabularyService == null) {
            vocabularyService = (VocabularyService) context.getBean("vocabularyService");
        }
        if (spreadsheetService == null) {
            spreadsheetService = (SpreadsheetServiceImpl) context.getBean("spreadsheetService");
        }
    }



    public void clearAll() {
        vocabularyService.removeAll();
    }



                      @Test
    public void testSearcher() throws Exception {
        Word w = new Word();
        //w.setDescription("fluen");
        w.setContent("piece of music");

    QueryParser parser = new QueryParser(Version.LUCENE_29, LuceneSearchField.CONTENT, new SimpleAnalyzer());
    //Query query = parser.parse("+content:giao mùa");

        Query query = LuceneUtils.buildQuery(w);
        LOGGER.info("Query: " + query.toString());
        List<String> idsResult = LuceneUtils.performSearch(query, LuceneUtils.getLuceneSearcher());
        if (idsResult.size() > 0) {
            for (String id : idsResult) {
                LOGGER.info("Id : " + id);
            }

        }
    }
    public void testIndexer() throws IOException {
        List<Word> words = vocabularyService.getAll();
        for (Word w : words) {
            LuceneUtils.writeWordToIndex(w);
        }
        LuceneUtils.closeLuceneWriter();
    }
    public void testVocabularyService() throws Exception {
        List<String> words = spreadsheetService.getWordList();

        if (words.size() > 0) {
            LOGGER.info("Total words:  " + words.size());
        }
        // vocabularyService.removeAll();

        for (String w : words) {
            vocabularyService.save(w);
        }
    }

    public String showWord(Word w) {
        StringBuilder sb = new StringBuilder();

        sb.append("Word : " + w.getDescription());

        Map<Long, List<Meaning>> m = w.getMeaningMap();

        Set<Long> mk = m.keySet();

        for (Long k : mk) {
            String kind = Word.WORD_KINDS[Integer.parseInt(k + "")];
            sb.append("\n [" + kind + "]");

            List<Meaning> mm = m.get(k);
            for (Meaning mmm : mm) {
                sb.append("\n");
                sb.append(mmm.toString());
            }
        }

        return sb.toString();
    }

    /**
     * tag for kind: wordclassSelected
     *
     * @param aWord
     * @param word
     * @return
     */
    public Word lookupENLongman(Word aWord, String word) {
        LOGGER.info("looking up word EN : " + word);
        try {
            Source source = checkWordExistence("http://www.ldoceonline.com/dictionary/", word.toLowerCase(), "Entry");
            int i = 1;
            if (source == null) {
                LOGGER.info("Check URL with index");
                // check it again
                source = checkWordExistence("http://www.ldoceonline.com/dictionary/", word.toLowerCase() + "_" + i, "Entry");
            }
            while (source != null) {
                // get kind
                String kind = "";
                List<Element> kinds = source.getAllElementsByClass("wordclassSelected");
                if (kinds != null && kinds.size() > 0) {
                    kind = kinds.get(0).getTextExtractor().toString();
                    LOGGER.info("Kind: " + kind);
                }
                // process meaning

                List<Element> meaningList = source.getAllElementsByClass("Sense");
                if (meaningList != null) {
                    for (Element meaning : meaningList) {

                        Meaning mainM = new Meaning();
                        // process GRAM
                        String gramStr = "";
                        List<Element> grams = meaning.getAllElementsByClass("GRAM");
                        if (grams != null && grams.size() > 0) {
                            gramStr = grams.get(0).getTextExtractor().toString();
                            LOGGER.info("GRAM: " + gramStr);
                        }

                        // process main meaning
                        List<Element> ftdefs = meaning.getAllElements("ftdef");
                        Element ftdef = null;
                        if (ftdefs != null && ftdefs.size() > 0) {
                            ftdef = ftdefs.get(0);
                            mainM.setContent(StringEscapeUtils.escapeSql(gramStr + " " + ftdef.getTextExtractor().toString()));
                            LOGGER.info("Meaning: " + ftdef.getTextExtractor().toString());
                        } else {
                            LOGGER.info("Could not check definition for this word: " + word);
                        }
                        // process example for this main meaning
                        List<Element> ftexas = meaning.getAllElements("ftexa");
                        if (ftexas != null) {
                            for (Element ftexa : ftexas) {

                                mainM.addExample(ftexa.getTextExtractor().toString());

                                LOGGER.info("Example: " + ftexa.getTextExtractor().toString());
                            }
                        }

                        aWord.addMeaning(mainM);

                        // process gram example
                        List<Element> gramexas = meaning.getAllElementsByClass("GramExa");
                        if (ftexas != null) {
                            for (Element gramexa : gramexas) {
                                Meaning mm = processSubExampleLongman(gramexa, "PROPFORM");
                                mm.setType("gram");
                                aWord.addMeaning(mm);
                            }
                        }

                        // process gram example
                        List<Element> colloexas = meaning.getAllElementsByClass("ColloExa");
                        if (ftexas != null) {
                            for (Element colloexa : colloexas) {
                                Meaning mm = processSubExampleLongman(colloexa, "COLLO");
                                mm.setType("collo");
                                aWord.addMeaning(mm);
                            }
                        }
                    }
                }
                source = checkWordExistence("http://www.ldoceonline.com/dictionary/", word + "_" + ++i, "Entry");
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred.", e);
        }

        return aWord;
    }


    /**
     * Process Sub example for tag GramExa
     *
     * @param s source to process.
     * @return Meaning found.
     */
    private Meaning processSubExampleLongman(Element s, String nametag) {
        Meaning m = new Meaning();

        List<Element> grams = s.getAllElementsByClass(nametag);
        if (grams != null && grams.size() > 0) {
            String str = grams.get(0).getTextExtractor().toString();
            str = StringEscapeUtils.escapeSql(str);
            m.setContent(StringEscapeUtils.escapeSql(str));
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

    public Word lookupENCambridge(Word aWord, String word) {
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
                        LOGGER.info("Pron: " + pron);
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
                        LOGGER.info("Found a sound source: " + soundSrc);
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

                                LOGGER.info("Meaning description: " + meaningStr);
                            }

                            // get examples
                            List<Element> examples = aMeaningsChildren.getAllElementsByClass("examp");
                            if (examples != null && examples.size() > 0) {
                                for (Element example : examples) {
                                    String ex = example.getChildElements().get(0).getTextExtractor().toString();
                                    m.addExample(ex);

                                    LOGGER.info("Example: " + ex);

                                }
                            }
                            // add this meaning
                            aWord.addMeaning(m);
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

    private Source checkWordExistence(String urlLink, String word, String targetContent) {
        try {
            URL url = new URL(urlLink + word);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // get inputStream
            InputStream is = connection.getInputStream();
            // create source HTML
            Source source = new Source(is);

            List<Element> contentEles = source.getAllElementsByClass(targetContent);

            if (contentEles == null || contentEles.size() == 0) {
                return null;
            } else {
                return source;
            }
        } catch (Exception e) {
            LOGGER.info("http://dictionary.cambridge.org/dictionary/british/" + word + " not found.");
            return null;
        }
    }

    public Word lookup(String word) {
        LOGGER.info("Looking up word : " + word);
        try {
            URL url = new URL("http://m.vdict.com/?word=" + word + "&dict=1&searchaction=Lookup");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // get inputStream
            InputStream is = connection.getInputStream();
            // create source HTML
            Source source = new Source(is);
            List<Element> contentEles = source.getAllElementsByClass("result");
            if (contentEles == null || contentEles.size() == 0) {
                return null;
            }
            // expect only one exists
            Element targetContent = contentEles.get(0);
            Word aWord = new Word();
            aWord.setDescription(word);
            Meaning meaning = new Meaning();
            String kind = "";

            // System.out.println(targetContent.getContent());
            List<Element> eles = targetContent.getChildElements();
            for (Element ele : eles) {
                // System.out.println(ele);
                if (ele.getName().equals("div")) {
                    kind = "";
                }
                // get the kind
                if (ele.getAttributeValue("class") != null && ele.getAttributeValue("class").equals("phanloai")) {
                    // set the word kind.
                    kind = ele.getContent().toString();
                    if (kind != null) {
                        String[] words = kind.split(" ");
                        kind = "";
                        int limit = words.length > 3 ? 3 : words.length;
                        for (int i = 0; i < limit; i++) {
                            kind += words[i] + " ";
                        }
                        kind = kind.trim();
                        LOGGER.info(Arrays.toString(kind.getBytes("UTF-8")));
                        LOGGER.info("Kind : " + kind);
                    }
                }
                if (ele.getName().equals("ul") && StringUtils.isNotEmpty(kind)) {
                    // convert kind
                    // log.info(Arrays.toString(kind.getBytes("UTF-8")));
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

                                        LOGGER.info("content : " + contentRaw);
                                    }
                                    if (content.getName().equals("ul")) {
                                        String example = content.getChildElements().get(0).getChildElements().get(0)
                                                .getContent().toString();
                                        if (StringUtils.isNotBlank(example)) {
                                            meaning.addExample(example);
                                            LOGGER.info("Example: " + example);
                                        }


                                    }
                                }
                            }
                            if (meaning != null) {


                                // log.info(meaning.getContent());
                                aWord.addMeaning(meaning);
                            }
                        }
                    }
                }
            }
            // log.info(aWord.getKindidmap());
            return aWord;
        } catch (Exception e) {
            LOGGER.error("Exception", e);
            return null;
        }
    }

    public VocabularyService getVocabularyService() {
        return vocabularyService;
    }

    @Autowired
    public void setVocabularyService(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }
}
