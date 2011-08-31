package com.nkhoang.common.collections;

import java.io.Serializable;
import java.util.Map;


/**
 * An implementation of <code>StrategyMap.Strategy</code> that maps <code>Class
 * </code> to some other object, and traverses the class hierarchy on <code>
 * get()</code>. This is useful to replace chains of <code>instanceof</code>
 * tests with a function map, like so:
 * <pre>
 *  </pre>
 * The most common reason that you'd want to do this is because you're handed
 * an object that's actually a subclass of your desired class (for example, a
 * Hibernate proxy class). You can't use a simple <code>Class</code> -> <code>
 * Method</code> mapping, because you can't know all the possible subclasses
 * until you actually see them. So you use this strategy, and finds the most
 * specific mapping that applies to your class.
 */
public class ClassHierarchyMapStrategy<MappingType> extends StrategyMap.Strategy<Class, MappingType> implements
	Serializable {
	private final static long serialVersionUID = 1L;

	/**
	 * Determines whether the delegate contains a mapping for the specified
	 * class or one of its superclasses.
	 *
	 * @throws ClassCastException if the passed key is not a <code>Class</code>.
	 */
	@Override
	public boolean containsKey(Map<Class, MappingType> map, Object key) {
		if (map.containsKey(key)) {
			return true;
		}

		Class parent = ((Class) key).getSuperclass();
		return (parent == null) ? false : containsKey(map, parent);
	}


	/**
	 * Retrieves the mapping for the passed class. If no such mapping, returns
	 * the mapping for the most specific superclass of the passed class. If no
	 * mapping for any superclass, returns <code>null</code>.
	 *
	 * @throws ClassCastException if the passed key is not a <code>Class</code>.
	 */
	@Override
	public MappingType get(Map<Class, MappingType> map, Object key) {
		if (map.containsKey(key)) {
			return map.get(key);
		}

		Class parent = ((Class) key).getSuperclass();
		return (parent == null) ? null : get(map, parent);
	}
}
