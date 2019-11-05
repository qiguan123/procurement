package com.beifang.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.beifang.rest.vo.StatisticResponseVo;
import com.beifang.service.ConferenceService;
import com.beifang.service.PackageService;
import com.beifang.service.ProjectService;
import com.beifang.service.dto.ConferenceDto;
import com.beifang.service.dto.PackageDto;
import com.beifang.service.dto.ProjectDto;
import com.beifang.util.ListUtil;

@RestController
@RequestMapping("/statistic")
public class StatisticController {
	@Autowired
	private ConferenceService cfrsService;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private PackageService pkgService;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public StatisticResponseVo get() {
		StatisticResponseVo result = new StatisticResponseVo();
		List<ConferenceDto> closedCfrsList = cfrsService.getClosedCfrsList();
		result.setCfrsCount(closedCfrsList.size());
		List<ProjectDto> projectList = projectService.getByIds(
				ListUtil.extractDistinctList(closedCfrsList, ConferenceDto::getId));
		result.setProjectCount(projectList.size());
		List<PackageDto> pkgList = pkgService.getByProjectIds(
				ListUtil.extractDistinctList(projectList, ProjectDto::getId));
		result.setPkgCount(pkgList.size());
		if (ListUtil.isEmpty(pkgList)) {
			result.setBidUpperLimit("0");
		} else {
			Double total = pkgList.stream().map(p -> p.getBidUpperLimit()).reduce((a, b) -> a + b).get();
			result.setBidUpperLimit(total + "");
		}
		return result;
	}
	
}
