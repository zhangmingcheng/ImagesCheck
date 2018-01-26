package com.osp.imagecheck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.osp.imagecheck.redis.DHashRedisServiceImpl;
import com.osp.imagecheck.redis.InitStatusServiceImpl;
import com.osp.imagecheck.redis.MD5RedisServiceImpl;
import com.osp.imagecheck.service.ImageService;
import com.osp.imagecheck.util.BaseUtils;
import com.osp.imagecheck.util.ImageSearchUtils;

/**
 * 启动时初始化
 * 
 * @author zhangmingcheng
 */
@Component
public class ApplicationStartRunner implements ApplicationRunner {

	@Autowired
	private DHashRedisServiceImpl dHashRedisServiceImpl;

	@Autowired
	private MD5RedisServiceImpl md5RedisServiceImpl;

	@Autowired
	private ImageService imageServiceImpl;

	@Autowired
	private InitStatusServiceImpl initStatusServiceImpl;

	/**
	 * 初始化 初始化结束后不需要的对象删除掉
	 */
	@Override
	public void run(ApplicationArguments var1) throws Exception {
		System.out.println("========项目启动时执行特定方法=======");
		// 1.清空内存
		ImageSearchUtils.getImagelists().clear();
		ImageSearchUtils.getData().clear();

		// 2.生成实例Id
		String instanceId = BaseUtils.getUUID();
		ImageCheck.setInstanceId(instanceId);
		System.out.println("应用实例在redis维护的库=" + instanceId);

		// 3.启动线程，进行redis数据的初始化
		InitRedisRunnable initRedisRunnable = new InitRedisRunnable(imageServiceImpl, initStatusServiceImpl,
				dHashRedisServiceImpl, md5RedisServiceImpl);
		Thread thread = new Thread(initRedisRunnable, BaseUtils.getInetAddress() + "初始化redis数据线程");
		thread.start();
	}
}
