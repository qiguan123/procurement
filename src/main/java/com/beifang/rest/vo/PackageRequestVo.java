package com.beifang.rest.vo;

import java.util.ArrayList;
import java.util.List;

public class PackageRequestVo {
	private Long id;

	private String name;
	private String indexNumber;
	private Double bidUpperLimit;
	
	private Long projectId;
	private List<Long> bidderIds = new ArrayList<>();
	private List<Long> expertIds = new ArrayList<>();
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
	public List<Long> getBidderIds() {
		return bidderIds;
	}
	public void setBidderIds(List<Long> bidderIds) {
		this.bidderIds = bidderIds;
	}
	public List<Long> getExpertIds() {
		return expertIds;
	}
	public void setExpertIds(List<Long> expertIds) {
		this.expertIds = expertIds;
	}
}
