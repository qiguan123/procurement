package com.beifang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.beifang.model.PackageExpertRelation;

public interface PackageExpertRelRepository extends PagingAndSortingRepository<PackageExpertRelation, Long>{

	List<PackageExpertRelation> findByPackageIdIn(List<Long> pkgIds);

	@Query("update PackageExpertRelation r set r.modifiable = 0 where r.packageId=?1 and r.expertId=?2")
	@Modifying
	int setUnmodifiable(Long pkgId, Long expertId);

}
