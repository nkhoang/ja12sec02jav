
package com.nkhoang.common.xml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;


import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.Attributes;

/**
 * Performs simple schema validation on an XML document
 */
public class Validator {
  
  private static final Log LOG = LogFactory.getLog(Validator.class);
  
  private static final String SAX_PARSER = "javax.xml.parsers.SAXParserFactory";
  private static final String APACHE_SAX_PARSER = "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl";
  
  private static final SchemaFactory SCHEMA_FACTORY; 
    
  static {
    // Handler.init();
    SCHEMA_FACTORY = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
  }
  
  /** Parser factory for validating flows */
  private SAXParserFactory _parserFactory;
  
  public Validator() {
    this(false);
  }
  
  /**
   * @param schema Schema to use for validation of grammar
   */
  public Validator(Schema schema) {
    this(false, schema);
  }
  
  /**
   * @param schemaContents Schema string to use for validation of grammar
   */
  public Validator(String schemaContents) throws SAXException {
    this(false, createSchema(schemaContents));
  }
  
  /**
   * @param schemaFile Schema string to use for validation of grammar
   */
  public Validator(File schemaFile) throws SAXException {
    this(false, SCHEMA_FACTORY.newSchema(schemaFile));
  }
  
  /**
   * @param useApacheSaxParser - Whether or not to use the apache 
   *        xerces sax parser
   */
  public Validator(boolean useApacheSaxParser) { 
    this(useApacheSaxParser, (Schema) null);
  }
  
  /**
   * @param useApacheSaxParser - Whether or not to use the apache 
   *        xerces sax parser
   * @param schemaContents Schema string to use for validation of grammar
   */
  public Validator(boolean useApacheSaxParser, String schemaContents) 
      throws SAXException { 
    this(useApacheSaxParser, createSchema(schemaContents));
  }
  
  /**
   * @param useApacheSaxParser - Whether or not to use the apache 
   *        xerces sax parser
   * @param schemaFile Schema string to use for validation of grammar
   */
  public Validator(boolean useApacheSaxParser, File schemaFile) 
      throws SAXException { 
    this(useApacheSaxParser, SCHEMA_FACTORY.newSchema(schemaFile));
  }
  
  /**
   * @param useApacheSaxParser - Whether or not to use the apache 
   *        xerces sax parser
   * @param schema Schema to use for validation of grammar
   */
  public Validator(boolean useApacheSaxParser, Schema schema) {
    if (useApacheSaxParser) {
      initializeValidator(schema);
    }
    else {
      String originalFactory = System.getProperty(SAX_PARSER);
      System.setProperty(SAX_PARSER, APACHE_SAX_PARSER);
      initializeValidator(schema);
      if (null == originalFactory) {
        System.clearProperty(SAX_PARSER);
      }
      else {
        System.setProperty(SAX_PARSER, originalFactory);
      }
    }
  }
  
  private void initializeValidator(Schema schema) {
    _parserFactory = SAXParserFactory.newInstance();
    _parserFactory.setNamespaceAware(true);
    _parserFactory.setValidating(true);
    setParserFeature("http://xml.org/sax/features/namespaces", true);
    setParserFeature("http://xml.org/sax/features/namespace-prefixes", true);
    setParserFeature("http://xml.org/sax/features/validation", true);
    setParserFeature("http://apache.org/xml/features/validation/schema", true);
    setParserFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
    if (schema != null) {
      _parserFactory.setSchema(schema);
    }
  }
  
  private void setParserFeature(String feature, boolean value) {
    try {
      _parserFactory.setFeature(feature, value);
    } catch (ParserConfigurationException e) {
      LOG.error("Can't set feature " + feature, e);
    } catch (SAXNotRecognizedException e) {
      LOG.warn("Can't set feature " + feature, e);
    } catch (SAXNotSupportedException e) {
      LOG.warn("Can't set feature " + feature, e);
    }
  }

  public void validate(String fileName)
    throws ParserConfigurationException, SAXException, IOException
  {
    validate(fileName, null);
  }
  
