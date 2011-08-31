package com.nkhoang.common.xml;

import java.util.ArrayList;

import org.apache.xerces.xs.StringList;

/** StringList implementation built from an ArrayList */
public class ListStringList extends ArrayList implements StringList {

	private static final long serialVersionUID = 8803884981572296262L;

	public int getLength() {
		return size();
	}

	public boolean contains(String item) {
		return contains((Object) item);
	}

	public String item(int index) {
		return (String) get(index);
	}

}
