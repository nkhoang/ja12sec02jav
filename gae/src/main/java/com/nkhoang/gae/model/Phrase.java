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

}
