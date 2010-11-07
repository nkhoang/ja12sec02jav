package com.nkhoang.gae.test;

import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XMLModifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration({"/applicationContext-service.xml"})

public class VTDXMLTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(VTDXMLTest.class);
    @Test
    public void testXML() throws Exception {
        ClassLoader loader = this.getClass().getClassLoader();
        InputStream is = loader.getResourceAsStream("resources/MSLine.xml");

        if (is != null) {
            LOGGER.info("File loaded successfully");
        }

        VTDGen vg = new VTDGen(); // Instantiate VTDGen
        XMLModifier xm = new XMLModifier(); //Instantiate XMLModifier
        LOGGER.info("Starting to parse XML");
        if (vg.parseFile("/Development/MSLine.xml", false)) {
            LOGGER.info("XML is OK!");
            VTDNav vn = vg.getNav();

            xm.bind(vn);

            AutoPilot ap = new AutoPilot(vn);
            ap.selectXPath("/chart/dataset[@seriesName='VN']");
            int i = -1;

            while ((i = ap.evalXPath()) != -1) {
                xm.insertAfterHead("\n<set label='Jan' value='17400' />\n");
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            xm.output(bos);

            LOGGER.info(bos.toString());


        }

    }
}
