package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.HardwareModuleHistory;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class HardwareModuleHistoryRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		HardwareModuleHistory f = new HardwareModuleHistory(new LongPrimaryKey(rs.getLong("hardwareid")),
				rs.getString("updatedat"),
				rs.getInt("modulestatus"),
				rs.getLong("updatedbyemp"));
		return f;
	}
}
