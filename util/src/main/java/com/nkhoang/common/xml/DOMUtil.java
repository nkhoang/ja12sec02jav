package com.nkhoang.common.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.nkhoang.common.collections.CollectionUtils;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


/**
 * Utility methods for manipulating a DOM structure.
 * <p/>
 * Methods to manipulate raw XML text -- including parsing to a DOM -- are
 * in {@link XMLUtil}.
 */
public class DOMUtil {

	private static final String PREFIX_XMLNS = "xmlns:";

	/**
	 * @param doc Document to process
	 *
	 * @return A map whose keys are the namespace URIs in the document,
	 *         and whose values are the namespace prefixes for those URIs
	 *         (String -> String).  The default namespace URI will be mapped to null.
	 *
	 * @deprecated this method will fail if the document reuses tags or assigns
	 *             different tags to the same URI -- both of which are perfectly legal.
	 */
	@Deprecated
	public static BidiMap getNamespacePrefixes(Document doc) {
		BidiMap rtn = new DualHashBidiMap();
		NamedNodeMap attrs = doc.getDocumentElement().getAttributes();
		for (int i = 0; i < attrs.getLength(); i++) {
			Attr attr = (Attr) attrs.item(i);
			String name = attr.getName();
			if (name.startsWith(PREFIX_XMLNS)) {
				rtn.put(attr.getValue(), name.substring(PREFIX_XMLNS.length()));
			} else if (name.equals("xmlns")) {
				rtn.put(attr.getValue(), null);
			}
		}
		return rtn;
	}


	/**
	 * Returns the next <code>Element</code> sibling of this node, <code>null\
	 * </code> if there isn't one.
	 */
	public static Element getNextElementSibling(Element elem) {
		Node node = elem;
		while (node != null) {
			node = node.getNextSibling();
			if (node instanceof Element) {
				return (Element) node;
			}
		}
		return null;
	}


	/**
	 * Returns the next <code>Element</code> sibling of this node, <code>null\
	 * </code> if there isn't one.
	 */
	public static Element getPreviousElementSibling(Element elem) {
		Node node = elem;
		while (node != null) {
			node = node.getPreviousSibling();
			if (node instanceof Element) {
				return (Element) node;
			}
		}
		return null;
	}


