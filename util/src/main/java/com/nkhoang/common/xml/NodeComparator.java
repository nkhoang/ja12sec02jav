package com.nkhoang.common.xml;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @deprecated Replaced by {@link DOMEqualityUtil}
 *             <p/>
 *             Compares DOM Nodes recursively for canonical equality
 */
@Deprecated
public class NodeComparator {

	public static boolean areEqual(Node n1, Node n2) {
		if (n1 != null) {
			if (n2 != null) {
				if (n1.getNodeType() != n2.getNodeType()) {
					return false;
				}
				n1 = n1.cloneNode(true);
				n2 = n2.cloneNode(true);
				n1.normalize();
				n2.normalize();
				if (!areEqual(n1.getNodeName(), n2.getNodeName())) {
					return false;
				}
				if (!areEqual(n1.getNodeValue(), n2.getNodeValue())) {
					return false;
				}
				if (!areEqual(n1.getAttributes(), n2.getAttributes())) {
					return false;
				}
				if (!areEqual(n1.getChildNodes(), n2.getChildNodes())) {
					return false;
				}
			} else {
				return false;
			}
		} else {
			return n2 == null;
		}
		return true;
	}

	private static boolean areEqual(String s1, String s2) {
		if (s1 != null) {
			if (!s1.equals(s2)) {
				return false;
			}
		} else {
			if (s2 != null) {
				return false;
			}
		}
		return true;
	}

	public static boolean areEqual(NamedNodeMap map1, NamedNodeMap map2) {
		if (map1 != null) {
			if (map2 != null) {
				for (int i = 0; i < map1.getLength(); i++) {
					if (!areEqual(map1.item(i), map2.getNamedItem(map1.item(i).getNodeName()))) {
						return isBoolean(map1.item(i).getNodeValue()) && map2.getNamedItem(
							map1.item(i).getNodeName()) == null;
					}
				}
				for (int i = 0; i < map2.getLength(); i++) {
					if (!areEqual(map2.item(i), map1.getNamedItem(map2.item(i).getNodeName()))) {
						return isBoolean(map2.item(i).getNodeValue()) && map1.getNamedItem(
							map2.item(i).getNodeName()) == null;
					}
				}
			} else {
				return false;
			}
		} else {
			return map2 == null;
		}
		return true;
	}

	private static boolean isBoolean(String s) {
		return "true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s);
	}

	public static boolean areEqual(NodeList list1, NodeList list2) {
		if (list1 != null) {
			if (list2 != null) {
				if (list1.getLength() != list2.getLength()) {
					return false;
				}
				for (int i = 0; i < list1.getLength(); i++) {
					if (!areEqual(list1.item(i), list2.item(i))) {
						return false;
					}
				}
			}
		}
		return true;
	}

}
