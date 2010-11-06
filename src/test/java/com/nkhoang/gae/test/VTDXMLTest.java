package com.nkhoang.gae.test;

import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileOutputStream;

@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration({ "/applicationContext-service.xml" })

public class VTDXMLTest {
    @Test
    public void testXML() throws Exception{
         		VTDGen vg = new VTDGen();
		AutoPilot ap = new AutoPilot();
		ap.selectXPath("/CATALOG/CD");

        if (vg.parseFile("MSLine.xml", false))
                {
                    VTDNav vn = vg.getNav();
                    ap.bind(vn);
                    if (ap.evalXPath() == -1)
                    {
                        System.out.println("XPath eval failed");
                        System.exit(0);
                    }
                    fillTemplate(vn, "Empire Burlesque", "Bob Dylan", "USA", "Columbia", 10.90, 1985);
                    if (ap.evalXPath() == -1)
                    {
                        System.out.println("XPath eval failed");
                        System.exit(0);
                    }
                    fillTemplate(vn, "Still Got the Blues", "Gary More", "UK", "Virgin Records", 10.20, 1990);
                    // dump out the XML
                    fos.write(vn.getXML().getBytes());
                    fos.close();
                }

    }
}
