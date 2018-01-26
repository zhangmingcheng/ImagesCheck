package com.osp.imagecheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 
 * @author zhangmingcheng
 * @date 2018年1月5日
 */
@SpringBootApplication
@EnableConfigurationProperties
public class ImageCheck {
	
	//实例编号，通过UUID生成
	public static String instanceId;
	
	public static void main(String[] args) {
		SpringApplication.run(ImageCheck.class, args);
	}

	public static String getInstanceId() {
		return instanceId;
	}

	public static void setInstanceId(String instanceId) {
		ImageCheck.instanceId = instanceId;
	}

}
