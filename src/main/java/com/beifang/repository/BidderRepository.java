package com.beifang.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.beifang.model.Bidder;

public interface BidderRepository extends PagingAndSortingRepository<Bidder, Long>{

}
