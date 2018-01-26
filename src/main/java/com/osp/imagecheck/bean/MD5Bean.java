package com.osp.imagecheck.bean;

import java.io.Serializable;

/**
 * 汉明距离实体类 Set<MD5Bean>集合使用
 * 
 * @author zhangmingcheng
 * @date 2018年1月8日
 */
public class MD5Bean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String MD5;
	// IMGID
	private String filename;

	public MD5Bean() {
		super();
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		else if (o == this)
			return true;
		else if (o instanceof MD5Bean && ((MD5Bean) o).getFilename() == this.filename
				&& ((MD5Bean) o).getMD5() == this.MD5)
			return true;
		else
			return false;
	}

	public int hashCode() {
		return MD5.hashCode();
	}

	public MD5Bean(String MD5, String filename) {
		this.MD5 = MD5;
		this.filename = filename;
	}

	public String getMD5() {
		return MD5;
	}

	public void setMD5(String mD5) {
		MD5 = mD5;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
