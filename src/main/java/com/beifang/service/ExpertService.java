package com.beifang.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.beifang.common.PageResult;
import com.beifang.model.Expert;
import com.beifang.repository.ExpertRepository;
import com.beifang.service.dto.ExpertDto;
import com.beifang.util.BeanCopier;
import com.beifang.util.ListUtil;

@Service
public class ExpertService {
	@Autowired
	private ExpertRepository expertRepo;
	
	public void addExperts(List<Expert> experts) {
		if (ListUtil.isEmpty(experts)) {
			return;
		}
		expertRepo.save(experts);
	}

	public PageResult<Expert> getPageByName(Integer page, Integer limit, String name) {
		Page<Expert> searchResult = null;
		Pageable pageRequest = new PageRequest(page - 1, limit);
		if (name == null || name.isEmpty() || name.trim().isEmpty()) {
			searchResult = expertRepo.findAll(pageRequest);
		} else {
			searchResult = expertRepo.findByNameContaining(name, pageRequest);
		}
		return new PageResult<>(searchResult.getTotalElements(), searchResult.getContent());
	}

	public void saveExpert(Expert expert) {
		if (expert != null) {
			expertRepo.save(expert);
		}
	}

	public List<ExpertDto> getSimpleAll() {
		Iterable<Expert> experts = expertRepo.findAll();
		return BeanCopier.copy(experts, ExpertDto.class);
	}
	
}
