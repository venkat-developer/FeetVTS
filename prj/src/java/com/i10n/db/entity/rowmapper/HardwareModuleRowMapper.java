package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.HardwareModule;

public class HardwareModuleRowMapper implements RowMapper{
	private static Logger LOG = Logger.getLogger(HardwareModuleRowMapper.class);
	@Override
	public Object mapRow(ResultSet rs, int rownum) throws SQLException {
		HardwareModule hm = new HardwareModule(rs.getLong("id"),
												rs.getString("imei"),
												rs.getFloat("moduleversion"),
												rs.getDate("modulecreatedat"),
												rs.getDate("statuslastupdatedat"),
												rs.getInt("modulestatus"),
												rs.getLong("lastupdatedempid"),
												rs.getFloat("firmwareversion"),
												rs.getBoolean("deleted"),
												rs.getLong("ownerid"),
												rs.getString("mobilenumber"),
												rs.getString("simid"),
												rs.getString("simprovider")
												);
		LOG.info("Load hardware "+hm);
		return hm;
	}

}
