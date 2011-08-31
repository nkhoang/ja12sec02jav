package com.nkhoang.common.xml.xdiff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nkhoang.common.xml.XMLUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;


/**
 * This is an implementation of the XML differencing algorithm presented in the
 * paper by Yuan Wang, David J. DeWitt, and Jin-Yi Cai titled: "X-Diff: An
 * Effective Change Detection Algorithm for XML Documents" The algorithm is
 * designed to compute the difference between two un-ordered XML documents.
 * (That is, two XML documents where sibling order is not relevant, so if the
 * only differences are ordering of sibling nodes the documents will be deemed
 * the same.) The difference is output in the form of an Edit Script, which is a
 * discrete list of steps to take to convert the original document into the
 * other document.
 */
public class XDiff {


	/**
	 * Computes a difference and applies all changes as markup to the original
	 * document
	 *
	 * @param original the original XML Document
	 * @param other    the other XML document
	 */
	public static void generateDiffDocument(
		Document original, Document other, boolean includeElements) {
		generateDiffDocument(
			original, other, includeElements, Collections.<String>emptyList(),
			Collections.<String, List<String>>emptyMap());
	}

	public static void generateDiffDocument(
		Document original, Document other, boolean includeElements, List<String> filterIgnore,
		Map<String, List<String>> hint) {
		if (hint == null) {
			hint = Collections.<String, List<String>>emptyMap();
		}
		Element originalElement = original.getDocumentElement();
		Element otherElement = other.getDocumentElement();
		List<XDiffEditStep> steps = compute(
			originalElement, otherElement, includeElements, filterIgnore, hint);
		for (XDiffEditStep step : steps) {
			step.applyOperationAsMarkup();
		}
	}

	/**
	 * Computes the edit script from the original document to the other document
	 *
	 * @param original the original XML document
	 * @param other    the other XML document
	 *
	 * @return the edit script
	 */
	public static List<XDiffEditStep> compute(Document original, Document other) {
		return compute(original, other, true);
	}

	/**
	 * Computes the edit script from the original document to the other document
	 *
	 * @param original the original XML document
	 * @param other    the other XML document
	 *
	 * @return the edit script
	 */
	public static List<XDiffEditStep> compute(
		Document original, Document other, boolean includeAttributes) {
		Element originalElement = original.getDocumentElement();
		Element otherElement = other.getDocumentElement();
		return compute(originalElement, otherElement, includeAttributes);
	}

	/**
	 * Computes the edit script from the original document to the other document.
	 * The algorithm assumes that there is only element and attribute content.
	 *
	 * @param original the root node of the original XML document
	 * @param other    the root node of the other XML document
	 *
	 * @return the edit script
	 */
	public static List<XDiffEditStep> compute(
		Element original, Element other, boolean includeAttributes) {
		return compute(
			original, other, includeAttributes, Collections.<String>emptyList(),
			Collections.<String, List<String>>emptyMap());
	}

	/**
	 * Computes the edit script from the original document to the other document.
	 * The algorithm assumes that there is only element and attribute content.
	 *
	 * @param original           the root node of the original XML document
	 * @param other              the root node of the other XML document
	 * @param includeAtrtributes whether or not to include attributes in the differencing
	 * @param filterIgnore       This is a list of nodes to skip in the prefilter step.  Using it
	 *                           will potentially lead to unexpected behavior.  It is designed to
	 *                           allow preferential treatment to types of differences.  For
	 *                           instance, ignoring rank will make XDiff prefer edit scripts that
	 *                           include rank changes in the case that subtree filter mappings
	 *                           coiuld include rank to rank mapping or could not.
	 *
	 * @return the edit script
	 */
	public static List<XDiffEditStep> compute(
		Element original, Element other, boolean includeAttributes, List<String> filterIgnore,
		Map<String, List<String>> hint) {
		if (hint == null) {
			hint = Collections.<String, List<String>>emptyMap();
		}
		// Trims out formatting node
		pruneDom(original);
		pruneDom(other);

		// check they aren't the same to begin with
		if (XDiffUtil.xHashesAreEqual(
			XDiffUtil.computeXHash(original, includeAttributes), XDiffUtil.computeXHash(other, includeAttributes))) {
			List<XDiffEditStep> emptyResult = Collections.emptyList();
			return emptyResult;
		}

		// this is the "distanceTable" the algorithm discusses, I use a mapping
		// from the match to the distance associated with that match
		Map<Tuple<Node>, Integer> distanceMap = new HashMap<Tuple<Node>, Integer>();

		// compute the lowest cost matching from the original to the other
		// nodes that are not matched between documents will not be included in this
		// match (no matches of the form (node, null) or (null, node) will appear)
		List<Tuple<Node>> match = XDiffMatcher.doMatch(
			original, other, distanceMap, includeAttributes, filterIgnore, hint);

		// generate editScript from matching
		List<XDiffEditStep> editScript = XDiffEditScriptGenerator.generateEditScript(
			original, other, match, distanceMap, includeAttributes);

		//clean up the strangeness that sometime shows up in here...
		//this is hacky, but it cleans up the mess that occasionally appears
		//and is fast.
		editScript = EditScriptCleaner.clean(editScript);
		return editScript;
	}

	public static Document restoreOriginal(Document markedDocument) {
		Document clone = XMLUtil.parse(XMLUtil.serialize(markedDocument));
		Node root = clone.getDocumentElement();
		List<ProcessingInstruction> instructions = XDiffUtil.getAllXDiffProcessingInstructions(root);

		for (ProcessingInstruction pi : instructions) {
			EmbeddedInstruction.removeProcessingInstruction(pi);
		}
		return clone;
	}

	public static Document restoreOther(Document markedDocument) {
		Document clone = XMLUtil.parse(XMLUtil.serialize(markedDocument));
		Node root = clone.getDocumentElement();
		List<ProcessingInstruction> instructions = XDiffUtil.getAllXDiffProcessingInstructions(root);

		for (ProcessingInstruction pi : instructions) {
			EmbeddedInstruction.applyProcessingInstruction(pi);
		}
		return clone;
	}

	/**
	 * Helper method to prune out formatting nodes that othersise complicate
	 * things and slow them down.
	 *
	 * @param document
	 */
	private static void pruneDom(Element element) {
		pruneDomHelper(element);
	}

	private static void pruneDomHelper(Node node) {
		List<Node> children = XDiffUtil.getChildren(node, true);
		List<Node> toRemove = new ArrayList<Node>();
		for (Node child : XDiffUtil.getChildren(node, true)) {
			if (shouldRemoveChild(child, children)) {
				toRemove.add(child);
			} else if (child.hasChildNodes()) {
				pruneDomHelper(child);
			}
		}
		for (Node child : toRemove) {
			node.removeChild(child);
		}
	}

	public static boolean shouldRemoveChild(Node child, List<Node> children) {
		if (children.size() > 1 && child.getNodeType() == Node.TEXT_NODE) {
			for (Node node : children) {
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					return true;
				}
			}
		}
		return false;
	}
}
