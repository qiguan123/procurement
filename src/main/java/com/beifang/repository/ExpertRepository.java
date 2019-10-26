package com.beifang.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.beifang.model.Expert;

public interface ExpertRepository extends PagingAndSortingRepository<Expert, Long> {

	List<Expert> findByName(String expertName);

	Page<Expert> findByNameContaining(String name, Pageable pageRequest);
}
