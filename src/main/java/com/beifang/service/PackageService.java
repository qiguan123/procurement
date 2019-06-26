package com.beifang.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beifang.model.BidPrice;
import com.beifang.model.Bidder;
import com.beifang.model.Expert;
import com.beifang.model.ItemScore;
import com.beifang.model.PackageBidderRelation;
import com.beifang.model.PackageExpertRelation;
import com.beifang.model.ProcurePackage;
import com.beifang.repository.BidPriceRepository;
import com.beifang.repository.BidderRepository;
import com.beifang.repository.ExpertRepository;
import com.beifang.repository.PackageBidderRelRepository;
import com.beifang.repository.PackageExpertRelRepository;
import com.beifang.repository.PackageRepository;
import com.beifang.service.dto.GradeItemDto;
import com.beifang.service.dto.PackageDto;
import com.beifang.service.dto.ProjectDto;
import com.beifang.util.BeanCopier;
import com.beifang.util.ExcelUtil;
import com.beifang.util.ListUtil;
import com.beifang.util.func.ExpertGetIdFunction;
import com.beifang.util.func.PkgDtoGetIdFunction;
import com.beifang.util.func.ProjectDtoGetIdFunction;
import com.google.common.base.Function;
import com.google.common.io.Files;

@Service
public class PackageService {
	@Autowired
	private PackageRepository packageRepo;
	@Autowired
	private PackageExpertRelRepository pkgExpertRepo;
	@Autowired
	private PackageBidderRelRepository pkgBidderRepo;
	@Autowired
	private BidPriceRepository bidPriceRepo;
	@Autowired
	private BidderRepository bidderRepo;
	@Autowired
	private ExpertRepository expertRepo;
	@Autowired
	private ProjectService projectService;
	@Autowired
	private GradeItemService itemService;
	
	@Value("${gradeTable.location}")
	private String gradeTableRootDir;
	

	/********************* 会务端***START **************************/

	/**
	 * 会务端用，查看当前会议的所有分包、厂商报价、价格评分条目（含分数）
	 */
	public List<PackageDto> getAllWithPriceByCfrsId(Long cfrsId) {
		List<PackageDto> pkgDtos = getAllByCfrsId(cfrsId);
		if (ListUtil.isEmpty(pkgDtos)) {
			return pkgDtos;
		}
		setPkgsBidPrices(pkgDtos);
		setPkgsItemScore(pkgDtos);
		//取消返回值中的allItems和totalItem,仅保留priceItem
		for (PackageDto p: pkgDtos) {
			p.setAllItems(null);
			p.setTotalItem(null);
		}
		return pkgDtos;
	}

	/**
	 * 设置包的所有评分条目（含分数）
	 */
	private void setPkgsItemScore(List<PackageDto> pkgs) {
		if (ListUtil.isEmpty(pkgs)) {
			return;
		}
		List<Long> pkgIds = ListUtil.extractDistinctList(pkgs, new PkgDtoGetIdFunction());
		//所有评分条目
		List<GradeItemDto> items = itemService.getItemsByPkgIds(pkgIds);
		Map<Long, PackageDto> idWithPkgDtoMap = ListUtil.list2Map(
				pkgs, new PkgDtoGetIdFunction());
		for (GradeItemDto item: items) {
			PackageDto p = idWithPkgDtoMap.get(item.getPackageId());
			p.getAllItems().add(item);
			//价格条目
			if (item.getCategoryId() == 3) {
				p.setPriceItem(item);
			//总价
			} else if (item.getCategoryId() == 4) {
				p.setTotalItem(item);
			}
		}
	}

	/**
	 * 设置包的投标价格
	 */
	private void setPkgsBidPrices(List<PackageDto> pkgs) {
		if (ListUtil.isEmpty(pkgs)) {
			return;
		}
		List<Long> pkgIds = ListUtil.extractDistinctList(pkgs, new PkgDtoGetIdFunction());
		List<BidPrice> bidPrices = bidPriceRepo.findByPackageIdIn(pkgIds);
		for (PackageDto pkg: pkgs) {
			for (BidPrice price: bidPrices) {
				if (price.getPackageId() == pkg.getId()) {
					pkg.getBidPrices().add(price);
				}
			}
		}
	}

