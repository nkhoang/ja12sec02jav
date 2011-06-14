package com.nkhoang.common.util;

import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

/** Provides a way to register a custom URL protocol handler. */
public class URLProtocolHandlerUtil {

	private final static String HANDLER_PKGS = "java.protocol.handler.pkgs";

	/** Lock for controlling access to our URLStreamHandler setup stuff */
	private static final Object FACTORY_LOCK = new Object();

	/**
	 * Our custom URLStreamHandlerFactory (if we are using this method of
	 * registration, otherwise we will be using system properties)
	 */
	private static HandlerFactory _handlerFactory = null;

	static {
		try {
			// We attempt to use a URLStreamHandlerFactory to handle custom url
			// protocols.  This may fail, because setURLStreamHandlerFactory can
			// only be called one time, and it may have already been called by other
			// code.  If this fails, we fallback to the system property method
			// (which doesn't seem to work in certain contexts--like Java Web
			// Start--so we try the factory method first).
			HandlerFactory tmpFactory = new HandlerFactory();
			URL.setURLStreamHandlerFactory(tmpFactory);
			_handlerFactory = tmpFactory;
		}
		catch (Error e) { //NOPMD
			// somebody else already registered a factory, so fallback to the system
			// property method.
		}
	}


	/**
	 * Register a protocol handler package with the JVM.
	 *
	 * @param protocol the protocol being handled by this handler
	 * @param handler  the handler for this protocol
	 */
	public static void registerProtocolHandler(
		String protocol, URLStreamHandler handler) {
		if (_handlerFactory != null) {

			// the easy case, we successfully registered a factory, so just add this
			// to the factory.
			_handlerFactory.addHandler(protocol, handler);

		} else {
			registerAsSystemProperty(protocol, handler);
		}
	}

	/**
	 * Register the given stream handler as a system property.  Package-scoped
	 * for unit testing.
	 */
	static void registerAsSystemProperty(
		String protocol, URLStreamHandler handler) {
		// We couldn't register a factory, so we have to fallback to the system
		// property route.
		Package pack = handler.getClass().getPackage();
		if (pack == null) {
			throw new IllegalArgumentException(
				"Class loader did not define a package for class " + handler.getClass().getName());
		}
		String pkg = pack.getName();
		String protocolPkg = "." + protocol;

		// Java requires that a URL protocol handler class be named
		// <some-package>.protocol.Handler.  Check for valid name.
		if (pkg.endsWith(protocolPkg) && handler.getClass().getSimpleName().equals("Handler")) {
			// good, strip the protocol off of the package name
			pkg = pkg.substring(0, (pkg.length() - protocolPkg.length()));
		} else {
			throw new IllegalArgumentException(
				"Malformed handler class name '" + handler.getClass().getName() + "' for protocol '" + protocol +
				"'.  Should be " + "'<pkg>.<protocol>.Handler.");
		}

		// Now, tack this modified package name onto the system property
		synchronized (FACTORY_LOCK) {
			String pkgProp = StringUtils.defaultString(
				System.getProperty(HANDLER_PKGS));
			// Build list of currently registered package names
			List<String> packages = new ArrayList<String>();
			for (String oldPkg : pkgProp.split("\\|")) {
				if (!StringUtils.isBlank(oldPkg)) {
					packages.add(oldPkg);
				}
			}
			// If this package isn't already registered, register it
			if (!packages.contains(pkg)) {
				packages.add(pkg);
				System.setProperty(
					HANDLER_PKGS, StringUtils.join(packages.iterator(), "|"));
			}
		}

	}

	/**
	 * Our handler factory for URLStreamHandler's.  If we successfully register
	 * this factory, this controls the mapping of protocol to handler for our
	 * custom protocol handlers.
	 */
	static class HandlerFactory implements URLStreamHandlerFactory {

		/**
		 * Map of protocol to handler (String -> URLStreamHandler).  Use a
		 * TreeMap because it uses less space than a HashMap for a small number
		 * of entries.  We shouldn't have a lot of elements and we don't need
		 * super speedy access.
		 */
		private final TreeMap<String, URLStreamHandler> _protocolMap = new TreeMap<String, URLStreamHandler>();

		/**
		 * Registers the given handler for the given protocol.
		 *
		 * @param protocol the protocol being handled by this handler
		 * @param handler  the handler for this protocol
		 */
		void addHandler(String protocol, URLStreamHandler handler) {
			synchronized (FACTORY_LOCK) {
				_protocolMap.put(protocol, handler);
			}
		}

		public URLStreamHandler createURLStreamHandler(String protocol) {
			if (protocol == null) {
				return null;
			}
			synchronized (FACTORY_LOCK) {
				return _protocolMap.get(protocol);
			}
		}

	}


}