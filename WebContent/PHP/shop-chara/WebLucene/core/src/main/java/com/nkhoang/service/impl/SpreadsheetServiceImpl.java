package com.nkhoang.service.impl;

import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hoangnk
 * Date: Nov 18, 2010
 * Time: 7:50:19 AM
 * To change this template use File | Settings | File Templates.
 */
@Service("spreadsheetService")
public class SpreadsheetServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetService.class);
    private SpreadsheetService service;

    public void initialize() {
        try {
            service = new SpreadsheetService("Batch Cell Demo");
            service.setUserCredentials("nkhoang.it@gmail.com", "me27&ml17");
            service.setProtocolVersion(SpreadsheetService.Versions.V1);
        } catch (Exception ex) {

        }
    }

    public void setService(SpreadsheetService service) {
        this.service = service;
    }

    public SpreadsheetService getService() {
        if (service == null) {
            initialize();
        }
        return service;
    }

    /**
     * Get list of word from google spreadsheet.
     * @return retrieved list.
     */
    public List<String> getWordList() {
        List<String> wordList = new ArrayList<String>();
        if (service == null) {
            initialize();
        }
        try {
            URL cellfeedURL = new URL("https://spreadsheets.google.com/feeds/cells/peZDHyA4LqhdRYA8Uv_sufw/od6/private/full");
            CellQuery query = new CellQuery(cellfeedURL);
            CellFeed cellfeed = service.query(query, CellFeed.class);

            List<CellEntry> cells = cellfeed.getEntries();
            for (CellEntry cell : cells) {
                // String shortId = cell.getId().substring(cell.getId().lastIndexOf('/') + 1);
                String cellValue = cell.getCell().getValue();

                if (StringUtils.isNotBlank(cellValue)) {
                    wordList.add(cellValue.trim().toLowerCase());
                }
            }
        } catch (Exception authex) {
            LOGGER.error("Could not communicate with Google Spreadsheet.", authex);
        }

        return wordList;

    }
}
