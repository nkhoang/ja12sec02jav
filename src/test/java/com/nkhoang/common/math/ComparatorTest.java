package com.nkhoang.common.math;

import org.apache.commons.lang.math.NumberUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: hoangknguyen
 * Date: 6/21/11
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class ComparatorTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ComparatorTest.class.getCanonicalName());
	@Test
	public void testComparator() {
		List<Person> persons = new ArrayList<Person>();
		Person p = new Person(1);
		Person p2 = new Person(3);
		Person p3 = new Person(12);

		persons.add(p3);
		persons.add(p);
		persons.add(p2);

		LOGGER.info(persons.toString());

		Collections.sort(persons, new Comparator<Person>() {
			@Override
			public int compare(Person o1, Person o2) {
				return NumberUtils.compare(o1.getRank(), o2.getRank());
			}
		});
		LOGGER.info(persons.toString());

		persons = persons.subList(0,2);
		LOGGER.info(persons.toString());
	}

	private class Person {
		private int _rank;

		public Person(int rank) {
			_rank = rank;
		}

		public int getRank() {
			return _rank;
		}

		public void setRank(int rank) {
			_rank = rank;
		}

		public String toString(){
			return String.format("P[%s]",_rank);
		}
	}
}
