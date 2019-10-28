package com.beifang.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.beifang.common.PageResult;
import com.beifang.model.Project;
import com.beifang.repository.ProjectRepository;
import com.beifang.service.dto.ConferenceDto;
import com.beifang.service.dto.ProjectDto;
import com.beifang.util.BeanCopier;
import com.beifang.util.ListUtil;

@Service
public class ProjectService {
	@Autowired
	private ProjectRepository projectRepo;
	@Autowired
	private ConferenceService cfrsService;
	
	public ProjectDto add(ProjectDto dto) {
		Project project = BeanCopier.copy(dto, Project.class);
		project = projectRepo.save(project);
		return BeanCopier.copy(project, ProjectDto.class);
	}

	public PageResult<ProjectDto> getPageByName(Integer page, Integer limit, String name) {
		Page<Project> projectPage = null;
		Pageable pageRequest = new PageRequest(page -1, limit);
		if (name == null || name.isEmpty() || name.trim().isEmpty()) {
			projectPage = projectRepo.findAll(pageRequest);
		} else {
			projectPage = projectRepo.findByNameContaining(name.trim(), pageRequest);
		}
		List<ProjectDto> projectDtos = BeanCopier.copy(projectPage.getContent(), ProjectDto.class);
		setCfrsName(projectDtos);
		return new PageResult<>(projectPage.getTotalElements(), projectDtos);
	}
	
	private void setCfrsName(List<ProjectDto> projectDtos) {
		if (ListUtil.isEmpty(projectDtos)) {
			return;
		}
		List<Long> cfrsIds = ListUtil.extractDistinctList(projectDtos, ProjectDto::getCfrsId);
		List<ConferenceDto> cfrsList = cfrsService.getByIds(cfrsIds);
		Map<Long, String> cfrsIdWithNameMap = ListUtil.list2Map(cfrsList, ConferenceDto::getId, ConferenceDto::getName);
		for (ProjectDto p: projectDtos) {
			p.setCfrsName(cfrsIdWithNameMap.get(p.getCfrsId()));
		}
	}

	public List<ProjectDto> getByCfrsId(Long cfrsId) {
		List<Project> projectList = projectRepo.findByCfrsId(cfrsId);
		if (ListUtil.isEmpty(projectList)) {
			return new ArrayList<>();
		}
		List<ProjectDto> result = BeanCopier.copy(projectList, ProjectDto.class);
		return result;
	}

	public ProjectDto getById(Long id) {
		Project project = projectRepo.findOne(id);
		ProjectDto dto = BeanCopier.copy(project, ProjectDto.class);
		ConferenceDto cfrs = cfrsService.getById(dto.getCfrsId());
		dto.setCfrsName(cfrs.getName());
		return dto;
	}

	public List<ProjectDto> getByIds(List<Long> ids) {
		Iterable<Project> projects = projectRepo.findAll(ids);
		return BeanCopier.copy(projects, ProjectDto.class);
	}

	public List<ProjectDto> getUnstartedProjects() {
		List<ConferenceDto> preparedCfrsList = cfrsService.getPreparedCfrsList();
		if (ListUtil.isEmpty(preparedCfrsList)) {
			return new ArrayList<>();
		}
		List<Long> cfrsIds = ListUtil.extractDistinctList(preparedCfrsList, ConferenceDto::getId);
		List<Project> projects = projectRepo.findByCfrsIdIn(cfrsIds);
		List<ProjectDto> projectDtos = BeanCopier.copy(projects, ProjectDto.class);
		setCfrsName(projectDtos);
		return projectDtos;
	}
	
}
