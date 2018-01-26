package com.osp.imagecheck.redis;
import org.springframework.stereotype.Service;


/**
 * key[MD5]=value[IMGID,IMGID,....,IMGID]
 * @author zhangmingcheng
 * @date 2018年1月16日
 */
@Service
public class MD5RedisServiceImpl extends IRedisService<String>{
    private static final String REDIS_KEY = "MD5DATA";

	@SuppressWarnings("static-access")
	@Override
    protected String getRedisKey() {
        return this.REDIS_KEY;
    }
}
