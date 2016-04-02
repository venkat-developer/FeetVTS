package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.Driver;

public class DriverGroupReportRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		Driver newDriver = new Driver(rs.getLong("id"),
				rs.getString("firstname"),
				rs.getString("lastname"),
				rs.getString("licenseno"),
				rs.getString("photo"),
				rs.getLong("groupid"),
				rs.getBoolean("deleted"),
				rs.getString("group_value"));
		return newDriver;
	}

}
