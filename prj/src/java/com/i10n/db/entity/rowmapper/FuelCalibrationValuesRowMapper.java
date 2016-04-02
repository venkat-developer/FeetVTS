package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.FuelCalibrationValues;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class FuelCalibrationValuesRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		FuelCalibrationValues f = new FuelCalibrationValues(new LongPrimaryKey(rs.getLong("calibrationid")),
				rs.getInt("advalue"),
				rs.getInt("fuelinliters"),
				rs.getLong("tripid"),
				rs.getDouble("gradient"),
				rs.getInt("base_ad"),
				rs.getInt("base_fuel"));
		return f;
	}
}



