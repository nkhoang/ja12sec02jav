package com.nkhoang.common.util.math;


import org.apache.commons.lang.StringUtils;
import org.apache.taglibs.standard.tag.common.core.RemoveTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 * User: hoangknguyen
 * Date: 7/7/11
 * Time: 10:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class PRNUtil {
	public static List<String> Infix2(String input) {
		System.out.println(input);
		char[] in = input.toCharArray();
		Stack<Character> stack = new Stack<Character>();
		StringBuilder out = new StringBuilder();
		List<String> stack2 = new ArrayList<String>();
		String s = "";
		for (int i = 0; i < in.length; i++) {
			switch (in[i]) {
				case '|':
					while(!stack.empty() && stack.peek() == '&') {
						out.append(' ').append(stack.peek());
						stack2.add(stack.pop() + "");
					}
				case '&':
					out.append(' ');
				case '(':
					stack.push(in[i]);
				case ' ':
					if (s.length() > 0) {
						// System.out.println(s);
						stack2.add(s);
					}
					s = "";
					break;
				case ')':
					while (!stack.empty() && stack.peek() != '(') {
						if (s.length() > 0) {
							stack2.add(s);
							s = "";
						}
						stack2.add(stack.peek().toString());
						out.append(' ').append(stack.pop());
					}
					if (!stack.empty()) stack.pop();
					break;
				default:
					out.append(in[i]);
					s += in[i];
					break;
			}
		}

		if (s.length() > 0) {
			stack2.add(s);
		}
		while (!stack.isEmpty()) {
			stack2.add(stack.peek().toString());
			out.append(' ').append(stack.pop());
		}

		System.out.println("stack2 : " + stack2.toString());
		System.out.println(out.toString());
		return stack2;
	}

	public static boolean checkPRNExpression(List<String> stack) {
		boolean isValid = false;
		int index = 0;
		do {
			String o = stack.get(index);
			if (isOperator(o)) {
				// check position
				int offset = stack.indexOf(o);
				if (offset - 2 >= 0) {
					// just remove from the list
					stack.remove(offset - 1);
					stack.remove(offset - 1);

					System.out.println("----> " +stack.toString());
					index -= 2;
				}
			}
			index++;
		} while (index < stack.size());
		// check the stack size

		if (stack.size() == 1 && !isOperator(stack.get(0))) {
			isValid = true;
		}

		return isValid;
	}


	public static boolean checkPRNExpression(String expression) {
		boolean isValid = false;
		String[] s = expression.trim().split(" ");
		List<String> stack = new ArrayList<String>();
		stack.addAll(Arrays.asList(s));

		int index = 0;
		do {
			String o = stack.get(index);
			if (isOperator(o)) {
				// check position
				int offset = stack.indexOf(o);
				if (offset - 2 >= 0) {
					// just remove from the list
					stack.remove(offset - 1);
					stack.remove(offset - 1);

					System.out.println("----> " +stack.toString());
					index -= 2;
				}
			}
			index++;
		} while (index < stack.size());
		// check the stack size

		if (stack.size() == 1 && !isOperator(stack.get(0))) {
			isValid = true;
		}

		return isValid;
	}

	private static boolean isOperator(String s) {
		return StringUtils.equals("&", s) || StringUtils.equals("|", s);
	}

}
