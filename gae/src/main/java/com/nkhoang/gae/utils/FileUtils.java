package com.nkhoang.gae.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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

    public static void writeToCSV(List<String> data, String filePath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true));

        for (String row : data) {
            writer.write(row);
            writer.write("\n");
        }
        writer.close();
    }
}
