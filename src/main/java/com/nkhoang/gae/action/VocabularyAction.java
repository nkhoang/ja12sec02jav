package com.nkhoang.gae.action;

import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.gdata.client.Query;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.spreadsheet.CellQuery;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.*;
import com.google.gdata.data.media.MediaByteArraySource;
import com.google.gdata.data.spreadsheet.CellEntry;
import com.google.gdata.data.spreadsheet.CellFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.nkhoang.gae.gson.strategy.GSONStrategy;
import com.nkhoang.gae.manager.UserManager;
import com.nkhoang.gae.model.Meaning;
import com.nkhoang.gae.model.User;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.VocabularyService;
import com.nkhoang.gae.service.impl.SpreadsheetServiceImpl;
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
    private final String BASE_URL = "https://docs.google.com/feeds/default/private/full";
    private final String DOWNLOAD_URL = "https://docs.google.com/feeds/download";
    private final String URL_CATEGORY_DOCUMENT = "/-/document";
    private final String URL_CATEGORY_SPREADSHEET = "/-/spreadsheet";
    private final String URL_CATEGORY_PDF = "/-/pdf";
    private final String URL_CATEGORY_PRESENTATION = "/-/presentation";
    private final String URL_CATEGORY_STARRED = "/-/starred";
    private final String URL_CATEGORY_TRASHED = "/-/trashed";
    private final String URL_CATEGORY_FOLDER = "/-/folder";
    private final String URL_CATEGORY_EXPORT = "/Export";
    private final DocsService docsService = new DocsService(APP_NAME); // Google Document Service.

    @RequestMapping(value = "/" + ViewConstant.VOCABULARY_HOME_REQUEST, method = RequestMethod.GET)
    public String getVocabularyPage() {
        return ViewConstant.VOCABULARY_VIEW;
    }

    public void save(String folderName, String documentTitle, String content, String anotherUserName, String anotherPassword) throws Exception{
            login(anotherUserName, anotherPassword);

            DocumentListEntry targetFolder = checkFolderExistence(folderName); // create or get.
            LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : Creating new document ... " + documentTitle);
            DocumentListEntry document = createNew(documentTitle, "document"); // create a new document with default name

            if (document == null) {
                throw new Exception(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : Unable to create a new document");
            }
            LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : Move it to our default folder");
            document = moveObjectToFolder(targetFolder, document); // move to folder
            document.setMediaSource(new MediaByteArraySource(content.getBytes("UTF-8"), "text/plain")); // update content
            document.updateMedia(true);

    }

    private void login(String username, String password) throws AuthenticationException {
        docsService.setUserCredentials(username, password);
    }

    public DocumentListEntry moveObjectToFolder(DocumentListEntry folderEntry, DocumentListEntry doc) throws IOException,
            ServiceException {
        String destFolderUri = ((MediaContent) folderEntry.getContent()).getUri();

        return docsService.insert(new URL(destFolderUri), doc);
    }

    private DocumentListEntry checkFolderExistence(String folderName) throws IOException, ServiceException {
        DocumentListEntry targetFolder = checkFolder(folderName); // check folder existence.
        if (targetFolder == null) { // not found.
            LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : " + folderName + " -> Could not find.");
            LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : Now creating new folder named : " + folderName);
            targetFolder = createNew(folderName, "folder");
        }
        return targetFolder;
    }

    private DocumentListFeed listFolders(String uri) throws IOException, ServiceException {
        if (uri == null) {
            uri = BASE_URL + URL_CATEGORY_FOLDER; // use default
        }
        URL url = new URL(uri);
        Query query = new Query(url);
        return docsService.query(query, DocumentListFeed.class);
    }


    private DocumentListEntry checkFolder(String folderTitle) throws IOException, ServiceException {
        DocumentListFeed feeds = listFolders(BASE_URL + URL_CATEGORY_FOLDER);  // get folder feeds which contains folder information.
        DocumentListEntry foundEntry = null;
        for (DocumentListEntry entry : feeds.getEntries()) {
            if (entry.getTitle().getPlainText().equals(folderTitle)) { // check name
                foundEntry = entry;
                break;
            }
        }
        return foundEntry;
    }


    private DocumentListEntry createNew(String folderTitle, String type) throws IOException, ServiceException {
        DocumentListEntry newEntry = null;
        if (type.equals("document")) { // type = " document ".
            newEntry = new DocumentEntry();
        } else if (type.equals("presentation")) {  // the same as above.
            newEntry = new PresentationEntry();
        } else if (type.equals("spreadsheet")) { // the same as above.
            newEntry = new SpreadsheetEntry();
        } else if (type.equals("folder")) { // type = " folder ".
            newEntry = new FolderEntry();
        }
        PlainTextConstruct textConstruct = new PlainTextConstruct(folderTitle);
        newEntry.setTitle(textConstruct); // set folder title
        return docsService.insert(new URL(BASE_URL), newEntry);
    }


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
            String xml = constructIVocabularyFile(index, 40);
            try {
                LOGGER.info("Saving to GOOGLE DOCS.");
                save("XML", index + " - " + (index + 40), xml, "nkhoang.it", "me27&ml17");
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

            String pron = w.getPron(); // append to comment.

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

    @RequestMapping(value = "/" + ViewConstant.VOCABULARY_I_VOCABULARY_REQUEST, method = RequestMethod.GET)
    public ModelAndView buildIVocabularyFile(@RequestParam("startingIndex") String startingIndexStr, @RequestParam("size") String sizeStr, HttpServletResponse response) {

        int startingIndex = 0, size = 100; // default size = 100;

        if (StringUtils.isEmpty(startingIndexStr)) {
            try {
                response.setContentType("text/html");
                response.getWriter().write("Check your param. startingIndex should not be ommitted.");
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

        String xmlStr = constructIVocabularyFile(startingIndex, size);

        ModelAndView mav = new ModelAndView();
        mav.setView(new XMLView());
        mav.addObject("data", xmlStr);

        return mav;
    }

    private String constructIVocabularyFile(int startingIndex, int size) {
        List<Word> allWords = vocabularyService.getAllWordsInRange(startingIndex, size);
        String xml = constructXMLBlockContent(allWords, 20, startingIndex, size);

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

    @RequestMapping(value = "/" + ViewConstant.VOCABULARY_VIEW_ALL_REQUEST, method = RequestMethod.POST)
    public ModelAndView listAll() {
        User user = getUserCredential();

        ModelAndView modelAndView = new ModelAndView();
        if (user != null) {
            Map<String, Object> jsonData = new HashMap<String, Object>();
            List<Word> words = vocabularyService.getAllWordsFromUser(user.getWordList());
            words.addAll(vocabularyService.getAllWords()); // get all DB words.
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
