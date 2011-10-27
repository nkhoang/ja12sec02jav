package com.nkhoang.gae.action;

import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.manager.UserManager;
import com.nkhoang.gae.model.User;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.VocabularyService;
import com.nkhoang.gae.service.impl.SpreadsheetServiceImpl;
import com.nkhoang.gae.view.JSONView;
import com.nkhoang.gae.view.constant.ViewConstant;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

@Deprecated
public class VocabularyAction {
	private static final Logger LOG = LoggerFactory
		.getLogger(VocabularyAction.class.getCanonicalName());
	private static final int    IVOCABULARY_TOTAL_ITEM = 40;
	private static final int    IVOCABULARY_PAGE_SIZE  = 20;

	@Autowired
	private SpreadsheetServiceImpl spreadsheetService;
	@Autowired
	private VocabularyService      vocabularyService;
	@Autowired
	private UserManager            userService;

	@Value("${google.username}")
	private String _username;
	@Value("${google.username}")
	private String _password;

	private final String APP_NAME = "Chara";
	// Google Document Service.
	DocsService docsService = new DocsService(APP_NAME);


	/** Render home page for vocabulary. */
	@RequestMapping(value = "/" + ViewConstant.VOCABULARY_HOME_REQUEST, method = RequestMethod.GET)
	public String renderVocabularyPage() {
		return ViewConstant.VOCABULARY_VIEW;
	}


	@RequestMapping(value = "/" + ViewConstant.VOCABULARY_MANAGER_REQUEST, method = RequestMethod.GET)
	public String renderVocabularyManagerPage() {
		return "vocabulary/manager";
	}



	@RequestMapping(value = "/" + "vocabularyBuilder", method = RequestMethod.GET)
	public ModelAndView renderIVocabularyBuilder() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("vocabulary/vocabularyBuilder");

