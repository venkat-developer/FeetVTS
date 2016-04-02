package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.Driver;

public class DriverSmartcardRowMapper implements RowMapper {
	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		Driver newDriver = new Driver(rs.getLong("id"),
				rs.getString("firstname"),
				rs.getLong("vehicleId"),
				rs.getString("imei")
				
			);
		return newDriver;
	}

}
