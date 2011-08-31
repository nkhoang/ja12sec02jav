package com.nkhoang.thread;

import java.util.concurrent.Future;

public class ThreadData {
	private String          _id;
	private String          _threadName;
	private Future<Integer> _future;


	public ThreadData(String id, String threadName, Future<Integer> future) {
		_id = id;
		_future = future;
		_threadName = threadName;
	}


	public Future<Integer> getFuture() {
		return _future;
	}


	public String getId() {
		return _id;
	}


	public String getThreadName() {
		return _threadName;
	}
};