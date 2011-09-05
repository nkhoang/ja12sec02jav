package com.nkhoang.common.xml;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


import junit.framework.TestCase;


public class SchemaUtilTest extends TestCase {

	public SchemaUtilTest(String name) {
		super(name);
	}


	//----------------------------------------------------------------------------
	//  Setup
	//----------------------------------------------------------------------------

	// a reused schema component that defines a name
	public final static String NAME_TYPE_NAME = "NameType";
	public final static String NAME_TYPE      =
		"<xsd:complexType name=\"" + NAME_TYPE_NAME + "\">" + "  <xsd:sequence>" +
		"    <xsd:element name=\"first\"   type=\"xsd:string\" minOccurs=\"5\"/>" +
		"    <xsd:element name=\"middle\"  type=\"xsd:string\" maxOccurs=\"5\"/>" +
		"    <xsd:element name=\"last\"    type=\"xsd:string\"/>" + "  </xsd:sequence>" + "</xsd:complexType>";


	// a reused schema component that defines an address
	public final static String ADDRESS_TYPE_NAME = "AddressType";
	public final static String ADDRESS_TYPE      =
		"<xsd:complexType name=\"" + ADDRESS_TYPE_NAME + "\">" + "  <xsd:sequence>" +
		"    <xsd:element name=\"street1\" type=\"xsd:string\"/>" +
		"    <xsd:element name=\"street2\" type=\"xsd:string\"/>" +
		"    <xsd:element name=\"city\"    type=\"xsd:string\"/>" +
		"    <xsd:element name=\"state\"   type=\"xsd:string\"/>" +
		"    <xsd:element name=\"zip\"     type=\"xsd:string\"/>" + "  </xsd:sequence>" + "</xsd:complexType>";


	//----------------------------------------------------------------------------
	//  Support code
	//----------------------------------------------------------------------------

