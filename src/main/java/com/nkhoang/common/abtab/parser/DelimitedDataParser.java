// Copyright (c) 2005 Health Market Science, Inc.

package com.nkhoang.common.abtab.parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.nkhoang.common.abtab.RawDataParseException;
import com.nkhoang.common.abtab.RawDataParser;
import com.nkhoang.common.abtab.handler.RawDataHandler;
import com.nkhoang.common.util.parser.DelimitedLineParser;
import com.nkhoang.common.util.parser.LineParseException;


/**
 * Parses delimited data (e.g. tab-delimited files).
 * @author Tim McCune
 */
public class DelimitedDataParser extends LineParser implements RawDataParser {
  
  private static final long serialVersionUID = 3825072006471673205L;
  
  /** Token delimiter */
  protected char _delimiter = '\t';
  //Properties on DelimitedLineParser
  private char[] _commentChars;
  private Character _escapeQuote;
  private Character _quote;
  /** iff <code>true</code>, allow fewer values in a row than the expected
      number of columns */
  private boolean _allowTooFewValues;
  /** iff <code>true</code>, allow more values in a row than the expected
      number of columns */
  private boolean _allowTooManyValues;
  private boolean _escapeCrlf;
  private boolean _escapeDelimiter;

  private Boolean _trimTokens;
  private Boolean _removeNonAscii;
  
  /**
   * Manually-defined list of field names to use, typically because they don't
   * appear in the first row of the file.
   */ 
  private List<String> _fields = new LinkedList<String>();
  
  public void setEscapeDelimiter(boolean escapeDelimiter){
    _escapeDelimiter = escapeDelimiter;
  }

  public boolean getEscapeDelimiter() {
    return _escapeDelimiter;
  }

  public void setEscapeCrlf ( boolean escapeCrlf ){
      _escapeCrlf = escapeCrlf;
  }
  
  public boolean getEscapeCrlf () {
      return _escapeCrlf;
  }

  public void setDelimiter(char c) {
    _delimiter = c;
  }
  public char getDelimiter() {
    return _delimiter;
  }
  
  public void setCommentChars(char[] c) {
    _commentChars = c;
  }
  public void setCommentChars(String chars) {
    _commentChars = chars.toCharArray();
  }
  public String getCommentChars() {
    if(_commentChars != null) {
      return new String(_commentChars);
    }
    return null;
  }
  
  public void setEscapeQuote(char c) {
    _escapeQuote = c;
  }
  public Character getEscapeQuote() {
    return _escapeQuote;
  }
  
  public void setQuote(Character c) {
    _quote = c;
  }
  public Character getQuote() {
    return _quote;
  }
  
  public void addField(String field) {
    _fields.add(field);
  }
  public void addFields(String... fields) {
    for (String field : fields) {
      addField(field);
    }
  }
  public List<String> getFields() {
    return _fields;
  }

  public boolean getAllowTooFewValues() {
    return _allowTooFewValues;
  }

  public void setAllowTooFewValues(boolean newAllowTooFewValues) {
    _allowTooFewValues = newAllowTooFewValues;
  }
  
  public boolean getAllowTooManyValues() {
    return _allowTooManyValues;
  }

  public void setAllowTooManyValues(boolean newAllowTooManyValues) {
    _allowTooManyValues = newAllowTooManyValues;
  }
  
  /** @see com.*****.common.util.parser.DelimitedLineParser#setTrimTokens */
  public void setTrimTokens(Boolean b) {
    _trimTokens = b;
  }

  public Boolean getTrimTokens() {
    return _trimTokens;
  }
  
  /** @see com.*****.common.util.parser.DelimitedLineParser#setRemoveNonAscii */
  public void setRemoveNonAscii(Boolean b) {
    _removeNonAscii = b;
  }

  protected void doParse(DelimitedLineParser parser, DelimitedDataListener listener, RawDataHandler handler) 
    throws RawDataParseException, LineParseException, IOException
  {
    if (_tableName == null) {
      throw new RawDataParseException(
          "Could not imply table name.  You must explicitly set it.");
    }
    if (_commentChars != null) { parser.setCommentChars(_commentChars); }
    if (_escapeQuote != null) { parser.setEscapeQuote(_escapeQuote); }
    if (_trimTokens != null) { parser.setTrimTokens(_trimTokens); }
    if (_removeNonAscii != null) { parser.setRemoveNonAscii(_removeNonAscii); }
    if (_escapeCrlf) { parser.setEscapeCrlf(_escapeCrlf); }
    if (_escapeDelimiter) { parser.setEscapeDelimiter(_escapeDelimiter); }
    parser.setQuote(_quote);
    setParserProperties(parser);
    parser.parse();
    // only call endTable if we called startTable
    if(!listener._needStartTable) {
      handler.endTable();
    }
  }

  //Javadoc copied from RawDataParser
  @Override
  public void doParse(RawDataHandler handler, InputStream input)
  throws RawDataParseException
  {
    try {
      DelimitedDataListener listener = new DelimitedDataListener(handler);
      DelimitedLineParser parser = new DelimitedLineParser(Channels.newChannel(
          input), listener, _delimiter);
      doParse (parser, listener, handler);
    } catch (LineParseException e) {
      throw new RawDataParseException(e, null, e.getLineNumber(), null);
    } catch (IOException e) {
      throw new RawDataParseException(e);
    }
  }

  protected class DelimitedDataListener extends Listener {
    
    private boolean _needColumnNames = _fields.isEmpty();
    private int _numColumns;
    private boolean _needStartTable = true;
    
    public DelimitedDataListener(RawDataHandler handler) {
      super(handler);
    }

    @Override
    public boolean endLine() throws LineParseException {
      boolean rtn = true;
      try {
        if (_needStartTable) {
          RawMetadata md = new RawMetadata();
          Iterator<? extends CharSequence> iter;
          if (_needColumnNames) {
            _numColumns = _tokens.size();
            iter = _tokens.iterator();
          } else {
            _numColumns = _fields.size();
            iter = _fields.iterator();
          }
          md.setColumnCount(_numColumns);
          int index = 1;
          while (iter.hasNext()) {
            md.setColumnName(index++, iter.next().toString());
          }
          _handler.startTable(_tableName, md);
        }
        if (!_needColumnNames) {
          if(_numColumns != _tokens.size()) {
            if(_numColumns < _tokens.size()) {
              // too many values in row
              String msg = 
                "Too many values for row, expected " + _numColumns +
                ", found  " + _tokens.size();
              if(!_allowTooManyValues) {
                // user isn't allowing this to happen
                throw new LineParseException(msg);
              } else {
                _handler.addWarning(msg);
              }

              // we should always return the right amount of values, so update
              // the list appropriately
              while(_numColumns < _tokens.size()) {
                _tokens.remove(_tokens.size() - 1);
              }
              
            } else {
              
              // too few values in row
              String msg = 
                "Too few values for row, expected " + _numColumns +
                ", found  " + _tokens.size();
              if(!_allowTooFewValues) {
                // user isn't allowing this to happen
                throw new LineParseException(msg);
              } else {
                _handler.addWarning(msg);
              }
              
              // we should always return the right amount of values, so update
              // the list appropriately
              while(_numColumns > _tokens.size()) {
                _tokens.add(null);
              }
              
            }
          }
          rtn = _handler.addRow(_tokens);
        }
      } catch (RawDataParseException e) {
        throw new LineParseException(e);
      }
      initTokens();
      _needColumnNames = false;
      _needStartTable = false;
      return rtn;
    }
  }
  
}
