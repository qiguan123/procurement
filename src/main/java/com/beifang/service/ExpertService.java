package com.beifang.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beifang.model.Expert;
import com.beifang.repository.ExpertRepository;
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
	
}
