package com.nkhoang.common.xml;

import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.NamespaceContext;


import com.nkhoang.common.collections.MapBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import junit.framework.TestCase;


public class XPathUtilTest extends TestCase {

	//----------------------------------------------------------------------------
	//  Setup
	//----------------------------------------------------------------------------

	// constants for prefixes and namespace URIs
	private final static String P1  = "x";
	private final static String NS1 = "argle";
	private final static String P2  = "y";
	private final static String NS2 = "bargle";
	private final static String P3  = "y";
	private final static String NS3 = NS2;


	//----------------------------------------------------------------------------
	//  Test Cases
	//----------------------------------------------------------------------------

	public void testCreateNamespaceContext() throws Exception {

		NamespaceContext context = XPathUtil.createNamespaceContext(
			new MapBuilder<String, String>(new HashMap<String, String>()).put(P1, NS1).put(P2, NS2).put(P3, NS3)
			                                                             .toMap());

		assertEquals(NS1, context.getNamespaceURI(P1));
		assertEquals(NS2, context.getNamespaceURI(P2));
		assertEquals(NS3, context.getNamespaceURI(P3));
		assertEquals(P1, context.getPrefix(NS1));
		assertTrue(context.getPrefix(NS2).equals(P2) || context.getPrefix(NS2).equals(P3));
		assertTrue(context.getPrefix(NS3).equals(P2) || context.getPrefix(NS3).equals(P3));

		// FIXME - add tests for spec'd associations
	}


	public void testXPathExtracts() throws Exception {
		Document doc = XMLUtil.newDocument("root");
		Element root = doc.getDocumentElement();
		Element a = DOMUtil.addChild(root, "foo");
		Element b1 = DOMUtil.addChild(a, "bar");
		Element b2 = DOMUtil.addChild(a, "bar");
		Element c21 = DOMUtil.addChild(b2, "baz");
		Element c22 = DOMUtil.addChild(b2, "baz");
		b1.setAttribute("bargle", "wargle");
		b2.setAttribute("bargle", "zargle");

		assertEquals("wargle", XPathUtil.selectValue(b1, "@bargle"));

		assertSame(a, XPathUtil.selectNode(doc, "/root/foo"));
		assertSame(a, XPathUtil.selectNode(doc, "//foo"));
		assertSame(a, XPathUtil.selectNode(root, "./foo"));
		assertSame(b1, XPathUtil.selectNode(root, "//*[@bargle='wargle']"));

		List<Node> nl1 = XPathUtil.selectNodes(root, "./foo");
		assertEquals(1, nl1.size());
		assertSame(a, nl1.get(0));

		List<Node> nl2 = XPathUtil.selectNodes(root, "//baz");
		assertEquals(2, nl2.size());
		assertSame(c21, nl2.get(0));
		assertSame(c22, nl2.get(1));
	}


	public void testXPathExtractsWithNamespaces() throws Exception {
		Document doc = XMLUtil.newDocument("root");
		Element root = doc.getDocumentElement();
		Element a = DOMUtil.addChild(root, NS1, "foo");
		Element b1 = DOMUtil.addChild(a, NS1, "bar");
		Element b2 = DOMUtil.addChild(a, NS2, "bar");
		Element c21 = DOMUtil.addChild(b2, NS2, "baz");
		Element c22 = DOMUtil.addChild(b2, null, "baz");
		b1.setAttribute("bargle", "wargle");
		b2.setAttribute("bargle", "zargle");

		NamespaceContext nsContext = XPathUtil.createNamespaceContext(
			new MapBuilder<String, String>(new HashMap<String, String>()).put("x", NS1).put("y", NS2).toMap());

		assertNull(XPathUtil.selectNode(doc, "/root/foo", nsContext));
		assertSame(a, XPathUtil.selectNode(doc, "/root/x:foo", nsContext));
		assertSame(a, XPathUtil.selectNode(doc, "//x:foo", nsContext));
		assertSame(a, XPathUtil.selectNode(root, "./x:foo", nsContext));

		List<Node> nl1 = XPathUtil.selectNodes(root, "./x:foo", nsContext);
		assertEquals(1, nl1.size());
		assertSame(a, nl1.get(0));

		List<Node> nl2 = XPathUtil.selectNodes(root, "//y:baz", nsContext);
		assertEquals(1, nl2.size());
		assertSame(c21, nl2.get(0));

		List<Node> nl3 = XPathUtil.selectNodes(root, "//baz", nsContext);
		assertEquals(1, nl3.size());
		assertSame(c22, nl3.get(0));
	}
}