		return mav;
	}





	//http://localhost:7070/vocabulary/updateViaGD.html?spreadsheetName=wordlist&worksheetName=wordlist&row=1&col=2&size=10

	/** Look up data from Google docs excel file and then update to GAE datastore. */
	@RequestMapping(value = "/" + ViewConstant.VOCABULARY_UPDATE_REQUEST, method = RequestMethod.GET)
	public void updateWordsFromSpreadSheet(
		@RequestParam("index") String startingIndex, @RequestParam("col") String columnIndex,
		@RequestParam("fileName") String fileName, @RequestParam("sheetName") String sheetName,
		HttpServletResponse response) {
		String cellfeedUrlStr = "https://spreadsheets.google.com/feeds/cells/peZDHyA4LqhdRYA8Uv_sufw/od6/private/full";
		// get the cellfeedURL
		if (StringUtils.isNotEmpty(fileName) && StringUtils.isNotEmpty(sheetName)) {
			// make sure that this url is not null.
			String searchedCellfeedUrl = spreadsheetService.findSpreadSheetCellUrlByTitle(fileName, sheetName)
			                                               .toString();
			if (StringUtils.isNotBlank(searchedCellfeedUrl)) {
				cellfeedUrlStr = searchedCellfeedUrl;
				LOG.debug("Found spreadsheet URL: " + cellfeedUrlStr);
			}
		}
		final long start = System.currentTimeMillis();

		int index = 0;
		int col = 1; // column index starting from 1 not 0.
		if (StringUtils.isNotEmpty(startingIndex)) { // parse starting index.
			try {
				index = Integer.parseInt(startingIndex);
			}
			catch (Exception e) {
				LOG
					.debug("Could not parse request param for update from spreadsheet. Continue with index = " + index);
			}
		}
		// parse column index.
		if (StringUtils.isNotEmpty(columnIndex)) {
			try {
				col = Integer.parseInt(columnIndex);
			}
			catch (Exception e) {
				LOG
					.debug("Could not parse request param for update from spreadsheet. Continue with index = " + index);
			}
		}
		boolean finished = false;
		while (System.currentTimeMillis() - start < 16384 && !finished) {
			try {
				URL cellfeedURL = new URL(cellfeedUrlStr);
				CellQuery query = new CellQuery(cellfeedURL);
				query.setMinimumCol(col);
				query.setMaximumCol(col);
				CellFeed cellfeed = spreadsheetService.getService().query(query, CellFeed.class);

				List<CellEntry> cells = cellfeed.getEntries();
				LOG.info("TOTAL items found: " + cells.size());

				// check index if it exceed the maximum row
				if (index == cells.size()) {
					// maximum row reached.
					col += 1;
					// reset index.
					index = 0;
					if (col > cellfeed.getColCount()) { // check column index.
						LOG.info("End of document.");
						break;
					}
				}

				CellEntry cell = cells.get(index);

				// String shortId = cell.getId().substring(cell.getId().lastIndexOf('/') + 1);
				String cellValue = cell.getCell().getValue();
				try {
					if (cellValue.trim().contains(" ")) {
						throw new IllegalArgumentException("Illegal param for a URL");
					}
					vocabularyService.save(cellValue.trim().toLowerCase());
					index += 1;

				}
				catch (IOException ex) {
					LOG.error("Could not process word: " + cellValue, ex);
				}
				catch (IllegalArgumentException iae) {
					index += 1;
				}
				//LOG.info("Index: " + index + " Cell " + shortId + ": " + cell.getCell().getValue());
				LOG.info(">>>>>>>>>>>>>>>>>>> Posting to Queue with index: [" + index + "] and col [" + col + "]");
				QueueFactory.getDefaultQueue().add(
					url(
						"/vocabulary/update.html?index=" + index + "&col=" + col + "&fileName=" + fileName +
						"&sheetName=" + sheetName).method(TaskOptions.Method.GET));

				finished = true;
			}
			catch (Exception authex) {
				LOG.error("Could not communicate with Google Spreadsheet.", authex);
			}
		}
		response.setContentType("text/html");
		try {
			response.getWriter().write("Being updated. Stay tuned!");
		}
		catch (IOException ioe) {
			LOG.error("Could not write to response.", ioe);
		}

	}

	/**
	 * Populate word with meanings and examples
	 *
	 * @param idStr id to be populated.
	 *
	 * @return a fully populated Word.
	 */
	@RequestMapping(value = "/" + ViewConstant.VOCABULARY_VIEW_POPULATE_WORD_REQUEST, method = RequestMethod.POST)
	public ModelAndView populateWord(@RequestParam("id") String idStr) {
		Long id = 0L;
		Word w = null;
		ModelAndView modelAndView = new ModelAndView();
		if (StringUtils.isNotEmpty(idStr)) {
			id = Long.parseLong(idStr);
			w = vocabularyService.populateWord(id);
		}
		Map<String, Object> jsonData = new HashMap<String, Object>();
		jsonData.put("word", w);

		View jsonView = new JSONView();
		modelAndView.setView(jsonView);

		List<String> attrs = new ArrayList<String>();
		attrs.addAll(Arrays.asList(Word.SKIP_FIELDS));
		modelAndView.addObject(GSONStrategy.EXCLUDE_ATTRIBUTES, attrs);

		modelAndView.addObject(GSONStrategy.DATA, jsonData);

		return modelAndView;
	}

	@RequestMapping(value = "/" + ViewConstant.VOCABULARY_WORD_KIND_REQUEST, method = RequestMethod.POST)
	public ModelAndView getWordKind() {
		ModelAndView modelAndView = new ModelAndView();
		View jsonView = new JSONView();
		modelAndView.setView(jsonView);
		Map<String, Object> jsonData = new HashMap<String, Object>();
		jsonData.put("wordKind", Word.WORD_KINDS);
		modelAndView.addObject(GSONStrategy.DATA, jsonData);

		return modelAndView;
	}


	/** Save and lookup word with meanings and examples */
	@RequestMapping(value = "/" + ViewConstant.VOCABULARY_LOOKUP_REQUEST, method = RequestMethod.GET)
	public ModelAndView lookupWord(@RequestParam("word") String wordStr) {
		Long id = 0L;
		Word w = null;
		ModelAndView modelAndView = new ModelAndView();
		try {
			if (StringUtils.isNotEmpty(wordStr)) {
				w = vocabularyService.save(wordStr);
			}
		}
		catch (IOException ex) {
			LOG.error("Cound not process word: " + wordStr, ex);
		}
		Map<String, Object> jsonData = new HashMap<String, Object>();
		jsonData.put("word", w);

		View jsonView = new JSONView();
		modelAndView.setView(jsonView);

		List<String> attrs = new ArrayList<String>();
		attrs.addAll(Arrays.asList(Word.SKIP_FIELDS));
		modelAndView.addObject(GSONStrategy.EXCLUDE_ATTRIBUTES, attrs);

		modelAndView.addObject(GSONStrategy.DATA, jsonData);

		return modelAndView;
	}

	@RequestMapping(value = "/" + ViewConstant.VOCABULARY_VIEW_RECENT_WORD_REQUEST, method = RequestMethod.GET)
	public ModelAndView listRecentWords(
		@RequestParam("offset") int offset, @RequestParam("size") int size) {
		ModelAndView modelAndView = new ModelAndView();
		Map<String, Object> jsonData = new HashMap<String, Object>();
		List<Word> words = vocabularyService.getAllWordsByRangeWithoutMeanings(offset, size);
		jsonData.put("words", words);

		View jsonView = new JSONView();
		modelAndView.setView(jsonView);

		List<String> attrs = new ArrayList<String>();
		attrs.addAll(Arrays.asList(Word.SKIP_FIELDS));
		modelAndView.addObject(GSONStrategy.EXCLUDE_ATTRIBUTES, attrs);

		modelAndView.addObject(GSONStrategy.DATA, jsonData);
		return modelAndView;
	}


	@RequestMapping(value = "/" + ViewConstant.VOCABULARY_VIEW_WORD_RANGE_REQUEST, method = RequestMethod.POST)
	public ModelAndView listWordsInRange(@RequestParam("size") String sizeStr) {
		int size = 10;
		if (StringUtils.isNotEmpty(sizeStr)) {
			size = Integer.parseInt(sizeStr);
		}

		User user = getUserCredential();

		ModelAndView modelAndView = new ModelAndView();
		if (user != null) {
			Map<String, Object> jsonData = new HashMap<String, Object>();
			List<Word> words = vocabularyService.getAllWordsById(user.getWordList(), true);
			words.addAll(vocabularyService.getAllWordsByRangeWithoutMeanings(0, size)); // get all DB words.
			jsonData.put("words", words);

			View jsonView = new JSONView();
			modelAndView.setView(jsonView);

			List<String> attrs = new ArrayList<String>();
			attrs.addAll(Arrays.asList(Word.SKIP_FIELDS));
			modelAndView.addObject(GSONStrategy.EXCLUDE_ATTRIBUTES, attrs);

			modelAndView.addObject(GSONStrategy.DATA, jsonData);
		} else {
			modelAndView.setViewName(ViewConstant.AUTHORIZATION_ERROR_VIEW);
		}
		return modelAndView;

	}

	/** Add a new word to the list. */
	@RequestMapping(value = "/" + ViewConstant.VOCABULARY_ADD_WORD_REQUEST, method = RequestMethod.POST)
	public ModelAndView submitWord(@RequestParam String word) {

		ModelAndView modelAndView = new ModelAndView();
		Map<String, Object> jsonData = new HashMap<String, Object>();
		boolean result = false;

		User user = getUserCredential();
		if (user != null && StringUtils.isNotEmpty(word)) {
			try {
				Word savedWord = vocabularyService.save(word);
				user.getWordList().add(savedWord.getId());
				userService.update(user);
			}
			catch (IOException ex) {
			}
		}

		jsonData.put("result", result);
		View jsonView = new JSONView();
		modelAndView.setView(jsonView);
		modelAndView.addObject(GSONStrategy.DATA, jsonData);

		return modelAndView;

	}

	private User getUserCredential() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = null;
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal != null && principal instanceof User) {
				currentUser = (User) principal;
			}
		}
		return userService.getUserByUsername(currentUser.getUsername());
	}

	public void setVocabularyService(VocabularyService vocabularyService) {
		this.vocabularyService = vocabularyService;
	}

	public VocabularyService getVocabularyService() {
		return vocabularyService;
	}

	public void setUserService(UserManager userService) {
		this.userService = userService;
	}

	public UserManager getUserService() {
		return userService;
	}

	public SpreadsheetServiceImpl getSpreadsheetService() {
		return spreadsheetService;
	}

	public void setSpreadsheetService(SpreadsheetServiceImpl spreadsheetService) {
		this.spreadsheetService = spreadsheetService;
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

}
