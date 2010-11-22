package com.nkhoang.gae.test;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
// specifies the Spring configuration to load for this test fixture
@ContextConfiguration({"/applicationContext-service.xml"})
public class PasswordGeneratorTest {
    @Autowired
    private StandardPBEStringEncryptor propertyEncryptor;

    @Test
    public void testRun() {
    }


    public void testEncryptor() {
        System.out.println(propertyEncryptor.encrypt("me27&ml17"));
    }


    public void testDecryptor() {
        System.out.println(propertyEncryptor.decrypt("emGwruT+2OU08aRDXqMTP412sxFxTh3p"));
    }

    public void setPropertyEncryptor(StandardPBEStringEncryptor propertyEncryptor) {
        this.propertyEncryptor = propertyEncryptor;
    }

    public StandardPBEStringEncryptor getPropertyEncryptor() {
        return propertyEncryptor;
    }

}
