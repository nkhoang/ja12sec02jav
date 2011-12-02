package com.nkhoang.service;

import com.nkhoang.gae.model.PhoneCardDiscount;
import com.nkhoang.gae.service.SpreadsheetService;
import com.nkhoang.gae.utils.PhoneCardUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
  public void testGetChietKhau() throws Exception{
    List<PhoneCardDiscount> phoneCardDiscounts = PhoneCardUtils.getLatestPhoneCard(spreadsheetService);
    LOG.info(phoneCardDiscounts.toString());
  }

  @Test
  public void testGetChietKhauPrice() throws Exception {
    List<String> prices = spreadsheetService.querySpreadsheet("chietkhau", "chietkhau", 3, 2, 23);
    StringBuilder stringBuilder = new StringBuilder();
    for (String s : prices) {
      stringBuilder.append(s + "\n");
    }
    LOG.info(stringBuilder.toString());
  }

  @Test
  public void testGetChietKhauDate() throws Exception{
    List<String> dates = spreadsheetService.querySpreadsheet("chietkhau", "chietkhau", 2, 1, 1);

    DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yyyy");
    DateTime dateTime = fmt.parseDateTime(dates.get(0));
    LOG.info(dateTime.toString());
  }

  @Test
  public void testGetDataFromSpreadsheet() throws Exception {
    List<String> script = spreadsheetService.querySpreadsheet("POL_CONFIG", "POL_CONFIG", 1, 1, 136);
    List<String> test = spreadsheetService.querySpreadsheet("POL_CONFIG", "POL_CONFIG", 1, 2, 136);
    List<String> prod = spreadsheetService.querySpreadsheet("POL_CONFIG", "POL_CONFIG", 1, 3, 136);
    List<String> db = spreadsheetService.querySpreadsheet("POL_CONFIG", "POL_CONFIG", 1, 4, 136);
    List<String> updatedScript = spreadsheetService.querySpreadsheet("POL_CONFIG", "POL_CONFIG", 1, 5, 136);
    test.removeAll(prod);

    LOG.info("Total : " + test.size());
    StringBuilder stringBuilder = new StringBuilder();
    for (String s : test) {
      stringBuilder.append(s + "\n");
    }
    LOG.info(stringBuilder.toString());

    /*prod.removeAll(db);

          LOG.info("Total : " + test.size());
      stringBuilder = new StringBuilder();
      for (String s : prod) {
        stringBuilder.append(s + "\n");
      }
      LOG.info(stringBuilder.toString());*/
  }
}
