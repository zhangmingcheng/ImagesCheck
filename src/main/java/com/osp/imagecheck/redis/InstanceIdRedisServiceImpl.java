package com.osp.imagecheck.redis;

import org.springframework.stereotype.Service;

/**
 * 不同应用Id维护不同的redis库
 * @author zhangmingcheng
 * @date 2018年1月16日
 */
@Service
public class InstanceIdRedisServiceImpl extends IRedisService<String>{
    private static String REDIS_KEY = "";

	@SuppressWarnings("static-access")
	@Override
    protected String getRedisKey() {
        return this.REDIS_KEY;
    }

	public static String getREDIS_KEY() {
		return REDIS_KEY;
	}

	public static void setREDIS_KEY(String rEDIS_KEY) {
		REDIS_KEY = rEDIS_KEY;
	}
}
