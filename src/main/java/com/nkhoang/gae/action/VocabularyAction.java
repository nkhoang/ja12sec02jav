package com.nkhoang.gae.action;

import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.manager.UserManager;
import com.nkhoang.gae.model.Meaning;
import com.nkhoang.gae.model.User;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.VocabularyService;
import com.nkhoang.gae.service.impl.SpreadsheetServiceImpl;
import com.nkhoang.gae.utils.GoogleDocsUtils;
import com.nkhoang.gae.view.JSONView;
import com.nkhoang.gae.view.XMLView;
import com.nkhoang.gae.view.constant.ViewConstant;
import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XMLModifier;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
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

    private final String APP_NAME = "Chara";
    DocsService docsService = new DocsService(APP_NAME); // Google Document Service.


    /**
     * Render home page for vocabulary.
     *
     * @return view name.
     */
    @RequestMapping(value = "/" + ViewConstant.VOCABULARY_HOME_REQUEST, method = RequestMethod.GET)
    public String renderVocabularyPage() {
        return ViewConstant.VOCABULARY_VIEW;
    }

    /**
     * Export vocabulary data from an excel file in google docs to new documents in google docs.
     *
     * @param indexStr word list offset.
     * @param response HttpServletResponse.
     */
    @RequestMapping(value = "/" + ViewConstant.VOCABULARY_UPDATE_GOOGLE_DOCS_REQUEST, method = RequestMethod.GET)
    public void exportGoogleDocs(@RequestParam("index") String indexStr, HttpServletResponse response) {
        int index = 0;
        if (StringUtils.isEmpty(indexStr)) {
            try {
                response.getWriter().write("Bad request.");
            } catch (Exception e) {
                LOGGER.info("Could not render response.");
            }
        }

        index = Integer.parseInt(indexStr);


        LOGGER.info("Starting to export iVocabulary to GOOGLE DOCS.");

        int numberOfWords = vocabularyService.getWordSize();
        int nextIndex = index + 40 + 1;
        if (index + 40 + 1 < numberOfWords) {
            String xml = constructIVocabularyFile(index, 40, 20);
            try {
                LOGGER.info("Saving to GOOGLE DOCS.");
                GoogleDocsUtils.save(docsService, "XML", index + " - " + (index + 40), xml, "charamhkh", "me27&ml17");
            } catch (Exception e) {
                LOGGER.info("Could not save to google docs. Try again.");
                nextIndex = index;
            }
            LOGGER.info(">>>>>>>>>>>>>>>>>>> Posting to Queue with index: [" + index + "]");
            QueueFactory.getDefaultQueue().add(url("/vocabulary/iVocabulary2GD.html?index=" + nextIndex).method(TaskOptions.Method.GET));
        }

        try {
            response.setContentType("text/html");
            response.getWriter().write("Be patient!!!");
        } catch (Exception e) {

        }
    }


    /**
     * Look up data from Google docs excel file and then update to GAE datastore.
     *
     * @param startingIndex row number.
     * @param columnIndex   column number.
     * @param response      HttpServletResponse.
     */
    @RequestMapping(value = "/" + ViewConstant.VOCABULARY_UPDATE_REQUEST, method = RequestMethod.GET)
    public void updateWordsFromSpreadSheet(@RequestParam("index") String startingIndex
            , @RequestParam("col") String columnIndex
            , HttpServletResponse response) {
        final long start = System.currentTimeMillis();

        int index = 0;
        int col = 1; // column index starting from 1 not 0.
        if (StringUtils.isNotEmpty(startingIndex)) { // parse starting index.
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

    /**
     * Contruct XML complied with iVocabulary XVOC format.
     *
     * @param allWords      list of words.
     * @param size          number of words contained in a Page.
     * @param startingIndex offset of the list.
     * @param requestSize   number of words will be processed.
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
            LOGGER.info("Use current date.");

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
                xmlBuilder.append("<Word sourceWord=\"" + w.getDescription() + " " + pron + "\" targetWord=\"" + targetWords.toString() + "\">");
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
     * @return xml view.
     */
    @RequestMapping(value = "/" + ViewConstant.VOCABULARY_I_VOCABULARY_REQUEST, method = RequestMethod.GET)
    public ModelAndView buildIVocabularyFile(@RequestParam("startingIndex") String startingIndexStr, @RequestParam("pageSize") String pageSizeStr,
                                             @RequestParam("size") String sizeStr, HttpServletResponse response) {

        int startingIndex = 0, size = 100, pageSize = 20; // default size = 100;

        if (StringUtils.isEmpty(startingIndexStr)) {
            try {
                response.setContentType("text/html");
                response.getWriter().write("Check your param. startingIndex must not be ommitted.");
            } catch (Exception e) {
                LOGGER.error("Could not print output.");
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
     * @return xml string.
     */
    private String constructIVocabularyFile(int startingIndex, int size, int pageSize) {
        List<Word> allWords = vocabularyService.getAllWordsInRange(startingIndex, size);
        String xml = constructXMLBlockContent(allWords, pageSize, startingIndex, size);

        String xmlStr = "";
        try {
            InputStream is = this.getClass().getResourceAsStream("/vocabulary.xml");

            if (is == null) {
                LOGGER.info("Could not load resources.");
            }

            VTDGen vg = new VTDGen(); // Instantiate VTDGen
            StringWriter writer = new StringWriter();
            IOUtils.copy(is, writer);
            String theString = writer.toString();
            vg.setDoc(theString.getBytes());

            vg.parse(true);

            XMLModifier xm = new XMLModifier(); //Instantiate XMLModifier
            LOGGER.info("Starting to parse XML");
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


        } catch (Exception e) {
            LOGGER.info("Could not parse or update vocabulary.xml file.");
        }
        return xmlStr;
    }

    

    @RequestMapping(value = "/" + ViewConstant.VOCABULARY_VIEW_NEWLY_ADDED_REQUEST, method = RequestMethod.POST)
    public ModelAndView listNewlyAdded(@RequestParam("size") String sizeStr) {
        int size = 10;
        if (StringUtils.isNotEmpty(sizeStr)) {
            size = Integer.parseInt(sizeStr);            
        }

        User user = getUserCredential();

        ModelAndView modelAndView = new ModelAndView();
        if (user != null) {
            Map<String, Object> jsonData = new HashMap<String, Object>();
            List<Word> words = vocabularyService.getAllWordsFromUser(user.getWordList());
            words.addAll(vocabularyService.getAllWordsInRange(0, size)); // get all DB words.
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

    /**
     * Add a new word to the list.
     * @param word word to be added.
     * @return view.
     */
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
            } catch (IOException ex) {
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

}
