package com.nkhoang.gae.test;

import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XMLModifier;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringWriter;

@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration({"/applicationContext-service.xml"})

public class VTDXMLTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(VTDXMLTest.class);

    @Test
    public void run() {

    }

    @Test
    public void testIVocabulary() throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/vocabulary.xml");

        if (is == null) {
            LOGGER.info("Could not load resources.");
        }

        VTDGen vg = new VTDGen(); // Instantiate VTDGen
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer);
        String theString = writer.toString();
        vg.setDoc(theString.getBytes());

        vg.parse(true);

        XMLModifier xm = new XMLModifier(); //Instantiate XMLModifier
        LOGGER.info("Starting to parse XML");
        VTDNav vn = vg.getNav();

        xm.bind(vn);

        AutoPilot ap = new AutoPilot(vn);

        ap.selectXPath("/Vocabulary/Root");
        int i = -1;
        while ((i = ap.evalXPath()) != -1) {
            xm.insertAfterHead("<Page title='abc' ></Page>");
        }

        ap.selectXPath("/Vocabulary/Root/Page");

        i = -1;
        while ((i = ap.evalXPath()) != -1) {
            xm.insertAfterHead("<Word sourceWord='def' targetWord='ghi'>");
        }

        ap.selectXPath("/Vocabulary/Root/Page/Word[@sourceWord='def']");

        i = -1;
        while ((i = ap.evalXPath()) != -1) {
            xm.insertAfterHead("<Comment>commnet 1</Comment>");
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        xm.output(bos);

        String xmlStr = bos.toString();

        LOGGER.info(xmlStr);


    }

    public void testXML() throws Exception {
        ClassLoader loader = this.getClass().getClassLoader();
        InputStream is = loader.getResourceAsStream("resources/MSLine.xml");

        if (is != null) {
            LOGGER.info("File loaded successfully");
        }


        VTDGen vg = new VTDGen(); // Instantiate VTDGen
        vg.parseFile("/Development/MSLine.xml", false);

        XMLModifier xm = new XMLModifier(); //Instantiate XMLModifier
        LOGGER.info("Starting to parse XML");
        VTDNav vn = vg.getNav();
        AutoPilot ap = new AutoPilot(vn);

        xm.bind(vn);


        int i = vn.getAttrVal("subcaption");
        if (i != -1) {
            xm.updateToken(i, "caption");
        }

        ap.selectXPath("/chart");

        i = vn.getAttrVal("yAxisMaxValue");
        if (i != -1) {
            xm.updateToken(i, "456");
        }

        ap.selectXPath("/chart");
        i = vn.getAttrVal("yAxisMinValue");
        if (i != -1) {
            xm.updateToken(i, "123");
        }

        ap.selectXPath("/chart/categories");
        i = -1;
        while ((i = ap.evalXPath()) != -1) {
            String categoryTag = "<category value='cccc' />";

            xm.insertAfterHead(categoryTag);
        }
        ap.selectXPath("/chart/dataset[@seriesName='VN']");
        i = -1;
        while ((i = ap.evalXPath()) != -1) {
            xm.insertAfterHead("<set value='bbbbb' />");

        }


        ap.selectXPath("/chart/dataset[@seriesName='International']");
        i = -1;

        while ((i = ap.evalXPath()) != -1) {
            xm.insertAfterHead("<set value='aaaaa' />");
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        xm.output(bos);

        LOGGER.info(bos.toString());

    }
}
