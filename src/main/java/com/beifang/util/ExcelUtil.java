package com.beifang.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class ExcelUtil {

	/**
	 * 读每一行，
	 * 空着的单元格取值为空字符串
	 */
	public static List<List<String>> readRows(File tableFile) {
		List<List<String>> result = new ArrayList<>();
		try (POIFSFileSystem fs = new POIFSFileSystem(tableFile);
			HSSFWorkbook wb = new HSSFWorkbook(fs.getRoot(), false);) {
			
			Sheet sheet = wb.getSheetAt(0);
			int rowStart = sheet.getFirstRowNum();
			int rowEnd = sheet.getLastRowNum();
			for (int i = rowStart; i <= rowEnd; i++) {
				Row row = sheet.getRow(i);
				List<String> cellValues = new ArrayList<>();
				short cellStart = row.getFirstCellNum();
				short cellEnd = row.getLastCellNum();
				for (int j = cellStart; j <= cellEnd; j++) {
					Cell cell = row.getCell(j);
					if (cell == null) {
						cellValues.add("");
					} else {
						cellValues.add(getCellStringValue(cell));
					}
				}
				result.add(cellValues);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static String getCellStringValue(Cell c) {
		CellType cellTypeEnum = c.getCellTypeEnum();
		if (cellTypeEnum == CellType.STRING) {
			return c.getStringCellValue();
		} else if (cellTypeEnum == CellType.NUMERIC) {
			return c.getNumericCellValue() + "";
		} else if (cellTypeEnum == CellType.FORMULA) {
			return c.getCellFormula();
		}
		return "";
	}

	public static void main(String[] args) {
		List<List<String>> rows = readRows(new File("C:\\Users\\hasee\\Desktop\\test.xls"));
		System.out.println(rows.get(5).get(4));
		System.out.println(rows.get(3).get(2));
	}

	public static Sheet newSheet(Workbook wb, String name) {
		return wb.createSheet(name);
	}

	public static void writeRow(Sheet sheet, int start, List<String> rowContent) {
		Row row = sheet.createRow(start);
		for (int i = 0; i < rowContent.size(); i++) {
			row.createCell(i).setCellValue(rowContent.get(i));
		}
	}

	public static void writeRows(Sheet sheet, int start, List<List<String>> rows) {
		for (int i = 0; i < rows.size(); i++) {
			writeRow(sheet, start + i, rows.get(i));
		}
	}
}