	/** @return The first child element of the passed-in node, or null if none found. */
	public static Element getFirstChildElement(Node parent) {
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				return (Element) children.item(i);
			}
		}
		return null;
	}


	/** Delete all child nodes from a node. */
	public static void clear(Node node) {
		Node child;
		while ((child = node.getFirstChild()) != null) {
			node.removeChild(child);
		}
	}


	/** Returns all child nodes of a given type. */
	public static <T extends Node> List<T> getChildrenOfType(Node parent, Class<T> klass) {
		List<T> result = new ArrayList<T>();
		NodeList children = parent.getChildNodes();
		for (int ii = 0; ii < children.getLength(); ii++) {
			Node child = children.item(ii);
			if (klass.isAssignableFrom(child.getClass())) {
				result.add(klass.cast(child));
			}
		}
		return result;
	}

	/**
	 * Included for backwards compatability.
	 *
	 * @see #getChildren(org.w3c.dom.Node)
	 */
	public static List<Element> getChildren(Element parent) {
		return getChildren((Node) parent);
	}

	/**
	 * Returns the list of elements that are children of a passed element.
	 * <p/>
	 * Note that this returns a <code>java.util.List</code>, not a
	 * <code>NodeList</code>. This is generally more useful, since you can
	 * use a Java 1.5 iterator.
	 */
	public static List<Element> getChildren(Node parent) {
		return getChildrenOfType(parent, Element.class);
	}

	/**
	 * Included for backwards compatability.
	 *
	 * @see #getChildrenByName(org.w3c.dom.Node, String)
	 */
	public static List<Element> getChildrenByName(
		Element parent, String localName) {
		return getChildrenByName((Node) parent, localName);
	}

	/**
	 * Returns the list of child elements with a given <em>local<em> name.
	 * Unlike <code>Element.getElementsByTagName</code>, does not recurse.
	 * <p/>
	 * This method uses local name to simplify dealing with namespaces. In
	 * most cases, the children of a node belong to a homogenenous namespace,
	 * so searches against a local name are reasonable. You don't want to search
	 * against a qualified name, because the prefix may differ depending on
	 * instance document.
	 */
	public static List<Element> getChildrenByName(Node parent, String localName) {
		List<Element> result = new ArrayList<Element>();
		for (Element child : getChildren(parent)) {
			if (getLocalName(child).equals(localName)) {
				result.add(child);
			}
		}
		return result;
	}

	/**
	 * Included for backwards compatability.
	 *
	 * @see #getChildrenByName(org.w3c.dom.Node, String, String)
	 */
	public static List<Element> getChildrenByName(
		Element parent, String namespace, String localName) {
		return getChildrenByName((Node) parent, namespace, localName);
	}

	/**
	 * Returns the list of child elements matching a specified
	 * localname/namespace pairing. The passed namespace may be
	 * <code>null</code>, which limits result to elements with no namespace.
	 */
	public static List<Element> getChildrenByName(
		Node parent, String namespace, String localName) {
		List<Element> result = new ArrayList<Element>();
		for (Element child : getChildren(parent)) {
			if (child.getLocalName().equals(localName)) {
				if (((namespace == null) && (child.getNamespaceURI() == null)) ||
				    ((namespace != null) && namespace.equals(child.getNamespaceURI()))) {
					result.add(child);
				}
			}
		}
		return result;
	}

	/**
	 * Included for backwards compatability.
	 *
	 * @see #getChildByName(org.w3c.dom.Node, String)
	 */
	public static Element getChildByName(Element parent, String localName) {
		return getChildByName((Node) parent, localName);
	}

	/**
	 * Returns a single child element with the given <em>local<em> name,
	 * <code>null</code> if there is no element with the given name.
	 * <p/>
	 * This method uses local name to simplify dealing with namespaces. In most
	 * cases, the children of a node belong to a homogenenous namespace, so
	 * searches against a local name are reasonable. You don't want to search
	 * against a qualified name, because the prefix may differ depending on
	 * instance document.
	 *
	 * @throws IllegalArgumentException if the passed element contains more
	 *                                  than one element with the specified name.
	 */
	public static Element getChildByName(Node parent, String localName) {
		List<Element> children = getChildrenByName(parent, localName);
		if (children.size() == 1) {
			return children.get(0);
		} else if (children.size() == 0) {
			return null;
		} else {
			throw new IllegalArgumentException(
				"element contains " + children.size() + " children with name \"" + localName + "\"");
		}
	}

	/**
	 * Included for backwards compatability.
	 *
	 * @see #getChildByName(org.w3c.dom.Node, String, String)
	 */
	public static Element getChildByName(Element parent, String namespace, String name) {
		return getChildByName((Node) parent, namespace, name);
	}

	/**
	 * Returns a single child element with the given local name/namespace
	 * combination, <code>null</code> if there is no element with the given
	 * name.
	 *
	 * @throws IllegalArgumentException if the passed element contains more
	 *                                  than one element with the specified name.
	 */
	public static Element getChildByName(Node parent, String namespace, String name) {
		List<Element> children = getChildrenByName(parent, namespace, name);
		if (children.size() == 1) {
			return children.get(0);
		} else if (children.size() == 0) {
			return null;
		} else {
			throw new IllegalArgumentException(
				"element contains " + children.size() + " children with name \"[" + namespace + "]:" + name + "\"");
		}
	}

	/**
	 * Like {@link #getChildByName} except that the child is required to exist.
	 *
	 * @throw IllegalArgumentException if the elemnt does not exist
	 */
	public static Element getRequiredChildByName(
		Node parent, String localName) {
		Element child = getChildByName(parent, localName);
		if (child == null) {
			throw new IllegalArgumentException("missing element " + localName);
		}
		return child;
	}


	/**
	 * Adds a child element to the passed element. This method is equivalent to
	 * calling <code>Document.createElementNS()</code> and then attaching the
	 * new node to the parent element.
	 *
	 * @param parent  The parent element.
	 * @param nsUri   The namespace for the new element. May be <code>null
	 *                </code>, in which case the child is created with
	 *                unspecified namespace.
	 * @param tagName The qualified tagname of the child.
	 */
	public static Element addChild(Element parent, String nsUri, String tagName) {
		Document doc = parent.getOwnerDocument();
		Element child = doc.createElementNS(nsUri, tagName);
		parent.appendChild(child);
		return child;
	}


	/**
	 * Adds a child element to the passed element, inheriting its namespace (if
	 * any), and optionally inheriting its namespace prefix.
	 *
	 * @param parent  The parent element.
	 * @param tagName The name of the child. If this is not a qualified name,
	 *                will attach the prefix (if any) from the parent element.
	 */
	public static Element addChild(Element parent, String tagName) {
		String parentNS = parent.getNamespaceURI();
		Element child = addChild(parent, parentNS, tagName);
		if (child.getPrefix() == null) {
			child.setPrefix(parent.getPrefix());
		}
		return child;
	}


	/**
	 * Adds a child element to the passed element, along with a text node.
	 *
	 * @param parent  The parent element.
	 * @param nsUri   The namespace for the new element. May be <code>null
	 *                </code>, in which case the child is created without
	 *                a namespace.
	 * @param tagName The qualified name of the child.
	 * @param text    Content of a text node added to the new child. May
	 *                be <code>null</code>, in which case no text node is
	 *                added.
	 */
	public static Element addChildWithText(
		Element parent, String nsUri, String tagName, String text) {
		Element child = addChild(parent, nsUri, tagName);
		if (text != null) {
			setText(child, text);
		}
		return child;
	}


	/**
	 * Adds a child element to the passed element, along with a text node,
	 * inheriting the namespace of the parent element.
	 *
	 * @param parent  The parent element.
	 * @param tagName The name of the child. If this is not a qualified name,
	 *                will attach the prefix from the parent element.
	 * @param text    Content of a text node added to the new child. May
	 *                be <code>null</code>, in which case no text node is
	 *                added.
	 */
	public static Element addChildWithText(Element parent, String tagName, String text) {
		Element child = addChild(parent, tagName);
		if (text != null) {
			setText(child, text);
		}
		return child;
	}


	/**
	 * Adds a child element to the given parent element, and associates nameValuePairs as
	 * attributes of the newly created element.
	 *
	 * @param parent         The parent element.
	 * @param tagName        The name of the child. If this is not a qualified name,
	 *                       will attach the prefix from the parent element.
	 * @param nameValuePairs The list of attributeName/attributeValue's that will get attached
	 *                       to the newly created child element.
	 */
	public static Element addChildWithAttributes(Element parent, String tagName, String... nameValuePairs) {
		Element child = addChild(parent, tagName);
		for (int n = 0; n < nameValuePairs.length; n += 2) {
			child.setAttribute(nameValuePairs[n], nameValuePairs[n + 1]);
		}
		return child;
	}


	/**
	 * Replaces all immediate child Text nodes of the passed element with the
	 * passed string. This differs from <code>Node.setTextContent()</code>,
	 * which removes <em>all children</em> from the element.
	 */
	public static void setText(Element elem, String text) {
		setText((Node) elem, text);
	}

	/**
	 * Replaces all immediate child Text nodes of the passed attribute with the
	 * passed string. This differs from <code>Node.setTextContent()</code>,
	 * which removes <em>all children</em> from the element.
	 */
	public static void setText(Attr attr, String text) {
		setText((Node) attr, text);
	}

	/**
	 * Replaces all immediate child Text nodes of the passed nodes with the
	 * passed string.
	 * <p/>
	 * Making the assumption that we only want set text for an element
	 * or an attribute.  This is why this method is private.
	 */
	private static void setText(Node node, String text) {
		// explicitly using a NodeList, and working backward through it, so we
		// don't get any modification exceptions
		NodeList children = node.getChildNodes();
		for (int ii = children.getLength() - 1; ii >= 0; ii--) {
			if (children.item(ii).getNodeType() == Node.TEXT_NODE) {
				node.removeChild(children.item(ii));
			}
		}
		node.appendChild(node.getOwnerDocument().createTextNode(text));
	}


	/**
	 * Returns the immediate text of an Element, normalizing blocks that have
	 * intervening elements. This is unlike <code>Node.getTextContent</code>,
	 * which returns text from all descendent nodes.
	 * <p/>
	 * If the element does not have any text nodes, returns an empty string.
	 */
	public static String getText(Element elem) {
		return getText((Node) elem);
	}

	/**
	 * Returns the immediate text of an Attribute, normalizing blocks that have
	 * intervening attributes.
	 * <p/>
	 * If the attribute does not have any text nodes, returns an empty string.
	 */
	public static String getText(Attr attr) {
		return getText((Node) attr);
	}

	/**
	 * Returns the immediate text of an Node, normalizing blocks that have
	 * intervening attributes.
	 * <p/>
	 * If the node does not have any text nodes, returns an empty string.
	 * <p/>
	 * Making the assumption that we only want get text for an element
	 * or an attribute.  This is why this method is private.
	 */
	private static String getText(Node node) {
		StringBuilder result = new StringBuilder("");
		NodeList children = node.getChildNodes();
		for (int ii = 0; ii < children.getLength(); ii++) {
			if (children.item(ii) instanceof Text) {
				result.append(((Text) children.item(ii)).getNodeValue());
			}
		}
		return result.toString();
	}

	/**
	 * Gets the text from the child with the given node name.
	 *
	 * @param parent   the parent
	 * @param nodeName the node name
	 *
	 * @return the child text
	 */
	public static String getChildText(Element parent, String nodeName) {
		Element e = getChildByName(parent, nodeName);
		if (e == null) {
			return null;
		}
		return DOMUtil.getText(e);
	}

	/**
	 * Gets the double version of the child text or returns.
	 *
	 * @param node the parent node
	 * @param tag  the tag
	 *
	 * @return the number
	 *
	 * @throws NumberFormatException the number format exception
	 */
	public static double getChildAsDouble(Element node, String tag) throws NumberFormatException {
		String val = null;
		if ((val = getChildText(node, tag)) == null) {
			throw new NumberFormatException("Child node not found with tag " + tag + ".");
		}
		return Double.valueOf(val).doubleValue();
	}

	/**
	 * Returns a string representing an xpath-style path from the root of the
	 * document to the passed element. Takes an optional list of attribute
	 * names; if present, these are added to each path element as a predicate.
	 * <p/>
	 * Note that the returned path <em>does not</em> uniquely identify the
	 * element; for that, call {@link #getAbsolutePath}. It is meant instead
	 * to provide a human-readable method of reporting element locations in
	 * logs and exceptions.
	 */
	public static String getPath(Element elem, String... attributes) {
		if (elem == null) {
			return "/";
		}

		String thisElem = "/" + elem.getNodeName();
		for (String attrName : attributes) {
			thisElem += "[" + attrName + "='" + elem.getAttribute(attrName) + "']";
		}

		if (elem.getParentNode() instanceof Element) {
			return getPath((Element) elem.getParentNode(), attributes) + thisElem;
		} else {
			return thisElem;
		}
	}


	/**
	 * Returns a string representing the absolute xpath from the root of the
	 * document to the passed element. For example, <code>"/foo/bar[2]/baz"
	 * </code> indicates the sole "baz" child of the second "bar" element
	 * under the root "foo" element.
	 */
	public static String getAbsolutePath(Element elem) {
		if (elem == null) {
			return "/";
		}

		if (!(elem.getParentNode() instanceof Element)) {
			return "/" + elem.getTagName();
		}

		Element parent = (Element) elem.getParentNode();
		String tagName = elem.getTagName();
		int siblingCount = 0;
		int elemIndex = 0;
		for (Element sibling : getChildren(parent)) {
			if (sibling.getTagName().equals(tagName)) {
				siblingCount++;
			}
			if (sibling == elem) {
				elemIndex = siblingCount;
			}
		}
		return getAbsolutePath(parent) + "/" + tagName + ((siblingCount > 1) ? "[" + elemIndex + "]" : "");
	}


	/**
	 * Returns the unqualified name of the passed node. If the node does not
	 * have a namespace or uses the default namespace, this is the same as
	 * calling <code>getNodeName()</code>. If the node does have a namespace
	 * prefix, it's the same as calling <code>getLocalName()</code>.
	 */
	public static String getLocalName(Node node) {
		String name = node.getLocalName();
		return (name != null) ? name : node.getNodeName();
	}

	/**
	 * Returns the unqualified name of the passed node. If the node does not
	 * have a namespace or uses the default namespace, this is the same as
	 * calling <code>getNodeName()</code>. If the node does have a namespace
	 * prefix, it's the same as calling <code>getLocalName()</code>.
	 * <p/>
	 * Shadows {@link #getLocalName(org.w3c.dom.Node)} but needs to be kept around for
	 * binary compatibility with older code
	 */
	public static String getLocalName(Element node) {
		return getLocalName((Node) node);
	}

	/**
	 * @return the attribute with the given name from the given element,
	 *         or <code>null</code> if the value is an empty string
	 */
	public static String getOptionalAttribute(Element el, String attrName) {
		return StringUtils.isEmpty(el.getAttribute(attrName)) ? null : el.getAttribute(attrName);
	}

	/**
	 * @return the attribute with the given name from the given element,
	 *         requiring the result to be a non-empty string
	 *
	 * @throw IllegalArgumentException if the attribute does not exist or has an
	 * empty (all whitespace) string value
	 */
	public static String getRequiredAttribute(Element el, String attrName) {
		String attr = getOptionalAttribute(el, attrName);
		if (attr == null) {
			throw new IllegalArgumentException("missing attribute " + attrName);
		}
		return attr;
	}

	/**
	 * @return the attribute with the given name from the given element as an
	 *         instance of the given class, or <code>null</code> if the value is
	 *         an empty string.  the given class must have a constructor which
	 *         takes a single string.
	 *
	 * @throw IllegalArgumentException if the given class does not have a valid
	 * constructor or the found attribute is invalid for the class
	 */
	public static <T> T getOptionalAttribute(
		Element el, String attrName, Class<T> clazz) {
		String attr = getOptionalAttribute(el, attrName);
		if (attr != null) {
			try {
				return clazz.getConstructor(String.class).newInstance(attr);
			}
			catch (Exception e) {
				throw new IllegalArgumentException("invalid attribute " + attrName, e);
			}
		}
		return null;
	}

	/**
	 * @return the attribute with the given name from the given element as an
	 *         instance of the given class, requiring the result to be a
	 *         non-empty string value valid for the given type.  the given class
	 *         must have a constructor which takes a single string.
	 *
	 * @throw IllegalArgumentException if the given class does not have a valid
	 * constructor, the found attribute is invalid for the class, or the
	 * attribute does not exist or has an empty (all whitespace) string
	 * value
	 */
	public static <T> T getRequiredAttribute(
		Element el, String attrName, Class<T> clazz) {
		T attr = getOptionalAttribute(el, attrName, clazz);
		if (attr == null) {
			throw new IllegalArgumentException("missing attribute " + attrName);
		}
		return attr;
	}

	/**
	 * Re-creates the passed element with a new namespace, replacing it in the
	 * DOM tree with the new element. All children and attributes are moved
	 * from the old element to the new. Wouldn't it be nice if DOM provided a
	 * <code>setNamespace()</code> method?
	 */
	public static void recreateWithNamespace(Element elem, String namespaceUri) {
		Element newElem = elem.getOwnerDocument().createElementNS(namespaceUri, elem.getTagName());

		// why not iterate through a NodeList?
		// because it's a linked list, and when you copy the first child, you
		// no longer have a link to the second one
		while (elem.getFirstChild() != null) {
			newElem.appendChild(elem.getFirstChild());
		}

		// attributes are nodes when it's inconvenient for them to be so, and
		// not nodes when it would be convenient ... and no, there's no easy
		// way to just copy attributes from one element to another
		NamedNodeMap attrs = elem.getAttributes();
		for (int ii = 0; ii < attrs.getLength(); ii++) {
			Attr attr = (Attr) attrs.item(ii);
			newElem.setAttributeNS(
				attr.getNamespaceURI(), attr.getName(), attr.getValue());
		}

		elem.getParentNode().replaceChild(newElem, elem);
	}


	/**
	 * Recursively processes the DOM tree rooted at the passed element, applying
	 * the specified namespace to every element that doesn't have a namespace.
	 * Since JAXP doesn't allow us to set a namespace on an existing element,
	 * this method actually copies the entire tree, and inserts that copy into
	 * the element's parent at the appropriate location.
	 */
	public static void applyDefaultNamespace(Element elem, String namespaceUri) {
		for (Element child : getChildren(elem)) {
			applyDefaultNamespace(child, namespaceUri);
		}
		if (elem.getNamespaceURI() != null) {
			return;
		}
		recreateWithNamespace(elem, namespaceUri);
	}


	/**
	 * Compares the trees rooted at the passed element, and returns the
	 * first difference between their elements. Trees are compared for
	 * element name, attributes, text content, and child element order
	 * (recursively). Ignores processing instructions and comments,
	 * coalesces adjacent non-whitespace text blocks, and optionally
	 * ignores whitespace text blocks.
	 * <p/>
	 * If you want to know all of the differences in the two trees, see
	 * the <code>xdiff</code> package.
	 *
	 * @param ignoreWhitespace If <code>true</code>, ignore element content
	 *                         that's just whitespace. Does <em>not</em> trim
	 *                         element content that contains anything other
	 *                         than whitespace.
	 *
	 * @return The first elements determined to be different, <code>null</code>
	 *         if the trees are identical. This array contains two items, one
	 *         from each passed tree. Either of the items may be <code>null
	 *         </code>, indicating that one tree had more elements than the
	 *         other.
	 */
	public static Element[] firstDifference(Element e1, Element e2, boolean ignoreWhitespace) {
		if (!ObjectUtils.equals(e1.getNamespaceURI(), e2.getNamespaceURI()) ||
		    !getLocalName(e1).equals(getLocalName(e2)) ||
		    !DOMEqualityUtil.isEqual(e1.getAttributes(), e2.getAttributes()) ||
		    !DOMEqualityUtil.isTextEqual(e1, e2, ignoreWhitespace)) {
			return new Element[]{e1, e2};
		}
		Iterator<Element> itx1 = getChildren(e1).iterator();
		Iterator<Element> itx2 = getChildren(e2).iterator();
		while (itx1.hasNext()) {
			Element c1 = itx1.next();
			if (!itx2.hasNext()) {
				return new Element[]{c1, null};
			}
			Element c2 = itx2.next();
			Element[] recurse = firstDifference(c1, c2, ignoreWhitespace);
			if (recurse != null) {
				return recurse;
			}
		}
		if (itx2.hasNext()) {
			return new Element[]{null, itx2.next()};
		}
		return null;
	}

	/**
	 * Method to traverse the tree rooted at the given node and return all nodes
	 * of a given type contained in the tree.
	 *
	 * @param <T>   The specific type of Node to return.
	 * @param node  the root node of the tree to search through.
	 * @param klass the Class corresponding to the desired Node type.
	 *
	 * @return A List of the specified type containing all Nodes of that type
	 *         contained in the tree rooted at node.
	 */
	public static <T extends Node> List<T> getAllNodesOfType(Node node, Class<T> klass) {
		List<T> result = new ArrayList<T>();
		if (klass.isAssignableFrom(node.getClass())) {
			result.add(klass.cast(node));
		}
		List<Node> children = getChildrenOfType(node, Node.class);
		for (Node child : children) {
			result.addAll(getAllNodesOfType(child, klass));
		}
		return result;
	}


	/**
	 * Helper class to allow using methods that return a <code>List</code> in
	 * code that expects a <code>NodeList</code>.
	 */
	public static class ElementNodeList implements NodeList {

		private List<Element> _delegate;

		public ElementNodeList(List<Element> delegate) {
			_delegate = delegate;
		}

		public int getLength() {
			return _delegate.size();
		}

		public Node item(int index) {
			return _delegate.get(index);
		}
	}


	/**
	 * @return an Iterable which returns an Iterator over the given NodeList,
	 *         useful for using the new "foreach" syntax with a NodeList.  A
	 *         <code>null</code> NodeList will be treated the same as an empy
	 *         NodeList.  Note, as NodeList has no equivalent "remove" syntax,
	 *         the returned Iterator will not support the remove method.
	 */
	public static Iterable<Node> nodeIterable(NodeList nl) {
		return (nl != null) ? new NodeIterable(nl) : Collections.<Node>emptyList();
	}


	/**
	 * Helper class that turns a <code>NodeList</code> into something that you
	 * can give to a new for loop.
	 */
	private static class NodeIterable extends CollectionUtils.IterableIterator<Node> {
		private int _nextIndex = 0;
		private NodeList _nl;

		private NodeIterable(NodeList nl) {
			_nl = nl;
		}

		public boolean hasNext() {
			return (_nextIndex < _nl.getLength());
		}

		public Node next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			return _nl.item(_nextIndex++);
		}
	}

}
