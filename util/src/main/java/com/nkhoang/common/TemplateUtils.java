package com.nkhoang.common;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

import javax.servlet.ServletContext;
import java.util.Locale;

public class TemplateUtils {
	public static Configuration _cfg = null;

	/**
	 * Get the FreeMarker configuration.
	 *
	 * @param context the servlet context passed in in order to get the resource path (WEB-INF folder).
	 *
	 * @return the successfully created {@link freemarker.template.Configuration} object.
	 */
	public static Configuration getConfiguration(ServletContext context) {
		if (_cfg == null) {
			_cfg = new Configuration();
			_cfg.setServletContextForTemplateLoading(context, "WEB-INF/templates");
			_cfg.setEncoding(Locale.US, "UTF-8");
			_cfg.setObjectWrapper(new DefaultObjectWrapper());
		}

		return _cfg;
	}
}