package com.osp.imagecheck.service;

import java.util.Map;
import java.util.Set;

import com.osp.imagecheck.bean.HamBean;

public interface ImageService {
	
	/**
	 * 判断应用实例是否初始化结束
	 * @return
	 */
	Boolean whetherInitializationEnd();
	
	/**
	 * 启动应用，数据初始化
	 * @return
	 */
	int initState();
	
	/**
	 * 比较MD5
	 * @param IMG_ID
	 * @param IMG_MD5
	 * @return
	 */
	Boolean exitMd5(String IMG_ID,String IMG_MD5);

	/**
	 * 插入MD5相同的图片
	 * @param IMG_ID
	 * @param IMG_MD5
	 * @return
	 */
	Boolean insertMd5ToSYS_IMAGE_SIMILARITY(String IMG_ID,String IMG_MD5);


	/**
	 * 下载图片
	 * @param iMG_ID
	 * @param iMG_TYPE
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	Map downloadPic(String IMG_OBJID, String iMG_TYPE);
	
	/**
	 * 获取图片的Dhash
	 * @param absolutePathName
	 * @return
	 */
	String getDhash(String absolutePathName);

	/**
	 * 更新Dhash
	 * @param iMG_ID
	 * @param dhash
	 * @param iMG_MD5
	 * @param iMG_ZZJG
	 * @return
	 */
	int updateSYS_IMAGE_HMCODE(String iMG_ID, String dhash, String iMG_MD5, String iMG_ZZJG);

	/**
	 * 比较汉明距离
	 * @param dhash
	 * @param iMG_HMDIS
	 * @return
	 */
	Set<HamBean> compareHaming(String dhash, Integer iMG_HMDIS);

	/**
	 * 插入相似图片
	 * @param iMG_ID
	 * @param hamBeans
	 * @return
	 */
	int insertSYS_IMAGE_SIMILARITY(String iMG_ID, Set<HamBean> hamBeans);

	/**
	 * 维护Dhash到Redis和Mem
	 * @param dhash
	 * @param iMG_ID
	 */
	void updateRedisAndMem(String dhash, String iMG_ID);
	
	/**
	 * 维护其他实例更新数据产生的Dhash值到内存
	 * @param dhash
	 * @param iMG_ID
	 */
	void updateDhashFromAnotherInstance();
	
	/**
	 * 维护Dhash到其他应用实例维护的redis里
	 * @param dhash
	 * @param iMG_ID
	 */
	void updateDhashToAnotherInstance(String dhash, String iMG_ID);
	
}
