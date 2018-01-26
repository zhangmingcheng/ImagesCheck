package com.osp.imagecheck;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

import com.osp.imagecheck.bean.InitStatus;
import com.osp.imagecheck.redis.InitStatusServiceImpl;
import com.osp.imagecheck.redis.InstanceIdRedisServiceImpl;

/**
 * SpringBoot之退出服务（exit）时调用自定义的销毁方法
 * 
 * @author zhangmingcheng
 */
@Component
public class ApplicationExit implements DisposableBean, ExitCodeGenerator {

	@Autowired
	private InstanceIdRedisServiceImpl instanceIdRedisServiceImpl;

	@Autowired
	private InitStatusServiceImpl initStatusServiceImpl;

	@Override
	public int getExitCode() {
		// TODO Auto-generated method stub
		return 5;
	}

	/**
	 * 1、实例退出时，清空当前实例维护的redis库
	 * 2、实例退出时，维护redis状态库key["ImageCheck"]，将其实例id删掉,如果只剩最后一个实例运行，删除这个库
	 */
	@Override
	public void destroy() throws Exception {
		String instanceId = ImageCheck.getInstanceId();
		InstanceIdRedisServiceImpl.setREDIS_KEY(ImageCheck.getInstanceId());
		instanceIdRedisServiceImpl.empty();
		this.exitInstanceId(instanceId);
	}

	/**
	 * 处理集群redis状态库
	 * 
	 * @param instanceId
	 */
	public void exitInstanceId(String instanceId) {
		try {
			// 避免所有实例同时关闭
			int sleeptime = InitRedisRunnable.getSleepTime();
			Thread.sleep(sleeptime);
			System.out.println(Thread.currentThread().getName() + "线程sleep" + sleeptime + "ms");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		InitStatus initStatusBean = initStatusServiceImpl.get("ImageCheck");
		System.out.println("当前所有实例编号=" + initStatusBean.getInstanceId());
		String[] instanceIds = initStatusBean.getInstanceId().split(",");
		if (instanceIds.length == 1) {
			initStatusServiceImpl.empty();
			System.out.println("退出最后一个实例，清空InitRedisData库维护的集群状态值！");
			return;
		}
		String aString = "";
		for (int i = 0; i < instanceIds.length; i++) {
			if (instanceIds[i].equals(instanceId) == false) {
				aString += instanceIds[i] + ",";
			}
		}
		aString = aString.substring(0, aString.length() - 1);// 去除最后的逗号
		initStatusServiceImpl.put("ImageCheck", new InitStatus(aString), -1);
		System.out.println("实例" + ImageCheck.getInstanceId() + "退出成功,当前redis状态库key[ImageCheck]=" + aString);
		return;
	}

}
