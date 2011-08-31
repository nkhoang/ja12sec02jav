package com.nkhoang.common.xml.xdiff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

/**
 * This is an implementation of the distance calculation section of the X-Diff
 * algorithm The exact method used in the paper is not employed here because I
 * could not locate the complete description of that algorithm. It is based on
 * the following assumptions: (For the exact assumptions, see the paper, this is
 * a generalization) Where D(x, y) is the Distance function, and x, y are
 * leaf-nodes: D(x, null) = D(null, y) = 1 -> DELETE_LEAF, or INSERT_LEAF
 * operations D(x, y) = 1 where x != y -> UPDATE_LEAF operation One potential
 * improvement to this algorithm then would be to locate the paper by K. Zhang
 * titled "A New Editing based Distance between Unordered Labeled Trees" and
 * replace the appropriate sections of this code with an implementation of that
 * algorithm.
 */
class XDiffDistanceCalculator {

	/**
	 * Computes the distance between the two input nodes. If neither node is null,
	 * it will add an entry to the distance map for this distance, as well as for
	 * any child distances required in order to compute this distance. This is
	 * done in order to prevent computing the same distances multiple times.
	 *
	 * @param original    the original node
	 * @param other       the other node
	 * @param distanceMap the distanceMap
	 *
	 * @return the distance from original to other
	 */
	public static int computeDistance(
		Node original, Node other, Map<Tuple<Node>, Integer> distanceMap, boolean includeAttributes,
		Map<String, List<String>> hint) {
		if (original == null && other == null) {
			return 0; // they are the same
		}
		// we might have already computed this distance
		if (original != null && other != null && distanceMap.containsKey(
			XDiffUtil.computeMatchKey(
				original, other))) {
			return distanceMap.get(XDiffUtil.computeMatchKey(original, other));
		}

		// if e1 is non-null leaf and e2 is null
		// or e1 is null and e2 is non-null leaf
		if ((original != null && !original.hasChildNodes() && other == null) ||
		    original == null && other != null && !other.hasChildNodes()) {
			return 1; // insert_leaf or delete_leaf
		}

		// if e1 and e2 are non-null leaves
		if (original != null && !original.hasChildNodes() && other != null && !original.hasChildNodes()) {
			// if the signature is the same
			if (XDiffUtil.getSignature(original).equals(XDiffUtil.getSignature(other))) {
				String v1 = original.getTextContent();
				String v2 = other.getTextContent();
				// and the content is the same
				if (StringUtils.equals(v1, v2)) {
					// TODO: potentially check for absolute subtree equality
					distanceMap.put(new Tuple<Node>(original, other), Integer.valueOf(0));
					return 0; // they are the same
				}
				// if the content is different
				else {
					distanceMap.put(new Tuple<Node>(original, other), Integer.valueOf(1));
					return 1; // update_leaf
				}
			}
		}

		// we should only be here if we're dealing with two non-null leaf nodes
		// that have different signatures, or if we're comparing at least one
		// non-leaf subtree to something else

		if (original == null) {
			List<Node> children = XDiffUtil.getChildren(other, includeAttributes);
			int distance = 0;
			for (Node child : children) {
				distance += computeDistance(
					null, child, distanceMap, includeAttributes, hint) + 1;
			}
			return distance;
		} else if (other == null) {
			List<Node> children = XDiffUtil.getChildren(original, includeAttributes);
			int distance = 0;
			for (Node child : children) {
				distance += computeDistance(
					child, null, distanceMap, includeAttributes, hint) + 1;
			}
			return distance;
		} else {
			// Now we know we have two non-null, non-leaf subtrees
			if (XDiffUtil.getSignature(original).equals(XDiffUtil.getSignature(other))) {
				List<Node> children1 = XDiffUtil.getChildren(original, includeAttributes);
				List<Node> children2 = XDiffUtil.getChildren(other, includeAttributes);

				// TODO: this is the section that could be improved by using the
				// referenced algorithm.  But, the algorithm in use now has gone through
				//several iterations and appears to work correctly, and quickly
				List<Tuple<Node>> mapping = getMinCostMapping(
					children1, children2, distanceMap, includeAttributes, hint);

				// Add up the distances of each mapping, that is the distance of this
				// mapping
				int sum = 0;
				for (Tuple<Node> tuple : mapping) {
					sum += computeDistance(
						tuple._e1, tuple._e2, distanceMap, includeAttributes, hint);
				}
				distanceMap.put(new Tuple<Node>(original, other), Integer.valueOf(sum));
				return sum;
			} else {
				// if the signatures are different, there's no recourse but to delete
				// the first one
				// and then insert the second one, which is what this distance
				// represents
				int distance = computeDistance(
					original, null, distanceMap, includeAttributes, hint) + computeDistance(
					null, other, distanceMap, includeAttributes, hint);
				distanceMap.put(
					new Tuple<Node>(original, other), Integer.valueOf(distance));
				return distance;
			}
		}
	}

