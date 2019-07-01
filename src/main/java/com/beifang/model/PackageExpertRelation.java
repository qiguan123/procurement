package com.beifang.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class PackageExpertRelation {
	@Id @GeneratedValue
	private Long id;
	private Long packageId;
	private Long expertId;
	
	private int modifiable = 1;
	private Long modifyTime;
	
	public PackageExpertRelation() {
		super();
	}
	public PackageExpertRelation(Long packageId, Long expertId) {
		super();
		this.packageId = packageId;
		this.expertId = expertId;
	}
	
	public Long getModifyTime() {
		return modifyTime;
	}
	public void setModifyTime(Long modifyTime) {
		this.modifyTime = modifyTime;
	}
	public int getModifiable() {
		return modifiable;
	}
	public void setModifiable(int modifiable) {
		this.modifiable = modifiable;
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
	public Long getExpertId() {
		return expertId;
	}
	public void setExpertId(Long expertId) {
		this.expertId = expertId;
	}
	
}
