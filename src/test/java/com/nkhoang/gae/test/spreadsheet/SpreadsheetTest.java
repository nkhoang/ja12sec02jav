package com.nkhoang.gae.test.spreadsheet;

import com.nkhoang.gae.utils.FileUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration({"/applicationContext-service.xml"})
public class SpreadsheetTest {
  public static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetTest.class);
  @Autowired
  private com.nkhoang.gae.service.SpreadsheetService _spreadsheetService;

  @Ignore
  @Test
  public void testFindSpreadsheetByTitle() {
    String cellfeedUrl = _spreadsheetService.findSpreadSheetCellUrlByTitle("wordlist", "wordlist").toString();
    LOGGER.info(cellfeedUrl);
    Assert.assertNotNull(cellfeedUrl);
  }

  @Test
  public void testUpdateSpreadsheet() throws Exception {
    List<String> wordList = FileUtils.readWordsFromFile("src/test/resources/word-list.txt");
    _spreadsheetService.updateWordListToSpreadsheet(wordList, "abc", "abc", 80000, 150000);
    // _spreadsheetService.updateWordListToSpreadsheet(wordList, "bcd", "bcd", 0, 1000);
  }

  @Test
  public void testQuerySpreadsheet() throws Exception {
    List<String> data = _spreadsheetService.querySpreadsheet("abc", "abc", 1, 2, 16000);
    Assert.assertTrue(data.size() > 0);
  }

  @Test
  public void testTotalUpdate() throws Exception {
    int rowTarget = 4;
    int total = 0;
    for (int i = 1; i <= rowTarget; i++) {
      List<String> data = _spreadsheetService.querySpreadsheet("abc", "abc", 1, i, 16000);
      total += data.size();
      LOGGER.info(String.format("Total at col [%s]: %s", i, total));
    }
    LOGGER.info(String.format("Total : %s", total));
  }

  public com.nkhoang.gae.service.SpreadsheetService getSpreadsheetService() {
    return _spreadsheetService;
  }

  public void setSpreadsheetService(com.nkhoang.gae.service.SpreadsheetService spreadsheetService) {
    _spreadsheetService = spreadsheetService;
  }

}
