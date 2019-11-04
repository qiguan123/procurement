package com.beifang.util;

public class MathUtil {
	/**
	 * 仅针对正数四舍五入
	 * @param count 保留小数位数
	 */
	public static double toDecimal(double x, int count) {
		if (x < 0) {
			throw new RuntimeException("cannot be negtive");
		}
		double times = Math.pow(10, count);
		return Math.round(x * times) / times;
	}
	
	public static void main(String[] args) {
		System.out.println(0.1);
		System.out.println(0.1 + 0.1);
		System.out.println(1000.1 + 0.1 + 0.1);
		System.out.println(toDecimal(1000.1 + 0.1 + 0.1333333, 3));
		System.out.println(toDecimal(-1.5, 0));
	}
}