	/**
	 * Parses a document and validates according to the passed schema,
	 * throwing an exception if unable to validate.
	 */
	private Document parse(String xml, Document schema) throws Exception {
		Document doc = XMLUtil.parse(xml);
		Validator val = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new DOMSource(schema))
		                             .newValidator();
		val.setErrorHandler(new XMLUtil.ExceptionErrorHandler());
		val.validate(new DOMSource(doc));
		return doc;
	}


	/** A hack to relax the occurrence constraints on a type definition. */
	private String relaxOccurrenceConstraints(String typedef) {
		return typedef.replaceAll("minOccurs=\"\\d+\"", "").replaceAll("maxOccurs=\"\\d+\"", "")
		              .replaceAll("element ", "element minOccurs=\"0\" maxOccurs=\"unbounded\" ");
	}


	//----------------------------------------------------------------------------
	//  Test Cases
	//----------------------------------------------------------------------------

	public void testGetTypeDefinition() throws Exception {
		final String schemaXml = "<xsd:schema xmlns:xsd=\"" + XMLConstants.W3C_XML_SCHEMA_NS_URI + "\">" +
		                         "<xsd:element name=\"root\" type=\"AddressType\"/>" + ADDRESS_TYPE + NAME_TYPE +
		                         "</xsd:schema>";

		Document schema = XMLUtil.parse(schemaXml);

		Element rslt01 = SchemaUtil.getTypeDefinition(schema, NAME_TYPE_NAME);
		assertEquals("complexType", rslt01.getLocalName());
		assertEquals(NAME_TYPE_NAME, rslt01.getAttribute("name"));

		Element rslt01seq = DOMUtil.getChildren(rslt01).get(0);
		Element rslt01el1 = DOMUtil.getChildren(rslt01seq).get(0);
		assertEquals("first", rslt01el1.getAttribute("name"));

		try {
			SchemaUtil.getTypeDefinition(schema, "Fubar");
			fail("didn't throw with invalid type name");
		}
		catch (IllegalArgumentException e) {
			// success
		}
	}


	public void testAppendChildFailure() throws Exception {
		final String schemaXml = "<xsd:schema xmlns:xsd=\"" + XMLConstants.W3C_XML_SCHEMA_NS_URI + "\">" +
		                         "<xsd:element name=\"root\" type=\"AddressType\"/>" + ADDRESS_TYPE + "</xsd:schema>";

		Document schema = XMLUtil.parse(schemaXml);
		Document doc = XMLUtil.newDocument("root");
		Element root = doc.getDocumentElement();
		Element child = doc.createElement("foo");

		try {
			SchemaUtil.appendChild(root, child, schema, "Fubar");
			fail("able to append child of unknown type");
		}
		catch (IllegalArgumentException e) {
			// success
		}
	}


	public void testAppendChildSequence() throws Exception {
		final String schemaXml = "<xsd:schema xmlns:xsd=\"" + XMLConstants.W3C_XML_SCHEMA_NS_URI + "\">" +
		                         "<xsd:element name=\"root\" type=\"AddressType\"/>" +
		                         relaxOccurrenceConstraints(ADDRESS_TYPE) + "</xsd:schema>";

		final String documentXml =
			XMLUtil.XML_DECL + "<root>" + "<street1>123 Main St</street1>" + "<city>Philadelphia</city>" +
			"<state>PA</state>" + "<zip>19119</zip>" + "</root>";

		Document schema = XMLUtil.parse(schemaXml);
		Document doc = parse(documentXml, schema);
		Element root = doc.getDocumentElement();
		assertEquals(4, DOMUtil.getChildren(root).size());

		Element child1 = doc.createElement("street2");
		child1.setTextContent("foo");
		SchemaUtil.appendChild(root, child1, schema, ADDRESS_TYPE_NAME);
		assertEquals(5, DOMUtil.getChildren(root).size());
		assertEquals("123 Main St", DOMUtil.getChildren(root).get(0).getTextContent());
		assertEquals("foo", DOMUtil.getChildren(root).get(1).getTextContent());
		assertEquals("Philadelphia", DOMUtil.getChildren(root).get(2).getTextContent());

		Element child2 = doc.createElement("zip");
		child2.setTextContent("bar");
		SchemaUtil.appendChild(root, child2, schema, ADDRESS_TYPE_NAME);
		assertEquals(6, DOMUtil.getChildren(root).size());
		assertEquals("19119", DOMUtil.getChildren(root).get(4).getTextContent());
		assertEquals("bar", DOMUtil.getChildren(root).get(5).getTextContent());

		Element child3 = doc.createElement("street1");
		child3.setTextContent("baz");
		SchemaUtil.appendChild(root, child3, schema, ADDRESS_TYPE_NAME);
		assertEquals(7, DOMUtil.getChildren(root).size());
		assertEquals("123 Main St", DOMUtil.getChildren(root).get(0).getTextContent());
		assertEquals("baz", DOMUtil.getChildren(root).get(1).getTextContent());
	}


	public void testAppendChildChoice() throws Exception {
		final String schemaXml = "<xsd:schema xmlns:xsd=\"" + XMLConstants.W3C_XML_SCHEMA_NS_URI + "\">" +
		                         "<xsd:element name=\"root\" type=\"Zippy\"/>" + "<xsd:complexType name=\"Zippy\">" +
		                         "  <xsd:choice>" + "    <xsd:element name=\"foo\" type=\"xsd:string\"/>" +
		                         "    <xsd:element name=\"bar\" type=\"xsd:string\"/>" +
		                         "    <xsd:element name=\"baz\" type=\"xsd:string\"/>" + "  </xsd:choice>" +
		                         "</xsd:complexType>" + "</xsd:schema>";

		Document schema = XMLUtil.parse(schemaXml);
		Document doc = XMLUtil.newDocument("root");
		Element root = doc.getDocumentElement();

		Element child1 = doc.createElement("bar");
		child1.setTextContent("argle");
		SchemaUtil.appendChild(root, child1, schema, "Zippy");
		assertEquals(1, DOMUtil.getChildren(root).size());
		assertEquals("argle", DOMUtil.getChildren(root).get(0).getTextContent());

		// this will create a document that does not validate against the schema,
		// since it permits only a single child of the root ... but it does verify
		// that we'll properly insert other values from the choice
		Element child2 = doc.createElement("bar");
		child2.setTextContent("bargle");
		SchemaUtil.appendChild(root, child2, schema, "Zippy");
		assertEquals(2, DOMUtil.getChildren(root).size());
		assertEquals("argle", DOMUtil.getChildren(root).get(0).getTextContent());
		assertEquals("bargle", DOMUtil.getChildren(root).get(1).getTextContent());
	}


	public void testAppendChildChoiceInSequence() throws Exception {
		final String schemaXml = "<xsd:schema xmlns:xsd=\"" + XMLConstants.W3C_XML_SCHEMA_NS_URI + "\">" +
		                         "<xsd:element name=\"root\" type=\"Zippy\"/>" + "<xsd:complexType name=\"Zippy\">" +
		                         "  <xsd:sequence>" + "    <xsd:element name=\"argle\" type=\"xsd:string\"/>" +
		                         "    <xsd:choice>" + "      <xsd:element name=\"foo\" type=\"xsd:string\"/>" +
		                         "      <xsd:element name=\"bar\" type=\"xsd:string\"/>" +
		                         "      <xsd:element name=\"baz\" type=\"xsd:string\"/>" + "    </xsd:choice>" +
		                         "    <xsd:element name=\"bargle\" type=\"xsd:string\"/>" + "  </xsd:sequence>" +
		                         "</xsd:complexType>" + "</xsd:schema>";

		Document schema = XMLUtil.parse(schemaXml);

		// the first set of tests will create the document in reverse
		Document doc1 = XMLUtil.newDocument("root");
		Element root1 = doc1.getDocumentElement();

		Element child1a = doc1.createElement("bargle");
		child1a.setTextContent("789");
		SchemaUtil.appendChild(root1, child1a, schema, "Zippy");
		assertEquals(1, DOMUtil.getChildren(root1).size());
		assertEquals("789", DOMUtil.getChildren(root1).get(0).getTextContent());

		Element child1b = doc1.createElement("bar");
		child1b.setTextContent("456");
		SchemaUtil.appendChild(root1, child1b, schema, "Zippy");
		assertEquals(2, DOMUtil.getChildren(root1).size());
		assertEquals("456", DOMUtil.getChildren(root1).get(0).getTextContent());
		assertEquals("789", DOMUtil.getChildren(root1).get(1).getTextContent());

		Element child1c = doc1.createElement("argle");
		child1c.setTextContent("123");
		SchemaUtil.appendChild(root1, child1c, schema, "Zippy");
		assertEquals(3, DOMUtil.getChildren(root1).size());
		assertEquals("123", DOMUtil.getChildren(root1).get(0).getTextContent());
		assertEquals("456", DOMUtil.getChildren(root1).get(1).getTextContent());
		assertEquals("789", DOMUtil.getChildren(root1).get(2).getTextContent());

		// the second set will work from the inside out
		Document doc2 = XMLUtil.newDocument("root");
		Element root2 = doc2.getDocumentElement();

		Element child2a = doc2.createElement("bar");
		child2a.setTextContent("456");
		SchemaUtil.appendChild(root2, child2a, schema, "Zippy");
		assertEquals(1, DOMUtil.getChildren(root2).size());
		assertEquals("456", DOMUtil.getChildren(root2).get(0).getTextContent());

		Element child2b = doc2.createElement("argle");
		child2b.setTextContent("123");
		SchemaUtil.appendChild(root2, child2b, schema, "Zippy");
		assertEquals(2, DOMUtil.getChildren(root2).size());
		assertEquals("123", DOMUtil.getChildren(root2).get(0).getTextContent());
		assertEquals("456", DOMUtil.getChildren(root2).get(1).getTextContent());

		Element child2c = doc2.createElement("bargle");
		child2c.setTextContent("789");
		SchemaUtil.appendChild(root2, child2c, schema, "Zippy");
		assertEquals(3, DOMUtil.getChildren(root2).size());
		assertEquals("123", DOMUtil.getChildren(root2).get(0).getTextContent());
		assertEquals("456", DOMUtil.getChildren(root2).get(1).getTextContent());
		assertEquals("789", DOMUtil.getChildren(root2).get(2).getTextContent());
	}


	// this test exists for some corner cases identified by coverage
	public void testAppendChildCornerCases() throws Exception {
		final String schemaXml = "<xsd:schema xmlns:xsd=\"" + XMLConstants.W3C_XML_SCHEMA_NS_URI + "\">" +
		                         "<xsd:element name=\"root\" type=\"AddressType\"/>" + ADDRESS_TYPE + "</xsd:schema>";

		Document schema = XMLUtil.parse(schemaXml);
		Document doc = XMLUtil.newDocument("root");
		Element root = doc.getDocumentElement();

		// add this to the middle of the element, so that we don't hit the
		// "end of type" shortcut
		Element child1 = doc.createElement("city");
		child1.setTextContent("foo");
		SchemaUtil.appendChild(root, child1, schema, ADDRESS_TYPE_NAME);
		assertEquals(1, DOMUtil.getChildren(root).size());
		assertEquals("foo", DOMUtil.getChildren(root).get(0).getTextContent());

		// this has to go before any existing elements
		Element child2 = doc.createElement("street1");
		child2.setTextContent("bar");
		SchemaUtil.appendChild(root, child2, schema, ADDRESS_TYPE_NAME);
		assertEquals(2, DOMUtil.getChildren(root).size());
		assertEquals("bar", DOMUtil.getChildren(root).get(0).getTextContent());
		assertEquals("foo", DOMUtil.getChildren(root).get(1).getTextContent());
	}
}
