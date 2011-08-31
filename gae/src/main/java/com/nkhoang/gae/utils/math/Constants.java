package com.nkhoang.gae.utils.math;

import java.math.BigDecimal;

/** Constants for well known values */
public class Constants {
	/** Constant for number of days in a week. */
	public static final long DaysPerWeek = 7;

	/** Constant for number of hours in a day. */
	public static final long HoursPerDay = 24;

	/** Constant for number of minutes per hour. */
	public static final long MinutesPerHour = 60;

	/** Constant for number of seconds per minute. */
	public static final long SecondsPerMinute = 60;

	/** Constant for number of milliseconds per second. */
	public static final long MillisPerSecond = 1000;

	/** Constant for number of milliseconds per minute. */
	public static final long MillisPerMinute = MillisPerSecond * SecondsPerMinute;

	/** Constant for number of milliseconds per hour. */
	public static final long MillisPerHour = MillisPerMinute * MinutesPerHour;

	/** Constant for number of milliseconds per day. */
	public static final long MillisPerDay = MillisPerHour * HoursPerDay;

	/** Constant for number of milliseconds per week. */
	public static final long MillisPerWeek = MillisPerDay * DaysPerWeek;

	/** String constant for java.lang.Object */
	public static final String ObjectType     = "java.lang.Object";
	/** String constant for java.lang.String */
	public static final String StringType     = "java.lang.String";
	/** String constant for java.lang.Boolean */
	public static final String BooleanType    = "java.lang.Boolean";
	/** String constant for java.lang.Integer */
	public static final String IntegerType    = "java.lang.Integer";
	/** String constant for java.lang.Long */
	public static final String LongType       = "java.lang.Long";
	/** String constant for java.lang.Float */
	public static final String FloatType      = "java.lang.Float";
	/** String constant for java.lang.Double */
	public static final String DoubleType     = "java.lang.Double";
	/** String constant for java.lang.Number */
	public static final String NumberType     = "java.lang.Number";
	/** String constant for java.math.BigDecimal */
	public static final String BigDecimalType = "java.math.BigDecimal";
	/** String constant for java.util.Date */
	public static final String JavaDateType   = "java.util.Date";
	/** String constant for java.sql.Date */
	public static final String SQLDateType    = "java.sql.Date";
	/** String constant for java.sql.Timestamp */
	public static final String TimestampType  = "java.sql.Timestamp";

	/** String constant for char */
	public static final String CharPrimitiveType    = "char";
	public static final String JavaCharAbbreviation = "C";
	/** String constant for byte */
	public static final String BytePrimitiveType    = "byte";
	public static final String JavaByteAbbreviation = "B";

	/** String constant for int */
	public static final String ShortPrimitiveType      = "short";
	public static final String JavaShortAbbreviation   = "S";
	/** String constant for int */
	public static final String IntPrimitiveType        = "int";
	public static final String JavaIntAbbreviation     = "I";
	/** String constant for long */
	public static final String LongPrimitiveType       = "long";
	public static final String JavaLongAbbreviation    = "J";
	/** String constant for float */
	public static final String FloatPrimitiveType      = "float";
	public static final String JavaFloatAbbreviation   = "F";
	/** String constant for double */
	public static final String DoublePrimitiveType     = "double";
	public static final String JavaDoubleAbbreviation  = "D";
	/** String constant for char */
	public static final String BooleanPrimitiveType    = "boolean";
	public static final String JavaBooleanAbbreviation = "Z";

	public static final String JavaArrayAbbreviation  = "[";
	public static final String JavaObjectAbbreviation = "L";

	/** String constant for [Ljava.lang.Integer (jni) */
	public static final String IntegerArrayType = "[Ljava.lang.Integer";
	/** String constant for [I (jni) */
	public static final String IntArrayType     = "[I";

	/** Public constant for Integer(-1) to avoid object allocation */
	public static final Integer MinusOneInteger = new Integer(-1); // OK
	/** Public constant for Integer(0 to avoid object allocation */
	public static final Integer ZeroInteger     = new Integer(0);  // OK
	/** Public constant for Integer(1) to avoid object allocation */
	public static final Integer OneInteger      = new Integer(1);  // OK

	/** Public constant for Long(-1) to avoid object allocation */
	public static final Long MinusOneLong = new Long(-1); // OK
	/** Public constant for Long(0) to avoid object allocation */
	public static final Long ZeroLong     = new Long(0); // OK
	/** Public constant for Long(1) to avoid object allocation */
	public static final Long OneLong      = new Long(1); // OK

	/** Public constant for Float(0.0) to avoid object allocation */
	public static final Float  ZeroFloat  = new Float(0.0);
	/** Public constant for Double(0.0) to avoid object allocation */
	public static final Double ZeroDouble = new Double(0.0);

