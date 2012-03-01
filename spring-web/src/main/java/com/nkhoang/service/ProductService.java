package com.nkhoang.service;

import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.flex.remoting.RemotingInclude;
import org.springframework.stereotype.Service;
import com.nkhoang.model.Product;

@Service("productService")
@RemotingDestination(channels = "my-amf")
public class ProductService {

   @RemotingInclude
   public Product getProduct() {
      Product product = new Product();
      product.setName("Product 1");
      product.setType("Product Type 1");

      return product;
   }

   @RemotingInclude
   public Product getProduct(String productName) {
      Product product = new Product();
      product.setName("Product " + productName);
      product.setType("Product Type 1");
      product.setType("Product Type 1");

      return product;
   }
}
