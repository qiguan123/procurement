package com.beifang.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class PackageBidderRelation {
	@Id @GeneratedValue
	private Long id;
	private Long packageId;
	private Long bidderId;
	private Double meanScore;
	
	public PackageBidderRelation() {
		super();
	}
	public PackageBidderRelation(Long packageId, Long bidderId) {
		super();
		this.packageId = packageId;
		this.bidderId = bidderId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getPackageId() {
		return packageId;
	}
	public void setPackageId(Long packageId) {
		this.packageId = packageId;
	}
	public Long getBidderId() {
		return bidderId;
	}
	public void setBidderId(Long bidderId) {
		this.bidderId = bidderId;
	}
	public Double getMeanScore() {
		return meanScore;
	}
	public void setMeanScore(Double meanScore) {
		this.meanScore = meanScore;
	}
	
}
