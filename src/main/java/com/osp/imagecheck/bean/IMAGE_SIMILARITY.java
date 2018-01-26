package com.osp.imagecheck.bean;

import java.io.Serializable;

public class IMAGE_SIMILARITY implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String F_IMGID;
	private String F_IMGID2;
	private String F_HMDIS;
	private String F_MD5SAME;// 测试

	public IMAGE_SIMILARITY() {
		super();
	}

	public IMAGE_SIMILARITY(String F_IMGID, String F_IMGID2, String F_HMDIS, String F_MD5SAME) {
		this.F_IMGID = F_IMGID;
		this.F_IMGID2 = F_IMGID2;
		this.F_HMDIS = F_HMDIS;
		this.F_MD5SAME = F_MD5SAME;
	}

	public String getF_IMGID() {
		return F_IMGID;
	}

	public void setF_IMGID(String f_IMGID) {
		F_IMGID = f_IMGID;
	}

	public String getF_IMGID2() {
		return F_IMGID2;
	}

	public void setF_IMGID2(String f_IMGID2) {
		F_IMGID2 = f_IMGID2;
	}

	public String getF_HMDIS() {
		return F_HMDIS;
	}

	public void setF_HMDIS(String f_HMDIS) {
		F_HMDIS = f_HMDIS;
	}

	public String getF_MD5SAME() {
		return F_MD5SAME;
	}

	public void setF_MD5SAME(String f_MD5SAME) {
		F_MD5SAME = f_MD5SAME;
	}

}
