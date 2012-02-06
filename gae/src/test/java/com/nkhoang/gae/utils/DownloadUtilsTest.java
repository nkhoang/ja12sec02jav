package com.nkhoang.gae.utils;

import com.nkhoang.common.*;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.service.LookupService;
import com.nkhoang.gae.utils.web.DownloadUtils;
import com.nkhoang.thread.ThreadData;
import com.nkhoang.thread.callable.ThreadTask;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/applicationContext-service.xml", "/applicationContext-dao.xml", "/applicationContext-resources.xml"})
public class DownloadUtilsTest {
   private static final int THREAD_POOL_QUEUE_SIZE = 60;
   private static final int THREAD_POOL_KEEP_ALIVE_TIME = 0;
   private static final int THREAD_POOL_MAX_SIZE = 15;
   private static final int THREAD_POOL_CORE_SIZE = 15;
   private static final Logger LOG = LoggerFactory
         .getLogger(DownloadUtilsTest.class.getCanonicalName());
   private static ExecutorService _executor = new ThreadPoolExecutor(
         THREAD_POOL_CORE_SIZE, THREAD_POOL_MAX_SIZE, THREAD_POOL_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
         new ArrayBlockingQueue<Runnable>(THREAD_POOL_QUEUE_SIZE), new ThreadPoolExecutor.CallerRunsPolicy());

   @Autowired
   private LookupService cambridgeLookupService;


   @Test
   public void testDownloadFile() {
      DownloadUtils.fileDownload("http://dictionary.cambridge.org/media/british/us_pron/c/com/come_/come.mp3", "abc", "sound/english/");
   }


   @Test
   public void startDownloadThread() {
      List<String> wordList = com.nkhoang.common.FileUtils.readWordsFromFile("lucene/src/main/resources/fullList.txt");

      int index = 13000;
      int size = 4000;
      if (CollectionUtils.isNotEmpty(wordList)) {
         LOG.info("Total words found: " + wordList.size());
         for (int i = index; i < index + size; i++) {
            String word = wordList.get(i);
            _executor.submit(new DownloadWordTask(word));
            LOG.info("index: " + i);
         }
      }
   }

   class DownloadWordTask implements Runnable {
      private String _word;

      public DownloadWordTask(String word) {
         _word = word;
      }

      public void run() {
         Word w = cambridgeLookupService.lookup(_word);
         if (StringUtils.isNotEmpty(w.getDescription()) && StringUtils.isNotEmpty(w.getSoundSource())) {
            String downloadUrl = w.getSoundSource().trim();
            downloadUrl = downloadUrl.replaceAll("playSoundFromFlash\\(\\'", "");
            downloadUrl = downloadUrl.replaceAll("\\', this\\)", "");
            downloadUrl = downloadUrl.trim();
            DownloadUtils.fileDownload(downloadUrl, w.getDescription(), "sound/english/");
         }
      }
   }

   public void setCambridgeLookupService(LookupService cambridgeLookupService) {
      this.cambridgeLookupService = cambridgeLookupService;
   }
}
