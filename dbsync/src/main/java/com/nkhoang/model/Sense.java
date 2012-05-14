package com.nkhoang.model;

import java.util.ArrayList;
import java.util.List;

public class Sense {
  private String wordForm;
  private String grammarGroup;
  private String languageGroup;
  private String definition;
  private String kind;
  private List<Meaning> subSenses = new ArrayList<Meaning>(0);
  private List<String> examples = new ArrayList<String>(0);
  private Long key;

  public Long getKey() {
    return key;
  }

  public void setKey(Long value) {
    key = value;
  }


  public String getWordForm() {
    return wordForm;
  }


  public String getGrammarGroup() {
    return grammarGroup;
  }


  public String getLanguageGroup() {
    return languageGroup;
  }


  public String getDefinition() {
    return definition;
  }


  public List<Meaning> getSubSenses() {
    return subSenses;
  }

  public void setSubSenses(List<Meaning> subSenses) {
    this.subSenses = subSenses;
  }

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
