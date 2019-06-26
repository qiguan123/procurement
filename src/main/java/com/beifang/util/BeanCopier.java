package com.beifang.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;


/**
 * map到map的copy有问题
 */
public class BeanCopier {
	private static Mapper mapper = new DozerBeanMapper();
	
	public static <T> T copy(Object obj, Class<T> clazz) {
		if (obj == null) {
			return null;
		}
		return mapper.map(obj, clazz);
	}
	
	public static <T, L> List<T> copy(Iterable<L> src, Class<T> clazz) {
		List<T> result = new ArrayList<>();
		if (src == null) {
			return result;
		}
		for (L s: src) {
			result.add(copy(s, clazz));
		}
		return result;
	}
	
	public static void main(String[] args) {
		LinkedHashMap<String, LinkedHashMap<String, String>> m1 = new LinkedHashMap<>();
		LinkedHashMap<String, String> m1m = new LinkedHashMap<>(); 
		m1.put("aa", m1m);
		m1m.put("bb", "cc");
		
		LinkedHashMap<String, String> copy = copy(m1m, m1m.getClass());
		copy.size();
	}
}
