package com.beifang.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.beifang.model.ItemScore;

public interface ItemScoreRepository extends CrudRepository<ItemScore, Long>{

	List<ItemScore> findByItemIdIn(List<Long> itemIds);

}
