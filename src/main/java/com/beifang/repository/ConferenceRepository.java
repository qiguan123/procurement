package com.beifang.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.beifang.model.Conference;

public interface ConferenceRepository extends PagingAndSortingRepository<Conference, Long> {

	List<Conference> findByState(Integer state);

	@Modifying
	@Query("update Conference c set c.state = ?2 where c.id = ?1")
	int setState(Long cfrsId, int state);

	Page<Conference> findByNameContaining(String name, Pageable pageable);
}
