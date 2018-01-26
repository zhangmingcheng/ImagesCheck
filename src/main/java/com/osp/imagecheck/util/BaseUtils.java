package com.osp.imagecheck.util;

import java.io.File;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import com.osp.imagecheck.config.PythonConfig;

/**
 * 公共方法类
 * 
 * @author zhangmingcheng
 */
public class BaseUtils {

	/**
	 * 产生UUID
	 * @return
	 */
	public static String getUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	/**
	 * 产生4位值在[min,max]之间的随机整数
	 */
	public static int getRangeInt(int min, int max) {
		Random random = new Random();
		return random.nextInt(max) % (max - min + 1) + min;
	}


    /**
     * 获得主机ip地址
     * @return
     */
	public static String getInetAddress() {
		String ip = "";
		try {
			InetAddress address = InetAddress.getLocalHost();
			ip = address.getHostAddress();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ip;
	}

	/**
	 * 在PythonConfig.ImagesPath目录下，建立yyyy/MM/dd子目录
	 * 
	 * @return
	 */
	public static String getLocalDirectoryPath() {
		Date date = new Date();
		// 格式化并转换String类型
		String path = PythonConfig.ImagesPath + new SimpleDateFormat("yyyy/MM/dd").format(date);
		// 创建文件夹
		File f = new File(path);
		if (!f.exists()) {
			f.mkdirs();
		}
		return path;
	}

	/**
	 * 字符串!=""&&字符串=null
	 * 
	 * @param a
	 * @return
	 */
	public static Boolean stringNotEmpty(String a) {
		if (a != null && a.isEmpty() == false) {
			return true;
		}
		return false;
	}

	/**
	 * 获取当前时间:格式yyyy-MM-dd HH:mm:ss
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.util.Date date = new java.util.Date();
		return sdf.format(date);
	}
}
