package com.beifang.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.beifang.exception.UploadGradeTableException;
import com.beifang.model.BidPrice;
import com.beifang.model.ItemScore;
import com.beifang.rest.vo.ItemScoreRequestVo;
import com.beifang.rest.vo.PackageRequestVo;
import com.beifang.rest.vo.PriceWithPriceItemRequestVo;
import com.beifang.service.PackageService;
import com.beifang.service.dto.PackageDto;
import com.beifang.util.BeanCopier;
import com.beifang.util.ListUtil;

@Controller
@RequestMapping("/pkg")
public class PackageController {

	@Autowired
	private PackageService packageService;
	

	/*********************会务端***START**************************/
	
	/**
	 * 查看此次会议的包、价格、评分
	 */
	@RequestMapping(path = "/priceWithScore", method = RequestMethod.GET)
	@ResponseBody
	public List<PackageDto> getPkgsWithPriceByCfrsId(
			@RequestParam Long cfrsId) {
		return packageService.getAllWithPriceByCfrsId(cfrsId);
	}
	
	/**
	 * 设置包的价格、价格分数
	 */
	@RequestMapping(path = "/{pkgId}/price/score", method = RequestMethod.POST)
	@ResponseBody
	public String setPriceScores(@PathVariable Long pkgId,
			@RequestBody PriceWithPriceItemRequestVo vo) {
		
		List<BidPrice> prices = vo.getPrices();
		packageService.setPriceAndPriceScore(pkgId, prices);
		return "ok";
	}
	
	/**
	 * 查看此次会议的包
	 */
	@RequestMapping(path = "/cfrs", method = RequestMethod.GET)
	@ResponseBody
	public List<PackageDto> getByCfrsId(@RequestParam Long cfrsId) {
		return packageService.getAllByCfrsId(cfrsId);
	}
	
	/**
	 * 根据id查看包的详情(含所有评分项、分数)
	 */
	@RequestMapping(path = "/{id}/score", method = RequestMethod.GET)
	@ResponseBody
	public PackageDto getPkgWithScores(
			@PathVariable Long id) {
		
		PackageDto pkgDto = packageService.getPkgWithScores(id);
		//map copy有问题，所以这里直接返回PackageDto
		return pkgDto;
	}
	
	/**
	 * 导出包的评分表zip
	 */
	@RequestMapping(path = "/{id}/score/export", method = RequestMethod.GET)
	public void exportPkgScores(@PathVariable Long id,
			HttpServletResponse rsp) {
		
		byte[] scoreExcel = packageService.getPkgWithTotalExcel(id);
		rsp.setContentType("text/plain");
		rsp.setHeader("Content-disposition", "attachment;filename=score-pkg-" + id + ".zip");
		try (OutputStream out = rsp.getOutputStream();) {
			out.write(scoreExcel);
		} catch (IOException e) {
			throw new RuntimeException("导出失败", e);
		}
		
	}
	
	/*********************会务端***END***********************************/
	
	
	
	/*********************专家端***START***********************************/

	/**
	 * 查看会议中专家需要评分的包
	 */
	@RequestMapping(path = "/expert", method = RequestMethod.GET)
	@ResponseBody
	public List<PackageDto> getExpertPkgs(
			@RequestParam Long cfrsId,
			@RequestParam Long expertId) {
		return packageService.getAllByCfrsAndExpert(cfrsId, expertId);
	}
	
	/**
	 * 设置单个分数
	 */
	@RequestMapping(path = "/score", method = RequestMethod.POST)
	@ResponseBody
	public String setSingleScore(@RequestBody ItemScoreRequestVo score) {
		ItemScore dto = BeanCopier.copy(score, ItemScore.class);
		packageService.setSingleScore(dto);
		return "ok";
	}
	/**
	 * 提交评分表 =》修改PackageExpertRelation的modifiable状态为0
	 * 设置总分项目
	 */
	@RequestMapping(path = "/{pkgId}/expert/submit", method = RequestMethod.POST)
	@ResponseBody
	public String submitScore(@PathVariable Long pkgId, @RequestParam Long expertId) {
		packageService.expertSubmitScore(pkgId, expertId);
		return "ok";
	}
	
	/*******************专家端***END******************************************/
	
	
	/*******************管理端***START********************************************/
	
	/**
	 * 查看所有包
	 */
	@RequestMapping(path = "", method = RequestMethod.GET)
	@ResponseBody
	public List<PackageDto> getAll() {
		return packageService.getAll();
	}
	
	/**
	 * 根据id查看
	 */
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public PackageDto getById(@PathVariable Long id) {
		return packageService.getById(id);
	}
	
	/**
	 * 创建分包、投标价、评分项及评分
	 */
	@RequestMapping(path = "", method = RequestMethod.POST,
			consumes = {"multipart/form-data"})
	@ResponseBody
	public PackageDto addPkgWithScores(
			@RequestPart("package") PackageRequestVo pkg,
			@RequestPart("table") MultipartFile file) {
		
		if(pkg == null || ListUtil.isEmpty(pkg.getBidderIds()) || 
				ListUtil.isEmpty(pkg.getExpertIds()) || pkg.getProjectId() == null ||
				file == null) {
			throw new RuntimeException("required parameters cannot be empty");
		}
		PackageDto pkgDto = BeanCopier.copy(pkg, PackageDto.class);
		try {
			pkgDto = packageService.addPkgWithPriceAndScores(pkgDto, file.getBytes());
		} catch (IOException e) {
			throw new UploadGradeTableException(e.getMessage());
		}
		return pkgDto;
	}
	
	
	/**********************管理端**END******************************************/
	
	
}
