package com.beifang.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.beifang.common.PageResult;
import com.beifang.model.Expert;
import com.beifang.service.ExpertService;
import com.beifang.service.PackageService;
import com.beifang.service.dto.PackageDto;

@RestController
@RequestMapping("/expert")
public class ExpertController {
	@Autowired
	private ExpertService expertService;
	@Autowired
	private PackageService pkgService;
	
	/**
	 * 根据姓名分页查看
	 */
	@RequestMapping(path = "", method = RequestMethod.GET)
	@ResponseBody
	public PageResult<Expert> getPageByName(
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "20") Integer limit,
			@RequestParam(required = false) String name) {
		
		return expertService.getPageByName(page, limit, name);
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public String addExpert(@RequestBody Expert expert) {
		expertService.saveExpert(expert);
		return "ok";
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public String addExperts(@RequestBody List<Expert> experts) {
		expertService.addExperts(experts);
		return "ok";
	}
	
	/**
	 * 查看此次会议的专家
	 */
	@RequestMapping(path = "/cfrs", method = RequestMethod.GET)
	public List<Expert> getByCfrsId(@RequestParam Long cfrsId) {
		List<PackageDto> pkgs = pkgService.getAllByCfrsId(cfrsId);
		Set<Expert> result = new HashSet<>();
		for (PackageDto p : pkgs) {
			result.addAll(p.getExperts());
		}
		return new ArrayList<>(result);
	}
}
