package com.nkhoang.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nkhoang.thread.callable.ThreadTask;


public class JavaThread {
	private int _number;

	private static final int             THREAD_POOL_QUEUE_SIZE      = 60;
	private static final int             THREAD_POOL_KEEP_ALIVE_TIME = 0;
	private static final int             THREAD_POOL_MAX_SIZE        = 15;
	private static final int             THREAD_POOL_CORE_SIZE       = 15;
	private static       Logger LOG = LoggerFactory.getLogger(JavaThread.class);
	private static       ExecutorService _executor                   = new ThreadPoolExecutor(
		THREAD_POOL_CORE_SIZE, THREAD_POOL_MAX_SIZE, THREAD_POOL_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
		new ArrayBlockingQueue<Runnable>(THREAD_POOL_QUEUE_SIZE), new ThreadPoolExecutor.CallerRunsPolicy());

	public JavaThread(int i) {
		_number = i;
	}



	public void startUpdateThreadPool(int number) {
		List<ThreadData> threadData = new ArrayList<ThreadData>();
		for (int i = 0; i < number; i++) {
			String taskName = Thread.currentThread().getName() + "_" + i;
			LOG.info("[" + Thread.currentThread().getName() + "] adding task [" + taskName + "]");
			ThreadTask task = new ThreadTask(taskName, Thread.currentThread().getName());

			threadData.add(new ThreadData(taskName, Thread.currentThread().getName(), _executor.submit(task)));

			if (threadData.size() % 200 == 0) {
				LOG.info("Thread data size reach. Checking futures...");
				checkFutures(threadData);
			}
		}
	}


	public void checkFutures(List<ThreadData> data) {
		for (ThreadData i : data) {
			try {
				Integer result = i.getFuture().get();
				LOG.info("Returned result from Tread [" + i.getThreadName() + "] | task [" + i.getId() + "] ok.");
			}
			catch (ExecutionException exe) {
				LOG.error("Failed to get result from task: " + i.getId());
			}
			catch (InterruptedException ite) {
				LOG.error("Failed to get result from task: " + i.getId());
			}
		}
	}

}
