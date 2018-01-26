package com.osp.imagecheck.bean;

import java.io.Serializable;

/**
 * 维护集群初始化状态
 * 
 * @author zhangmingcheng
 */
public class InitStatus implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// 应用编号，通过UUID产生
	private String instanceId;
	// 初始化状态，status=1表示正在初始化，status=2表示集群初始化结束
	private Integer status;

	public InitStatus() {
		super();
	}

	public InitStatus(Integer status) {
		this.status = status;
	}
	
	public InitStatus(String instanceId){
		this.instanceId = instanceId;
	}

	public InitStatus(String instanceId, Integer status) {
		this.instanceId = instanceId;
		this.status = status;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
