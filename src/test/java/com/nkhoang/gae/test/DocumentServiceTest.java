package com.nkhoang.gae.test;

import com.google.gdata.client.DocumentQuery;
import com.google.gdata.client.Query;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.docs.*;
import com.google.gdata.data.media.MediaByteArraySource;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.nkhoang.gae.service.BackupService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration({"/applicationContext-service.xml"})
public class DocumentServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentServiceTest.class);

    private final String URL_FEED = "/feeds";
    private final String URL_DOWNLOAD = "/download";
    private final String URL_DOCLIST_FEED = "/private/full";

    private final String URL_DEFAULT = "/default";
    private final String URL_FOLDERS = "/contents";
    private final String URL_ACL = "/acl";
    private final String URL_REVISIONS = "/revisions";

    private final String URL_CATEGORY_DOCUMENT = "/-/document";
    private final String URL_CATEGORY_SPREADSHEET = "/-/spreadsheet";
    private final String URL_CATEGORY_PDF = "/-/pdf";
    private final String URL_CATEGORY_PRESENTATION = "/-/presentation";
    private final String URL_CATEGORY_STARRED = "/-/starred";
    private final String URL_CATEGORY_TRASHED = "/-/trashed";
    private final String URL_CATEGORY_FOLDER = "/-/folder";
    private final String URL_CATEGORY_EXPORT = "/Export";

    /**
     * Services
     */
    @Autowired
    private BackupService backupService;
    // Document service from Google
    private DocsService documentService = new DocsService("chara");

    @Test
    public void testFun() {
        System.out.println("Just a fun test");

    }

    /**
     * Test get revision from google document
     */

    @Test
    public void testRevisions() {
        List<String> revisions = backupService.listBackupRevisions();
        for (String s : revisions) {
            System.out.println("Revision name : " + s);
        }
    }

    public void testRestore() throws Exception {
        String content = backupService.getBackup("24/09/2010");

        if (content != null) {
            System.out.println(content);
        }
    }

    public void testBackup() {
        backupService.backup("Nguyen Khanh Hoang");
    }


    public void testUpdateContent() {
        save("XML", "iVocabulary", "<Page></Page>", "nkhoang.it@gmail.com", "me27&ml17");
    }


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

    private DocumentListEntry checkFolderExistence(String folderName) throws IOException, ServiceException {
        DocumentListEntry targetFolder = checkFolder(folderName); // check folder existence.
        if (targetFolder == null) { // not found.
            LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : " + folderName + " -> Could not find.");
            LOGGER.info(">>>>>>>>>>>>> GOOGLE DOCS <<<<<<<<<<<<<<<< : Now creating new folder named : " + folderName);
            targetFolder = createNew(folderName, "folder");
        }
        return targetFolder;
    }


    public void testUpdate() throws IOException, ServiceException {
        DocumentListEntry entry = documentService.getEntry(new URL(
                "https://docs.google.com/feeds/default/private/full/document:"
                        + "0ARQF0llLMMGXZDVicnJ2ZF8xMTM2Zm1mZHp3NmY"), DocumentListEntry.class);

        entry.setMediaSource(new MediaByteArraySource("Hello 2".getBytes(), "text/plain"));
        entry.setTitle(new PlainTextConstruct("Chara Backup"));
        entry.updateMedia(true);

        System.out.println(entry.getTitle().getPlainText());
    }

    public void _testSearch() {
        try {
            DocumentListEntry folder = checkFolder("Backup");
            if (folder != null) {
                System.out.println(folder.getResourceId());
                System.out.println(((MediaContent) folder.getContent()).getUri());

                String feedUrl = "https://docs.google.com/feeds/default/private/full/-/document";
                URL folderFeedURL = new URL(feedUrl);
                DocumentQuery query = new DocumentQuery(folderFeedURL);
                query.setTitleQuery("Chara Backup");
                DocumentListFeed feeds = documentService.getFeed(query, DocumentListFeed.class);

                System.out.println(feeds.getEntries().get(0).getTitle().getPlainText());

                System.out.println(feeds);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public DocumentListFeed search(String url, Map<String, String> searchParameters) throws IOException,
            ServiceException {

        Query qry = new Query(new URL(url));

        for (String key : searchParameters.keySet()) {
            qry.setStringCustomParameter(key, searchParameters.get(key));
        }

        return documentService.query(qry, DocumentListFeed.class);

    }

    public void _testCreate() {
        try {
            DocumentListEntry entry = createNew("Backup", "folder");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void _testCreateDocument() {
        try {
            DocumentListEntry document = createNew("backup", "document");
            // check Folder
            DocumentListEntry folder = checkFolder("Backup");
            if (folder == null) {
                folder = createNew("Backup", "folder");
            }

            // moving
            moveObjectToFolder(document, folder);

            if (document != null) {
                System.out.println(document.getTitle().getPlainText());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void _testCheckFolder() {
        try {
            DocumentListEntry entry = checkFolder("Backup");
            System.out.println(entry.getTitle().getPlainText());
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public DocumentListEntry checkFolder(String title) throws IOException, ServiceException {
        DocumentListFeed feeds = listFolders("https://docs.google.com/feeds/default/private/full/-/folder");

        DocumentListEntry foundEntry = null;

        for (DocumentListEntry entry : feeds.getEntries()) {
            // check name
            if (entry.getTitle().getPlainText().equals(title)) {
                foundEntry = entry;
                break;
            }
        }
        return foundEntry;
    }

    public DocumentListEntry moveObjectToFolder(DocumentListEntry folderEntry, DocumentListEntry doc) throws IOException,
            ServiceException {
        String destFolderUri = ((MediaContent) folderEntry.getContent()).getUri();

        return documentService.insert(new URL(destFolderUri), doc);
    }

    public DocumentListFeed listFolders(String uri) throws IOException, ServiceException {
        if (uri == null) {
            uri = "https://docs.google.com/feeds/default/private/full/-/folder";
        }
        URL url = new URL(uri);
        Query query = new Query(url);
        return documentService.query(query, DocumentListFeed.class);
    }

    private DocumentListEntry createNew(String title, String type) throws IOException, ServiceException {
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

        newEntry.setTitle(new PlainTextConstruct(title));
        return documentService.insert(new URL("https://docs.google.com/feeds/default/private/full"), newEntry);
    }

    public void login(String username, String password) throws AuthenticationException {
        documentService.setUserCredentials(username, password);
    }

    public void setBackupService(BackupService backupService) {
        this.backupService = backupService;
    }

    public BackupService getBackupService() {
        return backupService;
    }

}
