package com.nkhoang.common.xml.xdiff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.nkhoang.common.xml.DOMUtil;
import org.apache.commons.collections.ListUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

/**
 * A utility class used by various parts of the XDiff implementation
 *
 */
class XDiffUtil {

	/**
	 * Gets the XDiff signature, which must be the same for nodes of the same type
	 * that have the same ancestry
	 *
	 * @param node the node to obtain the signature for
	 *
	 * @return the node's signature
	 */
	public static String getSignature(Node node) {
		StringBuilder sb = new StringBuilder();
		sb.append(node.getNodeType());
		Node current = node;
		do {
			String namespace = current.getNamespaceURI();
			if (namespace == null) {
				sb.append(current.getNodeName());
			} else {
				sb.append(namespace + current.getLocalName());
			}
			if (current.getNodeType() == Node.ATTRIBUTE_NODE) {
				current = ((Attr) current).getOwnerElement();
			} else {
				current = current.getParentNode();
			}
		} while (current != null);
		return sb.toString();
	}


	/**
	 * Method to get a list of X-Diff processing instructions below a node.
	 *
	 * @param node
	 *
	 * @return
	 */
	public static List<ProcessingInstruction> getAllXDiffProcessingInstructions(
		Node node) {
		List<ProcessingInstruction> instructions = DOMUtil.getAllNodesOfType(
			node, ProcessingInstruction.class);
		List<ProcessingInstruction> result = new ArrayList<ProcessingInstruction>();
		for (ProcessingInstruction instruction : instructions) {
			if (XDiffOperation.X_DIFF_TARGET.equals(instruction.getTarget())) {
				result.add(instruction);
			}
		}
		return result;
	}


	/**
	 * Computes the X-Hash of a Node. The X-Hash should be the same for nodes
	 * with the same (up to sibling ordering) subtree contents
	 *
	 * @param node the node to compute the X-Hash of
	 *
	 * @return the X-Hash as a List<Long> - Hashes are equal if they contain the
	 *         same values.  This can be tested using xHashesAreEqual()
	 */
	public static List<Long> computeXHash(Node node, boolean includeAttributes) {
		Long sum = new Long(getSignature(node).hashCode());

		// recursive base case
		if (!node.hasChildNodes()) {
			List<Long> result = new ArrayList<Long>(1);
			result.add(sum + node.getTextContent().hashCode());
			return result;
		}

		// recursive case
		List<Node> children = getChildren(node, includeAttributes);

		List<Long> childHashes = new ArrayList<Long>();
		for (Node child : children) {
			List<Long> hash = computeXHash(child, includeAttributes);
			childHashes.addAll(hash);
			for (Long val : hash) {
				sum += val;
			}
		}
		childHashes.add(sum);

		return childHashes;
	}

	public static boolean xHashesAreEqual(List<Long> hash1, List<Long> hash2) {
		if (hash1 == hash2) {
			return true;
		} else if (hash1 == null || hash2 == null) {
			return false;
		} else if (hash1.size() != hash2.size()) {
			return false;
		} else {
			Collections.sort(hash1);
			Collections.sort(hash2);
			return ListUtils.isEqualList(hash1, hash2);
		}
	}

	/**
	 * Simple utility method to get the parents of all nodes in a given list
	 *
	 * @param children the list of nodes to get the parents of
	 *
	 * @return the unique list of parents of the input nodes
	 */
	public static List<Node> getAllParents(List<Node> children) {
		List<Node> parents = new ArrayList<Node>();
		for (Node child : children) {
			Node parent = child.getParentNode();
			if (parent != null) {
				if (!parents.contains(parent)) {
					parents.add(parent);
				}
			}
		}
		return parents;
	}

	/**
	 * Simple utility method to get the children of all nodes in a given list
	 *
	 * @param nodes the list of nodes to get the children of
	 *
	 * @return the unique list of children of the input nodes
	 */
	public static List<Node> getAllChildren(
		List<Node> nodes, boolean includeAttributes) {
		List<Node> children = new ArrayList<Node>();
		for (Node node : nodes) {
			children.addAll(getChildren(node, includeAttributes));
		}
		return children;
	}

	/**
	 * Returns a key to be used in the distance map for a given matching
	 *
	 * @param original the 'from' node in the match
	 * @param other    the 'to' node in the match
	 *
	 * @return a Tuple to be used as a key in the distance map
	 */
	public static Tuple<Node> computeMatchKey(Node original, Node other) {
		return new Tuple<Node>(original, other);
	}


	/**
	 * Returns all child nodes of the given node in a list, including attributes
	 *
	 * @param parent the node to get the children of
	 *
	 * @return A list of nodes containing all child and attribute nodes of the
	 *         given node
	 */
	public static List<Node> getChildren(Node parent, boolean includeAttributes) {
		List<Node> children = new ArrayList<Node>();
		// add child nodes
		NodeList childNodeList = parent.getChildNodes();
		for (int i = 0; i < childNodeList.getLength(); i++) {
			Node child = childNodeList.item(i);
			children.add(child);
		}
		// add attributes
		if (includeAttributes && parent.hasAttributes()) {
			NamedNodeMap attributes = parent.getAttributes();
			for (int i = 0; i < attributes.getLength(); i++) {
				Node child = attributes.item(i);
				children.add(child);
			}
		}
		return children;
	}


	/**
	 * Utility to get all leaf nodes beneath a given node
	 *
	 * @param node the node to find all leaf nodes under
	 *
	 * @return a list of all nodes that are leaf nodes under the input node
	 */
	public static List<Node> getAllLeafNodes(Node node, boolean includeAttributes) {

		// recursive base case
		if (!node.hasChildNodes()) {
			return Collections.singletonList(node);
		}

		// recursive case
		List<Node> leaves = new ArrayList<Node>();
		List<Node> children = getChildren(node, includeAttributes);
		for (Node child : children) {
			leaves.addAll(getAllLeafNodes(child, includeAttributes));
		}

		return leaves;
	}
}
