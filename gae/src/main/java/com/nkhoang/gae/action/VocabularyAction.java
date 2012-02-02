package com.nkhoang.gae.action;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.manager.UserManager;
import com.nkhoang.gae.model.Meaning;
import com.nkhoang.gae.model.Sense;
import com.nkhoang.gae.model.User;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.ApplicationService;
import com.nkhoang.gae.service.VocabularyService;
import com.nkhoang.gae.service.impl.AppCache;
import com.nkhoang.gae.utils.FileUtils;
import com.nkhoang.gae.utils.WebUtils;
import com.nkhoang.gae.view.JSONView;
import com.nkhoang.gae.view.constant.ViewConstant;
import com.nkhoang.gae.vocabulary.IVocabularyUtils;
import freemarker.template.TemplateException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
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
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("/vocabulary")
public class VocabularyAction {
  private static final Logger LOG = LoggerFactory.getLogger(VocabularyAction.class.getCanonicalName());

  @Autowired
  private VocabularyService vocabularyService;

  @Autowired
  private UserManager userService;
  @Autowired
  private AppCache appCache;
  @Autowired
  private ApplicationService applicationService;
  @Value("#{appConfig.delimiter}")
  private String delimiter;

  /**
   * The mail sender must be registered in appengine admin panel.
   * It is configured in mail.properties file.
   */
  @Value("#{mail.sender}")
  private String mailSender;


  /**
   * Render index page.
   *
   * @return {@link ModelAndView} object
   */
  @RequestMapping(value = "/index", method = RequestMethod.GET)
  public ModelAndView renderVocabularyIndex() {
    ModelAndView mav = new ModelAndView();
    mav.setViewName(ViewConstant.VOCABULARY_INDEX_VIEW);

    return mav;
  }


  /**
   * Trigger task: "Lookup Words".
   *
   * @param response the {@link HttpServletResponse} object.
   * @param size     specify how many word item entities will be processed.
   */
  @RequestMapping(value = "/triggerLookupWords")
  public void triggerLookupWords(
      HttpServletResponse response, @RequestParam int size) {
    if (vocabularyService.checkConfiguredDicts()) {
      Queue queue = QueueFactory.getDefaultQueue();
      queue.add(
          TaskOptions.Builder.withUrl("/vocabulary/lookupWordsTask.html").param("size", size + "")
              .method(TaskOptions.Method.GET));
      try {
        response.getWriter().write("Task: 'Lookup Words Task' started.");
      } catch (Exception e) {
        LOG.error("Error:", e);
      }
    } else {
      LOG.info("No pre configured dictionary to start the task.");
    }
  }


  /**
   * The task behind the scene that handle a lookup word cron job request.
   *
   * @param size    the number of words to process at a time.
   * @param request the HttpServletRequest.
   */
  @RequestMapping(value = "/lookupWordsTask")
  public void lookupWordsTask(@RequestParam int size, HttpServletRequest request) {
    // get word list from appCache first.
    LOG.info("Refresh appCache with full word list.");
    // then refresh it by loading it from file.
    List<String> fullWordItems = FileUtils.readWordsFromFile(
        request.getSession().getServletContext().getRealPath("WEB-INF/vocabulary/fullList.txt"));

    LOG.info("wordList size: " + fullWordItems.size());
    if (CollectionUtils.isEmpty(fullWordItems)) {
      WebUtils.sendMail("Hi Boss, <br/> <p>I could not load words from the file in server. Please check it out.</p>", mailSender, "Daily Lookup Report", "nkhoang.it@gmail.com");
      return;
    }
    // get starting index from DS.
    int wordItemIndex = 0;
    List<String> wordItemIndexes = applicationService.getAppConfig(ViewConstant.WORD_ITEM_INDEX_KEY, delimiter);
    if (CollectionUtils.isNotEmpty(wordItemIndexes)) {
      try {
        wordItemIndex = Integer.valueOf(wordItemIndexes.get(0));
      } catch (NumberFormatException nfe) {

      }
    }
    // get a sub list from wordList.
    int nextIndex = wordItemIndex + size;
    if (nextIndex > fullWordItems.size()) {
      nextIndex = fullWordItems.size();
    }
    List<String> wordItems = fullWordItems.subList(wordItemIndex, wordItemIndex + size);

    List<String> foundList = new ArrayList<String>();
    List<String> notFoundList = new ArrayList<String>();
    if (CollectionUtils.isNotEmpty(wordItems)) {
      for (String w : wordItems) {
        try {
          Map<String, Word> foundWords = vocabularyService.lookup(w);
          if (MapUtils.isNotEmpty(foundWords)) {
            foundList.add(w);
          } else {
            notFoundList.add(w);
          }
        } catch (Exception ex) {

        }
      }

      applicationService.saveAppConfig(ViewConstant.WORD_ITEM_INDEX_KEY, (wordItemIndex + size) + "");

      Map<String, Object> mailData = new HashMap<String, Object>();
      mailData.put("totalWords", wordItems.size() + "");
      mailData.put("totalFailed", notFoundList.size() + "");
      mailData.put("totalSuccess", foundList.size() + "");
      mailData.put("foundList", foundList);
      mailData.put("notFoundList", notFoundList);

      String messageBody = WebUtils.buildMail(request.getSession().getServletContext(), "daily_word_report.ftl", mailData);
      WebUtils.sendMail(messageBody, mailSender, "Daily Lookup Report", "nkhoang.it@gmail.com");
    }
  }


