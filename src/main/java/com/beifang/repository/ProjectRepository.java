package com.beifang.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.beifang.model.Project;

public interface ProjectRepository extends PagingAndSortingRepository<Project, Long> {

	List<Project> findByCfrsId(Long cfrsId);

	Page<Project> findByNameContaining(String name, Pageable pageRequest);

}
