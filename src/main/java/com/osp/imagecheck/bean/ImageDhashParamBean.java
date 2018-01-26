package com.osp.imagecheck.bean;

import java.io.Serializable;

/**
 * /images/search接口参数
 * 
 * @author zhangmingcheng
 */
public class ImageDhashParamBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String IMG_ID;
	private String IMG_OBJID;
	private String IMG_GROUPID;
	private String STORAGE_TYPE;
	private String IMG_MD5;
	private Integer IMG_HMDIS;
	private String IMG_DAYS;
	private String IMG_ZZJG;
	private String IMG_TYPE;

	public ImageDhashParamBean() {
		super();
	}

	public ImageDhashParamBean(String IMG_ID, String IMG_OBJID, String IMG_GROUPID, String STORAGE_TYPE, String IMG_MD5,
			Integer IMG_HMDIS, String IMG_DAYS, String IMG_ZZJG, String IMG_TYPE) {
		this.IMG_ID = IMG_ID;
		this.IMG_OBJID = IMG_OBJID;
		this.IMG_GROUPID = IMG_GROUPID;
		this.STORAGE_TYPE = STORAGE_TYPE;
		this.IMG_MD5 = IMG_MD5;
		this.IMG_HMDIS = IMG_HMDIS;
		this.IMG_DAYS = IMG_DAYS;
		this.IMG_ZZJG = IMG_ZZJG;
		this.IMG_TYPE = IMG_TYPE;
	}

	public String getIMG_OBJID() {
		return IMG_OBJID;
	}

	public void setIMG_OBJID(String iMG_OBJID) {
		IMG_OBJID = iMG_OBJID;
	}

	public String getIMG_GROUPID() {
		return IMG_GROUPID;
	}

	public void setIMG_GROUPID(String iMG_GROUPID) {
		IMG_GROUPID = iMG_GROUPID;
	}

	public String getSTORAGE_TYPE() {
		return STORAGE_TYPE;
	}

	public void setSTORAGE_TYPE(String sTORAGE_TYPE) {
		STORAGE_TYPE = sTORAGE_TYPE;
	}

	public String getIMG_MD5() {
		return IMG_MD5;
	}

	public void setIMG_MD5(String iMG_MD5) {
		IMG_MD5 = iMG_MD5;
	}

	public Integer getIMG_HMDIS() {
		return IMG_HMDIS;
	}

	public void setIMG_HMDIS(Integer iMG_HMDIS) {
		IMG_HMDIS = iMG_HMDIS;
	}

	public String getIMG_DAYS() {
		return IMG_DAYS;
	}

	public void setIMG_DAYS(String iMG_DAYS) {
		IMG_DAYS = iMG_DAYS;
	}

	public String getIMG_ZZJG() {
		return IMG_ZZJG;
	}

	public void setIMG_ZZJG(String iMG_ZZJG) {
		IMG_ZZJG = iMG_ZZJG;
	}

	public String getIMG_TYPE() {
		return IMG_TYPE;
	}

	public void setIMG_TYPE(String iMG_TYPE) {
		IMG_TYPE = iMG_TYPE;
	}

	public String getIMG_ID() {
		return IMG_ID;
	}

	public void setIMG_ID(String iMG_ID) {
		IMG_ID = iMG_ID;
	}

}
