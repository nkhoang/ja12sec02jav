package com.nkhoang.google.docs;

import com.google.gdata.client.Query;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.*;
import com.google.gdata.data.media.MediaByteArraySource;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class GoogleDocsUtils {
    private static final String BASE_URL = "https://docs.google.com/feeds/default/private/full";
    private static final String DOWNLOAD_URL = "https://docs.google.com/feeds/download";
    private static final String URL_CATEGORY_DOCUMENT = "/-/document";
    private static final String URL_CATEGORY_SPREADSHEET = "/-/spreadsheet";
    private static final String URL_CATEGORY_PDF = "/-/pdf";
    private static final String URL_CATEGORY_PRESENTATION = "/-/presentation";
    private static final String URL_CATEGORY_STARRED = "/-/starred";
    private static final String URL_CATEGORY_TRASHED = "/-/trashed";
    private static final String URL_CATEGORY_FOLDER = "/-/folder";
    private static final String URL_CATEGORY_EXPORT = "/Export";
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleDocsUtils.class);

    private static void login(DocsService docsService, String username, String password) throws AuthenticationException {
        docsService.setUserCredentials(username, password);
    }

    public static void save(DocsService docsService, String folderName, String documentTitle, String content, String anotherUserName, String anotherPassword) throws Exception {
        GoogleDocsUtils.login(docsService, anotherUserName, anotherPassword);

        DocumentListEntry targetFolder = checkFolderExistence(docsService, folderName); // create or get.
        LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : Creating new document ... " + documentTitle);
        DocumentListEntry document = GoogleDocsUtils.createNew(docsService, documentTitle, "document"); // create a new document with default name

        if (document == null) {
            throw new Exception(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : Unable to create a new document");
        }
        LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : Move it to our default folder");
        document = moveObjectToFolder(docsService, targetFolder, document); // move to folder
        document.setMediaSource(new MediaByteArraySource(content.getBytes("UTF-8"), "text/plain")); // update content
        document.updateMedia(true);
    }


    private static DocumentListEntry createNew(DocsService docsService, String folderTitle, String type) throws IOException, ServiceException {
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

    private static DocumentListEntry checkFolderExistence(DocsService docsService, String folderName) throws IOException, ServiceException {
        DocumentListEntry targetFolder = checkFolder(docsService, folderName); // check folder existence.
        if (targetFolder == null) { // not found.
            LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : " + folderName + " -> Could not find.");
            LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : Now creating new folder named : " + folderName);
            targetFolder = createNew(docsService, folderName, "folder");
        }
        return targetFolder;
    }


    private static DocumentListEntry moveObjectToFolder(DocsService docsService, DocumentListEntry folderEntry, DocumentListEntry doc) throws IOException,
            ServiceException {
        String destFolderUri = ((MediaContent) folderEntry.getContent()).getUri();

        return docsService.insert(new URL(destFolderUri), doc);
    }


    private static DocumentListFeed listFolders(DocsService docsService, String uri) throws IOException, ServiceException {
        if (uri == null) {
            uri = BASE_URL + URL_CATEGORY_FOLDER; // use default
        }
        URL url = new URL(uri);
        Query query = new Query(url);
        return docsService.query(query, DocumentListFeed.class);
    }


    private static DocumentListEntry checkFolder(DocsService docsService, String folderTitle) throws IOException, ServiceException {
        DocumentListFeed feeds = listFolders(docsService, BASE_URL + URL_CATEGORY_FOLDER);  // get folder feeds which contains folder information.
        DocumentListEntry foundEntry = null;
        for (DocumentListEntry entry : feeds.getEntries()) {
            if (entry.getTitle().getPlainText().equals(folderTitle)) { // check name
                foundEntry = entry;
                break;
            }
        }
        return foundEntry;
    }


}
