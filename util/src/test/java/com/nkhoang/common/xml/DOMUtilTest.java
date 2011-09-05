
package com.nkhoang.common.xml;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class DOMUtilTest extends TestCase {

  public DOMUtilTest(String name) {
    super(name);
  }


//----------------------------------------------------------------------------
//  Support code
//----------------------------------------------------------------------------

  /**
   *  Creates a test document, along with its root element.
   *  @return The root element of the document.
   */
  private Element createTestDocument(String rootName) throws Exception {
    Document doc = DocumentBuilderFactory.newInstance()
                                         .newDocumentBuilder()
                                         .getDOMImplementation()
                                         .createDocument(null, rootName, null);
    return doc.getDocumentElement();
  }


  /**
   *  Adds a child to the passed element, with optional namespace.
   *
   *  @param  elem    The parent element.
   *  @param  nsTag   The tag used to identify the child's namespace. May
   *                  be <code>null</code>, in which case the element is
   *                  not tagged, and <code>nsUri</code> (if it's used)
   *                  is set as the default namespace of the element.
   *  @param  nsUri   Namespace URI. May be <code>null</code>, in which
   *                  case the element is not associated with a namespace.
   *  @param  name    The name of the child element. If namespace params
   *                  are provided, this is the local name.
   *  @return The newly created child.
   */
  private Element createElement(Element elem, String nsTag,
                                String nsUri, String name) {
    name = (nsTag != null) ? name = nsTag + ":" + name
                           : name;
    Document doc = elem.getOwnerDocument();
    Element result = doc.createElementNS(nsUri, name);
    elem.appendChild(result);
    return result;
  }



//----------------------------------------------------------------------------
//  Test methods
//----------------------------------------------------------------------------


  public void testGetNamespacePrefixes() throws Exception {
    String xml = "<a:foo xmlns:a=\"zzz\" xmlns=\"yyy\" xmlns:b=\"xxx\" b:foo=\"q\"/>";
    Map prefixes = DOMUtil.getNamespacePrefixes(
        DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
        new ByteArrayInputStream(xml.getBytes())));
    assertEquals(3, prefixes.size());
    assertEquals("a", prefixes.get("zzz"));
    assertNull(prefixes.get("yyy"));
    assertEquals("b", prefixes.get("xxx"));
  }


  public void testGetChildrenByName() throws Exception {
    final String ROOT = "root";
    final String A = "A";
    final String B = "B";
    final String C = "C";

    Element root = createTestDocument(ROOT);
    Element a = createElement(root, null, null, A);
    Element b = createElement(root, null, null, B);
                createElement(b, null, null, B);
    Element c = createElement(root, null, null, C);
                createElement(c, null, null, C);
                createElement(c, null, null, C);
                createElement(c, null, null, C);

    assertSame(a, DOMUtil.getChildByName(root, A));
    assertSame(a, DOMUtil.getRequiredChildByName(root, A));
    try {
      DOMUtil.getRequiredChildByName(a, A);
      fail("getRequiredChildByName() didn't throw on missing child");
    }
    catch (IllegalArgumentException e) {
      // this is success
    }
    assertNull(DOMUtil.getChildByName(a, A));
    assertNotNull(DOMUtil.getChildByName(b, B));
    try {
      DOMUtil.getChildByName(c, C);
      fail("getChildByName() didn't throw on multiple children");
    }
    catch (IllegalArgumentException e) {
      // this is success
    }

    assertEquals(0, DOMUtil.getChildrenByName(a, A).size());
    assertEquals(1, DOMUtil.getChildrenByName(b, B).size());
    assertEquals(3, DOMUtil.getChildrenByName(c, C).size());
  }


  public void testGetChildrenByNameNS() throws Exception {
    final String rootName = "root";
    final String childName = "child";
    final String nsTag = "test";
    final String nsUri = "http://hmsonline.com";

    Element root = createTestDocument(rootName);
    Element a = createElement(root, null, null, childName);
    Element b = createElement(root, null, nsUri, childName);
    Element c = createElement(root, nsTag, nsUri, childName);

    assertEquals(1, DOMUtil.getChildrenByName(root, null, childName).size());
    assertSame(a, DOMUtil.getChildrenByName(root, null, childName).get(0));

    assertEquals(2, DOMUtil.getChildrenByName(root, nsUri, childName).size());
    assertSame(b, DOMUtil.getChildrenByName(root, nsUri, childName).get(0));
    assertSame(c, DOMUtil.getChildrenByName(root, nsUri, childName).get(1));

    assertEquals(a, DOMUtil.getChildByName(root, null, childName));
    try {
      DOMUtil.getChildByName(root, nsUri, childName);
    }
    catch (IllegalArgumentException e) {
      // success
    }
  }


  public void testGetAndSetText() throws Exception {
    final String A_TEXT = "How doth the little crocodile";
    final String B_TEXT = "Improve his shining tail";
    final String NEW_TEXT = "And pour the waters of the Nile";

    Element root = createTestDocument("root");
    Document doc = root.getOwnerDocument();

    Element a = DOMUtil.addChild(root, null, "A");
    a.setTextContent(A_TEXT);
    Element b = DOMUtil.addChild(a, null, "B");
    b.setTextContent(B_TEXT);

    assertEquals("A child count", 2, a.getChildNodes().getLength());
    assertEquals("A text", A_TEXT, DOMUtil.getText(a));
    assertEquals("B text", B_TEXT, DOMUtil.getText(b));

    // repeat all assertions to make sure we didn't damage B
    DOMUtil.setText(a, NEW_TEXT);
    assertEquals("A child count", 2, a.getChildNodes().getLength());
    assertEquals("A text", NEW_TEXT, DOMUtil.getText(a));
    assertEquals("B text", B_TEXT, DOMUtil.getText(b));

    // append text to A with an intervening element, verify it's
    // normalized on retrieval
    DOMUtil.addChild(a, null, "C");
    a.appendChild(doc.createTextNode(A_TEXT));
    assertEquals("combined text", NEW_TEXT + A_TEXT, DOMUtil.getText(a));
  }
  
  public void testGetAndSetAttrText() throws Exception {
    final String A_ATTR = "How doth the little crocodile";
    final String B_ATTR = "Improve his shining tail";
    final String NEW_ATTR = "And pour the waters of the Nile";
    final String DOC = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><a at=\"" 
                       + A_ATTR + "\"><b at=\"" + B_ATTR + "\"></b></a>";
        
    //setup
    Document dom = XMLUtil.parse(DOC);
    Node nodeA = XPathUtil.selectNode(dom, "//a");
    Node nodeB = XPathUtil.selectNode(dom, "//b");
    Attr a = org.apache.xerces.util.DOMUtil.getAttr((Element) nodeA, "at");
    Attr b = org.apache.xerces.util.DOMUtil.getAttr((Element) nodeB, "at");
    
    assertEquals("A text", A_ATTR, DOMUtil.getText(a));
    assertEquals("B text", B_ATTR, DOMUtil.getText(b));

    // repeat all assertions to make sure we didn't damage B
    DOMUtil.setText(a, NEW_ATTR);
    assertEquals("A text", NEW_ATTR, DOMUtil.getText(a));
    assertEquals("B text", B_ATTR, DOMUtil.getText(b));
  }


  public void testAddChild() throws Exception {
    final String TEST_NS = "local:local";
    final String TEST_PREFIX = "local";
    final String TEST_LOCALNAME = "baz";
    final String TEST_QUALNAME = TEST_PREFIX + ":" + TEST_LOCALNAME;

    Element root = createTestDocument("root");

    // adding children with explicit namespaces

    Element a = DOMUtil.addChild(root, null, TEST_LOCALNAME);
    assertSame(root, a.getParentNode());
    assertEquals(TEST_LOCALNAME, a.getTagName());
    assertNull(a.getNamespaceURI());
    assertNull(a.getPrefix());
//    assertNull(a.getLocalName());   // depends on the underlying creation method

    Element b = DOMUtil.addChild(root, TEST_NS, TEST_QUALNAME);
    assertSame(root, b.getParentNode());
    assertEquals(TEST_QUALNAME, b.getTagName());
    assertEquals(TEST_NS, b.getNamespaceURI());
    assertEquals(TEST_PREFIX, b.getPrefix());
    assertEquals(TEST_LOCALNAME, b.getLocalName());

    // adding children that inherit their parent's namespace

    Element a1 = DOMUtil.addChild(a, TEST_LOCALNAME);
    assertSame(a, a1.getParentNode());
    assertEquals(TEST_LOCALNAME, a1.getTagName());
    assertNull(a1.getNamespaceURI());
    assertNull(a1.getPrefix());

    Element b1 = DOMUtil.addChild(b, TEST_LOCALNAME);
    assertSame(b, b1.getParentNode());
    assertEquals(TEST_QUALNAME, b1.getTagName());
    assertEquals(TEST_NS, b1.getNamespaceURI());
    assertEquals(TEST_PREFIX, b1.getPrefix());
    assertEquals(TEST_LOCALNAME, b1.getLocalName());
  }


  public void testAddChildWithText() throws Exception {
    final String TEST_NS = "local:local";
    final String TEST_TEXT = "a quick brown fox, yadda yadda";

    // adding children with text

    Element root = createTestDocument("root");

    Element a = DOMUtil.addChildWithText(root, TEST_NS, "A", null);
    assertSame(root, a.getParentNode());
    assertEquals(TEST_NS, a.getNamespaceURI());
    assertEquals("", DOMUtil.getText(a));

    Element b = DOMUtil.addChildWithText(root, null, "B", TEST_TEXT);
    assertSame(root, b.getParentNode());
    assertNull(b.getNamespaceURI());
    assertEquals(TEST_TEXT, DOMUtil.getText(b));

    // now the variants that inherit the parent namespace

    Element a1 = DOMUtil.addChildWithText(a, "A", TEST_TEXT);
    assertSame(a, a1.getParentNode());
    assertEquals(TEST_NS, a1.getNamespaceURI());
    assertEquals(TEST_TEXT, DOMUtil.getText(a1));

    Element b1 = DOMUtil.addChildWithText(b, "B", null);
    assertSame(b, b1.getParentNode());
    assertNull(b1.getNamespaceURI());
    assertEquals("", DOMUtil.getText(b1));
  }


  public void testNodeIterator() throws Exception
  {
    final List<Node> l = new ArrayList<Node>(
        Arrays.asList(XMLUtil.newDocument(),
                      XMLUtil.newDocument(),
                      XMLUtil.newDocument()));
    NodeList nl = new NodeList() {
        public int getLength() { return l.size(); }
        public Node item(int index) { return l.get(index); }
      };

    int idx = 0;
    for(Node n : DOMUtil.nodeIterable(nl)) {
      assertSame(l.get(idx), n);
      ++idx;
    }
  }


  public void testElementSiblingAccessors() throws Exception {
    Document doc = DocumentBuilderFactory.newInstance()
                                         .newDocumentBuilder()
                                         .newDocument();
    Element root = doc.createElement("root");
    Element a = doc.createElement("A");
    Element b = doc.createElement("B");
    Element c = doc.createElement("C");

    doc.appendChild(root);
    root.appendChild(a);
    root.appendChild(doc.createTextNode("ibbledy bibbledy boo"));
    root.appendChild(doc.createTextNode("this document is surely foo"));
    root.appendChild(b);
    root.appendChild(doc.createTextNode("ibbledy bibbledy baa"));
    root.appendChild(doc.createTextNode("it makes me want to go waaah"));
    root.appendChild(c);
    root.appendChild(doc.createTextNode("ibbledy bibbledy bonk"));
    root.appendChild(doc.createTextNode("this test had better not plonk"));

    assertSame(a, DOMUtil.getPreviousElementSibling(b));
    assertNotSame(a, b.getPreviousSibling());
    assertSame(c, DOMUtil.getNextElementSibling(b));
    assertNotSame(c, b.getNextSibling());
  }


  public void testGetPath() throws Exception {
    Document doc = XMLUtil.newDocument("root");
    Element root = doc.getDocumentElement();
    Element a = DOMUtil.addChild(root, "foo");
    Element b = DOMUtil.addChild(a, "bar");
    a.setAttribute("name", "baz");
    b.setAttribute("argle", "bargle");

    assertEquals("/", DOMUtil.getPath(null));
    assertEquals("/root", DOMUtil.getPath(root));
    assertEquals("/root/foo", DOMUtil.getPath(a));
    assertEquals("/root/foo/bar", DOMUtil.getPath(b));

    assertEquals("/root[name='']/foo[name='baz']/bar[name='']",
                 DOMUtil.getPath(b, "name"));
    assertEquals("/root[name=''][argle='']/foo[name='baz'][argle='']/bar[name=''][argle='bargle']",
                 DOMUtil.getPath(b, "name", "argle"));
  }


  public void testGetAbsolutePath() throws Exception {
    Document doc = XMLUtil.newDocument("root");
    Element root = doc.getDocumentElement();
    Element a = DOMUtil.addChild(root, "foo");
    Element b1 = DOMUtil.addChild(a, "bar");
    Element b2 = DOMUtil.addChild(a, "bar");
    Element c20 = DOMUtil.addChild(b2, "argle", "a:baz");
    Element c21 = DOMUtil.addChild(b2, "baz");
    Element c22 = DOMUtil.addChild(b2, "baz");

    assertEquals("/", DOMUtil.getAbsolutePath(null));
    assertEquals("/root", DOMUtil.getAbsolutePath(root));
    assertEquals("/root/foo", DOMUtil.getAbsolutePath(a));
    assertEquals("/root/foo/bar[1]", DOMUtil.getAbsolutePath(b1));
    assertEquals("/root/foo/bar[2]", DOMUtil.getAbsolutePath(b2));
    assertEquals("/root/foo/bar[2]/a:baz", DOMUtil.getAbsolutePath(c20));
    assertEquals("/root/foo/bar[2]/baz[1]", DOMUtil.getAbsolutePath(c21));
    assertEquals("/root/foo/bar[2]/baz[2]", DOMUtil.getAbsolutePath(c22));
  }


  public void testGetLocalName() throws Exception {
    Document doc = DocumentBuilderFactory.newInstance()
                                         .newDocumentBuilder()
                                         .newDocument();
    Element root = doc.createElement("root");
    Element c1 = doc.createElement("bar");
    Element c2 = doc.createElementNS("wibble", "w:bar");
    Element c3 = doc.createElementNS("wibble", "bar");
    doc.appendChild(root);
    root.appendChild(c1);
    root.appendChild(c2);
    root.appendChild(c3);

    assertEquals("bar", DOMUtil.getLocalName(c1));
    assertEquals("bar", DOMUtil.getLocalName(c2));
    assertEquals("bar", DOMUtil.getLocalName(c3));
  }


  public void testRecreateWithNamespace() throws Exception {
    Document doc = XMLUtil.newDocument("foo");
    Element foo = doc.getDocumentElement();
    Element bar = doc.createElementNS("bargle", "bar");
    foo.appendChild(doc.createTextNode("O for a Muse of fire, that would ascend"));
    foo.appendChild(bar);
    foo.appendChild(doc.createTextNode("The brightest heaven of invention,"));
    foo.setAttribute("qwerty", "uiop");
    foo.setAttributeNS("asdf", "ghjkl", "zxcvbnm");

    // verifies that we set up the testcase properly
    assertEquals(null, foo.getNamespaceURI());
    assertEquals("uiop", foo.getAttributeNS(null, "qwerty"));
    assertEquals("zxcvbnm", foo.getAttributeNS("asdf", "ghjkl"));
    assertEquals(3, foo.getChildNodes().getLength());
    assertSame(bar, foo.getChildNodes().item(1));

    // first test verifies that pieces get copied properly, as well as
    // ensuring that we replace the element in the document

    DOMUtil.recreateWithNamespace(foo, "foozle");
    Element foo2 = doc.getDocumentElement();
    assertNotSame(foo, foo2);
    assertEquals("foozle", foo2.getNamespaceURI());
    assertEquals("uiop", foo2.getAttributeNS(null, "qwerty"));
    assertEquals("zxcvbnm", foo2.getAttributeNS("asdf", "ghjkl"));
    assertEquals(0, foo.getChildNodes().getLength());
    assertEquals(3, foo2.getChildNodes().getLength());
    assertSame(bar, foo2.getChildNodes().item(1));

    // second test verifies that we properly replace an element in an element

    DOMUtil.recreateWithNamespace(bar, "foozle");
    Element bar2 = DOMUtil.getChildren(foo2).get(0);
    assertEquals("foozle", bar2.getNamespaceURI());
    assertNotSame(bar, bar2);
    assertSame(bar2, foo2.getChildNodes().item(1));
  }


  public void testApplyDefaultNamespace() {
    Document doc = XMLUtil.newDocument("foo");
    Element root = doc.getDocumentElement();
    Element a = DOMUtil.addChild(root, "arg");
    Element b = DOMUtil.addChild(root, "bargle", "bar");
    Element c = DOMUtil.addChild(b, null, "baz");

    a.setAttribute("bbb", "BBB");
    DOMUtil.setText(b, "qqqq");

    // this first set of assertions just verifies that we set things
    // up correctly

    assertNull(root.getNamespaceURI());
    assertNull(a.getNamespaceURI());
    assertEquals("bargle", b.getNamespaceURI());
    assertNull(c.getNamespaceURI());
    assertEquals("BBB", a.getAttribute("bbb"));
    assertEquals("qqqq", b.getTextContent());

    DOMUtil.applyDefaultNamespace(root, "foo");

    // since this method might create new nodes, we'll have to work
    // through the document again

    Element rootX = doc.getDocumentElement();
    Element aX = DOMUtil.getChildren(rootX).get(0);
    Element bX = DOMUtil.getChildren(rootX).get(1);
    Element cX = DOMUtil.getChildren(bX).get(0);

    assertEquals("foo", rootX.getNamespaceURI());
    assertEquals("foo", aX.getNamespaceURI());
    assertEquals("bargle", bX.getNamespaceURI());
    assertEquals("foo", cX.getNamespaceURI());
    assertEquals("BBB", aX.getAttribute("bbb"));
    assertEquals("qqqq", DOMUtil.getText(bX));
  }

  public void testAttributes() throws Exception
  {
    String emptyString = "  ";
    String validString = "foo";
    Integer intValue = 37;
    String intString = intValue.toString();

    Element el = createTestDocument("testElement");
    el.setAttribute("attr1", null);
    el.setAttribute("attr2", emptyString);
    el.setAttribute("attr3", validString);
    el.setAttribute("attr4", intString);

    assertEquals(null, DOMUtil.getOptionalAttribute(el, "attr0"));
    assertEquals(null, DOMUtil.getOptionalAttribute(el, "attr1"));
    assertEquals(null, DOMUtil.getOptionalAttribute(el, "attr2"));
    assertEquals(validString, DOMUtil.getOptionalAttribute(el, "attr3"));
    assertEquals(intString, DOMUtil.getOptionalAttribute(el, "attr4"));

    try {
      DOMUtil.getRequiredAttribute(el, "attr0");
      fail("IllegalArgumentException should have been thrown");
    } catch(IllegalArgumentException e) {
      // success
    }
    try {
      DOMUtil.getRequiredAttribute(el, "attr1");
      fail("IllegalArgumentException should have been thrown");
    } catch(IllegalArgumentException e) {
      // success
    }
    try {
      DOMUtil.getRequiredAttribute(el, "attr2");
      fail("IllegalArgumentException should have been thrown");
    } catch(IllegalArgumentException e) {
      // success
    }
    assertEquals(validString, DOMUtil.getRequiredAttribute(el, "attr3"));
    assertEquals(intString, DOMUtil.getRequiredAttribute(el, "attr4"));

    assertEquals(null, DOMUtil.getOptionalAttribute(el, "attr0",
                                                    Integer.class));
    assertEquals(null, DOMUtil.getOptionalAttribute(el, "attr1",
                                                    Integer.class));
    assertEquals(null, DOMUtil.getOptionalAttribute(el, "attr2",
                                                    Integer.class));
    try {
      DOMUtil.getOptionalAttribute(el, "attr3", Integer.class);
      fail("IllegalArgumentException should have been thrown");
    } catch(IllegalArgumentException e) {
      // success
    }
    assertEquals(intValue, DOMUtil.getOptionalAttribute(el, "attr4",
                                                        Integer.class));

    try {
      DOMUtil.getRequiredAttribute(el, "attr0", Integer.class);
      fail("IllegalArgumentException should have been thrown");
    } catch(IllegalArgumentException e) {
      // success
    }
    try {
      DOMUtil.getRequiredAttribute(el, "attr1", Integer.class);
      fail("IllegalArgumentException should have been thrown");
    } catch(IllegalArgumentException e) {
      // success
    }
    try {
      DOMUtil.getRequiredAttribute(el, "attr2", Integer.class);
      fail("IllegalArgumentException should have been thrown");
    } catch(IllegalArgumentException e) {
      // success
    }
    try {
      DOMUtil.getRequiredAttribute(el, "attr3", Integer.class);
      fail("IllegalArgumentException should have been thrown");
    } catch(IllegalArgumentException e) {
      // success
    }
    assertEquals(intValue, DOMUtil.getRequiredAttribute(el, "attr4",
                                                        Integer.class));
  }


  public void testDiff() throws Exception {
    Element e1 = XMLUtil.parse(
              "<foo>\n"
            + "  <bar>\n"
            + "  </bar>\n"
            + "</foo>\n")
            .getDocumentElement();

    Element e2 = XMLUtil.parse(
              "<foo>\n"
            + "    <bar/>\n"
            + "</foo>\n")
            .getDocumentElement();

    Element e3 = XMLUtil.parse(
              "<foo>\n"
            + "    <bar>\n"
            + "something here"
            + "    </bar>\n"
            + "</foo>\n")
            .getDocumentElement();

    Element e4 = XMLUtil.parse(
              "<foo>\n"
            + "    <bar>something here</bar>\n"
            + "</foo>\n")
            .getDocumentElement();

    Element e5 = XMLUtil.parse(
              "<foo>\n"
            + "    <bar/>\n"
            + "    <bargle/>\n"
            + "</foo>\n")
            .getDocumentElement();

    Element e6 = XMLUtil.parse(
              "<foo>\n"
            + "    <bar attr='ix'/>\n"
            + "</foo>\n")
            .getDocumentElement();

    Element e7 = XMLUtil.parse(
              "<foo>\n"
            + "    <bar attr='ox'/>\n"
            + "</foo>\n")
            .getDocumentElement();

    assertNull(DOMUtil.firstDifference(e1, e1, false));
    assertNull(DOMUtil.firstDifference(e2, e2, false));
    assertNull(DOMUtil.firstDifference(e3, e3, false));
    assertNull(DOMUtil.firstDifference(e4, e4, false));
    assertNull(DOMUtil.firstDifference(e5, e5, false));
    assertNull(DOMUtil.firstDifference(e6, e6, false));

    Element[] result01 = DOMUtil.firstDifference(e1, e2, false);
    assertEquals(e1, result01[0]);
    assertEquals(e2, result01[1]);

    assertNull(DOMUtil.firstDifference(e1, e2, true));

    Element[] result02 = DOMUtil.firstDifference(e1, e3, true);
    assertEquals(XPathUtil.selectNode(e1, "//bar"), result02[0]);
    assertEquals(XPathUtil.selectNode(e3, "//bar"), result02[1]);

    Element[] result03 = DOMUtil.firstDifference(e3, e4, true);
    assertEquals(XPathUtil.selectNode(e3, "//bar"), result03[0]);
    assertEquals(XPathUtil.selectNode(e4, "//bar"), result03[1]);

    Element[] result04 = DOMUtil.firstDifference(e1, e5, true);
    assertNull(result04[0]);
    assertEquals(XPathUtil.selectNode(e5, "//bargle"), result04[1]);

    Element[] result05 = DOMUtil.firstDifference(e2, e6, true);
    assertEquals(XPathUtil.selectNode(e2, "//bar"), result05[0]);
    assertEquals(XPathUtil.selectNode(e6, "//bar"), result05[1]);

    Element[] result06 = DOMUtil.firstDifference(e6, e7, true);
    assertEquals(XPathUtil.selectNode(e6, "//bar"), result06[0]);
    assertEquals(XPathUtil.selectNode(e7, "//bar"), result06[1]);
  }
}
