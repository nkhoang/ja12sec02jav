package com.nkhoang.gae.model;

import com.google.gdata.data.dublincore.Title;

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
	private List<Long> senseIds = new ArrayList<Long>(0);
	@Transient
	private List<Sense> senseList = new ArrayList<Sense>(0);


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

	public List<Long> getSenseIds() {
		return senseIds;
	}

	public void setSenseIds(List<Long> senseIds) {
		this.senseIds = senseIds;
	}

	public List<Sense> getSenseList() {
		return senseList;
	}

	public void setSenseList(List<Sense> senseList) {
		this.senseList = senseList;
	}
}
