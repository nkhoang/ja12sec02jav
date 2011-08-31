package com.nkhoang.common.xml.xdiff;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

class AccumulatorKey {
	private List<Integer> _list1    = new ArrayList<Integer>();
	private List<Integer> _list2    = new ArrayList<Integer>();
	private int           _hashCode = 0;

	public AccumulatorKey(List<Node> list1, List<Node> list2) {
		for (Node t : list1) {
			int val = t.hashCode();
			_list1.add(val);
			_hashCode += val;
		}
		for (Node t : list2) {
			int val = t.hashCode();
			_list2.add(val);
			_hashCode += val;
		}
	}

	public List<Integer> getList1() {
		return _list1;
	}

	public List<Integer> getList2() {
		return _list2;
	}

	@Override
	public int hashCode() {
		return _hashCode;
	}

	@Override
	public boolean equals(Object other) {
		if (getClass() != other.getClass()) {
			return false;
		} else {
			AccumulatorKey otherKey = (AccumulatorKey) other;
			if (_list1.size() == otherKey.getList1().size() && _list2.size() == otherKey.getList2().size()) {
				for (Integer val : _list1) {
					if (!otherKey.getList1().contains(val)) {
						return false;
					}
				}
				for (Integer val : _list2) {
					if (!otherKey.getList2().contains(val)) {
						return false;
					}
				}
			} else {
				return false;
			}
		}
		return true;
	}
}
