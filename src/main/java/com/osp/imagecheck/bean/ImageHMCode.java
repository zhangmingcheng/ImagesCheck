package com.osp.imagecheck.bean;

import java.io.Serializable;
import java.util.Date;

public class ImageHMCode implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String f_IMGID;
	private String f_HMCODE;
	private String f_MD5;
	private String f_ZZJG;
	private Date f_CRTIME;

	public ImageHMCode() {
		super();
	}

	public ImageHMCode(String f_IMGID, String f_HMCODE, String f_MD5) {
		this.f_HMCODE = f_HMCODE;
		this.f_IMGID = f_IMGID;
		this.f_MD5 = f_MD5;
	}

	public String getF_IMGID() {
		return f_IMGID;
	}

	public void setF_IMGID(String f_IMGID) {
		this.f_IMGID = f_IMGID;
	}

	public String getF_HMCODE() {
		return f_HMCODE;
	}

	public void setF_HMCODE(String f_HMCODE) {
		this.f_HMCODE = f_HMCODE;
	}

	public String getF_MD5() {
		return f_MD5;
	}

	public void setF_MD5(String f_MD5) {
		this.f_MD5 = f_MD5;
	}

	public String getF_ZZJG() {
		return f_ZZJG;
	}

	public void setF_ZZJG(String f_ZZJG) {
		this.f_ZZJG = f_ZZJG;
	}

	public Date getF_CRTIME() {
		return f_CRTIME;
	}

	public void setF_CRTIME(Date f_CRTIME) {
		this.f_CRTIME = f_CRTIME;
	}
}