	/**
	 * This method returns the minimum cost mapping between the given node lists
	 *
	 * @param nodes1            the original list of nodes
	 * @param nodes2            the other list of nodes
	 * @param distanceMap       the distance map
	 * @param includeAttributes whether or not to include attributes
	 *
	 * @return A List representing the minimum cost mapping from the original
	 *         input list to the other
	 */
	public static List<Tuple<Node>> getMinCostMapping(
		List<Node> nodes1, List<Node> nodes2, Map<Tuple<Node>, Integer> distanceMap, boolean includeAttributes,
		Map<String, List<String>> hint) {
		List<Tuple<Node>> tuples = new ArrayList<Tuple<Node>>();
		//we will only consider solutions that contain tuples from this list when
		//possible.
		for (int i = 0; i < nodes1.size(); i++) {
			Node node1 = nodes1.get(i);
			for (int j = 0; j < nodes2.size(); j++) {
				Node node2 = nodes2.get(j);
				if (distanceMap.containsKey(XDiffUtil.computeMatchKey(node1, node2))) {
					tuples.add(new Tuple<Node>(node1, node2));
				}
			}
		}

		List<Tuple<Node>> result = new ArrayList<Tuple<Node>>();

		Map<String, List<Node>> nodes1Map = getListsBySignature(nodes1);
		Map<String, List<Node>> nodes2Map = getListsBySignature(nodes2);
		for (Map.Entry<String, List<Node>> entry : nodes1Map.entrySet()) {
			if (nodes2Map.containsKey(entry.getKey())) {
				String localName = entry.getValue().get(0).getLocalName();
				if (hint.containsKey(localName)) {
					result.addAll(
						HintHandler.getMinCostMapping(
							entry.getValue(), nodes2Map.get(entry.getKey()), distanceMap, tuples, hint.get(localName)));
				} else {
					//this accumulator is a datastructure used by the recursive helper method
					//to remember min cost mappings from sublist to sublist so they don't need
					//to be computed again
					Map<AccumulatorKey, List<Tuple<Node>>> accumulator = new HashMap<AccumulatorKey, List<Tuple<Node>>>();

					//call into the recursive helper
					result.addAll(
						getMinCostMappingHelper(
							entry.getValue(), nodes2Map.get(entry.getKey()), distanceMap, tuples, includeAttributes,
							accumulator, hint));
				}
			}
		}
		//it's possible that the recursive helper left out some of the node->null
		//or null->node mappings (although it DOES consider them in the distances)
		//so we fill them in here.
		List<Node> temp1 = new ArrayList<Node>(nodes1);
		List<Node> temp2 = new ArrayList<Node>(nodes2);

		for (Tuple<Node> t : result) {
			temp1.remove(t._e1);
			temp2.remove(t._e2);
		}
		for (Node n : temp1) {
			result.add(new Tuple<Node>(n, null));
		}
		for (Node n : temp2) {
			result.add(new Tuple<Node>(null, n));
		}
		//and return the result
		return result;
	}

