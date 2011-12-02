//
// Copyright (c) 2011, Health Market Science, Inc.
//
package com.nkhoang.ws;

import com.nkhoang.gae.ws.VocabularyRESTServiceImpl;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ApacheCxfHttpServer.
 *
 * @author rnambiar
 */
public class ApacheCxfHttpServer {
  JAXRSServerFactoryBean sf = null;
  Server _server = null;
  private static Logger LOG = LoggerFactory.getLogger(ApacheCxfHttpServer.class
      .getCanonicalName());


  public static void main(String args[]) throws Exception {
    ApacheCxfHttpServer server = new ApacheCxfHttpServer("localhost", 8080);
    server.start();
  }


  public ApacheCxfHttpServer(String host, int port) throws Exception {
    if (LOG.isDebugEnabled()) {
      LOG.debug("GAE REST ApacheCxfHttpServer Service initializing for host ["
          + host + "] and port [" + port + "]");
    }

    sf = new JAXRSServerFactoryBean();
    sf.setResourceClasses(VocabularyRESTServiceImpl.class);
    sf.setResourceProvider(VocabularyRESTServiceImpl.class,
        new SingletonResourceProvider(new VocabularyRESTServiceImpl()));
    sf.setAddress("http://" + host + ":" + port + "/");
  }


  public void start() {
    try {
      LOG.debug("GAE REST ApacheCxfHttpServer Service starting....");

      _server = sf.create();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  public void stop() {
    _server.destroy();
  }
}
