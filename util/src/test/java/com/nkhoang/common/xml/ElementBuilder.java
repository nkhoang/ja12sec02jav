package com.nkhoang.common.xml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * This class is used to define XML trees, and then create DOM or String
 * representations from those trees. It uses less code than creating a
 * DOM directly, and avoids the mismatched tags and concatenation needed
 * to build a literal string. As such, its very appropriate for test
 * cases, where you want to build XML using constant values, and yet see
 * the structure as its built.
 * <p/>
 * The hierarchical nature of XML doesn't exactly fit the linear nature
 * of a "builder class." To get around this, you need to create a separate
 * builder instance for each element in the document, then add children or
 * text as appropriate.
 * <p/>
 * Once constructed, a builder instance may be used multiple times to
 * produce output. Each created output object is distinct; ie, calling
 * {@link #toDocument} three times produces three separate documents.
 */
public class ElementBuilder {

	private String _name;
	private String _namespace;
	List<BuilderNode> _children = new ArrayList<BuilderNode>();


	/**
	 * Creates a new instance, with namespace.
	 *
	 * @param namespace The namespace URI for this element. May be <code>null
	 *                  </code>, if the node does not have a namespace (it's
	 *                  easier to use the alternate constructor for this).
	 * @param tagName   The qualified tag name for the node (if not qualified,
	 *                  the namespace URI is used as the default namespace for
	 *                  the node).
	 */
	public ElementBuilder(String namespace, String tagName) {
		_name = tagName;
		_namespace = namespace;
	}


	/**
	 * Creates a new instance, without namespace.
	 *
	 * @param name The unqualified name for the element.
	 */
	public ElementBuilder(String name) {
		this(null, name);
	}


	//----------------------------------------------------------------------------
	//  Public Methods
	//----------------------------------------------------------------------------

	/**
	 * Adds a child element to this element. The builder maintains the order of
	 * addition, including interleaved element and text children.
	 */
	public ElementBuilder appendElement(ElementBuilder child) {
		_children.add(new ElementNode(child));
		return this;
	}


	/** Adds an attribute to this element. */
	public ElementBuilder appendAttribute(String name, String value) {
		_children.add(new AttributeNode(name, value));
		return this;
	}


	/**
	 * Adds text to this element. The builder maintains the order of addition,
	 * including interleaved element and text (ie, text nodes are not normalized
	 * by the builder; this is useful when building test documents).
	 */
	public ElementBuilder appendText(String text) {
		_children.add(new TextNode(text));
		return this;
	}


	/**
	 * Creates a DOM <code>Document</code> from this builder. This may be called
	 * multiple times; each call produces a new DOM.
	 */
	public Document toDocument() {
		Document result = XMLUtil.newDocument();
		result.appendChild(createSubtree(result));
		return result;
	}


	/** Appends the subtree defined by this builder to the passed Element. */
	public void appendTo(Element elem) {
		elem.appendChild(createSubtree(elem.getOwnerDocument()));
	}


	/**
	 * Creates a string representation of the XML tree rooted at this
	 * builder. See also {@link #toPrettyString}.
	 */
	@Override
	public String toString() {
		return XMLUtil.serialize(toDocument());
	}


	/**
	 * Creates a string representation of the XML tree rooted at this
	 * builder, pretty-printed with a 2-character indent.
	 */
	public String toPrettyString() {
		return XMLUtil.serialize(toDocument(), 2, false);
	}


	//----------------------------------------------------------------------------
	//  Internals
	//----------------------------------------------------------------------------

	/**
	 * Utility method to create a tree from this builder and its children,
	 * within the passed document.
	 */
	private Element createSubtree(Document doc) {
		Element root = doc.createElementNS(_namespace, _name);
		for (BuilderNode child : _children) {
			child.appendTo(root);
		}
		return root;
	}


	/**
	 * The basic node. Builder nodes know how to convert themselves into a DOM
	 * <code>Node</code> and append themselves to another DOM node. Everything
	 * else is handled by the caller.
	 */
	private static abstract class BuilderNode {

		/**
		 * Create a DOM Node from this Builder node and appends it to the passed
		 * parent node.
		 */
		public abstract void appendTo(Element parent);
	}


	/**
	 * An element node. Contains zero or more mixed Element, Attribute, or
	 * Text chidren. Conversion produces a tree, which must then be appended
	 * to an existing document / element.
	 */
	private static class ElementNode extends BuilderNode {
		private ElementBuilder _child;

		public ElementNode(ElementBuilder child) {
			_child = child;
		}

		@Override
		public void appendTo(Element parent) {
			_child.appendTo(parent);
		}
	}


	/**
	 * An attribute node. Contains a name/value pair, and may only be added to
	 * Element nodes.
	 */
	private static class AttributeNode extends BuilderNode {
		String _name;
		String _value;

		public AttributeNode(String name, String value) {
			_name = name;
			_value = value;
		}

		@Override
		public void appendTo(Element parent) {
			parent.setAttribute(_name, _value);
		}
	}


	/**
	 * An attribute node. Contains literal text, and may only be added to
	 * Element nodes.
	 */
	private static class TextNode extends BuilderNode {
		String _text;

		public TextNode(String text) {
			_text = text;
		}

		@Override
		public void appendTo(Element parent) {
			Document doc = parent.getOwnerDocument();
			parent.appendChild(doc.createTextNode(_text));
		}
	}

}
