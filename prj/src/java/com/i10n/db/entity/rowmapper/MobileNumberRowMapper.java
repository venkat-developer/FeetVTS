package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.MobileNumber;

public class MobileNumberRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		MobileNumber alert = new MobileNumber(rs.getLong("id"), rs
				.getString("name"), rs.getLong("userid"), rs
				.getLong("mobilenumber"), rs.getBoolean("overspeeding"), rs
				.getBoolean("geofencing"), rs.getBoolean("chargerdisconnected"));
		return alert;
	}

}
