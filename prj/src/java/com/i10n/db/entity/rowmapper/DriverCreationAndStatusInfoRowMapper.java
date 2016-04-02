package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.DriverCreationAndStatusInfo;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
public class DriverCreationAndStatusInfoRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		DriverCreationAndStatusInfo newdriver = new DriverCreationAndStatusInfo(new LongPrimaryKey(rs.getLong("driverid")),
				rs.getString("createdat"),
				rs.getLong("lastupdateduserid"),
				rs.getInt("status"),
				rs.getLong("createduserid"),
		        rs.getLong("currentownerid"));
		return newdriver;
	}

}

