package com.nkhoang.gae.ws;

import com.nkhoang.common.xml.XMLUtil;
import com.nkhoang.common.xml.XSLTUtil;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.VocabularyService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

@Service
@Path("/")
public class VocabularyRESTServiceImpl {
    private static Logger LOG = LoggerFactory.getLogger(VocabularyRESTServiceImpl.class.getCanonicalName());
    private VocabularyService vocabularyService;

    @GET
    @Produces("application/xml")
    @Path("/search/{word}")
    public String search(@PathParam("word") String word) {
        String result = null;
        try {

            JAXBContext context = JAXBContext.newInstance(Word.class);

            Word w = vocabularyService.lookupVN(word);

            Writer writer = new StringWriter();
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(w, writer);

            String xml = writer.toString();
            // transform result.
            LOG.info(XMLUtil.prettyPrint(xml));

            InputStream is = this.getClass().getResourceAsStream("word.xslt");
            String xslt = IOUtils.toString(is);
            // LOG.info(xslt);

            result = XSLTUtil.transform(xml, xslt);
            LOG.info(XMLUtil.prettyPrint(result));
        } catch (Exception ex) {
            LOG.error(String.format("WS could not lookup: ", word), ex);
        }

        return result;
    }

    public VocabularyService getVocabularyService() {
        return vocabularyService;
    }

    public void setVocabularyService(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }
}
