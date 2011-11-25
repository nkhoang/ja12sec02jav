package com.nkhoang.ws;

import com.nkhoang.gae.model.Word;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations = {"classpath:applicationContext-resources.xml"})
@RunWith(SpringJUnit4ClassRunner.class)


public class VocabularyRestServiceImplTest {
  @Autowired
  private RestTemplate template;

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
    final String fullUrl = "http://localhost:9999" + urlApp;

    Word w = template.execute(fullUrl, HttpMethod.GET,
        new RequestCallback() {
          public void doWithRequest(ClientHttpRequest request) throws IOException {
            HttpHeaders headers = request.getHeaders();

            headers.add("Accept", "application/xml");
            headers.add("ContentTyp,e", "application/xml");
          }
        }, responseExtractor);

    assertTrue(w != null);
  }


  public RestTemplate getTemplate() {
    return template;
  }

  public void setTemplate(RestTemplate template) {
    this.template = template;
  }
}
