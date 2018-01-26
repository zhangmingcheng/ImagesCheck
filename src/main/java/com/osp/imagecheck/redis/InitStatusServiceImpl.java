package com.osp.imagecheck.redis;

import org.springframework.stereotype.Service;

import com.osp.imagecheck.bean.InitStatus;

/**
 * 集群初始化状态
 * @author zhangmingcheng
 */
@Service
public class InitStatusServiceImpl extends IRedisService<InitStatus>{
    private static final String REDIS_KEY = "InitRedisData";

	@SuppressWarnings("static-access")
	@Override
    protected String getRedisKey() {
        return this.REDIS_KEY;
    }
	
	
}