package com.nkhoang.gae.utils;

import com.nkhoang.gae.model.User;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.htmlparser.jericho.Source;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class WebUtils {
    private static Logger LOG = LoggerFactory.getLogger(WebUtils.class.getCanonicalName());

    public static final String DEFAULT_GOLD_DATE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DEFAULT_CURRENCY_DATE_FORMAT = "HH:mm a dd/MM/yyyy";
    public static final String DEFAULT_WORD_DATE_FORMAT = "dd/MM/yyyy hh:mm";
    public static final String DEFAULT_CLIENT_DATE_FORMAT = "dd/MM/yyyy";
    public static SimpleDateFormat _formatter = new SimpleDateFormat(
            DEFAULT_GOLD_DATE_FORMAT, Locale.US);

    public static Source retrieveWebContent(String websiteURL) throws IOException {
        Source source = null;
        URL url = new URL(websiteURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        // get inputStream
        InputStream is = connection.getInputStream();
        // create source HTML
        source = new Source(is);

        return source;
    }

    /**
     * Convert from <code>DS timestamp</code> to default display date format.
     *
     * @param timeStamp a timestamp in <code>long</code>.
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
     * @return a Date object.
     * @throws java.text.ParseException parse error.
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

    /**
     * Convert from date in string format to Date object in specific pattern specified by <i></i>
     *
     * @param date       the date to be converted.
     * @param dateFormat format string.
     * @return a Date object.
     * @throws ParseException if failing to convert date string to Date object.
     */
    public static Date parseDate(String date, String dateFormat) throws ParseException {
        _formatter = new SimpleDateFormat(dateFormat, Locale.US);
        _formatter.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
        return _formatter.parse(date);
    }

    public static String sendMail(
            String msgBody, String senderEmail, String subject, String recipEmail) {
        String message = "";
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(senderEmail));
            msg.addRecipient(
                    Message.RecipientType.TO, new InternetAddress(recipEmail));
            msg.setSubject(subject);
            msg.setContent(msgBody, "text/html");
            Transport.send(msg);
        } catch (MessagingException mse) {
            message = mse.getMessage();
            LOG.error(String.format("Could not send email to [%s].", recipEmail), mse);
        }
        return message;
    }

    /**
     * Build mail body message from a template by providing these following information:
     *
     * @param context      the {@link javax.servlet.ServletContext} object to get the template path.
     * @param templateName the template name used to build the message body.
     * @param data         the root data for templating.
     * @return a message body based on a specific template.
     */
    public static String buildMail(ServletContext context, String templateName, Map<String, Object> data) {
        try {
            Template template = TemplateUtils.getConfiguration(context).getTemplate(templateName);
            StringWriter writer = new StringWriter();
            template.process(data, writer);

            return writer.toString();
        } catch (IOException ioe) {
            LOG.error(String.format("Could not load template '[%s]'", templateName), ioe);
        } catch (TemplateException tpe) {
            LOG.error("Could not build template because: ", tpe);
        }
        return "";
    }

    @Test
    public void testDateConvert() throws Exception {
        String date = "19/09/2011";
        Date startDate = WebUtils.parseDate(date + " 00:00:01", WebUtils.DEFAULT_CLIENT_DATE_FORMAT + " HH:mm:ss");
        Date endDate = WebUtils.parseDate(date + " 23:59:59", WebUtils.DEFAULT_CLIENT_DATE_FORMAT + " HH:mm:ss");
    }
}
