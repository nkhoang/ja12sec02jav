
package com.nkhoang.common.xml;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;



public class DOMEqualityUtilTest extends TestCase {

  public DOMEqualityUtilTest(String name) {
    super(name);
  }


//----------------------------------------------------------------------------
//  Setup
//----------------------------------------------------------------------------

  // these are built into the document
  private final static String EL_ROOT         = "root";
  private final static String EL_A1           = "a1";
  private final static String EL_B1           = "b1";
  private final static String EL_C1           = "c1";
  private final static String EL_C2_CHILD     = "c2-common";

  // these get added later
  private final static String AT_KEY_1        = "argle";
  private final static String AT_VAL_1a       = "bargle";
  private final static String AT_VAL_1b       = "wargle";
  private final static String AT_KEY_2        = "zippy";
  private final static String AT_VAL_2       = "mrToad";


  private Document _doc;
  private Element _root;
  private Element _a1;
  private Element _b1;
  private Element _c1;
  private Element _c2a;
  private Element _c2b;
  private Element _c2c;
  private Element _c2d;


  @Override
  protected void setUp() {
    _doc = XMLUtil.newDocument();
    _root = _doc.createElement(EL_ROOT);
    _a1 = _doc.createElement(EL_A1);
    _b1 = _doc.createElement(EL_B1);
    _c1 = _doc.createElement(EL_C1);
    _c2a = _doc.createElement(EL_C2_CHILD);
    _c2b = _doc.createElement(EL_C2_CHILD);
    _c2c = _doc.createElement(EL_C2_CHILD);
    _c2d = _doc.createElement(EL_C2_CHILD);

    _root.appendChild(_a1);
    _root.appendChild(_b1);
    _root.appendChild(_c1);
    _c1.appendChild(_c2a);
    _c1.appendChild(_c2b);
    _c1.appendChild(_c2c);
    _c1.appendChild(_c2d);
  }


//----------------------------------------------------------------------------
//  Support Code
//----------------------------------------------------------------------------


//----------------------------------------------------------------------------
//  Test Cases
//----------------------------------------------------------------------------

  public void testIsTextEqual() throws Exception {
    _c2a.appendChild(_doc.createTextNode("   "));
    _c2a.appendChild(_doc.createTextNode("foo"));
    _c2a.appendChild(_doc.createTextNode("bar"));
    _c2a.appendChild(_doc.createTextNode("   "));

    _c2b.appendChild(_doc.createTextNode("foo"));
    _c2b.appendChild(_doc.createTextNode("bar"));

    _c2c.appendChild(_doc.createTextNode("foobar"));

    _c2d.appendChild(_doc.createTextNode("foo"));
    _c2d.appendChild(_doc.createTextNode(""));
    _c2d.appendChild(_doc.createTextNode("bar"));

    // ignoring empty text
    assertTrue(DOMEqualityUtil.isTextEqual(_c2a, _c2b, true));
    assertTrue(DOMEqualityUtil.isTextEqual(_c2a, _c2c, true));
    assertTrue(DOMEqualityUtil.isTextEqual(_c2b, _c2c, true));
    assertFalse(DOMEqualityUtil.isTextEqual(_c2a, _c2d, true));
    assertFalse(DOMEqualityUtil.isTextEqual(_c2b, _c2d, true));
    assertFalse(DOMEqualityUtil.isTextEqual(_c2c, _c2d, true));

    assertTrue(DOMEqualityUtil.isTextEqual(_c2b, _c2a, true));
    assertTrue(DOMEqualityUtil.isTextEqual(_c2c, _c2a, true));
    assertTrue(DOMEqualityUtil.isTextEqual(_c2c, _c2b, true));
    assertFalse(DOMEqualityUtil.isTextEqual(_c2d, _c2a, true));
    assertFalse(DOMEqualityUtil.isTextEqual(_c2d, _c2b, true));
    assertFalse(DOMEqualityUtil.isTextEqual(_c2d, _c2c, true));

    // not ignoring empty text
    assertFalse(DOMEqualityUtil.isTextEqual(_c2a, _c2b, false));
    assertFalse(DOMEqualityUtil.isTextEqual(_c2b, _c2a, false));

    assertTrue(DOMEqualityUtil.isTextEqual(_c2b, _c2c, false));
    assertTrue(DOMEqualityUtil.isTextEqual(_c2c, _c2b, false));

    assertFalse(DOMEqualityUtil.isTextEqual(_c2b, _c2d, false));
    assertFalse(DOMEqualityUtil.isTextEqual(_c2d, _c2b, false));
  }


