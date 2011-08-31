package com.nkhoang.common.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpUtils;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.servlet.http.HttpServletResponse;

import com.nkhoang.common.StringUtil;
import com.nkhoang.common.collections.ArrayUtil;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.io.IOUtils;

import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSModel;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.NameValuePair;


/**
 * Utilities for working with XML text, including parsing that text into a DOM
 * and generating new text from a DOM. For utilities that manipulate the DOM
 * directly, look at {@link DOMUtil}.
 */
public class XMLUtil {

	// some standard error messages
	private final static String ERR_CANT_CONFIGURE = "can't configure parser";
	private final static String ERR_CANT_PARSE     = "can't parse";
	private final static String ERR_CANT_PARSE_SCHEMA = "can't parse as schema";
	private final static String ERR_CANT_VALIDATE  = "can't validate instance document";
	private final static String ERR_CANT_SERIALIZE = "can't serialize";
	private final static String ERR_CANT_READ_URL  = "can't read url";

	// default pretty-print indentation
	private static final int PRETTY_PRINT_INDENTATION = 2;


	/**
	 * The first thing that appears in an XML document. Note that this constant
	 * assumes that you will be using the default platform character set, which
	 * is "ISO-8859-1" for US Java installations. If you are generating XML with
	 * arbitrary Unicode content, you should build a DOM and use <code>javax.xml
	 * </code> to serialize.
	 */
	public final static String XML_DECL =
		"<?xml version=\"1.0\" encoding=\"" + Charset.defaultCharset().name() + "\"?>";

	/**
	 * Default instance of ErrorHandler which throws XMLParsingException for any
	 * errors/warnings encountered.
	 */
	public static final ErrorHandler DEFAULT_ERROR_HANDLER = new ExceptionErrorHandler();

	/**
	 * A document builder factory that creates namespace aware, non-validating
	 * builders.
	 * <p/>
	 * We create the singleton because it's a relatively expensive operation and
	 * the thread-safety caveat of JDK 1.4 no longer applies.
	 */
	private static final DocumentBuilderFactory NVDBF = DocumentBuilderFactory.newInstance();

	static {
		NVDBF.setNamespaceAware(true);
	}


	/**
	 * A document builder factory that creates namespace aware, Schema-validating
	 * builders. Note that you must attach an error handler to any builder
	 * produced by this factory, as the default handler simply sends errors to
	 * StdOut.
	 * <p/>
	 * We create the singleton because it's a relatively expensive operation and
	 * the thread-safety caveat of JDK 1.4 no longer applies.
	 */
	private static final DocumentBuilderFactory SVDBF = DocumentBuilderFactory.newInstance();

	static {
		SVDBF.setNamespaceAware(true);
		SVDBF.setValidating(true);
		SVDBF.setAttribute(
			"http://java.sun.com/xml/jaxp/properties/schemaLanguage", XMLConstants.W3C_XML_SCHEMA_NS_URI);
	}

	/**
	 * A sax parser factory that creates namespace aware, non-validating
	 * builders.
	 */
	private static final SAXParserFactory NVSPF = SAXParserFactory.newInstance();

	static {
		NVSPF.setNamespaceAware(true);
	}

