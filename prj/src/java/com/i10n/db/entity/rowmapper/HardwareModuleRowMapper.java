package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.HardwareModule;

public class HardwareModuleRowMapper implements RowMapper{

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
		return hm;
	}

}
