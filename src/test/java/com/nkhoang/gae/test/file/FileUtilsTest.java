package com.nkhoang.gae.test.file;

import com.nkhoang.gae.utils.FileUtils;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hoangknguyen
 * Date: 6/17/11
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext-service.xml"})

public class FileUtilsTest {
	@Test
	public void testLoadWordList() throws Exception {
		List<String> wordList = FileUtils.readWordsFromFile("src/test/resources/word-list.txt");

		Assert.assertTrue(wordList.size() > 0);
	}
}
