package com.nkhoang.gae.service;

public interface SpreadsheetService {
    public String findSpreadSheetCellUrlByTitle(String title, String worksheetName);

    public com.google.gdata.client.spreadsheet.SpreadsheetService getService();

}
