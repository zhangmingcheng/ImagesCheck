package com.osp.imagecheck.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
/**
 * Python Restful信息配置
 * @author zhangmingcheng
 * @date 2018年1月5日
 */
@Configuration
@PropertySource("classpath:imagescheck.properties")
public class PythonConfig {

	public static String PythonIP;
	public static int PythonPort;

	@Value("${python.ip}")
	private String ip;

	@Value("${python.port}")
	private int port;

	@PostConstruct
	public void init() {
		PythonConfig.PythonIP = this.ip;
		PythonConfig.PythonPort = this.port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
