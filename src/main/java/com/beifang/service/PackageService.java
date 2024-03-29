package com.beifang.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beifang.common.PageResult;
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
import com.beifang.service.dto.BidFinalResultDto;
import com.beifang.service.dto.GradeItemDto;
import com.beifang.service.dto.PackageDto;
import com.beifang.service.dto.ProjectDto;
import com.beifang.util.BeanCopier;
import com.beifang.util.DateTimeFormatUtil;
import com.beifang.util.ExcelUtil;
import com.beifang.util.ListUtil;
import com.beifang.util.MathUtil;
import com.beifang.util.ZipUtil;
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
		List<Long> pkgIds = ListUtil.extractDistinctList(pkgs, PackageDto::getId);
		//所有评分条目
		List<GradeItemDto> items = itemService.getItemsByPkgIds(pkgIds);
		Map<Long, PackageDto> idWithPkgDtoMap = ListUtil.list2Map(
				pkgs, PackageDto::getId);
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
		List<Long> pkgIds = ListUtil.extractDistinctList(pkgs, PackageDto::getId);
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
		if (pkgDto == null) {
			return null;
		}
		setPkgsItemScore(Arrays.asList(pkgDto));
		setPkgBidPrices(pkgDto);
		return pkgDto;
	}

	private void setPkgBidPrices(PackageDto pkgDto) {
		List<BidPrice> bidPrices = bidPriceRepo.findByPackageIdIn(
				Arrays.asList(pkgDto.getId()));
		pkgDto.setBidPrices(bidPrices);
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
						projects, ProjectDto::getId));
		if (ListUtil.isEmpty(pkgDtos)) {
			return new ArrayList<>();
		}
		//设置包的项目名
		Map<Long, ProjectDto> idWithProjectMap = ListUtil.list2Map(
				projects, ProjectDto::getId);
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
	 * 专家id由小到大
	 */
	private void setPkgsExpert(List<PackageDto> pkgs) {
		List<Long> pkgIds = ListUtil.extractDistinctList(pkgs, PackageDto::getId);
		List<PackageExpertRelation> pkgExpertRels = pkgExpertRepo.findByPackageIdIn(pkgIds);
		List<Long> expertIds = ListUtil.extractDistinctList(pkgExpertRels, PackageExpertRelation::getExpertId);
		Iterable<Expert> experts = expertRepo.findAll(expertIds);
		Map<Long, Expert> idWithExpertMap = ListUtil.list2Map(experts, Expert::getId);
		Map<Long, PackageDto> idWithPkgDtoMap = ListUtil.list2Map(
				pkgs, PackageDto::getId);
		for (PackageExpertRelation r : pkgExpertRels) {
			PackageDto pkg = idWithPkgDtoMap.get(r.getPackageId());
			pkg.getExperts().add(idWithExpertMap.get(r.getExpertId()));
			pkg.getExpertRels().add(r);
		}
		for (PackageDto p: pkgs) {
			p.getExperts().sort((a,b) -> a.getId() > b.getId() ? 1 : -1);
		}
	}
	/**
	 * 设置包的投标人，设置投标人与包的关系
	 * 投标人顺序 按照id由小到大
	 */
	private void setPkgsBidder(List<PackageDto> pkgs) {
		List<Long> pkgIds = ListUtil.extractDistinctList(pkgs, PackageDto::getId);
		List<PackageBidderRelation> pkgBidderRels = pkgBidderRepo.findByPackageIdIn(pkgIds);
		List<Long> bidderIds = ListUtil.extractDistinctList(pkgBidderRels, PackageBidderRelation::getBidderId);
		Iterable<Bidder> bidders = bidderRepo.findAll(bidderIds);
		Map<Long, Bidder> idWithBidderMap = ListUtil.list2Map(bidders, Bidder::getId);
		Map<Long, PackageDto> idWithPkgDtoMap = ListUtil.list2Map(
				pkgs, PackageDto::getId);
		for (PackageBidderRelation r : pkgBidderRels) {
			PackageDto pkg = idWithPkgDtoMap.get(r.getPackageId());
			pkg.getBidders().add(idWithBidderMap.get(r.getBidderId()));
			pkg.getBidderRels().add(r);
		}
		for (PackageDto p: pkgs) {
			p.getBidders().sort((a,b) -> a.getId() > b.getId() ? 1 : -1);
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
					p.getExperts(), Expert::getId);
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
		String dir = gradeTableRootDir + File.separator + pkgId;
		List<File> excels = makeGradeTableExcels(pkg, dir);
		try {
			File excelZip = ZipUtil.makeZip(excels, new File(dir, "评分表.zip"));
			return Files.toByteArray(excelZip);
		} catch(Exception e) {
			throw new RuntimeException("读取 专家评分结果.xls失败", e);
		}
	}
	
	private List<File> makeGradeTableExcels(PackageDto pkg, String dir) {
		List<File> result = new ArrayList<>();
		File rawTable = new File(dir, "评分表.xls");
		try {
			for (Expert e: pkg.getExperts()) {
				//复制上传模板
				File expertTable = new File(
						dir, e.getName() + "_" + e.getId() +"_评分结果.xls");
				if (!expertTable.exists()) {
					expertTable.createNewFile();
				}
				Files.copy(rawTable, expertTable);
				//填充模板
				fillGradeTable(expertTable, e, pkg);
				result.add(expertTable);
			}
		} catch (IOException ex) {
			throw new RuntimeException("创建评分表出错", ex);
		}
		return result;
	}

	private void fillGradeTable(File expertTable, Expert e, PackageDto pkg) {
		List<List<String>> expertScores = makeExcelScoreRows(pkg, e);
		try (FileInputStream in = new FileInputStream(expertTable);
			Workbook wb = WorkbookFactory.create(in);) {
			
			Sheet sheet = wb.getSheetAt(0);
			//厂商名称
			ExcelUtil.writeRow(sheet, 1, 6,
				ListUtil.extractDistinctList(pkg.getBidders(), Bidder::getName));
			//分数
			ExcelUtil.writeRows(sheet, 2, 6, expertScores);
			//总分分值
			ExcelUtil.writeRow(sheet, sheet.getLastRowNum() - 2, 4, 
				Arrays.asList("" + pkg.getTotalItem().getMaxValue()));
			//专家+时间
			ExcelUtil.writeRow(sheet, sheet.getLastRowNum() - 1, 0,
				Arrays.asList(e.getName()));
			ExcelUtil.writeRow(sheet, sheet.getLastRowNum(), 0,
					Arrays.asList(DateTimeFormatUtil.format(new Date())));
			try (FileOutputStream out = new FileOutputStream(expertTable);) {
				wb.write(out);
			}
		} catch(IOException ex) {
			throw new RuntimeException("创建评分表出错", ex);
		}		
	}

	private List<List<String>> makeExcelScoreRows(PackageDto pkg, Expert e) {
		List<List<String>> scores = new ArrayList<>();
		for (GradeItemDto item: pkg.getAllItems()) {
			Map<Long, ItemScore> bidderScores = item.getExpertBidderScores().get(e.getId());
			List<String> row = new ArrayList<>();
			for (Bidder b: pkg.getBidders()) {
				row.add("" + bidderScores.get(b.getId()).getScore());
			}
			scores.add(row);
		}
		return scores;
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
		setProjectName(pkgDtos);
		return pkgDto;
	}
	
	/**
	 * 查询投标最终结果
	 */
	public PackageDto getDetailById(Long pkgId) {
		PackageDto pkg = getPkgWithScores(pkgId);
		if (pkg == null) {
			return null;
		}
		//所有专家都提交则查询最终结果
		if (isExpertAllSubmit(pkg.getExpertRels())) {
			setBidFinalResult(pkg);
		}
		return pkg;
	}
	
	/**
	 * 专家都提交评分
	 */
	private boolean isExpertAllSubmit(List<PackageExpertRelation> expertRels) {
		if (ListUtil.isEmpty(expertRels)) {
			return false;
		}
		for (PackageExpertRelation r : expertRels) {
			if (r == null || r.getModifiable() == 1) {
				return false;
			}
		}
		return true;
	}

	private void setBidFinalResult(PackageDto pkg) {
		List<BidFinalResultDto> results = new ArrayList<>();
		//bidder <id, name>
		Map<Long, String> bidderIdWithNameMap = ListUtil.list2Map(
			pkg.getBidders(), Bidder::getId, Bidder::getName);
		//bidder <id, price>
		Map<Long, Double> bidderIdWithPriceMap = ListUtil.list2Map(
			pkg.getBidPrices(), BidPrice::getBidderId, BidPrice::getPrice);
		
		GradeItemDto totalItem = pkg.getTotalItem();
		List<ItemScore> finalScores = totalItem.getScores();
		//设置投标人平均分
		setBidderMeanScore(pkg.getBidderRels(), finalScores);
		//专家评分，针对每个投标人，专家顺序为id由小到大
		Map<Long, List<String>> bidderExpertScoresMap = getBidderExpertScoresMap(finalScores);
		//按得分由高到低排序
		pkg.getBidderRels().sort((a, b) -> a.getMeanScore() > b.getMeanScore() ? -1 : 1);
		for (PackageBidderRelation r: pkg.getBidderRels()) {
			BidFinalResultDto result = new BidFinalResultDto();
			result.setBidderName(bidderIdWithNameMap.get(r.getBidderId()));
			result.setBidPrice(String.valueOf(bidderIdWithPriceMap.get(r.getBidderId())));
			result.setFinalScore(r.getMeanScore() + "");
			result.setExpertScores(bidderExpertScoresMap.get(r.getBidderId()));
			results.add(result);
		}
		pkg.setFinalResult(results);
	}

	private Map<Long, List<String>> getBidderExpertScoresMap(List<ItemScore> finalScores) {
		Map<Long, List<String>> result = new HashMap<>();
		Map<Long, List<ItemScore>> bidderScores = new HashMap<>();
		for (ItemScore s : finalScores) {
			Long bidderId = s.getBidderId();
			List<ItemScore> scores = bidderScores.getOrDefault(bidderId, new ArrayList<>());
			scores.add(s);
			bidderScores.put(bidderId, scores);
		}
		for (Long bidderId: bidderScores.keySet()) {
			bidderScores.get(bidderId).sort((a,b) -> a.getExpertId() > b.getExpertId() ? 1 : -1);
			List<String> sortedScoreStringList = ListUtil.extractList(
					bidderScores.get(bidderId), t->t.getScore() + "");
			result.put(bidderId, sortedScoreStringList);
		}
		return result;
	}

	/**
	 * 设置投标人平均分，去除一个最高、一个最低
	 */
	private void setBidderMeanScore(List<PackageBidderRelation> bidderRels, List<ItemScore> finalScores) {
		Double firstMeanScore = bidderRels.get(0).getMeanScore();
		if (firstMeanScore != null && firstMeanScore != 0) {
			return;
		}
		Map<Long, Double> bidderMaxScore = new HashMap<>();
		Map<Long, Double> bidderMinScore = new HashMap<>();
		Map<Long, Double> bidderTotalScore = new HashMap<>();
		Set<Long> expertIds = new HashSet<>();
		for (ItemScore s: finalScores) {
			Long bidderId = s.getBidderId();
			if (bidderMaxScore.get(bidderId) == null || bidderMaxScore.get(bidderId) < s.getScore()) {
				bidderMaxScore.put(bidderId, s.getScore());
			}
			if (bidderMinScore.get(bidderId) == null || bidderMinScore.get(bidderId) > s.getScore()) {
				bidderMinScore.put(bidderId, s.getScore());
			}
			bidderTotalScore.compute(bidderId, (k, v) -> v == null ? s.getScore() : v + s.getScore());
			expertIds.add(s.getExpertId());
		}
		for (PackageBidderRelation r : bidderRels) {
			Long bidderId = r.getBidderId();
			Double finalMeanScore = MathUtil.toDecimal(
					(bidderTotalScore.get(bidderId) - bidderMaxScore.get(bidderId) 
						- bidderMinScore.get(bidderId)) / (expertIds.size() - 2),
					2);
			r.setMeanScore(finalMeanScore);
		}
		pkgBidderRepo.save(bidderRels);
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
			s.setScore(
				MathUtil.toDecimal(bidderTotalScoreMap.get(s.getBidderId()), 2));
		}
		itemService.updateScoreList(expertTotalScores);
	}
	
	/**
	 * 设置专家对包的评分不可变
	 */
	private void setPkgExpertGradeUnmodifiable(Long pkgId, Long expertId) {
		pkgExpertRepo.setUnmodifiable(pkgId, expertId, new Date().getTime());
	}

	/**
	 * 设置包的投标价格、
	 * 基准价法的   价格的评分
	 */
	public void setPriceAndPriceScore(Long pkgId, List<BidPrice> prices) {
		bidPriceRepo.save(prices);
		PackageDto pkgDto = getPkgWithScores(pkgId);
		//平均价法
		if (pkgDto.getPriceStandardType() == 1) {
			setPriceScoresByMeanMethod(prices, pkgDto.getPriceItem().getScores(),
					pkgDto.getPriceItem().getMaxValue(), 
					pkgDto.getHigherDeduction(), pkgDto.getLowerDeduction());
		} else if (pkgDto.getPriceStandardType() == 2) {//最低价法
			setPriceScoresByLowestMethod(prices, pkgDto.getPriceItem().getScores(),
					pkgDto.getPriceItem().getMaxValue());
		}
		itemService.updateItemScores(pkgDto.getPriceItem());
	}

	private void setPriceScoresByLowestMethod(List<BidPrice> prices, List<ItemScore> scores, Double maxValue) {
		if (ListUtil.isEmpty(prices) || ListUtil.isEmpty(scores)) {
			 return;
		}
		double minPrice = Double.MAX_VALUE;
		for (BidPrice price : prices) {
			if (price.getPrice() != null && price.getPrice() < minPrice) {
				minPrice = price.getPrice();
			}
		}
		Map<Long, Double> bidScoreMap = new HashMap<>();
		for (BidPrice price: prices) {
			if (price.getPrice() != null) {
				bidScoreMap.put(price.getBidderId(), 
					MathUtil.toDecimal(minPrice / price.getPrice() * maxValue, 2));
			}
		}
		for (ItemScore score: scores) {
			score.setScore(bidScoreMap.get(score.getBidderId()));
		}
	}

	/**
	 * 基准价法设置价格分
	 */
	private void setPriceScoresByMeanMethod(List<BidPrice> prices, 
			List<ItemScore> scores, double maxValue, 
			Double higherDeduction, Double lowerDeduction) {
		
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
			 //fix double precision problem
			 percent += 0.000001;
			 double deduction = 0.0;
			 if (percent > 0) {
				 double roundPercent = MathUtil.toDecimal(percent, 0);
				 deduction = MathUtil.toDecimal(roundPercent * higherDeduction, 2);
				 
			 } else {
				 double roundPercent = MathUtil.toDecimal(-1 * percent, 0);
				 deduction = MathUtil.toDecimal(roundPercent * lowerDeduction, 2);
			 }
			 deduction = (deduction < maxValue) ? deduction : maxValue;
			 bidderPrices.put(p.getBidderId(), 
				MathUtil.toDecimal(maxValue - deduction, 2));
		 }
		 for (ItemScore s: scores) {
			 double priceScore = bidderPrices.get(s.getBidderId());
			 s.setScore(priceScore < 0 ? 0 : priceScore);
		 }
	}

	public PageResult<PackageDto> getPageByName(Integer page, Integer limit, String name) {
		Pageable pageRequest = new PageRequest(page - 1, limit);
		Page<ProcurePackage> searchResult = null; 
		if (name == null || name.isEmpty() || name.trim().isEmpty()) {
			searchResult = packageRepo.findAll(pageRequest);
		} else {
			searchResult = packageRepo.findByNameContaining(name, pageRequest);
		}
		List<PackageDto> pkgDtos = BeanCopier.copy(searchResult.getContent(), PackageDto.class);
		setProjectName(pkgDtos);
		PageResult<PackageDto> result = new PageResult<>(searchResult.getTotalElements(), pkgDtos);
		return result;
	}

	private void setProjectName(List<PackageDto> pkgDtos) {
		if (ListUtil.isEmpty(pkgDtos)) {
			return;
		}
		List<ProjectDto> projectDtos = projectService.getByIds(
			ListUtil.extractDistinctList(pkgDtos, PackageDto::getProjectId));
		Map<Long, String> projectIdWithNameMap = ListUtil.list2Map(projectDtos, ProjectDto::getId, ProjectDto::getName);
		for (PackageDto p: pkgDtos) {
			p.setProjectName(projectIdWithNameMap.get(p.getProjectId()));
		}
	}

	public PackageDto getExpertScore(Long id, Long expertId) {
		PackageDto pkg = getById(id);
		if (pkg == null) {
			return null;
		}
		List<GradeItemDto> items = itemService.getExpertScoresByPkgId(id, expertId);
		pkg.setAllItems(items);
		return pkg;
	}
	
	public List<PackageDto> getByProjectIds(List<Long> projectIds) {
		List<ProcurePackage> pkgs = packageRepo.findByProjectIdIn(projectIds);
		return BeanCopier.copy(pkgs, PackageDto.class);
	}
	

}
