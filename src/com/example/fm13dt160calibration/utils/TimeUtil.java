package com.example.fm13dt160calibration.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.annotation.SuppressLint;

/**
 * 时间工具�?
 * 
 * @author Sai
 */
@SuppressLint("SimpleDateFormat")
public class TimeUtil {
	 /**
     * 日期格式：yyyy-MM-dd HH:mm:ss
     **/
    public static final String DF_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 获取当前时间
	 */
	public static String NowString() {		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式		
		return df.format(new Date());
	}
	/**
	 * 获取当前时间n年后的日�?
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String AfterNowString(int num) {		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		Date date = new Date();
		date.setYear(date.getYear()+num);
		return df.format(date);
	}
	
	public static String getTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(new Date(time));
	}
		

	public static String getTime(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}
	
	public static String getTime2(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm");
		return format.format(date);
	}
	
	public static String getTime3(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(date);
	}
	
	public static Date getDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date;
		try {
			date = sdf.parse(str);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}	
	
	public static Date getDate2(String str) {
//		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss 格林尼治标准时间+0800 yyyy", Locale.ENGLISH);
		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", new Locale("ENGLISH"));
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		Date date;
		try {
			date = sdf.parse(str);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return new Date();
	}

	public static String getTimeWithoutYear(long time) {
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		return format.format(new Date(time));
	}

	public static String getTimeWithoutYear(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		return format.format(date);
	}
	
	public static String getYearMonthDay(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date(time));
	}

	public static String getHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(new Date(time));
	}

	public static long getWebTimeToLong(String time) {
		try {
		time = time.replace("T", " ");
		time = time.substring(0, time.lastIndexOf(":"));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		
			date = format.parse(time);
			return date.getTime();
		} catch (ParseException e) {
		} catch (NullPointerException e) {
		}catch(Exception e){}
		return 0;
	}

	public static long getLong(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = format.parse(time);
			return date.getTime();
		} catch (ParseException e) {
		} catch (NullPointerException e) {
		}
		return 0;
	}
	
	public static long getLong(Date date) {
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//		Date date = null;
		try {
//			date = format.parse(time);
			return date.getTime();
//		} catch (ParseException e) {
		} catch (NullPointerException e) {
		}
		return 0;
	}
	
	public static long getLong2(String time) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		Date date = null;
		try {
			date = format.parse(time);
			return date.getTime();
		} catch (ParseException e) {
		} catch (NullPointerException e) {
		}
		return 0;
	}

	/**
	 * 将毫秒数换算成x天x时x分x秒x毫秒
	 * 
	 * @return
	 */
	public static String[] formatDHMS(long ms) {
		String[] times = new String[4];
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		int dd = hh * 24;

		long day = ms / dd;
		long hour = (ms - day * dd) / hh;
		long minute = (ms - day * dd - hour * hh) / mi;
		long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		long milliSecond = ms - day * dd - hour * hh - minute * mi - second
				* ss;

		String strDay = times[0] = day < 10 ? "0" + day : "" + day;
		String strHour = times[1] = hour < 10 ? "0" + hour : "" + hour;
		String strMinute = times[2] = minute < 10 ? "0" + minute : "" + minute;
		String strSecond = times[3] = second < 10 ? "0" + second : "" + second;
		// String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : ""
		// + milliSecond;
		// strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : ""
		// + strMilliSecond;

		// return strDay + " " + strHour + ":" + strMinute + ":" + strSecond +
		// " "
		// + strMilliSecond;
		return times;
	}
	
	/**
	 * 将毫秒数换算成x天x时x分x秒x毫秒
	 * 
	 * @return
	 */
	public static String formatDHMS2(long ms) {
		String[] times = new String[4];
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		int dd = hh * 24;

		long day = ms / dd;
		long hour = (ms - day * dd) / hh;
		long minute = (ms - day * dd - hour * hh) / mi;
		long second = (ms - day * dd - hour * hh - minute * mi) / ss;
		long milliSecond = ms - day * dd - hour * hh - minute * mi - second
				* ss;

		String strDay = times[0] = day < 10 ? "0" + day : "" + day;
		String strHour = times[1] = hour < 10 ? "0" + hour : "" + hour;
		String strMinute = times[2] = minute < 10 ? "0" + minute : "" + minute;
		String strSecond = times[3] = second < 10 ? "0" + second : "" + second;
		// String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : ""
		// + milliSecond;
		// strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : ""
		// + strMilliSecond;

		 return strDay + "d" + strHour + "h" + strMinute + "m" + strSecond +
		 "s";
//		 + strMilliSecond;
//		return times;
	}

	public static String getChatTime(long timesamp) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		Date today = new Date(System.currentTimeMillis());
		Date otherDay = new Date(timesamp);
		int temp = Integer.parseInt(sdf.format(today))
				- Integer.parseInt(sdf.format(otherDay));

		switch (temp) {
		case 0:
			result = "今天 " + getHourAndMin(timesamp);
			break;
		case 1:
			result = "昨天 " + getHourAndMin(timesamp);
			break;
		case 2:
			result = "前天 " + getHourAndMin(timesamp);
			break;

		default:
			// result = temp + "天前 ";
			result = getTime(timesamp);
			break;
		}

		return result;
	}
    /**
     * 将日期以yyyy-MM-dd HH:mm:ss格式�?
     *
     * @param dateL 日期
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String formatDateTime(long dateL) {
        SimpleDateFormat sdf = new SimpleDateFormat(DF_YYYY_MM_DD_HH_MM_SS);
        Date date = new Date(dateL);
        return sdf.format(date);
    }
    
    public static int[] getTimeHex() {
        int[] ints = new int[4];
        long aLong = System.currentTimeMillis() / 1000;
        String string = Long.toHexString(aLong);
        int a = Integer.parseInt(string.substring(0, 2), 16),
                b = Integer.parseInt(string.substring(2, 4), 16),
                c = Integer.parseInt(string.substring(4, 6), 16),
                d = Integer.parseInt(string.substring(6, 8), 16);
//        LogUtil.d(string);
        ints[0] = a;
        ints[1] = b;
        ints[2] = c;
        ints[3] = d;
        return ints;
    }
}
