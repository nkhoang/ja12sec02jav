package com.nkhoang.common.xml;

import java.io.*;
import java.util.*;

import org.w3c.dom.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import javax.xml.parsers.*;

/**
 * This class is an adapter between DOM and SAX api's so that
 * you can use a DOM parser to parse something once, and then
 * do some processing like datamap or other xpath stuff and
 * then feed the result DOM through a SAX DocumentHandler without
 * having to write the xml back out to a stream and reparse it.
 *
 * @see com.hmsonline.common.xml.DOMInputSource
 * @see org.w3c.dom.Node
 * @see org.xml.sax.XMLReader
 */
public class DOMXMLReader implements XMLReader {

	private static final Set<String> SUPPORTED_FEATURES = new HashSet<String>();

	static {
		SUPPORTED_FEATURES.add("http://xml.org/sax/features/namespaces");
		SUPPORTED_FEATURES.add("http://xml.org/sax/features/namespace-prefixes");
	}

	private ContentHandler _contentHandler;

	/** the document we get from the input source */
	protected Node _document;

	/** The error handler */
	private ErrorHandler _errHandler;

	/** The entity resolver */
	private EntityResolver _entityResolver;

	private XMLReader _nonDOMReader;

	/**
	 * Create a reader that will use the default system SAX parser if the
	 * input source to parse is not a DOMInputSource
	 */
	public DOMXMLReader() {
		try {
			_nonDOMReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
		}
		catch (ParserConfigurationException e) {
			throw new XMLParsingException(e);
		}
		catch (SAXException e) {
			throw new XMLParsingException(e);
		}
	}

	/**
	 * Create a reader that will use a custom SAX XMLReader if the input source
	 * to parse is not a DOMInputSource
	 *
	 * @param nonDOMReader Reader to use for other input source types
	 */
	public DOMXMLReader(XMLReader nonDOMReader) {
		_nonDOMReader = nonDOMReader;
	}

	public void runParse() throws SAXException {
		if (_document != null && _contentHandler != null) {
			_contentHandler.setDocumentLocator(new DOMLocator());
			_contentHandler.startDocument();
			parseNode(_document);
		}
	}

	private void parseNode(Node n) throws SAXException {
		String name = n.getNodeName();
		int type = n.getNodeType();
		switch (type) {
			case Node.ELEMENT_NODE:

				_contentHandler.startElement(
					nonNull(n.getNamespaceURI()), nonNull(n.getLocalName()), name,
					domNodeListToAttributes(n.getAttributes()));
				parseNodeList(n.getChildNodes());

				_contentHandler.endElement(
					nonNull(n.getNamespaceURI()), nonNull(n.getLocalName()), name);

				break;
			case Node.CDATA_SECTION_NODE:
			case Node.TEXT_NODE:
				// tell the sax parser to do some chars
				try {
					String s = ((CharacterData) n).getData();
					if (s.trim().equals("")) {
						_contentHandler.ignorableWhitespace(s.toCharArray(), 0, s.length());
					} else {
						_contentHandler.characters(s.toCharArray(), 0, s.length());
					}
				}
				catch (DOMException e) {
					throw new SAXException(e);
				}
				break;
			case Node.PROCESSING_INSTRUCTION_NODE:
				String t = ((ProcessingInstruction) n).getTarget();
				String d = ((ProcessingInstruction) n).getData();
				_contentHandler.processingInstruction(t, d);
				break;
			default:
				// unhandled by sax, do nothing
		}
	}

	public void parseNodeList(NodeList nodes) throws SAXException {
		int len = nodes.getLength();
		for (int i = 0; i < len; i++) {
			parseNode(nodes.item(i));
		}
	}

	public static Attributes domNodeListToAttributes(NamedNodeMap nnm) {

		AttributesImpl rtn = new AttributesImpl();

		if (nnm != null) {
			Attr a;
			int len = nnm.getLength();
			for (int i = 0; i < len; i++) {
				Node n = nnm.item(i);
				if (n instanceof Attr) {
					a = (Attr) n;
					rtn.addAttribute(
						nonNull(a.getNamespaceURI()), nonNull(a.getLocalName()), a.getName(), "CDATA", a.getValue());
				}
			}
		}
		return rtn;
	}

	public class DOMLocator implements Locator {
		public int getColumnNumber() {
			return -1;
		}

		public int getLineNumber() {
			return -1;
		}

		public String getPublicId() {
			return "";
		}

		public String getSystemId() {
			return "";
		}
	}

	private void setupInputSource(DOMInputSource source) throws ParserConfigurationException {
		if (source.getDocumentBuilder() == null) {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			if (_entityResolver != null) {
				docBuilder.setEntityResolver(_entityResolver);
			}
			if (_errHandler != null) {
				docBuilder.setErrorHandler(_errHandler);
			}
			source.setDocumentBuilder(docBuilder);
		}
	}

	// BEGIN XMLReader Methods

	// Javadoc copied from XMLReader
	public boolean getFeature(String name) {
		return SUPPORTED_FEATURES.contains(name);
	}

	// Javadoc copied from XMLReader
	public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
		_nonDOMReader.setFeature(name, value);
	}

	// Javadoc copied from XMLReader
	public Object getProperty(String name) throws SAXNotRecognizedException {
		throw new SAXNotRecognizedException("Sorry Charlie");
	}

	// Javadoc copied from XMLReader
	public void setProperty(String name, Object value) throws SAXNotRecognizedException {
		throw new SAXNotRecognizedException("Sorry Charlie");
	}

	// Javadoc copied from XMLReader
	public void setEntityResolver(EntityResolver resolver) {
		_entityResolver = resolver;
	}

	// Javadoc copied from XMLReader
	public EntityResolver getEntityResolver() {
		return _entityResolver;
	}

	// Javadoc copied from XMLReader
	public void setDTDHandler(DTDHandler handler) {
	}

	// Javadoc copied from XMLReader
	public DTDHandler getDTDHandler() {
		return null;
	}

	// Javadoc copied from XMLReader
	public void setContentHandler(ContentHandler handler) {
		_contentHandler = handler;
	}

	// Javadoc copied from XMLReader
	public ContentHandler getContentHandler() {
		return _contentHandler;
	}

	// Javadoc copied from XMLReader
	public void setErrorHandler(ErrorHandler handler) {
		_errHandler = handler;
	}

	// Javadoc copied from XMLReader
	public ErrorHandler getErrorHandler() {
		return _errHandler;
	}

	// Javadoc copied from XMLReader
	public void parse(InputSource source) throws SAXException, IOException {
		if (source instanceof DOMInputSource) {
			try {
				setupInputSource((DOMInputSource) source);
			}
			catch (ParserConfigurationException e) {
				throw new SAXException(e);
			}
			Node doc = ((DOMInputSource) source).getDocument();
			if (doc != null) {
				_document = doc;
				runParse();
			} else {
				throw new SAXException("No document found");
			}
		} else {
			_nonDOMReader.parse(source);
		}
	}

	// Javadoc copied from XMLReader
	public void parse(String systemId) throws SAXException, IOException {
		_nonDOMReader.parse(systemId);
	}

	private static String nonNull(String s) {
		if (s == null) {
			return "";
		} else {
			return s;
		}
	}

}

