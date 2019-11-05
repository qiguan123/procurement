package com.beifang.rest.vo;

public class StatisticResponseVo {
	private int cfrsCount;
	private int projectCount;
	private int pkgCount;
	private String bidUpperLimit;
	public int getCfrsCount() {
		return cfrsCount;
	}
	public void setCfrsCount(int cfrsCount) {
		this.cfrsCount = cfrsCount;
	}
	public int getProjectCount() {
		return projectCount;
	}
	public void setProjectCount(int projectCount) {
		this.projectCount = projectCount;
	}
	public int getPkgCount() {
		return pkgCount;
	}
	public void setPkgCount(int pkgCount) {
		this.pkgCount = pkgCount;
	}
	public String getBidUpperLimit() {
		return bidUpperLimit;
	}
	public void setBidUpperLimit(String bidUpperLimit) {
		this.bidUpperLimit = bidUpperLimit;
	}
	
}
