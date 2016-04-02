package com.i10n.db.entity.rowmapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.VehicleHistory;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class VehicleHistoryRowMapper implements RowMapper {
	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		VehicleHistory vehicleHistory = new VehicleHistory(new LongPrimaryKey(rs.getLong("vehicleid")),
				rs.getString("vehicleattended"),
				rs.getString("imei"),
				rs.getBoolean("battrychanged"),
				rs.getBoolean("fusechanged"),
				rs.getString("updatedbyuser"),
				new Date(rs.getTimestamp("updatedtime").getTime()));
		return vehicleHistory;
	}
}
