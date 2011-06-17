package com.nkhoang.gae.service.impl;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.Link;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchStatus;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpreadsheetServiceImpl implements com.nkhoang.gae.service.SpreadsheetService {
	private static final String SPREADSHEET_SERVICE_NAME    = "Spreadsheet Search";
	private static final Logger LOGGER                      = LoggerFactory.getLogger(SpreadsheetService.class);
	private static final int    MAXIMUM_CELL_UPDATE_AT_TIME = 20;
	private static final int    MAXIMUM_ROW_IN_A_COL        = 5000;

	// singleton service.
	private SpreadsheetService _service;
	// username and password are configured in a property file.
	private String             _username;
	private String             _password;


	/** Singleton Google Spreadsheet service. Username and password must be provided from the property file. */
	public SpreadsheetService getService() {
		if (_service == null) {
			try {
				_service = new SpreadsheetService(SPREADSHEET_SERVICE_NAME);
				_service.setUserCredentials(_username, _password);
				_service.setProtocolVersion(SpreadsheetService.Versions.V1);
			}
			catch (Exception e) {
				// set null to service if we cannot get it from Google.
				_service = null;
				LOGGER.error("Could not create google spreadsheet service", e);
			}
		}
		return _service;
	}

	/**
	 * Update word list stored in a list to Google spreadsheet document to be used later.
	 *
	 * @param wordList        word list data which data will be copied to google spreadsheet.
	 * @param spreadSheetName google spreadsheet file name.
	 * @param worksheetName   google worksheet name.
	 * @param offset          offset in case of the last update is failed and the next update will start from the offset.
	 *
	 * @throws Exception
	 */
	public void updateWordListToSpreadsheet(
		List<String> wordList, String spreadSheetName, String worksheetName,
		int offset) throws IOException, ServiceException {
		List<CellAddress> cellAddrs = new ArrayList<CellAddress>();
		int row = 1;
		int col = 1;
		int count = 0;
		// hold the character starting for a word.
		char c = wordList.get(0).toLowerCase().charAt(0);
		for (String s : wordList) {
			if (s.toLowerCase().charAt(0) != c) {
				c = s.toLowerCase().charAt(0);
				col++;
				row = 1;
			}
			cellAddrs.add(new CellAddress(row, col, wordList.get(count)));
			row++;
			if (row > MAXIMUM_ROW_IN_A_COL) {
				row = 1;
				col++;
			}
			count++;
		}
		do {
			URL cellFeedUrl = findSpreadSheetCellUrlByTitle(spreadSheetName, worksheetName);
			CellFeed cellFeed = getService().getFeed(cellFeedUrl, CellFeed.class);

			Map<String, CellEntry> cellEntries = getCellEntryMap(
				getService(), cellFeedUrl, cellAddrs, offset);

			CellFeed batchRequest = new CellFeed();
			for (int i = offset; i < offset + MAXIMUM_CELL_UPDATE_AT_TIME; i++) {
				CellAddress cellAddr = cellAddrs.get(i);
				URL entryUrl = new URL(cellFeedUrl.toString() + "/" + cellAddr.idString);
				CellEntry batchEntry = new CellEntry(cellEntries.get(cellAddr.idString));
				batchEntry.changeInputValueLocal(cellAddr.value);
				BatchUtils.setBatchId(batchEntry, cellAddr.idString);
				BatchUtils.setBatchOperationType(batchEntry, BatchOperationType.UPDATE);
				batchRequest.getEntries().add(batchEntry);
			}

			// Submit the update
			Link batchLink = cellFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM);
			CellFeed batchResponse = getService().batch(new URL(batchLink.getHref()), batchRequest);

			// Check the results
			boolean isSuccess = true;
			for (CellEntry entry : batchResponse.getEntries()) {
				String batchId = BatchUtils.getBatchId(entry);
				if (!BatchUtils.isSuccess(entry)) {
					isSuccess = false;
					BatchStatus status = BatchUtils.getBatchStatus(entry);
					LOGGER.info(
						String.format(
							"%s failed (%s) %s", batchId, status.getReason(), status.getContent()));
				}
			}

			offset += MAXIMUM_CELL_UPDATE_AT_TIME;
			LOGGER.info(String.format("Updating ... offset [ %s ]", offset));
		} while (offset < cellAddrs.size());
	}

	public static Map getCellEntryMap(
		SpreadsheetService ssSvc, URL cellFeedUrl, List<CellAddress> cellAddrs,
		int offset) throws IOException, ServiceException {
		CellFeed batchRequest = new CellFeed();
		for (int i = offset; i < offset + MAXIMUM_CELL_UPDATE_AT_TIME; i++) {
			CellAddress cellId = cellAddrs.get(i);
			CellEntry batchEntry = new CellEntry(cellId.row, cellId.col, cellId.idString);
			batchEntry.setId(String.format("%s/%s", cellFeedUrl.toString(), cellId.idString));
			BatchUtils.setBatchId(batchEntry, cellId.idString);
			BatchUtils.setBatchOperationType(batchEntry, BatchOperationType.QUERY);
			batchRequest.getEntries().add(batchEntry);
		}
		CellFeed cellFeed = ssSvc.getFeed(cellFeedUrl, CellFeed.class);
		CellFeed queryBatchResponse = ssSvc.batch(
			new URL(cellFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM).getHref()), batchRequest);

		Map<String, CellEntry> cellEntryMap = new HashMap<String, CellEntry>(cellAddrs.size());
		for (CellEntry entry : queryBatchResponse.getEntries()) {
			cellEntryMap.put(BatchUtils.getBatchId(entry), entry);
			if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
				String.format(
					"batch %s {CellEntry: id=%s editLink=%s inputValue=%s\n", BatchUtils.getBatchId(entry),
					entry.getId(), entry.getEditLink().getHref(), entry.getCell().getInputValue()));
			}
		}
		return cellEntryMap;
	}


	/**
	 * Find spreadsheet cell url by title.
	 *
	 * @return url or null.
	 */
	public URL findSpreadSheetCellUrlByTitle(String title, String worksheetName) {
		URL cellfeedUrl = null;
		try {
			SpreadsheetService _service = getService();

			URL metafeedUrl = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
			SpreadsheetFeed feed = _service.getFeed(metafeedUrl, SpreadsheetFeed.class);
			List<SpreadsheetEntry> spreadsheets = feed.getEntries();

			for (int i = 0; i < spreadsheets.size(); i++) {
				SpreadsheetEntry spreadsheetEntry = spreadsheets.get(i);
				String spreadsheetTitle = spreadsheetEntry.getTitle().getPlainText();
				//LOGGER.info("Spreadsheet title : " + spreadsheetTitle);
				if (spreadsheetTitle.equals(title)) {
					//LOGGER.info("\t" + spreadsheetTitle);
					List<WorksheetEntry> worksheets = spreadsheetEntry.getWorksheets();
					for (int j = 0; j < worksheets.size(); j++) {
						WorksheetEntry worksheetEntry = worksheets.get(j);

						String worksheetTitle = worksheetEntry.getTitle().getPlainText();
						if (worksheetTitle.equals(worksheetName)) {
							//LOGGER.info(worksheetTitle);
							URL url = worksheetEntry.getCellFeedUrl();
							if (url != null) {
								{
									cellfeedUrl = url;
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			LOGGER.debug(
				"Could not find the requested worksheet [" + worksheetName + "] or " + "spreadsheet " + "with name [" +
				title + "].");
		}
		return cellfeedUrl;
	}

	public String getUsername() {
		return _username;
	}

	public void setUsername(String username) {
		_username = username;
	}

	public String getPassword() {
		return _password;
	}

	public void setPassword(String password) {
		_password = password;
	}

	/** Part of getter and setter methods. */
	public void setService(SpreadsheetService service) {
		_service = service;
	}

	private static class CellAddress {
		public final int    row;
		public final int    col;
		public final String idString;
		public final String value;


		/**
		 * Constructs a CellAddress representing the specified {@code row} and
		 * {@code col}.  The idString will be set in 'RnCn' notation.
		 */
		public CellAddress(int row, int col, String value) {
			this.row = row;
			this.col = col;
			this.idString = String.format("R%sC%s", row, col);
			this.value = value.toLowerCase();
		}
	}


}


