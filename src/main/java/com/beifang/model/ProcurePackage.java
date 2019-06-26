package com.beifang.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ProcurePackage {
	@Id @GeneratedValue
	private Long id;

	private String name;
	private String indexNumber;
	private Double bidUpperLimit;
	
	private Long projectId;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIndexNumber() {
		return indexNumber;
	}
	public void setIndexNumber(String indexNumber) {
		this.indexNumber = indexNumber;
	}
	public Double getBidUpperLimit() {
		return bidUpperLimit;
	}
	public void setBidUpperLimit(Double bidUpperLimit) {
		this.bidUpperLimit = bidUpperLimit;
	}
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

}
