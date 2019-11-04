package com.beifang.rest.vo;

import java.util.ArrayList;
import java.util.List;

/**
 *投标人按照id由小到大排序
 */
public class GradeItemResponseVo {
	private Long id;
	private String category;
	// 1:技术, 2:商务, 3:价格, 4:总分
	private Integer categoryId;
	private String name;
	private String content;
	private String standard;
	private Double maxValue;
	//评审依据的文件名，以逗号分隔
	private String relateFilesName;
	
	private List<String> bidderScores = new ArrayList<>();

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

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
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

	public String getRelateFilesName() {
		return relateFilesName;
	}

	public void setRelateFilesName(String relateFilesName) {
		this.relateFilesName = relateFilesName;
	}

	public List<String> getBidderScores() {
		return bidderScores;
	}

	public void setBidderScores(List<String> bidderScores) {
		this.bidderScores = bidderScores;
	}
	
}
