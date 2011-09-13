package com.nkhoang.common.xml.xdiff;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.nkhoang.common.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import junit.framework.TestCase;

public class XDiffTest extends TestCase {
	private final static String EL_ROOT  = "root";
	private final static String EL_FOO   = "foo";
	private final static String EL_BAR   = "bar";
	private final static String CONTENT1 = "content1";
	private final static String CONTENT2 = "content2";

	private final static String XML_1 = XMLUtil.XML_DECL + element(
		EL_ROOT, element(
		EL_FOO, CONTENT1, element(EL_BAR, CONTENT1)));

	private final static String XML_2 = XMLUtil.XML_DECL + element(
		EL_ROOT, element(
		EL_FOO, CONTENT1, element(EL_BAR, CONTENT1)));

	private final static String XML_3 = XMLUtil.XML_DECL + element(
		EL_ROOT, element(EL_FOO, element(EL_BAR, CONTENT1), element(EL_BAR, CONTENT2)));

	private final static String XML_4 = XMLUtil.XML_DECL + element(
		EL_ROOT, element(EL_FOO, element(EL_BAR, CONTENT2), element(EL_BAR, CONTENT1)));

	private final static String XML_5 = XMLUtil.XML_DECL + element(
		EL_ROOT, element(EL_FOO, element(EL_BAR, element(EL_BAR, CONTENT2))), element(EL_FOO, CONTENT2),
		element(EL_FOO, element(EL_BAR, CONTENT1)));

	private final static String XML_6 = XMLUtil.XML_DECL + element(
		EL_ROOT, element(EL_FOO, element(EL_BAR, element(EL_BAR, CONTENT2))), element(EL_FOO, CONTENT2), element(
		EL_FOO, "<" + EL_BAR + " attribute=\"attrVal\">" + CONTENT1 + "</" + EL_BAR + ">"));

	private final static String XML_7 = XMLUtil.XML_DECL + element(
		EL_ROOT, element(EL_FOO, element(EL_BAR, element(EL_BAR, CONTENT2))), element(EL_FOO, CONTENT2), element(
		EL_FOO, "<" + EL_BAR + " attribute=\"attrVal2\">" + CONTENT1 + "</" + EL_BAR + ">"));

	private final static String XML_8 = XMLUtil.XML_DECL + element(
		EL_ROOT, element(EL_FOO, element(EL_BAR, element(EL_BAR, CONTENT2))), element(EL_FOO, CONTENT2), element(
		EL_FOO, "<" + EL_BAR + " attribute2=\"attrVal2\">" + CONTENT1 + "</" + EL_BAR + ">"));

	public void testCompute() {
		DocumentBuilder builder = null;
		try {
			builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		}
		catch (ParserConfigurationException pce) {

		}

		Document d1 = builder.newDocument();
		Document d2 = builder.newDocument();

		Element r1 = d1.createElementNS("foo", "foo:root");
		Element r2 = d2.createElementNS("foo", "foo:root");
		List<XDiffEditStep> editScript = XDiff.compute(r1, r2, true);
		assertEquals(editScript.size(), 0);

		Element c1 = d1.createElementNS("foo", "foo:child1");
		c1.setTextContent("Child-1");
		r1.appendChild(c1);
		editScript = XDiff.compute(r1, r2, true);
		assertEquals(editScript.size(), 1);

		Element c2 = d2.createElementNS("foo", "foo:child1");
		c2.setTextContent("Child-2");
		r2.appendChild(c2);
		editScript = XDiff.compute(r1, r2, true);
		assertEquals(editScript.size(), 1);
		assertEquals(editScript.get(0)._op, XDiffOperation.UPDATE_LEAF);

		c1 = d1.createElementNS("foo", "foo:child1_new");
		c1.setTextContent("Child-1_new");
		r1.appendChild(c1);
		editScript = XDiff.compute(r1, r2, true);
		assertEquals(editScript.size(), 2);
		assertEquals(editScript.get(0)._op, XDiffOperation.UPDATE_LEAF);
		assertEquals(editScript.get(1)._op, XDiffOperation.DELETE_SUBTREE);
	}

	public void testLargeDiff() throws Exception {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = builderFactory.newDocumentBuilder();

		Document doc1 = builder.parse(getInput("Doc1.xml"));
		Document doc2 = builder.parse(getInput("Doc2.xml"));

		List<XDiffEditStep> editScript = XDiff.compute(doc1, doc2);
		assertEquals("This script should be length four", editScript.size(), 4);
	}

