package com.nkhoang.gae.utils;

import org.junit.Test;

public class MailUtilsTest {
	@Test
	public void testSendMail() throws Exception{
		     MailUtils.sendMail("<h1>Hello from Test</h1>","nkhoang.it@gmail.com", "Hello from Test", "nkhoang.it@gmail.com");
	}
}
