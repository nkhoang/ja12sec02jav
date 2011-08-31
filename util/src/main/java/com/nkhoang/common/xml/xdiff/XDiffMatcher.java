package com.nkhoang.common.xml.xdiff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Node;


/** This is an implementation of the matching section of the X-Diff algorithm */
class XDiffMatcher {

	/**
	 * Performs the matching between the trees rooted at the two input nodes.
	 *
	 * @param original    the root of the original tree
	 * @param other       the root of the other tree
	 * @param distanceMap the map to populate with known distances as the match is computed
	 *
	 * @return a list of Tuples representing the nodes matched between the
	 *         original tree and the other tree. Nodes that do not match will not
	 *         appear anywhere in this matching.
	 */
	public static List<Tuple<Node>> doMatch(
		Node original, Node other, Map<Tuple<Node>, Integer> distanceMap, boolean includeAttributes,
		List<String> filterIgnore, Map<String, List<String>> hint) {

		// STEP 1: Reduce Matching Space
		final Set<Node> toSkip = filterNodes(
			original, other, distanceMap, includeAttributes, filterIgnore);

		List<Node> nodes1 = XDiffUtil.getAllLeafNodes(original, includeAttributes);
		List<Node> nodes2 = XDiffUtil.getAllLeafNodes(other, includeAttributes);
		// STEP 2: compute editing distance for (n1 -> n2)
		do {
			for (int i = 0; i < nodes1.size(); i++) {
				Node node1 = nodes1.get(i);
				if (toSkip.contains(node1)) {
					continue;
				}
				for (int j = 0; j < nodes2.size(); j++) {
					Node node2 = nodes2.get(j);
					if (toSkip.contains(node2)) {
						continue;
					}
					if (XDiffUtil.getSignature(node1).equals(XDiffUtil.getSignature(node2))) {

						Tuple<Node> matchKey = XDiffUtil.computeMatchKey(node1, node2);
						int distance = XDiffDistanceCalculator.computeDistance(
							node1, node2, distanceMap, includeAttributes, hint);
						distanceMap.put(matchKey, distance);
					}
				}
			}

			// update nodes1 and nodes2 to be parents of previous nodes
			nodes1 = XDiffUtil.getAllParents(nodes1);
			nodes2 = XDiffUtil.getAllParents(nodes2);
		} while (!(nodes1.isEmpty() || nodes2.isEmpty()));

		// STEP 3: record all matchings
		List<Tuple<Node>> matchResult = new ArrayList<Tuple<Node>>();

		if (!XDiffUtil.getSignature(original).equals(XDiffUtil.getSignature(original))) {
			List<Tuple<Node>> emptyMatch = Collections.emptyList();
			return emptyMatch; // empty match
		} else {
			matchResult = buildResult(
				original, other, distanceMap, includeAttributes, hint);
		}
		return matchResult;
	}


	/**
	 * Helper to build the match result
	 *
	 * @param original    the original node
	 * @param other       the other node
	 * @param distanceMap the distance map used to find lowest cost mappings
	 *
	 * @return the list of tuples constituting the match from the original to the
	 *         other.
	 */
	private static List<Tuple<Node>> buildResult(
		Node original, Node other, Map<Tuple<Node>, Integer> distanceMap, boolean includeAttributes,
		Map<String, List<String>> hint) {
		List<Tuple<Node>> result = new ArrayList<Tuple<Node>>();
		List<Node> children1 = XDiffUtil.getChildren(original, includeAttributes);
		List<Node> children2 = XDiffUtil.getChildren(other, includeAttributes);
		result.add(new Tuple<Node>(original, other));
		if (!(children1.isEmpty() || children2.isEmpty())) {
			List<Tuple<Node>> match = XDiffDistanceCalculator.getMinCostMapping(
				children1, children2, distanceMap, includeAttributes, hint);
			for (Tuple<Node> tuple : match) {
				if (!(tuple._e1 == null || tuple._e2 == null)) {
					result.addAll(
						buildResult(
							tuple._e1, tuple._e2, distanceMap, includeAttributes, hint));
				}
			}
		}
		return result;
	}

	/**
	 * This method is designed to find equal subtrees and add them to the distance
	 * map with a distance of zero.  This step greatly shrinks the problem space
	 * to be considered later, and speeds up the distance calculation step for
	 * higher level trees.
	 *
	 * @param original    the root of the original tree
	 * @param other       the root of the other tree
	 * @param distanceMap the distance map to update
	 *
	 * @return A Set of all nodes matched in the the filter step, so that they can
	 *         be excluded from the later computation steps
	 */
	private static Set<Node> filterNodes(
		Node original, Node other, Map<Tuple<Node>, Integer> distanceMap, boolean includeAttributes,
		List<String> filterIgnore) {
		List<Node> level1 = new ArrayList<Node>();
		List<Node> level2 = new ArrayList<Node>();
		level1.add(original);
		level2.add(other);

		return processLevel(
			level1, level2, distanceMap, includeAttributes, filterIgnore);
	}

