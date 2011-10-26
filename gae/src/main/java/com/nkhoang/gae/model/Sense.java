package com.nkhoang.gae.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@SuppressWarnings({"JpaAttributeTypeInspection"})
public class Sense {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long   id;
	@Basic
	private String wordForm;
	@Basic
	private String grammarGroup;
	@Basic
	private String languageGroup;
	@Basic
	private String definition;
	@Basic
	private List<Long> subSenses = new ArrayList<Long>(0);


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

	public List<Long> getSubSenses() {
		return subSenses;
	}

	public void setSubSenses(List<Long> subSenses) {
		this.subSenses = subSenses;
	}
}
