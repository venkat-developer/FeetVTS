package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.TimeDeviation;

public class TimeDeviationRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {

		TimeDeviation timeDev = new TimeDeviation(rs.getLong("id"), rs
				.getLong("vehicleid"), rs.getString("vehiclename"), rs
				.getLong("stopid"), rs.getString("stopname"), rs
				.getLong("routeid"), rs.getString("routename"), rs
				.getTimestamp("expectedtime"), rs.getTimestamp("actualtime"));
		return timeDev;
	}

}
