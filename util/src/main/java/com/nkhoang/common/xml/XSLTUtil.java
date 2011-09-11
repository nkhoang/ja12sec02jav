package com.nkhoang.common.xml;

import java.io.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * This is a utility class designed to make transforming xml into other xml
 * easier.  It is designed to allow straightforward dynamic construciton of
 * XSLT.  A typical use case would be to obtain an identity transform from the
 * getXMLIdentityTransform method, then build it up using other methods.  The
 * getSourceForDocument method and getInputStreamForDocument method are included
 * for convenience.
 */
public class XSLTUtil {
	private static final String TEMPLATE        = "template";
	private static final String ELEMENT         = "element";
	private static final String MATCH           = "match";
	private static final String NAME            = "name";
	private static final String COPY            = "copy";
	private static final String COPY_OF         = "copy-of";
	private static final String APPLY_TEMPLATES = "apply-templates";
	private static final String SELECT          = "select";
	private static final String SELECT_VALUE    = "@*|node()";
	private static final String ANY             = "//";
	private static final String AT              = "@";
	private static final String THIS            = ".";

	private static final String TRANSFORM_STRING =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + " <xsl:stylesheet version=\"1.0\"" +
		" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">" + " <xsl:output method=\"xml\" />" + " " +
		" <xsl:template match=\"@*|node()\">" + "   <xsl:copy>" + "    <xsl:apply-templates select=\"@*|node()\"/>" +
		"  </xsl:copy>" + "</xsl:template>" + " " + " </xsl:stylesheet>";

	private static final String EMPTY_TRANSFORM_STRING =
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<xsl:stylesheet version=\"2.0\"" +
		"  xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">" + "  <xsl:output method=\"xml\" />" + "  " +
		"  <xsl:template match=\"@*|node()\"/>" + "  <xsl:template match=\"/child::*\">" + "    <xsl:copy>" +
		"      <xsl:apply-templates select=\"@*|node()\"/>" + "    </xsl:copy>" + "  </xsl:template>" + " " +
		"</xsl:stylesheet>";

	static {
		// Handler.init();
	}

	/**
	 * Allows children of the nodes specified by the xpath expression to pass
	 * through to template-matchers.
	 */
	public static void addXPathPassthrough(Document transform, String xpath) {
		Element docElement = transform.getDocumentElement();
		String namespaceURI = docElement.getNamespaceURI();
		String prefix = docElement.getPrefix();

		Element xslTemplate = DOMUtil.addChild(
			docElement, namespaceURI, getName(prefix, TEMPLATE));
		xslTemplate.setAttribute(MATCH, xpath);

		Element copy = DOMUtil.addChild(
			xslTemplate, namespaceURI, getName(prefix, COPY));

		Element applyTemplates = DOMUtil.addChild(
			copy, namespaceURI, getName(prefix, APPLY_TEMPLATES));
		applyTemplates.setAttribute(SELECT, SELECT_VALUE);
	}

	public static void addXPathKeeper(Document transform, String xpath) {
		addXPathKeeper(transform, xpath, THIS);
	}

	public static void addXPathKeeper(
		Document transform, String xpath, String attribute) {

		Element docElement = transform.getDocumentElement();
		String namespaceURI = docElement.getNamespaceURI();
		String prefix = docElement.getPrefix();

		Element xslTemplate = DOMUtil.addChild(
			docElement, namespaceURI, getName(prefix, TEMPLATE));

		xslTemplate.setAttribute(MATCH, xpath);

		Element copyOf = DOMUtil.addChild(
			xslTemplate, namespaceURI, getName(prefix, COPY_OF));
		copyOf.setAttribute(SELECT, attribute);
	}

