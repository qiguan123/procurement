package com.beifang.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class GradeItem {
	@Id @GeneratedValue
	private Long id;
	
	private String category;
	//1:技术, 2:商务, 3:价格, 4:总分
	private Integer categoryId;
	
	private String name;
	private String content;
	private String standard;
	private Double maxValue;
	//评审依据的文件名，以逗号分隔
	private String relateFilesName;
	
	private Long packageId; 
	
	
	public String getRelateFilesName() {
		return relateFilesName;
	}
	public void setRelateFilesName(String relateFilesName) {
		this.relateFilesName = relateFilesName;
	}
	public Integer getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
	
	public Long getPackageId() {
		return packageId;
	}
	public void setPackageId(Long packageId) {
		this.packageId = packageId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getStandard() {
		return standard;
	}
	public void setStandard(String standard) {
		this.standard = standard;
	}
	public Double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}
	
}
