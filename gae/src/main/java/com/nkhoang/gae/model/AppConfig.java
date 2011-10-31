package com.nkhoang.gae.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class AppConfig {
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE)
   private Long id;
   @Basic
   private String label;
   @Basic
   private List<String> values;

   public static final String[] SKIP_FIELDS = {"jdoDetachedState"};


   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public List<String> getValues() {
      return values;
   }

   public void setValues(List<String> values) {
      this.values = values;
   }
}
