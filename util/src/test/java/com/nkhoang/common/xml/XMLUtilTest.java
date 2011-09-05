package com.nkhoang.common.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.validation.Schema;


import com.nkhoang.common.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import junit.framework.TestCase;

public class XMLUtilTest extends TestCase {

	public XMLUtilTest(String name) {
		super(name);
	}


	//----------------------------------------------------------------------------
	//  Setup
	//----------------------------------------------------------------------------

	// elements and content for sample XML
	private final static String EL_ROOT  = "root";
	private final static String EL_FOO   = "foo";
	private final static String EL_BAR   = "bar";
	private final static String CONTENT1 = "content1";
	private final static String CONTENT2 = "content2";


	/** A schema definition. */
	private final static String SCHEMA =
		"<xs:schema" + "    version='1.0'" + "    xmlns:xs='http://www.w3.org/2001/XMLSchema'" + "    >" +
		"<xs:element name='root' type='RootType'/>" + "<xs:complexType name='RootType'>" + "  <xs:sequence>" +
		"    <xs:element name='foo'>" + "      <xs:complexType mixed='true'>" + "        <xs:sequence>" +
		"          <xs:element name='bar' type='xs:string'/>" + "        </xs:sequence>" + "      </xs:complexType>" +
		"    </xs:element>" + "  </xs:sequence>" + "</xs:complexType>" + "</xs:schema>";


	/** XML that is well-formed and valid according to the schema. */
	private final static String VALID_XML = XMLUtil.XML_DECL + element(
		EL_ROOT, element(
		EL_FOO, CONTENT1, element(EL_BAR, CONTENT2)));


	/** XML that is well-formed but not valid according to the schema. */
	private final static String INVALID_XML = XMLUtil.XML_DECL + element(
		EL_ROOT, CONTENT1, element(EL_FOO, CONTENT2));


	/** XML that is not well-formed. */
	private final static String BAD_XML = XMLUtil.XML_DECL + "<root>blah<foo>blah<bar>blah</foo></bar></root>";


	//----------------------------------------------------------------------------
	//  Support code
	//----------------------------------------------------------------------------

	/** Helper method to create an element string. */
	private static String element(String name, String... content) {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(name).append(">");
		for (String child : content) {
			sb.append(child);
		}
		sb.append("</").append(name).append(">");
		return sb.toString();
	}


	/**
	 * Stores the passed string in a temporary file, and returns a URL to
	 * access it. This is used to reference the schema document.
	 */
	private static URL storeInTempfile(String s) throws IOException {
		File file = File.createTempFile("test", ".tmp");
		file.deleteOnExit();
		PrintWriter out = new PrintWriter(new FileWriter(file));
		out.println(s);
		out.flush();
		out.close();
		return file.toURL();
	}


	/**
	 * Updates the passed string, inserting a link to the passed schema URL
	 * into the <code>root</code> element.
	 */
	private static String addSchemaUrl(String xml, URL schemaUrl) {
		StringBuilder sb = new StringBuilder(xml);
		int pos = sb.indexOf("root") + 4;
		sb.insert(
			pos, " xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'" + " xsi:noNamespaceSchemaLocation='" +
			     schemaUrl.toString() + "'");
		return sb.toString();
	}


	/** An implementation of ErrorHandler that records its invocations. */
	private static class MockErrorHandler implements ErrorHandler {

		public List<SAXParseException> report = new ArrayList<SAXParseException>();

		public void error(SAXParseException exception) throws SAXException {
			report.add(exception);
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			report.add(exception);
		}

		public void warning(SAXParseException exception) throws SAXException {
			report.add(exception);
		}
	}


	//----------------------------------------------------------------------------
	//  Test cases
	//----------------------------------------------------------------------------

	public void testNonvalidatingParse() throws Exception {
		Document doc1 = XMLUtil.parse(VALID_XML);
		Element root1 = doc1.getDocumentElement();
		assertEquals(EL_ROOT, root1.getTagName());
		assertEquals(CONTENT1 + CONTENT2, root1.getTextContent());

		Document doc2 = XMLUtil.parse(INVALID_XML);
		Element root2 = doc2.getDocumentElement();
		assertEquals(EL_ROOT, root2.getTagName());
		assertEquals(CONTENT1 + CONTENT2, root2.getTextContent());

		try {
			XMLUtil.parse(BAD_XML);
			fail("successfully parsed a non-well-formed document");
		}
		catch (XMLParsingException e) {
			// this is success
		}
	}


