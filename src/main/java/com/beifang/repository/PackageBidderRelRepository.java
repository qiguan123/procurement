package com.beifang.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.beifang.model.PackageBidderRelation;

public interface PackageBidderRelRepository extends PagingAndSortingRepository<PackageBidderRelation, Long>{

	List<PackageBidderRelation> findByPackageIdIn(List<Long> pkgIds);

}
