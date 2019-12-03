package com.beifang.service.dto;

import java.util.ArrayList;
import java.util.List;

import com.beifang.model.BidPrice;
import com.beifang.model.Bidder;
import com.beifang.model.Expert;
import com.beifang.model.PackageBidderRelation;
import com.beifang.model.PackageExpertRelation;

public class PackageDto {
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
	
	private String projectName;
	
	private List<Expert> experts = new ArrayList<>();
	private List<Bidder> bidders = new ArrayList<>();
	private List<PackageExpertRelation> expertRels = new ArrayList<>();
	private List<PackageBidderRelation> bidderRels = new ArrayList<>();
	
	private List<Long> bidderIds = new ArrayList<>();
	private List<Long> expertIds = new ArrayList<>();
	
	private List<BidPrice> bidPrices = new ArrayList<>();
	
	private List<GradeItemDto> allItems = new ArrayList<>();
	private GradeItemDto priceItem;
	private GradeItemDto totalItem;
	
	private List<BidFinalResultDto> finalResult = new ArrayList<>();
	
	
	
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
	public List<PackageBidderRelation> getBidderRels() {
		return bidderRels;
	}
	public void setBidderRels(List<PackageBidderRelation> bidderRels) {
		this.bidderRels = bidderRels;
	}
	public List<BidFinalResultDto> getFinalResult() {
		return finalResult;
	}
	public void setFinalResult(List<BidFinalResultDto> finalResult) {
		this.finalResult = finalResult;
	}
	public List<PackageExpertRelation> getExpertRels() {
		return expertRels;
	}
	public void setExpertRels(List<PackageExpertRelation> expertRels) {
		this.expertRels = expertRels;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
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
	public List<BidPrice> getBidPrices() {
		return bidPrices;
	}
	public void setBidPrices(List<BidPrice> bidPrices) {
		this.bidPrices = bidPrices;
	}
	public List<GradeItemDto> getAllItems() {
		return allItems;
	}
	public void setAllItems(List<GradeItemDto> allItems) {
		this.allItems = allItems;
	}
	public GradeItemDto getTotalItem() {
		return totalItem;
	}
	public void setTotalItem(GradeItemDto totalItem) {
		this.totalItem = totalItem;
	}
	public GradeItemDto getPriceItem() {
		return priceItem;
	}
	public void setPriceItem(GradeItemDto priceItem) {
		this.priceItem = priceItem;
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
	public List<Bidder> getBidders() {
		return bidders;
	}
	public void setBidders(List<Bidder> bidders) {
		this.bidders = bidders;
	}
	public List<Expert> getExperts() {
		return experts;
	}
	public void setExperts(List<Expert> experts) {
		this.experts = experts;
	}
}
