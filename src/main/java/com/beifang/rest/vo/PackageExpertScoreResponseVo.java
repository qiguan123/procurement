package com.beifang.rest.vo;

import java.util.ArrayList;
import java.util.List;

import com.beifang.model.Bidder;

/**
 * 一个专家对投标厂家的评分，投标人按id由小到大排序
 */
public class PackageExpertScoreResponseVo {
	private String pkgName;
	private String expertName;
	private List<Bidder> bidders = new ArrayList<>();
	private List<GradeItemResponseVo> allItems = new ArrayList<>();
	
	public String getPkgName() {
		return pkgName;
	}
	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}
	public List<Bidder> getBidders() {
		return bidders;
	}
	public void setBidders(List<Bidder> bidders) {
		this.bidders = bidders;
	}
	public List<GradeItemResponseVo> getAllItems() {
		return allItems;
	}
	public void setAllItems(List<GradeItemResponseVo> allItems) {
		this.allItems = allItems;
	}
	public String getExpertName() {
		return expertName;
	}
	public void setExpertName(String expertName) {
		this.expertName = expertName;
	}
	
}
