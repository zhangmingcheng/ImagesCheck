package com.osp.imagecheck.dao;

import java.util.List;
import java.util.Set;

import com.osp.imagecheck.bean.HamBean;
import com.osp.imagecheck.bean.ImageHMCode;
import com.osp.imagecheck.bean.MD5Bean;

public interface ImageDao {

	/**
	 * 启动应用，数据初始化
	 * 
	 * @return
	 */
	List<ImageHMCode> selectFromSYS_IMAGE_HMCODE();

	/**
	 * 插入相似图片
	 * 
	 * @param imageid
	 * @param md5Beans
	 * @return
	 */
	int insertMd5ToSYS_IMAGE_SIMILARITY(String imageid, Set<MD5Bean> md5Beans);

	/**
	 * 更新表SYS_IMAGE_HMCODE
	 * 
	 * @param iMG_ID
	 * @param dhash
	 * @param iMG_MD5
	 * @param iMG_ZZJG
	 * @return
	 */
	int updateSYS_IMAGE_HMCODE(String iMG_ID, String dhash, String iMG_MD5, String iMG_ZZJG);

	/**
	 * 插入表SYS_IMAGE_SIMILARITY
	 * 
	 * @param iMG_ID
	 * @param hamBeans
	 * @return
	 */
	int insertSYS_IMAGE_SIMILARITY(String iMG_ID, Set<HamBean> hamBeans);

}
