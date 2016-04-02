package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.DriverHistory;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class DriverHistoryRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		DriverHistory f = new DriverHistory(new LongPrimaryKey(rs.getLong("driverid")),
				rs.getString("updatedtime"),
				rs.getInt("driverstatus"),
				rs.getLong("updatedbyuser"));
		return f;
	}
}
