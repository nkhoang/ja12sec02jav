package com.nkhoang.gae.utils.math;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.List;

public class Fmt {
	/**
	 */
	public static final Object Null = new NoArgument();

	/**
	 */
	public static final Object[] NoMore = new Object[0];


	/** Don't want a hidden constructor. */
	private Fmt() {
	}


	private static final int StartST             = 1;
	private static final int GetPadCharST        = 2;
	private static final int GetPreNumberST      = 3;
	private static final int DonePreST           = 4;
	private static final int GetPostNumberST     = 5;
	private static final int DonePostST          = 6;
	private static final int CollectNumberST     = 7;
	private static final int ConsumeDotST        = 8;
	private static final int GetFinalCharacterST = 9;
	private static final int UnexpectedEndST     = 10;
	private static final int PrintNextObjectST   = 11;

	private static final ThreadLocal bufferStorage = new ThreadLocal();

	/**
	 */
	public static FormatBuffer getBuffer() {
		FormatBuffer fmb = (FormatBuffer) bufferStorage.get();
		return (fmb == null) ? new FormatBuffer() : fmb;
	}

	private static final int BufSize      = FormatBuffer.DefaultBufferSize;
	private static final int BufSizeLimit = FormatBuffer.ReportThreshold;

	/**

	 */
	public static void freeBuffer(FormatBuffer buf) {
		buf.truncateToLength(0);
		if (buf.getBuffer().length > BufSizeLimit) {
			buf.setCapacity(BufSize);
		}
		bufferStorage.set(buf);
	}

	// ----------------------------------------------------------------
	// Format into a string and return it

