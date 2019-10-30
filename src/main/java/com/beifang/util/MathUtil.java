package com.beifang.util;

public class MathUtil {
	public static double toDecimal(double x, int count) {
		double times = Math.pow(10, count);
		return Math.round(x * times) / times;
	}
	
	public static void main(String[] args) {
		System.out.println(0.1);
		System.out.println(0.1 + 0.1);
		System.out.println(1000.1 + 0.1 + 0.1);
		System.out.println(toDecimal(1000.1 + 0.1 + 0.1333333, 3));
	}
}
