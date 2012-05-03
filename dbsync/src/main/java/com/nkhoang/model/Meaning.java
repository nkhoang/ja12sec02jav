package com.nkhoang.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "IMeaning")
@Table(name = "Meaning")
public class Meaning implements IMeaning {
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
   private Long key;

   // no default constructor.
   public Meaning() {

   }

   public void setKey(Long value) {
      key = value;
   }

   @Column(name = IMeaning.ID)
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   public Long getKey() {
      return key;
   }

   public void addExample(String example) {
      this.examples.add(example);
   }

   @ElementCollection
   @CollectionTable(name = "MEANING_EXAMPLES", joinColumns = @JoinColumn(name = IMeaning.ID))
   @Column(name = IMeaning.EXAMPLE)
   public List<String> getExamples() {
      return this.examples;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Meaning: [id=");
      sb.append(", languageGroup=");
      sb.append(languageGroup);
      sb.append(", grammarGroup=");
      sb.append(grammarGroup);
      sb.append(", content=");
      sb.append(content);
      sb.append(", examples=");
      sb.append(examples.toArray());

      return sb.toString();
   }

   @Column(name = IMeaning.CONTENT)
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

   @Transient
   public String getKind() {
      return kind;
   }

   @Transient
   public String getType() {
      return type;
   }

   public void setType(String type) {
      this.type = type;
   }

   @Column(name = IMeaning.GRAMMAR_GROUP)
   public String getGrammarGroup() {
      return grammarGroup;
   }

   public void setGrammarGroup(String grammarGroup) {
      this.grammarGroup = grammarGroup;
   }

   @Column(name = IMeaning.LANGUAGE_GROUP)
   public String getLanguageGroup() {
      return languageGroup;
   }

   public void setLanguageGroup(String languageGroup) {
      this.languageGroup = languageGroup;
   }

   @Column(name = IMeaning.WORD_FORM)
   public String getWordForm() {
      return wordForm;
   }

   public void setWordForm(String wordForm) {
      this.wordForm = wordForm;
   }

}
