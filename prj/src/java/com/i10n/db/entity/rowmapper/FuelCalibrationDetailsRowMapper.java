package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.FuelCalibrationDetails;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class FuelCalibrationDetailsRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		FuelCalibrationDetails f = new FuelCalibrationDetails(new LongPrimaryKey(rs.getLong("id")),
				rs.getString("calibratedat"),
				rs.getLong("calibratedbyemp"),
				rs.getInt("minad"),
				rs.getInt("maxad"));
		return f;
	}
}
