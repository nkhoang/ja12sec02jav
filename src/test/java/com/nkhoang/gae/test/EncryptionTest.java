package com.nkhoang.gae.test;


import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.salt.FixedStringSaltGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration({"/applicationContext-service.xml"})

public class EncryptionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionTest.class);

    @Test
    public void testRun() {
    }

    @Test
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

        //LOGGER.info(encryptor.decrypt("emGwruT+2OXLTyHskiBsXw=="));

        //LOGGER.info(encryptor.encrypt(iterationString));
        //LOGGER.info(encryptor.encrypt(algorithmString));
        //LOGGER.info(encryptor.encrypt(saltString));


    }
}
