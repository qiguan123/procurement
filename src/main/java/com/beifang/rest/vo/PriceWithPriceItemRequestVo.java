package com.beifang.rest.vo;

import java.util.ArrayList;
import java.util.List;

import com.beifang.model.BidPrice;

/**
 * 包的投标价格
 */
public class PriceWithPriceItemRequestVo {
	private List<BidPrice> prices = new ArrayList<>();
	public List<BidPrice> getPrices() {
		return prices;
	}
	public void setPrices(List<BidPrice> prices) {
		this.prices = prices;
	}
}
