package com.beifang.rest.vo;

import java.util.ArrayList;
import java.util.List;

public class BidFinalResultResponseVo {
	private String bidderName;
	private String bidPrice;
	private List<String> expertScores = new ArrayList<>();
	
	public String getBidderName() {
		return bidderName;
	}
	public void setBidderName(String bidderName) {
		this.bidderName = bidderName;
	}
	public String getBidPrice() {
		return bidPrice;
	}
	public void setBidPrice(String bidPrice) {
		this.bidPrice = bidPrice;
	}
	public List<String> getExpertScores() {
		return expertScores;
	}
	public void setExpertScores(List<String> expertScores) {
		this.expertScores = expertScores;
	}
}
