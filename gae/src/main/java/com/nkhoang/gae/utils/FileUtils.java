package com.nkhoang.gae.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FileUtils {
    private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

    /**
     * Read word / word items from file located in WEB-INF/vocabulary folder.
     *
     * @param filePath the path to the file.
     * @return a list of word in string.
     */
    public static List<String> readWordsFromFile(String filePath) {
        LOG.info("Getting data from file: " + filePath);
        List<String> wordList = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            do {
                String word = reader.readLine();
                wordList.add(word);
            } while (reader.ready());
        } catch (IOException ioe) {
            LOG.error(String.format("Could not open file in : %s", filePath));
        }

        return wordList;
    }
}
