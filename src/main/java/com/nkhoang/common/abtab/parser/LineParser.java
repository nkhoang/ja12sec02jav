//Copyright (c) 2005 Health Market Science, Inc.
package com.nkhoang.common.abtab.parser;

import com.nkhoang.common.abtab.RawDataParseException;
import com.nkhoang.common.abtab.handler.RawDataHandler;
import com.nkhoang.common.util.parser.LineParseException;
import com.nkhoang.common.util.parser.ParseListener;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class for parsers that parse lines of text.  
 * @author Tim McCune
 */
public abstract class LineParser extends RawDataStreamParser
  implements SimpleParser, TextParser
{
  
  private static final Pattern PAT_TABLE_NAME = Pattern.compile(".*?(\\w+)\\.\\w+(\\.gz)?");

  /** Table name to pass to the RawDataHandler. */
  protected String _tableName;
  protected Charset _charset;
  protected Integer _blockSize;
  protected Integer _startLineNumber;
  protected String _startAt;
  protected String _startAfter;
  
  public void setTableName(String tableName) {
    _tableName = tableName;
  }
  public String getTableName() {
    return _tableName;
  }

  public void setBlockSize(int blockSize) {
    _blockSize = blockSize;
  }

  public void setCharset(Charset charset) {
    _charset = charset;
  }

  public Charset getCharset() {
    return _charset;
  }

  public void setCharsetName(String charset) {
    _charset = Charset.forName(charset);
  }

  public String getCharsetName() {
    return((_charset != null) ? _charset.name() : null);
  }
  
  public void setStartAt(String startAt) {
    _startAt = startAt;
  }

  public String getStartAt() {
    return _startAt;
  }
  
  public void setStartAfter(String startAfter) {
    _startAfter = startAfter;
  }

  public String getStartAfter() {
    return _startAfter;
  }  
  
  public void setStartLineNumber(Integer number) {
    _startLineNumber = number;
  }

  public Integer getStartLineNumber() {
    return _startLineNumber;
  }
  
  protected void setParserProperties(com.nkhoang.common.util.parser.LineParser parser) {
    if (_charset != null) { parser.setCharset(_charset); }
    if (_blockSize != null) { parser.setBlockSize(_blockSize); }
    if (_startLineNumber != null) { parser.setStartLineNumber(_startLineNumber); }
    if (_startAt != null) { parser.setStartAt(_startAt); }
    if (_startAfter != null) { parser.setStartAfter(_startAfter); }
    parser.setGenerateWarnings(true);
  }
  
  @Override
  public void parse(RawDataHandler handler, URI input)
  throws RawDataParseException
  {
    setTableNameFromUrl(((input != null) ? input.getPath() : null), false);
    super.parse(handler, input);
  }
  
  /**
   * @deprecated use {@link #setTableNameFromUrl} instead
   */
  @Deprecated
  public void inferTableNameFrom(URI input) {
    if(input != null) {
      setTableNameFromUrl(input.getPath(), false);
    }
  }

  /**
   * @deprecated use {@link #setTableNameFromUrl} instead
   */
  @Deprecated
  public void inferTableNameFrom(String name) {
    setTableNameFromUrl(name, true);
  }

  public void setTableNameFromUrl(String urlStr, boolean override) {
    if(!override && (_tableName != null)) {
      return;
    }
    if(urlStr != null) {
      Matcher m = PAT_TABLE_NAME.matcher(urlStr);
      if (m.matches()) {
        _tableName = m.group(1);
      }
    }
  }
  
  protected abstract class Listener implements ParseListener {
    
    protected List<CharSequence> _tokens;
    protected RawDataHandler _handler;
    
    public Listener(RawDataHandler handler) {
      _handler = handler;
      initTokens();
    }
    
    public void nextToken(CharSequence token) {
      _tokens.add(token.toString());
    }
    
    public void initTokens() {
      _tokens = new ArrayList<CharSequence>();
    }
    
    public void startData() {}
    public void endData() {}
    
    public abstract boolean endLine() throws LineParseException;

    public void addWarning(int lineNumber, String msg)
      throws LineParseException
    {
      try {
        _handler.addWarning("Line " + lineNumber + ": " + msg);
      } catch(RawDataParseException e) {
        throw new LineParseException(e);
      }
    }
    
  }

}
