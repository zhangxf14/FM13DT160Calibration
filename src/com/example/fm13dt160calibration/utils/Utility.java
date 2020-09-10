package com.example.fm13dt160calibration.utils;

import java.util.Arrays;

public class Utility {	
	
	/// <summary>
    /// byte[]转LSB格式的二进制字符�?
    /// </summary>
    /// <param name="data"></param>
    /// <returns></returns>
    public static String LSB_Bytes2BinString(byte[] data)
    {
        String lsb = "";
        String ZERO="00000000";
        for (int i = 0; i < data.length; i++)
        {
        	String   s   =   Integer.toBinaryString(data[i]);
			if   (s.length()   >
			8)   {
				s   =   s.substring(s.length() - 8);
			}   else   if   (s.length()  
					<   8)   {
				s   =   ZERO.substring(s.length()) + s;
			}
        	   	
        	
			lsb=s;	
        }
        
        return lsb;
    }
    
    /// <summary>
    /// byte[]�?16进制字符�?
    /// </summary>
    /// <param name="data"></param>
    /// <returns></returns>
    public static String Bytes2HexString(byte[] data)
    {
    	StringBuilder stringBuilder = new StringBuilder("");  
        if (data == null || data.length <= 0) {  
            return null;  
        }  
        for (int i = 0; i < data.length; i++) {  
            int v = data[i] & 0xFF;  
            String hv = Integer.toHexString(v);  
            if (hv.length() < 2) {  
                stringBuilder.append(0);  
            }  
            stringBuilder.append(hv);  
    }
     
    return stringBuilder.toString();  
    }
    
  /// <summary>
    /// 十六进制字符串转LSB格式的byte[]
    /// </summary>
    /// <param name="hex"></param>
    /// <returns></returns>
    public static byte[] LSB_HexString2Bytes(String hex)
    {
        byte[] data =  HexString2Bytes(hex);//HexString2Bytes(hex);
        byte[] lsbBytes = new byte[data.length];

        for (int i = 0; i < lsbBytes.length; i++)
        {
            lsbBytes[i] = (byte) data[lsbBytes.length - 1 - i];
        }
        return lsbBytes;
    }
	  
    /** 
     * Convert hex string to byte[] 
     * @param hexString the hex string 
     * @return byte[] 
     */  
    public static byte[] HexString2Bytes(String hexString) {  
        if (hexString == null || hexString.equals("")) {  
            return null;  
        }  
        hexString = hexString.toUpperCase();  
        int length = hexString.length() / 2;  
        char[] hexChars = hexString.toCharArray();  
        byte[] d = new byte[length];  
        for (int i = 0; i < length; i++) {  
            int pos = i * 2;  
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
        }  
        return d;  
    }  
    
    /** 
     * Convert char to byte 
     * @param c char 
     * @return byte 
     */  
     private static byte charToByte(char c) {  
        return (byte) "0123456789ABCDEF".indexOf(c);  
    }  
    public static String LSB_HexString2MSB_HexString(String str){
    	byte[] byteStr =LSB_HexString2Bytes(str);
		str = Bytes2HexString(byteStr);
		return str;
    }
    /** 
     * 将int数�?�转换为占四个字节的byte数组，本方法适用�?(低位在前，高位在�?)的顺序�?? 和bytesToInt（）配套使用
     * @param value 
     *            要转换的int�?
     * @return byte数组
     */  
	public static byte[] intToBytes( int value ) 
	{ 
		byte[] src = new byte[4];
		src[3] =  (byte) ((value>>24) & 0xFF);
		src[2] =  (byte) ((value>>16) & 0xFF);
		src[1] =  (byte) ((value>>8) & 0xFF);  
		src[0] =  (byte) (value & 0xFF);				
		return src; 
	}
	 /** 
     * 将int数�?�转换为占四个字节的byte数组，本方法适用�?(高位在前，低位在�?)的顺序�??  和bytesToInt2（）配套使用
     */  
	public static byte[] intToBytes2(int value) 
	{ 
		byte[] src = new byte[4];
		src[0] = (byte) ((value>>24) & 0xFF);
		src[1] = (byte) ((value>>16)& 0xFF);
		src[2] = (byte) ((value>>8)&0xFF);  
		src[3] = (byte) (value & 0xFF);		
		return src;
	}
	

	 /** 
	     * byte数组中取int数�?�，本方法�?�用�?(低位在前，高位在�?)的顺序，和和intToBytes（）配套使用
	     *  
	     * @param src 
	     *            byte数组 
	     * @param offset 
	     *            从数组的第offset位开�? 
	     * @return int数�?? 
	     */  
		public static int bytesToInt(byte[] src, int offset) {
			int value;	
			value = (int) ((src[offset] & 0xFF) 
					| ((src[offset+1] & 0xFF)<<8) 
					| ((src[offset+2] & 0xFF)<<16) 
					| ((src[offset+3] & 0xFF)<<24));
			return value;
		}
		
		 /** 
	     * byte数组中取int数�?�，本方法�?�用�?(低位在后，高位在�?)的顺序�?�和intToBytes2（）配套使用
	     */
		public static int bytesToInt2(byte[] src, int offset) {
			int value;	
			value = (int) ( ((src[offset] & 0xFF)<<24)
					|((src[offset+1] & 0xFF)<<16)
					|((src[offset+2] & 0xFF)<<8)
					|(src[offset+3] & 0xFF));
			return value;
		}
		/**
		 * 
		 * @param s
		 * @param length
		 * @return
		 */
		protected String padLeft(String s, int length)
	    {
	        byte[] bs = new byte[length];
	        byte[] ss = s.getBytes();
	        Arrays.fill(bs, (byte) (48 & 0xFF));
	        System.arraycopy(ss, 0, bs,length - ss.length, ss.length);
	        return new String(bs);
	    }
		
		 /// <summary>
	     /// int转byte
	     /// </summary>
	     /// <param name="value"></param>
	     /// <returns></returns>
	     public static byte[] intTo2Bytes(int value)
	     {
	         byte[] src = new byte[2];
	         //src[3] = (byte)((value >> 24) & 0xFF);
	         //src[2] = (byte)((value >> 16) & 0xFF);
	         src[0] = (byte)((value >> 8) & 0xFF);
	         src[1] = (byte)(value & 0xFF);
	         return src;
	     }
	     /**
	      * int�?2字节
	      * 低位在前�? 高位在后
	      * @param value
	      * @return
	      */
	     public static byte[] intTo2Bytes2(int value)
	     {
	         byte[] src = new byte[2];
	         src[0] = (byte)(value & 0xFF);
	         src[1] = (byte)((value >> 8) & 0xFF);
	         //src[2] = (byte)((value >> 16) & 0xFF);
	         //src[3] = (byte)((value >> 24) & 0xFF);
	        
	         return src;
	     }
}
