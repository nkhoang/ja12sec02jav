package com.nkhoang.gae.model;

import javax.persistence.*;

/** Keep the statistics of word item lookup process. */
@Entity
public class WordItemStat {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long   id;
	@Basic
	private String errorLog;
	@Basic
	private int    index;
	@Basic
	private int    failedCount;
	@Basic
	private int    successCount;
	@Basic
	private int    processTime;

	public String getErrorLog() {
		return errorLog;
	}

	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getFailedCount() {
		return failedCount;
	}

	public void setFailedCount(int failedCount) {
		this.failedCount = failedCount;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	public int getProcessTime() {
		return processTime;
	}

	public void setProcessTime(int processTime) {
		this.processTime = processTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
