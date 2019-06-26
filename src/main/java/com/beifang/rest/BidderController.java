package com.beifang.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.beifang.model.Bidder;
import com.beifang.service.BidderService;

@RestController
@RequestMapping("/bidder")
public class BidderController {
	@Autowired
	private BidderService bidderService;
	
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public String addBidders(@RequestBody List<Bidder> bidders) {
		bidderService.addBidders(bidders);
		return "ok";
	}
}
