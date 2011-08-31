
package com.nkhoang.common.xml;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
* This is a special SAX InputSource which can also wrap a DOM
* Document instead of a normal sax stream
*
* @see org.w3c.dom.Node
* @see org.xml.sax.InputSource
* @see com.hmsonline.common.xml.DOMXMLReader
*/
public class DOMInputSource extends InputSource {
  /**
  * The DOM Document
  */
  protected Node _document;
  
  /**
  * A DocumentBuilder to use if neccessary
  */
  protected DocumentBuilder _docBuilder;
  
  public DOMInputSource() {
    super();
  }
  
  /**
  * constructor that takes a DOM Node
  * 
  * @param n Node which should be a Document 
  * @exception org.xml.sax.SAXException thrown if the Node given is null
  */
  public DOMInputSource(Node n) throws SAXException {
    super();
    setDocument(n);
  }
  
  /**
  * Constructor for InputStream data
  */
  public DOMInputSource(InputStream byteStream) {
    super(byteStream);
  }
  
  public DOMInputSource(Reader characterStream) {
    super(characterStream);
  }
  
  public DOMInputSource(String systemId) {
    super(systemId);
  }
  
  public void setDocumentBuilder(DocumentBuilder docBuilder) {
    _docBuilder = docBuilder;
  }
  
  public DocumentBuilder getDocumentBuilder() {
    return _docBuilder;
  }
  
  
  /**
  * @return The document node.  This is not necessarily a Document object,
  * 	just the root of the document that we're parsing.
  */
  public Node getDocument() {
    if (_document == null) {
      try {
        if (_docBuilder == null) {
          _document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(getByteStream());
        }
        else {
          _document = _docBuilder.parse(getByteStream());
        }
      }
      catch (ParserConfigurationException e) {
        throw new XMLParsingException(e);
      }
      catch (IOException e) {
        throw new XMLParsingException(e);
      }
      catch (SAXException e) {
        throw new XMLParsingException(e);
      }
    }
    
    return _document;
  }
  
  /**
  * Sets the document to the given node.
  * @exception org.xml.sax.SAXException if the node is null.
  */
  
  public void setDocument(Node n) 
  throws SAXException
  {
    if (n != null) {
      _document = n;
    }
    else {
      throw new SAXException("DOMInputSource.setDocument() node is null");
    }	
    
  }
  
  /**
  * here we have a special deal to convert DOM's to an xml input stream.
  * This is useful if you have a DOM and want to feed it to a SAX parser
  * easily. Though you should really use the DOMXMLReader if you want
  * to do something like that.
  * @see DOMXMLReader
  */
  @Override
  public InputStream getByteStream() 
  {	
    InputStream byteStream = super.getByteStream();
    if (byteStream == null) {
      try {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(os);
        TransformerFactory.newInstance().newTransformer().transform(
        new DOMSource(_document), new StreamResult(writer));
        os.flush();
        
        byte[] rawxml = os.toByteArray();
        byteStream = new ByteArrayInputStream(rawxml);
        setByteStream(byteStream);
      }
      catch (Throwable t) {
        return null;
      }
    }
    return byteStream;
    
  }
  
}
