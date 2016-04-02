package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.StopDeviation;

public class StopDeviationRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		StopDeviation stopDev = new StopDeviation(rs.getLong("id"), rs
				.getLong("vehicleid"), rs.getString("vehiclename"), rs
				.getLong("stopid"), rs.getString("missedstopname"), rs
				.getLong("routeid"), rs.getTimestamp("expectedtime"),rs.getTimestamp("occurredat"));
		return stopDev;
	}

}
