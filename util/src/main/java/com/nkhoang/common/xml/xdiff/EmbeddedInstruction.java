package com.nkhoang.common.xml.xdiff;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

enum EmbeddedInstruction {
	DELETED {
		public void apply(ProcessingInstruction pi) {
			INSERTED.remove(pi);
		}

		public void remove(ProcessingInstruction pi) {
			INSERTED.apply(pi);
		}
	},
	DELETED_ATTRIBUTE {
		public void apply(ProcessingInstruction pi) {
			INSERTED_ATTRIBUTE.remove(pi);
		}

		public void remove(ProcessingInstruction pi) {
			INSERTED.apply(pi);
		}
	},
	INSERTED {
		public void apply(ProcessingInstruction pi) {
			//simple, just strip out the instruction
			Node affected = pi.getParentNode();
			affected.removeChild(pi);
		}

		public void remove(ProcessingInstruction pi) {
			//strip out the inserted node
			Node inserted = pi.getParentNode();
			Node affected = inserted.getParentNode();
			affected.removeChild(inserted);
			//the instruction gets stripped out for us for free
			//since it's inside the deleted node
		}
	},
	INSERTED_ATTRIBUTE {
		public void apply(ProcessingInstruction pi) {
			INSERTED.apply(pi);
		}

		public void remove(ProcessingInstruction pi) {
			//strip out the inserted attribute
			Element affected = (Element) pi.getParentNode();
			String[] splitData = pi.getData().split("=");
			affected.removeAttribute(splitData[1]);
			//and strip out the instruction
			affected.removeChild(pi);
		}
	},
	UPDATED {
		public void apply(ProcessingInstruction pi) {
			INSERTED.apply(pi);
		}

		public void remove(ProcessingInstruction pi) {
			//restore the updated node, which must be a leaf
			Node updated = pi.getParentNode();
			String[] splitData = pi.getData().split("=");
			updated.setTextContent(splitData[1]);
			//and strip out the instruction
			updated.removeChild(pi);
		}
	},
	UPDATED_ATTRIBUTE {
		public void apply(ProcessingInstruction pi) {
			INSERTED.apply(pi);
		}

		public void remove(ProcessingInstruction pi) {
			//restore the updated node, which must be a leaf
			Element updated = (Element) pi.getParentNode();
			String[] splitData = pi.getData().split("=");
			String[] splitArgs = splitData[1].split("'");
			updated.setAttribute(splitArgs[0], splitArgs[1]);
			//and strip out the instruction
			updated.removeChild(pi);
		}
	};

	public static EmbeddedInstruction getBaseInstruction(String instructionData) {
		String[] values = instructionData.split("=");
		return EmbeddedInstruction.valueOf(values[0]);
	}

	public static void removeProcessingInstruction(ProcessingInstruction pi) {
		String data = pi.getData();
		EmbeddedInstruction instruction = EmbeddedInstruction.getBaseInstruction(data);
		instruction.remove(pi);
	}

	public static void applyProcessingInstruction(ProcessingInstruction pi) {
		String data = pi.getData();
		EmbeddedInstruction instruction = EmbeddedInstruction.getBaseInstruction(data);
		instruction.apply(pi);
	}

	public abstract void apply(ProcessingInstruction pi);

	public abstract void remove(ProcessingInstruction pi);
}
