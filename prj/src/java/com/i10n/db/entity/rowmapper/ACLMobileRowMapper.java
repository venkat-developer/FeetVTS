package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.ACLMobile;

public class ACLMobileRowMapper implements RowMapper{

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		ACLMobile driver=new ACLMobile(rs.getLong("vehicleid"), rs.getLong("mobileid"));
		return driver;
	}

}
