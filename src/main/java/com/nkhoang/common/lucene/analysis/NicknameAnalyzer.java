package com.nkhoang.common.lucene.analysis;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public final class NicknameAnalyzer extends SynonymAnalyzer {
  private static final Log LOG = LogFactory.getLog(NicknameAnalyzer.class
          .getName());
  private static final String NICK_NAMES_RESOURCE = 
    "resource://com/nkhoang/common/lucene/analysis/nicknames.tab";

  private static InMemorySynonymEngine _inMemorySynonymEngine;
  
  static {
    try {
      _inMemorySynonymEngine = new InMemorySynonymEngine(NICK_NAMES_RESOURCE); 
    } catch (Exception e) {
      LOG.error("Unable to construct an in-memory synonym engine from "
              + NICK_NAMES_RESOURCE, e);
    }
  }

  public NicknameAnalyzer() {
    super(_inMemorySynonymEngine);
  }

}
