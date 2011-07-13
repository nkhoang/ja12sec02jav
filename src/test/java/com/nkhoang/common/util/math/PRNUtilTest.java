package com.nkhoang.common.util.math;

import org.junit.Test;

/**
 * Created by IntelliJ IDELastname.
 * User: hoangknguyen
 * Date: 7/7/11
 * Time: 10:16 LastnameM
 * To change this template use File | Settings | File Templates.
 */
public class PRNUtilTest {
	@Test
	public void testPRN() {

		String i = " A AND b and C or D Or F";
		i = i.replaceAll("AND|And|and", "&");
		i = i.replaceAll("OR|Or|or", "|");
		System.out.println(i);

		String s = "(Lastname      & FirstName) | City & &";
		String s2 = " Lastname & (FirstName | City City & Name) Firstname &";
		String s3 = "( ( Lastname & FirstName) | City) & DeaNum";
		String s4 = "(Lastname & FirstName) | (City | (DeaNum & Lastname))";
		String s5 = "Lastname      &          FirstName | City";
		String s6 = "((A|B) & (B | (C&D)))";
		String s7 = "(((A | B) & C) | D) | lastname";
		String s8 = "A | B & C";
		String s9 = "A & B | C";
		String s10 = "(A & B) | (B & D) & (C | F)"                     ;

		System.out.println(PRNUtil.checkPRNExpression(PRNUtil.Infix2(s10)));
		System.out.println(PRNUtil.checkPRNExpression(PRNUtil.Infix2(s8)));
		System.out.println(PRNUtil.checkPRNExpression(PRNUtil.Infix2(s9)));
		//System.out.println(PRNUtil.checkPRNExpression(PRNUtil.Infix2(s2)));
		//System.out.println(PRNUtil.checkPRNExpression(PRNUtil.Infix2(s3)));
		//System.out.println(PRNUtil.checkPRNExpression(PRNUtil.Infix2(s4)));
	}
}
