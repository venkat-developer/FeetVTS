package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.ACLDriver;

public class ACLDriverRowMapper implements RowMapper {
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		ACLDriver driver=new ACLDriver(rs.getLong("driverid"), rs.getLong("userid"));
		return driver;
	}

}
