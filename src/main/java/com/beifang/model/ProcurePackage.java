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
	// 1-平均值法, 2-最低价法
	private Integer priceStandardType = 1;
	// 平均值法时，百分之一价差扣减分数
	private Double higherDeduction = 1.0;
	private Double lowerDeduction = 0.8;
	
	private Long projectId;
	
	
	public Integer getPriceStandardType() {
		return priceStandardType;
	}
	public void setPriceStandardType(Integer priceStandardType) {
		this.priceStandardType = priceStandardType;
	}
	public Double getHigherDeduction() {
		return higherDeduction;
	}
	public void setHigherDeduction(Double higherDeduction) {
		this.higherDeduction = higherDeduction;
	}
	public Double getLowerDeduction() {
		return lowerDeduction;
	}
	public void setLowerDeduction(Double lowerDeduction) {
		this.lowerDeduction = lowerDeduction;
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
	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

}