	/**
	 * Format a message into a String.
	 *
	 * @param control a String defining the format of the output
	 *
	 * @return the formatted String
	 */
	public static String S(String control) {
		return S(control, 0, Null, Null, Null, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into a String.
	 *
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 *
	 * @return the formatted String
	 */
	public static String S(String control, Object a1) {
		return S(control, 0, a1, Null, Null, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into a String.
	 *
	 * @param control a String defining the format of the output
	 * @param i1      an integer argument to the format string
	 *                <b>control</b>
	 *
	 * @return the formatted String
	 */
	public static String S(String control, int i1) {
		return S(
			control, 0, Constants.getInteger(i1), Null, Null, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into a String.
	 *
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 *
	 * @return the formatted String
	 */
	public static String S(String control, Object a1, Object a2) {
		// Optimize a common, simple case, even though people should know
		// to call concat for this.
		if (control == "%s%s" && a1 instanceof String && a2 instanceof String) {
			return ((String) a1).concat((String) a2);
		}
		return S(control, 0, a1, a2, Null, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into a String.
	 *
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 *
	 * @return the formatted String
	 */
	public static String S(String control, Object a1, Object a2, Object a3) {
		return S(control, 0, a1, a2, a3, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into a String.
	 *
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 * @param a4      the fourth argument to the format string
	 *                <b>control</b>
	 *
	 * @return the formatted String
	 */
	public static String S(
		String control, Object a1, Object a2, Object a3, Object a4) {
		return S(control, 0, a1, a2, a3, a4, Null, Null, NoMore);
	}

	/**
	 * Format a message into a String.
	 *
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 * @param a4      the fourth argument to the format string
	 *                <b>control</b>
	 * @param a5      the fifth argument to the format string
	 *                <b>control</b>
	 *
	 * @return the formatted String
	 */
	public static String S(
		String control, Object a1, Object a2, Object a3, Object a4, Object a5) {
		return S(control, 0, a1, a2, a3, a4, a5, Null, NoMore);
	}

	/**
	 * Format a message into a String.
	 *
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 * @param a4      the fourth argument to the format string
	 *                <b>control</b>
	 * @param a5      the fifth argument to the format string
	 *                <b>control</b>
	 * @param a6      the sixth argument to the format string
	 *                <b>control</b>
	 *
	 * @return the formatted String
	 */
	public static String S(
		String control, Object a1, Object a2, Object a3, Object a4, Object a5, Object a6) {
		return S(control, 0, a1, a2, a3, a4, a5, a6, NoMore);
	}

	/**
	 * Format a message into a String.
	 *
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 * @param a4      the fourth argument to the format string
	 *                <b>control</b>
	 * @param a5      the fifth argument to the format string
	 *                <b>control</b>
	 * @param a6      the sixth argument to the format string
	 *                <b>control</b>
	 * @param more    an array containing all subsequent arguments to
	 *                the format string <b>control</b> after the sixth one.
	 *
	 * @return the formatted String
	 *
	 * @deprecated
	 */
	public static String S(
		String control, Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object[] more) {
		return S(control, 0, a1, a2, a3, a4, a5, a6, more);
	}

	/**
	 * Format a message into a String.
	 *
	 * @param control a String defining the format of the output
	 * @param args    an array containing all arguments to the format
	 *                string <b>control</b>
	 *
	 * @return the formatted String
	 */
	public static String S(String control, Object[] args) {
		return S(control, 6, Null, Null, Null, Null, Null, Null, args);
	}

	private static String S(
		String control, int firstArg, Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object[] more) {
		FormatBuffer buf = getBuffer();
		B(buf, control, firstArg, a1, a2, a3, a4, a5, a6, more);
		String result = buf.toString();
		freeBuffer(buf);
		return result;
	}

	// ----------------------------------------------------------------
	// Format into an writer


	/**
	 * Format a message into a PrintWriter.
	 *
	 * @param out     a PrintWriter to format the output into
	 * @param control a String defining the format of the output
	 */
	public static void F(PrintWriter out, String control) {
		F(out, control, 0, Null, Null, Null, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into a PrintWriter.
	 *
	 * @param out     a PrintWriter to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 */
	public static void F(PrintWriter out, String control, Object a1) {
		F(out, control, 0, a1, Null, Null, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into a PrintWriter.
	 *
	 * @param out     a PrintWriter to format the output into
	 * @param control a String defining the format of the output
	 * @param i1      an integer argument to the format string
	 *                <b>control</b>
	 */
	public static void F(PrintWriter out, String control, int i1) {
		F(
			out, control, 0, Constants.getInteger(i1), Null, Null, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into a PrintWriter.
	 *
	 * @param out     a PrintWriter to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 */
	public static void F(
		PrintWriter out, String control, Object a1, Object a2) {
		F(out, control, 0, a1, a2, Null, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into a PrintWriter.
	 *
	 * @param out     a PrintWriter to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 */
	public static void F(
		PrintWriter out, String control, Object a1, Object a2, Object a3) {
		F(out, control, 0, a1, a2, a3, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into a PrintWriter.
	 *
	 * @param out     a PrintWriter to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 * @param a4      the fourth argument to the format string
	 *                <b>control</b>
	 */
	public static void F(
		PrintWriter out, String control, Object a1, Object a2, Object a3, Object a4) {
		F(out, control, 0, a1, a2, a3, a4, Null, Null, NoMore);
	}

	/**
	 * Format a message into a PrintWriter.
	 *
	 * @param out     a PrintWriter to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 * @param a4      the fourth argument to the format string
	 *                <b>control</b>
	 * @param a5      the fifth argument to the format string
	 *                <b>control</b>
	 */
	public static void F(
		PrintWriter out, String control, Object a1, Object a2, Object a3, Object a4, Object a5) {
		F(out, control, 0, a1, a2, a3, a4, a5, Null, NoMore);
	}

	/**
	 * Format a message into a PrintWriter.
	 *
	 * @param out     a PrintWriter to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 * @param a4      the fourth argument to the format string
	 *                <b>control</b>
	 * @param a5      the fifth argument to the format string
	 *                <b>control</b>
	 * @param a6      the sixth argument to the format string
	 *                <b>control</b>
	 */
	public static void F(
		PrintWriter out, String control, Object a1, Object a2, Object a3, Object a4, Object a5, Object a6) {
		F(out, control, 0, a1, a2, a3, a4, a5, a6, NoMore);
	}

	/**
	 * Format a message into a PrintWriter.
	 *
	 * @param out     a PrintWriter to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 * @param a4      the fourth argument to the format string
	 *                <b>control</b>
	 * @param a5      the fifth argument to the format string
	 *                <b>control</b>
	 * @param a6      the sixth argument to the format string
	 *                <b>control</b>
	 * @param more    an array containing all subsequent arguments to
	 *                the format string <b>control</b> after the sixth one.
	 *
	 * @deprecated
	 */
	public static void F(
		PrintWriter out, String control, Object a1, Object a2, Object a3, Object a4, Object a5, Object a6,
		Object[] more) {
		F(out, control, 0, a1, a2, a3, a4, a5, a6, more);
	}

	/**
	 * Format a message into a PrintWriter.
	 *
	 * @param out     a PrintWriter to format the output into
	 * @param control a String defining the format of the output
	 * @param args    an array containing all arguments to the format
	 *                string <b>control</b>
	 */
	public static void F(PrintWriter out, String control, Object[] args) {
		F(out, control, 6, Null, Null, Null, Null, Null, Null, args);
	}

	private static void F(
		PrintWriter out, String control, int firstArg, Object a1, Object a2, Object a3, Object a4, Object a5, Object a6,
		Object[] more) {
		FormatBuffer buf = getBuffer();
		B(buf, control, firstArg, a1, a2, a3, a4, a5, a6, more);
		buf.print(out);
		freeBuffer(buf);
	}

	// ----------------------------------------------------------------
	// Format into an OutputStream


	/**
	 * Format a message into an OutputStream.
	 *
	 * @param out     an OutputStream to format the output into
	 * @param control a String defining the format of the output
	 *
	 * @throws IOException if there is a problem writing to the
	 *                     OutputStream.
	 */
	public static void O(OutputStream out, String control) throws IOException {
		O(out, control, 0, Null, Null, Null, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into an OutputStream.
	 *
	 * @param out     an OutputStream to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 *
	 * @throws IOException if there is a problem writing to the
	 *                     OutputStream.
	 */
	public static void O(OutputStream out, String control, Object a1) throws IOException {
		O(out, control, 0, a1, Null, Null, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into an OutputStream.
	 *
	 * @param out     an OutputStream to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 *
	 * @throws IOException if there is a problem writing to the
	 *                     OutputStream.
	 */
	public static void O(
		OutputStream out, String control, Object a1, Object a2) throws IOException {
		O(out, control, 0, a1, a2, Null, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into an OutputStream.
	 *
	 * @param out     an OutputStream to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 *
	 * @throws IOException if there is a problem writing to the
	 *                     OutputStream.
	 */
	public static void O(
		OutputStream out, String control, Object a1, Object a2, Object a3) throws IOException {
		O(out, control, 0, a1, a2, a3, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into an OutputStream.
	 *
	 * @param out     an OutputStream to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 * @param a4      the fourth argument to the format string
	 *                <b>control</b>
	 *
	 * @throws IOException if there is a problem writing to the
	 *                     OutputStream.
	 */
	public static void O(
		OutputStream out, String control, Object a1, Object a2, Object a3, Object a4) throws IOException {
		O(out, control, 0, a1, a2, a3, a4, Null, Null, NoMore);
	}

	/**
	 * Format a message into an OutputStream.
	 *
	 * @param out     an OutputStream to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 * @param a4      the fourth argument to the format string
	 *                <b>control</b>
	 * @param a5      the fifth argument to the format string
	 *                <b>control</b>
	 *
	 * @throws IOException if there is a problem writing to the
	 *                     OutputStream.
	 */
	public static void O(
		OutputStream out, String control, Object a1, Object a2, Object a3, Object a4, Object a5) throws IOException {
		O(out, control, 0, a1, a2, a3, a4, a5, Null, NoMore);
	}

	/**
	 * Format a message into an OutputStream.
	 *
	 * @param out     an OutputStream to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 * @param a4      the fourth argument to the format string
	 *                <b>control</b>
	 * @param a5      the fifth argument to the format string
	 *                <b>control</b>
	 * @param a6      the sixth argument to the format string
	 *                <b>control</b>
	 *
	 * @throws IOException if there is a problem writing to the
	 *                     OutputStream.
	 */
	public static void O(
		OutputStream out, String control, Object a1, Object a2, Object a3, Object a4, Object a5,
		Object a6) throws IOException {
		O(out, control, 0, a1, a2, a3, a4, a5, a6, NoMore);
	}

	/**
	 * Format a message into an OutputStream.
	 *
	 * @param out     an OutputStream to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 * @param a4      the fourth argument to the format string
	 *                <b>control</b>
	 * @param a5      the fifth argument to the format string
	 *                <b>control</b>
	 * @param a6      the sixth argument to the format string
	 *                <b>control</b>
	 * @param more    an array containing all subsequent arguments to
	 *                the format string <b>control</b> after the sixth one.
	 *
	 * @throws IOException if there is a problem writing to the
	 *                     OutputStream.
	 * @deprecated
	 */
	public static void O(
		OutputStream out, String control, Object a1, Object a2, Object a3, Object a4, Object a5, Object a6,
		Object[] more) throws IOException {
		O(out, control, 0, a1, a2, a3, a4, a5, a6, more);
	}

	/**
	 * Format a message into an OutputStream.
	 *
	 * @param out     an OutputStream to format the output into
	 * @param control a String defining the format of the output
	 * @param args    an array containing all arguments to
	 *                the format string <b>control</b>
	 *
	 * @throws IOException if there is a problem writing to the
	 *                     OutputStream.
	 */
	public static void O(OutputStream out, String control, Object[] args) throws IOException {
		O(out, control, 6, Null, Null, Null, Null, Null, Null, args);
	}

	private static void O(
		OutputStream out, String control, int firstArg, Object a1, Object a2, Object a3, Object a4, Object a5,
		Object a6, Object[] more) throws IOException {
		FormatBuffer buf = getBuffer();
		B(buf, control, firstArg, a1, a2, a3, a4, a5, a6, more);
		buf.print(out);
		freeBuffer(buf);
	}

	// ----------------------------------------------------------------
	// Format into a given buffer

	/**
	 * Format a message into a FormatBuffer.
	 *
	 * @param buf     a FormatBuffer to format the output into
	 * @param control a String defining the format of the output
	 */
	public static void B(FormatBuffer buf, String control) {
		B(buf, control, 0, Null, Null, Null, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into a FormatBuffer.
	 *
	 * @param buf     a FormatBuffer to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 */
	public static void B(FormatBuffer buf, String control, Object a1) {
		B(buf, control, 0, a1, Null, Null, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into a FormatBuffer.
	 *
	 * @param buf     a FormatBuffer to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 */
	public static void B(
		FormatBuffer buf, String control, Object a1, Object a2) {
		B(buf, control, 0, a1, a2, Null, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into a FormatBuffer.
	 *
	 * @param buf     a FormatBuffer to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 */
	public static void B(
		FormatBuffer buf, String control, Object a1, Object a2, Object a3) {
		B(buf, control, 0, a1, a2, a3, Null, Null, Null, NoMore);
	}

	/**
	 * Format a message into a FormatBuffer.
	 *
	 * @param buf     a FormatBuffer to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 * @param a4      the fourth argument to the format string
	 *                <b>control</b>
	 */
	public static void B(
		FormatBuffer buf, String control, Object a1, Object a2, Object a3, Object a4) {
		B(buf, control, 0, a1, a2, a3, a4, Null, Null, NoMore);
	}

	/**
	 * Format a message into a FormatBuffer.
	 *
	 * @param buf     a FormatBuffer to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 * @param a4      the fourth argument to the format string
	 *                <b>control</b>
	 * @param a5      the fifth argument to the format string
	 *                <b>control</b>
	 */
	public static void B(
		FormatBuffer buf, String control, Object a1, Object a2, Object a3, Object a4, Object a5) {
		B(buf, control, 0, a1, a2, a3, a4, a5, Null, NoMore);
	}

	/**
	 * Format a message into a FormatBuffer.
	 *
	 * @param buf     a FormatBuffer to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 * @param a4      the fourth argument to the format string
	 *                <b>control</b>
	 * @param a5      the fifth argument to the format string
	 *                <b>control</b>
	 * @param a6      the sixth argument to the format string
	 *                <b>control</b>
	 */
	public static void B(
		FormatBuffer buf, String control, Object a1, Object a2, Object a3, Object a4, Object a5, Object a6) {
		B(buf, control, 0, a1, a2, a3, a4, a5, a6, NoMore);
	}

	/**
	 * Format a message into a FormatBuffer.
	 *
	 * @param buf     a FormatBuffer to format the output into
	 * @param control a String defining the format of the output
	 * @param a1      the first argument to the format string
	 *                <b>control</b>
	 * @param a2      the second argument to the format string
	 *                <b>control</b>
	 * @param a3      the third argument to the format string
	 *                <b>control</b>
	 * @param a4      the fourth argument to the format string
	 *                <b>control</b>
	 * @param a5      the fifth argument to the format string
	 *                <b>control</b>
	 * @param a6      the sixth argument to the format string
	 *                <b>control</b>
	 * @param more    an array containing all subsequent arguments to
	 *                the format string <b>control</b> after the sixth one.
	 *
	 * @deprecated
	 */
	public static void B(
		FormatBuffer buf, String control, Object a1, Object a2, Object a3, Object a4, Object a5, Object a6,
		Object[] more) {
		B(buf, control, 0, a1, a2, a3, a4, a5, a6, more);
	}

	/**
	 * Format a message into a FormatBuffer.
	 *
	 * @param buf     a FormatBuffer to format the output into
	 * @param control a String defining the format of the output
	 * @param args    an array containing all arguments to the format
	 *                string <b>control</b> after the sixth one
	 */
	public static void B(FormatBuffer buf, String control, Object[] args) {
		B(buf, control, 6, Null, Null, Null, Null, Null, Null, args);
	}

	// ----------------------------------------------------------------
	// The master formatter.

	private static void B(
		FormatBuffer buf, String control, int whichArg, Object a1, Object a2, Object a3, Object a4, Object a5,
		Object a6, Object[] more) {
		int controlLength = control.length();
		int controlPos = 0;

		while (controlPos < controlLength) {
			char c = control.charAt(controlPos++);

			// Copy simple character in control string to output
			if (c != '%') {
				buf.append(c);
			}

			// Parse a printing directive of the form "%[-][min][.[max]]s"
			else {
				int state = StartST;
				int nextStateAfterCollectNumber = -1;

				// Set to true if the field spec starts with '-'
				boolean isLeftJustified = false;

				// Set to true if the control string has a zero
				// directly following the '-' or '+' in the field spec.
				boolean padWithZeros = false;

				// The minimum field width.
				int pre = -1;

				// The maximum number of characters to print.
				int post = -1;

				// Used by the state machine to collect
				// the numbers in the field spec.
				int accumulator = 0;

				DoOneControlLoop:
				while (true) {

					// Get the next character
					if (controlPos < controlLength) {
						c = control.charAt(controlPos);
					} else {
						state = UnexpectedEndST;
					}

					switch (state) {
						case StartST:
							if (c == 's') {
								state = PrintNextObjectST;
							} else if (c == '-') {
								isLeftJustified = true;
								state = GetPadCharST;
								controlPos++;
							} else if (c == '+') {
								isLeftJustified = false;
								state = GetPadCharST;
								controlPos++;
							} else if (c == '0') {
								state = GetPadCharST;
							} else if (c == '%') {
								controlPos++;
								buf.append(c);
								break DoOneControlLoop;
							} else if (c == '/') {
								controlPos++;
								buf.append(File.separator);
								break DoOneControlLoop;
							} else {
								state = GetPreNumberST;
							}
							break;

						case GetPadCharST:
							if (c == '0') {
								padWithZeros = true;
								controlPos++;
							}
							state = GetPreNumberST;
							break;

						case GetPreNumberST:
							if (c >= '0' && c <= '9') {
								accumulator = c - '0';
								state = CollectNumberST;
								nextStateAfterCollectNumber = DonePreST;
								controlPos++;
							} else {
								state = ConsumeDotST;
							}
							break;

						case DonePreST:
							pre = accumulator;
							state = ConsumeDotST;
							break;

						case GetPostNumberST:
							if (c >= '0' && c <= '9') {
								accumulator = c - '0';
								state = CollectNumberST;
								nextStateAfterCollectNumber = DonePostST;
								controlPos++;
							} else {
								state = GetFinalCharacterST;
							}
							break;

						case DonePostST:
							state = GetFinalCharacterST;
							post = accumulator;
							break;

						case CollectNumberST:
							if (c >= '0' && c <= '9') {
								accumulator = (accumulator * 10) + (c - '0');
								controlPos++;
							} else {
								state = nextStateAfterCollectNumber;
							}
							break;

						case ConsumeDotST:
							if (c == '.') {
								state = GetPostNumberST;
								controlPos++;
							} else {
								state = GetFinalCharacterST;
							}
							break;

						case GetFinalCharacterST:
							controlPos++;

							// print object using column controls
							if (c == 's') {
								Object arg;
								switch (whichArg++) {
									case 0:
										arg = a1;
										break;
									case 1:
										arg = a2;
										break;
									case 2:
										arg = a3;
										break;
									case 3:
										arg = a4;
										break;
									case 4:
										arg = a5;
										break;
									case 5:
										arg = a6;
										break;
									default:
										int i = whichArg - 7;
										if (i >= more.length) {
											arg = "(too many controls)";
										} else {
											arg = more[i];
										}
								}

								String argAsString = String.valueOf(arg);
								int len = argAsString.length();

								// Set min and max to sensible values.
								if (post == -1) {
									post = Math.max(len, pre);
								}
								if (pre == -1) {
									pre = 0;
								}

								// Determine ammount of pad
								int pad = 0;
								if (len < post && len < pre) {
									pad = pre - len;
								}

								// Write left side padding.
								if (!isLeftJustified) {
									for (int i = 0; i < pad; i++) {
										buf.append(padWithZeros ? '0' : ' ');
									}
								}

								// Write subject, truncating if necessary.
								if (len <= post) {
									buf.append(argAsString);
								} else {
									int startPosition = isLeftJustified ? 0 : len - post;
									for (int j = 0; j < post; j++) {
										char subject = argAsString.charAt(startPosition + j);
										buf.append(subject);
									}
								}

								// Write right-side padding.
								if (isLeftJustified) {
									for (int i = 0; i < pad; i++) {
										buf.append(padWithZeros ? '0' : ' ');
									}
								}
							}

							// Not a recognized printing control code.
							else {
								buf.append("(Fmt: unknown control character)");
							}

							break DoOneControlLoop;

						// Print the next argument
						case PrintNextObjectST:
							controlPos++;
							Object arg;
							switch (whichArg++) {
								case 0:
									arg = a1;
									break;
								case 1:
									arg = a2;
									break;
								case 2:
									arg = a3;
									break;
								case 3:
									arg = a4;
									break;
								case 4:
									arg = a5;
									break;
								case 5:
									arg = a6;
									break;
								default:
									int i = whichArg - 7;
									if (i >= more.length) {
										arg = "(too many controls)";
									} else {
										arg = more[i];
									}
							}

							// Handle char arrays so they print their contents.
							if (arg instanceof char[]) {
								buf.append((char[]) arg);
							} else {
								buf.append(arg == null ? "null" : arg.toString());
							}

							break DoOneControlLoop;

						// Reached end of control string too soon.
						case UnexpectedEndST:
							buf.append("(Fmt: unexpected end of control sequence)");
							break DoOneControlLoop;

					}
				}
			}
		}
	}

	private static Object[] argsArray(
		Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object[] more) {
		Object[] args;
		//number of other args = 6
		if (more != null && more.length > 0) {
			args = new Object[6 + more.length];
			System.arraycopy(more, 0, args, 6, more.length);
		} else {
			args = new Object[6];
		}
		args[0] = a1;
		args[1] = a2;
		args[2] = a3;
		args[3] = a4;
		args[4] = a5;
		args[5] = a6;
		return args;
	}

	/*-----------------------------------------------------------------------
			Utility methods direct the calls to MessageFormat#format
		  -----------------------------------------------------------------------*/

	/**
	 * An internationalized version of Fmt.S uses the java.text.MessageFormat
	 * style control string.
	 *
	 * @param control MessageFormat style pattern
	 * @param a1      object to be substituted
	 *
	 * @return pattern with formatted objects.
	 */
	public static String Si(String control, Object a1) {
		return Si(control, a1, null, null, null, null, null, null);
	}

	/**
	 * An internationalized version of Fmt.S uses the java.text.MessageFormat
	 * style control string.
	 *
	 * @param control MessageFormat style pattern
	 * @param a1      object to be substituted
	 * @param a2      object to be substituted
	 *
	 * @return pattern with formatted objects.
	 */
	public static String Si(String control, Object a1, Object a2) {
		return Si(control, a1, a2, null, null, null, null, null);
	}

	/**
	 * An internationalized version of Fmt.S uses the java.text.MessageFormat
	 * style control string.
	 *
	 * @param control MessageFormat style pattern
	 * @param a1      object to be substituted
	 * @param a2      object to be substituted
	 * @param a3      object to be substituted
	 *
	 * @return pattern with formatted objects.
	 */
	public static String Si(String control, Object a1, Object a2, Object a3) {
		return Si(control, a1, a2, a3, null, null, null, null);
	}

	/**
	 * An internationalized version of Fmt.S uses the java.text.MessageFormat
	 * style control string.
	 *
	 * @param control MessageFormat style pattern
	 * @param a1      object to be substituted
	 * @param a2      object to be substituted
	 * @param a3      object to be substituted
	 * @param a4      object to be substituted
	 *
	 * @return pattern with formatted objects.
	 */
	public static String Si(
		String control, Object a1, Object a2, Object a3, Object a4) {
		return Si(control, a1, a2, a3, a4, null, null, null);
	}

	/**
	 * An internationalized version of Fmt.S uses the java.text.MessageFormat
	 * style control string.
	 *
	 * @param control MessageFormat style pattern
	 * @param a1      object to be substituted
	 * @param a2      object to be substituted
	 * @param a3      object to be substituted
	 * @param a4      object to be substituted
	 * @param a5      object to be substituted
	 *
	 * @return pattern with formatted objects.
	 */
	public static String Si(
		String control, Object a1, Object a2, Object a3, Object a4, Object a5) {
		return Si(control, a1, a2, a3, a4, a5, null, null);
	}

	/**
	 * An internationalized version of Fmt.S uses the java.text.MessageFormat
	 * style control string.
	 *
	 * @param control MessageFormat style pattern
	 * @param a1      object to be substituted
	 * @param a2      object to be substituted
	 * @param a3      object to be substituted
	 * @param a4      object to be substituted
	 * @param a5      object to be substituted
	 * @param a6      object to be substituted
	 *
	 * @return pattern with formatted objects.
	 */
	public static String Si(
		String control, Object a1, Object a2, Object a3, Object a4, Object a5, Object a6) {
		return Si(control, a1, a2, a3, a4, a5, a6, null);
	}

	/**
	 * An internationalized version of Fmt.S uses the java.text.MessageFormat
	 * style control string.
	 *
	 * @param control MessageFormat style pattern
	 * @param a1      object to be substituted
	 * @param a2      object to be substituted
	 * @param a3      object to be substituted
	 * @param a4      object to be substituted
	 * @param a5      object to be substituted
	 * @param a6      object to be substituted
	 * @param more    array of objects for additional substitutions
	 *
	 * @return pattern with formatted objects.
	 *
	 * @deprecated
	 */
	public static String Si(
		String control, Object a1, Object a2, Object a3, Object a4, Object a5, Object a6, Object[] more) {
		Object[] args = argsArray(a1, a2, a3, a4, a5, a6, more);
		return Si(control, args);
	}

	/**
	 * An internationalized version of Fmt.S uses the java.text.MessageFormat
	 * style control string.
	 *
	 * @param control    MessageFormat style pattern
	 * @param vectorArgs object arguments to be substituted
	 *
	 * @return pattern with formatted objects.
	 */
	public static String Si(String control, List listArgs) {
		Object[] args = listArgs.toArray();
		return Si(control, args);
	}

	/**
	 * An internationalized version of Fmt.S uses the java.text.MessageFormat
	 * style control string. This is just a wrapper around MessageFormat's
	 * <code>format(String, Object[])</code> method.
	 *
	 * @param control MessageFormat style pattern
	 * @param args    object arguments to be substituted
	 *
	 * @return pattern with formatted objects.
	 *
	 * @see java.text.MessageFormat#format(String, Object[])
	 */
	public static String Si(String control, Object[] args) {
		try {
			return MessageFormat.format(control, args);
		}
		catch (IllegalArgumentException exp) {
			FastStringBuffer buf = new FastStringBuffer();
			buf.append("The message you tried to print ");
			buf.append(control);
			buf.append("\nHas a problem with the arguments ");
			buf.append(args);
			System.err.print(buf);
		}
		return control;
	}
}

