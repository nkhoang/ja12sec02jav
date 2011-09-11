package com.nkhoang.gae.action;

import com.google.appengine.api.taskqueue.*;
import com.google.appengine.api.taskqueue.Queue;
import com.google.gdata.client.docs.DocsService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nkhoang.common.xml.XMLUtil;
import com.nkhoang.gae.dao.MessageDao;
import com.nkhoang.gae.dao.WordItemDao;
import com.nkhoang.gae.dao.WordItemStatDao;
import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.manager.UserManager;
import com.nkhoang.gae.model.*;
import com.nkhoang.gae.service.VocabularyService;
import com.nkhoang.gae.service.impl.SpreadsheetServiceImpl;
import com.nkhoang.gae.utils.FileUtils;
import com.nkhoang.gae.utils.MailUtils;
import com.nkhoang.gae.utils.TemplateUtils;
import com.nkhoang.gae.view.JSONView;
import com.nkhoang.gae.view.XMLView;
import com.nkhoang.gae.view.constant.ViewConstant;
import com.nkhoang.gae.vocabulary.IVocabularyUtils;
import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XMLModifier;
import freemarker.template.TemplateException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.xalan.xsltc.compiler.Template;
import org.junit.Assert;
import org.junit.experimental.theories.PotentialAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/** VocaAction will replace the old {@link VocabularyAction} to handle vocabulary request. */
@Controller
@RequestMapping("/" + ViewConstant.VOCABULARY_NAMESPACE)
public class VocaAction {
	private static final Logger LOG                      = LoggerFactory.getLogger(VocaAction.class.getCanonicalName());
	private static final int    TOTAL_AMOUNT_LOOKUP_WORD = 100;
	private static final String WORD_ITEM_STATUS_FAILED  = "F";

	@Autowired
	private SpreadsheetServiceImpl spreadsheetService;
	@Autowired
	private VocabularyService      vocabularyService;
	@Autowired
	private MessageDao             messageDao;
	@Autowired
	private WordItemDao            wordItemDao;
	@Autowired
	private WordItemStatDao        wordItemStatDao;

	@Autowired
	private UserManager userService;

	@Value("${google.username}")
	private String _username;
	@Value("${google.username}")
	private String _password;

	// TODO: value is null.
	@Value("${sender.email}")
	private String senderEmail = "nkhoang4survey@gmail.com";

	private static String SENDER_EMAIL = "nkhoang4survey@gmail.com";

	private final String APP_NAME = "Chara";
	// Google Document Service.
	DocsService docsService = new DocsService(APP_NAME);

	/**
	 * Render index page.
	 *
	 * @return {@link ModelAndView} object
	 */
	@RequestMapping(value = "/" + ViewConstant.VOCABULARY_INDEX_REQUEST, method = RequestMethod.GET)
	public ModelAndView renderVocabularyIndex() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(ViewConstant.VOCABULARY_INDEX_VIEW);

