package com.osp.imagecheck.bean;
/**
 * Dhash实体bean
 * @author zhangmingcheng
 * @date 2018年1月5日
 */
public class DhashBean {
	private String name;
	private String dhash;

	public DhashBean() {
		super();
	}

	public DhashBean(String name, String dhash) {
		this.name = name;
		this.dhash = dhash;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDhash() {
		return dhash;
	}

	public void setDhash(String dhash) {
		this.dhash = dhash;
	}
}
