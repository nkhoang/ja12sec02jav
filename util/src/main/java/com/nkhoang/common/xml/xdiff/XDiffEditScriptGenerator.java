package com.nkhoang.common.xml.xdiff;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static com.nkhoang.common.xml.xdiff.XDiffOperation.*;

import org.w3c.dom.Node;

/**
 * This is an implementation of the edit script generation section of the X-Diff
 * algorithm.
 */
class XDiffEditScriptGenerator {

	/**
	 * Computes a list of the steps required to transform the input Node to the
	 * output Node.
	 *
	 * @param n1          The 'from' node
	 * @param n2          The 'to' node
	 * @param matching    the matching to use when computing the edit script
	 * @param distanceMap the pre-computed distance map
	 *
	 * @return the list of steps that make up the Edit Script
	 */
	public static List<XDiffEditStep> generateEditScript(
		Node n1, Node n2, List<Tuple<Node>> matching, Map<Tuple<Node>, Integer> distanceMap,
		boolean includeAttributes) {
		List<XDiffEditStep> result = new ArrayList<XDiffEditStep>();
		Integer rootDist = distanceMap.get(XDiffUtil.computeMatchKey(n1, n2));
		Node n1match = getMatchFor(n1, matching);

		// if there is a match for n1, and it's not equal to n2, then we have to
		// delete n1 and insert n2 in it's place.
		if (n1match != null && !n1match.equals(n2)) {
			// delete subtree rooted at node n1
			if (n1.hasChildNodes()) {
				result.add(new XDiffEditStep(DELETE_SUBTREE, n1));
			} else {
				result.add(new XDiffEditStep(DELETE_LEAF, n1));
			}
			// insert subtree n2 as a child of the parent of n1
			if (n2.hasChildNodes()) {
				result.add(new XDiffEditStep(INSERT_SUBTREE, n1.getParentNode(), n2));
			} else {
				result.add(new XDiffEditStep(INSERT_LEAF, n1.getParentNode(), n2));
			}
			return result;
		}
		// Otherwise, if it is n2, and the distance between them is 0, then there
		// are no steps required to go from one to the other, they are the same.
		else if (rootDist != null && rootDist.intValue() == 0) {
			return result;
		}
		// Otherwise, they match up, but they are not the same
		else {
			// so we'll go through and look at the child nodes
			List<Node> c1 = XDiffUtil.getChildren(n1, includeAttributes);
			List<Node> c2 = XDiffUtil.getChildren(n2, includeAttributes);

			// for each entry in the matching
			for (Tuple<Node> tuple : matching) {
				Node match1 = tuple._e1;
				Node match2 = tuple._e2;
				// if the entry is a match between the child nodes of the current node
				if (c1.contains(match1) && c2.contains(match2)) {
					// remove them from the child lists, so we know what children were not
					// mapped
					c1.remove(match1);
					c2.remove(match2);
					// if both are leaf nodes
					if (!(match1.hasChildNodes() || match2.hasChildNodes())) {
						// if the distance is 0
						Integer dist = distanceMap.get(
							XDiffUtil.computeMatchKey(
								match1, match2));
						if (dist != null && dist.intValue() == 0) {
							// they are the same, so we do nothing. This is just here for
							// clarity
						} else {
							// otherwise we just update the value of node match1 to be match2
							result.add(new XDiffEditStep(UPDATE_LEAF, match1, match2));
						}
					}
					// if they are not both leaf nodes
					else {
						// then we recurse, and add the edit script we get into the edit
						// script for n1 to n2
						result.addAll(
							generateEditScript(
								match1, match2, matching, distanceMap, includeAttributes));
					}
				}
			}
			// Now we've gone through each entry in the matching and there may still
			// be child nodes
			// that we haven't found matches for. So we need to either remove or
			// insert those nodes
			for (Node node : c1) {
				// These are nodes in the input that are not in the output. We'll delete
				// them.
				if (node.hasChildNodes()) {
					result.add(new XDiffEditStep(DELETE_SUBTREE, node));
				} else {
					result.add(new XDiffEditStep(DELETE_LEAF, node));
				}
			}
			for (Node node : c2) {
				// These are nodes in the output that are not in the input. We'll insert
				// them.
				if (node.hasChildNodes()) {
					result.add(new XDiffEditStep(INSERT_SUBTREE, n1, node));
				} else {
					result.add(new XDiffEditStep(INSERT_LEAF, n1, node));
				}
			}

			return result;
		}
	}


	/**
	 * Helper method that searches through the list of matches to find the one for
	 * the input node.
	 *
	 * @param input   the node to find a mtch for
	 * @param matches the matches to look through
	 *
	 * @return the match node
	 */
	private static Node getMatchFor(Node input, List<Tuple<Node>> matches) {
		for (Tuple<Node> match : matches) {
			if (match._e1.equals(input)) {
				return match._e2;
			}
		}
		return null;
	}
}
