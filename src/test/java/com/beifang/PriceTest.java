package com.beifang;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.beifang.util.ExcelUtil;

public class PriceTest {
	public static void main(String[] args) {
		File file = new File("C:\\Users\\hasee\\Desktop\\bb.xls");
		List<List<String>> rows = ExcelUtil.readRows(file);
		Set<String> unique = new HashSet<>();
		for (List<String> row : rows) {
			for (String c : row) {
				if (!unique.add(c.trim())) {
					System.out.println(c.trim());
				}
			}
		}
	}
}
