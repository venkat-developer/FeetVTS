package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.RouteSchedule;

public class RouteScheduleRowMapper implements RowMapper {

	@SuppressWarnings("deprecation")
	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		RouteSchedule routeSchedule = new RouteSchedule();
		routeSchedule.setRouteScheduleId(rs.getString("routescheduleid"));
		routeSchedule.setRouteId(rs.getLong("routeid"));
		routeSchedule.setStopId(rs.getLong("stopid"));
		routeSchedule.setSequenceNumber(rs.getInt("seqno"));
		Time time = rs.getTime("expectedtime");
		time.setSeconds(0);
		routeSchedule.setExpectedTime(time); 
		routeSchedule.setEstimatedDistance(rs.getLong("estimateddistance"));
		routeSchedule.setSpanDay(rs.getInt("spanday"));
		return routeSchedule;
	}

}
