package com.osp.imagecheck.bean;

import java.io.Serializable;

/**
 * 汉明距离实体类 Set<HamBean>集合使用
 * 
 * @author zhangmingcheng
 * @date 2018年1月8日
 */
public class HamBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String dhash;
	private String filename;
	private int hamcount;

	public HamBean() {
		super();
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		else if (o == this)
			return true;
		else if (o instanceof HamBean && ((HamBean) o).getDhash() == this.dhash)
			return true;
		else
			return false;
	}

	public int hashCode() {
		return dhash.hashCode();
	}

	public HamBean(String dhash, int hamcount) {
		this.dhash = dhash;
		this.hamcount = hamcount;
	}

	public HamBean(String filename, String dhash, int hamcount) {
		this.filename = filename;
		this.dhash = dhash;
		this.hamcount = hamcount;
	}

	public String getDhash() {
		return dhash;
	}

	public void setDhash(String dhash) {
		this.dhash = dhash;
	}

	public int getHamcount() {
		return hamcount;
	}

	public void setHamcount(int hamcount) {
		this.hamcount = hamcount;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
