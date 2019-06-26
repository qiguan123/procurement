package com.beifang.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beifang.model.Bidder;
import com.beifang.repository.BidderRepository;

@Service
public class BidderService {
	@Autowired
	private BidderRepository bidderRepo;
	
	public void addBidders(List<Bidder> bidders) {
		bidderRepo.save(bidders);
	}
}
