package com.nkhoang.common.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.transform.TransformerException;

import com.nkhoang.common.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xpath.CachedXPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeIterator;

/**
 * Evaluates XPath expressions and performs searches
 *
 * @deprecated This class was for use before XPath was included in the jdk,
 *             please use XPathUtil instead.
 */
@Deprecated
public class XPathEvaluator {

	private static final Log LOG = LogFactory.getLog(XPathEvaluator.class);

	private static final Pattern PAT_VARIABLE = Pattern.compile("\\$(\\w+)");

	/** Whether or not methods that get String values should include the text in comments */
	private boolean _ignoreCommentText = false;
	private CachedXPathAPI _peer;
	private Map<String, String> _variables = new HashMap<String, String>();
	private Node _namespaceNode;

	/**
	 * Create a new evaluator
	 *
	 * @param namespaceNode The element to use for namespace declaration context
	 */
	public XPathEvaluator(Node namespaceNode) {
		_peer = new CachedXPathAPI();
		_namespaceNode = namespaceNode;
	}

	public void addVariable(String name, String value) {
		_variables.put(name, value);
	}

	public void setIgnoreCommentText(boolean ignoreCommentText) {
		_ignoreCommentText = ignoreCommentText;
	}

	/** Perform XPath variable replacement (e.g. $foo) */
	protected String lookupVars(String xpath) {
		Matcher matcher = PAT_VARIABLE.matcher(xpath);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String value = _variables.get(matcher.group(1));
			if (value == null) {
				value = "$" + matcher.group(1);
			}
			if (value.length() == 0) {
				value = "```";
			}
			value = "'" + value + "'";
			matcher.appendReplacement(sb, value);
		}
		matcher.appendTail(sb);
		String rtn = sb.toString();
		rtn = rtn.replaceAll("''", "");
		rtn = rtn.replaceAll("```", "");
		return rtn;
	}

	/**
	 * Select a single node using the namespace node as context
	 *
	 * @param xpath XPath expression to use for selection
	 *
	 * @return The requested node or null if it was not found
	 */
	public Node select(String xpath) throws TransformerException {
		return select(_namespaceNode, xpath);
	}

	/**
	 * Select a single node using another node as context
	 *
	 * @param context Context node for selection
	 * @param xpath   XPath expression to use for selection
	 *
	 * @return The requested node or null if it was not found
	 */
	public Node select(Node context, String xpath) throws TransformerException {
		return _peer.selectSingleNode(context, lookupVars(xpath), _namespaceNode);
	}

	/**
	 * Select a list of nodes using the namespace node as context
	 *
	 * @param xpath XPath expression to use for selection
	 *
	 * @return The requested node list or null if no matches were found
	 */
	public NodeList selectList(String xpath) throws TransformerException {
		return selectList(_namespaceNode, xpath);
	}

	/**
	 * Select a list of nodes using another node as context
	 *
	 * @param context Context node for selection
	 * @param xpath   XPath expression to use for selection
	 *
	 * @return The requested node list or null if no matches were found
	 */
	public NodeList selectList(Node context, String xpath) throws TransformerException {
		NodeList rtn = _peer.selectNodeList(context, lookupVars(xpath), _namespaceNode);
		if (rtn.getLength() == 0) {
			return null;
		} else {
			return rtn;
		}
	}

	/**
	 * Get the String value of a node from an XPath expression using the
	 * namespace node as context,
	 *
	 * @param xpath XPath expression to use for selection
	 *
	 * @return The requested String value or null if no matches were found
	 */
	public String getValue(String xpath) throws TransformerException {
		return getValue(_namespaceNode, xpath);
	}

	/**
	 * Get the String value of a node from an XPath expression using another
	 * node as context,
	 *
	 * @param context Context node for selection
	 * @param xpath   XPath expression to use for selection
	 *
	 * @return The requested String value or null if no matches were found
	 */
	public String getValue(Node context, String xpath) throws TransformerException {
		XObject xobj = _peer.eval(context, lookupVars(xpath), _namespaceNode);
		switch (xobj.getType()) {
			case (XObject.CLASS_BOOLEAN):
				return Boolean.toString(xobj.bool());
			case (XObject.CLASS_NODESET):
				return nodeToString(xobj.nodeset().nextNode());
			case (XObject.CLASS_NULL):
				return null;
			default:
				return xobj.toString();
		}
	}

	public String getDeepValue(String xpath) throws TransformerException {
		return getDeepValue(_namespaceNode, xpath);
	}

	public String getDeepValue(Node context, String xpath) throws TransformerException {
		XObject xobj = _peer.eval(context, lookupVars(xpath), _namespaceNode);
		switch (xobj.getType()) {
			case (XObject.CLASS_BOOLEAN):
				return Boolean.toString(xobj.bool());
			case (XObject.CLASS_NODESET):
				NodeIterator iter = xobj.nodeset();
				Node node;
				StringBuffer rtn = new StringBuffer();
				do {
					node = iter.nextNode();
					if (node != null) {
						rtn.append(deepNodeToString(node));
					}
				} while (node != null);
				return rtn.toString();
			case (XObject.CLASS_NULL):
				return null;
			default:
				return xobj.toString();
		}
	}

	private String deepNodeToString(Node node) throws TransformerException {
		StringBuffer rtn = new StringBuffer();
		if (node != null) {
			if (canContainText(node)) {
				rtn.append(node.getNodeValue());
			}
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				rtn.append(deepNodeToString(children.item(i)));
			}
		}
		return StringUtil.condenseWhiteSpace(rtn.toString());
	}

	/**
	 * Convert a single node's children into a list of Strings.
	 *
	 * @param node               Node whose children should be converted
	 * @param delimitingElements Collection of element names (Strings) that should
	 *                           serve as delimiters when splitting up the text within the node.
	 *                           For instance, in HTML, this would typically contain "BR".
	 *                           To avoid splitting on elements, pass in an empty collection.
	 * @param delimitingText     Regex to split text values on if delimiting elements
	 *                           are not found.  To avoid splitting on text values, pass in "".
	 *
	 * @return A list of all of the text values that were parsed out of this node.
	 *         Examples:       <br>
	 *         &lt;TD&gt;abc &lt;i&gt;d&lt;/i&gt;&lt;BR/&gt;z&lt;/TD&gt;       <br> returns    <br>
	 *         [abc d, z]      <br>
	 *         ab, c, d,e,f    <br> returns    <br>
	 *         [ab, c, d, e, f]
	 */
	public List<String> deepChildrenToStrings(
		Node node, Collection delimitingElements, String delimitingText) throws TransformerException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("deepChildrenToStrings entered");
		}
		List<String> rtn = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		boolean foundDelimitingElement = false;
		if (node != null) {
			NodeList children = node.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Processing node named " + children.item(i).getNodeName());
				}
				if (canContainText(children.item(i))) {
					String s = children.item(i).getNodeValue();
					if (s != null) {
						if (LOG.isDebugEnabled()) {
							LOG.debug("adding " + s.trim());
						}
						sb.append(s.trim());
					}
				} else if (delimitingElements.contains(children.item(i).getNodeName())) {
					rtn.add(sb.toString().trim());
					sb = new StringBuffer();
					foundDelimitingElement = true;
				} else {
					String s = deepNodeToString(children.item(i));
					if (LOG.isDebugEnabled()) {
						LOG.debug("adding " + s);
					}
					sb.append(s.trim());
				}
			}
		}
		if (foundDelimitingElement) {
			if (sb.length() > 0) {
				rtn.add(sb.toString().trim());
			}
		} else {
			rtn.addAll(Arrays.asList(sb.toString().trim().split(delimitingText)));
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("returning " + rtn);
		}
		return rtn;
	}

	private boolean canContainText(Node node) {
		boolean rtn = node instanceof Text || node instanceof CDATASection;
		if (!_ignoreCommentText) {
			rtn |= node instanceof Comment;
		}
		return rtn;
	}

	/**
	 * @param node Node to convert to a String
	 *
	 * @return String value of the node
	 */
	private String nodeToString(Node node) {
		if (node != null) {
			String s = node.getNodeValue();
			if (s != null) {
				s = s.trim();
			} else {
				StringBuffer buf = new StringBuffer();
				NodeList children = node.getChildNodes();
				for (int i = 0; i < children.getLength(); i++) {
					if (children.item(i) instanceof Text) {
						buf.append(children.item(i).getNodeValue());
					}
				}
				s = buf.toString().trim();
			}
			return s;
		} else {
			return null;
		}
	}

	/**
	 * Get the String values of a list of nodes from an XPath expression
	 *
	 * @param xpath XPath expression to use for selection
	 *
	 * @return The requested List of values or an empty list if no matches were found
	 */
	public List<String> getValues(String xpath) throws TransformerException {
		return getValues(_namespaceNode, xpath);
	}

	/**
	 * Get the String values of a list of nodes from an XPath expression using another
	 * node as context,
	 *
	 * @param context Context node for selection
	 * @param xpath   XPath expression to use for selection
	 *
	 * @return The requested List of values or an empty list if no matches were found
	 */
	public List<String> getValues(Node context, String xpath) throws TransformerException {
		List<String> rtn = new ArrayList<String>();
		NodeList list = selectList(context, xpath);
		if (list != null) {
			for (int i = 0; i < list.getLength(); i++) {
				String s = nodeToString(list.item(i));
				if (s != null) {
					rtn.add(s);
				}
			}
		}
		return rtn;
	}

	public List<String> getDeepValues(String xpath) throws TransformerException {
		return getDeepValues(_namespaceNode, xpath);
	}

	public List<String> getDeepValues(Node context, String xpath) throws TransformerException {
		List<String> rtn = new ArrayList<String>();
		NodeList list = selectList(context, xpath);
		if (list != null) {
			for (int i = 0; i < list.getLength(); i++) {
				String s = deepNodeToString(list.item(i));
				if (s != null) {
					rtn.add(s);
				}
			}
		}
		return rtn;
	}

}