	public void testValidatingParseWithExplicitSchema() throws Exception {
		Schema schema = XMLUtil.parseAsSchema(SCHEMA);
		MockErrorHandler eh = new MockErrorHandler();

		Document doc1 = XMLUtil.parse(VALID_XML, eh, schema);
		assertFalse(eh.report.size() > 0);
		Element root1 = doc1.getDocumentElement();
		assertEquals(EL_ROOT, root1.getTagName());
		assertEquals(CONTENT1 + CONTENT2, root1.getTextContent());

		Document doc2 = XMLUtil.parse(INVALID_XML, eh, schema);
		assertTrue(eh.report.size() > 0);
		// even if we don't validate, we still parse
		Element root2 = doc2.getDocumentElement();
		assertEquals(EL_ROOT, root2.getTagName());

		try {
			XMLUtil.parse(BAD_XML, eh, schema);
			fail("parsed a non-well-formed document");
		}
		catch (XMLParsingException e) {
			// this is success
		}
	}

	public void testSubElementValidation() throws Exception {
		Schema schema = XMLUtil.parseAsSchema(SCHEMA);

		String wrappedXml = XMLUtil.XML_DECL + element(
			"argle", element(
			EL_ROOT, element(
			EL_FOO, CONTENT1, element(EL_BAR, CONTENT2))));

		Document doc1 = XMLUtil.parse(wrappedXml);

		Element argleEl = doc1.getDocumentElement();
		Element rootEl = DOMUtil.getFirstChildElement(argleEl);
		Element fooEl = DOMUtil.getFirstChildElement(rootEl);

		Document newDocEl = XMLUtil.validate(rootEl, schema);
		assertEquals(EL_ROOT, DOMUtil.getLocalName(newDocEl.getDocumentElement()));

		try {
			XMLUtil.validate(argleEl, schema);
			fail("parsed a non-well-formed document");
		}
		catch (XMLParsingException e) {
			// this is success
		}

		try {
			XMLUtil.validate(fooEl, schema);
			fail("parsed a non-well-formed document");
		}
		catch (XMLParsingException e) {
			// this is success
		}
	}

	public void testValidatingParseWithExplicitSchemaAsUri() throws Exception {
		URL schemaUrl = storeInTempfile(SCHEMA);
		MockErrorHandler eh = new MockErrorHandler();

		Document doc1 = XMLUtil.parse(VALID_XML, eh, schemaUrl);
		assertFalse(eh.report.size() > 0);
		Element root1 = doc1.getDocumentElement();
		assertEquals(EL_ROOT, root1.getTagName());
		assertEquals(CONTENT1 + CONTENT2, root1.getTextContent());

		Document doc2 = XMLUtil.parse(INVALID_XML, eh, schemaUrl);
		assertTrue(eh.report.size() > 0);
		// even if we don't validate, we still parse
		Element root2 = doc2.getDocumentElement();
		assertEquals(EL_ROOT, root2.getTagName());

		try {
			XMLUtil.parse(BAD_XML, eh, schemaUrl);
			fail("parsed a non-well-formed document");
		}
		catch (XMLParsingException e) {
			// this is success
		}
	}


	public void testValidatingParseWithLinkedSchema() throws Exception {
		MockErrorHandler eh = new MockErrorHandler();
		URL schemaUrl = storeInTempfile(SCHEMA);

		Document doc1 = XMLUtil.parse(addSchemaUrl(VALID_XML, schemaUrl), eh);
		assertFalse(eh.report.size() > 0);
		Element root1 = doc1.getDocumentElement();
		assertEquals(EL_ROOT, root1.getTagName());

		Document doc2 = XMLUtil.parse(addSchemaUrl(INVALID_XML, schemaUrl), eh);
		assertTrue(eh.report.size() > 0);
		Element root2 = doc2.getDocumentElement();
		assertEquals(EL_ROOT, root2.getTagName());

		try {
			XMLUtil.parse(addSchemaUrl(BAD_XML, schemaUrl), eh);
			fail("parsed a non-well-formed document");
		}
		catch (XMLParsingException e) {
			// this is success
		}
	}


