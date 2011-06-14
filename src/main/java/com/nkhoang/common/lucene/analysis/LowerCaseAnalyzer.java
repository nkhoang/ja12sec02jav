package com.nkhoang.common.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Version;

/**
 * Simple Analyzer that just lower cases the input.
 * No tokenizing is done
 * 
 * Example:
 * "TEST" -> "test"
 * "THIS IS A TEST" -> "this is a test"
 * 
 *
 */
public final class LowerCaseAnalyzer extends Analyzer {

  @Override
  public TokenStream tokenStream(String fieldName, Reader reader) {

    TokenStream result = new LowerCaseFilter(Version.LUCENE_31, new KeywordTokenizer(reader));

    return result;
  }

}