		return mav;
	}

    @RequestMapping(value = "/xml")
    public void testJaxb() {
        try {
           JAXBContext context = JAXBContext.newInstance(Word.class);

        Word w = vocabularyService.lookupVN("help");

        Writer writer = new StringWriter();
        context.createMarshaller().marshal(w, writer);
        LOG.info(XMLUtil.prettyPrint(writer.toString()));
        }catch (Exception e) {
            MailUtils.sendMail(e.getMessage(), SENDER_EMAIL, "Error", "nkhoang.it@gmail.com");
        }
    }

	@RequestMapping(value = "/triggerRemoveWordItemDuplicates")
	public void triggerRemoveWordItemDuplicates(
		HttpServletResponse response, @RequestParam int startingIndex, @RequestParam int size) {
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(
			TaskOptions.Builder.withUrl("/vocabulary/removeWordItemDuplicates.html").param("index", startingIndex + "")
			                   .param("size", size + "").method(TaskOptions.Method.GET));

		try {
			response.getWriter().write("success");
		}
		catch (Exception e) {
			LOG.error("Error:", e);
		}
	}

	@RequestMapping(value = "/testMail")
	public void testMail(HttpServletResponse response) {
		String message = SENDER_EMAIL;
		try {
			MailUtils.sendMail(
				"<h1>Hello from Test</h1>", SENDER_EMAIL, "Hello from Test", "nkhoang.it@gmail.com");

		}
		catch (Exception e) {
			message = e.toString() + " " + e.getMessage();
		}

		try {
			response.getWriter().write(message);
		}
		catch (Exception e) {
			LOG.error("Error: ", e);
		}

	}


	/**
	 * This method will help to remove duplicate wordItem entity in the DS.
	 *
	 * @param index    the index to start processing.
	 * @param size     specify how many entities will be processed.
	 * @param request  the {@link HttpServletRequest} object.
	 * @param response the {@link HttpServletResponse} object.
	 */
	@RequestMapping(value = "/removeWordItemDuplicates")
	public void removeWordItemDuplicates(
		@RequestParam int index, @RequestParam int size, HttpServletRequest request, HttpServletResponse response) {
		List<WordItem> removeList = new ArrayList<WordItem>();
		List<WordItem> wordItems = wordItemDao.getAllInRangeWithOrder(index, size, "word", "asc");
		Map<String, Object> mailData = new HashMap<String, Object>();
		String messageBody = "";
		if (CollectionUtils.isNotEmpty(wordItems)) {
			int i = 0;
			do {
				if (i == wordItems.size() - 1) {
					i = wordItems.size();
				}
				for (int j = i + 1; j < wordItems.size(); j++) {
					if (StringUtils.equalsIgnoreCase(wordItems.get(i).getWord(), wordItems.get(j).getWord())) {
						removeList.add(wordItems.get(j));
						if (j == wordItems.size() - 1) {
							i = wordItems.size();
						}
					} else {
						i = j;
						break;
					}
				}
			} while (i < wordItems.size());
			LOG.info("Filtering completed. Start deleting...");
			if (CollectionUtils.isNotEmpty(removeList)) {
				// starting to delete;
				for (WordItem w : removeList) {
					wordItemDao.delete(w.getId());
				}
				// create mail content.
				mailData.put("wordList", removeList);
			}
		}
		mailData.put("sampleList", wordItems.subList(0, 100));
		messageBody = MailUtils.buildMail(request.getSession().getServletContext(), "remove_duplicate.ftl", mailData);
		MailUtils.sendMail(messageBody, SENDER_EMAIL, "Remove Duplicates report", "nkhoang.it@gmail.com");
		try {
			response.getWriter().write("success");
		}
		catch (Exception e) {
			LOG.error("Error:", e);
		}
	}


	/**
	 * Render vocabulary builder page. Vocabulary builder will help user to build their own
	 * iVocabulary file based on their selections.
	 *
	 * @return
	 */
	@RequestMapping(value = "/" + "vocabularyBuilder", method = RequestMethod.GET)
	public ModelAndView renderIVocabularyBuilder(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("vocabulary/vocabularyBuilder");

		return mav;
	}


	/**
	 * Construct iVocabulary XML file as the response to the request.
	 *
	 * @param dateTime     the date time that will be displayed in the XML output.
	 * @param chapterTitle the chapter title.
	 * @param pageTitle    the page title.
	 * @param ids          user selected word ids.
	 * @param exampleIds   user selected examples.
	 * @param meaningIds   user selected meanings.
	 * @param response     the {@link HttpServletResponse} object to write the output XML.
	 */
	@RequestMapping(value = "/" + "constructIVocabulary", method = RequestMethod.GET)
	public void renderIVocabulary(
		@RequestParam("dateTime") String dateTime, @RequestParam("chapterTitle") String chapterTitle,
		@RequestParam("pageTitle") String pageTitle, @RequestParam("ids") String ids,
		@RequestParam("exampleIds") String[] exampleIds, @RequestParam("meaningIds") String[] meaningIds,
		HttpServletResponse response, HttpServletRequest request) {

		// I don't know why we don't use array or list here.
		// TODO: check the ids if we can change it to an array.
		List<Long> filteredIds = new ArrayList<Long>();
		String[] idStrs = ids.split(",");
		for (String id : idStrs) {
			filteredIds.add(Long.parseLong(id.trim()));
		}

		// get word list based on what selected.
		List<Word> wordList = vocabularyService.getAllWordsById(filteredIds);

		Map<String, List<Integer>> filteredMeaningExampleIdMap = new HashMap<String, List<Integer>>();
		// build meaning - example map.
		for (String example : exampleIds) {
			// split to get meaning and example index.
			String[] data = example.split("-");
			if (filteredMeaningExampleIdMap.get(data[0]) == null) {
				filteredMeaningExampleIdMap.put(data[0], new ArrayList<Integer>());
			}
			filteredMeaningExampleIdMap.get(data[0]).add(Integer.parseInt(data[1]));
		}

		List<Long> filteredMeaningIds = new ArrayList<Long>();
		// build filtered meaning ids.
		for (String s : meaningIds) {
			filteredMeaningIds.add(Long.parseLong(s));
		}

		// filter meanings base on id.
		for (Word w : wordList) {
			optimizeStructure(w, filteredMeaningIds, filteredMeaningExampleIdMap);
		}
		try {
			DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.S");
			String currentDate = dateFormatter.format(Calendar.getInstance().getTime());
			dateFormatter = new SimpleDateFormat("dd/MM/yy");
			String simpleCurrentDate = dateFormatter.format(Calendar.getInstance().getTime());
			IVocabularyUtils.buildIVocabulary(
				"English", "English", "Hoang Nguyen Khanh",
				"Created by http://mini-vocabulary.appspot.com/vocabulary/vocabularyBuilder.html", currentDate,
				"Vocabulary List - " + simpleCurrentDate, wordList.size() + "", pageTitle, chapterTitle,
				request.getSession().getServletContext(), wordList, response.getWriter());
		}
		catch (IOException ioe) {
			LOG.error(
				"The template file may not find in the target folder. Please check the error message for more details.",
				ioe);
		}
		catch (TemplateException tple) {
			LOG.error("There are errors while templating. Please check the error message for more details.", tple);
		}
	}

	/**
	 * Optimize word structure by removing meanings and examples which
	 * are not selected in the {@code filteredMeaningIds} and the {@code filteredExampledIds}
	 *
	 * @param filteredMeaningIds        filtered meaning ids.
	 * @param filteredMeaningExampleIds filtered example ids by meaning id.
	 */
	public void optimizeStructure(
		Word w, List<Long> filteredMeaningIds, Map<String, List<Integer>> filteredMeaningExampleIds) {
		// filter the meaning by the meaning id.
		CollectionUtils.filter(w.getMeanings(), new MeaningPredicate(filteredMeaningIds));
		// filter the example by the example index.
		if (CollectionUtils.isNotEmpty(w.getMeanings())) {
			for (Meaning m : w.getMeanings()) {
				List<String> examples = m.getExamples();
				List<String> filteredExamples = new ArrayList<String>();
				if (CollectionUtils.isNotEmpty(filteredMeaningExampleIds.get(m.getId().toString()))) {
					for (Integer i : filteredMeaningExampleIds.get(m.getId().toString())) {
						filteredExamples.add(examples.get(i));
					}
				}
				// now we can retain the needed one.
				examples.retainAll(filteredExamples);
			}
		}
	}

	/**
	 * Daily Lookup will start every 10 hour from 00:01 to 20:01.
	 * <p/>
	 * preserved.
	 */
	@RequestMapping(value = "/startDailyLookup", method = RequestMethod.GET)
	public void startDailyLookup(HttpServletResponse response) {
		// check the word stat entity to see if this is the first time.
		List<WordItemStat> wordStats = wordItemStatDao.getAllInRange(0, 1);
		int index = 0;
		WordItemStat wordStat = null;
		if (CollectionUtils.isNotEmpty(wordStats)) {
			wordStat = wordStats.get(0);
			index = wordStat.getIndex();
		} else {
			WordItemStat newWordState = new WordItemStat();
			newWordState.setIndex(0);
			newWordState.setFailedCount(0);
			newWordState.setSuccessCount(0);
			newWordState.setProcessTime(0);

			wordStat = wordItemStatDao.save(newWordState);
		}

		LOG.info(wordStat.getId() + "");
		// starting the queue
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(
			TaskOptions.Builder.withUrl("/vocabulary/dailyLookup.html").param("id", wordStat.getId() + "")
			                   .param("index", index + "").param("maxIndex", (index + TOTAL_AMOUNT_LOOKUP_WORD) + "")
			                   .param("failedCount", wordStat.getFailedCount() + "").param("processingTime", 0 + "")
			                   .param("successCount", wordStat.getSuccessCount() + "").method(TaskOptions.Method.GET));

		try {
			response.getWriter().write("success");
		}
		catch (Exception e) {
			LOG.error("Error: ", e);
		}
	}

	@RequestMapping(value = "/dailyLookup", method = RequestMethod.GET)
	public void dailyLookup(
		@RequestParam int index, @RequestParam int maxIndex, @RequestParam int failedCount,
		@RequestParam int successCount, @RequestParam int processingTime, @RequestParam int id,
		HttpServletResponse response) {
		// continue only this condition is met.
		if (index < maxIndex) {
			int nextIndex = index + 20;
			if (nextIndex >= maxIndex) {
				nextIndex = maxIndex;
			}
			Long start = System.currentTimeMillis();
			List<WordItem> wis = wordItemDao.getAllInRange(index, nextIndex);
			for (WordItem wi : wis) {
				try {
					if (StringUtils.isNotEmpty(wi.getWord())) {
						Word savedWord = vocabularyService.save(wi.getWord().toLowerCase());
						// check meanings
						if (savedWord != null && CollectionUtils.isNotEmpty(savedWord.getKindIdList())) {
							for (Long wordId : savedWord.getKindIdList()) {
								if (wordId <= 5) {
									wi.setHaveVietnamese("Y");
								}
								if (wordId > 5) {
									wi.setHaveEnglish("Y");
								}
							}
							wordItemDao.update(wi);
							++successCount;
						}
					}
				}
				catch (IOException ioe) {
					// set both wordItem 'haveEnglish' and 'haveVietnamese' to 'F' = 'Failed'
					wi.setHaveEnglish(WORD_ITEM_STATUS_FAILED);
					wi.setHaveVietnamese(WORD_ITEM_STATUS_FAILED);

					wordItemDao.update(wi);

					++failedCount;
				}
			}

			int processTime = (int) (System.currentTimeMillis() - start) / 1000;

			if (index + 20 > maxIndex) {
				WordItemStat wordItemStat = wordItemStatDao.get(Long.parseLong(id + ""));
				wordItemStat.setFailedCount(failedCount);
				wordItemStat.setSuccessCount(successCount);
				wordItemStat.setIndex(maxIndex);
				wordItemStat.setProcessTime(processingTime + processTime);
				// should stop here
				Map<String, Object> mailData = new HashMap<String, Object>();
				mailData.put("wordStat", wordItemStat);
			} else {
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(
					TaskOptions.Builder.withUrl("/vocabulary/dailyLookup.html").param("id", id + "")
					                   .param("index", (index + 20) + "").param("maxIndex", maxIndex + "")
					                   .param("failedCount", failedCount + "")
					                   .param("processingTime", (processingTime + processTime) + "")
					                   .param("successCount", successCount + "").method(TaskOptions.Method.GET));
			}
		}
		try {
			response.getWriter().write("success");
		}
		catch (Exception e) {
			LOG.error("Error: ", e);
		}
	}

	/**
	 * Load word items from file located in WEB-INF/vocabulary folder.
	 *
	 * @param response      the {@link HttpServletResponse} object passed in by Spring.
	 * @param request       the {@link HttpServletRequest} object passed in by Spring.
	 * @param startingIndex the starting index in the file, it can be considered as the row number to start processing with.
	 * @param size          specify how many word items will be processed.
	 */
	@RequestMapping(value = "/loadWordItemsFromFile", method = RequestMethod.GET)
	public void loadWordItemsFromFile(
		HttpServletResponse response, HttpServletRequest request, @RequestParam int startingIndex,
		@RequestParam int size, @RequestParam int maxIndex) {
		LOG.info(
			String.format(
				"Load word items from file with starting index [%d] - [%d]", startingIndex, startingIndex + size));
		// waste the resource because we're going to use this function only one.
		List<String> wordList = FileUtils.readWordsFromFile(
			request.getSession().getServletContext().getRealPath("WEB-INF/vocabulary/word-list.txt"));
		boolean shouldNotContinue = false;
		if (size == 0 || (startingIndex + size > maxIndex)) {
			shouldNotContinue = true;
		}
		if (CollectionUtils.isNotEmpty(wordList)) {
			// this flag is used to detec whether we need to rollback everything from this "task" before retrying it.
			boolean shouldRollback = false;
			List<Long> savedIds = new ArrayList<Long>();
			int endingIndex = startingIndex + size;
			if (endingIndex > wordList.size()) {
				endingIndex = wordList.size();
				shouldNotContinue = true;
			}
			for (int index = startingIndex; index < (endingIndex); index++) {

				WordItem wi = new WordItem();
				wi.setWord(wordList.get(index));

				try {
					WordItem savedWord = wordItemDao.save(wi);
					savedIds.add(savedWord.getId());
				}
				catch (Exception e) {
					shouldRollback = true;
					LOG.error("Could not save word item :" + wordList.get(index), e);
				}
			}
			if (shouldRollback && CollectionUtils.isNotEmpty(savedIds)) {
				LOG.info(
					String.format(
						"Rolling back from task. Removing word time with id from [%d] to [%d]...", startingIndex,
						startingIndex + size));
				List<Long> failedIds = new ArrayList<Long>();
				for (Long id : savedIds) {
					boolean result = wordItemDao.delete(id);
					if (!result) {
						failedIds.add(id);
					}
				}
				if (CollectionUtils.isNotEmpty(failedIds)) {
					shouldNotContinue = true;
					LOG.info("These following word item ids were failed to delete: " + failedIds.toArray().toString());
				}
			}

			if (!shouldNotContinue) {
				Queue queue = QueueFactory.getDefaultQueue();
				queue.add(
					TaskOptions.Builder.withUrl("/vocabulary/loadWordItemsFromFile.html")
					                   .param("startingIndex", (startingIndex + size) + "").param("size", size + "")
					                   .param("maxIndex", maxIndex + "").method(TaskOptions.Method.GET));
			}
		}
		try {
			response.getWriter().write("Success");
		}
		catch (Exception e) {
			LOG.error("Error occurred.", e);
		}
	}

	/**
	 * This is the trigger to load all word from file loacted in 'WEB-INF/vocabulary' folder.
	 * <p/>
	 * This functionality is very restricted because it will take 80% of application capability.
	 *
	 * @param response      the {@link HttpServletResponse} object.
	 * @param startingIndex the index (in file) to start processing.
	 * @param size          specify the number of rows (word) to be processed.
	 */
	@RequestMapping(value = "/triggerLoadWordItemsFromFile", method = RequestMethod.GET)
	public void triggerLoadWordItemsFromFile(
		HttpServletResponse response, @RequestParam int startingIndex, @RequestParam int size,
		@RequestParam int maxIndex) {
		Queue queue = QueueFactory.getDefaultQueue();
		LOG.info("Starting load word items from file queue...");
		queue.add(
			TaskOptions.Builder.withUrl("/vocabulary/loadWordItemsFromFile.html")
			                   .param("startingIndex", startingIndex + "").param("size", size + "")
			                   .param("maxIndex", maxIndex + "").method(TaskOptions.Method.GET));
		try {
			response.getWriter().write("Your request is served. Please wait....");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	@RequestMapping(value = "/" + ViewConstant.VOCABULARY_UPDATE_VIA_GD_REQUEST, method = RequestMethod.GET)
	public ModelAndView saveWordsFromSpreadsheet(
		@RequestParam("spreadsheetName") String fileName, @RequestParam("worksheetName") String worksheetName,
		@RequestParam("row") int rowIndex, @RequestParam("col") int columnIndex, @RequestParam("size") int size) {
		String message = "";
		try {
			postMessage(String.format("Connecting to Google Spreadsheet: %s->%s", fileName, worksheetName));
			List<String> data = spreadsheetService
				.querySpreadsheet(fileName, worksheetName, rowIndex, columnIndex, size);

			postMessage(String.format("Number of words found in Google Spreadsheet: %s", data.size()));

			List<String> excludeAttrs = Arrays.asList(Word.SKIP_FIELDS);
			Gson gson = new GsonBuilder().setExclusionStrategies(new GSONStrategy(excludeAttrs)).create();

			for (String w : data) {
				Word word = gson.fromJson(w, Word.class);
				vocabularyService.save(word);
			}

			postMessage("Update finished!!!.");
			postMessage(String.format("Total words: %s , total saved: %s", size, data.size()));
		}
		catch (Exception e) {
			message = "An error occurred when trying to connect to Google Spreadsheet. Parameters invalid!!";
			LOG.error(message, e);
		}

		ModelAndView mav = new ModelAndView();
		mav.setView(new JSONView());
		mav.addObject("message", message);
		return mav;
	}

	private void postMessage(String s) {
		messageDao.save(new Message(Message.VOCABULARY_CATEGORY, s));
	}

	/**
	 * Contruct XML complied with iVocabulary XVOC format.
	 *
	 * @param allWords      list of words.
	 * @param size          number of words contained in a Page.
	 * @param startingIndex offset of the list.
	 * @param requestSize   number of words will be processed.
	 *
	 * @return xml string.
	 */

	private String constructXMLBlockContent(List<Word> allWords, int size, int startingIndex, int requestSize) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
		formatter.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));

		Date currentDate = GregorianCalendar.getInstance().getTime();
		try {
			currentDate = formatter.parse("20/11/2010");
			int incrementDay = 0;
			if (startingIndex != 0) {
				incrementDay = (startingIndex + requestSize) / size;
			} else if (startingIndex == 0) {
				incrementDay = requestSize / size;
			}

			currentDate = DateUtils.addDays(currentDate, incrementDay + 1);
		}
		catch (Exception e) {
			LOG.info("Use current date.");

		}

		formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
		formatter.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));


		StringBuilder xmlBuilder = new StringBuilder();

		xmlBuilder.append("<Chapter title='" + startingIndex + " - " + (requestSize + startingIndex) + "' >");
		int counter = 1;
		int dayCounter = -1;
		for (Word w : allWords) {
			if (counter == 1 || counter > size) {
				counter = 1; // reset counter.
				String dateStr = formatter.format(DateUtils.addDays(currentDate, dayCounter - 1));
				dayCounter++;

				xmlBuilder.append("<Page title='" + dateStr + "' >");
			}

			StringBuilder comment = new StringBuilder();
			StringBuilder targetWords = new StringBuilder();

			String pron = w.getPron() == null ? "" : w.getPron(); // append to comment. remove null

			List<Meaning> lmn = w.getMeaning(w.getKindidmap().get("noun")); // meaning for noun.
			if (lmn != null && lmn.size() > 0) {
				Meaning m = lmn.get(0);

				String content = m.getContent();

				targetWords.append("(n) " + content + "\n");
				if (m.getExamples() != null && m.getExamples().size() > 0) {
					comment.append(" (n) " + m.getExamples().get(0));
				}
			}

			List<Meaning> lmv = w.getMeaning(w.getKindidmap().get("verb")); // meaning for verb.
			if (lmv != null && lmv.size() > 0) {
				Meaning m = lmv.get(0);

				String content = m.getContent();

				targetWords.append("(v) " + content + "\n");
				if (m.getExamples() != null && m.getExamples().size() > 0) {
					comment.append(" (v) " + m.getExamples().get(0));
				}

			}

			List<Meaning> lmadj = w.getMeaning(w.getKindidmap().get("adjective")); // meaning for adjective
			if (lmadj != null && lmadj.size() > 0) {
				Meaning m = lmadj.get(0);

				String content = m.getContent();

				targetWords.append("(adj) " + content + "\n");
				if (m.getExamples() != null && m.getExamples().size() > 0) {
					comment.append(" (adj) " + m.getExamples().get(0));
				}

			}

			List<Meaning> lmadv = w.getMeaning(w.getKindidmap().get("adverb")); // meaning for adv.
			if (lmadv != null && lmadv.size() > 0) {
				Meaning m = lmadv.get(0);

				String content = m.getContent();

				targetWords.append("(adv) " + content + "\n");
				if (m.getExamples() != null && m.getExamples().size() > 0) {
					comment.append(" (adv) " + m.getExamples().get(0));
				}
			}

			if (StringUtils.isNotEmpty(comment.toString())) {
				xmlBuilder.append(
					"<Word sourceWord=\"" + w.getDescription() + " " + pron + "\" targetWord=\"" +
					targetWords.toString() + "\">");
				xmlBuilder.append("<Comment>" + comment.toString() + "</Comment>");
				xmlBuilder.append("</Word>");
			}

			if (counter + (size * dayCounter) == allWords.size()) {
				xmlBuilder.append("</Page>"); // append ending tag.
			} else {

				counter++;
				if (counter > size) {
					xmlBuilder.append("</Page>"); // append ending tag.
				}
			}

		}

		xmlBuilder.append("</Chapter>");

		return xmlBuilder.toString();
	}


	/**
	 * Build iVocabulary file right from an URL.
	 *
	 * @param startingIndexStr offset to be started with.
	 * @param sizeStr          number of words will be processed.
	 * @param pageSizeStr      number of words in a Page.
	 * @param response         HttpServletResponse.
	 *
	 * @return xml view.
	 */
	@RequestMapping(value = "/" + ViewConstant.VOCABULARY_I_VOCABULARY_REQUEST, method = RequestMethod.GET)
	public ModelAndView buildIVocabularyFile(
		@RequestParam("startingIndex") String startingIndexStr, @RequestParam("pageSize") String pageSizeStr,
		@RequestParam("size") String sizeStr, HttpServletResponse response) {

		int startingIndex = 0, size = 100, pageSize = 20; // default size = 100;

		if (StringUtils.isEmpty(startingIndexStr)) {
			try {
				response.setContentType("text/html");
				response.getWriter().write("Check your param. startingIndex must not be ommitted.");
			}
			catch (Exception e) {
				LOG.error("Could not print output.");
			}
			return null;
		} else {
			startingIndex = Integer.parseInt(startingIndexStr);
		}

		if (StringUtils.isNotEmpty(sizeStr)) {
			size = Integer.parseInt(sizeStr);
		}

		if (StringUtils.isNotEmpty(pageSizeStr)) {
			pageSize = Integer.parseInt(pageSizeStr);
		}

		String xmlStr = constructIVocabularyFile(startingIndex, size, pageSize);

		ModelAndView mav = new ModelAndView();
		mav.setView(new XMLView());
		mav.addObject("data", xmlStr);

		return mav;
	}

	/**
	 * Contruct iVocabulary file from database.
	 *
	 * @param startingIndex word offset.
	 * @param size          iVocabulary page size.
	 *
	 * @return xml string.
	 */
	private String constructIVocabularyFile(int startingIndex, int size, int pageSize) {
		List<Word> allWords = vocabularyService.getAllWordsByRange(startingIndex, size);
		String xml = constructXMLBlockContent(allWords, pageSize, startingIndex, size);

		String xmlStr = "";
		try {
			InputStream is = this.getClass().getResourceAsStream("/vocabulary.xml");

			if (is == null) {
				LOG.info("Could not load resources.");
			}

			VTDGen vg = new VTDGen(); // Instantiate VTDGen
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer);
			String theString = writer.toString();
			vg.setDoc(theString.getBytes());

			vg.parse(true);

			XMLModifier xm = new XMLModifier(); //Instantiate XMLModifier
			LOG.info("Starting to parse XML");
			VTDNav vn = vg.getNav();

			xm.bind(vn);

			AutoPilot ap = new AutoPilot(vn);

			ap.selectXPath("/Vocabulary/Root");
			int i = -1;
			while ((i = ap.evalXPath()) != -1) {
				xm.insertAfterHead(xml);
			}

			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			xm.output(bos);

			xmlStr = bos.toString("UTF-8");


		}
		catch (Exception e) {
			LOG.info("Could not parse or update vocabulary.xml file.");
		}
		return xmlStr;
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
			List<Word> words = vocabularyService.getAllWordsById(user.getWordList());
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

	public MessageDao getMessageDao() {
		return messageDao;
	}

	public void setMessageDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}

	public WordItemDao getWordItemDao() {
		return wordItemDao;
	}

	public void setWordItemDao(WordItemDao wordItemDao) {
		this.wordItemDao = wordItemDao;
	}

	public String getSENDER_EMAIL() {
		return SENDER_EMAIL;
	}

	public void setSENDER_EMAIL(String SENDER_EMAIL) {
		this.SENDER_EMAIL = SENDER_EMAIL;
	}

	public WordItemStatDao getWordItemStatDao() {
		return wordItemStatDao;
	}

	public void setWordItemStatDao(WordItemStatDao wordItemStatDao) {
		this.wordItemStatDao = wordItemStatDao;
	}
}


class MeaningPredicate implements Predicate {
	private List<Long> _filteredList = new ArrayList<Long>();

	public MeaningPredicate(List<Long> filteredList) {
		_filteredList = filteredList;
	}

	public boolean evaluate(Object o) {
		Meaning m = (Meaning) o;

		return _filteredList.contains(m.getId());
	}
}
