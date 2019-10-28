package com.beifang.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beifang.common.PageResult;
import com.beifang.rest.vo.ProjectRequestVo;
import com.beifang.rest.vo.ProjectResponseVo;
import com.beifang.service.ProjectService;
import com.beifang.service.dto.ProjectDto;
import com.beifang.util.BeanCopier;

@RestController
@RequestMapping("/project")
public class ProjectController {

	@Autowired
	private ProjectService projectService;
	
	@RequestMapping(path = "", method = RequestMethod.GET)
	public PageResult<ProjectResponseVo> getPageByName(
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "20") Integer limit,
			@RequestParam(required = false) String name) {
		
		PageResult<ProjectDto> pageResult = projectService.getPageByName(page, limit, name);
		List<ProjectResponseVo> restData = BeanCopier.copy(pageResult.getData(), ProjectResponseVo.class);
		return new PageResult<>(pageResult.getTotal(), restData);
	}
	
	@RequestMapping(path = "{id}", method = RequestMethod.GET)
	public ProjectResponseVo getById(@PathVariable Long id) {
		ProjectDto projectDto = projectService.getById(id);
		return BeanCopier.copy(projectDto, ProjectResponseVo.class);
	}
	
	@RequestMapping(path = "", method = RequestMethod.POST)
	public ProjectResponseVo addProject(@RequestBody ProjectRequestVo vo) {
		if (vo == null || vo.getCfrsId() == null) {
			throw new RuntimeException("no conference related");
		}
		ProjectDto projectDto = BeanCopier.copy(vo, ProjectDto.class);
		projectDto = projectService.add(projectDto);
		return BeanCopier.copy(projectDto, ProjectResponseVo.class);
	}
	
	@RequestMapping(path = "/unstarted", method = RequestMethod.GET)
	public List<ProjectResponseVo> unstartedProjects() {
		List<ProjectDto> projects = projectService.getUnstartedProjects();
		return BeanCopier.copy(projects, ProjectResponseVo.class);
	}
	
}
