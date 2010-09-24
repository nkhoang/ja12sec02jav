package com.nkhoang.gae.core.encryption.test;


import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.FixedStringSaltGenerator;
import org.junit.Test;

import junit.framework.TestCase;

public class EncryptionTest extends TestCase
{
	public void testPropertyEncryptor() {
		FixedStringSaltGenerator saltGenerator = new FixedStringSaltGenerator();
		saltGenerator.setSalt("property salt...");
		
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		//encryptor.setAlgorithm("PBEWithMD5AndDES");
		encryptor.setAlgorithm("PBEWithSHA1AndRC2_40");
		encryptor.setKeyObtentionIterations(1000);
		encryptor.setSaltGenerator(saltGenerator);
		encryptor.setPassword("property password");
		
		String saltString = "digest password";
		//String algorithmString = "MD5";
		String algorithmString = "SHA-256";
		String iterationString = "1000";
		
		System.out.println(encryptor.encrypt(iterationString));
		System.out.println(encryptor.encrypt(algorithmString));
		System.out.println(encryptor.encrypt(saltString));
		assertNotNull(encryptor);
		
		
	}
}
