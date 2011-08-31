package com.nkhoang.gae.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by IntelliJ IDEA.
 * User: hnguyen93
 * Date: Nov 4, 2010
 * Time: 11:30:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class DateConverter {
	public static final  String           DEFAULT_GOLD_DATE_FORMAT     = "yyyy-MM-dd HH:mm";
	public static final  String           DEFAULT_CURRENCY_DATE_FORMAT = "HH:mm a dd/MM/yyyy";
	private static final String           DEFAULT_WORD_DATE_FORMAT     = "dd/MM/yyyy hh:mm";
	private static       SimpleDateFormat _formatter                   = new SimpleDateFormat(
		DEFAULT_GOLD_DATE_FORMAT, Locale.US);

	/**
	 * Convert from <code>DS timestamp</code> to default display date format.
	 *
	 * @param timeStamp a timestamp in <code>long</code>.
	 *
	 * @return date string in default display date format.
	 */
	public static String formatDefaultDisplayDate(Long timeStamp) {
		_formatter = new SimpleDateFormat(DEFAULT_WORD_DATE_FORMAT);
		_formatter.setTimeZone(TimeZone.getTimeZone(TimeZone.getTimeZone("GMT-8").getID()));
		return _formatter.format(new Date(timeStamp));
	}

	/**
	 * Convert from a string token to a Date object
	 *
	 * @param tokenString a token string to be parsed to Date object.
	 *
	 * @return a Date object.
	 *
	 * @throws ParseException parse error.
	 */
	public static Date convertFromStringToken(String tokenString, String dateFormat) throws ParseException {
		_formatter = new SimpleDateFormat(dateFormat, Locale.US);
		_formatter.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
		return _formatter.parse(tokenString);
	}

	public static String parseDateFromLong(Long l) {
		GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Asia/Bangkok"));
		calendar.setTimeInMillis(l);
		Date d = calendar.getTime();
		return parseDate(d, DEFAULT_GOLD_DATE_FORMAT);
	}

	public static String parseDate(Date date, String dateFormat) {
		_formatter = new SimpleDateFormat(dateFormat, Locale.US);
		_formatter.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
		return _formatter.format(date);
	}
}
