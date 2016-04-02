package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.ShiftTimings;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class ShiftTimingsRowMapper implements RowMapper{

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		ShiftTimings sms = new ShiftTimings(
        		new LongPrimaryKey(rs.getLong("id")),
        		rs.getLong("vehicleid"),
                rs.getTime("starttime"),
                rs.getTime("endtime"));
        return sms;
	}
}
