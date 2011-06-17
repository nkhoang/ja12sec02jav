package com.nkhoang.gae.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.NewsAddress;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hoangknguyen
 * Date: 6/17/11
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class FileUtils {
	private static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

	public static List<String> readWordsFromFile(String filePath) {
		List<String> wordList = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));


			do {
				String word = reader.readLine();
				wordList.add(word);
			} while (reader.ready());
		}
		catch (IOException ioe) {
			LOG.error(String.format("Could not open file in : %s", filePath));
		}

		return wordList;
	}
}
