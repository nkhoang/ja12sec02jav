package com.nkhoang.common.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 *  Utility methods for working with XML data that follows an XML Schema.
 *  <p>
 *  Note that most of these methods actually work with a DOM representation
 *  of the schema, <em>not</em> a <code>javax.xml.validation.Schema</code>.
 */
public class SchemaUtil {

  /**
   *  Parses a <code>boolean</code> value per
   *  http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#boolean
   *
   *  @throws IllegalArgumentException if argument does not match the allowed
   *          values for <code>boolean</code>.
   */
  public static boolean parseBoolean(String str) {
    String s2 = str.trim();
    if (s2.equals("true") || s2.equals("1")) {
      return true;
    }
    if (s2.equals("false") || s2.equals("0")) {
      return false;
    }
    throw new IllegalArgumentException(str);
  }


  /**
   *  Generates the canonical string representation of the passed value, per
   *  http://www.w3.org/TR/2004/REC-xmlschema-2-20041028/datatypes.html#boolean
   */
  public static String toBoolean(boolean bool) {
    return bool ? "true" : "false";
  }


  /**
   *  Given a DOM document representing a schema, extracts the subtree for a
   *  specified type definition.
   *
   *  @param  schema  The schema definition.
   *  @param  type    The name of the type to retrieve.
   *
   *  @throws IllegalArgumentException if the schema does not contain the
   *          desired type.
   */
  public static Element getTypeDefinition(Document schema, String type) {
    String path = "/xsd:schema/xsd:complexType[@name='" + type + "']";
    Element result = (Element)XPathUtil.selectNode(
                        schema, path,
                        XPathUtil.createSingleNamespaceContext(
                            "xsd", XMLConstants.W3C_XML_SCHEMA_NS_URI));
    if (result == null) {
      throw new IllegalArgumentException("no definition for type " + type);
    }
    return result;
  }


  /**
   *  Appends a child element according to the schema definition of its parent
   *  type.
   *  <p>
   *  At the present time, this method takes a very naive approach to inserting
   *  elements. It does not pay attention to occurrence constraints, nor does
   *  it verify the type of the passed element. This should be sufficient for
   *  98% of expected uses; if you fall into that 2%, feel free to enhance.
   *  <p>
   *  Does not currently pay attention to namespaces. Elements in the instance
   *  document are accessed by local name, which should be sufficient, but will
   *  probably break in the worst possible way. FIXME
   *
   *  @param  parent  The parent element.
   *  @param  child   The element to insert.
   *  @param  schema  The schema used to validate this element's document, as
   *                  a DOM document.
   *  @param  type    The <code>xs:complexType</code> name for the schema
   *                  type representing <code>parent</code>.
   *
   *  @throws IllegalArgumentException if unable to perform this operation due
   *          to problems with the passed arguments; the thrown exception will
   *          contain more information.
   */
  public static void appendChild(Element parent, Element child,
                                 Document schema, String type)
  {
    List<TypeComponent> components = extractDefinition(schema, type, null);
    Element insertAt = new LocationFinder(parent, child, components).findInsertLocation();
    if (insertAt == null) {
      parent.appendChild(child);
    }
    else {
      parent.insertBefore(child, insertAt);
    }
  }


//----------------------------------------------------------------------------
//  Helpers
//----------------------------------------------------------------------------

  /**
   *  Extracts the legal element names from a <code>complexType</code>, by
   *  order. An actual parent element may have multiple children for each
   *  element in the returned list.
   *
   *  @param  schema  Contains the type definition.
   *  @param  type    The name fo the type that we want to extract.
   *  @param  cache   Used to cache repeated calls to this method for the
   *                  same schema. May be <code>null</code>, in which
   *                  case it's ignored.
   *  @throws IllegalArgumentException if unable to find a definition for
   *          the desired type in the schema document.
   */
  private static List<TypeComponent> extractDefinition(
          Document schema, String type, Map<String,List<TypeComponent>> cache)
  {
    List<TypeComponent> result = (cache != null) ? cache.get(type) : null;
    if (result != null) {
      return result;
    }

    Element typedef = getTypeDefinition(schema, type);
    return TypeDecomposer.handleComplexTypeElement(typedef);
  }


  /**
   *  Base class for modeling schema-defined types. A schema <code>complexType
   *  </code> definition is translated into an list of these objects.
   */
  private static abstract class TypeComponent {

    /**
     *  Returns <code>true</code> if the passed element name is acceptable to
     *  this component (ie, in this location in the type).
     */
    public abstract boolean isElementAcceptable(Element elem);
  }



  /**
   *  Type component for discrete elements.
   */
  private static class ElementComponent extends TypeComponent {
    private String _elementName;

    public ElementComponent(String elementName) {
      _elementName = elementName;
    }

    @Override
    public boolean isElementAcceptable(Element elem) {
      return DOMUtil.getLocalName(elem).equals(_elementName);
    }
  }



