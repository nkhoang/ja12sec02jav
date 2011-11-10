package com.nkhoang.harvester;


import com.nkhoang.file.FileUtils;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class OxfordHarvesterTest {
    private static Logger LOG = LoggerFactory.getLogger(OxfordHarvesterTest.class.getCanonicalName());

    @Test
    public void testHarvester() {
        List<String> result = OxfordHarvester.getRelatedWords("cool");
        LOG.info(result.toString());
        Assert.assertTrue(CollectionUtils.isNotEmpty(result));
    }

    @Test
    public void testReadFromFile() {
        List<String> wordList = FileUtils.readWordsFromFile("harvester/src/test/resources/fullList.txt");
        Assert.assertTrue(CollectionUtils.isNotEmpty(wordList));
    }

    @Test
    public void testLookupOxford() throws Exception {
        FileWriter writer = null;

        writer = new FileWriter(new File("harvester/src/main/resources/validWords.txt"), true);


        List<String> wordList = FileUtils.readWordsFromFile("harvester/src/test/resources/fullList.txt");
        LOG.info("Total size: " + wordList.size());
        int i = 0;
        List<String> invalidList = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(wordList)) {
            for (String word : wordList) {
                ++i;
                // LOG.info("Index" + ++i);
                List<String> relatedWords = OxfordHarvester.getRelatedWords(word);
                if (CollectionUtils.isEmpty(relatedWords)) {
                    invalidList.add(word);
                } else {
                    LOG.info("Word: " + word  + " index : " + i);
                    writer.append(word + "\n");
                    writer.flush();
                }
            }
        }

        writer.close();
    }
}