	/**
	 * This method adds a template to an XSLT transform to output
	 *
	 * @param transform
	 * @param oldElementName
	 * @param newElementName
	 */
	public static void addElementReplacement(
		Document transform, String oldElementName, String newElementName) {
		Element docElement = transform.getDocumentElement();
		String namespaceURI = docElement.getNamespaceURI();
		String prefix = docElement.getPrefix();

		Element xslTemplate = DOMUtil.addChild(
			docElement, namespaceURI, getName(prefix, TEMPLATE));
		xslTemplate.setAttribute(MATCH, ANY + oldElementName);

		Element xslElement = DOMUtil.addChild(
			xslTemplate, namespaceURI, getName(prefix, ELEMENT));
		xslElement.setAttribute(NAME, newElementName);

		Element xslApplyTemplates = DOMUtil.addChild(
			xslElement, namespaceURI, getName(prefix, APPLY_TEMPLATES));
		xslApplyTemplates.setAttribute(SELECT, SELECT_VALUE);
	}

	public static void addElementWrapper(
		Document transform, String elementName, String wrapperElementName) {
		addElementWrapper(transform, elementName, wrapperElementName, true);
	}

	public static void addElementWrapper(
		Document transform, String elementName, String wrapperElementName, boolean useLocalName) {
		Element docElement = transform.getDocumentElement();
		String namespaceURI = docElement.getNamespaceURI();
		String prefix = docElement.getPrefix();

		Element xslTemplate = DOMUtil.addChild(
			docElement, namespaceURI, getName(prefix, TEMPLATE));
		if (useLocalName) {
			xslTemplate.setAttribute(MATCH, ANY + wrapAsLocalName(elementName));
		} else {
			xslTemplate.setAttribute(MATCH, ANY + elementName);
		}

		Element xslElement = DOMUtil.addChild(
			xslTemplate, namespaceURI, getName(prefix, ELEMENT));
		xslElement.setAttribute(NAME, wrapperElementName);

		Element xslCopy = DOMUtil.addChild(
			xslElement, namespaceURI, getName(prefix, COPY));

		Element xslApplyTemplates = DOMUtil.addChild(
			xslCopy, namespaceURI, getName(prefix, APPLY_TEMPLATES));
		xslApplyTemplates.setAttribute(SELECT, SELECT_VALUE);
	}

	public static void addElementDeleter(
		Document transform, String elementName, boolean passthroughContents) {
		addElementDeleter(transform, elementName, passthroughContents, true);
	}

	public static void addElementDeleter(
		Document transform, String elementName, boolean passthroughContents, boolean useLocalName) {
		Element docElement = transform.getDocumentElement();
		String namespaceURI = docElement.getNamespaceURI();
		String prefix = docElement.getPrefix();

		Element xslTemplate = DOMUtil.addChild(
			docElement, namespaceURI, getName(prefix, TEMPLATE));
		if (useLocalName) {
			xslTemplate.setAttribute(MATCH, ANY + wrapAsLocalName(elementName));
		} else {
			xslTemplate.setAttribute(MATCH, ANY + elementName);
		}

		if (passthroughContents) {
			Element xslApplyTemplates = DOMUtil.addChild(
				xslTemplate, namespaceURI, getName(prefix, APPLY_TEMPLATES));
			xslApplyTemplates.setAttribute(SELECT, SELECT_VALUE);
		}
	}

	public static void addAttributeDeleter(
		Document transform, String attributeName) {
		Element docElement = transform.getDocumentElement();
		String namespaceURI = docElement.getNamespaceURI();
		String prefix = docElement.getPrefix();

		Element xslTemplate = DOMUtil.addChild(
			docElement, namespaceURI, getName(prefix, TEMPLATE));
		xslTemplate.setAttribute(MATCH, AT + attributeName);
	}

	/**
	 * Gets a Document that is an Identity XML xslt.  If applied as a transform to
	 * an xml document, the same document will be output.
	 *
	 * @return the Document representation of the transform
	 */
	public static Document getXMLIdentityTransform() {
		return XMLUtil.parse(TRANSFORM_STRING);
	}

	/**
	 * Gets a Document that is an empty XML xslt.  This is the opposite of an
	 * Identity transform.
	 *
	 * @return the Document representation of the transform
	 */
	public static Document getXMLEmptyTransform() {
		return XMLUtil.parse(EMPTY_TRANSFORM_STRING);
	}

