package com.beifang.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beifang.common.PageResult;
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
		//不允许有重名专家
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

	public PageResult<ConferenceDto> getPageByName(Integer page, Integer size, String name) {
		Page<Conference> cfrsPage = null;
		Pageable pageRequest = new PageRequest(page -1, size);
		if (name == null || name.isEmpty() || name.trim().isEmpty()) {
			cfrsPage = cfrsRepo.findAll(pageRequest);
		} else {
			cfrsPage = cfrsRepo.findByNameContaining(name.trim(), pageRequest);
		}
		List<ConferenceDto> cfrsDtos = BeanCopier.copy(cfrsPage.getContent(), ConferenceDto.class);
		return new PageResult<>(cfrsPage.getTotalElements(), cfrsDtos);
	}

	public ConferenceDto getById(Long id) {
		Conference cfrs = cfrsRepo.findOne(id);
		return BeanCopier.copy(cfrs, ConferenceDto.class);
	}

	/**
	 *    状态为0的会议
	 */
	public List<ConferenceDto> getPreparedCfrsList() {
		List<Conference> cfrsList = cfrsRepo.findByState(0);
		return BeanCopier.copy(cfrsList, ConferenceDto.class);
	}

	public List<ConferenceDto> getByIds(List<Long> ids) {
		Iterable<Conference> cfrsList = cfrsRepo.findAll(ids);
		return BeanCopier.copy(cfrsList, ConferenceDto.class);
	}

	public List<ConferenceDto> getClosedCfrsList() {
		List<Conference> cfrsList = cfrsRepo.findByState(1);
		return BeanCopier.copy(cfrsList, ConferenceDto.class);
	}
}
