package com.nkhoang.common.xml.xdiff;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;

/** A wrapper class for an operation that makes up a step in an edit script */
public class XDiffEditStep {
	XDiffOperation _op;
	Node[]         _args;


	/**
	 * Constructor
	 *
	 * @param op   The operation that this edit step requires
	 * @param args the arguments for this operation
	 */
	public XDiffEditStep(XDiffOperation op, Node... args) {
		_args = args;
		_op = op;
	}


	/** Applies the operation to the original document */
	public void applyOperation() {
		_op.applyOperation(_args);
	}

	/** Applies the operation as markup to the original document */
	public void applyOperationAsMarkup() {
		_op.applyOperationAsMarkup(_args);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(_op + " -> [ ");
		for (Node node : _args) {
			sb.append(
				node + " @ " +
				((Node.ATTRIBUTE_NODE == node.getNodeType()) ? ((Attr) node).getOwnerElement() : node.getParentNode()) +
				", ");
		}
		sb.deleteCharAt(sb.length() - 2);
		sb.append("]");
		return sb.toString();
	}
}
