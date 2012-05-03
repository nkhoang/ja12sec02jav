package com.nkhoang.model;

import org.hibernate.annotations.ForeignKey;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "ISense")
@Table(name = "Sense")
public class Sense implements ISense {
   private String wordForm;
   private String grammarGroup;
   private String languageGroup;
   private String definition;
   private String kind;
   private List<Meaning> subSenses = new ArrayList<Meaning>(0);
   private List<String> examples = new ArrayList<String>(0);
   private Long key;

   @Column(name = ISense.ID)
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   public Long getKey() {
      return key;
   }

   public void setKey(Long value) {
      key = value;
   }


   @Column(name = ISense.WORD_FORM)
   public String getWordForm() {
      return wordForm;
   }


   @Column(name = ISense.GRAMMAR_GROUP)
   public String getGrammarGroup() {
      return grammarGroup;
   }


   @Column(name = ISense.LANGUAGE_GROUP)
   public String getLanguageGroup() {
      return languageGroup;
   }


   @Column(name = ISense.CONTENT)
   public String getDefinition() {
      return definition;
   }


   @OneToMany(targetEntity = Meaning.class, fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
   @JoinColumn(name = ISense.ID, referencedColumnName = ISense.ID, nullable = true)
   @ForeignKey(name = "FK_SENSE_MEANINGS")
   public List<Meaning> getSubSenses() {
      return subSenses;
   }

   public void setSubSenses(List<Meaning> subSenses) {
      this.subSenses = subSenses;
   }

   @ElementCollection()
   @CollectionTable(name = "SENSE_EXAMPLES", joinColumns = @JoinColumn(name = ISense.ID))
   @Column(name = ISense.EXAMPLE)
   public List<String> getExamples() {
      return examples;
   }

   public void setExamples(List<String> examples) {
      this.examples = examples;
   }

   public String getKind() {
      return kind;
   }

   public void setKind(String kind) {
      this.kind = kind;
   }


   public void setWordForm(String wordForm) {
      this.wordForm = wordForm;
   }

   public void setGrammarGroup(String grammarGroup) {
      this.grammarGroup = grammarGroup;
   }

   public void setLanguageGroup(String languageGroup) {
      this.languageGroup = languageGroup;
   }

   public void setDefinition(String definition) {
      this.definition = definition;
   }
}
