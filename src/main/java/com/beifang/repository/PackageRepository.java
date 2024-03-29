package com.beifang.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.beifang.model.ProcurePackage;

public interface PackageRepository extends PagingAndSortingRepository<ProcurePackage, Long> {

	List<ProcurePackage> findByProjectIdIn(List<Long> projectIds);
	Page<ProcurePackage> findByNameContaining(String name, Pageable pageable);
}
