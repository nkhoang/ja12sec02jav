package com.nkhoang.common.xml.xdiff;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class EditScriptCleaner {

	/**
	 * For some reason, XDiff will occasionally create a delete_leaf and
	 * insert_leaf series of instructions when an update_leaf would do perfectly.
	 * I've tried a number of things to make them go away and everything either
	 * slows the algorithm down tremendously or doesn't work correctly.  This is a
	 * patch that I don't like, but it works to fix these when they show up.
	 */
	public static List<XDiffEditStep> clean(List<XDiffEditStep> editScript) {
		List<XDiffEditStep> toRemove = new ArrayList<XDiffEditStep>();
		List<XDiffEditStep> toAdd = new ArrayList<XDiffEditStep>();
		for (int i = 0; i < editScript.size(); i++) {
			XDiffEditStep step1 = editScript.get(i);
			for (int j = i + 1; j < editScript.size(); j++) {
				XDiffEditStep step2 = editScript.get(j);
				if (toRemove.contains(step2)) {
					continue;
				}
				if (step1._op == XDiffOperation.INSERT_LEAF) {
					//this is the case where we're deleting a leaf and reinserting where
					//we really should be updating
					if (step2._op == XDiffOperation.DELETE_LEAF) {
						reconcileInsertLeafDeleteLeaf(toRemove, toAdd, step1, step2);
					}
					//this is the case where we're deleting a node and then
					//inserting the same node back because one was empty
					else if (step2._op == XDiffOperation.DELETE_SUBTREE) {
						reconcileInsertLeafDeleteSubtree(toRemove, toAdd, step1, step2);
					}
				} else if (step1._op == XDiffOperation.INSERT_SUBTREE) {
					//this is the case where we're deleting a leaf and reinserting where
					//we really should be updating
					if (step2._op == XDiffOperation.DELETE_LEAF) {
						reconcileInsertSubtreeDeleteLeaf(toRemove, toAdd, step1, step2);
					} else if (step2._op == XDiffOperation.DELETE_SUBTREE) {
						reconcileInsertSubtreeDeleteSubtree(toRemove, toAdd, step1, step2);
					}
				} else if (step1._op == XDiffOperation.DELETE_LEAF) {
					//this is the case where we're deleting a leaf and reinserting where
					//we really should be updating
					if (step2._op == XDiffOperation.INSERT_LEAF) {
						reconcileInsertLeafDeleteLeaf(toRemove, toAdd, step2, step1);
					}
					if (step2._op == XDiffOperation.INSERT_SUBTREE) {
						reconcileInsertSubtreeDeleteLeaf(toRemove, toAdd, step2, step1);
					}
				}
				//this is the case where we're deleting a node and then
				//inserting the same node back because one was empty
				else if (step1._op == XDiffOperation.DELETE_SUBTREE) {
					if (step2._op == XDiffOperation.INSERT_LEAF) {
						reconcileInsertLeafDeleteSubtree(toRemove, toAdd, step2, step1);
					}
					if (step2._op == XDiffOperation.INSERT_SUBTREE) {
						reconcileInsertSubtreeDeleteSubtree(toRemove, toAdd, step2, step1);
					}
				}
			}
		}
		editScript.removeAll(toRemove);
		editScript.addAll(toAdd);
		return editScript;
	}

	private static void reconcileInsertLeafDeleteLeaf(
		List<XDiffEditStep> toRemove, List<XDiffEditStep> toAdd, XDiffEditStep insert, XDiffEditStep delete) {
		if (worthReconciling(insert, delete)) {
			XDiffEditStep replacement = new XDiffEditStep(
				XDiffOperation.UPDATE_LEAF, new Node[]{delete._args[0], insert._args[1]});
			toAdd.add(replacement);
			toRemove.add(insert);
			toRemove.add(delete);
		}
	}

	private static void reconcileInsertLeafDeleteSubtree(
		List<XDiffEditStep> toRemove, List<XDiffEditStep> toAdd, XDiffEditStep insert, XDiffEditStep delete) {
		if (worthReconciling(insert, delete)) {
			//if the insert is empty
			NodeList insertChildren = insert._args[1].getChildNodes();
			NodeList deleteChildren = delete._args[0].getChildNodes();
			if (insertChildren.getLength() == 0) {
				// and the delete is text valued
				if (deleteChildren.getLength() == 1 && deleteChildren.item(0).getNodeType() == Node.TEXT_NODE) {
					//we need to put an empty TextNode child in in order to update correctly
					Node child = insert._args[1].getOwnerDocument().createTextNode("");
					insert._args[1].appendChild(child);
					XDiffEditStep replacement = new XDiffEditStep(
						XDiffOperation.UPDATE_LEAF,
						new Node[]{delete._args[0].getFirstChild(), insert._args[1].getFirstChild()});
					toAdd.add(replacement);
					toRemove.add(insert);
					toRemove.add(delete);
				}
				//if the delete contains one or more elements
				else if (deleteChildren.getLength() > 0) {
					boolean updated = false;
					for (int i = 0; i < deleteChildren.getLength(); i++) {
						Node toDelete = deleteChildren.item(i);
						if (toDelete.getNodeType() == Node.ELEMENT_NODE) {
							XDiffEditStep replacement = new XDiffEditStep(
								XDiffOperation.DELETE_SUBTREE, new Node[]{toDelete});
							toAdd.add(replacement);
							updated = true;
						}
					}
					if (updated) {
						toRemove.add(insert);
						toRemove.add(delete);
					}
				}
			}
		}
	}

	private static void reconcileInsertSubtreeDeleteLeaf(
		List<XDiffEditStep> toRemove, List<XDiffEditStep> toAdd, XDiffEditStep insert, XDiffEditStep delete) {
		if (worthReconciling(insert, delete)) {
			//if the delete is empty
			NodeList insertChildren = insert._args[1].getChildNodes();
			NodeList deleteChildren = delete._args[0].getChildNodes();
			if (deleteChildren.getLength() == 0) {
				//if the insert is text valued
				if (insertChildren.getLength() == 1 && insertChildren.item(0).getNodeType() == Node.TEXT_NODE) {
					//we need to put an empty TextNode child in in order to update correctly
					Node child = delete._args[0].getOwnerDocument().createTextNode("");
					delete._args[0].appendChild(child);
					XDiffEditStep replacement = new XDiffEditStep(
						XDiffOperation.UPDATE_LEAF,
						new Node[]{delete._args[0].getFirstChild(), insert._args[1].getFirstChild()});
					toAdd.add(replacement);
					toRemove.add(insert);
					toRemove.add(delete);
				}
				//if the insert contains one or more elements
				else if (insertChildren.getLength() > 0) {
					boolean updated = false;
					for (int i = 0; i < insertChildren.getLength(); i++) {
						Node toInsert = insertChildren.item(i);
						if (toInsert.getNodeType() == Node.ELEMENT_NODE) {
							XDiffEditStep replacement = new XDiffEditStep(
								XDiffOperation.INSERT_SUBTREE, new Node[]{delete._args[0], toInsert});
							toAdd.add(replacement);
							updated = true;
						}
					}
					if (updated) {
						toRemove.add(insert);
						toRemove.add(delete);
					}
				}
			}
		}
	}

	private static void reconcileInsertSubtreeDeleteSubtree(
		List<XDiffEditStep> toRemove, List<XDiffEditStep> toAdd, XDiffEditStep insert, XDiffEditStep delete) {
		if (worthReconciling(insert, delete)) {
			//if the delete and the insert are text valued
			NodeList insertChildren = insert._args[1].getChildNodes();
			NodeList deleteChildren = delete._args[0].getChildNodes();
			if (deleteChildren.getLength() == 1 && deleteChildren.item(0).getNodeType() == Node.TEXT_NODE &&
			    insertChildren.getLength() == 1 && insertChildren.item(0).getNodeType() == Node.TEXT_NODE) {
				XDiffEditStep replacement = new XDiffEditStep(
					XDiffOperation.UPDATE_LEAF,
					new Node[]{delete._args[0].getFirstChild(), insert._args[1].getFirstChild()});
				toAdd.add(replacement);
				toRemove.add(insert);
				toRemove.add(delete);
			}
		}
	}

	private static boolean worthReconciling(XDiffEditStep insert, XDiffEditStep delete) {
		//  we're deleting a node from the same node we're inserting into
		//  and the inserted and deleted nodes have the same local name
		//  and the inserted and deleted nodes have the same signature
		String deleteLocalName = delete._args[0].getLocalName();
		String insertLocalName = insert._args[1].getLocalName();

		return delete._args[0].getParentNode() == insert._args[0] && (deleteLocalName == insertLocalName ||
		                                                              (deleteLocalName != null &&
		                                                               deleteLocalName.equals(insertLocalName))) &&
		       XDiffUtil.getSignature(delete._args[0]).equals(XDiffUtil.getSignature(insert._args[1]));
	}
}
