package com.nkhoang.common.xml.xdiff;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

public enum XDiffOperation {

	DELETE_LEAF {
		public void applyOperation(Node[] args) {
			deleteNode(args[0]);
		}

		public void applyOperationAsMarkup(Node[] args) {
			addDeletedInstruction(args[0]);
		}
	},

	INSERT_LEAF {
		//args are [parent node, node to insert]
		public void applyOperation(Node[] args) {
			insertNode(args[0], args[1]);
		}

		public void applyOperationAsMarkup(Node[] args) {
			Node inserted = insertNode(args[0], args[1]);
			addInsertedInstruction(args[0], inserted);
		}
	},

	UPDATE_LEAF {
		//args are [node to update, node with new value]
		public void applyOperation(Node[] args) {
			updateNode(args[0], args[1]);
		}

		public void applyOperationAsMarkup(Node[] args) {
			String oldVal = null;
			if (args[0].getNodeType() == Node.ATTRIBUTE_NODE && args[0] instanceof Attr) {
				oldVal = ((Attr) args[0]).getValue();
			} else if (args[0].getNodeType() == Node.TEXT_NODE) {
				oldVal = args[0].getTextContent();
			}
			updateNode(args[0], args[1]);
			addUpdatedInstruction(args[0], oldVal);
		}
	},

	DELETE_SUBTREE {
		//args are [node to delete]
		public void applyOperation(Node[] args) {
			deleteNode(args[0]);
		}

		public void applyOperationAsMarkup(Node[] args) {
			addDeletedInstruction(args[0]);
		}
	},

	INSERT_SUBTREE {
		//args are [parent node, node to insert]
		public void applyOperation(Node[] args) {
			insertNode(args[0], args[1]);
		}

		public void applyOperationAsMarkup(Node[] args) {
			Node inserted = insertNode(args[0], args[1]);
			addInsertedInstruction(args[0], inserted);
		}
	};

	public static final  String X_DIFF_TARGET      = "X-Diff";
	private static final String DELETED            = "DELETED";
	private static final String DELETED_ATTRIBUTE  = "DELETED_ATTRIBUTE=";
	private static final String INSERTED           = "INSERTED";
	private static final String INSERTED_ATTRIBUTE = "INSERTED_ATTRIBUTE=";
	private static final String UPDATED            = "UPDATED=";
	private static final String UPDATED_ATTRIBUTE  = "UPDATED_ATTRIBUTE=";

	public abstract void applyOperation(Node[] args);

	public abstract void applyOperationAsMarkup(Node[] args);

	private static void deleteNode(Node toDelete) {
		if (toDelete.getNodeType() == Node.ELEMENT_NODE || toDelete.getNodeType() == Node.TEXT_NODE) {
			toDelete.getParentNode().removeChild(toDelete);
		} else if (toDelete.getNodeType() == Node.ATTRIBUTE_NODE) {
			if (!(toDelete instanceof Attr)) {
				//TODO: shouldn't happen?
			} else {
				((Attr) toDelete).getOwnerElement().removeAttributeNode((Attr) toDelete);
			}
		}
	}

	private static Node insertNode(Node parent, Node child) {
		Document doc = parent.getOwnerDocument();
		Node newNode = child.cloneNode(true);
		doc.adoptNode(newNode);
		if (newNode.getNodeType() == Node.ELEMENT_NODE || newNode.getNodeType() == Node.TEXT_NODE) {
			parent.appendChild(newNode);
		} else if (newNode.getNodeType() == Node.ATTRIBUTE_NODE) {
			if (!(parent instanceof Element)) {
				//TODO: shouldn't happen?
			} else {
				((Element) parent).setAttributeNodeNS((Attr) newNode);
			}
		}
		return newNode;
	}

	private static void updateNode(Node node, Node newNode) {
		node.setNodeValue(newNode.getNodeValue());
	}

	private static void addDeletedInstruction(Node node) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			addProcessingInstruction(node, DELETED);
		} else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
			if (node instanceof Attr) {
				Attr attr = (Attr) node;
				addProcessingInstruction(node, DELETED_ATTRIBUTE + attr.getName());
			} else {
				//TODO: shouldn't happen?
			}
		}
	}

	private static void addInsertedInstruction(Node node, Node inserted) {
		if (inserted.getNodeType() == Node.ELEMENT_NODE) {
			addProcessingInstruction(inserted, INSERTED);
		} else if (inserted.getNodeType() == Node.ATTRIBUTE_NODE) {
			if (inserted instanceof Attr) {
				Attr attr = (Attr) inserted;
				addProcessingInstruction(node, INSERTED_ATTRIBUTE + attr.getName());
			} else {
				//TODO: shouldn't happen?
			}
		}
	}

	private static void addUpdatedInstruction(Node node, String oldVal) {
		if (node.getNodeType() == Node.TEXT_NODE) {
			addProcessingInstruction(node, UPDATED + oldVal);
		} else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
			if (node instanceof Attr) {
				Attr attr = (Attr) node;
				addProcessingInstruction(
					node, UPDATED_ATTRIBUTE + attr.getName() + "," + oldVal);
			} else {
				//TODO: shouldn't happen?
			}
		}
	}

	private static void addProcessingInstruction(Node node, String data) {
		Document doc = node.getOwnerDocument();
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			ProcessingInstruction pi = doc.createProcessingInstruction(
				X_DIFF_TARGET, data);
			node.appendChild(pi);
		} else if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
			if (node instanceof Attr) {
				Attr attr = (Attr) node;
				ProcessingInstruction pi = doc.createProcessingInstruction(X_DIFF_TARGET, data);
				Node parent = attr.getOwnerElement();
				parent.appendChild(pi);
			} else {
				//TODO: shouldn't happen?
			}
		} else if (node.getNodeType() == Node.TEXT_NODE) {
			ProcessingInstruction pi = doc.createProcessingInstruction(X_DIFF_TARGET, data);
			Node parent = node.getParentNode();
			parent.appendChild(pi);
		} else {
			//TODO: shouldn't happen?
		}
	}
}
