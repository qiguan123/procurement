package com.beifang.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beifang.model.Conference;
import com.beifang.model.Expert;
import com.beifang.repository.ConferenceRepository;
import com.beifang.repository.ExpertRepository;
import com.beifang.service.dto.ConferenceDto;
import com.beifang.service.dto.PackageDto;
import com.beifang.util.BeanCopier;
import com.beifang.util.ListUtil;
import com.beifang.util.func.ExpertGetIdFunction;

@Service
public class ConferenceService {
	@Autowired
	private ConferenceRepository cfrsRepo;
	@Autowired
	private ExpertRepository expertRepo;
	@Autowired
	private PackageService pkgService;
	
	public List<ConferenceDto> getAllByState(Integer state) {
		List<Conference> conferenceList = cfrsRepo.findByState(state);
		if (ListUtil.isEmpty(conferenceList)) {
			return new ArrayList<>();
		}
		return BeanCopier.copy(conferenceList, ConferenceDto.class);
	}

	public ConferenceDto add(ConferenceDto cfrsDto) {
		Conference cfrs = BeanCopier.copy(cfrsDto, Conference.class);
		//新建状态
		cfrs.setState(0);
		cfrs = cfrsRepo.save(cfrs);
		
		return BeanCopier.copy(cfrs, ConferenceDto.class);
	}

	@Transactional
	public void updateState(Long cfrsId, int state) {
		cfrsRepo.setState(cfrsId, state);
	}
	
	public ConferenceDto getOngoingCfrs() {
		List<ConferenceDto> cfrsList = getAllByState(2);
		if (ListUtil.isEmpty(cfrsList)) {
			return null;
		}
		return cfrsList.get(0);
	}

	public Long expertAttendCfrs(Long cfrsId, String expertName) {
		List<Expert> experts = expertRepo.findByName(expertName);
		if (ListUtil.isEmpty(experts)) {
			return 0L;
		}
		Expert expert = experts.get(0);
		List<PackageDto> pkgs = pkgService.getAllByCfrsId(cfrsId);
		for (PackageDto p: pkgs) {
			List<Long> expertIds = ListUtil.extractDistinctList(
					p.getExperts(), new ExpertGetIdFunction());
			if (expertIds.contains(expert.getId())) {
				return expert.getId();
			}
		}
		return 0L;
	}

	public List<ConferenceDto> getAll() {
		Iterable<Conference> cfrsList = cfrsRepo.findAll();
		return BeanCopier.copy(cfrsList, ConferenceDto.class);
	}

	public ConferenceDto getById(Long id) {
		Conference cfrs = cfrsRepo.findOne(id);
		return BeanCopier.copy(cfrs, ConferenceDto.class);
	}
}
