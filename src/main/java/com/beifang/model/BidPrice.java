package com.beifang.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class BidPrice {
	@Id @GeneratedValue
	private Long id;
	
	private Long bidderId;
	private Long packageId;
	private Double price;
	
	public BidPrice(Long bidderId, Long packageId) {
		super();
		this.bidderId = bidderId;
		this.packageId = packageId;
	}
	public BidPrice() {
		super();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getBidderId() {
		return bidderId;
	}
	public void setBidderId(Long bidderId) {
		this.bidderId = bidderId;
	}
	public Long getPackageId() {
		return packageId;
	}
	public void setPackageId(Long packageId) {
		this.packageId = packageId;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
}
