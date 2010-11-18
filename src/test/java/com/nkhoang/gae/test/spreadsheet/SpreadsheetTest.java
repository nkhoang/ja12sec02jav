package com.nkhoang.gae.test.spreadsheet;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hoangnk
 * Date: Nov 17, 2010
 * Time: 11:45:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpreadsheetTest {
    public static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetTest.class);

    @Test
    public void run() {
    }

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
            if (spreadsheetTitle.equals("Vocabulary")) {
                LOGGER.info("\t" + spreadsheetTitle);

                List<WorksheetEntry> worksheets = spreadsheetEntry.getWorksheets();
                for (int j = 0; j < worksheets.size(); j++) {
                    WorksheetEntry worksheetEntry = worksheets.get(j);
                    String worksheetTitle = worksheetEntry.getTitle().getPlainText();
                    if (worksheetTitle.equals("General Vocabulary")) {
                        LOGGER.info(worksheetTitle);

                        URL cellFeedUrl = worksheetEntry.getCellFeedUrl();
                        CellFeed cellFeed = service.getFeed(cellFeedUrl, CellFeed.class);
                        for (CellEntry cell : cellFeed.getEntries()) {
                            String shortId = cell.getId().substring(cell.getId().lastIndexOf('/') + 1);
                            LOGGER.info("Cell " + shortId + ": " + cell.getCell().getValue());
                            //System.out.println(" -- Cell(" + shortId + "/" + cell.getTitle().getPlainText()
                            //        + ") formula(" + cell.getCell().getInputValue() + ") numeric("
                             //       + cell.getCell().getNumericValue() + ") value("
                            //        + cell.getCell().getValue() + ")");

                        }

                    }
                }
            }
        }

    }
}
