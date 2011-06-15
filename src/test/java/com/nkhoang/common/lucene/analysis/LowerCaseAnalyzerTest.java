package com.nkhoang.common.lucene.analysis;


import java.io.Reader;
import java.io.StringReader;

import com.nkhoang.common.lucene.analysis.LowerCaseAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.junit.Test;

public class LowerCaseAnalyzerTest {

	@Test
	public void testLowerCaseNoSpace() throws Exception {
		LowerCaseAnalyzer analyzer = new LowerCaseAnalyzer();
		Reader reader = new StringReader("TEST");
		TokenStream tokens = analyzer.tokenStream("", reader);

		while (!tokens.incrementToken()) ;

		//TODO: fix deprecation
		//    Token token = tokens.next();
		//    assertEquals("test", token.term());
		//    assertNull(tokens.next());
	}

	@Test
	public void testLowerCaseWithSpace() throws Exception {
		LowerCaseAnalyzer analyzer = new LowerCaseAnalyzer();
		Reader reader = new StringReader("THIS IS A TEST");
		TokenStream tokens = analyzer.tokenStream("", reader);

		while (!tokens.incrementToken()) ;

		//TODO: fix deprecation
		//    Token token = tokens.next();
		//    assertEquals("this is a test", token.term());
		//    assertNull(tokens.next());
	}
}
