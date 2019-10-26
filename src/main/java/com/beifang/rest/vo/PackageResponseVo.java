package com.beifang.rest.vo;

import java.util.ArrayList;
import java.util.List;

public class PackageResponseVo {
	private Long id;

	private String name;
	private String indexNumber;
	private Double bidUpperLimit;
	
	private String projectName;
	private List<String> bidderNames = new ArrayList<>();
	private List<String> expertNames = new ArrayList<>();
	
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
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public List<String> getBidderNames() {
		return bidderNames;
	}
	public void setBidderNames(List<String> bidderNames) {
		this.bidderNames = bidderNames;
	}
	public List<String> getExpertNames() {
		return expertNames;
	}
	public void setExpertNames(List<String> expertNames) {
		this.expertNames = expertNames;
	}
	
}
