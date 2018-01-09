package com.osp.imagecheck.util;

/**
 * 进制转换
 * 
 * @author zhangmingcheng
 * @date 2018年1月5日
 */
public class NumberConversionUtils {

	/**
	 * 16进制转2进制
	 * @param hexString
	 * @return
	 */
	public static String hexString2binaryString(String hexString) {
		if (hexString == null || hexString.length() % 2 != 0)
			return null;
		String bString = "", tmp;
		for (int i = 0; i < hexString.length(); i++) {
			tmp = "0000" + Integer.toBinaryString(Integer.parseInt(hexString.substring(i, i + 1), 16));
			bString += tmp.substring(tmp.length() - 4);
		}
		return bString;
	}
}
