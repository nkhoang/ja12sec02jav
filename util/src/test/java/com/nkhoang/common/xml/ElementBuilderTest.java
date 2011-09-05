package com.nkhoang.common.xml;

import java.util.ArrayList;
import java.util.List;


import com.nkhoang.common.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import junit.framework.TestCase;

public class ElementBuilderTest extends TestCase {

	//----------------------------------------------------------------------------
	//  Setup
	//----------------------------------------------------------------------------

	private final static String TEST_NS = "foo";

	private final static String EL_ROOT = "root";
	private final static String EL_L1   = "l1";

	private final static String AT_KEY1 = "argle";
	private final static String AT_VAL1 = "bargle";

	private final static String TEXT_1 = "A quick brown fox";
	private final static String TEXT_2 = " jumped over the lazy dog";


	//----------------------------------------------------------------------------
	//  Test methods
	//----------------------------------------------------------------------------

	public void testCreateSimpleElement() throws Exception {
		Document dom = new ElementBuilder(EL_ROOT).toDocument();
		assertNotNull(dom);

		Element root = dom.getDocumentElement();
		assertNotNull(root);
		assertNull(root.getNamespaceURI());
		assertEquals(EL_ROOT, root.getTagName());
		assertEquals(0, DOMUtil.getChildren(root).size());
	}


	public void testCreateNamespacedElement() throws Exception {
		Document dom = new ElementBuilder(TEST_NS, EL_ROOT).toDocument();
		assertNotNull(dom);

		Element root = dom.getDocumentElement();
		assertNotNull(root);
		assertEquals(TEST_NS, root.getNamespaceURI());
		assertEquals(EL_ROOT, root.getTagName());
		assertEquals(EL_ROOT, root.getLocalName());
		assertEquals(0, DOMUtil.getChildren(root).size());
	}


	public void testCreateNestedElement() throws Exception {
		Document dom = new ElementBuilder(EL_ROOT).appendElement(new ElementBuilder(EL_L1)).toDocument();
		assertNotNull(dom);

		Element root = dom.getDocumentElement();
		assertNotNull(root);
		assertNull(root.getNamespaceURI());
		assertEquals(EL_ROOT, root.getTagName());
		assertEquals(1, DOMUtil.getChildren(root).size());

		Element l1 = DOMUtil.getChildren(root).get(0);
		assertNull(l1.getNamespaceURI());
		assertEquals(EL_L1, l1.getTagName());
	}


	public void testCreateAttribute() throws Exception {
		Document dom = new ElementBuilder(EL_ROOT).appendAttribute(AT_KEY1, AT_VAL1).toDocument();
		assertNotNull(dom);

		Element root = dom.getDocumentElement();
		assertNotNull(root);
		assertNull(root.getNamespaceURI());
		assertEquals(EL_ROOT, root.getTagName());
		assertEquals(AT_VAL1, root.getAttribute(AT_KEY1));
	}


	public void testCreateText() throws Exception {
		Document dom = new ElementBuilder(EL_ROOT).appendText(TEXT_1).appendText(TEXT_2).toDocument();
		assertNotNull(dom);

		Element root = dom.getDocumentElement();
		assertNotNull(root);
		assertNull(root.getNamespaceURI());
		assertEquals(EL_ROOT, root.getTagName());
		assertEquals(TEXT_1 + TEXT_2, DOMUtil.getText(root));

		List<String> textNodes = new ArrayList<String>();
		for (Node child : DOMUtil.nodeIterable(root.getChildNodes())) {
			if (child instanceof Text) {
				textNodes.add(child.getTextContent());
			}
		}
		assertEquals(2, textNodes.size());
		assertEquals(TEXT_1, textNodes.get(0));
		assertEquals(TEXT_2, textNodes.get(1));
	}


	public void testAppendToElement() throws Exception {
		Document dom = XMLUtil.newDocument(EL_ROOT);
		new ElementBuilder(EL_L1).appendTo(dom.getDocumentElement());

		Element root = dom.getDocumentElement();
		assertEquals(1, DOMUtil.getChildren(root).size());

		Element l1 = DOMUtil.getChildren(root).get(0);
		assertNull(l1.getNamespaceURI());
		assertEquals(EL_L1, l1.getTagName());
	}


	public void testToString() throws Exception {
		String str = new ElementBuilder(EL_ROOT).appendText(TEXT_1).toString();
		assertTrue(
			str.indexOf("<" + EL_ROOT + ">" + TEXT_1 + "</" + EL_ROOT + ">") > 0);
	}


	public void testToPrettyString() throws Exception {
		String str = new ElementBuilder(EL_ROOT).appendText(TEXT_1).toPrettyString();
		assertTrue(
			StringUtil.linkedIndexOf(
				str, "<" + EL_ROOT + ">", TEXT_1, "</" + EL_ROOT + ">") > 0);
	}
}
