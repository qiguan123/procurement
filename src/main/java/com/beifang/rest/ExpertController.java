package com.beifang.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
		List<Expert> result = new ArrayList<>();
		for (PackageDto p : pkgs) {
			result.addAll(p.getExperts());
		}
		return result;
	}
}
