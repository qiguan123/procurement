package com.beifang.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.beifang.model.BidPrice;

public interface BidPriceRepository extends CrudRepository<BidPrice, Long> {

	List<BidPrice> findByPackageIdIn(List<Long> pkgIds);

}
