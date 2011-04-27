package com.nkhoang.gae.test.spreadsheet;

import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
import com.nkhoang.gae.service.VocabularyService;
import com.nkhoang.gae.service.impl.SpreadsheetServiceImpl;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URL;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration({"/applicationContext-service.xml"})
public class SpreadsheetTest {
    public static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetTest.class);


    @Autowired
    private com.nkhoang.gae.service.SpreadsheetService _spreadsheetService;

    @Test
    public void run() {
        Assert.assertNotNull(_spreadsheetService);
    }

    @Test
    public void testFindSpreadsheetByTitle() {
        String cellfeedUrl = _spreadsheetService.findSpreadSheetCellUrlByTitle("Vocabulary","General Vocabulary");
        LOGGER.info(cellfeedUrl);
        Assert.assertNotNull(cellfeedUrl);
    }

    @Ignore
    @Test
    public void testGetWorksheet() throws Exception {
        SpreadsheetService service = new SpreadsheetService("Batch Cell Demo");
        service.setUserCredentials("", "");
        service.setProtocolVersion(SpreadsheetService.Versions.V1);

        URL cellfeedURL = new URL("https://spreadsheets.google.com/feeds/cells/peZDHyA4LqhdRYA8Uv_sufw/od6/private/full");
        // CellFeed cellfeed = service.getFeed(worksheetURL, CellFeed.class);
        CellQuery query = new CellQuery(cellfeedURL);
        query.setMinimumCol(2);
        query.setMaximumCol(2);


        CellFeed cellfeed = service.query(query, CellFeed.class);

        LOGGER.info("Size = " + cellfeed.getEntries().size());
        LOGGER.info("Maximum COl = " + cellfeed.getColCount());

        List<CellEntry> cells = cellfeed.getEntries();
        for (CellEntry cell : cells) {
            String shortId = cell.getId().substring(cell.getId().lastIndexOf('/') + 1);
            LOGGER.info(shortId);
            // LOGGER.info("Cell " + shortId + ": " + cell.getCell().getValue());
            //System.out.println(" -- Cell(" + shortId + "/" + cell.getTitle().getPlainText()
            //        + ") formula(" + cell.getCell().getInputValue() + ") numeric("
            //       + cell.getCell().getNumericValue() + ") value("
            //        + cell.getCell().getValue() + ")");


        }


    }

    @Ignore
    @Test
    public void testGetData() throws Exception {
        SpreadsheetService service = new SpreadsheetService("Batch Cell Demo");
        service.setUserCredentials("nkhoang.it@gmail.com", "me27&ml17");
        service.setProtocolVersion(SpreadsheetService.Versions.V1);

        URL metafeedUrl = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
        SpreadsheetFeed feed = service.getFeed(metafeedUrl, SpreadsheetFeed.class);
        List<SpreadsheetEntry> spreadsheets = feed.getEntries();

        for (int i = 0; i < spreadsheets.size(); i++) {
            SpreadsheetEntry spreadsheetEntry = spreadsheets.get(i);
            String spreadsheetTitle = spreadsheetEntry.getTitle().getPlainText();
            LOGGER.info("Spreadsheet title : " + spreadsheetTitle);
            if (spreadsheetTitle.equals("Vocabulary")) {
                LOGGER.info("\t" + spreadsheetTitle);

                List<WorksheetEntry> worksheets = spreadsheetEntry.getWorksheets();
                for (int j = 0; j < worksheets.size(); j++) {
                    WorksheetEntry worksheetEntry = worksheets.get(j);

                    String worksheetTitle = worksheetEntry.getTitle().getPlainText();
                    if (worksheetTitle.equals("General Vocabulary")) {
                        LOGGER.info(worksheetTitle);
                        LOGGER.info(worksheetEntry.getId());

                        URL cellFeedUrl = worksheetEntry.getCellFeedUrl();
                        CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);
                        for (CellEntry cell : cellFeed.getEntries()) {
                            String shortId = cell.getId().substring(cell.getId().lastIndexOf('/') + 1);
                            // LOGGER.info("Cell " + shortId + ": " + cell.getCell().getValue());
                            // System.out.println(" -- Cell(" + shortId + "/" + cell.getTitle().getPlainText()
                            //        + ") formula(" + cell.getCell().getInputValue() + ") numeric("
                            //       + cell.getCell().getNumericValue() + ") value("
                            //        + cell.getCell().getValue() + ")");

                        }

                    }
                }
            }
        }

    }


    public com.nkhoang.gae.service.SpreadsheetService getSpreadsheetService() {
        return _spreadsheetService;
    }

    public void setSpreadsheetService(com.nkhoang.gae.service.SpreadsheetService spreadsheetService) {
        _spreadsheetService = spreadsheetService;
    }

}
