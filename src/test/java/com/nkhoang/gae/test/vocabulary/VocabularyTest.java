package com.nkhoang.gae.test.vocabulary;

import com.nkhoang.gae.model.Meaning;
import com.nkhoang.gae.model.Word;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext-service.xml"})
public class VocabularyTest {
    public static final Logger LOGGER = LoggerFactory.getLogger(VocabularyTest.class);

    @Test
    public void testLookupEN() {
        Word w = lookup("take");
        w = lookupENLongman(w, "take");
        //w = lookupENCambridge(w, "take");


        String test = showWord(w);

        LOGGER.info(test);


    }

    public String showWord(Word w) {
        StringBuilder sb = new StringBuilder();

        sb.append("Word : " + w.getDescription());

        Map<Long, List<Meaning>> m = w.getMeanings();

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
                            mainM.setContent(gramStr + " " + ftdef.getTextExtractor().toString());
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

                        aWord.addMeaning(aWord.getKindidmap().get(kind), mainM);

                        // process gram example
                        List<Element> gramexas = meaning.getAllElementsByClass("GramExa");
                        if (ftexas != null) {
                            for (Element gramexa : gramexas) {
                                Meaning mm = processSubExampleLongman(gramexa, "PROPFORM");
                                mm.setType("gram");
                                aWord.addMeaning(aWord.getKindidmap().get(kind), mm);
                            }
                        }

                        // process gram example
                        List<Element> colloexas = meaning.getAllElementsByClass("ColloExa");
                        if (ftexas != null) {
                            for (Element colloexa : colloexas) {
                                Meaning mm = processSubExampleLongman(colloexa, "COLLO");
                                mm.setType("collo");
                                aWord.addMeaning(aWord.getKindidmap().get(kind), mm);
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
            m.setContent(str);
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
                                        meaning.addExample(example);

                                        LOGGER.info("Example: " + example);
                                    }
                                }
                            }
                            if (meaning != null) {
                                // log.info(meaning.getContent());
                                aWord.addMeaning(aWord.getKindidmap().get(kind), meaning);
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
}
