package com.osp.imagecheck.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.osp.imagecheck.bean.DhashBean;
import com.osp.imagecheck.bean.HamBean;
import com.osp.imagecheck.config.PythonConfig;
import com.osp.imagecheck.redis.RedisServiceImpl;
import com.osp.imagecheck.util.ImageSearchUtils;
import com.osp.imagecheck.util.JsonUtils;
import com.osp.imagecheck.util.NetUtils;

/**
 * 图片Hash控制类
 * 
 * @author zhangmingcheng
 * @date 2018年1月5日
 */
@Controller
@RequestMapping("/images")
public class DhashController {

	@Autowired
	private RedisServiceImpl redisServiceImpl;

	private String response = "不存在相似图片，上传成功!";

	public void init() {
		redisServiceImpl.empty();// 初始化dhash前，先清空redis数据
		ImageSearchUtils.getImagelists().clear();
		ImageSearchUtils.getData().clear();
	}

	/**
	 * 取得目录下所有图片的dhash
	 * 
	 * @param path
	 * @return
	 */
	@RequestMapping(value = "/getImagesDhash", method = RequestMethod.GET)
	@ResponseBody
	public String getImagesDhash(@RequestParam(value = "path", defaultValue = "") String path) {
		this.init();
		String url = "http://" + PythonConfig.PythonIP + ":" + PythonConfig.PythonPort + "/images/getImagesDhash?path="
				+ path;
		String response = NetUtils.sendGet(url);
		List<DhashBean> dhashlists = JsonUtils.jsonToBeanList(response, DhashBean.class);
		for (DhashBean dhashBean : dhashlists) {
			String dhash = dhashBean.getDhash();
			if (redisServiceImpl.isKeyExists(dhash) == true) {
				redisServiceImpl.put(dhash, redisServiceImpl.get(dhash) + "," + dhashBean.getName(), -1);
			} else {
				redisServiceImpl.put(dhash, dhashBean.getName(), -1);
				ImageSearchUtils.getImagelists().add(dhash);
			}
			System.out.println("dhash===" + dhash + " filename===" + redisServiceImpl.get(dhash));
		}
		ImageSearchUtils.toInit();
		return "200";
	}

	/**
	 * 取得单张图片dhash并比较
	 * 
	 * @param path
	 * @return
	 */
	@RequestMapping(value = "/getImageDhash", method = RequestMethod.GET)
	@ResponseBody
	public String getImageDhash(@RequestParam(value = "path", defaultValue = "") String path) {
		String url = "http://" + PythonConfig.PythonIP + ":" + PythonConfig.PythonPort + "/images/getImageDhash?path="
				+ path;
		String dhash = NetUtils.sendGet(url);
		String filename = path.substring(path.lastIndexOf('/'));
		System.out.println("filename===" + filename + " dhash===" + dhash);
		/*
		 * if (redisServiceImpl.isKeyExists(dhash) == true) { this.response =
		 * "影像库存在相同影像，不需要重新上传!"; } else {
		 */
		Set<HamBean> sets = new ImageSearchUtils().toSearch(dhash);
		List<HamBean> lists = new ArrayList<>();
		if (sets.size() > 0) {
			for (HamBean hamBean : sets) {
				hamBean.setFilename(redisServiceImpl.get(hamBean.getDhash()));
				lists.add(hamBean);
			}
			this.response = JsonUtils.listToJson(lists);
		}
		return this.response;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
}
