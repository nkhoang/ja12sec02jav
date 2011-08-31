package com.nkhoang.math;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PRNUtilTest {
	private static final Logger LOG = LoggerFactory.getLogger(PRNUtilTest.class.getCanonicalName());

	@Test
	public void testPRN() {

		String i = " A AND b and C or D Or F";
		i = i.replaceAll("AND|And|and", "&");
		i = i.replaceAll("OR|Or|or", "|");
		LOG.info(i);

		String s = "(Lastname      & FirstName) | City & &";
		String s2 = " Lastname & (FirstName | City City & Name) Firstname &";
		String s3 = "( ( Lastname & FirstName) | City) & DeaNum";
		String s4 = "(Lastname & FirstName) | (City | (DeaNum & Lastname))";
		String s5 = "Lastname      &          FirstName | City";
		String s6 = "((A|B) & (B | (C&D)))";
		String s7 = "(((A | B) & C) | D) | lastname";
		String s8 = "A | B & C";
		String s9 = "A & B | C";
		String s10 = "(A & B) | (B & D) & (C | F)";

		LOG.info(Boolean.toString(PRNUtil.checkPRNExpression(PRNUtil.Infix2(s10))));
		LOG.info(Boolean.toString(PRNUtil.checkPRNExpression(PRNUtil.Infix2(s8))));
		LOG.info(Boolean.toString(PRNUtil.checkPRNExpression(PRNUtil.Infix2(s9))));
		LOG.info(Boolean.toString(PRNUtil.checkPRNExpression(PRNUtil.Infix2(s2))));
		LOG.info(Boolean.toString(PRNUtil.checkPRNExpression(PRNUtil.Infix2(s3))));
		LOG.info(Boolean.toString(PRNUtil.checkPRNExpression(PRNUtil.Infix2(s4))));
	}
}
