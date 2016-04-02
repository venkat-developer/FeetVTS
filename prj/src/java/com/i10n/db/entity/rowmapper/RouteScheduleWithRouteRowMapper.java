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
import com.i10n.db.entity.primarykey.LongPrimaryKey;


public class RouteScheduleWithRouteRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		RouteSchedule routeSchedule = new RouteSchedule(new LongPrimaryKey(rs.getLong("id")),
				rs.getString("routescheduleid"),
				rs.getInt("routeid"),
				rs.getInt("stopid"),
				rs.getInt("seqno"),
				rs.getTime("expectedtime"),
				rs.getLong("estimateddistance"),
				rs.getInt("spanday"),
				rs.getString("routename"),
				rs.getString("stopname")
				);

		return routeSchedule;
	}

}