  /**
   *  Type component for choice elements.
   */
  private static class ChoiceComponent extends TypeComponent {
    private List<TypeComponent> children;

    public ChoiceComponent() {
      children = new ArrayList<TypeComponent>();
    }

    public void addChild(TypeComponent child) {
      children.add(child);
    }

    @Override
    public boolean isElementAcceptable(Element elem) {
      for (TypeComponent child : children) {
        if (child.isElementAcceptable(elem)) {
          return true;
        }
      }
      return false;
    }
  }


  /**
   *  A helper class for decomposing the <code>complexType</code> element into
   *  a list of <code>TypeComponent</code>s.
   *  <p>
   *  For now, this class exists to segregate these (static) methods. It may end
   *  up as an instantiated class that decomposes and then holds type components.
   *  <p>
   *  FIXME - doesn't handle nested <code>choice</code> elements (are they even
   *  legal?), nor <code>group</code> elements.
   */
  private static abstract class TypeDecomposer {

    public static List<TypeComponent> handleComplexTypeElement(Element elem)
    {
      List<TypeComponent> result = new ArrayList<TypeComponent>();
      for (Element child : DOMUtil.getChildren(elem)) {
        if (child.getLocalName().equals("sequence")) {
          handleSequenceElement(child, result);
        }
        else if (child.getLocalName().equals("choice")) {
          handleChoiceElement(child, result);
        }
      }
      return result;
    }

    private static List<TypeComponent> handleSequenceElement(
            Element elem, List<TypeComponent> list)
    {
      for (Element child : DOMUtil.getChildren(elem)) {
        if (DOMUtil.getLocalName(child).equals("element")) {
          list.add(new ElementComponent(child.getAttribute("name")));
        }
        if (DOMUtil.getLocalName(child).equals("choice")) {
          handleChoiceElement(child, list);
        }
      }
      return list;
    }

    private static List<TypeComponent> handleChoiceElement(
            Element elem, List<TypeComponent> list)
    {
      ChoiceComponent component = new ChoiceComponent();
      for (Element child : DOMUtil.getChildren(elem)) {
        if (DOMUtil.getLocalName(child).equals("element")) {
          component.addChild(new ElementComponent(child.getAttribute("name")));
        }
      }
      list.add(component);
      return list;
    }
  }


  /**
   *  A helper class to find where the child element should go in a parent
   *  according to the parent's type definition.
   *  <p>
   *  Both the constructor and <code>findInsertLocation()</code> may throw
   *  <code>IllegalArgumentException</code> ... it's what's advertised by
   *  the using class, even if the latter method has no arguments.
   *  <p>
   *  FIXME - this isn't going to work for <code>sequence</code> inside of
   *  <code>choice</code>. The big problem is that there are multiple paths,
   *  possibly overlapping. I think we'll need a more complex state machine,
   *  and probably need to identify paths based on the entire element
   *  content. On the other hand, anyone who creates this type of construct
   *  deserves to be shot, so we're going with the simple implementation as
   *  long as possible.
   */
  private static class LocationFinder {

    private Element _parent;
    private Element _child;
    private List<TypeComponent> _typedef;
    private int _insertComponentIndex;

    public LocationFinder(Element parent, Element child, List<TypeComponent> typedef) {
      _parent = parent;
      _child = child;
      _typedef = typedef;
      determineInsertComponent();
    }

    public Element findInsertLocation() {
      // if the child was the last component in the type, then life is easy
      if (_insertComponentIndex == _typedef.size()) {
        return null;
      }

      // otherwise assign each child of the parent to some type component,
      // until we reach the insert point
      int curComponentIndex = 0;
      for (Element elem : DOMUtil.getChildren(_parent)) {
        if (curComponentIndex == _insertComponentIndex) {
          return elem;
        }
        while (!_typedef.get(curComponentIndex).isElementAcceptable(elem)
               && (curComponentIndex < _typedef.size())) {
          curComponentIndex++;
          if (curComponentIndex == _insertComponentIndex) {
            return elem;
          }
        }
        if (curComponentIndex == _typedef.size()) {
          throw new IllegalArgumentException(
                  "type definition doesn't apply to parent");
        }
      }

      // we went through all elements without hitting the insert point,
      // so add it to the end
      return null;
    }

    /**
     *  This method finds the type component that follows the component that
     *  should accept the child. That description doesn't pass the grandmother
     *  test, so look above at how <code>_insertComponentIndex</code> is used.
     *
     */
    private void determineInsertComponent() {
      for (int ii = _insertComponentIndex ; ii < _typedef.size() ; ii++) {
        if (_typedef.get(ii).isElementAcceptable(_child)) {
          _insertComponentIndex = ii + 1;
          return;
        }
      }
        throw new IllegalArgumentException(
                "type definition has no place for " + _child.getTagName());
    }
  }

}