	/**
	 * This is the helper method that does all the work in the filter step.  It
	 * finds matches between children at a common level in the tree.
	 *
	 * @param level1      The original tree children nodes
	 * @param level2      The other tree children nodes
	 * @param distanceMap The distance map to update
	 *
	 * @return
	 */
	private static Set<Node> processLevel(
		List<Node> level1, List<Node> level2, Map<Tuple<Node>, Integer> distanceMap, boolean includeAttributes,
		List<String> filterIgnore) {

		//We store nodes to remove  from the first list in here temporarily
		Set<Node> level1Removed = new HashSet<Node>();

		//This is our result, we add all nodes that should be excluded from the later
		//process to this set.
		Set<Node> toSkip = new HashSet<Node>();

		//compare nodes between levels
		for (Node node1 : level1) {
			if (level1Removed.contains(node1) || filterIgnore.contains(node1.getLocalName())) {
				continue;
			}
			List<Node> matches = new ArrayList<Node>();
			for (Node node2 : level2) {
				if (filterIgnore.contains(node2.getLocalName())) {
					continue;
				}
				//if the signature is the same, the hash is the same, and finally the
				//nodes are indeed the same, then we tag the nodes for removal, update
				//the distance tree, and go through the subtree children since they must
				//all match up as well
				if (XDiffUtil.getSignature(node1).equals((XDiffUtil.getSignature(node2))) && XDiffUtil.xHashesAreEqual(
					XDiffUtil.computeXHash(node1, includeAttributes), XDiffUtil.computeXHash(node2, includeAttributes))
					/*&& DOMEqualityUtil.isEqual(node1, node2, true, true, true, true)*/) {

					//now we need to do an annoying thing, that makes this run slower.
					//basically, there are cases where this node will match up, but yet we
					//can't skip it because it also matches other nodes at this level.
					matches.add(node2);
				}
			}

			if (matches.size() == 1) {
				Node node2 = matches.get(0);
				distanceMap.put(XDiffUtil.computeMatchKey(node1, node2), 0);
				toSkip.add(node1);
				toSkip.add(node2);
				level1Removed.add(node1);
				level2.remove(node2);
				toSkip.addAll(
					processLevel(
						XDiffUtil.getChildren(node1, includeAttributes),
						XDiffUtil.getChildren(node2, includeAttributes), distanceMap, includeAttributes,
						Collections.<String>emptyList()));
			} else if (matches.size() > 1 && node1.getNodeType() != Node.TEXT_NODE) {
				//given multiple matches, we can check if one potential match has more
				//sibling matches than the others

				List<Integer> siblingMatches = new ArrayList<Integer>(matches.size());
				for (Node match : matches) {
					Integer count = 0;
					List<Node> matchSiblings = XDiffUtil.getChildren(
						match.getParentNode(), includeAttributes);
					List<Node> siblings = XDiffUtil.getChildren(
						node1.getParentNode(), includeAttributes);
					for (Node sibling : siblings) {
						for (Node potential : matchSiblings) {
							if (XDiffUtil.getSignature(sibling).equals((XDiffUtil.getSignature(potential))) &&
							    XDiffUtil.computeXHash(sibling, includeAttributes) ==
							    (XDiffUtil.computeXHash(potential, includeAttributes))) {
								count++;
							}
						}
					}
					siblingMatches.add(count);
				}
				//now we know how many sibling matches each match has
				int max = 0;
				for (int i = 0; i < matches.size(); i++) {
					if (max < siblingMatches.get(i)) {
						max = siblingMatches.get(i);
					}
				}
				Node bestMatch = null;
				for (int i = 0; i < matches.size(); i++) {
					if (max == siblingMatches.get(i)) {
						if (bestMatch == null) {
							bestMatch = matches.get(i);
						} else {
							bestMatch = null;
							break;
						}
					}
				}
				if (bestMatch != null) {
					Set<Node> skip = processLevel(
						XDiffUtil.getChildren(node1.getParentNode(), includeAttributes),
						XDiffUtil.getChildren(bestMatch.getParentNode(), includeAttributes), distanceMap,
						includeAttributes, filterIgnore);
					for (Node node : skip) {
						if (level1.contains(node)) {
							level1Removed.add(node);
						} else if (level2.contains(node)) {
							level2.remove(node);
						}
					}
					toSkip.addAll(skip);
				}
			}
		}
		//now remove all the matched nodes from level1
		level1.removeAll(level1Removed);
		//we get the unmatched children of these levels
		List<Node> nextLevel1 = XDiffUtil.getAllChildren(level1, includeAttributes);
		List<Node> nextLevel2 = XDiffUtil.getAllChildren(level2, includeAttributes);

		//if there are children in both levels, we recurse into those children
		if (!(nextLevel1.isEmpty() || nextLevel2.isEmpty())) {
			toSkip.addAll(
				processLevel(
					nextLevel1, nextLevel2, distanceMap, includeAttributes, filterIgnore));
		}
		//hand back the Set of matched up nodes
		return toSkip;
	}
}
