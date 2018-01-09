package com.osp.imagecheck.redis;

import org.springframework.stereotype.Service;


/**
 * 
 * @author zhangmingcheng
 * @date 2018年1月5日
 */
@Service
public class RedisServiceImpl extends IRedisService<String>{
    private static final String REDIS_KEY = "OSPDhashss";

	@SuppressWarnings("static-access")
	@Override
    protected String getRedisKey() {
        return this.REDIS_KEY;
    }
}
