package com.nkhoang.gae.test.spreadsheet;

import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.SpreadsheetService;
import com.nkhoang.gae.service.VocabularyService;
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
@ContextConfiguration({"/applicationContext-service.xml", "/applicationContext-dao.xml"})
public class SpreadsheetTest {
  public static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetTest.class);
  @Autowired
  private com.nkhoang.gae.service.SpreadsheetService _spreadsheetService;



  @Autowired
  private VocabularyService _vocabularyService;

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
    // _spreadsheetService.updateWordListToSpreadsheet(wordList, "abc", "abc", 80000, 82000);
    _spreadsheetService.updateWordListToSpreadsheet(wordList, "bcd", "bcd", 10000, 20000);
  }

  @Test
  public void testUpdateWordSpreadsheet() throws Exception {
	  List<Word> words = _vocabularyService.lookupWords("bcd", "bcd", 200, 1, 300);
	  LOGGER.info(String.format("Total word size : %s", words.size()));
	  List<String> data = _spreadsheetService.querySpreadsheet("wordlist", "wordlist", 1, 1, 10000);
	  int offset = data.size();
	  _spreadsheetService.updateWordMeaningToSpreadsheet(words, "wordlist", "wordlist", offset + 1, words.size());
  }

  @Test
  public void testQuerySpreadsheet() throws Exception {
    List<String> data = _spreadsheetService.querySpreadsheet("abc", "abc", 1, 1, 10000);
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

  public VocabularyService getVocabularyService() {
    return _vocabularyService;
  }

  public void setVocabularyService(VocabularyService _vocabularyService) {
    this._vocabularyService = _vocabularyService;
  }

}