	/**
	 * Private recursive helper method to find the minimum cost mapping.  This
	 * method works on some fundamental notions.  First, if we are given two
	 * lists A and B, and if the minimum cost mapping from A to B contains a given
	 * pair, say (a1, b1), then the minimum cost mapping from A to B is the
	 * minimum cost mapping from A-{a1} to B-{b1}, plus the pair (a1, b1). Second,
	 * if a given pairing is not included in the distance map by the time this
	 * method is called, then we do not need to consider solutions that include it.
	 * This is because those pairings do not occur at the same level in the tree,
	 * so we already know that if such a pairing, say (an, bm) were included in
	 * the result, it would really need to be (an, null) and (null, bm).  So we
	 * can build partial matches that do not include such pairings (as long as we
	 * correctly compute the distance for such a match!) and then fill in the null
	 * match portions later.
	 */
	private static List<Tuple<Node>> getMinCostMappingHelper(
		List<Node> nodes1, List<Node> nodes2, Map<Tuple<Node>, Integer> distanceMap, List<Tuple<Node>> tuples,
		boolean includeAttributes, Map<AccumulatorKey, List<Tuple<Node>>> accumulator, Map<String, List<String>> hint) {

		//recursive base cases:
		//BASE CASE 1: we've already computed this
		AccumulatorKey key = new AccumulatorKey(nodes1, nodes2);
		if (accumulator.containsKey(key)) {
			return accumulator.get(key);
		}
		List<Tuple<Node>> result = new ArrayList<Tuple<Node>>();
		//BASE CASE 2: both lists are one element
		if (nodes1.size() == 1 && nodes2.size() == 1) {
			Tuple<Node> tuple = new Tuple<Node>(nodes1.get(0), nodes2.get(0));
			if (tuples.contains(tuple)) { //we either return the obvious mapping
				result.add(tuple);
			}
			//otherwise we fall through to the end of the method
		}
		//BASE CASE 3: one or the other list is empty
		else if (nodes1.isEmpty() || nodes2.isEmpty()) {
			//we also fall through to the end of the method
		}
		//RECURSIVE CASE:
		else {
			//keep track of the minimum value mapping for this call
			int outerMin = Integer.MAX_VALUE;
			//try pairing up each outer node
			OUTER:
			for (Node n1 : nodes1) {
				List<Node> list1 = new ArrayList<Node>(nodes1);
				list1.remove(n1);
				//keep track of the minimum value mapping using n1
				int innerMin = Integer.MAX_VALUE;
				List<Tuple<Node>> innerMinResult = null;
				//go through each inner node for the given outer node
				//the idea here is that if the minimum cost mapping includes this pair
				//of nodes, then the rest of the mapping will be the minimum cost
				//mapping between the remaining two sets of nodes
				for (Node n2 : nodes2) {
					List<Node> list2 = new ArrayList<Node>(nodes2);
					list2.remove(n2);
					//variable to house the mapping being considered
					List<Tuple<Node>> mapping = null;
					//this is the current pairing of nodes
					Tuple<Node> current = new Tuple<Node>(n1, n2);
					//if the input list of tuples does not contain this pair, then
					//the optimal solution does not contain this pair, so go on
					if (!tuples.contains(current)) {
						continue;
					}
					int sum = 0;
					//get the minimum cost mapping of the remaining nodes
					//wrap it so we don't mess with the one in the accumulator
					mapping = new ArrayList<Tuple<Node>>(
						getMinCostMappingHelper(
							list1, list2, distanceMap, tuples, includeAttributes, accumulator, hint));
					mapping.add(current);
					//this sum is the total distance associated with the above mapping
					sum = computeDistance(n1, n2, distanceMap, includeAttributes, hint);

					//remember the inner minimum
					if (sum < innerMin) {
						innerMin = sum;
						innerMinResult = mapping;
						if (sum == 0) {
							//no point hanging around...
							result = innerMinResult;
							break OUTER;
						}
					}
				}
				//remember the outer minimum
				if (innerMin < outerMin) {
					outerMin = innerMin;
					result = innerMinResult;
				}
			}
		}
		accumulator.put(key, result);
		return result;
	}

	private static Map<String, List<Node>> getListsBySignature(List<Node> nodes) {
		Map<String, List<Node>> result = new HashMap<String, List<Node>>();
		for (Node node : nodes) {
			String signature = XDiffUtil.getSignature(node);
			if (result.containsKey(signature)) {
				result.get(signature).add(node);
			} else {
				List<Node> list = new ArrayList<Node>();
				list.add(node);
				result.put(signature, list);
			}
		}
		return result;
	}
}
