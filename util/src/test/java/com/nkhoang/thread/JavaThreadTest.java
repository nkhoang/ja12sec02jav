package com.nkhoang.thread;

import org.junit.Test;


public class JavaThreadTest {

	@Test
	public void testMultiRequest() throws Exception {
		new Thread(
			new Runnable() {

				public void run() {
					new JavaThread(400).startUpdateThreadPool(400);
				}
			}).start();

		new Thread(
			new Runnable() {

				public void run() {
					new JavaThread(500).startUpdateThreadPool(400);
				}
			}).start();

		new Thread(
			new Runnable() {

				public void run() {
					new JavaThread(600).startUpdateThreadPool(400);
				}
			}).start();

		Thread.sleep(10000L);
	}
}
