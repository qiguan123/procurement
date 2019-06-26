package com.beifang.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.beifang.model.Expert;
import com.beifang.service.ExpertService;

@RestController
@RequestMapping("/expert")
public class ExpertController {
	@Autowired
	private ExpertService expertService;
	
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public String addExperts(@RequestBody List<Expert> experts) {
		expertService.addExperts(experts);
		return "ok";
	}
}
