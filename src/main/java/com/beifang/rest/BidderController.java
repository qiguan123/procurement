package com.beifang.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.beifang.common.PageResult;
import com.beifang.model.Bidder;
import com.beifang.rest.vo.BidderResponseVo;
import com.beifang.service.BidderService;
import com.beifang.service.dto.BidderDto;
import com.beifang.util.BeanCopier;

@RestController
@RequestMapping("/bidder")
public class BidderController {
	@Autowired
	private BidderService bidderService;
	
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public String addBidders(@RequestBody List<Bidder> bidders) {
		bidderService.addBidders(bidders);
		return "ok";
	}
	
	/**
	 * 根据名称分页查看
	 */
	@RequestMapping(path = "", method = RequestMethod.GET)
	@ResponseBody
	public PageResult<Bidder> getPageByName(
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "20") Integer limit,
			@RequestParam(required = false) String name) {
		
		return bidderService.getPageByName(page, limit, name);
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public String addBidderExpert(@RequestBody Bidder bidder) {
		bidderService.saveBidder(bidder);
		return "ok";
	}
	
	/**
	 *所有应标人的简单信息 
	 */
	@RequestMapping(path = "/simple", method = RequestMethod.GET)
	@ResponseBody
	public List<BidderResponseVo> getSimpleAll() {
		List<BidderDto> all = bidderService.getSimpleAll();
		return BeanCopier.copy(all, BidderResponseVo.class);
	}
}
