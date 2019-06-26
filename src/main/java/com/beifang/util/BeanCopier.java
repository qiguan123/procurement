package com.beifang.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;


/**
 * map到map的copy有问题
 */
public class BeanCopier {
	private static Mapper mapper = DozerBeanMapperBuilder.buildDefault();
	
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
		Map<String, Map<String, String>> m1 = new HashMap<>();
		Map<String, String> m1m = new HashMap<>(); 
		m1.put("aa", m1m);
		m1m.put("bb", "cc");
		
		HashMap<String, String> copy = copy(m1m, HashMap.class);
		copy.size();
	}
}
