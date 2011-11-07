package com.nkhoang.gae.action;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.nkhoang.gae.dao.WordItemDao;
import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.manager.UserManager;
import com.nkhoang.gae.model.*;
import com.nkhoang.gae.service.VocabularyService;
import com.nkhoang.gae.utils.FileUtils;
import com.nkhoang.gae.utils.WebUtils;
import com.nkhoang.gae.view.JSONView;
import com.nkhoang.gae.view.constant.ViewConstant;
import com.nkhoang.gae.vocabulary.IVocabularyUtils;
import freemarker.template.TemplateException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
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
   private WordItemDao wordItemDao;

   @Autowired
   private UserManager userService;

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


   @RequestMapping(value = "/lookupWordsTask")
   public void lookupWordsTask(@RequestParam int size, HttpServletRequest request) {
      // now get the wordItem.
      List<WordItem> wordItems = wordItemDao.getAllInRange(0, size);
      List<String> foundList = new ArrayList<String>();
      List<String> notFoundList = new ArrayList<String>();
      if (CollectionUtils.isNotEmpty(wordItems)) {
         for (WordItem wi : wordItems) {
            try {
               Map<String, Word> foundWords = vocabularyService.lookup(wi.getWord());
               if (MapUtils.isNotEmpty(foundWords)) {
                  foundList.add(wi.getWord());
               } else {
                  notFoundList.add(wi.getWord());
               }
               wordItemDao.delete(wi.getId());
            } catch (Exception ex) {

            }
         }

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
    * This method will help to remove duplicate wordItem entities in the DS.
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
      // check sample list size
      int sublistSize = 100;
      if (wordItems.size() < 100) {
         sublistSize = wordItems.size();
      }
      mailData.put("sampleList", wordItems.subList(0, sublistSize));
      messageBody = WebUtils.buildMail(request.getSession().getServletContext(), "remove_duplicate.ftl", mailData);
      WebUtils.sendMail(messageBody, mailSender, "Remove Duplicates report", "nkhoang.it@gmail.com");
      try {
         response.getWriter().write("success");
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
      List<Word> wordList = vocabularyService.getAllWordsById(filteredIds, true);

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
            request.getSession().getServletContext().getRealPath("WEB-INF/vocabulary/allWords.txt"));
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
            } catch (Exception e) {
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
         } else {
            WebUtils.sendMail(String.format("Hi Boss, <br /> <p>I have completed loading all words from file <b>fullList.txt</b>. Please help me check it again.</p>. <br /><br />. Regards,")
                  , mailSender, "Load All Words from File Finished.", "nkhoang.it@gmail.com");
         }
      }
      try {
         response.getWriter().write("Success");
      } catch (Exception e) {
         LOG.error("Error occurred.", e);
      }
   }

   /**
    * This is the trigger to load all word from file located in 'WEB-INF/vocabulary' folder.
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
      } catch (Exception e) {
         e.printStackTrace();
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
    * Get wordkind list.
    *
    * @return the wordkind list in JSON format.
    */
   @RequestMapping(value = "/wordKind", method = RequestMethod.POST)
   public ModelAndView getWordKind() {
      ModelAndView modelAndView = new ModelAndView();
      View jsonView = new JSONView();
      modelAndView.setView(jsonView);
      Map<String, Object> jsonData = new HashMap<String, Object>();
      jsonData.put("wordKind", Word.WORD_KINDS);
      modelAndView.addObject(GSONStrategy.DATA, jsonData);

      return modelAndView;
   }

   /**
    * Save and lookup word with meanings and examples
    */
   @RequestMapping(value = "/lookup", method = RequestMethod.GET)
   public ModelAndView lookup(@RequestParam(value = "word", required = false, defaultValue = "") String requestWord) {
      Map<String, Word> wordMap = vocabularyService.lookup(requestWord);
      ModelAndView mav = new ModelAndView();
      Map<String, Object> jsonData = new HashMap<String, Object>();
      jsonData.put("data", wordMap);

      View jsonView = new JSONView();
      mav.setView(jsonView);

      List<String> attrs = new ArrayList<String>();
      attrs.addAll(Arrays.asList(Word.SKIP_FIELDS));
      mav.addObject(GSONStrategy.EXCLUDE_ATTRIBUTES, attrs);
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

   public WordItemDao getWordItemDao() {
      return wordItemDao;
   }

   public void setWordItemDao(WordItemDao wordItemDao) {
      this.wordItemDao = wordItemDao;
   }

   public String getMailSender() {
      return mailSender;
   }

   public void setMailSender(String mailSender) {
      this.mailSender = mailSender;
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
