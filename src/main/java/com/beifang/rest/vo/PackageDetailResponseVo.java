package com.beifang.rest.vo;

import java.util.ArrayList;
import java.util.List;

import com.beifang.model.Bidder;
import com.beifang.model.Expert;

public class PackageDetailResponseVo {
	private Long id;
	private String name;
	private String indexNumber;
	private Double bidUpperLimit;
	private String projectName;
	private List<Expert> experts = new ArrayList<>();
	private List<Bidder> bidders = new ArrayList<>();
	private List<BidFinalResultResponseVo> finalResult = new ArrayList<>();
	
	
	public List<Expert> getExperts() {
		return experts;
	}
	public void setExperts(List<Expert> experts) {
		this.experts = experts;
	}
	public List<Bidder> getBidders() {
		return bidders;
	}
	public void setBidders(List<Bidder> bidders) {
		this.bidders = bidders;
	}
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
	public List<BidFinalResultResponseVo> getFinalResult() {
		return finalResult;
	}
	public void setFinalResult(List<BidFinalResultResponseVo> finalResult) {
		this.finalResult = finalResult;
	}
	
}
