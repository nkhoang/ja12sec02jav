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
    public static final String defaultGoldDateFormat = "yyyy-MM-dd HH:mm";
    public static final String defaultCurrencyDateFormat = "HH:mm a dd/MM/yyyy";
    private static SimpleDateFormat formatter = new SimpleDateFormat(defaultGoldDateFormat, Locale.US);

    /**
     * Convert from a string token to a Date object
     * @param tokenString a token string to be parsed to Date object.
     * @return a Date object.
     * @throws ParseException parse error.
     */
    public static Date convertFromStringToken(String tokenString, String dateFormat) throws ParseException{
        formatter = new SimpleDateFormat(dateFormat, Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
        return formatter.parse(tokenString);
    }

    public static String parseDateFromLong(Long l) {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Asia/Bangkok"));
        calendar.setTimeInMillis(l);
        Date d = calendar.getTime();
        return parseDate(d, defaultGoldDateFormat);
    }

    public static String parseDate(Date date, String dateFormat) {
        formatter = new SimpleDateFormat(dateFormat, Locale.US);
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
        return formatter.format(date);
    }
}