  public void testNodeMapShortCircuit() throws Exception {

    assertTrue(DOMEqualityUtil.isEqual((NamedNodeMap)null, (NamedNodeMap)null));
    assertTrue(DOMEqualityUtil.isEqual(_a1.getAttributes(), _a1.getAttributes()));
    assertFalse(DOMEqualityUtil.isEqual(_a1.getAttributes(), null));
    assertFalse(DOMEqualityUtil.isEqual(null, _a1.getAttributes()));
  }


  public void testNodeMapSingleAttribute() throws Exception {

    _a1.setAttribute(AT_KEY_1, AT_VAL_1a);
    _b1.setAttribute(AT_KEY_1, AT_VAL_1b);
    _c1.setAttributeNS("foo", "foo:" + AT_KEY_1, AT_VAL_1a);

    // explicit comparison
    assertFalse(DOMEqualityUtil.isEqual(_a1.getAttributes(), _b1.getAttributes(),
                                       false, false, false));
    assertFalse(DOMEqualityUtil.isEqual(_a1.getAttributes(), _c1.getAttributes(),
                                       false, false, false));

    // ignoring namespaces
    assertFalse(DOMEqualityUtil.isEqual(_a1.getAttributes(), _b1.getAttributes(),
                                       true, false, false));
    assertTrue(DOMEqualityUtil.isEqual(_a1.getAttributes(), _c1.getAttributes(),
                                       true, false, false));

    // ignoring values
    assertTrue(DOMEqualityUtil.isEqual(_a1.getAttributes(), _b1.getAttributes(),
                                       false, false, true));
    assertFalse(DOMEqualityUtil.isEqual(_a1.getAttributes(), _c1.getAttributes(),
                                       false, false, true));
  }


  public void testNodeMapMultipleAttributes() throws Exception {

    _a1.setAttribute(AT_KEY_1, AT_VAL_1a);
    _b1.setAttribute(AT_KEY_1, AT_VAL_1a);
    _b1.setAttribute(AT_KEY_2, AT_VAL_2);

    assertFalse(DOMEqualityUtil.isEqual(_a1.getAttributes(), _b1.getAttributes(),
                                       false, false, false));
  }


  public void testNodeMapIgnoringXmlnsAttrs() throws Exception {

    _a1.setAttribute(AT_KEY_1, AT_VAL_1a);
    _a1.setAttribute("xmlns:foo", "foo");
    _b1.setAttribute(AT_KEY_1, AT_VAL_1a);
    _b1.setAttribute("xmlns:bar", "bar");
    assertFalse(DOMEqualityUtil.isEqual(_a1.getAttributes(), _b1.getAttributes(),
                                        false, false, false));
    assertTrue(DOMEqualityUtil.isEqual(_a1.getAttributes(), _b1.getAttributes(),
                                        false, true, false));
  }


  public void testNodeShortCircuit() throws Exception {
    assertTrue(DOMEqualityUtil.isEqual((Element)null, (Element)null));
    assertTrue(DOMEqualityUtil.isEqual(_a1, _a1));
    assertFalse(DOMEqualityUtil.isEqual(_a1, null));
    assertFalse(DOMEqualityUtil.isEqual(null, _a1));
    assertFalse(DOMEqualityUtil.isEqual(_doc, _a1));
  }