	/********************* 会务端***END ***********************************/

	
	/**
	 * 创建包、包的评分项、分数
	 */
	public PackageDto addPkgWithPriceAndScores(PackageDto pkgDto, byte[] gradeTable) {
		// add package
		ProcurePackage pkg = BeanCopier.copy(pkgDto, ProcurePackage.class);
		pkg = packageRepo.save(pkg);
		pkgDto.setId(pkg.getId());
		//pkg-expert
		List<PackageExpertRelation> pkgExpertRels = new ArrayList<>();
		for (Long expertId: pkgDto.getExpertIds()) {
			pkgExpertRels.add(new PackageExpertRelation(pkg.getId(), expertId));
		}
		pkgExpertRepo.save(pkgExpertRels);
		//pkg-bidder 和     bidPrices
		List<PackageBidderRelation> pkgBidderRels = new ArrayList<>();
		List<BidPrice> bidPrices = new ArrayList<>();
		for (Long bidderId: pkgDto.getBidderIds()) {
			pkgBidderRels.add(new PackageBidderRelation(pkg.getId(), bidderId));
			bidPrices.add(new BidPrice(bidderId, pkg.getId()));
		}
		pkgBidderRepo.save(pkgBidderRels);
		bidPriceRepo.save(bidPrices);
		// save table file
		File gradeTableFile = saveGradeTableFile(
				gradeTableRootDir + pkg.getId(), gradeTable);
		// add items
		itemService.makeItemsWithScore(pkgDto, gradeTableFile);
		return pkgDto;
	}
	