	public static Document getXMLEmptyTransform(
		NamespaceContext nsCtx, String... namespaces) {
		Document dom = XMLUtil.parse(EMPTY_TRANSFORM_STRING);
		for (String namespace : namespaces) {
			Iterator iter = nsCtx.getPrefixes(namespace);
			if (iter != null) {
				while (iter.hasNext()) {
					String prefix = (String) iter.next();
					String name = XMLConstants.XMLNS_ATTRIBUTE + ":" + prefix;
					dom.getDocumentElement().setAttribute(name, namespace);
				}
			}
		}
		return dom;
	}

	/**
	 * Gets an InputStream backed by the input Document
	 *
	 * @param doc
	 *
	 * @return
	 */
	public static InputStream getInputStreamForDocument(Document doc) {
		return new ByteArrayInputStream(XMLUtil.serialize(doc).getBytes());
	}

	/**
	 * Transforms an xml document via a xslt transform.
	 *
	 * @param xml    the dom
	 * @param xsl    the xslt
	 *
	 * @return an string of the resulting xml
	 */
	public static String transform(String xml, String xsl){
        try {
		ByteArrayOutputStream result = XSLTUtil.innerTransform(
			new StreamSource(
				new ByteArrayInputStream(xml.getBytes("UTF-8"))), new StreamSource(
			new ByteArrayInputStream(xsl.getBytes("UTF-8"))), Collections.<String, Object>emptyMap());
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
	}

	/**
	 * Transforms an xml document via a xslt transform.
	 *
	 * @param dom    the dom
	 * @param xsl    the xslt
	 * @param params the parameters
	 *
	 * @return an string of the resulting xml
	 */
	public static String transform(Document dom, String xsl, Map<String, Object> params) {
		ByteArrayOutputStream result = innerTransform(
			new DOMSource(dom), new StreamSource(new ByteArrayInputStream(xsl.getBytes())), params);
		return result.toString();
	}

	/**
	 * Transforms an xml document via a xslt transform.
	 *
	 * @param xml    the dom
	 * @param xsl    the xslt
	 *
	 * @return an input stream of the resulting xml
	 */
	public static InputStream transform(InputStream xml, InputStream xsl) {
		return transform(xml, xsl, Collections.<String, Object>emptyMap());
	}

	/**
	 * Transforms an xml document via a xslt transform.
	 *
	 * @param xsl    the xslt
	 * @param params the parameters
	 *
	 * @return an input stream of the resulting xml
	 */
	public static InputStream transform(
		InputStream xml, InputStream xsl, Map<String, Object> params) {
		ByteArrayOutputStream byteOut = innerTransform(
			new StreamSource(xml), new StreamSource(xsl), params);
		return new ByteArrayInputStream(byteOut.toByteArray());
	}

	private static ByteArrayOutputStream innerTransform(
		Source xml, Source xsl, Map<String, Object> params) {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		try {
			transformAndWrite(xsl, xml, byteOut, params);
		}
		catch (TransformerConfigurationException tce) {
			throw new RuntimeException("Could not apply transform", tce);
		}
		catch (TransformerException te) {
			throw new RuntimeException("Could not apply transform", te);
		}
		return byteOut;
	}

	private static void transformAndWrite(
		Source xsltSource, Source xmlSource, OutputStream out,
		Map<String, Object> params) throws TransformerConfigurationException, TransformerException {
		//initialize a transformer for the real work
		TransformerFactory transFact = TransformerFactory.newInstance();
		Transformer trans = transFact.newTransformer(xsltSource);

		for (Map.Entry<String, Object> entry : params.entrySet()) {
			trans.setParameter(entry.getKey(), entry.getValue());
		}

		//transform the xml and write the results to the output
		trans.transform(xmlSource, new StreamResult(out));
	}

	/**
	 * Gets a Source object backed by the input Document
	 *
	 * @param doc
	 *
	 * @return
	 */
	public static Source getSourceForDocument(Document doc) {
		return new DOMSource(doc);
	}

	private static String getName(String prefix, String name) {
		return prefix + ":" + name;
	}

	private static String wrapAsLocalName(String name) {
		return "node()[local-name() = '" + name + "']";
	}

}
