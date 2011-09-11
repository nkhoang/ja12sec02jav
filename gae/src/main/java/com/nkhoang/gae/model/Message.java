package com.nkhoang.gae.model;

import javax.persistence.*;

@Entity
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long   id;
	@Basic
	private Long   time;
	@Basic
	private String message;
	@Basic
	private int    categoryId;

  public static final String SKIP_FIELDS[] = {"jdoDetachedState"};


	public Message(int categoryId, String message) {
		this.message = message;
		this.categoryId = categoryId;
		// set time to the creation time.
		this.time = System.currentTimeMillis();
	}

	public static int VOCABULARY_CATEGORY = 1;

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getTime() {
		return time;
	}

	public void setTime(Long time) {
		this.time = time;
	}

	public String toString() {
		return message;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
