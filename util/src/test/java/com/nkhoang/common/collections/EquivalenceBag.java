package com.nkhoang.common.collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Collection which keeps track of caller defined equivalences between
 * objects.  No methods on the objects themselves are used
 * (<code>equals</code>, <code>hashCode</code>, <code>compareTo</code>,
 * etc. are ignored on the given objects).  The only thing that relates two
 * objects is a call to {@link #addEquivalence} with the given objects.  The
 * general property of this bag is:
 * <pre>
 * if:
 *   (A eqv B) and (B eqv C)
 * then:
 *   getEquivalence(A) -> List(A, B, C)
 *   getEquivalence(B) -> List(A, B, C)
 *   getEquivalence(C) -> List(A, B, C)
 * </pre>
 * <p/>
 * <p/>
 * Additionally, this bag keeps track of order of addition of objects, and all
 * get methods return objects in order of addition, earliest to latest.  So:
 * <pre>
 * if:
 *   addEquivalence(A, B);
 *   addEquivalence(B, C);
 * then:
 *   getEquivalence(A) -> List(A, B, C);
 *
 * if:
 *   addEquivalence(C, B);
 *   addEquivalence(A, B);
 * then:
 *   getEquivalence(A) -> List(C, B, A);
 * </pre>
 */
public class EquivalenceBag<ObjType> {

	/** the next earliest occurrence value to use */
	private Map<Holder<ObjType>, Holder<ObjType>> _equivalences = new LinkedHashMap<Holder<ObjType>, Holder<ObjType>>();

	/**
	 * Adds an equivalence between the given objects.  No methods on the objects
	 * themselves are used (<code>equals</code>, <code>hashCode</code>,
	 * <code>compareTo</code>, etc. are ignored on the given objects).
	 *
	 * @param obj1 object which is equivalent to obj2
	 * @param obj2 object which is equivalent to obj1
	 */
	public void addEquivalence(ObjType obj1, ObjType obj2) {
		if ((obj1 == null) || (obj2 == null)) {
			throw new IllegalArgumentException("Object may not be null");
		}

		Holder<ObjType> eqH1 = getHolder(obj1);
		if (obj1 != obj2) {
			Holder<ObjType> eqH2 = getHolder(obj2);
			eqH1.merge(eqH2);
		}
	}

	/** Gets the Holder for the given object, creating if necessary. */
	private Holder<ObjType> getHolder(ObjType obj) {
		Holder<ObjType> eqH = new Holder<ObjType>(_equivalences.size(), obj);
		Holder<ObjType> tmpEqH = _equivalences.get(eqH);
		if (tmpEqH != null) {
			eqH = tmpEqH;
		} else {
			eqH.init();
			_equivalences.put(eqH, eqH);
		}
		return eqH;
	}

	/**
	 * Returns the equivalence list for the given object.  The objects in the
	 * list will be ordered based on their addition to the bag.
	 *
	 * @param obj object for which to retrieve an equivalence.  Note, the
	 *            equivalence will only be found for this object if <i>this
	 *            actual instance</i> was added to the bag (all
	 *            <code>equals</code> implementations are ignored)
	 *
	 * @return the equivalence for the given object.  Will always return a list
	 *         with at least one element (the given object)
	 */
	public List<ObjType> getEquivalence(ObjType obj) {
		Holder<ObjType> eqH = new Holder<ObjType>(0, obj);
		Holder<ObjType> tmpEqH = _equivalences.get(eqH);
		if (tmpEqH != null) {
			eqH = tmpEqH;
		}
		return eqH.toEquivalence();
	}

	/**
	 * Returns all equivalences in this bag.  The objects in each list will be
	 * ordered based on their addition to the bag, and the lists of equivalences
	 * will be ordered based on the "earliest" object in each equivalence.
	 * Returned lists should not be modified.
	 *
	 * @return all equivalences in this bag
	 */
	public List<List<ObjType>> getEquivalences() {
		// find all the "head" elements.  no need to sort because we use a
		// LinkedHashMap (so elements should be returned from the iterator based
		// on order of addition to the set)
		List<List<ObjType>> eqList = new LinkedList<List<ObjType>>();
		for (Holder<ObjType> eqH : _equivalences.keySet()) {
			if (eqH.isHead()) {
				eqList.add(eqH.toEquivalence());
			}
		}
		return eqList;
	}

	/** Clears all existing equivalences. */
	public void clear() {
		_equivalences.clear();
	}

	/**
	 * comparator for sorting Holders in ascending order by
	 * _earliestOccurence
	 */
	private static final Comparator<Holder> HOLDER_OCCURENCE_COMPARATOR = new Comparator<Holder>() {
		public int compare(Holder obj1, Holder obj2) {
			return ((obj1._earliestOccurence < obj2._earliestOccurence) ? -1 : ((obj1._earliestOccurence >
			                                                                     obj2._earliestOccurence) ? 1 : 0));
		}
	};

	/**
	 * Holder for an object which uses the instance identity of the object for
	 * equals/hashMap implementations.  An equivalence is essentially an
	 * unordered linked list of Holder objects.
	 */
	private static class Holder<HObjType> extends IdentityKey<HObjType> {
		private static final long serialVersionUID = 1L;

		private int                         _earliestOccurence;
		private SortedSet<Holder<HObjType>> _equivalence;

		/**
		 * Initializes a new Holder
		 *
		 * @param earliestOccurence
		 * @param obj
		 */
		public Holder(int earliestOccurence, HObjType obj) {
			super(obj);
			_earliestOccurence = earliestOccurence;
		}

		/** initializes the equivalence for this holder */
		public void init() {
			_equivalence = new TreeSet<Holder<HObjType>>(HOLDER_OCCURENCE_COMPARATOR);
			_equivalence.add(this);
		}

		/**
		 * @return <code>true</code> if this object is the first object in its
		 *         equivalence
		 */
		public boolean isHead() {
			// are we the head of our equivalence?
			return (this == _equivalence.first());
		}

		/** @return a list of the equivalence for this object */
		public List<HObjType> toEquivalence() {
			if ((_equivalence == null) || (_equivalence.size() == 1)) {
				return Collections.singletonList(get());
			}

			// convert ordered set to a list for return to caller
			List<HObjType> eqs = new ArrayList<HObjType>(_equivalence.size());
			for (Holder<HObjType> eqH : _equivalence) {
				eqs.add(eqH.get());
			}
			return eqs;
		}

		/**
		 * connects two equivalences if not already connected
		 *
		 * @param otherH The Holder to connect
		 */
		public void merge(Holder<HObjType> otherH) {

			if (_equivalence == otherH._equivalence) {
				// nothing to do, already equivalent
				return;
			}

			SortedSet<Holder<HObjType>> smallSet = null;
			SortedSet<Holder<HObjType>> largeSet = null;
			if (_equivalence.size() < otherH._equivalence.size()) {
				smallSet = _equivalence;
				largeSet = otherH._equivalence;
			} else {
				smallSet = otherH._equivalence;
				largeSet = _equivalence;
			}

			// copy smallSet into largeSet, and update all smallSet objects
			largeSet.addAll(smallSet);

			for (Holder<HObjType> eqH : smallSet) {
				eqH._equivalence = largeSet;
			}
		}

	}

}
