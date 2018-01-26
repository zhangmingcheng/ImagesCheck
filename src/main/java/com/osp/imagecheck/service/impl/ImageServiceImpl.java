package com.osp.imagecheck.service.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.efounder.image.filenet.ContentEngineConnection;
import com.osp.imagecheck.ImageCheck;
import com.osp.imagecheck.InitRedisRunnable;
import com.osp.imagecheck.bean.HamBean;
import com.osp.imagecheck.bean.ImageHMCode;
import com.osp.imagecheck.bean.InitStatus;
import com.osp.imagecheck.bean.MD5Bean;
import com.osp.imagecheck.config.PythonConfig;
import com.osp.imagecheck.dao.ImageDao;
import com.osp.imagecheck.filenet.FileNetManager;
import com.osp.imagecheck.redis.DHashRedisServiceImpl;
import com.osp.imagecheck.redis.InitStatusServiceImpl;
import com.osp.imagecheck.redis.InstanceIdRedisServiceImpl;
import com.osp.imagecheck.redis.MD5RedisServiceImpl;
import com.osp.imagecheck.service.ImageService;
import com.osp.imagecheck.util.BaseUtils;
import com.osp.imagecheck.util.ImageSearchUtils;
import com.osp.imagecheck.util.NetUtils;
import com.osp.imagecheck.util.SFTPUtil;

/**
 * 具体处理逻辑
 *
 */
@Service
public class ImageServiceImpl implements ImageService {

	@Autowired
	private DHashRedisServiceImpl dHashRedisServiceImpl;

	@Autowired
	private ImageDao imageDaoImpl;

	@Autowired
	private MD5RedisServiceImpl md5RedisServiceImpl;

	@Autowired
	private InstanceIdRedisServiceImpl instanceIdRedisServiceImpl;

	@Autowired
	private InitStatusServiceImpl initStatusServiceImpl;

	/**
	 * 是否初始化结束
	 */
	@Override
	public Boolean whetherInitializationEnd() {
		return InitRedisRunnable.getStatus();
	}

	/**
	 * SYS_IMAGE_HMCODE表中MD5相同的图片是不需要计算dhash值的，需要特判
	 */
	@Transactional(readOnly = true)
	@Override
	public int initState() {
		// 从数据库加载数据 将来需要修改为根据组织机构 时间加载
		List<ImageHMCode> imageHMCodeLists = imageDaoImpl.selectFromSYS_IMAGE_HMCODE();

		for (ImageHMCode imageHMCode : imageHMCodeLists) {
			String hmCode = imageHMCode.getF_HMCODE();
			String md5 = imageHMCode.getF_MD5();
			String imgId = imageHMCode.getF_IMGID();
			// 预加载dhash值到redis
			if (BaseUtils.stringNotEmpty(hmCode) == true && hmCode.length() == 16) {
				if (dHashRedisServiceImpl.isKeyExists(hmCode) == false) {
					dHashRedisServiceImpl.put(hmCode, imgId, -1);
					ImageSearchUtils.getImagelists().add(hmCode);
				} else {
					dHashRedisServiceImpl.put(hmCode, dHashRedisServiceImpl.get(hmCode) + "," + imgId, -1);
				}
			}
			// 预加载md5值到redis
			if (BaseUtils.stringNotEmpty(md5) == true && md5.length() == 32) {
				if (md5RedisServiceImpl.isKeyExists(md5) == false) {
					md5RedisServiceImpl.put(md5, imgId, -1);
				} else {
					md5RedisServiceImpl.put(md5, md5RedisServiceImpl.get(md5) + "," + imgId, -1);
				}
			}
		}
		return 200;
	}

	@Override
	public Boolean exitMd5(String IMG_ID, String IMG_MD5) {
		// 判断是否存在相同
		if (IMG_MD5.length() == 32 && md5RedisServiceImpl.isKeyExists(IMG_MD5) == true) {
			String imgIds = md5RedisServiceImpl.get(IMG_MD5);
			if (imgIds.indexOf(IMG_ID) == -1) {// redis值去重
				md5RedisServiceImpl.put(IMG_MD5, imgIds + "," + IMG_ID, -1);
			}
			return true;
		} else if (IMG_MD5.length() == 32) {
			md5RedisServiceImpl.put(IMG_MD5, IMG_ID, -1);
		}
		return false;
	}

