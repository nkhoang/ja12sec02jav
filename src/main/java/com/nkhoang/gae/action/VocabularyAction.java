package com.nkhoang.gae.action;

import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
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

@Controller
@RequestMapping("/" + ViewConstant.VOCABULARY_NAMESPACE)
public class VocabularyAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(VocabularyAction.class);

    @Autowired
    private SpreadsheetServiceImpl spreadsheetService;
    @Autowired
    private VocabularyService vocabularyService;
    @Autowired
    private UserManager userService;

    @RequestMapping(value = "/" + ViewConstant.VOCABULARY_HOME_REQUEST, method = RequestMethod.GET)
    public String getVocabularyPage() {
        return ViewConstant.VOCABULARY_VIEW;
    }


    /**
     * URL should contains params "index" and "col" to limit the response content from Google Spreadsheet API.
     *
     * @param startingIndex starting index.
     * @param columnIndex   column index.
     * @param response      HttpServletResponse.
     */
    @RequestMapping(value = "/" + ViewConstant.VOCABULARY_UPDATE_REQUEST, method = RequestMethod.GET)
    public void updateWordsFromSpreadSheet(@RequestParam("index") String startingIndex
            , @RequestParam("col") String columnIndex
            , HttpServletResponse response) {
        final long start = System.currentTimeMillis();

        int index = 0;
        int col = 1; // column index starting from 1 not 0.
        // parse starting index.
        if (StringUtils.isNotEmpty(startingIndex)) {
            try {
                index = Integer.parseInt(startingIndex);
            } catch (Exception e) {
                LOGGER.debug("Could not parse request param for update from spreadsheet. Continue with index = " + index);
            }
        }
        // parse column index.
        if (StringUtils.isNotEmpty(columnIndex)) {
            try {
                col = Integer.parseInt(columnIndex);
            } catch (Exception e) {
                LOGGER.debug("Could not parse request param for update from spreadsheet. Continue with index = " + index);
            }
        }
        boolean finished = false;
        while (System.currentTimeMillis() - start < 16384 && !finished) {
            try {
                URL cellfeedURL = new URL("https://spreadsheets.google.com/feeds/cells/peZDHyA4LqhdRYA8Uv_sufw/od6/private/full");
                CellQuery query = new CellQuery(cellfeedURL);
                query.setMinimumCol(col);
                query.setMaximumCol(col);
                CellFeed cellfeed = spreadsheetService.getService().query(query, CellFeed.class);

                List<CellEntry> cells = cellfeed.getEntries();

                if (index == cells.size()) { // check index if it exceed the maximum row
                    col += 1; // maximum row reached.
                    index = 0; // reset index.
                    if (col > cellfeed.getColCount()) { // check column index.
                        LOGGER.info("End of document.");
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

                } catch (IOException ex) {
                    LOGGER.error("Cound not process word: " + cellValue, ex);
                } catch (IllegalArgumentException iae) {
                    index += 1;
                }
                //LOGGER.info("Index: " + index + " Cell " + shortId + ": " + cell.getCell().getValue());
                LOGGER.info(">>>>>>>>>>>>>>>>>>> Posting to Queue with index: [" + index + "] and col [" + col + "]");
                QueueFactory.getDefaultQueue().add(url("/vocabulary/update.html?index=" + index + "&col=" + col).method(TaskOptions.Method.GET));
                finished = true;
            } catch (Exception
                    authex) {
                LOGGER.error("Could not communicate with Google Spreadsheet.", authex);
            }
        }
        response.setContentType("text/html");
        try {

            response.getWriter().write("Being updated. Stay tuned!");
        } catch (IOException ioe) {
            LOGGER.error("Could not write to response.", ioe);
        }

    }

    @RequestMapping(value = "/" + ViewConstant.VOCABULARY_VIEW_ALL_REQUEST, method = RequestMethod.POST)
    public ModelAndView listAll() {
        User user = getUserCredential();

        ModelAndView modelAndView = new ModelAndView();
        if (user != null) {
            Map<String, Object> jsonData = new HashMap<String, Object>();
            List<Word> words = vocabularyService.getAllWordsFromUser(user.getWordList());
            words.addAll(vocabularyService.getAllWords());
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

    @RequestMapping(value = "/" + ViewConstant.VOCABULARY_ADD_WORD_REQUEST, method = RequestMethod.POST)
    public ModelAndView submitWord(@RequestParam String word) {

        ModelAndView modelAndView = new ModelAndView();
        Map<String, Object> jsonData = new HashMap<String, Object>();
        boolean result = false;

        User user = getUserCredential();
        if (user != null && word != null && StringUtils.isNotEmpty(word)) {
            // delete item
            try {
                Word savedWord = vocabularyService.save(word);
                user.getWordList().add(savedWord.getId());
                userService.update(user);
            } catch (IOException ex) {
            }
        }

        jsonData.put("result", result);
        View jsonView = new JSONView();
        modelAndView.setView(jsonView);
        modelAndView.addObject(GSONStrategy.DATA, jsonData);

        return modelAndView;

    }

    public User getUserCredential() {
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
}
