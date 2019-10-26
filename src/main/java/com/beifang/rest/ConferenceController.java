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
import com.beifang.exception.NoConferenceException;
import com.beifang.rest.vo.ConferenceRequestVo;
import com.beifang.rest.vo.ConferenceResponseVo;
import com.beifang.service.ConferenceService;
import com.beifang.service.dto.ConferenceDto;
import com.beifang.util.BeanCopier;
import com.beifang.util.ListUtil;

@RestController
@RequestMapping("/cfrs")
public class ConferenceController {

	@Autowired
	private ConferenceService cfrsService;
	
	/**
	 * 当前进行的会议
	 */
	@RequestMapping(path = "/ongoing", method = RequestMethod.GET)
	public ConferenceResponseVo getOngoingConference() {
		List<ConferenceDto> conferences = cfrsService.getAllByState(2);
		if (ListUtil.isEmpty(conferences)) {
			throw new NoConferenceException();
		}
		return BeanCopier.copy(conferences.get(0), ConferenceResponseVo.class);
	}
	
	/**
	 * 专家登陆,成功返回专家Id,否则返回0
	 */
	@RequestMapping(path = "/{cfrsId}/expert/login", method = RequestMethod.GET)
	public Long expertLogin(@PathVariable Long cfrsId, @RequestParam String expertName) {
		Long expertId = cfrsService.expertAttendCfrs(cfrsId, expertName);
		return expertId;
	}
	
	/**
	 * 会议列表
	 */
	@RequestMapping(path = "", method = RequestMethod.GET)
	public PageResult<ConferenceResponseVo> getPageByName(
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(defaultValue = "20") Integer limit,
			@RequestParam(required = false) String name) {
		
		PageResult<ConferenceDto> pageResult = cfrsService.getPageByName(page, limit, name);
		List<ConferenceResponseVo> restData = BeanCopier.copy(
				pageResult.getData(), ConferenceResponseVo.class);
		return new PageResult<>(pageResult.getTotal(), restData);
	}
	
	/**
	 *未开始的会议列表
	 */
	@RequestMapping(path = "/prepared", method = RequestMethod.GET)
	public List<ConferenceResponseVo> getPreparedCfrsList() {
		List<ConferenceDto> cfrsList = cfrsService.getPreparedCfrsList();
		return BeanCopier.copy(cfrsList, ConferenceResponseVo.class);
	}
	
	/**
	 * 创建会议
	 */
	@RequestMapping(path = "", method = RequestMethod.POST)
	public ConferenceResponseVo add(@RequestBody ConferenceRequestVo vo) {
		ConferenceDto cfrsDto = BeanCopier.copy(vo, ConferenceDto.class);
		cfrsDto = cfrsService.add(cfrsDto);
		return BeanCopier.copy(cfrsDto, ConferenceResponseVo.class);
	}
	
	/**
	 * 创建会议
	 */
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public ConferenceResponseVo add(@PathVariable Long id) {
		ConferenceDto cfrsDto = cfrsService.getById(id);
		return BeanCopier.copy(cfrsDto, ConferenceResponseVo.class);
	}
	
	/**
	 * 启动会议
	 */
	@RequestMapping(path = "/{id}/start", method = RequestMethod.POST)
	public String setCfrsStart(@PathVariable Long id) {
		cfrsService.updateState(id, 2);
		return "ok";
	}
	
	/**
	 * 结束会议
	 */
	@RequestMapping(path = "/{id}/close", method = RequestMethod.POST)
	public String setCfrsClose(@PathVariable Long id) {
		cfrsService.updateState(id, 1);
		return "ok";
	}
	
}