	/** Public constant for BigDecimal(0.0) to avoid object allocation */
	public static final BigDecimal ZeroBigDecimal       = new BigDecimal(0.0); // OK
	/** Public constant for BigDecimal(1.0) to avoid object allocation */
	public static final BigDecimal OneBigDecimal        = new BigDecimal(1.0); // OK
	/**
	 * Public constant for BigDecimal(10.0) to avoid object
	 * allocation
	 */
	public static final BigDecimal TenBigDecimal        = new BigDecimal(10.0); // OK
	/**
	 * Public constant for BigDecimal(100.0) to avoid object
	 * allocation
	 */
	public static final BigDecimal OneHundredBigDecimal = new BigDecimal(100.0); // OK
	/** Public constant for Object() */
	public static final Object     NullObject           = new Object();
	/** Public constant for an empty string */
	public static final String     EmptyString          = "";

	// package private
	static final int     MaxSavedInt = 8192;
	static final int     MinSavedInt = -1024;
	static final Integer posInts[]   = new Integer[MaxSavedInt + 1];
	static final Integer negInts[]   = new Integer[(-MinSavedInt) + 1];

	static {
		int i;

		// start with the negative numbers
		for (i = 1; i <= (-MinSavedInt); i++) {
			negInts[i] = new Integer(-i); // OK
		}
		negInts[0] = ZeroInteger;

		// then the positive
		posInts[0] = ZeroInteger;
		posInts[1] = OneInteger;
		for (i = 2; i <= (MaxSavedInt); i++) {
			posInts[i] = new Integer(i); // OK
		}
	}

	// package private
	static final long MaxSavedLong = 64;
	static final long MinSavedLong = -16;
	static final Long posLongs[]   = new Long[(int) MaxSavedLong + 1];
	static final Long negLongs[]   = new Long[(-(int) MinSavedLong) + 1];

	static {
		int i;

		// start with the negative numbers
		for (i = 1; i <= (-MinSavedLong); i++) {
			negLongs[i] = new Long(-i); // OK
		}
		negLongs[0] = ZeroLong;

		// then the positive
		posLongs[0] = ZeroLong;
		posLongs[1] = OneLong;
		for (i = 2; i <= (MaxSavedLong); i++) {
			posLongs[i] = new Long(i); // OK
		}
	}

	/**
	 * Where applicable some functions use the Success int constant return
	 * value.
	 */
	public static final int Success = 0;

	/**
	 * Where applicable some functions use the Success Integer constant
	 * return value.
	 */
	public static final Integer SuccessInteger = Constants.ZeroInteger;

	/**
	 * Helper function to generate Booleans without excess memory
	 * allocation.
	 *
	 * @param b the boolean value requested
	 *
	 * @return the constant Boolean for true or false as requested.
	 */
	public static Boolean getBoolean(boolean b) {
		return b ? Boolean.TRUE : Boolean.FALSE;
	}

	/**
	 * Parse a string into a Boolean. This assumes that the
	 * Boolean.toBoolean() method was used to create the String.
	 *
	 * @param s a String representation of a boolean
	 *
	 * @return <b>Boolean.TRUE</b> if the string was for the true
	 *         boolean, <b>Boolean.FALSE</b> otherwise
	 */
	public static Boolean getBoolean(String s) {
		return getBoolean((s != null) && s.equalsIgnoreCase("true"));
	}

	public static Integer getInteger(int i) {
		/*
					This is code to provide a histogram of the uses of
					Constants.getInteger() it looks like the histogram changes
					every release and I don't want to have to keep rewriting
					the code...so I'm just leaving it commented out so there
					is no footprint

					int index = i/1000;
					index += 50;
					synchronized(histogram) {
					if (index >= 0 && index < 100) {
					histogram[index]++;
					}
					count ++;
					if (count % 10000 == 0) {
					FormatBuffer fsb = new FormatBuffer(1024);
					int tmpsum = 0;
					for (int j=0;j<100;j++) {
					int range = (j-50)*1000;
					Fmt.B(fsb, "%s:%s;", Integer.toString(range),
					Integer.toString(histogram[j]));
					tmpsum += histogram[j];
					}
					Log.misc.debug("Total of %s objects, %s not in histogram; %s",
					Integer.toString(count),
					Integer.toString(count - tmpsum),
					fsb.toString());
					}
					}
				*/
		if (i >= 0 && i <= Constants.MaxSavedInt) {
			return (Constants.posInts[i]);
		}
		if (i < 0 && i >= Constants.MinSavedInt) {
			return (Constants.negInts[-i]);
		}
		return new Integer(i); // OK
	}

	/**
	 * Helper function to create Long objects. This will try to
	 * avoid allocating new Long objects on every invocation.
	 *
	 * @param l a long to convert into a Long
	 *
	 * @return a constant Long, occasionally from a pre-allocated
	 *         set.
	 */
	public static Long getLong(long l) {
		if (l >= 0 && l <= Constants.MaxSavedLong) {
			return (Constants.posLongs[(int) l]);
		}
		if (l < 0 && l >= Constants.MinSavedLong) {
			return (Constants.negLongs[(int) -l]);
		}
		return new Long(l); // OK
	}
}








