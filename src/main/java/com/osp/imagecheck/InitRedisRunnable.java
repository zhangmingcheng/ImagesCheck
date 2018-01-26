package com.osp.imagecheck;

import java.util.Set;

import com.osp.imagecheck.bean.InitStatus;
import com.osp.imagecheck.config.PythonConfig;
import com.osp.imagecheck.redis.DHashRedisServiceImpl;
import com.osp.imagecheck.redis.InitStatusServiceImpl;
import com.osp.imagecheck.redis.MD5RedisServiceImpl;
import com.osp.imagecheck.service.ImageService;
import com.osp.imagecheck.util.ImageSearchUtils;
import com.osp.imagecheck.util.NetUtils;

/**
 * 初始化数据线程
 * 
 * @author zhangmingcheng
 */
public class InitRedisRunnable implements Runnable {

	// flag=true表示是此应用实例维护sql数据库中的数据到redis里面
	private static Boolean flag = false;

	// status=true表示初始化完成
	private static Boolean status = false;

	private InitStatusServiceImpl initStatusServiceImpl;

	private ImageService imageServiceImpl;

	private DHashRedisServiceImpl dHashRedisServiceImpl;

	private MD5RedisServiceImpl md5RedisServiceImpl;

	public InitRedisRunnable() {
		super();
	}

	public InitRedisRunnable(ImageService imageServiceImpl, InitStatusServiceImpl initStatusServiceImpl,
			DHashRedisServiceImpl dHashRedisServiceImpl, MD5RedisServiceImpl md5RedisServiceImpl) {
		this.imageServiceImpl = imageServiceImpl;
		this.initStatusServiceImpl = initStatusServiceImpl;
		this.dHashRedisServiceImpl = dHashRedisServiceImpl;
		this.md5RedisServiceImpl = md5RedisServiceImpl;
	}

	/**
	 * redis数据库初始化 
	 * 1、判断redis数据库是否存在key["initStatus"]
	 * 2、不存在则存入key["initStatus"]=1,表示一个应用实例正在初始化redis数据库,redis数据库初始化结束维护
	 * redis["initStatus"]=2
	 * 3、key["initStatus"]=2表示初始化结束,此时其它应用实例通过遍历redis，维护内存的状态信息
	 * 4、维护当前应用的instanceId到key["ImageCheck"]=[instanceId,..,instanceId]
	 */
	@Override
	public void run() {
		try {
			while (true) {
				int sleeptime = InitRedisRunnable.getSleepTime();
				Thread.sleep(sleeptime);
				System.out.println(Thread.currentThread().getName() + "线程sleep" + sleeptime + "ms");
				// 1.判断redis数据库是否存在key["initStatus"]
				if (!initStatusServiceImpl.isKeyExists("initStatus")) {
					if (initializationRedis() == 200) {
						System.out.println("=====redis数据库初始化结束=====");
						InitRedisRunnable.setStatus(true);
						this.organizeInstanceIdToRedis();
						break;
					}
				} else {
					InitStatus initStatusBean = initStatusServiceImpl.get("initStatus");
					// 2.其它应用实例通过遍历redis，维护内存的状态信息
					if (initStatusBean.getStatus() == 2 && InitRedisRunnable.getFlag() == false) {
						this.getDHashFromRedisToMem();
						System.out.println("=====内存数据初始化结束=====");
						InitRedisRunnable.setStatus(true);
						this.organizeInstanceIdToRedis();
						break;
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	/**
	 * 维护应用实例Id
	 */
	public void organizeInstanceIdToRedis() {
		if (initStatusServiceImpl.isKeyExists("ImageCheck") == false) {
			String instanceId = ImageCheck.getInstanceId();
			initStatusServiceImpl.put("ImageCheck", new InitStatus(instanceId), -1);
		} else {
			String instanceId = ImageCheck.getInstanceId();
			InitStatus initStatusBean = initStatusServiceImpl.get("ImageCheck");
			initStatusServiceImpl.put("ImageCheck", new InitStatus(initStatusBean.getInstanceId() + "," + instanceId),
					-1);
		}
	}

	/**
	 * 其他应用实例从redis读取dhash值到内存
	 */
	public void getDHashFromRedisToMem() {
		// 维护Dhash信息到内存
		Set<String> dHashKeys = dHashRedisServiceImpl.getKeys();
		for (String dhash : dHashKeys) {
			ImageSearchUtils.getImagelists().add(dhash);
		}
		ImageSearchUtils.toInit();
		ImageSearchUtils.getImagelists().clear();
	}

	/**
	 * 进行MD5,与DHash的初始化
	 * 
	 * @return
	 */
	public int initializationRedis() {
		// 1.初始化dhash md5前，先清空redis数据
		dHashRedisServiceImpl.empty();
		md5RedisServiceImpl.empty();
		initStatusServiceImpl.empty();
		
		System.out.println("清空--dhash库中key的数量=====" + dHashRedisServiceImpl.count());
		System.out.println("清空--md5库中key的数量=====" + md5RedisServiceImpl.count());
		System.out.println("清空--状态库中key的数量=====" + initStatusServiceImpl.count());
		// 2.此实例进行redis数据的初始化
		InitRedisRunnable.setFlag(true);
		initStatusServiceImpl.put("initStatus", new InitStatus(1), -1);
		int state = imageServiceImpl.initState();
		if (state == 200) {
			initStatusServiceImpl.put("initStatus", new InitStatus(2), -1);
			ImageSearchUtils.toInit();
			ImageSearchUtils.getImagelists().clear();
		}
		
		System.out.println("初始化--dhash库中key的数量=====" + dHashRedisServiceImpl.count());
		System.out.println("初始化--md5库中key的数量=====" + md5RedisServiceImpl.count());
		return state;
	}

	/**
	 * 循环返回1000,2000,3000,4000,5000ms
	 * 
	 * @return
	 */
	public static int getSleepTime() {
		String url = "http://" + PythonConfig.PythonIP + ":" + PythonConfig.PythonPort + "/images/getRandom";
		return Integer.parseInt(NetUtils.sendGet(url));
	}

	public static Boolean getFlag() {
		return flag;
	}

	public static void setFlag(Boolean flag) {
		InitRedisRunnable.flag = flag;
	}

	public static Boolean getStatus() {
		return status;
	}

	public static void setStatus(Boolean status) {
		InitRedisRunnable.status = status;
	}

}
