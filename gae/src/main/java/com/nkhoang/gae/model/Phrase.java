package com.nkhoang.gae.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@SuppressWarnings({"JpaAttributeTypeInspection"})
public class Phrase {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	@Basic
	private String description;
	@Basic
	private List<Long> subSenses = new ArrayList<Long>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getSubSenses() {
        return subSenses;
    }

    public void setSubSenses(List<Long> subSenses) {
        this.subSenses = subSenses;
    }
}