	/**
	 * 插入相似图片 可能是多张
	 */
	@Override
	public Boolean insertMd5ToSYS_IMAGE_SIMILARITY(String IMG_ID, String IMG_MD5) {
		Boolean flag = false;
		String value = md5RedisServiceImpl.get(IMG_MD5);
		System.out.println("key[" + IMG_MD5 + "]=====" + value);
		// 使用set去重
		Set<MD5Bean> md5Beans = new HashSet<>();
		if (BaseUtils.stringNotEmpty(value) == true) {
			for (String filename : value.split(",")) {
				if (IMG_ID.equals(filename) == false) {
					md5Beans.add(new MD5Bean(IMG_MD5, filename));
				}
			}
			if (md5Beans.size() > 0 && imageDaoImpl.insertMd5ToSYS_IMAGE_SIMILARITY(IMG_ID, md5Beans) > 0) {
				flag = true;
			}
		}
		return flag;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map downloadPic(String IMG_OBJID, String iMG_TYPE) {
		Map resMap = new HashMap();
		resMap.put("download", false);
		ContentEngineConnection connection = null;
		try {
			connection = FileNetManager.estanblishConnection();
			if (FileNetManager.isConnected(connection) == false) {
				resMap.put("error", "FileNet连接失败!");
			}
			String absolutePathName = FileNetManager.downloadFile(connection, IMG_OBJID, iMG_TYPE);
			resMap.put("absolutePathName", absolutePathName);
			resMap.put("download", true);
		} catch (Exception e) {
			resMap.put("error", "download pic fail");
			closeFileNetConne(connection);
			e.printStackTrace();
		}
		return resMap;
	}

	@Override
	public int updateSYS_IMAGE_HMCODE(String iMG_ID, String dhash, String iMG_MD5, String iMG_ZZJG) {
		int temp = imageDaoImpl.updateSYS_IMAGE_HMCODE(iMG_ID, dhash, iMG_MD5, iMG_ZZJG);
		return temp;
	}

	/**
	 * 获取影像dhash，获取完后将影像删除
	 */
	@Override
	public String getDhash(String absolutePathName) {
		String url = "http://" + PythonConfig.PythonIP + ":" + PythonConfig.PythonPort + "/images/getImageDhash?path="
				+ absolutePathName;
		String dhash = NetUtils.sendGet(url);
		try {
			SFTPUtil sftp = new SFTPUtil(PythonConfig.SFTPUser, PythonConfig.SFTPPSW, PythonConfig.PythonIP,
					PythonConfig.SFTPPort);
			sftp.login();
			String path = absolutePathName.substring(0, absolutePathName.lastIndexOf('/'));
			sftp.delete(path, absolutePathName);
			sftp.logout();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		System.out.println(absolutePathName + "影像的dhash值=" + dhash + "，删除影像成功");
		return dhash;
	}

	@Override
	public Set<HamBean> compareHaming(String dhash, Integer iMG_HMDIS) {
		Set<HamBean> hamBeans = ImageSearchUtils.toSearch(dhash, iMG_HMDIS);
		for(HamBean hamBean : hamBeans){
			System.out.println("相似图片有======"+dHashRedisServiceImpl.get(hamBean.getDhash()));
		}
		return hamBeans;
	}

	@Override
	public int insertSYS_IMAGE_SIMILARITY(String iMG_ID, Set<HamBean> hamBeans) {
		int result = imageDaoImpl.insertSYS_IMAGE_SIMILARITY(iMG_ID, hamBeans);
		return result;
	}

	@Override
	public void updateRedisAndMem(String dhash, String iMG_ID) {
		if (dHashRedisServiceImpl.isKeyExists(dhash) == false) {
			dHashRedisServiceImpl.put(dhash, iMG_ID, -1);
			ImageSearchUtils.addImageDhash(dhash);
		} else {
			dHashRedisServiceImpl.put(dhash, dHashRedisServiceImpl.get(dhash) + "," + iMG_ID, -1);
		}
	}

	/**
	 * 关闭Filenet连接
	 * 
	 * @param connection
	 */
	private void closeFileNetConne(ContentEngineConnection connection) {
		if (FileNetManager.isConnected(connection) == true) {
			connection.closeConnnection();
		}
	}

	/**
	 * 维护其他实例更新数据产生的Dhash值到内存
	 * 
	 * @param dhash
	 * @param iMG_ID
	 */
	@Override
	public void updateDhashFromAnotherInstance() {
		// 1.连接此应用实例库
		InstanceIdRedisServiceImpl.setREDIS_KEY(ImageCheck.getInstanceId());
		while (true) {
			System.out.println("需要从其他实例维护更新数据产生的Dhash的数量为="+instanceIdRedisServiceImpl.count());
			Set<String> anotherInstanceIdDhashs = instanceIdRedisServiceImpl.getKeys();
			for (String anotherInstanceIdDhash : anotherInstanceIdDhashs) {
				ImageSearchUtils.addImageDhash(anotherInstanceIdDhash);
				instanceIdRedisServiceImpl.remove(anotherInstanceIdDhash);
				System.out.println("维护dhash="+anotherInstanceIdDhash+"到内存");
			}
			if (instanceIdRedisServiceImpl.count() == 0) {
				System.out.println("=====维护其他实例更新数据产生的Dhash值到内存结束=====");
				break;
			}
		}

	}

	/**
	 * 维护Dhash值到其他应用实例维护的redis中 ，key[dhash]="",只需维护dhash到内存
	 */
	@Override
	public void updateDhashToAnotherInstance(String dhash, String iMG_ID) {
		// 1.取到其他实例编号
		InitStatus initStatusBean = initStatusServiceImpl.get("ImageCheck");
		String instanceIds = initStatusBean.getInstanceId();
		System.out.println("全部实例编号====="+instanceIds);
		// 2.维护Dhash值到其他应用实例维护的redis中
		for (String instanceId : instanceIds.split(",")) {
			if (ImageCheck.getInstanceId().equals(instanceId) == false) {
				// 更换库
				InstanceIdRedisServiceImpl.setREDIS_KEY(instanceId);
				instanceIdRedisServiceImpl.put(dhash, "", -1);
				System.out.println("实例"+ImageCheck.getInstanceId()+"向"+instanceId+"实例维护的redis库插入dhash="+dhash);
			}
		}
	}
}
