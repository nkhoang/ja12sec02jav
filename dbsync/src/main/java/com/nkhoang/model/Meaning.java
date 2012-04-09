package com.nkhoang.model;

import java.util.ArrayList;
import java.util.List;

public class Meaning {
   private String id;
   private String content;
   // based on oxford dictionary.
   // for example: mass noun...
   private String grammarGroup;
   // for example: informal, formal...
   private String languageGroup;
   private List<String> examples = new ArrayList<String>(0);
   private String kind;
   private String wordForm;
   private String type;

   // no default constructor.
   public Meaning() {

   }

   public void addExample(String example) {
      this.examples.add(example);
   }

   public List<String> getExamples() {
      return this.examples;
   }

   @Override
   public String toString() {
      return content + "\n" + examples.toString();
   }

   public String getContent() {
      return content;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public void setExamples(List<String> examples) {
      this.examples = examples;
   }


   public void setKind(String kind) {
      this.kind = kind;
   }

   public String getKind() {
      return kind;
   }

   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public String getGrammarGroup() {
      return grammarGroup;
   }

   public void setGrammarGroup(String grammarGroup) {
      this.grammarGroup = grammarGroup;
   }

   public String getLanguageGroup() {
      return languageGroup;
   }

   public void setLanguageGroup(String languageGroup) {
      this.languageGroup = languageGroup;
   }

   public String getWordForm() {
      return wordForm;
   }

   public void setWordForm(String wordForm) {
      this.wordForm = wordForm;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }
}