	private File saveGradeTableFile(String dir, byte[] table) {
		File tableDir = new File(dir);
		if (!tableDir.exists()) {
			tableDir.mkdirs();
		}
		File gradeTableFile = new File(tableDir, "评分表.xls");
		try {
			if (!gradeTableFile.exists()) {
				gradeTableFile.createNewFile();
			}
			Files.write(table, gradeTableFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return gradeTableFile;
	}

	/**
	 * 查询包及分数
	 */
	public PackageDto getPkgWithScores(Long id) {
		PackageDto pkgDto = getById(id);
		setPkgsItemScore(Arrays.asList(pkgDto));
		return pkgDto;
	}

	/**
	 * 查询会议中的包
	 */
	public List<PackageDto> getAllByCfrsId(Long cfrsId) {
		List<ProjectDto> projects = projectService.getByCfrsId(cfrsId);
		if (ListUtil.isEmpty(projects)) {
			return new ArrayList<>();
		}
		List<PackageDto> pkgDtos = getAllByProjectIds(
				ListUtil.extractDistinctList(
						projects, new ProjectDtoGetIdFunction()));
		if (ListUtil.isEmpty(pkgDtos)) {
			return new ArrayList<>();
		}
		//设置包的项目名
		Map<Long, ProjectDto> idWithProjectMap = ListUtil.list2Map(
				projects, new ProjectDtoGetIdFunction());
		for (PackageDto p: pkgDtos) {
			p.setProjectName(idWithProjectMap.get(p.getProjectId()).getName());
		}
		return pkgDtos;
	}
	/**
	 * 包、专家、投标方
	 */
	private List<PackageDto> getAllByProjectIds(List<Long> projectIds) {
		List<ProcurePackage> pkgs = packageRepo
				.findByProjectIdIn(projectIds);
		if (ListUtil.isEmpty(pkgs)) {
			return new ArrayList<>();
		}
		List<PackageDto> pkgDtos = BeanCopier.copy(pkgs, PackageDto.class);
		setPkgsExpert(pkgDtos);
		setPkgsBidder(pkgDtos);
		return pkgDtos;
	}
	
	/**
	 * 设置包的专家，设置包和专家的关系
	 */
	private void setPkgsExpert(List<PackageDto> pkgs) {
		List<Long> pkgIds = ListUtil.extractDistinctList(pkgs, new PkgDtoGetIdFunction());
		List<PackageExpertRelation> pkgExpertRels = pkgExpertRepo.findByPackageIdIn(pkgIds);
		List<Long> expertIds = ListUtil.extractDistinctList(pkgExpertRels, 
				new Function<PackageExpertRelation, Long>() {
					@Override
					public Long apply(PackageExpertRelation input) {
						return input.getExpertId();
					}
		});
		Iterable<Expert> experts = expertRepo.findAll(expertIds);
		Map<Long, Expert> idWithExpertMap = ListUtil.list2Map(
				experts, new Function<Expert, Long>() {
					@Override
					public Long apply(Expert input) {
						return input.getId();
					}
		});
		Map<Long, PackageDto> idWithPkgDtoMap = ListUtil.list2Map(
				pkgs, new PkgDtoGetIdFunction());
		for (PackageExpertRelation r : pkgExpertRels) {
			PackageDto pkg = idWithPkgDtoMap.get(r.getPackageId());
			pkg.getExperts().add(idWithExpertMap.get(r.getExpertId()));
			pkg.getExpertRels().add(r);
		}
	}
	/**
	 * 设置包的投标厂商
	 */
	private void setPkgsBidder(List<PackageDto> pkgs) {
		List<Long> pkgIds = ListUtil.extractDistinctList(pkgs, new PkgDtoGetIdFunction());
		List<PackageBidderRelation> pkgBidderRels = pkgBidderRepo.findByPackageIdIn(pkgIds);
		List<Long> bidderIds = ListUtil.extractDistinctList(
				pkgBidderRels, new Function<PackageBidderRelation, Long>() {
					@Override
					public Long apply(PackageBidderRelation input) {
						return input.getBidderId();
					}
				});
		Iterable<Bidder> bidders = bidderRepo.findAll(bidderIds);
		Map<Long, Bidder> idWithBidderMap = ListUtil.list2Map(bidders, new Function<Bidder, Long>() {
			@Override
			public Long apply(Bidder input) {
				return input.getId();
			}
		});
		Map<Long, PackageDto> idWithPkgDtoMap = ListUtil.list2Map(
				pkgs, new PkgDtoGetIdFunction());
		for (PackageBidderRelation r : pkgBidderRels) {
			idWithPkgDtoMap.get(r.getPackageId()).getBidders().add(
						idWithBidderMap.get(r.getBidderId()));
		}
	}

	/**
	 * 会议中专家需要评分的包
	 */
	public List<PackageDto> getAllByCfrsAndExpert(Long cfrsId, Long expertId) {
		List<PackageDto> pkgs = getAllByCfrsId(cfrsId);
		List<PackageDto> result = new ArrayList<>();
		for (PackageDto p : pkgs) {
			List<Long> expertIds = ListUtil.extractDistinctList(
					p.getExperts(), new ExpertGetIdFunction());
			if (expertIds.contains(expertId)) {
				result.add(p);
			}
		}
		return result;
	}

	public void setSingleScore(ItemScore score) {
		itemService.updateScore(score);
	}

	public byte[] getPkgWithTotalExcel(Long pkgId) {
		PackageDto pkg = getPkgWithScores(pkgId);
		if (pkg == null) {
			throw new RuntimeException("package not exist");
		}
		File excel = makeGradeTableExcel(
				pkg, gradeTableRootDir + File.separator + pkgId);
		try {
			return Files.toByteArray(excel);
		} catch(Exception e) {
			throw new RuntimeException("读取 专家评分结果.xls失败", e);
		}
	}
	
	private File makeGradeTableExcel(PackageDto pkg, String dir) {
		File result = new File(dir, "专家评分结果.xls");
		if (!result.exists()) {
			try {
				result.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException("创建评分表出错", e);
			}
		}
		Workbook wb = new HSSFWorkbook();
		List<String> headers = makeExcelTableHeaders(pkg);
		Map<Long, List<List<String>>> expertRows = makeExcelContentRows(pkg);
		try (FileOutputStream out = new FileOutputStream(result);) {
			for (Expert e: pkg.getExperts()) {
				//创建sheet
				Sheet sheet = ExcelUtil.newSheet(wb, e.getName());
				//标题
				ExcelUtil.writeRow(sheet, 0, Arrays.asList(pkg.getName()));
				//表头
				ExcelUtil.writeRow(sheet, 1, headers);
				//内容
				ExcelUtil.writeRows(sheet, 2, expertRows.get(e.getId()));
				//落款
				ExcelUtil.writeRow(sheet, expertRows.get(e.getId()).size() + 2,
						Arrays.asList(e.getName(), new Date().toString()));
			}
			wb.write(out);
		} catch(IOException e) {
			throw new RuntimeException("创建评分表出错", e);
		}
		return result;
	}

	private Map<Long, List<List<String>>> makeExcelContentRows(PackageDto pkg) {
		Map<Long, List<List<String>>> expertContent = new HashMap<>();
		for (GradeItemDto item: pkg.getAllItems()) {
			for (Entry<Long, Map<Long, ItemScore>> expertBidderScore : 
				item.getExpertBidderScores().entrySet()) {
				Long expertId = expertBidderScore.getKey();
				List<List<String>> rows = expertContent.get(expertId);
				if (ListUtil.isEmpty(rows)) {
					rows = new ArrayList<>();
					expertContent.put(expertId, rows);
				}
				List<String> row = new ArrayList<>();
				row.add(item.getName());
				row.add(String.valueOf(item.getMaxValue()));
				for (Bidder b: pkg.getBidders()) {
					row.add("" + expertBidderScore.getValue().get(b.getId()).getScore());
				}
				rows.add(row);
			}
		}
		return expertContent;
	}

	private List<String> makeExcelTableHeaders(PackageDto pkg) {
		List<String> headers = new ArrayList<>();
		headers.add("二级项");
		headers.add("分值");
		for (Bidder b: pkg.getBidders()) {
			headers.add(b.getName());
		}
		return headers;
	}

	/**
	 * 包、专家、投标方
	 */
	public PackageDto getById(Long pkgId) {
		ProcurePackage pkg = packageRepo.findOne(pkgId);
		if (pkg == null) {
			return null;
		}
		PackageDto pkgDto = BeanCopier.copy(pkg, PackageDto.class);
		List<PackageDto> pkgDtos = Arrays.asList(pkgDto);
		setPkgsBidder(pkgDtos);
		setPkgsExpert(pkgDtos);
		return pkgDto;
	}
	
	/**
	 * 提交评分表 =》修改PackageExpertRelation的modifiable状态为0
	 * 设置总分项目
	 */
	@Transactional
	public void expertSubmitScore(Long pkgId, Long expertId) {
		setPkgExpertGradeUnmodifiable(pkgId, expertId);
		PackageDto pkgDto = getPkgWithScores(pkgId);
		List<ItemScore> expertTotalScores = new ArrayList<>();
		Map<Long, Double> bidderTotalScoreMap = new HashMap<>();
		for (GradeItemDto item: pkgDto.getAllItems()) {
			for (ItemScore s : item.getScores()) {
				if (s.getExpertId() == expertId) {
					if (item.getCategoryId() == 4) {
						expertTotalScores.add(s);
						continue;
					}
					Double total = bidderTotalScoreMap.get(s.getBidderId());
					if (total == null) {
						total = 0.0;
					}
					bidderTotalScoreMap.put(s.getBidderId(), total + s.getScore());
				}
			}
		}
		//设置该专家的总分
		for (ItemScore s: expertTotalScores) {
			s.setScore(bidderTotalScoreMap.get(s.getBidderId()));
		}
		itemService.updateScoreList(expertTotalScores);
	}
	
	/**
	 * 设置专家对包的评分不可变
	 */
	private void setPkgExpertGradeUnmodifiable(Long pkgId, Long expertId) {
		pkgExpertRepo.setUnmodifiable(pkgId, expertId);
	}

	/**
	 * 设置包的投标价格、
	 * 基准价法的   价格的评分
	 */
	public void setPriceAndPriceScore(Long pkgId, List<BidPrice> prices) {
		bidPriceRepo.save(prices);
		PackageDto pkgDto = getPkgWithScores(pkgId);
		//基准价法
		setPriceScores(prices, pkgDto.getPriceItem().getScores(),
				pkgDto.getPriceItem().getMaxValue());
		itemService.updateItemScores(pkgDto.getPriceItem());
	}

	/**
	 * 基准价法设置价格分
	 */
	private void setPriceScores(List<BidPrice> prices, 
			List<ItemScore> scores, double maxValue) {
		 if (ListUtil.isEmpty(prices) || ListUtil.isEmpty(scores)) {
			 return;
		 }
		 double total = 0;
		 for (BidPrice p: prices) {
			 total += p.getPrice();
		 }
		 double mean = total / prices.size();
		 Map<Long, Double> bidderPrices = new HashMap<>();
		 for (BidPrice p: prices) {
			 double percent = (p.getPrice() - mean) / mean * 100;
			 if (percent > 0) {
				 bidderPrices.put(p.getBidderId(), maxValue - Math.round(percent) * 1.0);
			 } else {
				 bidderPrices.put(p.getBidderId(), maxValue - Math.round(-1 * percent) * 0.8);
			 }
		 }
		 for (ItemScore s: scores) {
			 s.setScore(bidderPrices.get(s.getBidderId()));
		 }
		 
	}

	public List<PackageDto> getAll() {
		Iterable<ProcurePackage> pkgs = packageRepo.findAll();
		List<PackageDto> pkgDtos = BeanCopier.copy(pkgs, PackageDto.class);
		if (ListUtil.isEmpty(pkgDtos)) {
			return pkgDtos;
		}
		setPkgsBidder(pkgDtos);
		setPkgsExpert(pkgDtos);
		return pkgDtos;
	}

}
