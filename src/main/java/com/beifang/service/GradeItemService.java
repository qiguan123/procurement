package com.beifang.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beifang.model.GradeItem;
import com.beifang.model.ItemScore;
import com.beifang.repository.GradeItemRepository;
import com.beifang.repository.ItemScoreRepository;
import com.beifang.service.dto.GradeItemDto;
import com.beifang.service.dto.PackageDto;
import com.beifang.util.BeanCopier;
import com.beifang.util.ExcelUtil;
import com.beifang.util.ListUtil;
import com.beifang.util.func.GradeItemDtoGetIdFunction;

@Service
public class GradeItemService {
	@Autowired
	private GradeItemRepository itemRepo;
	@Autowired
	private ItemScoreRepository scoreRepo;
	
	/**
	 * 根据上传的Excel模板创建评分项、分数
	 */
	public void makeItemsWithScore(PackageDto pkg, File gradeTableFile) {
		List<List<String>> rows = ExcelUtil.readRows(gradeTableFile);
		double totalScore = 0.0;
		List<GradeItem> items = new ArrayList<>();
		//跳过 表名、表头
		for (int i = 2; i < rows.size(); i++) {
			GradeItem item = makeItemFromExcelRow(rows.get(i));
			totalScore += item.getMaxValue();
			item.setPackageId(pkg.getId());
			items.add(item);
		}
		//创建总分项
		GradeItem totalItem = new GradeItem();
		totalItem.setCategoryId(4);
		totalItem.setCategory("总分");
		totalItem.setName("总分");
		totalItem.setPackageId(pkg.getId());
		totalItem.setMaxValue(totalScore);
		items.add(totalItem);
		
		Iterable<GradeItem> savedItems = itemRepo.save(items);
		//add score
		Iterator<GradeItem> itr = savedItems.iterator();
		while(itr.hasNext()) {
			GradeItem item = itr.next();
			List<ItemScore> scores = new ArrayList<>();
			for (Long expertId: pkg.getExpertIds()) {
				for (Long bidderId: pkg.getBidderIds()) {
					scores.add(new ItemScore(item.getId(), expertId, bidderId));
				}
			}
			scoreRepo.save(scores);
		}
		
	}

	/**
	 * 行格式：
	 * 分类	二级项目	评价内容	评分标准	分值项	评审依据
	 */
	private GradeItem makeItemFromExcelRow(List<String> excelRow) {
		GradeItem item = new GradeItem();
		String category = excelRow.get(0);
		if (category.indexOf("商务") != -1) {
			item.setCategoryId(2);
		} else if (category.indexOf("技术") != -1) {
			item.setCategoryId(1);
		} else if (category.indexOf("价格") != -1) {
			item.setCategoryId(3);
		}
		item.setCategory(category);
		item.setName(excelRow.get(1));
		item.setContent(excelRow.get(2));
		item.setStandard(excelRow.get(3));
		item.setMaxValue(Double.valueOf(excelRow.get(4)));
		item.setRelateFilesName(excelRow.get(5));
		return item;
	}

	/**
	 * 查询包的价格条目、价格分数
	 */
	public List<GradeItemDto> getItemsByPkgIds(List<Long> pkgIds) {
		List<GradeItemDto> result = new ArrayList<>();
		if (ListUtil.isEmpty(pkgIds)) {
			return result;
		}
		List<GradeItem> items = itemRepo.findByPackageIdIn(pkgIds);
		List<GradeItemDto> itemDtos = BeanCopier.copy(items, GradeItemDto.class);
		List<Long> itemIds = ListUtil.extractDistinctList(itemDtos, 
				new GradeItemDtoGetIdFunction());
		List<ItemScore> scores = scoreRepo.findByItemIdIn(itemIds);
		Map<Long, GradeItemDto> idWithItemMap = ListUtil.list2Map(
				itemDtos, new GradeItemDtoGetIdFunction());
		for(ItemScore s: scores) {
			GradeItemDto pkg = idWithItemMap.get(s.getItemId());
			pkg.getScores().add(s);
			Map<Long, ItemScore> bidderPriceMap = pkg.getExpertBidderScores().get(s.getExpertId());
			if (bidderPriceMap == null) {
				bidderPriceMap = new HashMap<>();
			}
			bidderPriceMap.put(s.getBidderId(), s);
			pkg.getExpertBidderScores().put(s.getExpertId(), bidderPriceMap);
		}
		return itemDtos;
	}

	/**
	 * 设置评分项对应的所有评分
	 */
	public void updateItemScores(GradeItemDto itemDto) {
		List<ItemScore> scores = itemDto.getScores();
		scoreRepo.save(scores);
	}
	
	/**
	 * 更新分数
	 */
	public void updateScore(ItemScore score) {
		scoreRepo.save(score);
	}

	public void updateScoreList(List<ItemScore> expertScores) {
		scoreRepo.save(expertScores);
	}

}
