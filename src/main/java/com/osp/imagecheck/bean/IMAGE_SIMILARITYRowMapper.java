package com.osp.imagecheck.bean;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class IMAGE_SIMILARITYRowMapper implements RowMapper<IMAGE_SIMILARITY>{
	@Override
	public IMAGE_SIMILARITY mapRow(ResultSet rs, int rowNum) throws SQLException {
		IMAGE_SIMILARITY imageSIMILARITY = new IMAGE_SIMILARITY();
		imageSIMILARITY.setF_IMGID(rs.getString("f_IMGID"));
		imageSIMILARITY.setF_IMGID2(rs.getString("f_IMGID2"));
		imageSIMILARITY.setF_HMDIS(rs.getString("f_HMDIS"));
		imageSIMILARITY.setF_MD5SAME(rs.getString("f_MD5SAME"));
		return imageSIMILARITY;
	}
}