	public void testExceptionErrorHandler() throws Exception {
		ErrorHandler eh = new XMLUtil.ExceptionErrorHandler();
		XMLUtil.parse(VALID_XML, eh, storeInTempfile(SCHEMA));
		try {
			XMLUtil.parse(INVALID_XML, eh, storeInTempfile(SCHEMA));
			fail("parsing didn't throw an exception");
		}
		catch (XMLParsingException e) {
			// success
		}
	}


	public void testInvalidChar() throws Exception {
		String bogusString = "this is\u0008a bogus xml string";
		assertEquals(
			"this isa bogus xml string", XMLUtil.cleanXmlString(bogusString));

		String validString = "this is a valid xml string";
		// note, this is intentionally testing reference equality
		assertTrue(validString == XMLUtil.cleanXmlString(validString));

		assertTrue(null == XMLUtil.cleanXmlString(null));
	}


	public void testSerialize() throws Exception {
		final String someText = "this is some text";
		final String someTextWithWhitespace = "     " + someText + "    ";

		Document doc = XMLUtil.newDocument(EL_ROOT);
		Element elem = DOMUtil.addChild(doc.getDocumentElement(), null, EL_FOO);
		DOMUtil.setText(elem, someTextWithWhitespace);

		String rslt1 = XMLUtil.serialize(doc);

		// parse it to verify we got valid XML out
		XMLUtil.parse(rslt1);

		// verify that we have a single unbroken block of text
		String expected1 =
			"<" + EL_ROOT + "><" + EL_FOO + ">" + someTextWithWhitespace + "</" + EL_FOO + "></" + EL_ROOT + ">";
		assertTrue(rslt1.indexOf(expected1) >= 0);


		String rslt2 = XMLUtil.serialize(doc, 2, false);
		XMLUtil.parse(rslt2);

		// look for the characteristics of pretty-printed text ... serializer
		// still has a lot of leeway ... like preserving space on text nodes
		// even if you tell it not to
		assertTrue(rslt2.indexOf(someText) >= 0);
		assertTrue(rslt2.indexOf("  <" + EL_FOO + ">") >= 0);
		assertTrue(rslt2.indexOf("\n") >= 0);
	}


	// regression: "pretty-printing" serializer wasn't emitting namespace attributes
	public void testSerializeWithNamespaces() throws Exception {
		final String TEST_NS = "zippy";
		final String TEST_PREFIX = "pinhead";

		Document dom = XMLUtil.newDocument();
		Element root = dom.createElement(EL_ROOT);
		dom.appendChild(root);
		Element defaultChild = dom.createElementNS(TEST_NS, EL_FOO);
		root.appendChild(defaultChild);
		Element qualifiedChild = dom.createElementNS(TEST_NS, TEST_PREFIX + ":" + EL_BAR);
		root.appendChild(qualifiedChild);

		String rslt1 = XMLUtil.serialize(dom);
		assertTrue(
			0 < StringUtil.linkedIndexOf(
				rslt1, EL_ROOT, EL_FOO, "xmlns=", TEST_NS, TEST_PREFIX, EL_BAR, "xmlns:", TEST_PREFIX, "=", TEST_NS,
				EL_ROOT));

		String rslt2 = XMLUtil.serialize(dom, 2, false);
		assertTrue(
			0 < StringUtil.linkedIndexOf(
				rslt2, EL_ROOT, EL_FOO, "xmlns=", TEST_NS, TEST_PREFIX, EL_BAR, "xmlns:", TEST_PREFIX, "=", TEST_NS,
				EL_ROOT));
	}


	public void testGetSetAttributeValue() throws Exception {
		final String text = "<root><foo name='bar'>";

		assertEquals("bar", XMLUtil.getAttributeValue(text, "name", 6));
		assertNull(XMLUtil.getAttributeValue(text, "foo", 6));
		assertNull(XMLUtil.getAttributeValue(text, "name", 0));
		assertNull(XMLUtil.getAttributeValue(text, "foo", 0));

		assertEquals(
			"<root><foo name='baz'>", XMLUtil.setAttributeValue(text, "name", "baz", 6));
		assertEquals(
			"<root><foo name='bar' foo='baz'>", XMLUtil.setAttributeValue(text, "foo", "baz", 6));
		assertEquals(
			"<root foo='baz'><foo name='bar'>", XMLUtil.setAttributeValue(text, "foo", "baz", 0));
		assertEquals(
			text + " ", XMLUtil.setAttributeValue(text + " ", "foo", "baz", text.length()));
	}
}
