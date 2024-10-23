package com.ymtz.commonlib.utils;


import android.util.Log;

public final class Logger {

	public static void v(String tag, String format, Object... args) {
		String message = String.format(format, args);
		Log.v(tag, message);
	}

	public static void d(String tag, String format, Object... args) {
		String message = String.format(format, args);
		Log.d(tag, message);
	}

	public static void i(String tag, String format, Object... args) {
		String message = String.format(format, args);
		Log.i(tag, message);
	}

	public static void w(String tag, String format, Object... args) {
		String message = String.format(format, args);
		Log.w(tag, message);
	}
	public static void e(String tag, String format, Object... args) {
		String message = String.format(formatPercent(format), args);
		Log.e(tag, message);
	}

	private static String formatPercent(String format) {
		if (format.contains("%")) {
			int pos = 0;
			while((pos = format.indexOf("%", pos)) != -1) {
				format = format.substring(0, pos) + "%" + format.substring(pos);
				pos += 2;
			}
        }
        return format;
    }

}
