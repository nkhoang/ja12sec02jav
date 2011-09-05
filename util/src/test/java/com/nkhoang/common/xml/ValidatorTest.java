package com.nkhoang.common.xml;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import junit.framework.TestCase;

public class ValidatorTest extends TestCase {

	public ValidatorTest(String name) {
		super(name);
	}

	public void testValidationReferencedInXml() throws Exception {
		Validator validator = new Validator();
		String path = getFilePathOfResource("docWithReferencedSchema.xml");
		validator.validate(path);
	}

	public void testValidationWithSchemaAsString() throws Exception {
		InputStream is = getInput("docYankerXmlSchema.xsd");
		String xsd = IOUtils.toString(is);
		Validator validator = new Validator(xsd);
		String path = getFilePathOfResource("docWithReferencedSchema.xml");
		validator.validate(path);
	}

	public void testValidationWithSchemaAsFile() throws Exception {
		URL url = getFileUrl("docYankerXmlSchema.xsd");
		File file = FileUtils.toFile(url);
		Validator validator = new Validator(file);
		String path = getFilePathOfResource("docWithReferencedSchema.xml");
		validator.validate(path);
	}

	public void testValidationWithSchemaAsStringThatFails() throws Exception {
		InputStream is = getInput("docYankerXmlSchema.xsd");
		String xsd = IOUtils.toString(is);
		Validator validator = new Validator(xsd);
		String path = getFilePathOfResource("docThatFailsValidation.xml");
		boolean caughtException = false;
		try {
			validator.validate(path);
		}
		catch (SAXException e) {
			caughtException = true;
		}
		assertTrue("Expected to catch an exception", caughtException);
	}

	public void testValidationWithSchemaAsFileThatFails() throws Exception {
		URL url = getFileUrl("docYankerXmlSchema.xsd");
		File file = FileUtils.toFile(url);
		Validator validator = new Validator(file);
		String path = getFilePathOfResource("docThatFailsValidation.xml");
		boolean caughtException = false;
		try {
			validator.validate(path);
		}
		catch (SAXException e) {
			caughtException = true;
		}
		assertTrue("Expected to catch an exception", caughtException);
	}

	private InputStream getInput(String path) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
	}

	private String getFilePathOfResource(String path) {
		return Thread.currentThread().getContextClassLoader().getResource(path).getFile();
	}

	private URL getFileUrl(String path) {
		return Thread.currentThread().getContextClassLoader().getResource(path);
	}

}