  /**
   * Validates an XML document, optionally checking the validity of the
   * namespaces as well.
   *
   * @param fileName XML document to check
   * @param allowedNamespaces if not <code>null</code> all namespaces in the
   *                          document must be in this Set (String), or
   *                          InvalidNamespaceException will be thrown.
   *                          Otherwise, no checking will be done of
   *                          namespaces.
   */
  public void validate(String fileName, Set<String> allowedNamespaces)
    throws ParserConfigurationException, SAXException, IOException
  {
    validate(new InputSource(new FileReader(new File(fileName))), allowedNamespaces);
  }

  public void validate(InputSource document)
    throws ParserConfigurationException, SAXException, IOException
  {
    validate(document, null);
  }
  
  /**
   * Validates an XML document, optionally checking the validity of the
   * namespaces as well.
   *
   * @param document XML document to check
   * @param allowedNamespaces if not <code>null</code> all namespaces in the
   *                          document must be in this Set (String), or
   *                          InvalidNamespaceException will be thrown.
   *                          Otherwise, no checking will be done of
   *                          namespaces.
   */
  public void validate(InputSource document, Set<String> allowedNamespaces)
    throws ParserConfigurationException, SAXException, IOException
  {
    // determine which parse handler to use.  if no allowedNamespaces Set,
    // just use basic ParseHandler, otherwise, use ParseHandlerWithNSChecking.
    ParseHandler handler =
      ((allowedNamespaces == null) ?
       (new ParseHandler()) :
       (new ParseHandlerWithNSChecking(allowedNamespaces)));
    _parserFactory.newSAXParser().parse(document, handler);
    List<Throwable> errors = handler.getErrors();
    if (!errors.isEmpty()) {
      StringBuffer msg = new StringBuffer();
      Iterator<Throwable> iter = errors.iterator();
      while (iter.hasNext()) {
        Throwable e = iter.next();
        if (e instanceof SAXParseException) {
          SAXParseException spe = (SAXParseException) e;
          msg.append("Line: " + spe.getLineNumber());
          msg.append(", Column: " + spe.getColumnNumber() + ", ");
        }
        msg.append(e.getMessage());
        if (iter.hasNext()) {
          msg.append("\n");
        }
      }
      throw new SAXException(msg.toString());
    }
  }
  
  private class ParseHandler extends DefaultHandler {
    
    private List<Throwable> _errors = new ArrayList<Throwable>();
    
    public List<Throwable> getErrors() {
      return _errors;
    }
    
    @Override
    public void error(SAXParseException e) {
      _errors.add(e);
    }
    
    @Override
    public void fatalError(SAXParseException e) {
      _errors.add(e);
    }

  }

  /**
   * Given a Set (String) of allowed namespaces, all elements with namespace
   * attributes are checked as the document is parsed.  If a namespace is
   * found which is not in the list, an InvalidNamespaceException is thrown.
   */
  private class ParseHandlerWithNSChecking extends ParseHandler {
    
    private Set<String> _allowedNamespaces;

    public ParseHandlerWithNSChecking(Set<String> allowedNamespaces) {
      _allowedNamespaces = allowedNamespaces;
    }

    @Override
    public void startElement(String uri, String localName, String qname,
                             Attributes attributes)
      throws SAXException
    {
      // check for any namespace ("xmlns*") attributes and make sure they are
      // allowed.
      for(int i = 0; i < attributes.getLength(); ++i) {
        if(attributes.getQName(i).startsWith("xmlns")) {
          String ns = attributes.getValue(i);
          if(!_allowedNamespaces.contains(ns)) {
            throw new InvalidNamespaceException(ns);
          }
        }
      }
    }
    
  }

  
  /**
   * Thrown to indicate an unexpected namespace was specified in the the
   * document being validated.  By checking this separately, we can more
   * easily determine the source of validation errors.
   */
  public static class InvalidNamespaceException extends SAXException
  {
    private static final long serialVersionUID = 1L;

    public InvalidNamespaceException(String invalidNS) {
      super("The namespace '" + invalidNS + "' is not supported.  This is " +
            "most likely caused by an out-of-date document.");
    }
  }
  
  public static void main(String[] args) throws Exception {
    new Validator().validate(args[0]);
  }
  
  private static Schema createSchema(String schemaContents) throws SAXException {
    InputStream byteStream = IOUtils.toInputStream(schemaContents);
    Source source = new StreamSource(byteStream);
    Schema schema = SCHEMA_FACTORY.newSchema(source);
    return schema;
  }
  
}
