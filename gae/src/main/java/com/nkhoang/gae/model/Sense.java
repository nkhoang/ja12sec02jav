package com.nkhoang.gae.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@SuppressWarnings({"JpaAttributeTypeInspection"})
public class Sense {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Basic
    private String wordForm;
    @Basic
    private String grammarGroup;
    @Basic
    private String languageGroup;
    @Basic
    private String definition;
    @Basic
    private String kind;
    @Transient
    private List<Meaning> subSenses = new ArrayList<Meaning>(0);
    @Basic
    private List<String> examples = new ArrayList<String>(0);
    @Basic
    private List<Long> subSenseIds = new ArrayList<Long>(0);


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWordForm() {
        return wordForm;
    }

    public void setWordForm(String wordForm) {
        this.wordForm = wordForm;
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

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
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

    public List<Long> getSubSenseIds() {
        return subSenseIds;
    }

    public void setSubSenseIds(List<Long> subSenseIds) {
        this.subSenseIds = subSenseIds;
    }
}
