package com.nkhoang.common.lucene.analysis;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSetMetaData;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.wordnet.SynonymMap;

import com.*****.common.util.resource.Handler;
import com.*****.common.abtab.RawDataHandler;
import com.*****.common.abtab.RawDataParseException;
import com.*****.common.abtab.RawDataParser;
import com.*****.common.abtab.handler.AbstractRawDataHandler;
import com.*****.common.abtab.parser.DelimitedDataParser;


public class InMemorySynonymEngine extends SynonymMap{

  private Map<String, Set<String>> _synonymMap;
  static {
    Handler.init();
  }
    
  public InMemorySynonymEngine(String synonymURL) throws IOException {
    super(null);
    DelimitedDataParser p = new DelimitedDataParser();
    // The name is not really used for anything other than debugging
    // in this instance.
    p.setTableName("synonym_map");
    try {
      init(p, new URI(synonymURL));
    } catch (RawDataParseException ex) {
      throw new RuntimeException("Bad nickname source data: " + synonymURL, ex);
    } catch (URISyntaxException ex) {
      throw new RuntimeException("Bad URI: " + synonymURL, ex);
    }
  }
  
  private void init(RawDataParser p, URI uri) throws RawDataParseException {
    RawDataHandler h = new AbstractRawDataHandler() {
      private Map<String, Set<String>> _synMap;
      public boolean addRow(List<?> row) throws RawDataParseException {
        if (row.size() < 2) {
          throw new RawDataParseException("Could not parse row of length: " + row.size());
        }
        String org = ((String)row.get(0)).toLowerCase();
        String synonym = ((String)row.get(1)).toLowerCase();
        if (!_synMap.containsKey(org)) {
          _synMap.put(org, new HashSet<String>());
        }
        _synMap.get(org).add(synonym);
        return true;
      }

      public void setUp() {}
      public void startTable(String tableName, ResultSetMetaData rsmd) {
        _synMap = new HashMap<String, Set<String>>();
      }
      public void endTable() {
        InMemorySynonymEngine.this._synonymMap = Collections
                .unmodifiableMap(this._synMap);
      }
      public void tearDown() {}
    };
    
    p.parse(h, uri);
  }
  
  
  public String[] getSynonyms(String original) {
    @SuppressWarnings("unchecked")
    Set<String> temp = (_synonymMap.containsKey(original.toLowerCase()) 
      ? _synonymMap.get(original.toLowerCase())
      : Collections.EMPTY_SET);
    if(temp == null) {
      return null;
    }
    String [] result = new String [temp.size()];
    temp.toArray(result);
    return result;
  }
 
  
}
