package com.beifang.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.beifang.model.Bidder;

public interface BidderRepository extends PagingAndSortingRepository<Bidder, Long>{

	Page<Bidder> findByNameContaining(String name, Pageable pageRequest);

}
