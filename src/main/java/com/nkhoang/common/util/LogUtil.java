package com.nkhoang.common.util;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;

public class LogUtil {

	/**
	 * Logs a warning of "<msg>: <throwable>" (no stack trace), unless DEBUG is
	 * enabled, in which case warn(msg, throwable) is called (prints stack
	 * trace).  Useful for cases where exceptions may be "expected" and stack
	 * traces are generally not desired (but still maintains the ability to
	 * print stack traces merely by enabling DEBUG).
	 */
	public static void warnDebug(Log log, Object msg, Throwable t) {
		if (log.isDebugEnabled()) {
			log.warn(msg, t);
		} else {
			log.warn(createNonDebugMsg(msg, t));
		}
	}

	/**
	 * Logs an info message of "<msg>: <throwable>" (no stack trace), unless
	 * DEBUG is enabled, in which case info(msg, throwable) is called (prints
	 * stack trace).  Useful for cases where exceptions may be "expected" and
	 * stack traces are generally not desired (but still maintains the ability
	 * to print stack traces merely by enabling DEBUG).
	 */
	public static void infoDebug(Log log, Object msg, Throwable t) {
		if (log.isDebugEnabled()) {
			log.info(msg, t);
		} else {
			log.info(createNonDebugMsg(msg, t));
		}
	}

	/** @return a String of "<msg>: <throwable>". */
	private static String createNonDebugMsg(Object msg, Throwable t) {
		return new StringBuilder(ObjectUtils.toString(msg)).append(": ").append(ObjectUtils.toString(t)).toString();
	}


	/**
	 * Returns a short "identity string," consisting of the passed object's
	 * classname sans package and its identity hashcode (eg, "Boolean@1adb").
	 * Null input is returned as "null@0".
	 * <p/>
	 * Note that Jakarta <code>ObjectUtils</code> provides a method with the
	 * same name. However, it returns the fully-qualified package name, which
	 * is not useful 99% of the time.
	 */
	public static String identityToString(Object obj) {
		return (obj == null) ? "null@0" : obj.getClass().getSimpleName() + "@" + System.identityHashCode(obj);
	}
}
