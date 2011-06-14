//Copyright (c) 2005 Health Market Science, Inc.
package com.nkhoang.common.abtab.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.zip.GZIPInputStream;

import com.nkhoang.common.abtab.RawDataParseException;
import com.nkhoang.common.abtab.RawDataParser;
import com.nkhoang.common.abtab.handler.RawDataHandler;
import com.nkhoang.common.util.IOUtil;
import org.apache.commons.compress.bzip2.CBZip2InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base RawDataParser implementation whose subclasses can handle input
 * streams.  This parser supports compressed sources.
 * @author Tim McCune
 */
public abstract class RawDataStreamParser implements RawDataParser {
  
  private static final Log LOG = LogFactory.getLog(RawDataStreamParser.class);
  
  private enum CompressionType {gzip, bzip2, none}
  
  private CompressionType _compression;
  protected URI _inputURI;
  
  public void setCompression(String type) {
    _compression = Enum.valueOf(CompressionType.class, type);
  }
  
  public String getCompression() {
    return _compression != null ? _compression.name() : null;
  }
   
  //Javadoc copied from RawDataParser
  public void parse(RawDataHandler handler, URI input)
  throws RawDataParseException
  {
    _inputURI = input;
    InputStream stream = null;
    try {
      parse(handler, (stream = input.toURL().openStream()));
    } catch (IOException e) {
      throw new RawDataParseException(e);
    } finally {
      IOUtil.closeQuietly(stream);
    }
  }
  
  public void parse(RawDataHandler handler, InputStream in)
  throws RawDataParseException
  {
    try {
      //  Use BufferedInputStream to ensure that mark and reset are supported.
      InputStream input = new BufferedInputStream(in);
      input.mark(8096);
      if (_compression != CompressionType.none) {
        if (_compression == CompressionType.gzip ||
            (_compression == null && ParserFactory.isGZip(input)))
        {
          input = new GZIPInputStream(input);
        } else if (_compression == CompressionType.bzip2 ||
            (_compression == null && ParserFactory.isBZip(input)))
        {
          // The brilliant Jakarta library needs the first 2
          // characters "BZ" stripped off or else it throws
          // an NPE (as of version 20040530.)
          input.read();
          input.read();
          input = new CBZip2InputStream(input);
        }
      }
      doParse(handler, input);
    } catch (IOException e) {
      throw new RawDataParseException(e);
    }
  }
  
  /**
   * @param handler Handler with callback methods that implementations call into.
   * @param input Stream source of the data.
   */
  public abstract void doParse(RawDataHandler handler, InputStream input)
  throws RawDataParseException;
  
}
