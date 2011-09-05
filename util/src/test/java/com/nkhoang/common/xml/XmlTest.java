package com.nkhoang.common.xml;

import junit.framework.Assert;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.print.DocFlavor;
import java.io.FileInputStream;
import java.io.InputStream;

/** Test XML manipulation using {@link DOMUtil} and {@link XMLUtil}. */
public class XmlTest {
	private static Logger LOG = LoggerFactory.getLogger(XmlTest.class.getCanonicalName());

	private String _xml;

	@Before
	public void initialize() throws Exception {
		InputStream in = this.getClass().getResourceAsStream("vocabulary.xml");
		_xml = IOUtils.toString(in);
	}

	@Test
	public void testPrettyPrint() throws Exception {
		LOG.info(XMLUtil.prettyPrint(_xml));
	}

	@Test
	public void testManipulatingXML() throws Exception {
		Document doc = XMLUtil.parse(_xml);
		Node pageNode = XPathUtil.selectNode(doc, "/Vocabulary/Root/Chapter/Page");
		Assert.assertNotNull(pageNode);
		Assert.assertTrue(CollectionUtils.isNotEmpty(DOMUtil.getChildren(pageNode)));
		for (Element word : DOMUtil.getChildren(pageNode)) {
			LOG.info(word.getTextContent());
			// test remove
			pageNode.removeChild(word);
		}
		LOG.info("XML after removing word nodes:");
		LOG.info(XMLUtil.prettyPrint(doc));
	}
}
