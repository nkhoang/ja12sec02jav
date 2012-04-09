/**
 * Copyright HOANG K NGUYEN 2012.
 */
package com.nkhoang.model;

/**
 * Abstract Word Model.
 */
public class AbstractWord {
   private String soundSource;
   private String description;

   public String getSoundSource() {
      return soundSource;
   }

   public void setSoundSource(String soundSource) {
      this.soundSource = soundSource;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }
}
