package com.nkhoang.common.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/** Convenience methods for accessing DOM documents via J2SE XPath. */
public class XPathUtil {

	// guaranteed never to fail, so pay initialization cost once
	private static final XPathFactory XPATH_FACTORY = XPathFactory.newInstance();


	/** Returns an XPath instance configured to work with a DOM tree. */
	public static XPath newXPath() {
		return XPATH_FACTORY.newXPath();
	}


	/**
	 * Returns an XPath instance configured to work with a DOM tree, using
	 * the passed context to resolve namespace references.
	 */
	public static XPath newXPath(NamespaceContext nsCtx) {
		XPath xpath = XPATH_FACTORY.newXPath();
		xpath.setNamespaceContext(nsCtx);
		return xpath;
	}


	/**
	 * Returns a <code>Namespace</code> context, based on the passed association
	 * of prefixes and namespaces.
	 *
	 * @param namespaces Namespace associations: prefixes are stored as keys,
	 *                   namespace URIs as values. Note that more than one
	 *                   prefix may be used for the same namespace. <em>The
	 *                   returned context retains a reference to the passed
	 *                   map<em>; change the map at your own risk.
	 */
	public static NamespaceContext createNamespaceContext(
		final Map<String, String> namespaces) {
		return new NamespaceContext() {
			private Map<String, String> _namespaces = namespaces;

			public String getNamespaceURI(String prefix) {
				// FIXME - this method must handle prefixes defined by spec in addition
				//         to those defined by map
				return _namespaces.get(prefix);
			}

			public String getPrefix(String namespaceURI) {
				List<String> prefixes = getPrefixList(namespaceURI);
				return (prefixes.size() == 0) ? null : prefixes.get(0);
			}

			public Iterator<String> getPrefixes(String namespaceURI) {
				return getPrefixList(namespaceURI).iterator();
			}

			private List<String> getPrefixList(String namespaceURI) {
				List<String> prefixes = new ArrayList<String>();
				for (Map.Entry<String, String> entry : _namespaces.entrySet()) {
					if (entry.getValue().equals(namespaceURI)) {
						prefixes.add(entry.getKey());
					}
				}
				// FIXME - check spec'd prefixes if list is empty
				return prefixes;
			}
		};
	}


	/**
	 * Returns a <code>NamespaceContext</code> useful for processing documents
	 * that use a single namespace.
	 *
	 * @param prefix       The prefix used to refer to this namespace in the
	 *                     XPath expression. This <em>does not</em> have to
	 *                     match the prefix used in the document.
	 * @param namespaceURI The namespace to associate with this prefix.
	 */
	public static NamespaceContext createSingleNamespaceContext(
		final String prefix, final String namespaceURI) {
		return createNamespaceContext(Collections.singletonMap(prefix, namespaceURI));
	}


	/**
	 * Selects a single value using the specified XPath. This method is only
	 * appropriate for documents that do not use namespaces.
	 *
	 * @throws RuntimeException if unable to parse/evaluate the passed path.
	 *                          This exception contains the original failure exception.
	 */
	public static String selectValue(Node context, String xpath) {
		try {
			return newXPath().evaluate(xpath, context);
		}
		catch (XPathExpressionException e) {
			throw new RuntimeException("unable to process xpath: " + xpath, e);
		}
	}


	/**
	 * Selects a single value using the specified XPath, with namespace
	 * resolution.
	 *
	 * @throws RuntimeException if unable to parse/evaluate the passed path.
	 *                          This exception contains the original failure exception.
	 */
	public static String selectValue(
		Node context, String xpath, NamespaceContext nsCtx) {
		try {
			return newXPath(nsCtx).evaluate(xpath, context);
		}
		catch (XPathExpressionException e) {
			throw new RuntimeException("unable to process xpath: " + xpath, e);
		}
	}


	/**
	 * Selects a single node using the specified XPath. This method is only
	 * appropriate for documents that do not use namespaces.
	 *
	 * @throws RuntimeException if unable to parse/evaluate the passed path.
	 *                          This exception contains the original failure exception.
	 */
	public static Node selectNode(Node context, String xpath) {
		try {
			return (Node) newXPath().evaluate(xpath, context, XPathConstants.NODE);
		}
		catch (XPathExpressionException e) {
			throw new RuntimeException("unable to process xpath: " + xpath, e);
		}
	}


	/**
	 * Selects a single node using the specified XPath, with namespace
	 * resolution.
	 *
	 * @throws RuntimeException if unable to parse/evaluate the passed path.
	 *                          This exception contains the original failure exception.
	 */
	public static Node selectNode(
		Node context, String xpath, NamespaceContext nsCtx) {
		try {
			return (Node) newXPath(nsCtx).evaluate(xpath, context, XPathConstants.NODE);
		}
		catch (XPathExpressionException e) {
			throw new RuntimeException("unable to process xpath: " + xpath, e);
		}
	}


	/**
	 * Selects the nodes corresponding to the specified XPath. This method is
	 * only appropriate for documents that do not use namespaces.
	 *
	 * @throws RuntimeException if unable to parse/evaluate the passed path.
	 *                          This exception contains the original failure exception.
	 */
	public static List<Node> selectNodes(Node context, String xpath) {
		try {
			NodeList nodes = (NodeList) newXPath().evaluate(xpath, context, XPathConstants.NODESET);
			List<Node> result = new ArrayList<Node>();
			for (int ii = 0; ii < nodes.getLength(); ii++) {
				result.add(nodes.item(ii));
			}
			return result;
		}
		catch (XPathExpressionException e) {
			throw new RuntimeException("unable to process xpath: " + xpath, e);
		}
	}


	/**
	 * Selects the nodes corresponding to the specified XPath, with namespace
	 * resolution.
	 *
	 * @throws RuntimeException if unable to parse/evaluate the passed path.
	 *                          This exception contains the original failure exception.
	 */
	public static List<Node> selectNodes(
		Node context, String xpath, NamespaceContext nsCtx) {
		try {
			NodeList nodes = (NodeList) newXPath(nsCtx).evaluate(xpath, context, XPathConstants.NODESET);
			List<Node> result = new ArrayList<Node>();
			for (int ii = 0; ii < nodes.getLength(); ii++) {
				result.add(nodes.item(ii));
			}
			return result;
		}
		catch (XPathExpressionException e) {
			throw new RuntimeException("unable to process xpath: " + xpath, e);
		}
	}

}