	/** A factory for creating Schema parsers. */
	private static final SchemaFactory SCHEMA_FACTORY = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);


	private static final TransformerFactory TRANS_FACT = TransformerFactory.newInstance();


	/**
	 * Internal method to create a document builder from a factory, translating
	 * exceptions.
	 */
	private static DocumentBuilder newDocumentBuilder(
		DocumentBuilderFactory factory) {
		try {
			return factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw new XMLParsingException(ERR_CANT_CONFIGURE, e);
		}
	}


	/**
	 * Internal method to create a sax parser from a factory, translating
	 * exceptions.
	 */
	private static SAXParser newSAXParser(
		SAXParserFactory factory) {
		try {
			return factory.newSAXParser();
		}
		catch (SAXException e) {
			throw new XMLParsingException(ERR_CANT_CONFIGURE, e);
		}
		catch (ParserConfigurationException e) {
			throw new XMLParsingException(ERR_CANT_CONFIGURE, e);
		}
	}

	/**
	 * Internal parsing method that translates exceptions.
	 *
	 * @param builder Does the parsing.
	 * @param source  The document.
	 *
	 * @throws XMLParsingException if unable to parse the document.
	 */
	private static Document internalParse(
		DocumentBuilder builder, InputSource source) {
		try {
			return builder.parse(source);
		}
		catch (SAXException e) {
			throw new XMLParsingException(ERR_CANT_PARSE, e);
		}
		catch (IOException e) {
			throw new XMLParsingException(ERR_CANT_PARSE, e);
		}
	}

	/**
	 * Internal parsing method that translates exceptions.
	 *
	 * @param parser  Does the parsing.
	 * @param source  The document.
	 * @param handler handles the sax events
	 *
	 * @throws XMLParsingException if unable to parse the document.
	 */
	private static void internalParse(
		SAXParser parser, InputSource source, DefaultHandler handler) {
		try {
			parser.parse(source, handler);
		}
		catch (SAXException e) {
			throw new XMLParsingException(ERR_CANT_PARSE, e);
		}
		catch (IOException e) {
			throw new XMLParsingException(ERR_CANT_PARSE, e);
		}
	}


	/** Creates an empty DOM. */
	public static Document newDocument() {
		return newDocumentBuilder(NVDBF).newDocument();
	}


	/** Creates a DOM that just has a root element. */
	public static Document newDocument(String rootElementName) {
		Document doc = newDocumentBuilder(NVDBF).newDocument();
		Element root = doc.createElement(rootElementName);
		doc.appendChild(root);
		return doc;
	}

	/** Creates a DOM that just has a root element with the given namespace. */
	public static Document newDocument(String rootElementName, String ns) {
		Document doc = newDocumentBuilder(NVDBF).newDocument();
		Element root = doc.createElementNS(ns, rootElementName);
		doc.appendChild(root);
		return doc;
	}

	/** Creates a SAXParser. */
	public static SAXParser newSAXParser() {
		return newSAXParser(NVSPF);
	}

	/**
	 * Parses the passed XML, using a namespace-aware non-validating parser.
	 * <p/>
	 * This is primarily useful for test structures. In production code, you
	 * should be using a validating parser and reacting to any exceptions.
	 */
	public static Document parse(String xml) {
		return parse(xml, (EntityResolver) null);
	}

	/**
	 * Parses the passed XML, using a namespace-aware non-validating parser.
	 * <p/>
	 * This is primarily useful for test structures. In production code, you
	 * should be using a validating parser and reacting to any exceptions.
	 */
	public static Document parse(String xml, EntityResolver resolver) {
		return parse(stringToInputSource(xml), resolver);
	}

	/**
	 * Parses XML from the passed <code>InputStream</code>, using a
	 * namespace-aware non-validating parser.  Errors are handled by
	 * {@link #DEFAULT_ERROR_HANDLER}.
	 * <p/>
	 * This is primarily useful for test structures. In production code, you
	 * should be using a validating parser and reacting to any exceptions.
	 * <p/>
	 * Caller is responsible for closing the InputStream.
	 */
	public static Document parse(InputStream stream) {
		return parse(new InputSource(stream));
	}

	/**
	 * Parses XML from the passed <code>InputSource</code>, using a
	 * namespace-aware non-validating parser.  Errors are handled by
	 * {@link #DEFAULT_ERROR_HANDLER}.
	 * <p/>
	 * This is primarily useful for test structures. In production code, you
	 * should be using a validating parser and reacting to any exceptions.
	 */
	public static Document parse(InputSource source) {
		return parse(source, (EntityResolver) null);
	}

	/**
	 * Parses XML from the passed <code>InputSource</code>, using a
	 * namespace-aware non-validating parser.  Errors are handled by
	 * {@link #DEFAULT_ERROR_HANDLER}.
	 * <p/>
	 * This is primarily useful for test structures. In production code, you
	 * should be using a validating parser and reacting to any exceptions.
	 */
	public static Document parse(InputSource source, EntityResolver resolver) {
		DocumentBuilder db = newDocumentBuilder(NVDBF);
		db.setErrorHandler(DEFAULT_ERROR_HANDLER);
		if (resolver != null) {
			db.setEntityResolver(resolver);
		}
		return internalParse(db, source);
	}

	/**
	 * Parses the passed XML, using a namespace-aware parser that attempts to
	 * validate using the schema(s) referenced from the source XML.
	 */
	public static Document parse(String xml, ErrorHandler errorHandler) {
		return parse(stringToInputSource(xml), errorHandler);
	}

	/**
	 * Parses XML from the passed <code>InputStream</code>, using a
	 * namespace-aware parser that attempts to validate using the schema(s)
	 * referenced from the source XML.
	 * <p/>
	 * Caller is responsible for closing the InputStream.
	 */
	public static Document parse(InputStream stream, ErrorHandler errorHandler) {
		return parse(new InputSource(stream), errorHandler);
	}

	/**
	 * Parses XML from the passed <code>InputSource</code>, using a
	 * namespace-aware parser that attempts to validate using the schema(s)
	 * referenced from the source XML.
	 */
	public static Document parse(InputSource source, ErrorHandler errorHandler) {
		DocumentBuilder builder = newDocumentBuilder(SVDBF);
		builder.setErrorHandler(errorHandler);
		return internalParse(builder, source);
	}


	/**
	 * Parses the passed XML, using a namespace-aware parser that validates
	 * using the explicitly specified schema(s).
	 */
	public static Document parse(
		String xml, ErrorHandler errorHandler, Schema... schemas) {
		return parse(
			stringToInputSource(xml), errorHandler, schemas);
	}

	/**
	 * Parses XML from the passed <code>InputStream</code>, using a
	 * namespace-aware parser that validates using the using the explicitly
	 * specified schema(s).
	 * <p/>
	 * Note: at the present time, the document is parsed and then validated
	 * against the schema(s). This means that line number information is lost.
	 * Perhaps some day Sun will actually implement <code>DocumentBuilderFactory
	 * .setSchema()</code>.
	 * <p/>
	 * Caller is responsible for closing the InputStream.
	 */
	public static Document parse(
		InputStream stream, ErrorHandler errorHandler, Schema... schemas) {
		return parse(new InputSource(stream), errorHandler, schemas);
	}

	/**
	 * Parses XML from the passed <code>InputSource</code>, using a
	 * namespace-aware parser that validates using the using the explicitly
	 * specified schema(s).
	 * <p/>
	 * Note: at the present time, the document is parsed and then validated
	 * against the schema(s). This means that line number information is lost.
	 * Perhaps some day Sun will actually implement <code>DocumentBuilderFactory
	 * .setSchema()</code>.
	 */
	public static Document parse(
		InputSource source, ErrorHandler errorHandler, Schema... schemas) {
		Document doc = parse(source);
		return validate(doc, errorHandler, schemas);
	}

	/**
	 * Parses the passed XML, using a namespace-aware non-validating parser.
	 * <p/>
	 * This is primarily useful for test structures. In production code, you
	 * should be using a validating parser.
	 */
	public static <HType extends DefaultHandler> HType parse(
		String xml, HType handler) {
		return parse(stringToInputSource(xml), handler);
	}

	/**
	 * Parses XML from the passed <code>InputSource</code>, using a
	 * namespace-aware non-validating parser.
	 * <p/>
	 * This is primarily useful for test structures. In production code, you
	 * should be using a validating parser.
	 */
	public static <HType extends DefaultHandler> HType parse(
		InputSource source, HType handler) {
		SAXParser parser = newSAXParser(NVSPF);
		internalParse(parser, source, handler);
		return handler;
	}

	/**
	 * Validates the given node using the explicitly specified schema(s).
	 * Errors are handled by {@link #DEFAULT_ERROR_HANDLER}.
	 *
	 * @return a new Document rooted at the given Node, enhanced with any info
	 *         added by the schema (such as default values)
	 */
	public static Document validate(Node source, Schema... schemas) {
		return validate(source, DEFAULT_ERROR_HANDLER, schemas);
	}

	/**
	 * Validates the given node using the explicitly specified schema(s).
	 * Errors are handled by the given errorHandler.
	 *
	 * @return a new Document rooted at the given Node, enhanced with any info
	 *         added by the schema (such as default values)
	 */
	public static Document validate(
		Node source, ErrorHandler errorHandler, Schema... schemas) {
		try {
			for (Schema schema : schemas) {
				Validator val = schema.newValidator();
				val.setErrorHandler(errorHandler);
				DOMResult augmented = new DOMResult();
				val.validate(new DOMSource(source), augmented);
				source = augmented.getNode();
			}
			return (Document) source;
		}
		catch (IOException e) {
			throw new XMLParsingException(ERR_CANT_VALIDATE, e);
		}
		catch (SAXException e) {
			throw new XMLParsingException(ERR_CANT_VALIDATE, e);
		}
	}

	/**
	 * Parses the passed XML, using a namespace-aware parser that validates
	 * using the explicitly specified schema(s).
	 */
	public static Document parse(
		String xml, ErrorHandler errorHandler, URL... schemaUrls) {
		return parse(
			stringToInputSource(xml), errorHandler, schemaUrls);
	}

	/**
	 * Parses XML from the passed <code>InputStream</code>, using a
	 * namespace-aware parser that validates using the using the explicitly
	 * specified schema(s).
	 * <p/>
	 * Caller is responsible for closing the InputStream.
	 */
	public static Document parse(
		InputStream stream, ErrorHandler errorHandler, URL... schemaUrls) {
		return parse(new InputSource(stream), errorHandler, schemaUrls);
	}

	/**
	 * Parses XML from the passed <code>InputSource</code>, using a
	 * namespace-aware parser that validates using the using the explicitly
	 * specified schema(s).
	 */
	public static Document parse(
		InputSource source, ErrorHandler errorHandler, URL... schemaUrls) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(true);
		factory.setAttribute(
			"http://java.sun.com/xml/jaxp/properties/schemaLanguage", XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// we can't pass URLs to setAttribute, so must convert to string
		if (!ArrayUtil.isEmpty(schemaUrls)) {
			List<String> urlsAsStrings = new ArrayList<String>(schemaUrls.length);
			for (int ii = 0; ii < schemaUrls.length; ii++) {
				if (schemaUrls[ii] != null) {
					urlsAsStrings.add(schemaUrls[ii].toString());
				}
			}
			if (!urlsAsStrings.isEmpty()) {
				factory.setAttribute(
					"http://java.sun.com/xml/jaxp/properties/schemaSource",
					urlsAsStrings.toArray(new String[urlsAsStrings.size()]));
			}
		}

		DocumentBuilder builder = newDocumentBuilder(factory);
		builder.setErrorHandler(errorHandler);
		return internalParse(builder, source);
	}


	/** Parses Schema XML from the passed string. */
	public static Schema parseAsSchema(String xml) {
		return parseAsSchema(stringToInputSource(xml));
	}

	/**
	 * Parses Schema XML from the passed InputStream.
	 * <p/>
	 * Caller is responsible for closing the InputStream.
	 */
	public static Schema parseAsSchema(InputStream in) {
		return parseAsSchema(new InputSource(in));
	}

	/**
	 * Parses Schema XML from the passed InputSource.
	 * <p/>
	 * Caller is responsible for closing the InputSource.
	 */
	public static Schema parseAsSchema(InputSource source) {
		try {
			Document docSource = XMLUtil.parse(source);
			return SCHEMA_FACTORY.newSchema(new DOMSource(docSource));
		}
		catch (SAXException e) {
			throw new XMLParsingException(ERR_CANT_PARSE_SCHEMA, e);
		}
	}


	/**
	 * Parses Schema XML from the passed URL. Any IO exceptions
	 * are caught and wrapped with a runtime parsing exception.
	 */
	public static Schema parseAsSchema(URL schemaUrl) {
		InputStream in = null;
		try {
			in = schemaUrl.openStream();
			return parseAsSchema(in);
		}
		catch (IOException e) {
			throw new XMLParsingException(ERR_CANT_PARSE_SCHEMA, e);
		}
		finally {
			IOUtils.closeQuietly(in);
		}
	}

	/** @return an InputSource based on the given url */
	public static InputSource urlToInputSource(String url) {
		try {
			return urlToInputSource(new URL(url));
		}
		catch (MalformedURLException e) {
			throw new XMLParsingException(ERR_CANT_READ_URL, e);
		}
	}

	/** @return an InputSource based on the given url */
	public static InputSource urlToInputSource(URL url) {
		try {
			return new InputSource(url.openStream());
		}
		catch (IOException e) {
			throw new XMLParsingException(ERR_CANT_READ_URL, e);
		}
	}

	/** @return an InputSource based on the given string of xml */
	public static InputSource stringToInputSource(String xml) {
		return new InputSource(new StringReader(xml));
	}

	/**
	 * Serialize an XML Document to a string. This is the basic serialization
	 * provided by <code>javax.xml.transform</code>; it preserves whitespace
	 * and does not pretty-print the output.
	 *
	 * @param node The root of the node tree to be serialized. Note that
	 *             only Documents, DocumentFragments, and Elements may be
	 *             serialized; any other node type throws ClassCastException.
	 *
	 * @return The serialized XML.
	 */
	public static String serialize(Node node) {
		return serialize(node, (Properties) null);
	}

	/**
	 * Serialize an XML Document to a string using the specified
	 * <code>Transformer</code> output properties.
	 * This is the basic serialization provided by
	 * <code>javax.xml.transform</code>; it preserves whitespace
	 * and does not pretty-print the output.
	 *
	 * @param node        The root of the node tree to be serialized. Note that
	 *                    only Documents, DocumentFragments, and Elements may be
	 *                    serialized; any other node type throws ClassCastException.
	 * @param outputProps Set of output properties to override for the transformation
	 *
	 * @return The serialized XML.
	 */
	public static String serialize(Node node, Properties outputProps) {
		try {
			StringWriter writer = new StringWriter();
			serialize(new DOMSource(node), new StreamResult(writer), outputProps);
			writer.close();
			return writer.toString();
		}
		catch (IOException e) {
			throw new XMLParsingException(ERR_CANT_SERIALIZE, e);
		}
	}

	/**
	 * Serialize an XML Document to a the given <code>OutputStream</code>. This
	 * is the basic serialization provided by <code>javax.xml.transform</code>;
	 * it preserves whitespace and does not pretty-print the output.
	 *
	 * @param node   The root of the node tree to be serialized. Note that
	 *               only Documents, DocumentFragments, and Elements may be
	 *               serialized; any other node type throws ClassCastException.
	 * @param stream The destination for serialized XML text. The caller is
	 *               responsible for closing this object.
	 */
	public static void serialize(Node node, OutputStream stream) {
		try {
			serialize(new DOMSource(node), new StreamResult(stream));
			stream.flush();
		}
		catch (IOException e) {
			throw new XMLParsingException(ERR_CANT_SERIALIZE, e);
		}
	}

	/**
	 * Serialize a source to a the given <code>Result</code>. This
	 * is the basic serialization provided by <code>javax.xml.transform</code>;
	 * it preserves whitespace and does not pretty-print the output.
	 *
	 * @param source The source to be serialized
	 * @param result The destination for serialized XML
	 */
	public static void serialize(Source source, Result result) {
		serialize(source, result, null);
	}

	/**
	 * Serialize a source to a the given <code>Result</code> using the
	 * supplied output properties for the transformation. This
	 * is the basic serialization provided by <code>javax.xml.transform</code>;
	 * it preserves whitespace and does not pretty-print the output.
	 *
	 * @param source      The source to be serialized
	 * @param result      The destination for serialized XML
	 * @param outputProps Set of output properties to override for the transformation
	 */
	public static void serialize(
		Source source, Result result, Properties outputProps) {
		try {
			Transformer trans = TRANS_FACT.newTransformer();
			if (outputProps != null) {
				trans.setOutputProperties(outputProps);
			}
			trans.transform(source, result);
		}
		catch (TransformerException e) {
			throw new XMLParsingException(ERR_CANT_SERIALIZE, e);
		}
	}


	/**
	 * Serializes a DOM tree to a String, optionally pretty-printing the result.
	 * <p/>
	 * This method uses a known serializer implementation, and is therefore able
	 * to guarantee behavior.
	 *
	 * @param node               The root of the node tree to be serialized.
	 *                           Note that only Documents, DocumentFragments,
	 *                           and Elements may be serialized; any other
	 *                           node type throws ClassCastException.
	 * @param indentAmount       If non-zero, the output is pretty-printed, with
	 *                           the specified indentation per level.
	 * @param preserveWhitespace If <code>true</code>, serialization preserves
	 *                           all Text nodes in the DOM tree. Ignored if
	 *                           <code>indentAmount</code> is non-zero.
	 */
	public static String serialize(
		Node node, int indentAmount, boolean preserveWhitespace) {
		try {
			StringWriter sw = new StringWriter();
			serialize(node, new StreamResult(sw), indentAmount, preserveWhitespace);
			sw.close();
			return sw.toString();
		}
		catch (IOException e) {
			throw new XMLParsingException(ERR_CANT_SERIALIZE, e);
		}
	}

	/**
	 * Serializes a DOM tree to an OutputStream, optionally pretty-printing the
	 * result.
	 * <p/>
	 * This method uses a known serializer implementation, and is therefore able
	 * to guarantee behavior.
	 *
	 * @param node               The root of the node tree to be serialized.
	 *                           Note that only Documents, DocumentFragments,
	 *                           and Elements may be serialized; any other
	 *                           node type throws ClassCastException.
	 * @param stream             The destination for output. The caller is
	 *                           responsible for closing this object.
	 * @param indentAmount       If non-zero, the output is pretty-printed,
	 *                           with the specified indentation per level.
	 * @param preserveWhitespace If <code>true</code>, serialization preserves
	 *                           all Text nodes in the DOM tree. Ignored if
	 *                           <code>indentAmount</code> is non-zero.
	 */
	public static void serialize(
		Node node, OutputStream stream, int indentAmount, boolean preserveWhitespace) {
		try {
			serialize(
				node, new StreamResult(stream), indentAmount, preserveWhitespace);
			stream.flush();
		}
		catch (IOException e) {
			throw new XMLParsingException(ERR_CANT_SERIALIZE, e);
		}
	}

	/**
	 * Serializes a DOM tree to a StreamResult, optionally pretty-printing the
	 * result.
	 * <p/>
	 * This method uses a known serializer implementation, and is therefore able
	 * to guarantee behavior.
	 *
	 * @param node               The root of the node tree to be serialized.
	 *                           Note that only Documents, DocumentFragments,
	 *                           and Elements may be serialized; any other
	 *                           node type throws ClassCastException.
	 * @param result             The destination for output.
	 * @param indentAmount       If non-zero, the output is pretty-printed,
	 *                           with the specified indentation per level.
	 * @param preserveWhitespace If <code>true</code>, serialization preserves
	 *                           all Text nodes in the DOM tree. Ignored if
	 *                           <code>indentAmount</code> is non-zero.
	 */
	public static void serialize(
		Node node, StreamResult result, int indentAmount, boolean preserveWhitespace) {
		try {
			OutputFormat format = new OutputFormat(
				(node instanceof Document) ? (Document) node : node.getOwnerDocument());
			if (indentAmount > 0) {
				format.setPreserveSpace(false);
				format.setIndenting(true);
				format.setIndent(indentAmount);
			} else {
				format.setPreserveSpace(preserveWhitespace);
			}
			XMLSerializer serializer = ((result.getOutputStream() != null) ? new XMLSerializer(
				result.getOutputStream(), format) : new XMLSerializer(
				result.getWriter(), format));
			serializer.setNamespaces(true);
			if (node instanceof Document) {
				serializer.serialize((Document) node);
			} else if (node instanceof DocumentFragment) {
				serializer.serialize((DocumentFragment) node);
			} else if (node instanceof Element) {
				serializer.serialize((Element) node);
			} else {
				throw new ClassCastException(
					node.getClass().getName() + " isn't serializable");
			}
		}
		catch (IOException e) {
			throw new XMLParsingException(ERR_CANT_SERIALIZE, e);
		}
	}

	/**
	 * Serializes a DOM tree to a String, pretty-printing the result.
	 *
	 * @param node The root of the node tree to be serialized.
	 *             Note that only Documents, DocumentFragments,
	 *             and Elements may be serialized; any other
	 *             node type throws ClassCastException.
	 */
	public static String prettyPrint(Node node) {
		return serialize(node, PRETTY_PRINT_INDENTATION, false);
	}

	/** Pretty-prints an XML String */
	public static String prettyPrint(String xml) {
		return prettyPrint(xml, null);
	}

	/** Pretty-prints an XML String */
	public static String prettyPrint(String xml, EntityResolver resolver) {
		return prettyPrint(parse(xml, resolver));
	}

	/**
	 * Parse an XML schema from a URI into an XSModel.
	 *
	 * @param schemaURI URI of the XML schema to parse.
	 *
	 * @return The parsed schema.
	 */
	public static XSModel parseSchema(String schemaURI) {
		try {
			return ((XSImplementation) DOMImplementationRegistry.newInstance().getDOMImplementation(
				"XS-Loader")).createXSLoader(null).loadURI(schemaURI);
		}
		catch (IllegalAccessException e) {
			throw new XMLParsingException(ERR_CANT_PARSE_SCHEMA, e);
		}
		catch (InstantiationException e) {
			throw new XMLParsingException(ERR_CANT_PARSE_SCHEMA, e);
		}
		catch (ClassNotFoundException e) {
			throw new XMLParsingException(ERR_CANT_PARSE_SCHEMA, e);
		}
	}


	/**
	 * @return <code>true</code> iff the given NodeList is <code>null</code> or
	 *         has length 0, <code>false</code> otherwise.
	 */
	public static boolean isEmpty(NodeList nl) {
		return ((nl == null) || (nl.getLength() == 0));
	}


	/**
	 * Removes invalid xml characters from a String.  See {@link #isValidChar}
	 * for definition of valid xml chars.
	 *
	 * @param xmlStr String which may contain invalid chars, may be
	 *               <code>null</code>
	 *
	 * @return an string with no invalid xml characters.  Iff no invalid chars
	 *         were found (or the given String was <code>null</code>), the
	 *         returned String will be the given String.
	 */
	public static String cleanXmlString(String xmlStr) {
		if (xmlStr == null) {
			return xmlStr;
		}

		// the hugely common case is no invalid chars, so assume the string is
		// clean, and only do the work of fixing it if we encounter an invalid
		// char
		for (int i = 0; i < xmlStr.length(); ++i) {
			if (!isValidChar(xmlStr.charAt(i))) {
				// need to fix the string
				return fixXmlString(xmlStr);
			}
		}
		return xmlStr;
	}


	/**
	 * @param xmlStr non-<code>null</code> String which contains invalid chars
	 *
	 * @return an string with no invalid xml characters
	 */
	private static String fixXmlString(String xmlStr) {
		StringBuilder ret = new StringBuilder(xmlStr.length());
		for (int i = 0; i < xmlStr.length(); ++i) {
			char c = xmlStr.charAt(i);
			// FIXME, should we insert spaces or just remove invalid chars?
			if (isValidChar(c)) {
				ret.append(c);
			}
		}
		return ret.toString();
	}


	/**
	 * Some characters are not valid within xml.  See
	 * <a href="http://www.w3.org/TR/REC-xml/#NT-Char">XML 1.0 Characters</a>.  Short list is:
	 * <pre>
	 * Char ::= #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
	 * </pre>
	 *
	 * @return <code>true</code> if the given char is valid for embedding within
	 *         xml, <code>false</code> otherwise.
	 */
	public static boolean isValidChar(int c) {
		return XMLChar.isValid(c);
	}


	/**
	 * Extracts the value of an XML attribute from the passed source string,
	 * which <em>need not be valid XML</em>. This is useful for GUI editors,
	 * which may provide editable XML text as well as shortcut fields that
	 * interact with that text.
	 *
	 * @param source    The XML-like text.
	 * @param attrName  The name of the attribute.
	 * @param startFrom The character position where the attribute's element
	 *                  starts (as returned by indexOfElement). This
	 *                  method will start looking for the attribute at this
	 *                  location, and will stop looking when it encounters
	 *                  the closing "&gt;" for the tag.
	 *
	 * @return The attribute value, <code>null</code> if there's no attribute
	 *         with the given name within the assumed tag bounds.
	 */
	public static String getAttributeValue(String source, String attrName, int startFrom) {
		Matcher m = Pattern.compile(attrName + "\\s*=\\s*[\"']").matcher(source);
		m.region(startFrom, source.indexOf(">", startFrom));
		if (!m.find()) {
			return null;
		}

		int valStart = m.end();
		int valFinish = StringUtil.indexOfAny(source, "'\"", valStart);
		return source.substring(valStart, valFinish);
	}


	/**
	 * Set the value of an XML attribute in the passed source string, which
	 * <em>need not be valid XML</em>. This is useful for GUI editors, which
	 * may provide editable XML text as well as shortcut fields that interact
	 * with that text.
	 * <p/>
	 * If the passed string does not have the corresponding attribute, it will
	 * be added before the presumed end of the tag (unless we can't find the
	 * end of the tag).
	 *
	 * @param source    The XML-like text.
	 * @param attrName  The name of the attribute.
	 * @param value     The new value for the attribute.
	 * @param startFrom The character position where the attribute's element
	 *                  starts (as returned by {@link indexOfElement). This
	 *                  method will start looking for the attribute at this
	 *                  location, and will stop looking when it encounters
	 *                  the closing "&gt;" for the tag.
	 *
	 * @return The source string, updated with the new attribute value.
	 */
	public static String setAttributeValue(String source, String attrName, String value, int startFrom) {
		int tagEnd = source.indexOf(">", startFrom);
		if (tagEnd < 0) {
			return source;
		}

		StringBuilder sb = new StringBuilder(source);
		Matcher m = Pattern.compile(attrName + "\\s*=\\s*[\"']").matcher(source);
		m.region(startFrom, tagEnd);

		if (m.find()) {
			int valStart = m.end();
			int valFinish = StringUtil.indexOfAny(source, "'\"", valStart);
			sb.replace(valStart, valFinish, value);
		} else {
			sb.insert(tagEnd, " " + attrName + "='" + value + "'");
		}

		return sb.toString();
	}

	/**
	 * Loads an xml file into a XMLConfiguration object.
	 * <p/>
	 * Based on a like-named method originally in docyanker-common.
	 *
	 * @param urlStr The url to the xml resource
	 *
	 * @return the XMLConfiguration created
	 *
	 * @throws ConfigurationException if the xml cannot be parsed
	 */
	public static XMLConfiguration createXmlConfig(String urlStr) throws ConfigurationException {

		URL xmlConfigUrl = null;

		// first, validate the xml
		try {
			xmlConfigUrl = new URL(urlStr);
			com.nkhoang.common.xml.Validator validator = new com.nkhoang.common.xml.Validator(true);
			validator.validate(new InputSource(xmlConfigUrl.openStream()));
		}
		catch (SAXException e) {
			throw new ConfigurationException(e);
		}
		catch (ParserConfigurationException e) {
			throw new ConfigurationException(e);
		}
		catch (IOException e) {
			throw new ConfigurationException(e);
		}

		// now, load the config
		XMLConfiguration config = new XMLConfiguration();
		config.load(xmlConfigUrl);
		return config;
	}

	/**
	 * An <code>ErrorHandler</code> that throws an <code>XMLParsingException
	 * </code> if validation fails.
	 */
	public static class ExceptionErrorHandler implements ErrorHandler {

		public void error(SAXParseException exception) throws SAXException {
			throw new XMLParsingException(exception);
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			throw new XMLParsingException(exception);
		}

		public void warning(SAXParseException exception) throws SAXException {
			throw new XMLParsingException(exception);
		}
	}

	/*public static void parseStream(String url, DefaultHandler handler) {
		parseStream(url, null, handler);
	}*/

	/*public static void parseStream(String url, List<NameValuePair> paramList, DefaultHandler handler) {
		GetMethod get = new GetMethod(url);
		try {
			if (paramList != null && paramList.size() > 0) {
				get.setQueryString(paramList.toArray(new NameValuePair[paramList.size()]));
			}
			int statusCode = HttpUtil.getHttpClient().executeMethod(get);
			if (statusCode != HttpServletResponse.SC_OK) {
				throw new RuntimeException(
					"Failed to get data from " + url + ",  " + "status = " + statusCode + ": " +
					get.getResponseBodyAsString());
			}
			InputStream inputStream = get.getResponseBodyAsStream();
			newSAXParser().parse(inputStream, handler);
			inputStream.close();
		}
		catch (Exception e) {
			throw new RuntimeException("unable to get data from " + url, e);
		}
		finally {
			get.releaseConnection();
		}
	}*/
}
