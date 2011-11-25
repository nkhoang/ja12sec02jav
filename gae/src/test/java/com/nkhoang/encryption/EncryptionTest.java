package com.nkhoang.encryption;


import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration({"/applicationContext-service.xml"})

public class EncryptionTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionTest.class);
  private static final String ALGORITHM = "DSA";

  @Autowired
  private StandardPBEStringEncryptor propertyEncryptor;

  @Test
  public void testPropertyEncryptor() {
    LOGGER.info(propertyEncryptor.encrypt("POL"));
    String saltString = "digest password";
    //String algorithmString = "MD5";
    String algorithmString = "SHA-256";
    String iterationString = "1000";

    LOGGER.info(propertyEncryptor.decrypt("emGwruT+2OXLTyHskiBsXw=="));
    LOGGER.info(propertyEncryptor.encrypt(iterationString));
    LOGGER.info(propertyEncryptor.encrypt(algorithmString));
    LOGGER.info(propertyEncryptor.encrypt(saltString));
  }

  public StandardPBEStringEncryptor getPropertyEncryptor() {
    return propertyEncryptor;
  }

  public void setPropertyEncryptor(StandardPBEStringEncryptor propertyEncryptor) {
    this.propertyEncryptor = propertyEncryptor;
  }
}
