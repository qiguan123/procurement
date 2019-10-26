package com.beifang.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ListUtil {
	public static <T> boolean isEmpty(List<T> list) {
		if (list == null || list.isEmpty() || list.get(0) == null) {
			return true;
		}
		return false;
	}
	
	public static <F, T> List<T> extractDistinctList(Iterable<F> list, Function<F, T> get) {
		List<T> result = new ArrayList<>();
		Map<T, F> map = list2Map(list, get);
		result.addAll(map.keySet());
		return result;
	}
	
	public static <F, T> Map<T, F> list2Map(Iterable<F> list, Function<F, T> key) {
		Map<T, F> result = new LinkedHashMap<>();
		Iterator<F> itr = list.iterator();
		while (itr.hasNext()) {
			F f = itr.next();
			result.put(key.apply(f), f);
		}
		return result;
	}
	
	public static <F, K, V> Map<K, V> list2Map(Iterable<F> list, Function<F, K> key, Function<F, V> value) {
		Map<K, V> result = new LinkedHashMap<>();
		Iterator<F> itr = list.iterator();
		while (itr.hasNext()) {
			F f = itr.next();
			result.put(key.apply(f), value.apply(f));
		}
		return result;
	}
	
}
