package com.nkhoang.gae.test;

import org.apache.poi.util.SystemOutLogger;
import org.junit.Test;

public class StringTest {
	@Test
	public void testStringReplacement() {
		String s = "?";
		if (s.contains("?")) {
			System.out.println(s.replace("?", "\\?"));
		}
	}
}
