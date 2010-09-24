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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    private String defaultFolder;
    private String defaultDocument;
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
    private final DocsService docsService = new DocsService(APP_NAME);

    private static final Log log = LogFactory.getLog(BackupServiceImpl.class);
    private final String URL_FOLDERS = "/contents";

    public boolean backup(String content) {
        try {
            // login first
            login(username, password);
            String newDocName = defaultDocument + "_" + getCurrentDate();
            DocumentListEntry targetFolder = checkFolder(defaultFolder);
            if (targetFolder == null) {
                log.info(defaultFolder + " does not exist.");
                log.info("Now creating new folder named : " + defaultFolder);
                targetFolder = createNew(defaultFolder, "folder");
                if (targetFolder == null) {
                    // unable to create a new folder
                    log.info("Unable to create a new folder");
                    throw new Exception("Unable to create a new folder.");
                }
            }
            log.info("Retrieving documents from [" + defaultFolder + "]...");
            DocumentListEntry document = retrieveDocument(newDocName, targetFolder);

            if (document == null) {
                log.info("Creating new document ... ");
                // create a new document with default name
                document = createNew(newDocName, "document");
                if (document == null) {
                    throw new Exception("Unable to create a new document");
                }
                log.info("Move it to our default folder");
                // move to folder
                document = moveObjectToFolder(targetFolder.getResourceId(), document.getResourceId());
            }
            // update content
            document.setMediaSource(new MediaByteArraySource(content.getBytes("UTF-8"), "text/plain"));
            // document.setContent(new PlainTextConstruct("Checking for new"));

            document.updateMedia(true);
        } catch (Exception e) {
            log.error(e);
            return false;
        }
        return true;
    }

    public String getBackup(String revision) {
        String xml = null;
        try {

            // reformat the document revision
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN_FULL);
            Date revisionDate = formatter.parse(revision);
            formatter = new SimpleDateFormat(DATE_PATTERN);

            login(username, password);
            String newDocName = defaultDocument + "_" + formatter.format(revisionDate);
            DocumentListEntry targetFolder = checkFolder(defaultFolder);
            if (targetFolder == null) {
                log.info(defaultFolder + " does not exist.");
                log.info("Now creating new folder named : " + defaultFolder);
                targetFolder = createNew(defaultFolder, "folder");
                if (targetFolder == null) {
                    // unable to create a new folder
                    log.info("Unable to create a new folder");
                    throw new Exception("Unable to create a new folder.");
                }
            }
            log.info("Retrieving documents from [" + defaultFolder + "]...");
            DocumentListEntry document = retrieveDocument(newDocName, targetFolder);

            if (document == null) {
                throw new Exception("Document with name: " + newDocName + " does not exist");
            } else {
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
            }

        } catch (Exception e) {
            log.error(e);
        }
        return xml;
    }

    public List<String> listBackupRevisions() {
        List<String> documents = new ArrayList<String>(0);
        // it just holds the document date and time
        try {
            login(username, password);
            DocumentListEntry targetFolder = checkFolder(defaultFolder);
            if (targetFolder == null) {
                log.info(defaultFolder + " does not exist.");
                log.info("Now creating new folder named : " + defaultFolder);
                targetFolder = createNew(defaultFolder, "folder");
                if (targetFolder == null) {
                    // unable to create a new folder
                    log.info("Unable to create a new folder");
                    throw new Exception("Unable to create a new folder.");
                }
            }
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
            log.error(e);
        }
        return documents;
    }

    /**
     * 
     * @param docName
     *            defaultName + datetime
     * @param targetFolder a target folder.
     * @return a document list.
     * @throws Exception if possible.
     */
    private DocumentListEntry retrieveDocument(String docName, DocumentListEntry targetFolder) throws Exception {
        DocumentListEntry document = null;
        // search it first
        String folderFeedUrl = ((MediaContent) targetFolder.getContent()).getUri();
        DocumentQuery query = new DocumentQuery(new URL(folderFeedUrl));
        DocumentListFeed feeds = docsService.getFeed(query, DocumentListFeed.class);

        if (feeds.getEntries() != null && feeds.getEntries().size() > 0) {

            // loop through the feeds
            for (DocumentListEntry feed : feeds.getEntries()) {
                // check the name
                if (feed.getTitle().getPlainText().equals(docName)) {
                    document = feed;
                }
            }

        }

        /*
         * int feedSize = feeds.getEntries().size();
         * log.info("Number of document inside [" + defaultFolder + "] : " +
         * feedSize); // check feeds if (feedSize == 0) {
         * log.info("Empty foler"); } else if (feedSize == 1) {
         * log.info("Only one item inside the folder"); // what we expected
         * document = feeds.getEntries().get(0); } else {
         * log.info("Deleting items inside folder"); // clear all for
         * (DocumentListEntry entry : feeds.getEntries()) { log.info("Item : " +
         * entry.getTitle().getPlainText()); // move it out of the folder.
         * entry.delete(); // then delete it docsService.delete(new URL(
         * "https://docs.google.com/feeds/default/private/full/" +
         * entry.getResourceId()), "*"); } }
         */
        return document;
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
        return formatter.format(calendar.getTime());
    }

    private DocumentListEntry moveObjectToFolder(String targetFolderPath, String docId) throws IOException,
            ServiceException {

        DocumentListEntry doc = new DocumentListEntry();
        doc.setId(BASE_URL + "/" + docId);

        URL url = new URL(BASE_URL + "/" + targetFolderPath + URL_FOLDERS);
        return docsService.insert(url, doc);
    }

    private DocumentListEntry createNew(String folderTitle, String type) throws IOException, ServiceException {
        DocumentListEntry newEntry = null;
        if (type.equals("document")) {
            newEntry = new DocumentEntry();
        } else if (type.equals("presentation")) {
            newEntry = new PresentationEntry();
        } else if (type.equals("spreadsheet")) {
            newEntry = new SpreadsheetEntry();
        } else if (type.equals("folder")) {
            newEntry = new FolderEntry();
        }
        // set folder title
        PlainTextConstruct textConstruct = new PlainTextConstruct(folderTitle);
        newEntry.setTitle(textConstruct);

        return docsService.insert(new URL(BASE_URL), newEntry);
    }

    private void login(String username, String password) throws AuthenticationException {
        docsService.setUserCredentials(username, password);
    }

    /**
     * Check to make sure that the folder is existing otherwise we have to
     * create a new one
     * 
     * @param folderTitle
     *            folder title for searching.
     * @return a DocumentListEntry
     * @throws IOException if possible.
     * @throws ServiceException if possible.
     */
    private DocumentListEntry checkFolder(String folderTitle) throws IOException,
            ServiceException {
        DocumentListFeed feeds = listFolders(BASE_URL + URL_CATEGORY_FOLDER);
        // hold the folder if it is found.
        DocumentListEntry foundEntry = null;

        for (DocumentListEntry entry : feeds.getEntries()) {
            // check name
            if (entry.getTitle().getPlainText().equals(folderTitle)) {
                foundEntry = entry;
                break;
            }
        }
        return foundEntry;
    }

    private DocumentListFeed listFolders(String uri) throws IOException, ServiceException {
        if (uri == null) {
            // use default
            uri = BASE_URL + URL_CATEGORY_FOLDER;
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
