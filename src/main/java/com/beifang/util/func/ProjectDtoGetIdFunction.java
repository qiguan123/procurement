package com.beifang.util.func;

import com.beifang.service.dto.ProjectDto;
import com.google.common.base.Function;

public class ProjectDtoGetIdFunction implements Function<ProjectDto, Long> {

	@Override
	public Long apply(ProjectDto input) {
		return input.getId();
	}

}
