package com.example.fm13dt160calibration.utils;

import java.text.DecimalFormat;
import java.util.Locale;


import android.R.integer;
import android.content.Context;

public class DataUtil {

	/**
	 * Format double number 
	 * 
	 * @param num number which to format
	 * @param format eg."0.00" or "0.0"
	 * @return string after format
	 */
	public static String formatNumber(int number) {
		return String.format(Locale.getDefault(), "%4s", number);
	}
	
	public static String formatDouble (double num, String format) {
		DecimalFormat decimalFormat = new DecimalFormat(format);
		return  decimalFormat.format(num);
	}
	
	public static String formatTemperature(double temperature) {
		return String.format(Locale.getDefault(), "%.1f �?", temperature);
	}
	
	public static String formatTemperature2(double temperature) {
		return String.format(Locale.getDefault(), "%.1f�?", temperature);
	}
	
	public static String formatTemperature(int temperature) {
		return String.format(Locale.getDefault(), "%1d �?", temperature);
	}
	
	public static String formatVoltage(double voltage) {
		return String.format(Locale.getDefault(), "%.2f V", voltage);
	}
	
//	public static String formatInterval(Context context, int interval) {
//		if(interval % 60 == 0)
//		   return context.getString(R.string.interval_unit, interval/60);
//		else
//		   return context.getString(R.string.interval_unit2, interval);
//		
//	}
	
//	public static int convertecondToMinute(int second) {
//		if (second % 60 == 0) {

//			return second / 60; 
//		} else {

//			return second ;
//		}
//	}

