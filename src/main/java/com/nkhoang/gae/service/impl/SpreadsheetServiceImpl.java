package com.nkhoang.gae.service.impl;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: hoangnk
 * Date: Nov 18, 2010
 * Time: 7:50:19 AM
 * To change this template use File | Settings | File Templates.
 */
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
}
