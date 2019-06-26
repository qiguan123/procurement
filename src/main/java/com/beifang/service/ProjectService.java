package com.beifang.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	public List<ProjectDto> getAll() {
		Iterable<Project> projects = projectRepo.findAll();
		return BeanCopier.copy(projects, ProjectDto.class);
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
	
}
