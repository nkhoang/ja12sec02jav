package com.nkhoang.gae.test.spreadsheet;

import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
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

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

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
	public void testUpdateSpreadsheet() throws Exception{
		List<String> wordList = FileUtils.readWordsFromFile("src/test/resources/word-list.txt");
		_spreadsheetService.updateWordListToSpreadsheet(wordList, "abc", "abc", 6000);
	}

	@Test
	public void testQuerySpreadsheet() throws Exception {
		List<String> data = _spreadsheetService.querySpreadsheet("abc", "abc", 1, 1, 20);
		Assert.assertTrue(data.size() > 0);
	}



    @Test
    public void testLoopData() {
        try {
            int col = 1;
            int index = 0;
            URL cellfeedURL = new URL("https://spreadsheets.google.com/feeds/cells/peZDHyA4LqhdRYA8Uv_sufw/od6/private/full");
            CellQuery query = new CellQuery(cellfeedURL);
            query.setMinimumCol(col);
            query.setMaximumCol(col);
            CellFeed cellfeed = _spreadsheetService.getService().query(query, CellFeed.class);

            List<CellEntry> cells = cellfeed.getEntries();

            // check index if it exceed the maximum row
            if (index == cells.size()) {
                // maximum row reached.
                col += 1;
                // reset index.
                index = 0;
                if (col > cellfeed.getColCount()) { // check column index.
                    LOGGER.info("End of document.");
                }
            }

            CellEntry cell = cells.get(index);

            // String shortId = cell.getId().substring(cell.getId().lastIndexOf('/') + 1);
            String cellValue = cell.getCell().getValue();
            if (cellValue.trim().contains(" ")) {
                throw new IllegalArgumentException("Illegal param for a URL");
            }
            // vocabularyService.save(cellValue.trim().toLowerCase());
            LOGGER.info("Saving value = ." + cellValue.trim().toLowerCase());
            index += 1;

        } catch (Exception
                authex) {
            LOGGER.error("Could not communicate with Google Spreadsheet.", authex);
        }

    }

    public com.nkhoang.gae.service.SpreadsheetService getSpreadsheetService() {
        return _spreadsheetService;
    }

    public void setSpreadsheetService(com.nkhoang.gae.service.SpreadsheetService spreadsheetService) {
        _spreadsheetService = spreadsheetService;
    }

}
