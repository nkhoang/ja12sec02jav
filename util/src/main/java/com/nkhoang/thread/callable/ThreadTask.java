package com.nkhoang.thread.callable;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadTask implements Callable<Integer> {
	private static Logger LOG = LoggerFactory.getLogger(ThreadTask.class);
	private String _id;
	private String _threadName;


	public ThreadTask(String taskId, String threadName) {
		_id = taskId;
		_threadName = threadName;
	}


	public Integer call() throws Exception {
		LOG.info(
			"Process task " + _threadName + " - " + _id + " with thread: " + Thread.currentThread().getName() + "...");
		Thread.sleep(2000);
		return 0;
	}

}
