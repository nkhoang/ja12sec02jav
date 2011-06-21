package com.nkhoang.gae.service.impl;

import com.nkhoang.gae.dao.MeaningDao;
import com.nkhoang.gae.dao.MessageDao;
import com.nkhoang.gae.dao.VocabularyDao;
import com.nkhoang.gae.dao.impl.VocabularyDaoImpl;
import com.nkhoang.gae.model.Meaning;
import com.nkhoang.gae.model.Message;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.SpreadsheetService;
import com.nkhoang.gae.service.VocabularyService;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class VocabularyServiceImpl implements VocabularyService {
	private static final Logger LOGGER                = LoggerFactory
		.getLogger(VocabularyDaoImpl.class.getCanonicalName());
	private static final String HTML_DIV              = "div";
	private static final String VN_DICT_CONTENT_CLASS = "result";
	private static final String HTML_ATTR_CLASS       = "class";

	private static final String ENCODING_UTF_8          = "UTF-8";
	private static final String VN_DICT_CLASS_KIND      = "phanloai";
	private static final int    MAX_NUM_VN_WORD_IN_KIND = 3;

	private static final String LONGMAN_DIC_CONTENT_CLASS        = "Entry";
	private static final String LONGMAN_DICT_KIND_CLASS          = "wordclassSelected";
	private static final String LONGMAN_DICT_CLASS_MEANING       = "Sense";
	private static final String LONGMAN_DICT_CLASS_GRAM          = "GRAM";
	private static final String LONGMAN_DICT_CLASS_MEANING_DEF   = "ftdef";
	private static final String LONGMAN_DICT_CLASS_EXAMPLE       = "ftexa";
	private static final String LONGMAN_DICT_CLASS_MEANING_EXTRA = "GramExa";

	private static final String GRAM_MEANING_TYPE  = "gram";
	private static final String COLLO_MEANING_TYPE = "collo";

	private static final String CAMBRIDGE_DICT_CONTENT_CLASS     = "cdo-section";
	private static final String CAMBRIDGE_DICT_URL_TYPE          = "http://dictionary.cambridge.org/search/british/";
	private static final String CAMBRIDGE_DICT_IDIOM_TYPE        = "?type=idiom";
	private static final String CAMBRIDGE_DICT_PHRASAL_VERB_TYPE = "?type=pv&";
	private static final String CAMBRIDGE_DICT_TYPE_QUERY        = "&q=";
	private static final String CAMBRIDGE_DICT_URL               = "http://dictionary.cambridge.org/dictionary/british/";
	private static final String CAMBRIDGE_DICT_CONTENT_CLASS_2nd = "gwblock ";
	private static final String CAMBRIDGE_DICT_KIND_CLASS        = "header";

	public static final int CORE_POOL_SIZE    = 100;
	public static final int MAXIMUM_POOL_SIZE = 100;
	public static final int KEEP_ALIVE_TIME   = 10;

	private MeaningDao    meaningDao;
	private VocabularyDao vocabularyDao;
	private MessageDao    messageDao;


	private com.nkhoang.gae.service.SpreadsheetService _spreadsheetService;

	private static final String LONGMAN_DICTIONARY_URL = "http://www.ldoceonline.com/dictionary/";

	/** Check word existence may need to use HTML class / id. */
	private enum DICTIONARY_TYPE {
		CLASS,
		ID
	}

	/** Get a range of words from database. */
	public List<Word> getAllWordsInRange(int startingIndex, int size) {
		List<Word> words = vocabularyDao.getAllInRange(startingIndex, size);
		List<Word> result = new ArrayList<Word>();
		int lastIndex = startingIndex + size;
		if (lastIndex > words.size()) {
			lastIndex = words.size() - 1;
		}
		for (int i = startingIndex; i < lastIndex; i++) {
			Word w = words.get(i);
			populateWord(w);

			result.add(w);
		}
		return result;
	}

	/**
	 * Just get a range of words only. Not populating meanings and examples.
	 * It will speed up the process of getting a word list.
	 */
	public List<Word> getAllWordsInRangeWithoutMeanings(int startingIndex, int size) {
		List<Word> words = vocabularyDao.getAllInRange(startingIndex, size);
		List<Word> result = new ArrayList<Word>();
		int lastIndex = startingIndex + size;
		if (lastIndex > words.size()) {
			lastIndex = words.size() - 1;
		}
		for (int i = startingIndex; i < lastIndex; i++) {
			Word w = words.get(i);
			w.setCurrentTime(formatDate(w.getTimeStamp()));
			result.add(w);
		}
		return result;

	}

	private String formatDate(Long timeStamp) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
		formatter.setTimeZone(TimeZone.getTimeZone(TimeZone.getTimeZone("GMT-8").getID()));
		String result = formatter.format(new Date(timeStamp));
		return result;
	}

	public Word populateWord(Long id) {
		Word w = vocabularyDao.get(id);
		populateWord(w);
		return w;
	}


	/**
	 * Simply get all words from database without populating it with meanings and examples.
	 * Used to list all available words.
	 */
	public List<Word> getAllWords() {
		return vocabularyDao.getAll();
	}


	public int getWordSize() {
		List<Word> words = vocabularyDao.getAll();
		if (words == null) {
			return 0;
		}
		return words.size();
	}

	public List<Word> getAllWordsFromUser(List<Long> wordIds) {
		List<Word> words = new ArrayList<Word>();
		for (Long id : wordIds) {
			Word word = get(id);
			if (word != null) {
				words.add(word);
			}
		}
		return words;
	}

	/** Populate word with meanings. */
	private Word populateWord(Word w) {
		// populate word by Meaning
		List<Long> meaningIds = w.getMeaningIds();
		for (Long meaningId : meaningIds) {
			Meaning meaning = meaningDao.get(meaningId);
			w.addMeaning(meaning.getKindId(), meaning);
		}
		return w;
	}

	private Word get(Long id) {
		Word word = vocabularyDao.get(id);

		return populateWord(word);
	}

	public List<Word> lookupWords(String spreadsheetName, String worksheetName, int row, int col, int size) {
		List<Word> lookupedWord = new ArrayList<Word>();
		List<String> wordList = new ArrayList<String>();
		try {
			wordList = _spreadsheetService.querySpreadsheet(spreadsheetName, worksheetName, row, col, size);
		}
		catch (Exception sre) {
			LOGGER.error(String.format("Could not query Google Spreadsheet because : %s", sre), sre);
		}
		// create ThreadPoolExecutor
		ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(MAXIMUM_POOL_SIZE);

		ThreadPoolExecutor executor = new ThreadPoolExecutor(
			CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, queue,
			new ThreadPoolExecutor.CallerRunsPolicy());

		int index = 0;
		if (wordList.size() > 0) {
			List<FutureTask<VocabularyTaskResult>> taskList = new ArrayList<FutureTask<VocabularyTaskResult>>();
			do {
				FutureTask<VocabularyTaskResult> task = new FutureTask<VocabularyTaskResult>(
					new VocabularyTaskCallable(wordList.get(index)));
				// add task to queue
				taskList.add(task);
				LOGGER.info(String.format("Looking up word: %s", wordList.get(index)));
				executor.execute(task);
				index++;

			} while (index < wordList.size());

			do {
			} while (executor.getTaskCount() != executor.getCompletedTaskCount());

			for (FutureTask<VocabularyTaskResult> t : taskList) {
				try {
					do {
					} while (!t.isDone());
					VocabularyTaskResult result = t.get();
					if (result.getWord() == null) {
						LOGGER.info(result.getMesssage());
					} else {
						lookupedWord.add(result.getWord());
					}
				}
				catch (Exception e) {
					LOGGER.info(String.format("Thread encountered problem : %s", e));
				}
			}
		}

		return lookupedWord;
	}

	private class VocabularyTaskCallable implements Callable<VocabularyTaskResult> {

		private String _word;

		public VocabularyTaskCallable(String s) {
			_word = s;
		}

		@Override
		public VocabularyTaskResult call() throws Exception {
			VocabularyTaskResult result = new VocabularyTaskResult();

			_word = _word.trim();
			if (StringUtils.isNotBlank(_word)) {
				try {
					Word word = lookupVN(_word);
					lookupENLongman(word);
					lookupPron(word);

					if (word.getMeanings().size() > 0) {
						result.setWord(word);
					} else {
						result.setMesssage(String.format("No meaning found for.", _word));
					}
				}
				catch (Exception e) {
					result.setMesssage(String.format("Could not lookup word [%s] because : %s", _word, e));
				}
			} else {
				result.setMesssage(String.format("word is empty."));
			}

			return result;
		}
	}

	/** Result object represent the data return by vocabulary thread process. */
	private class VocabularyTaskResult {
		private Word   _word;
		private String messsage;

		public VocabularyTaskResult() {

		}

		public VocabularyTaskResult(Word w, String message) {
			_word = w;
			this.messsage = message;
		}

		public Word getWord() {
			return _word;
		}

		public void setWord(Word w) {
			_word = w;
		}

		public String getMesssage() {
			return messsage;
		}

		public void setMesssage(String messsage) {
			this.messsage = messsage;
		}
	}

	public void save(Word word) {
		Word savedWord = vocabularyDao.lookup(word.getDescription());
		if (savedWord != null) {
			messageDao.save(new Message(Message.VOCABULARY_CATEGORY, String.format("[%s] found in DB.", word.getDescription())));
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(String.format("[%s] found in DB.", word.getDescription().toUpperCase()));
			}
		} else {
			word.setTimeStamp(System.currentTimeMillis());
			vocabularyDao.save(word);
			messageDao.save(
				new Message(Message.VOCABULARY_CATEGORY, String.format("[%s] saved.", word.getDescription().toUpperCase())));
		}
	}


	public Word save(String lookupWord) throws IOException, IllegalArgumentException {
		// remove unnescessary chars.
		lookupWord = lookupWord.trim().toLowerCase();
		Word word = vocabularyDao.lookup(lookupWord);
		// first check the current status
		if (word != null) {
			LOGGER.info(">>>>>>>>>>>>>>>>>>> Found :" + lookupWord);
			// reassign word.
			word = populateWord(word);
		} else {
			LOGGER.info("Saving word : " + lookupWord);
			word = null;
			try {
				word = lookupVN(lookupWord);
				lookupENLongman(word);
				lookupPron(word);
			}
			catch (IOException ex) {
				LOGGER.info("Failed to connect to dictionary to lookup word definition.");
				throw ex;
			}
			catch (IllegalArgumentException iae) {
				LOGGER.info("Failed to parse URL with wrong arguments.");
				throw iae;
			}

			if (word != null) {
				// build list of meaning
				for (int i = 0; i < Word.WORD_KINDS.length; i++) {
					List<Meaning> meanings = word.getMeaning(Long.parseLong(i + ""));
					if (meanings != null && meanings.size() > 0) {
						//LOGGER.info("found : " + meanings.size() + " meanings for this word");
						for (Meaning meaning : meanings) {
							// save
							try {
								Meaning savedMeaning = meaningDao.save(meaning);
								word.addMeaningId(savedMeaning.getId());
							}
							catch (Exception e) {
								LOGGER.info("Failed to save meaning to DB.");
								LOGGER.info(meaning.toString());
							}
						}
					} else {
						// log.info(word.getMeanings());
					}
				}
				try {
					word.setTimeStamp(GregorianCalendar.getInstance().getTimeInMillis());
					if (word.getMeanings().size() > 0) {
						vocabularyDao.save(word);
						LOGGER.info("word: " + word.getDescription() + " saved!!!!");
					} else {
						LOGGER.info("Could not find any word's meanings");
					}
				}
				catch (Exception e) {
					LOGGER.info("Could not save word:" + word.toString());
				}
			}
		}
		return word;
	}

	/**
	 * Lookup word using Longman online dictionary.
	 * Update to the current word.
	 */
	public void lookupENLongman(Word w) throws IOException {
		if (LOGGER.isDebugEnabled()) {
			//LOGGER.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> looking up word EN from Longman dictionary: " + word);
		}
		Source source = checkWordExistence(
			LONGMAN_DICTIONARY_URL, w.getDescription().toLowerCase(), LONGMAN_DIC_CONTENT_CLASS, DICTIONARY_TYPE.CLASS);
		// index for the next lookup
		int i = 1;
		// the URL structure of LONGMAN dictionary if a word has more than 2 meanings: .../word_[number].html
		if (source == null) {
			source = checkWordExistence(
				LONGMAN_DICTIONARY_URL, w.getDescription().toLowerCase() + "_" + i, LONGMAN_DIC_CONTENT_CLASS,
				DICTIONARY_TYPE.CLASS);
		}
		while (source != null) {
			// get kind
			String kind = "";
			List<Element> kinds = source.getAllElementsByClass(LONGMAN_DICT_KIND_CLASS);
			if (kinds != null && kinds.size() > 0) {
				kind = kinds.get(0).getTextExtractor().toString().trim();
				if (LOGGER.isDebugEnabled()) {
					//LOGGER.debug("Kind: " + kind);
				}
			}
			if (w.getKindidmap().get(kind) == null) {
				if (LOGGER.isDebugEnabled()) {
					//LOGGER.debug(">>>>>>>>>>>>>>>>>>>>>>>> CRITICAL >>>>>>>>>>>>>>> Kind not found in the map: " + kind);
				}
				return;
			}
			// process meaning
			List<Element> meaningList = source.getAllElementsByClass(LONGMAN_DICT_CLASS_MEANING);
			if (meaningList != null) {
				for (Element meaning : meaningList) {
					Meaning mainM = new Meaning();
					String gramStr = "";
					// process GRAM: [intransitive, transitive]...
					List<Element> grams = meaning.getAllElementsByClass(LONGMAN_DICT_CLASS_GRAM);
					if (grams != null && grams.size() > 0) {
						gramStr = grams.get(0).getTextExtractor().toString();
						if (LOGGER.isDebugEnabled()) {
							//LOGGER.debug("GRAM: " + gramStr);
						}
					}
					// process main meaning
					List<Element> ftdefs = meaning.getAllElements(LONGMAN_DICT_CLASS_MEANING_DEF);
					Element ftdef = null;
					if (ftdefs != null && ftdefs.size() > 0) {
						ftdef = ftdefs.get(0);
						// create this meaning
						mainM = new Meaning(
							gramStr + " " + ftdef.getTextExtractor().toString(), w.getKindidmap().get(kind));
						if (LOGGER.isDebugEnabled()) {
							//LOGGER.debug("Meaning: " + ftdef.getTextExtractor().toString());
						}
					} else {
						if (LOGGER.isDebugEnabled()) {
							//LOGGER.debug("Could not check definition for this word: " + word);
						}
					}
					// process example for this main meaning
					List<Element> ftexas = meaning.getAllElements(LONGMAN_DICT_CLASS_EXAMPLE);
					if (ftexas != null) {
						for (Element ftexa : ftexas) {
							mainM.addExample(ftexa.getTextExtractor().toString());
							if (LOGGER.isDebugEnabled()) {
								//LOGGER.debug("Example: " + ftexa.getTextExtractor().toString());
							}
						}
					}
					// check to make sure content is not blank.
					if (StringUtils.isNotBlank(mainM.getContent())) {
						w.addMeaning(w.getKindidmap().get(kind), mainM);
					}

					// process gram example. Another type of meaning.
					List<Element> gramexas = meaning.getAllElementsByClass(LONGMAN_DICT_CLASS_MEANING_EXTRA);
					if (ftexas != null) {
						for (Element gramexa : gramexas) {
							Meaning mm = processSubExampleLongman(gramexa, "PROPFORM", w.getKindidmap().get(kind));
							mm.setType(GRAM_MEANING_TYPE);
							// make sure content is not blank.
							if (StringUtils.isNotEmpty(mm.getContent())) {
								w.addMeaning(w.getKindidmap().get(kind), mm);
							}
						}
					}

					List<Element> colloexas = meaning.getAllElementsByClass("ColloExa"); // process gram example
					if (ftexas != null) {
						for (Element colloexa : colloexas) {
							Meaning mm = processSubExampleLongman(colloexa, "COLLO", w.getKindidmap().get(kind));
							mm.setType(COLLO_MEANING_TYPE);
							if (StringUtils.isNotEmpty(mm.getContent())) { // make sure content is not blank.
								w.addMeaning(w.getKindidmap().get(kind), mm);
							}
						}
					}
				}
			}
			source = checkWordExistence(
				LONGMAN_DICTIONARY_URL, w.getDescription() + "_" + ++i, LONGMAN_DIC_CONTENT_CLASS,
				DICTIONARY_TYPE.CLASS);
		}
	}


	/** Process Sub example for tag GramExa */
	private Meaning processSubExampleLongman(Element s, String nametag, Long kindId) {
		Meaning m = new Meaning();

		List<Element> grams = s.getAllElementsByClass(nametag);
		if (grams != null && grams.size() > 0) {
			String str = grams.get(0).getTextExtractor().toString();
			m = new Meaning(str, kindId);
			LOGGER.info(nametag + ": " + str);
		}

		// process example for this main meaning
		List<Element> ftexas = s.getAllElements(LONGMAN_DICT_CLASS_EXAMPLE);
		if (ftexas != null) {
			for (Element ftexa : ftexas) {
				m.addExample(ftexa.getTextExtractor().toString());
				LOGGER.info("Example: " + ftexa.getTextExtractor().toString());
			}
		}

		return m;
	}

	/** Look up Pron for a word. */
	public void lookupPron(Word w) throws IOException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("looking up PRON for this word : " + w.getDescription());
		}
		try {
			Source source = checkWordExistence(
				CAMBRIDGE_DICT_URL, w.getDescription().toLowerCase(), CAMBRIDGE_DICT_CONTENT_CLASS,
				DICTIONARY_TYPE.CLASS);
			int i = 1;
			if (source == null) {
				// check it again
				source = checkWordExistence(
					CAMBRIDGE_DICT_URL, w.getDescription().toLowerCase() + "_" + i, CAMBRIDGE_DICT_CONTENT_CLASS,
					DICTIONARY_TYPE.CLASS);
			}
			while (source != null) {
				// process the content
				List<Element> contentEles = source.getAllElementsByClass(CAMBRIDGE_DICT_CONTENT_CLASS_2nd);
				// LOGGER.info("content size = " + contentEles.size());
				if (contentEles != null && contentEles.size() > 0) {
					// should be one
					Element targetContent = contentEles.get(0);
					String kind = "";
					// get kind
					List<Element> headers = targetContent.getAllElementsByClass(CAMBRIDGE_DICT_KIND_CLASS);
					if (headers != null && headers.size() > 0) {
						Element header = headers.get(0);

						List<Element> kinds = header.getAllElementsByClass("pos");
						if (kinds != null && kinds.size() > 0) {
							kind = kinds.get(0).getContent().toString().trim();
						}
					}

					List<Element> additional_headers = targetContent.getAllElementsByClass("additional_header");
					if (additional_headers != null && additional_headers.size() > 0) {
						Element additional_header = additional_headers.get(0);
						List<Element> prons = additional_header.getAllElementsByClass("pron");
						// get Pron
						if (prons != null && prons.size() > 0) {
							String pron = prons.get(0).getTextExtractor().toString();
							LOGGER.info("Pron: " + pron);
							w.setPron(pron);
						}
						// get mp3 file
						List<Element> sounds = additional_header.getAllElementsByClass("sound");
						// may have 2
						if (sounds != null && sounds.size() > 0) {
							Element sound = null;
							if (sounds.size() == 1) {
								sound = sounds.get(0);
							} else if (sounds.size() == 2) {
								sound = sounds.get(1);
							}

							// process
							String soundSource = sound.getAttributeValue("onclick");
							String soundSrc = soundSource.replace("/media", "http://dictionary.cambridge.org/media");
							// LOGGER.info("Found a sound source: " + soundSrc);
							w.setSoundSource(soundSrc);
						}
						break;
					}
				} else {
					if (LOGGER.isDebugEnabled()) {
						//LOGGER.debug("Can not find content.");
					}

					break;
				}
			}
		}
		catch (Exception e) {
			LOGGER.error("Exception", e);
		}
	}


	public Word lookupIdiom(Word aWord) {
		Source source = checkWordExistence(
			CAMBRIDGE_DICT_URL_TYPE + CAMBRIDGE_DICT_IDIOM_TYPE + CAMBRIDGE_DICT_TYPE_QUERY, aWord.getDescription(),
			"search-page-results-container", DICTIONARY_TYPE.ID);

		if (source != null) {
			LOGGER.info(source.toString());
			LOGGER.info("OK");
		}
		return null;
	}

	/**
	 * Check word exsitence.
	 *
	 * @return null or Source object.
	 */
	private Source checkWordExistence(
		String urlLink, String word, String targetIdentifier, DICTIONARY_TYPE targetType) {
		try {
			if (LOGGER.isDebugEnabled()) {
				//LOGGER.debug("Check word existence: " + urlLink + word);
			}
			URL url = new URL(urlLink + word);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			// get inputStream
			InputStream is = connection.getInputStream();
			// create source HTML
			Source source = new Source(is);

			boolean wordFound = true;
			switch (targetType) {
				case CLASS:
					List<Element> contentEles = source.getAllElementsByClass(targetIdentifier);
					if (contentEles == null || contentEles.size() == 0) {
						wordFound = false;
					}
					break;
				case ID:
					Element content = source.getElementById(targetIdentifier);
					if (content == null) {
						wordFound = false;
					}
					break;
			}


			if (!wordFound) {
				return null;
			} else {
				if (StringUtils.equals(CAMBRIDGE_DICT_CONTENT_CLASS, targetIdentifier)) {
					// process the coxntent
					List<Element> contents = source.getAllElementsByClass(CAMBRIDGE_DICT_CONTENT_CLASS_2nd);
					if (contents != null && contents.size() > 0) {
						return source;
					}
					return null;
				} else {
					return source;
				}
			}
		}
		catch (Exception e) {
			LOGGER.error("Error fetching word using URL: " + urlLink + word);
			return null;
		}
	}


	/** Look up VN definitions. */
	public Word lookupVN(String word) throws IOException, IllegalArgumentException {
		// LOGGER.info("Looking up word VN meaning: " + word);

		Word aWord = null;
		// lookup using simple layout: layout for mobile.
		URL url = new URL("http://m.vdict.com/?word=" + word + "&dict=1&searchaction=Lookup");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		InputStream is = connection.getInputStream();

		Source source = new Source(is);
		List<Element> contentEles = source.getAllElementsByClass(VN_DICT_CONTENT_CLASS);
		if (contentEles == null || contentEles.size() == 0) {
			// Source ok but no content found => word may not exists.
			return null;
		}

		Element targetContent = contentEles.get(0);
		// construct word obj.
		aWord = new Word();
		// set description.
		aWord.setDescription(word);
		// starting to update Meanings.
		Meaning meaning = new Meaning();
		// starting with " kind "
		String kind = "";
		List<Element> eles = targetContent.getChildElements();
		for (Element ele : eles) {
			if (ele.getName().equals(HTML_DIV)) {
				kind = "";
			}
			if (ele.getAttributeValue(HTML_ATTR_CLASS) != null &&
			    ele.getAttributeValue(HTML_ATTR_CLASS).equals(VN_DICT_CLASS_KIND)) {
				// use TextExtractor to trim unwanted content.
				kind = ele.getTextExtractor().toString().trim();
				if (kind != null) {
					// may be a compond of something like: danh tu, ngoai dong tu
					if (kind.contains(",")) {
						// just get the first one. May be there are some more exceptional cases in the future.
						kind = kind.split(",")[0];
					}
					String[] words = kind.split(" ");
					kind = "";
					// maximum length = " ngoai dong tu ". Composed by 3 word.
					int limit = words.length > MAX_NUM_VN_WORD_IN_KIND ? MAX_NUM_VN_WORD_IN_KIND : words.length;
					for (int i = 0; i < limit; i++) {
						kind += words[i] + " ";
					}
					kind = kind.trim();

					if (LOGGER.isDebugEnabled()) {
						//LOGGER.debug("Kind : " + kind);
						//LOGGER.debug(Arrays.toString(kind.getBytes(ENCODING_UTF_8)));
					}
				}
			} else if (StringUtils.equals(ele.getName(), "ul") && StringUtils.isNotEmpty(kind)) {
				String className = ele.getAttributeValue(HTML_ATTR_CLASS);
				if (StringUtils.isNotBlank(className) && StringUtils.equals(className, "list1")) {
					List<Element> meaningLis = ele.getChildElements();
					for (Element meaningLi : meaningLis) {
						if (StringUtils.equals(meaningLi.getName(), "li")) {
							List<Element> liContent = meaningLi.getChildElements();
							for (Element content : liContent) {
								if (StringUtils.equals(content.getName(), "b")) {
									String contentRaw = content.getContent().toString();
									meaning = new Meaning(contentRaw, aWord.getKindidmap().get(kind));
									if (LOGGER.isDebugEnabled()) {
										//LOGGER.debug("content : " + contentRaw);
									}
								} else if (StringUtils.equals(content.getName(), "ul") &&
								           StringUtils.isNotEmpty(meaning.getContent())) {
									// should not store any meanings if content is null or blank.
									String example = content.getChildElements().get(0).getChildElements().get(0)
									                        .getContent().toString();
									if (StringUtils.isNotBlank(example)) {
										meaning.addExample(example);
									}
									if (LOGGER.isDebugEnabled()) {
										//LOGGER.debug("Example: " + example);
									}
								}
							}
						}
						Long kindId = aWord.getKindidmap().get(kind);
						if (kindId == null) {
							LOGGER.info(">>>>>>>>>>>>>>>>>> CRITICAL >>>>>>>>>>>>>>>>>>>>> Null for kind: " + kind);
						}
						if (meaning != null && StringUtils.isNotBlank(meaning.getContent()) && kindId != null) {
							aWord.addMeaning(kindId, meaning);
						}
					}
				}
			}
		}
		return aWord;
	}

	public MeaningDao getMeaningDao() {
		return meaningDao;
	}

	public void setMeaningDao(MeaningDao meaningDao) {
		this.meaningDao = meaningDao;
	}

	public VocabularyDao getVocabularyDao() {
		return vocabularyDao;
	}

	public void setVocabularyDao(VocabularyDao vocabularyDao) {
		this.vocabularyDao = vocabularyDao;
	}

	public SpreadsheetService getSpreadsheetService() {
		return _spreadsheetService;
	}

	public void setSpreadsheetService(SpreadsheetService spreadsheetService) {
		_spreadsheetService = spreadsheetService;
	}

	public void setMessageDao(MessageDao messageDao) {
		this.messageDao = messageDao;
	}
}

