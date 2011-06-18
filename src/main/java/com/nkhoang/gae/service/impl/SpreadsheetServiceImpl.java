package com.nkhoang.gae.service.impl;

import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.Link;
import com.google.gdata.data.batch.BatchOperationType;
import com.google.gdata.data.batch.BatchStatus;
import com.google.gdata.data.batch.BatchUtils;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.ServiceException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.model.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SpreadsheetServiceImpl implements com.nkhoang.gae.service.SpreadsheetService {
  private static final String SPREADSHEET_SERVICE_NAME = "Spreadsheet Search";
  private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetService.class);
  private static final int MAXIMUM_CELL_UPDATE_AT_TIME = 20;
  private static final int MAXIMUM_ROW_IN_A_COL = 15000;
  public static final int CORE_POOL_SIZE = 10;
  public static final int MAXIMUM_POOL_SIZE = 10;
  public static final int KEEP_ALIVE_TIME = 10;
  public static final int BATCH_REQUEST_SIZE = 2000;

  // singleton service.
  private SpreadsheetService _service;
  // username and password are configured in a property file.
  private String _username;
  private String _password;


  /**
   * Singleton Google Spreadsheet service. Username and password must be provided from the property file.
   */
  public SpreadsheetService getService() {
    if (_service == null) {
      try {
        _service = new SpreadsheetService(SPREADSHEET_SERVICE_NAME);
        _service.setUserCredentials(_username, _password);
        _service.setProtocolVersion(SpreadsheetService.Versions.V1);
      } catch (Exception e) {
        // set null to service if we cannot get it from Google.
        _service = null;
        LOGGER.error("Could not create google spreadsheet service", e);
      }
    }
    return _service;
  }

  public List<String> querySpreadsheet(
      String spreadSheetName, String worksheetName, int row, int col, int size) throws IOException, ServiceException {
    List<String> result = null;

    URL cellFeedUrl = findSpreadSheetCellUrlByTitle(spreadSheetName, worksheetName);
    CellQuery query = new CellQuery(cellFeedUrl);
    query.setMinimumRow(row);
    query.setMaximumRow(row + size);
    query.setMinimumCol(col);
    query.setMaximumCol(col);
    CellFeed feed = getService().query(query, CellFeed.class);

    if (feed.getRowCount() > 0) {
      LOGGER.info(String.format("Total found: %s", feed.getEntries().size()));
      result = new ArrayList<String>();

      for (CellEntry entry : feed.getEntries()) {
        String cellData = entry.getCell().getValue();
        result.add(cellData);
      }
    }

    return result;
  }

  public void updateWordMeaningToSpreadsheet(
      List<Word> wordList, String spreadSheetName, String worksheetName,
      int offset, int target) throws IOException, ServiceException {
    List<CellAddress> cellAddrs = new ArrayList<CellAddress>();

    Gson gson = null;
    List<String> excludeAttrs = Arrays.asList(Word.SKIP_FIELDS);
    if (excludeAttrs != null && excludeAttrs.size() > 0) {
      gson = new GsonBuilder().setExclusionStrategies(
          new GSONStrategy(excludeAttrs)).create();
    } else {
      gson = new Gson();
    }

    int rowIndex = 1;
    for (Word w : wordList) {
      cellAddrs.add(new CellAddress(rowIndex, 1, w.getDescription()));
      cellAddrs.add(new CellAddress(rowIndex, 2, gson.toJson(w)));
      rowIndex++;
    }

    // create ThreadPoolExecutor
    ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(MAXIMUM_POOL_SIZE);

    ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, queue, new ThreadPoolExecutor.CallerRunsPolicy());

    URL cellFeedUrl = findSpreadSheetCellUrlByTitle(spreadSheetName, worksheetName);
    CellFeed cellFeed = getService().getFeed(cellFeedUrl, CellFeed.class);
    List<String> failedTask = new ArrayList<String>();
    do {
      LOGGER.info(String.format("Batch from offset [%s] starting...", offset));
      // minimize the request to google service.
      Map<String, CellEntry> cellEntries = getCellEntryMap(
          getService(), cellFeedUrl, cellAddrs, offset, wordList.size() * 2);
      int batchTarget = offset + wordList.size() * 2;
      do {
        executor.execute(new UpdateDataTask(cellFeed, cellAddrs, offset, cellFeedUrl, cellEntries, failedTask));
        LOGGER.info(String.format("Updating ... offset [ %s ]", offset));
        offset += MAXIMUM_CELL_UPDATE_AT_TIME;
      } while (offset < batchTarget);
      // keep this until the batch finish then process to the next batch.
      do {
      } while (executor.getTaskCount() != executor.getCompletedTaskCount());
      if (failedTask.size() > 0) {
        for (String taskId : failedTask) {
          executor.execute(new UpdateDataTask(cellFeed, cellAddrs, Integer.parseInt(taskId), cellFeedUrl, cellEntries, failedTask));
        }
      }
    } while (offset < target);
    do {
      // LOGGER.info(String.format("Status: %s/%s", executor.getCompletedTaskCount(), executor.getTaskCount()));
    } while (executor.getTaskCount() != executor.getCompletedTaskCount());

  }


  /**
   * Update word list stored in a list to Google spreadsheet document to be used later.
   *
   * @param wordList        word list data which data will be copied to google spreadsheet.
   * @param spreadSheetName google spreadsheet file name.
   * @param worksheetName   google worksheet name.
   * @param offset          offset in case of the last update is failed and the next update will start from the offset.
   * @throws Exception
   */
  public void updateWordListToSpreadsheet(
      List<String> wordList, String spreadSheetName, String worksheetName,
      int offset, int target) throws IOException, ServiceException {
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

    // create ThreadPoolExecutor
    ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(MAXIMUM_POOL_SIZE);

    ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, queue, new ThreadPoolExecutor.CallerRunsPolicy());

    URL cellFeedUrl = findSpreadSheetCellUrlByTitle(spreadSheetName, worksheetName);
    CellFeed cellFeed = getService().getFeed(cellFeedUrl, CellFeed.class);
    List<String> failedTask = new ArrayList<String>();
    do {
      LOGGER.info(String.format("Batch from offset [%s] starting...", offset));
      // minimize the request to google service.
      Map<String, CellEntry> cellEntries = getCellEntryMap(
          getService(), cellFeedUrl, cellAddrs, offset, BATCH_REQUEST_SIZE);
      int batchTarget = offset + BATCH_REQUEST_SIZE;
      do {
        executor.execute(new UpdateDataTask(cellFeed, cellAddrs, offset, cellFeedUrl, cellEntries, failedTask));
        LOGGER.info(String.format("Updating ... offset [ %s ]", offset));
        offset += MAXIMUM_CELL_UPDATE_AT_TIME;
      } while (offset < batchTarget);
      // keep this until the batch finish then process to the next batch.
      do {
      } while (executor.getTaskCount() != executor.getCompletedTaskCount());
      if (failedTask.size() > 0) {
        for (String taskId : failedTask) {
          executor.execute(new UpdateDataTask(cellFeed, cellAddrs, Integer.parseInt(taskId), cellFeedUrl, cellEntries, failedTask));
        }
      }
    } while (offset < target);
    do {
      // LOGGER.info(String.format("Status: %s/%s", executor.getCompletedTaskCount(), executor.getTaskCount()));
    } while (executor.getTaskCount() != executor.getCompletedTaskCount());

  }

  /**
   * Establish a connection to Google Spreadsheet service to get cell addresses of a range of cells.
   * In order to do that we create a query request and send it to Google Spreadsheet service. In return, we get a list of entries which represents a list of cell addresses to use later.
   *
   * @param ssSvc       Google spreadsheet service.
   * @param cellFeedUrl cell feed URL.
   * @param cellAddrs   CellAddress list.
   * @param offset      offset to start with.
   * @param size        batch size.
   * @return a map of cell entries.
   * @throws IOException      if cannot write to Google Spreadsheet.
   * @throws ServiceException if cannot connect to google spreadsheet.
   */
  public static Map<String, CellEntry> getCellEntryMap(
      SpreadsheetService ssSvc, URL cellFeedUrl, List<CellAddress> cellAddrs,
      int offset, int size) throws IOException, ServiceException {
    // build batch request.
    CellFeed batchRequest = new CellFeed();
    int offsetTarget = offset + size;
    if (offsetTarget > cellAddrs.size()) {
      offsetTarget = cellAddrs.size();
    }
    for (int i = offset; i < offsetTarget; i++) {
      // cell id.
      CellAddress cellId = cellAddrs.get(i);
      // create batch entry.
      CellEntry batchEntry = new CellEntry(cellId.row, cellId.col, cellId.idString);
      // set cell id.
      batchEntry.setId(String.format("%s/%s", cellFeedUrl.toString(), cellId.idString));
      // set batch id.
      BatchUtils.setBatchId(batchEntry, cellId.idString);
      // set batch operation type.
      BatchUtils.setBatchOperationType(batchEntry, BatchOperationType.QUERY);
      // add batch entry to batch request.
      batchRequest.getEntries().add(batchEntry);
    }
    // get cell feed from cell feed URL.
    CellFeed cellFeed = ssSvc.getFeed(cellFeedUrl, CellFeed.class);
    // query and get the batch response.
    CellFeed queryBatchResponse = ssSvc.batch(
        new URL(cellFeed.getLink(Link.Rel.FEED_BATCH, Link.Type.ATOM).getHref()), batchRequest);

    // create cell entry Map.
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
    } catch (ServiceException se) {
      LOGGER.error(String.format("Could not connect to Google Spreadsheet service because : %s", se));
    } catch (IOException ioe) {
      LOGGER.error(String.format("Could not write to Google Spreadsheet because : %s", ioe));
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

  /**
   * Part of getter and setter methods.
   */
  public void setService(SpreadsheetService service) {
    _service = service;
  }

  private static class CellAddress {
    public final int row;
    public final int col;
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

  private class UpdateDataTask implements Runnable {

    private CellFeed cellFeed;
    private List<CellAddress> cellAddrs;
    private int offset;
    private URL cellFeedUrl;
    private int target;
    private Map<String, CellEntry> cellEntries;
    private List<String> failedTask;

    public UpdateDataTask(CellFeed cellFeed, List<CellAddress> cellAddrs, int offset, URL cellFeedUrl, Map<String, CellEntry> cellEntries, List<String> failedTask) {
      this.cellAddrs = cellAddrs;
      this.cellFeed = cellFeed;
      this.offset = offset;
      this.cellFeedUrl = cellFeedUrl;
      this.cellEntries = cellEntries;
      this.failedTask = failedTask;
    }

    @Override
    public void run() {
      try {
        CellFeed batchRequest = new CellFeed();
        int offsetTarget = offset + MAXIMUM_CELL_UPDATE_AT_TIME;
        if (offsetTarget > cellAddrs.size()) {
          offsetTarget = cellAddrs.size();
        }
        for (int i = offset; i < offsetTarget; i++) {
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
      } catch (Exception e) {
        LOGGER.error(String.format("Updating failed at offset [%s] because %s", offset, e), e);
        LOGGER.info("Add this offset to failed task to process later.");
        failedTask.add(String.format("%s", offset));
      }
    }
  }


}