	public void testFromStrings() {
		Document doc1 = XMLUtil.parse(XML_1);
		Document doc2 = XMLUtil.parse(XML_2);
		List<XDiffEditStep> editScript = XDiff.compute(
			doc1.getDocumentElement(), doc2.getDocumentElement(), true);
		assertEquals("These two documents are the same", editScript.size(), 0);

		Document doc3 = XMLUtil.parse(XML_3);
		Document doc4 = XMLUtil.parse(XML_4);
		editScript = XDiff.compute(
			doc3.getDocumentElement(), doc4.getDocumentElement(), true);
		assertEquals("These two documents are the same", editScript.size(), 0);

		Document doc5 = XMLUtil.parse(XML_5);
		editScript = XDiff.compute(
			doc4.getDocumentElement(), doc5.getDocumentElement(), true);
		assertEquals("This edit should be three steps.", editScript.size(), 3);
		//apply the changes and assert that the two are now equal
		for (XDiffEditStep step : editScript) {
			step.applyOperation();
		}
		editScript = XDiff.compute(
			doc4.getDocumentElement(), doc5.getDocumentElement(), true);
		assertTrue("The two documents should now be the same", editScript.isEmpty());

		Document doc6 = XMLUtil.parse(XML_6);
		editScript = XDiff.compute(
			doc5.getDocumentElement(), doc6.getDocumentElement(), true);
		assertEquals("This edit should be one step.", editScript.size(), 1);

		Document doc7 = XMLUtil.parse(XML_7);
		editScript = XDiff.compute(
			doc6.getDocumentElement(), doc7.getDocumentElement(), true);
		assertEquals("This edit should be one step.", editScript.size(), 1);

		Document doc8 = XMLUtil.parse(XML_8);
		editScript = XDiff.compute(
			doc7.getDocumentElement(), doc8.getDocumentElement(), true);
		assertEquals("This edit should only require two steps", editScript.size(), 2);
		//apply the changes and assert that the two are now equal
		for (XDiffEditStep step : editScript) {
			step.applyOperation();
		}
		editScript = XDiff.compute(
			doc7.getDocumentElement(), doc8.getDocumentElement(), true);
		assertTrue("The two documents should now be the same", editScript.isEmpty());

	}

	public void testDocumentMarkup() {
		Document doc4 = XMLUtil.parse(XML_4);
		Document doc5 = XMLUtil.parse(XML_5);

		String doc4Pre = XMLUtil.serialize(doc4);
		String doc5Pre = XMLUtil.serialize(doc5);

		List<XDiffEditStep> editScript = XDiff.compute(
			doc4.getDocumentElement(), doc5.getDocumentElement(), true);
		assertEquals("This edit should be three steps.", editScript.size(), 3);
		//apply the changes and assert that the two are now equal
		for (XDiffEditStep step : editScript) {
			step.applyOperationAsMarkup();
		}

		Document original = XDiff.restoreOriginal(doc4);
		Document other = XDiff.restoreOther(doc4);
		String doc4Post = XMLUtil.serialize(original);
		String doc5Post = XMLUtil.serialize(other);

		assertEquals("These two documents should be the same", doc4Pre, doc4Post);

		editScript = XDiff.compute(
			XMLUtil.parse(doc5Pre).getDocumentElement(), XMLUtil.parse(doc5Post).getDocumentElement(), true);
		assertEquals("This script should be zero length", editScript.size(), 0);

		Document doc7 = XMLUtil.parse(XML_7);
		Document doc8 = XMLUtil.parse(XML_8);

		String doc7Pre = XMLUtil.serialize(doc7);
		String doc8Pre = XMLUtil.serialize(doc8);

		editScript = XDiff.compute(
			doc7.getDocumentElement(), doc8.getDocumentElement(), true);
		assertEquals("This edit should only require two steps", editScript.size(), 2);
		for (XDiffEditStep step : editScript) {
			step.applyOperationAsMarkup();
		}

		original = XDiff.restoreOriginal(doc7);
		other = XDiff.restoreOther(doc7);
		String doc7Post = XMLUtil.serialize(original);
		String doc8Post = XMLUtil.serialize(other);

		assertEquals("These two documents should be the same", doc7Pre, doc7Post);
		assertEquals("These two documents should be the same", doc8Pre, doc8Post);
		assertTrue(true);
	}

	private static String element(String name, String... content) {
		StringBuilder sb = new StringBuilder();
		sb.append("<").append(name).append(">");
		for (String child : content) {
			sb.append(child);
		}
		sb.append("</").append(name).append(">");
		return sb.toString();
	}

	private InputStream getInput(String path) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
	}
}
