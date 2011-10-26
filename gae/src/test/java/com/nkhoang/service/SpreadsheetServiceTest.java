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
		List<String> production_pol_config = spreadsheetService.querySpreadsheet("POL_CONFIG", "POL_CONFIG", 1, 1, 101);
		List<String> dev_pol_config = spreadsheetService.querySpreadsheet("POL_CONFIG", "POL_CONFIG", 1, 2, 101);
		List<String> dev_pol_existing = spreadsheetService.querySpreadsheet("POL_CONFIG", "POL_CONFIG", 1, 3, 101);
		production_pol_config.removeAll(dev_pol_config);
		production_pol_config.removeAll(dev_pol_existing);
		LOG.info("Total count : " + production_pol_config.size());
		StringBuilder stringBuilder = new StringBuilder();
		for (String s : production_pol_config) {
			stringBuilder.append(s + "\n");
		}
		LOG.info(stringBuilder.toString());

		LOG.info("POL_DATA_A :");

		dev_pol_config = spreadsheetService.querySpreadsheet("POL_CONFIG", "POL_A", 1, 1, 150);
		production_pol_config = spreadsheetService.querySpreadsheet("POL_CONFIG", "POL_A", 1, 2, 150);
		production_pol_config.removeAll(dev_pol_config);
		stringBuilder = new StringBuilder();
		for (String s : production_pol_config) {
			stringBuilder.append(s + "\n");
		}
		LOG.info(stringBuilder.toString());

		LOG.info("POL_DATA_B :");
		dev_pol_config = spreadsheetService.querySpreadsheet("POL_CONFIG", "POL_B", 1, 1, 151);
		production_pol_config = spreadsheetService.querySpreadsheet("POL_CONFIG", "POL_B", 1, 2, 151);
		production_pol_config.removeAll(dev_pol_config);
		stringBuilder = new StringBuilder();
		for (String s : production_pol_config) {
			stringBuilder.append(s + "\n");
		}
		LOG.info(stringBuilder.toString());
	}
}
