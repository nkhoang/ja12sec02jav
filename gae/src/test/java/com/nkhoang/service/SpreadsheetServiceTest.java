package com.nkhoang.service;

import com.nkhoang.gae.service.SpreadsheetService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext-service.xml", "/applicationContext-dao.xml"})
public class SpreadsheetServiceTest {
	@Autowired
	private SpreadsheetService spreadsheetService;
	private static final Logger LOG = LoggerFactory.getLogger(SpreadsheetServiceTest.class.getCanonicalName());

	@Test
	public void testGetDataFromSpreadsheet() throws Exception {
		List<String> script = spreadsheetService.querySpreadsheet("POL_CONFIG", "POL_CONFIG", 1, 1, 136);
		List<String> test = spreadsheetService.querySpreadsheet("POL_CONFIG", "POL_CONFIG", 1, 2, 136);
		List<String> prod = spreadsheetService.querySpreadsheet("POL_CONFIG", "POL_CONFIG", 1, 3, 136);
        List<String> db = spreadsheetService.querySpreadsheet("POL_CONFIG", "POL_CONFIG", 1, 4, 136);

        script.removeAll(prod);

        LOG.info("Total : " + test.size());
		StringBuilder stringBuilder = new StringBuilder();
		for (String s : script) {
			stringBuilder.append(s + "\n");
		}
		LOG.info(stringBuilder.toString());

        test.removeAll(db);
        test.removeAll(script);

		LOG.info("Total table in test in difference (-db, -script): " + test.size());
		stringBuilder = new StringBuilder();
		for (String s : test) {
			stringBuilder.append(s + "\n");
		}
		LOG.info(stringBuilder.toString());

        prod.removeAll(script);
        prod.removeAll(db);
        LOG.info("Total table in prod in difference (-test, -script, -db): " + prod.size());
        stringBuilder = new StringBuilder();
		for (String s : prod) {
			stringBuilder.append(s + "\n");
		}
		LOG.info(stringBuilder.toString());
	}
}
