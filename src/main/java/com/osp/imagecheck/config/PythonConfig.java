package com.osp.imagecheck.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Python Restful信息配置、FileNet配置信息
 * 
 * @author zhangmingcheng
 * @date 2018年1月5日
 */
@Configuration
@PropertySource("classpath:imagescheck.properties")
public class PythonConfig {

	public static String PythonIP;
	public static int PythonPort;
	public static String ImagesPath;
	public static String Username;
	public static String Password;
	public static String JAASStanzaName;
	public static String URI;
	public static String DomainName;
	public static String SFTPUser;
	public static String SFTPPSW;
	public static int SFTPPort;

	@Value("${sftp.user}")
	private String sftpuser;

	@Value("${sftp.psw}")
	private String sftppsw;

	@Value("${sftp.port}")
	private int sftpport;

	@Value("${filenet.username}")
	private String username;

	@Value("${filenet.password}")
	private String password;

	@Value("${filenet.JAASStanzaName}")
	private String jAASStanzaName;

	@Value("${filenet.uri}")
	private String uri;

	@Value("${filenet.domainName}")
	private String domainName;

	@Value("${python.ip}")
	private String ip;

	@Value("${python.port}")
	private int port;

	@Value("${images.path}")
	private String path;

	@PostConstruct
	public void init() {
		PythonConfig.PythonIP = this.ip;
		PythonConfig.PythonPort = this.port;
		PythonConfig.ImagesPath = this.path;
		PythonConfig.Username = this.username;
		PythonConfig.Password = this.password;
		PythonConfig.DomainName = this.domainName;
		PythonConfig.JAASStanzaName = this.jAASStanzaName;
		PythonConfig.URI = this.uri;
		PythonConfig.SFTPUser = this.sftpuser;
		PythonConfig.SFTPPSW = this.sftppsw;
		PythonConfig.SFTPPort = this.sftpport;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
