package com.osp.imagecheck.redis;

import org.springframework.stereotype.Service;


/**
 * key[dhash]=value[IMGID,IMGID,....,IMGID]
 * @author zhangmingcheng
 * @date 2018年1月5日
 */
@Service
public class DHashRedisServiceImpl extends IRedisService<String>{
    private static final String REDIS_KEY = "DHASHDATA";

	@SuppressWarnings("static-access")
	@Override
    protected String getRedisKey() {
        return this.REDIS_KEY;
    }
}
