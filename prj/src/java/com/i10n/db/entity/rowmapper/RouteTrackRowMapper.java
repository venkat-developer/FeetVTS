package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.RouteTrack;

public class RouteTrackRowMapper implements RowMapper{

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		RouteTrack routeTrack = new RouteTrack(new Date(rs.getTimestamp("startdate").getTime()),
					rs.getString("imei"),
					rs.getLong("routeid"), 
//					rs.getLong("tripid"),
					rs.getLong("starttrackhistoryid"), 
					rs.getLong("endtrackhistoryid"));
		return routeTrack;
	}

}
