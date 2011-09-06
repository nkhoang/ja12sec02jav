package com.nkhoang.gae.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Properties;


public class MailUtils {
	private static final Logger LOG = LoggerFactory.getLogger(MailUtils.class.getCanonicalName());

	public static void sendMail(
		String msgBody, String senderEmail, String subject, String recipEmail) {
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
		}
		catch (MessagingException mse) {
			LOG.error(String.format("Could not send email to [%s].", recipEmail), mse);
		}
	}

	/**
	 * Build mail body message from a template by providing these following information:
	 *
	 * @param context      the {@link ServletContext} object to get the template path.
	 * @param templateName the template name used to build the message body.
	 * @param data         the root data for templating.
	 *
	 * @return a message body based on a specific template.
	 */
	public static String buildMail(ServletContext context, String templateName, Map<String, Object> data) {
		try {
			Template template = TemplateUtils.getConfiguration(context).getTemplate(templateName);
			StringWriter writer = new StringWriter();
			template.process(data, writer);

			return writer.toString();
		}
		catch (IOException ioe) {
			LOG.error(String.format("Could not load template '[%s]'", templateName), ioe);
		}
		catch (TemplateException tpe) {
			LOG.error("Could not build template because: ", tpe);
		}
		return "";
	}

}
