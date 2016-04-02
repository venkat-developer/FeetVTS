package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.RouteDeviation;

public class RouteDeviationRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		RouteDeviation routeDev = new RouteDeviation(rs.getLong("id"), rs
				.getLong("vehicleid"), rs.getString("vehiclename"), rs
				.getLong("stopid"), rs.getString("stopname"), rs
				.getLong("routeid"), rs.getString("routename"), rs
				.getFloat("estimateddistance"), rs.getFloat("actualdistance"),rs.getTimestamp("occurredat"));
		return routeDev;
	}

}
