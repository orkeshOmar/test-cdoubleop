package com.dz.coop.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	public final static String formatyyyyMMdd="yyyyMMdd"; 

	public static String getToday(String format) {
		SimpleDateFormat sf = new SimpleDateFormat(format);
		return sf.format(new Date());
	}

}
