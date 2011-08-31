package com.nkhoang.gae.service.impl;

import com.google.gdata.client.DocumentQuery;
import com.google.gdata.client.Query;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.*;
import com.google.gdata.data.media.MediaByteArraySource;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.nkhoang.gae.service.BackupService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BackupServiceImpl implements BackupService {
    private String username;
    private String password;
    private String defaultFolder; // " Backup "
    private String defaultDocument; // " Chara Backup "
    private final String DATE_PATTERN = "ddMMyyyy";
    private final String DATE_PATTERN_FULL = "dd/MM/yyyy";

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

    private static final Logger LOGGER = LoggerFactory.getLogger(BackupServiceImpl.class);
    private final String URL_FOLDERS = "/contents";


    public void save(String folderName, String documentTitle, String content, String anotherUserName, String anotherPassword) {
        try {
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

        } catch (Exception e) {
            LOGGER.error(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : ERROR. COULD NOT SAVE/UPDATE DATA.", e);

        }
    }

    /**
     * Backup content to Google Docs.
     *
     * @param content content to be saved.
     * @return true/false.
     */
    public boolean backup(String content) {
        try {
            login(username, password); // first login with username and password.
            String newDocName = defaultDocument + "_" + getCurrentDate(); // pattern: [document name]_ddmmyyyy.
            DocumentListEntry targetFolder = checkFolderExistence(); // get or create a new folder.
            LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : Retrieving documents from [" + defaultFolder + "]...");
            DocumentListEntry document = retrieveDocument(newDocName, targetFolder);

            if (document == null) {
                LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : Creating new document ... " + newDocName);
                document = createNew(newDocName, "document"); // create a new document with default name
                if (document == null) {
                    throw new Exception(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : Unable to create a new document");
                }
                LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : Move it to our default folder");
                document = moveObjectToFolder(targetFolder, document); // move to folder
            }
            document.setMediaSource(new MediaByteArraySource(content.getBytes("UTF-8"), "text/plain")); // update content
            document.updateMedia(true);
        } catch (Exception e) {
            LOGGER.error(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : ERROR. COULD NOT SAVE BACKUP DATA.", e);
            return false;
        }
        return true;
    }

    /**
     * Check folder existence with given folder name.
     *
     * @return always return an object. Create a new one if there is not one.
     * @throws IOException      if any.
     * @throws ServiceException if any.
     */
    private DocumentListEntry checkFolderExistence(String folderName) throws IOException, ServiceException {
        DocumentListEntry targetFolder = checkFolder(folderName); // check folder existence.
        if (targetFolder == null) { // not found.
            LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : " + folderName + " -> Could not find.");
            LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : Now creating new folder named : " + folderName);
            targetFolder = createNew(folderName, "folder");
        }
        return targetFolder;
    }

    /**
     * Check folder existence using default folder name.
     *
     * @return always return an object. Create a new one if there is not one.
     * @throws IOException      if any.
     * @throws ServiceException if any.
     */
    private DocumentListEntry checkFolderExistence() throws IOException, ServiceException {
        DocumentListEntry targetFolder = checkFolder(defaultFolder); // check folder existence.
        if (targetFolder == null) { // not found.
            LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : " + defaultFolder + " -> Could not find.");
            LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : Now creating new folder named : " + defaultFolder);
            targetFolder = createNew(defaultFolder, "folder");
        }
        return targetFolder;
    }

    /**
     * Get backup by providing a revision name.
     *
     * @param revision It is actually a date format after the backup file name.
     * @return xml content.
     */
    public String getBackup(String revision) {
        String xml = null;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN_FULL); // reformat the document revision.
            Date revisionDate = formatter.parse(revision);
            formatter = new SimpleDateFormat(DATE_PATTERN);

            login(username, password); // login
            String newDocName = defaultDocument + "_" + formatter.format(revisionDate);
            DocumentListEntry targetFolder = checkFolderExistence();
            LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : Retrieving documents from [" + defaultFolder + "]...");
            DocumentListEntry document = retrieveDocument(newDocName, targetFolder);
            if (document != null) {
                xml = downloadDocumentContent(document);
            }

        } catch (Exception e) {
            LOGGER.error(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : ERROR. COULD NOT GET BACKUP DATA.", e);
        }
        return xml;
    }

    /**
     * Download document content from Google Docs.
     *
     * @param document google document object.
     * @return download document content.
     * @throws IOException      if any.
     * @throws ServiceException if any.
     */
    private String downloadDocumentContent(DocumentListEntry document) throws IOException, ServiceException {
        String xml = null;
        URL downloadedUrl = new URL(DOWNLOAD_URL + "/documents" + URL_CATEGORY_EXPORT + "?docID="
                + document.getResourceId() + "&exportFormat=txt");

        MediaContent mc = new MediaContent();
        mc.setUri(downloadedUrl.toString());

        MediaSource fileSource = docsService.getMedia(mc);
        InputStream is = fileSource.getInputStream();
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            xml = sb.toString();
        }
        return xml;
    }

    public List<String> listBackupRevisions() {
        List<String> documents = new ArrayList<String>(0);
        // it just holds the document date and time
        try {
            login(username, password);
            DocumentListEntry targetFolder = checkFolderExistence();
            String folderFeedUrl = ((MediaContent) targetFolder.getContent()).getUri();
            DocumentQuery query = new DocumentQuery(new URL(folderFeedUrl));
            DocumentListFeed feeds = docsService.getFeed(query, DocumentListFeed.class);
            // looping to get the name
            for (DocumentListEntry feed : feeds.getEntries()) {
                // just add the one with the proper name
                String feedName = feed.getTitle().getPlainText();
                // check name
                if (feedName.contains(defaultDocument)) {
                    String[] feedNameArr = feedName.split("_");
                    if (feedNameArr.length == 2 && feedNameArr[1].length() == DATE_PATTERN.length()) {
                        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
                        Date formattedDate = formatter.parse(feedNameArr[1]);

                        // create another formatter
                        formatter = new SimpleDateFormat(DATE_PATTERN_FULL);

                        documents.add(formatter.format(formattedDate));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
        return documents;
    }

    /**
     * Searching for a given document name based on an folder DocumentListEntry.
     *
     * @param docName      defaultName + datetime
     * @param targetFolder a target folder.
     * @return a document list. Null if not found.
     * @throws Exception if possible.
     */
    private DocumentListEntry retrieveDocument(String docName, DocumentListEntry targetFolder) throws Exception {
        DocumentListEntry document = null;
        String folderFeedUrl = ((MediaContent) targetFolder.getContent()).getUri(); // get folder feed URI.
        DocumentQuery query = new DocumentQuery(new URL(folderFeedUrl)); // starting a query.
        DocumentListFeed feeds = docsService.getFeed(query, DocumentListFeed.class); // get all feeds.
        if (feeds.getEntries() != null && feeds.getEntries().size() > 0) {
            for (DocumentListEntry feed : feeds.getEntries()) { // loop through the feeds
                if (feed.getTitle().getPlainText().equals(docName)) { // check the name
                    document = feed; // found one.
                }
            }
        }
        return document;
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
        return formatter.format(calendar.getTime());
    }

    public DocumentListEntry moveObjectToFolder(DocumentListEntry folderEntry, DocumentListEntry doc) throws IOException,
            ServiceException {
        String destFolderUri = ((MediaContent) folderEntry.getContent()).getUri();

        return docsService.insert(new URL(destFolderUri), doc);
    }

    /**
     * Create new document type. Can be any of these followings: document, folder, spreadsheet, presentation.
     *
     * @param folderTitle folder title.
     * @param type        type as above.
     * @return DocumentListEntry.
     * @throws IOException      if any.
     * @throws ServiceException if any.
     */
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

    /**
     * Login to Google Docs.
     *
     * @param username username.
     * @param password password.
     * @throws AuthenticationException exception.
     */
    private void login(String username, String password) throws AuthenticationException {
        docsService.setUserCredentials(username, password);
    }

    /**
     * Provide Folder title to search for existence.
     *
     * @param folderTitle folder title for searching.
     * @return a DocumentListEntry if folder is existing.
     * @throws IOException      if possible.
     * @throws ServiceException if possible.
     */
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

    /**
     * List all folder available in Google Docs.
     *
     * @param uri the sample URI look like this: https://docs.google.com/feeds/default/private/full + [type] (can be /-/folder ...)
     * @return DocumentListFeed object which contains references to DocumentEntry.
     * @throws IOException      if any.
     * @throws ServiceException for connection problem.
     */
    private DocumentListFeed listFolders(String uri) throws IOException, ServiceException {
        if (uri == null) {
            uri = BASE_URL + URL_CATEGORY_FOLDER; // use default
        }
        URL url = new URL(uri);
        Query query = new Query(url);
        return docsService.query(query, DocumentListFeed.class);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDefaultFolder() {
        return defaultFolder;
    }

    public void setDefaultFolder(String defaultFolder) {
        this.defaultFolder = defaultFolder;
    }

    public String getDefaultDocument() {
        return defaultDocument;
    }

    public void setDefaultDocument(String defaultDocument) {
        this.defaultDocument = defaultDocument;
    }
}
