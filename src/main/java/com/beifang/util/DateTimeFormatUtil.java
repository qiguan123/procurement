package com.beifang.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeFormatUtil {
	private static ThreadLocal<SimpleDateFormat> f = new ThreadLocal<>();
	
	public static String format(Date d) {
		return get().format(d);
	}
	
	private static SimpleDateFormat get() {
		SimpleDateFormat format = f.get();
		if (format == null) {
			format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			f.set(format);
		}
		return format;
	}
}
