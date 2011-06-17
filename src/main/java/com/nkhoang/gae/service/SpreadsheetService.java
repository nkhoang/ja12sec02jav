package com.nkhoang.gae.service;

import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public interface SpreadsheetService {
	public URL findSpreadSheetCellUrlByTitle(String title, String worksheetName);

	public com.google.gdata.client.spreadsheet.SpreadsheetService getService();

	public void updateWordListToSpreadsheet(List<String> wordList, String spreadSheetName,
	                                        String worksheetName, int offset) throws IOException, ServiceException;

	public List<String> querySpreadsheet(
		String spreadSheetName, String worksheetName, int row, int col, int size) throws IOException, ServiceException;

}
