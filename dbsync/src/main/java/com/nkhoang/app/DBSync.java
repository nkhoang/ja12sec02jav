package com.nkhoang.app;

import com.nkhoang.common.FileUtils;
import com.nkhoang.service.WordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class DBSync {

  private static final int THREAD_POOL_QUEUE_SIZE = 60;
  private static final int THREAD_POOL_KEEP_ALIVE_TIME = 0;
  private static final int THREAD_POOL_MAX_SIZE = 15;
  private static final int THREAD_POOL_CORE_SIZE = 15;
  private static final int STARTING_INDEX = 3400;


  private static final Logger LOGGER = LoggerFactory.getLogger(WordService.class.getCanonicalName());
  private static List<String> wordList;
  private static int wordIndex = 0;
  private static WordService wordService;

  public static void main(String[] args) {
    ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
    wordService = ctx.getBean(WordService.class);
    URL fileUrl = Thread.currentThread().getContextClassLoader().getResource("word-list.txt");
    wordList = FileUtils.readWordsFromFile(fileUrl.getPath());
    ExecutorService executor = new ThreadPoolExecutor(
        THREAD_POOL_CORE_SIZE, THREAD_POOL_MAX_SIZE, THREAD_POOL_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
        new ArrayBlockingQueue<Runnable>(THREAD_POOL_QUEUE_SIZE), new ThreadPoolExecutor.CallerRunsPolicy());
    List<Future<Object>> futurePool = new ArrayList<Future<Object>>();

    for (int i = STARTING_INDEX; i < wordList.size(); i++) {
      final int wordIndex = i;
      futurePool.add(executor.submit(new Callable<Object>() {
        public Object call() throws Exception {
          String w = wordList.get(wordIndex);
          LOGGER.info("Processing word: " + w);
          wordService.query(w);
          return null;
        }
      }));

      if (futurePool.size() > 10) {
        for (Future<Object> future : futurePool) {
          try {
            future.get();
          } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.info("Shutdown...");
            executor.shutdown();
            System.exit(1);
          }
        }
      }
    }

  }
}