  public void testNodeSimple() throws Exception {
    assertFalse(DOMEqualityUtil.isEqual(_a1, _b1));
    assertFalse(DOMEqualityUtil.isEqual(_b1, _a1));

    assertTrue(DOMEqualityUtil.isEqual(_c2a, _c2b));
    assertTrue(DOMEqualityUtil.isEqual(_c2b, _c2a));

    Element aa1 = (Element)_a1.cloneNode(true);
    assertNotSame(_a1, aa1);
    assertTrue(DOMEqualityUtil.isEqual(_a1, aa1));
    assertTrue(DOMEqualityUtil.isEqual(aa1, _a1));
  }


  public void testNodeWithAttributes() throws Exception {
    _c2a.setAttribute(AT_KEY_1, AT_VAL_1a);
    _c2b.setAttribute(AT_KEY_1, AT_VAL_1a);
    assertTrue(DOMEqualityUtil.isEqual(_c2a, _c2b));
    assertTrue(DOMEqualityUtil.isEqual(_c2b, _c2a));

    _c2b.setAttribute(AT_KEY_1, AT_VAL_1b);
    assertFalse(DOMEqualityUtil.isEqual(_c2a, _c2b));
    assertFalse(DOMEqualityUtil.isEqual(_c2b, _c2a));

    _c2b.removeAttribute(AT_KEY_1);
    _c2b.setAttributeNS("foo", AT_KEY_1, AT_VAL_1a);
    assertFalse(DOMEqualityUtil.isEqual(_c2a, _c2b));
    assertFalse(DOMEqualityUtil.isEqual(_c2b, _c2a));
  }


  public void testNodeWithIgnorableChildren() throws Exception {
    _c2a.appendChild(_doc.createProcessingInstruction("foo", "bar"));
    _c2a.appendChild(_doc.createComment("blah blah blah"));

    _c2b.appendChild(_doc.createProcessingInstruction("foo", "bar"));
    _c2b.appendChild(_doc.createComment("blah blah blah"));

    _c2c.appendChild(_doc.createComment("blah blah blah"));
    _c2c.appendChild(_doc.createProcessingInstruction("foo", "bar"));

    _c2d.appendChild(_doc.createProcessingInstruction("argle", "bargle"));
    _c2d.appendChild(_doc.createComment("nah nah nah"));

    // first test, ignore them
    assertTrue(DOMEqualityUtil.isEqual(_c2a, _c2b, false, true, true, false));
    assertTrue(DOMEqualityUtil.isEqual(_c2a, _c2c, false, true, true, false));
    assertTrue(DOMEqualityUtil.isEqual(_c2a, _c2d, false, true, true, false));

    // second test, pay attention to them, in order
    assertTrue(DOMEqualityUtil.isEqual(_c2a, _c2b, false, false, false, false));
    assertFalse(DOMEqualityUtil.isEqual(_c2a, _c2c, false, false, false, false));
    assertFalse(DOMEqualityUtil.isEqual(_c2a, _c2d, false, false, false, false));

    // third test, only care about comments
    assertTrue(DOMEqualityUtil.isEqual(_c2a, _c2b, false, false, true, false));
    assertTrue(DOMEqualityUtil.isEqual(_c2a, _c2c, false, false, true, false));
    assertFalse(DOMEqualityUtil.isEqual(_c2a, _c2d, false, false, true, false));

    // fourth test, only care about PIs
    assertTrue(DOMEqualityUtil.isEqual(_c2a, _c2b, false, true, false, false));
    assertTrue(DOMEqualityUtil.isEqual(_c2a, _c2c, false, true, false, false));
    assertFalse(DOMEqualityUtil.isEqual(_c2a, _c2d, false, true, false, false));

    // FIXME - do it all again, unordered
  }


  public void testNodeHierarchy() throws Exception {
    Document foo = XMLUtil.newDocument();
    Element fooRoot = (Element)foo.importNode(_root, true);
    foo.appendChild(fooRoot);

    assertTrue(DOMEqualityUtil.isEqual(_root, fooRoot));
    assertTrue(DOMEqualityUtil.isEqual(fooRoot, _root));
  }
}
