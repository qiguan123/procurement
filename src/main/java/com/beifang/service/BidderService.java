package com.beifang.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.beifang.common.PageResult;
import com.beifang.model.Bidder;
import com.beifang.repository.BidderRepository;
import com.beifang.service.dto.BidderDto;
import com.beifang.util.BeanCopier;

@Service
public class BidderService {
	@Autowired
	private BidderRepository bidderRepo;
	
	public void addBidders(List<Bidder> bidders) {
		bidderRepo.save(bidders);
	}

	public PageResult<Bidder> getPageByName(Integer page, Integer limit, String name) {
		Page<Bidder> searchResult = null;
		Pageable pageRequest = new PageRequest(page - 1, limit);
		if (name == null || name.isEmpty() || name.trim().isEmpty()) {
			searchResult = bidderRepo.findAll(pageRequest);
		} else {
			searchResult = bidderRepo.findByNameContaining(name, pageRequest);
		}
		return new PageResult<>(searchResult.getTotalElements(), searchResult.getContent());
	}

	public void saveBidder(Bidder bidder) {
		bidderRepo.save(bidder);
	}
	
	public List<BidderDto> getSimpleAll() {
		Iterable<Bidder> all = bidderRepo.findAll();
		return BeanCopier.copy(all, BidderDto.class);
	}
}
