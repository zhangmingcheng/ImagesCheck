package com.osp.imagecheck.bean;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * 
 * @author zhangmingcheng
 */
public class ImageHMCodeRowMapper implements RowMapper<ImageHMCode>{
	@Override
	public ImageHMCode mapRow(ResultSet rs, int rowNum) throws SQLException {
		ImageHMCode imageHMCode = new ImageHMCode();
		imageHMCode.setF_HMCODE(rs.getString("f_HMCODE"));
		imageHMCode.setF_IMGID(rs.getString("f_IMGID"));
		imageHMCode.setF_MD5(rs.getString("f_MD5"));
		imageHMCode.setF_ZZJG(rs.getString("f_ZZJG"));
		imageHMCode.setF_CRTIME(rs.getDate("f_CRTIME"));
		return imageHMCode;
	}
}