  /**
   * Trigger task: "Remove Duplicate Word Item".
   *
   * @param response      the {@link HttpServletResponse} object.
   * @param startingIndex the index to start processing.
   * @param size          specify how many word item entities will be processed.
   */
  @RequestMapping(value = "/triggerRemoveWordItemDuplicates")
  public void triggerRemoveWordItemDuplicates(
      HttpServletResponse response, @RequestParam int startingIndex, @RequestParam int size) {
    Queue queue = QueueFactory.getDefaultQueue();
    queue.add(
        TaskOptions.Builder.withUrl("/vocabulary/removeWordItemDuplicates.html").param("index", startingIndex + "")
            .param("size", size + "").method(TaskOptions.Method.GET));
    try {
      response.getWriter().write("Task: 'Remove Duplicate WordItem' started.");
    } catch (Exception e) {
      LOG.error("Error:", e);
    }
  }

  /**
   * Render vocabulary builder page. Vocabulary builder will help users to build their own
   * iVocabulary file based on their selections.
   *
   * @return
   */
  @RequestMapping(value = "/" + "vocabularyBuilder", method = RequestMethod.GET)
  public String renderIVocabularyBuilder() {
    return "vocabulary/vocabularyBuilder";
  }


  @RequestMapping(value = "/" + "vocabularyBuilder2", method = RequestMethod.GET)
  public String renderVocabularyBuilder() {
    return "vocabulary/vocabularyBuilder2";
  }

  /**
   * Construct iVocabulary XML file as the response to the request. Result will be rendered directly to the response.
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
    // TODO: to change this.
    // List<Word> wordList = vocabularyService.getAllWordsById(filteredIds, true);
    List<Word> wordList = null;

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
      optimizeWordStructure(w, filteredMeaningIds, filteredMeaningExampleIdMap);
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
    } catch (IOException ioe) {
      LOG.error(
          "The template file may not find in the target folder. Please check the error message for more details.",
          ioe);
    } catch (TemplateException tple) {
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
  public void optimizeWordStructure(
      Word w, List<Long> filteredMeaningIds, Map<String, List<Integer>> filteredMeaningExampleIds) {
    // filter the meaning by the meaning id.
    CollectionUtils.filter(w.getMeanings(), new MeaningPredicate(filteredMeaningIds));
    // filter the example by the example index.
    if (CollectionUtils.isNotEmpty(w.getMeanings())) {
      for (Sense m : w.getMeanings()) {
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

  @RequestMapping(value = "sendMail")
  public void sendMail(HttpServletResponse response) {
    WebUtils.sendMail("Hello From Hoang,", mailSender, "Remove Duplicates report", "nkhoang.it@gmail.com");
    try {
      response.getWriter().write("success");
    } catch (Exception e) {

    }
  }


  /**
   * Handle the lookup action.
   *
   * @param requestWord the word to lookup.
   * @return the view.
   */
  @RequestMapping(value = "/lookup", method = RequestMethod.GET)
  public ModelAndView lookup(@RequestParam(value = "word", required = false, defaultValue = "") String requestWord) {
    Map<String, Word> wordMap = vocabularyService.lookup(requestWord);
    ModelAndView mav = new ModelAndView();
    Map<String, Object> jsonData = new HashMap<String, Object>();
    jsonData.put("data", wordMap);

    View jsonView = new JSONView();
    mav.setView(jsonView);

    mav.addObject(GSONStrategy.DATA, jsonData);
    return mav;
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


  public String getMailSender() {
    return mailSender;
  }

  public void setMailSender(String mailSender) {
    this.mailSender = mailSender;
  }

  public AppCache getAppCache() {
    return appCache;
  }

  public void setAppCache(AppCache appCache) {
    this.appCache = appCache;
  }

  public ApplicationService getApplicationService() {
    return applicationService;
  }

  public void setApplicationService(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  public String getDelimiter() {
    return delimiter;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
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
