package com.osp.imagecheck.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.osp.imagecheck.bean.HamBean;

/**
 * 计算汉明距离工具类
 * 
 * @author zhangmingcheng
 * @date 2018年1月8日
 */
public class ImageSearchUtils {

	/**
	 * 这个在确定下是否需要
	 */
	private static List<String> imagelists = new ArrayList<String>();

	/**
	 * 存储Dhash值
	 */
	private static Map<String, HashSet<String>> data = new HashMap<String, HashSet<String>>();

	public ImageSearchUtils() {
		super();
	}

	/**
	 * 初始化imagelists,并组织data
	 */
	public static void toInit() {
		String[] test = new String[8];
		for (String s : imagelists) {
			test[0] = s.substring(0, 2);
			test[1] = s.substring(2, 4);
			test[2] = s.substring(4, 6);
			test[3] = s.substring(6, 8);
			test[4] = s.substring(8, 10);
			test[5] = s.substring(10, 12);
			test[6] = s.substring(12, 14);
			test[7] = s.substring(14, 16);
			ImageSearchUtils.addLinkedList(test, s);
		}
	}

	/**
	 * 将上传图片的dhash值组织到data
	 * 
	 * @param imagesDhash
	 */
	public static void addImageDhash(String imagesDhash) {
		String[] test = new String[8];
		test[0] = imagesDhash.substring(0, 2);
		test[1] = imagesDhash.substring(2, 4);
		test[2] = imagesDhash.substring(4, 6);
		test[3] = imagesDhash.substring(6, 8);
		test[4] = imagesDhash.substring(8, 10);
		test[5] = imagesDhash.substring(10, 12);
		test[6] = imagesDhash.substring(12, 14);
		test[7] = imagesDhash.substring(14, 16);
		ImageSearchUtils.addLinkedList(test, imagesDhash);
	}

	/**
	 * 组织data
	 * 
	 * @param test
	 * @param s
	 */
	public static void addLinkedList(String[] test, String s) {
		for (int i = 0; i < 8; i++) {
			if (data.get(test[i]) == null) {
				HashSet<String> valueList = new HashSet<String>();
				valueList.add(s);
				data.put(test[i], valueList);
			} else {
				HashSet<String> valueList = data.get(test[i]);
				valueList.add(s);
				data.put(test[i], valueList);
			}
		}
	}

	/**
	 * 计算图片汉明距离
	 * 
	 * @param searchImage
	 * @return
	 */
	public static Set<HamBean> toSearch(String searchImage, Integer IMG_HMDIS) {
		Set<String> targets = new HashSet<String>();
		targets.add(searchImage.substring(0, 2));
		targets.add(searchImage.substring(2, 4));
		targets.add(searchImage.substring(4, 6));
		targets.add(searchImage.substring(6, 8));
		targets.add(searchImage.substring(8, 10));
		targets.add(searchImage.substring(10, 12));
		targets.add(searchImage.substring(12, 14));
		targets.add(searchImage.substring(14, 16));
		Set<HamBean> sets = new HashSet<HamBean>();
		for (String target : targets) {
			if (data.get(target) != null) {
				HashSet<String> valueLists = data.get(target);
				for (String value : valueLists) {
					int count = 0;
					count += ImageSearchUtils.toCompare(searchImage.substring(0, 8), value.substring(0, 8));
					count += ImageSearchUtils.toCompare(searchImage.substring(8, 16), value.substring(8, 16));
					if (count < IMG_HMDIS) {
						sets.add(new HamBean(value, count));
					}
				}
			}
		}
		return sets;
	}

	/**
	 * 计算汉明距离
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static int toCompare(String s1, String s2) {
		long result = Long.parseLong(s1, 16) ^ Long.parseLong(s2, 16);
		int num = 0;
		while (result != 0) {
			num += 1;
			result &= result - 1;
		}
		return num;
	}

	public static List<String> getImagelists() {
		return imagelists;
	}

	public static void setImagelists(List<String> imagelists) {
		ImageSearchUtils.imagelists = imagelists;
	}

	public static Map<String, HashSet<String>> getData() {
		return data;
	}

	public static void setData(Map<String, HashSet<String>> data) {
		ImageSearchUtils.data = data;
	}
}