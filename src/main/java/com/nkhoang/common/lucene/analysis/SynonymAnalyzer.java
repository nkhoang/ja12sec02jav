package com.nkhoang.common.lucene.analysis;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Version;
import org.apache.lucene.wordnet.SynonymMap;
import org.apache.lucene.wordnet.SynonymTokenFilter;

public class SynonymAnalyzer extends Analyzer {

  private SynonymMap _synonym;
  
  public SynonymAnalyzer(SynonymMap engine) {
    _synonym = engine;
  }


  @Override
  public TokenStream tokenStream(String fieldName, Reader reader) {

    TokenStream result = new SynonymTokenFilter(new LowerCaseFilter(Version.LUCENE_31, new KeywordTokenizer(reader)), _synonym, Integer.MAX_VALUE - 1);

    return result;
  }

}
