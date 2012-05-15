package com.nkhoang.model.dictionary.impl;

import com.nkhoang.model.dictionary.IDictionary;
import com.nkhoang.model.dictionary.ISound;
import com.nkhoang.model.dictionary.IWord;
import org.joda.time.DateTime;

public class WordImpl implements IWord {
   private Long id;
   private String word;
   private String data;
   private IDictionary dictionary;
   private ISound sound;
   private DateTime creationDate;
   private DateTime modificationDate;

   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("[id=");
      sb.append(id);
      sb.append(", dictionary=");
      sb.append(dictionary);
      sb.append(", word=");
      sb.append(word);
      sb.append(", creationDate=");
      sb.append(creationDate);
      sb.append(", modificationDate=");
      sb.append(modificationDate);
      sb.append("]");

      return sb.toString();
   }

   public Long getKey() {
      return id;
   }


   public void setKey(Long id) {
      this.id = id;
   }

   public IDictionary getDictionary() {
      return dictionary;
   }

   public void setDictionary(IDictionary dictionary) {
      this.dictionary = dictionary;
   }

   public String getData() {
      return data;
   }

   public void setData(String data) {
      this.data = data;
   }

   public DateTime getCreationDate() {
      return creationDate;
   }

   public void setCreationDate(DateTime value) {
      this.creationDate = value;
   }

   public DateTime getModificationDate() {
      return modificationDate;
   }

   public void setModificationDate(DateTime modificationDate) {
      this.modificationDate = modificationDate;
   }

   public String getWord() {
      return word;
   }

   public void setWord(String word) {
      this.word = word;
   }

   public ISound getSound() {
      return sound;
   }

   public void setSound(ISound sound) {
      this.sound = sound;
   }
}
