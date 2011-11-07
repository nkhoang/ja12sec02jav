package com.nkhoang.gae.model;

import com.google.appengine.api.datastore.Text;

import javax.persistence.*;

@Entity
public class WordEntity {
   @Id
   @GeneratedValue(strategy = GenerationType.SEQUENCE)
   private Long id;
   @Basic
   private String word;
   @Basic
   private String dictType;
   @Lob
   private Text wordJSON;
   @Basic
   private Long timeStamp;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getWord() {
      return word;
   }

   public void setWord(String word) {
      this.word = word;
   }

   public Text getWordJSON() {
      return wordJSON;
   }

   public void setWordJSON(Text wordJSON) {
      this.wordJSON = wordJSON;
   }

   public String getDictType() {
      return dictType;
   }

   public void setDictType(String dictType) {
      this.dictType = dictType;
   }

   public Long getTimeStamp() {
      return timeStamp;
   }

   public void setTimeStamp(Long timeStamp) {
      this.timeStamp = timeStamp;
   }
}
