package com.i10n.db.entity.rowmapper;

/** 
 * RouteSchedulesRowMapper class gets the ResultSet from DataBase by calling 
 * the constructor from RouteSchedules class and returns
 * @author Mrunal
 */
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.RouteSchedule;


public class RouteScheduleAssignRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		RouteSchedule routeSchedule = new RouteSchedule(rs.getString("routescheduleid"));
		
		return routeSchedule;
	}

}
