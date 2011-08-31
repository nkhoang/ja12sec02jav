package com.nkhoang.gae.service;

import java.util.List;

/**
 * Serving backup need.
 *
 * @author hoangnk
 */
public interface BackupService {
    public void save(String folderName, String documentTitle, String content, String anotherUserName, String anotherPassword);

    public boolean backup(String content);

    /**
     * List all available Revisions from Google Docs.
     *
     * @return a list of revision in text format. See the DATE_PATTERN for the
     *         revision text format.
     */
    public List<String> listBackupRevisions();

    /**
     * Get the content of the desired revision
     *
     * @param revision It is actually a date format after the backup file name.
     * @return content in text.
     */
    public String getBackup(String revision);
}
