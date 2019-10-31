package com.beifang.service.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * 专家打分按照专家id由小到大排序
 */
public class BidFinalResultDto {
	private String bidderName;
	private String bidPrice;
	private String finalScore;
	private List<String> expertScores = new ArrayList<>();
	
	
	public String getFinalScore() {
		return finalScore;
	}
	public void setFinalScore(String finalScore) {
		this.finalScore = finalScore;
	}
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
