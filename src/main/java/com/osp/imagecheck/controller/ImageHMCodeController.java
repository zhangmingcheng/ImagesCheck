package com.osp.imagecheck.controller;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.osp.imagecheck.bean.HamBean;
import com.osp.imagecheck.bean.ImageDhashParamBean;
import com.osp.imagecheck.service.ImageService;
import com.osp.imagecheck.util.BaseUtils;

/**
 * 图片Hash控制类
 * 
 * @author zhangmingcheng
 * @date 2018年1月5日
 */
@Controller
@RequestMapping("/images")
public class ImageHMCodeController {

	private static Queue<ImageDhashParamBean> imageDhashParamBeans = new LinkedList<ImageDhashParamBean>();

	@Autowired
	private ImageService imageServiceImpl;

	private String response = "影像上传失败";

	/**
	 * 异步调用不接收返回值，返回状态应写入对应的数据库或者其他方式记录
	 * 
	 * @param IMG_ID
	 *            影像ID
	 * @param IMG_OBJID
	 *            影像存储ID
	 * @param IMG_GROUPID
	 *            影像组ID
	 * @param STORAGE_TYPE
	 *            影像存储方式（FILENET/FASTDFS）
	 * @param IMG_MD5
	 *            影像MD5码，优先根据md5精确查找
	 * @param IMG_HMDIS
	 *            根据汉明距离查找范围，决定是否相似，默认5
	 * @param IMG_DAYS
	 *            查询影像天数的范围
	 * @param IMG_ZZJG
	 *            当前影像所属组织机构
	 * @param IMG_TYPE
	 *            影像类型，默认jpg
	 * @return
	 */
	@RequestMapping(value = "/search", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String getImageDhash(@RequestParam(value = "IMG_ID", defaultValue = "") String IMG_ID,
			@RequestParam(value = "IMG_OBJID", defaultValue = "") String IMG_OBJID,
			@RequestParam(value = "IMG_GROUPID", defaultValue = "") String IMG_GROUPID,
			@RequestParam(value = "STORAGE_TYPE", defaultValue = "") String STORAGE_TYPE,
			@RequestParam(value = "IMG_MD5", defaultValue = "") String IMG_MD5,
			@RequestParam(value = "IMG_HMDIS", defaultValue = "5") Integer IMG_HMDIS,
			@RequestParam(value = "IMG_DAYS", defaultValue = "") String IMG_DAYS,
			@RequestParam(value = "IMG_ZZJG", defaultValue = "") String IMG_ZZJG,
			@RequestParam(value = "IMG_TYPE", defaultValue = "jpg") String IMG_TYPE) {

		// 判断是否初始化结束
		if (imageServiceImpl.whetherInitializationEnd() == false) {
			imageDhashParamBeans.offer(new ImageDhashParamBean(IMG_ID, IMG_OBJID, IMG_GROUPID, STORAGE_TYPE, IMG_MD5,
					IMG_HMDIS, IMG_DAYS, IMG_ZZJG, IMG_TYPE));
			return "初始化还没有结束,插入数据将暂存到队列中!";
		}
		//每次调用接口，先将数据存到队列中
		imageDhashParamBeans.offer(new ImageDhashParamBean(IMG_ID, IMG_OBJID, IMG_GROUPID, STORAGE_TYPE, IMG_MD5,
				IMG_HMDIS, IMG_DAYS, IMG_ZZJG, IMG_TYPE));
		this.execteQueueData();
		System.out.println(this.response);
		return this.response;
	}

	/**
	 * 上传单张图片计算其dhash值 查找影像库中的相似图片 这里只控制流程 不处理逻辑 1. 对比MD5值 如果相同更新相似图片表 如不同 继续
	 * 2.从FileNet下载图片 3. 计算Dhash值 4. 存储Dhash 5. 计算汉明距离 6. 存储相似图片
	 * 7.将Dhash维护到redis和内存
	 * 
	 * 添加一张记录状态的表 记录处理过程 处理中1 处理失败-1 记录处理开始的时间 处理完成后删掉记录 对于状态是-1的再做重新处理 对于状态是1的
	 * 时间超过一天也做重新处理
	 * 
	 * 
	 * @param IMG_ID
	 * @param IMG_OBJID
	 * @param IMG_GROUPID
	 * @param STORAGE_TYPE
	 * @param IMG_MD5
	 * @param IMG_HMDIS
	 * @param IMG_DAYS
	 * @param IMG_ZZJG
	 * @param IMG_TYPE
	 */
	public void exeuteFlow(String IMG_ID, String IMG_OBJID, String IMG_GROUPID, String STORAGE_TYPE, String IMG_MD5,
			Integer IMG_HMDIS, String IMG_DAYS, String IMG_ZZJG, String IMG_TYPE) {
		// 1. 对比MD5值
		boolean isMd5 = imageServiceImpl.exitMd5(IMG_ID, IMG_MD5);
		if (isMd5) {
			// 更新相似图片表
			imageServiceImpl.insertMd5ToSYS_IMAGE_SIMILARITY(IMG_ID, IMG_MD5);
			this.response = "此影像已存在，无需上传，其MD5信息已维护";
			return;
		}

		// 2. 从FileNet下载图片
		@SuppressWarnings("rawtypes")
		Map resMap = imageServiceImpl.downloadPic(IMG_OBJID, IMG_TYPE);
		if ((boolean) resMap.get("download") == false) {
			this.response = resMap.get("error").toString();
			return;
		}

		// 3. 计算Dhash值
		String absolutePathName = resMap.get("absolutePathName").toString();
		String dhash = imageServiceImpl.getDhash(absolutePathName);
		if (BaseUtils.stringNotEmpty(dhash) == false && dhash.length() != 16) {
			this.response = "DHash fail";
			return;
		}

		// 4. 存储Dhash
		if (imageServiceImpl.updateSYS_IMAGE_HMCODE(IMG_ID, dhash, IMG_MD5, IMG_ZZJG) > 0) {
			this.response = "影像信息存入SYS_IMAGE_HMCODE库成功";
		}else{
			return ;
		}

		// 5.维护内存,读取其他实例插入的Dhash值到内存
		imageServiceImpl.updateDhashFromAnotherInstance();

		// 6. 计算汉明距离
		Set<HamBean> hamBeans = imageServiceImpl.compareHaming(dhash, IMG_HMDIS);

		// 7. 存储相似图片
		if (hamBeans.size() > 0) {
			if (imageServiceImpl.insertSYS_IMAGE_SIMILARITY(IMG_ID, hamBeans) > 0) {
				this.response = "影像信息存入SYS_IMAGE_HMCODE库与SYS_IMAGE_SIMILARITY库成功";
			}
		}

		// 8. 将Dhash维护到redis和内存
		imageServiceImpl.updateRedisAndMem(dhash, IMG_ID);

		// 9. 将Dhash维护到其他实例维护的redis中
		imageServiceImpl.updateDhashToAnotherInstance(dhash, IMG_ID);
		return;
	}

	public void execteQueueData() {
		ImageDhashParamBean imageDhashParamBean = null;
		while ((imageDhashParamBean = ImageHMCodeController.imageDhashParamBeans.poll()) != null) {
			String IMG_ID = imageDhashParamBean.getIMG_ID();
			String IMG_OBJID = imageDhashParamBean.getIMG_OBJID();
			String IMG_GROUPID = imageDhashParamBean.getIMG_GROUPID();
			String STORAGE_TYPE = imageDhashParamBean.getSTORAGE_TYPE();
			String IMG_MD5 = imageDhashParamBean.getIMG_MD5();
			Integer IMG_HMDIS = imageDhashParamBean.getIMG_HMDIS();
			String IMG_DAYS = imageDhashParamBean.getIMG_DAYS();
			String IMG_ZZJG = imageDhashParamBean.getIMG_ZZJG();
			String IMG_TYPE = imageDhashParamBean.getIMG_TYPE();
			this.exeuteFlow(IMG_ID, IMG_OBJID, IMG_GROUPID, STORAGE_TYPE, IMG_MD5, IMG_HMDIS, IMG_DAYS, IMG_ZZJG,
					IMG_TYPE);
		}
	}

	public Boolean paramMD5Judge(String md5) {
		if (BaseUtils.stringNotEmpty(md5) == true && md5.length() == 32) {
			return true;
		}
		this.response = "MD5参数不合法！";
		return false;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public static Queue<ImageDhashParamBean> getImageDhashParamBeans() {
		return imageDhashParamBeans;
	}

	public static void setImageDhashParamBeans(Queue<ImageDhashParamBean> imageDhashParamBeans) {
		ImageHMCodeController.imageDhashParamBeans = imageDhashParamBeans;
	}

}
