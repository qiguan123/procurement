package com.beifang.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.beifang.model.GradeItem;

public interface GradeItemRepository extends PagingAndSortingRepository<GradeItem, Long>{

	List<GradeItem> findByPackageIdIn(List<Long> pkgIds);
	List<GradeItem> findByPackageId(Long pkgId);

}
