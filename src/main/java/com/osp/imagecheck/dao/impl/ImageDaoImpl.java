package com.osp.imagecheck.dao.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.osp.imagecheck.bean.HamBean;
import com.osp.imagecheck.bean.ImageHMCode;
import com.osp.imagecheck.bean.ImageHMCodeRowMapper;
import com.osp.imagecheck.bean.MD5Bean;
import com.osp.imagecheck.dao.ImageDao;
import com.osp.imagecheck.redis.DHashRedisServiceImpl;

@Component
public class ImageDaoImpl implements ImageDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DHashRedisServiceImpl dHashRedisServiceImpl;

	/**
	 * 查询SYS_IMAGE_HMCODE表所有数据
	 */
	@Override
	public List<ImageHMCode> selectFromSYS_IMAGE_HMCODE() {
		List<ImageHMCode> imageHMCodeLists = jdbcTemplate.query("select * from SYS_IMAGE_HMCODE",
				new ImageHMCodeRowMapper());
		return imageHMCodeLists;
	}

	/**
	 * 相同影像
	 */
	@Override
	public int insertMd5ToSYS_IMAGE_SIMILARITY(String imageid, Set<MD5Bean> md5Beans) {
		int temp = -1;
		String sql = "insert into SYS_IMAGE_SIMILARITY(F_IMGID,F_IMGID2,F_MD5SAME) values(?,?,?)";
		for (MD5Bean md5Bean : md5Beans) {
			Object args[] = { imageid, md5Bean.getFilename(), "1" };
			temp = jdbcTemplate.update(sql, args);
		}
		System.out.println("SYS_IMAGE_SIMILARITY表存储MD5影像sql=" + sql);
		return temp;
	}

	@Override
	public int updateSYS_IMAGE_HMCODE(String iMG_ID, String dhash, String iMG_MD5, String iMG_ZZJG) {
		int result = 0;
		String sql = "update SYS_IMAGE_HMCODE set F_HMCODE=?,F_MD5 = ?,F_ZZJG=? where F_IMGID = ?";
		Object args[] = { dhash, iMG_MD5, iMG_ZZJG,iMG_ID };
		result = jdbcTemplate.update(sql, args);
		System.out.println("维护SYS_IMAGE_HMCODE表信息sql==" + sql);
		return result;
	}

	/**
	 * 
	 */
	@Override
	public int insertSYS_IMAGE_SIMILARITY(String iMG_ID, Set<HamBean> hamBeans) {
		int temp = -1;
		String sql = "insert into SYS_IMAGE_SIMILARITY(F_IMGID,F_IMGID2,F_HMDIS) values(?,?,?)";
		for (HamBean hamBean : hamBeans) {
			if (dHashRedisServiceImpl.isKeyExists(hamBean.getDhash()) == true) {
				String imgIds = dHashRedisServiceImpl.get(hamBean.getDhash());
				for (String imageId2 : imgIds.split(",")) {
					Object args2[] = { iMG_ID, imageId2, hamBean.getHamcount() };
					temp = jdbcTemplate.update(sql, args2);
				}
			}
		}
		System.out.println("SYS_IMAGE_SIMILARITY表保存相似图片汉明距离sql==" + sql);
		return temp;
	}

}
