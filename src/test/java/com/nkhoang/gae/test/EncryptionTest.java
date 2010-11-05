package com.nkhoang.gae.test;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration({ "/applicationContext-service.xml" })

public class EncryptionTest
{
    @Test
    public void testRun() {}

	public void testPropertyEncryptor() {
/*
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
*/

	}
}
