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

	public static void main(String[] args) {
		SpringApplication.run(ImageCheck.class, args);
	}
}
