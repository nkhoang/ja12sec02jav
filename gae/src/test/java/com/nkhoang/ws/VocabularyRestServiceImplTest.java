package com.nkhoang.ws;

import com.nkhoang.gae.exception.GAEException;
import com.nkhoang.gae.model.Word;
import com.nkhoang.gae.utils.WebUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.KeyPair;

import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations = {"classpath:applicationContext-resources.xml", "classpath:applicationContext-service.xml"})
@RunWith(SpringJUnit4ClassRunner.class)


public class VocabularyRestServiceImplTest {
  private static final Logger LOG = LoggerFactory.getLogger(VocabularyRestServiceImplTest.class.getCanonicalName());
  @Autowired
  private RestTemplate template;
  @Autowired
  private StandardPBEStringEncryptor propertyEncryptor;

  private HttpMessageConverterExtractor<Word> responseExtractor;
  private static ApacheCxfHttpServer server = null;


  @Before
  public void setUp() {
    responseExtractor = new HttpMessageConverterExtractor<Word>(Word.class, template.getMessageConverters());
  }

  @BeforeClass
  public static void setup() throws Exception {
    server = new ApacheCxfHttpServer("localhost", 9999);
    server.start();
  }


  @AfterClass
  public static void teardown() throws Exception {
    server.stop();
  }

  @Test
  public void shouldReturnCustomers() throws Exception {
    final String urlApp = "/services/vocabulary/search/help";
    final String fullUrl = "http://localhost:8080" + urlApp;

    Word w = template.execute(fullUrl, HttpMethod.GET,
        new RequestCallback() {
          public void doWithRequest(ClientHttpRequest request) throws IOException {
            HttpHeaders headers = request.getHeaders();

            headers.add("Accept", "*/*");
            // headers.add("Content-Type", "application/xml");
            try {
              headers.add("signature", WebUtils.createWSSignature("GAE"));
              String encryptedTime = propertyEncryptor.encrypt("" + System.currentTimeMillis());
              headers.add("key", encryptedTime);
            } catch (GAEException gaeEx) {
              LOG.debug(gaeEx.getMessage());
            }
          }
        }, responseExtractor);

    assertTrue(w != null);
  }

  @Test
  public void getKeys() throws Exception {
    KeyPair keypair = WebUtils.generateKeyPair();

    LOG.info(WebUtils.encodePrivateKey(keypair));
    LOG.info(WebUtils.encodePublicKey(keypair));
  }


  public RestTemplate getTemplate() {
    return template;
  }

  public void setTemplate(RestTemplate template) {
    this.template = template;
  }

  public StandardPBEStringEncryptor getPropertyEncryptor() {
    return propertyEncryptor;
  }

  public void setPropertyEncryptor(StandardPBEStringEncryptor propertyEncryptor) {
    this.propertyEncryptor = propertyEncryptor;
  }
}
