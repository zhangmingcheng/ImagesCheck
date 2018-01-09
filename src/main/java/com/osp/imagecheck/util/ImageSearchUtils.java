package com.osp.imagecheck.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
	private static List<String> imagelists = new ArrayList<String>();
	private static Map<String, LinkedList<String>> data = new HashMap<String, LinkedList<String>>();

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
	public static void addImageDhash(String[] imagesDhash) {
		String[] test = new String[8];
		for (String s : imagesDhash) {
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
	 * 组织data
	 * 
	 * @param test
	 * @param s
	 */
	public static void addLinkedList(String[] test, String s) {
		for (int i = 0; i < 8; i++) {
			if (data.get(test[i]) == null) {
				LinkedList<String> valueList = new LinkedList<String>();
				valueList.add(s);
				data.put(test[i], valueList);
			} else {
				LinkedList<String> valueList = data.get(test[i]);
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
	public Set<HamBean> toSearch(String searchImage) {
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
			System.out.println("target===="+target);
			if (data.get(target) != null) {
				LinkedList<String> valueLists = data.get(target);
				for (String value : valueLists) {
					int count = 0;
					count += ImageSearchUtils.toCompare(searchImage.substring(0, 8), value.substring(0, 8));
					count += ImageSearchUtils.toCompare(searchImage.substring(8, 16), value.substring(8, 16));
					if (count < 5) {
						sets.add(new HamBean(value, count));
					}
				}
			}
		}
		return sets;
	}

	public static int toCompare(String s1, String s2) {
		long result = Long.parseLong(s1, 16) ^ Long.parseLong(s2, 16);
		int num = 0;
		while (result != 0) {
			if (result % 2 == 1) {
				num++;
			}
			result = result / 2;
		}
		return num;
	}

	public static void main(String[] args) throws IOException {
		ImageSearchUtils.toInit();
		/*
		 * Set<String> set = ImageSearchUtils.toSearch(
		 * "1101001010010101000111010111001110101010011001101011001010111101"); for
		 * (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
		 * System.out.println(iterator.next()); }
		 */
	}

	public static List<String> getImagelists() {
		return imagelists;
	}

	public static void setImagelists(List<String> imagelists) {
		ImageSearchUtils.imagelists = imagelists;
	}

	public static Map<String, LinkedList<String>> getData() {
		return data;
	}

	public static void setData(Map<String, LinkedList<String>> data) {
		ImageSearchUtils.data = data;
	}
}
