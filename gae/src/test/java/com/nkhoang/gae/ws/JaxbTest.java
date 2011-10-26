package com.nkhoang.gae.ws;

import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.VocabularyService;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext-service.xml", "/applicationContext-dao.xml"})
public class JaxbTest {
    @Autowired
    private VocabularyService vocabularyService;

    private static Logger LOG = LoggerFactory.getLogger(JaxbTest.class.getCanonicalName());

	@BeforeClass
	public static void testGetResource() {
		URL url = JaxbTest.class.getResource("word.xml");
		if (url != null) {
			LOG.info("URL is not null");
		} else {
			LOG.info("URL is null");
		}
	}


    @Test
    public void testWordMarshaller() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Word.class);

        Word w = vocabularyService.lookupVN("help");

        Writer writer = new StringWriter();
        Marshaller marshaller = context.createMarshaller();
        context.createMarshaller().marshal(w, writer);
        Assert.assertTrue(StringUtils.isNotEmpty(writer.toString()));
        // LOG.info(XMLUtil.prettyPrint(writer.toString()));
    }

    @Test
    public void testWordUnmarshaller() throws Exception {
        JAXBContext context = JAXBContext.newInstance(Word.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        Word w = (Word) unmarshaller.unmarshal(this.getClass().getResourceAsStream("word.xml"));
        Assert.assertNotNull(w);
        Assert.assertTrue(w.getMeanings().size() > 0);
    }

    public VocabularyService getVocabularyService() {
        return vocabularyService;
    }

    public void setVocabularyService(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }
}
