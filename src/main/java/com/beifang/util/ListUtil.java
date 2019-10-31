package com.beifang.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.beifang.model.Bidder;

public class ListUtil {
	public static <T> boolean isEmpty(List<T> list) {
		if (list == null || list.isEmpty() || list.get(0) == null) {
			return true;
		}
		return false;
	}
	
	public static <F, T> List<T> extractList(Iterable<F> list, Function<F, T> get) {
		List<T> result = new ArrayList<>();
		Iterator<F> itr = list.iterator();
		while (itr.hasNext()) {
			F f = itr.next();
			result.add(get.apply(f));
		}
		return result;
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
	
	public static void main(String[] args) {
		List<Bidder> bidders = new ArrayList<>();
		for (int i = 0; i<5 ; i++) {
			Bidder b = new Bidder();
			b.setId(i + 0L);
			b.setName((5-i) + "");
			bidders.add(b);
		}
		Map<Long, String> map = list2Map(bidders, Bidder::getId, Bidder::getName);
		for (int i =0; i<5; i++) {
			System.out.println(map.get(i + 0L));
		}
	}
	
}
