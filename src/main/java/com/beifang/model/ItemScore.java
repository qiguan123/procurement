package com.beifang.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ItemScore {
	@Id @GeneratedValue
	private Long id;
	
	private Double score;
	
	private Long expertId;
	private Long bidderId;
	private Long itemId;
	
	public ItemScore(Long itemId, Long expertId, Long bidderId) {
		super();
		this.expertId = expertId;
		this.bidderId = bidderId;
		this.itemId = itemId;
	}

	public ItemScore() {
		super();
	}

	public Long getExpertId() {
		return expertId;
	}

	public void setExpertId(Long expertId) {
		this.expertId = expertId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public Long getBidderId() {
		return bidderId;
	}

	public void setBidderId(Long bidderId) {
		this.bidderId = bidderId;
	}

	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}
	
}