	public static String byteArrayToString(byte[] byteArray) {
		StringBuffer sb = new StringBuffer();
		for(byte b :byteArray) {
			sb.append((""+(byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
	                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
	                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
	                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1)));
		}
	
		String s =  sb.toString();
		return s;
	}
	// Hex help
    private static final byte[] HEX_CHAR_TABLE = {(byte) '0', (byte) '1',
            (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6',
            (byte) '7', (byte) '8', (byte) '9', (byte) 'A', (byte) 'B',
            (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F'};
    
	 public static String getHexString(byte[] raw, int len) {
	        byte[] hex = new byte[2 * len];
	        int index = 0;
	        int pos = 0;

	        for (byte b : raw) {
	            if (pos >= len)
	                break;

	            pos++;
	            int v = b & 0xFF;
	            hex[index++] = HEX_CHAR_TABLE[v >>> 4];
	            hex[index++] = HEX_CHAR_TABLE[v & 0xF];
	        }

	        return new String(hex);
	    }
	 /**
	  * 压缩模式0的解�?
	  * @param str
	  * @return
	  */
	 public static float strFromat0(String str) {
	        float result = 0;
//	        LogUtil.d(str);
	        int temp=Integer.parseInt(str, 16);
	        if ((temp&0x80)==0x80) {
				int i=-((0xff-temp)&0x7f)-1;
				result=(float)(i/1.0);
			}else{
				result = (float)(temp/1.0);
			}
	        
//	      LogUtil.d(result+"");
	        return result;
	 }
	 /**
	  * 正常模式
	  * @param str
	  * @return
	  */
	 public static float strFromat(String str) {
	        float result = 0;
//	        LogUtil.d(str);
	        String substring = str.substring(str.length() - 4, str.length() - 2);
	        String substring1 = str.substring(str.length() - 2, str.length());
	        String newstr = substring1 + substring;
	        
	        String stringToBinary = hexStringToBinary(newstr);
	        String tempData = stringToBinary.substring(stringToBinary.length() - 10);

	        String bStr = tempData.substring(1);
	        String hexString = binaryString2hexString("0000000" + bStr);

	        char[] chars = substring1.toCharArray();
	        char nu = '2';
	        if (chars[1] >= nu) {

	            int i = -((0xffff - Integer.parseInt(newstr, 16)) & 0x03ff) - 1;
	            result = (float) (i / 4.00);
	        } else {
	            int a = Integer.parseInt(hexString, 16);
	            result = (float) (a / 4.00);
	        }
//	      LogUtil.d(result+"");
	        return result;
	 }
	 
	 /**
	  * 超限模式2的解�?
	  * @param str
	  * @return
	  */
	 public static float strFromat6(String str) {
	        float result = 0;
//	        LogUtil.d(str);
	        int temp=Integer.parseInt(str, 16);
	        result = temp&0x07;
	        
//	      LogUtil.d(result+"");
	        return result;
	 }
	 
	 public static int[] getCount(int count) {
	        int[] ints = {0, 0};
	        if (count <= 255) {
	            ints[1] = count;
	        } else {
	            ints[0] = count / 256;
	            ints[1] = count % 256;
	        }
	        return ints;
	 }
	 
	 /**
	     * 十六转二进制
	     * 
	     * @param hex
	     *            十六进制字符�?
	     * @return 二进制字符串
	     */
	    public static String hexStringToBinary(String hex) {
	        hex = hex.toUpperCase();
	        String result = "";
	        int max = hex.length();
	        for (int i = 0; i < max; i++) {
	            char c = hex.charAt(i);
	            switch (c) {
	            case '0':
	                result += "0000";
	                break;
	            case '1':
	                result += "0001";
	                break;
	            case '2':
	                result += "0010";
	                break;
	            case '3':
	                result += "0011";
	                break;
	            case '4':
	                result += "0100";
	                break;
	            case '5':
	                result += "0101";
	                break;
	            case '6':
	                result += "0110";
	                break;
	            case '7':
	                result += "0111";
	                break;
	            case '8':
	                result += "1000";
	                break;
	            case '9':
	                result += "1001";
	                break;
	            case 'A':
	                result += "1010";
	                break;
	            case 'B':
	                result += "1011";
	                break;
	            case 'C':
	                result += "1100";
	                break;
	            case 'D':
	                result += "1101";
	                break;
	            case 'E':
	                result += "1110";
	                break;
	            case 'F':
	                result += "1111";
	                break;
	            }
	        }
	        return result;
	    }
	    
	    /**
	     *
	     * @param bString
	     * @return 将二进制转换为十六进制字符输�?
	     */
	    public static String binaryString2hexString(String bString) {
	        if (bString == null || bString.equals("") || bString.length() % 8 != 0){
	        }
	        StringBuffer tmp=new StringBuffer();
	        int iTmp = 0;
	        for (int i = 0; i < bString.length(); i += 4) {
	            iTmp = 0;
	            for (int j = 0; j < 4; j++) {
	                iTmp += Integer.parseInt(bString.substring(i + j, i + j + 1)) << (4 - j - 1);
	            }
	            tmp.append(Integer.toHexString(iTmp));
	        }
	        return tmp.toString();
	    }
	    
	    public static double calibrateTable(int b) {
	    	 double result = 0.0;
	    	 switch (b) {
	            case 0:
	                result = 2.8125;
	                break;
	            case 1:
	                result = 3.03125;
	                break;
	            case 2:
	                result = 3.25;
	                break;
	            case 3:
	                result = 3.4375;
	                break;
	            case 4:
	                result = 4.125;
	                break;
	            case 5:
	                result = 4.25;
	                break;
	            case 6:
	                result = 4.375;
	                break;
	            case 7:
	                result = 5.0;
	                break;
	            case 8:
	                result = 5.25;
	                break;
	            case 9:
	                result = 5.375;
	                break;
	            case 10:
	                result = 5.5;
	                break;
	            case 11:
	                result = 5.625;
	                break;
	            case 12:
	                result = 5.85;
	                break;
	            case 13:
	                result = 6.0;
	                break;
	            case 14:
	                result = 7.5;
	                break;
	            case 15:
	                result = 8.05;
	                break;
	            }
	    	 return result;
		}
	
}
