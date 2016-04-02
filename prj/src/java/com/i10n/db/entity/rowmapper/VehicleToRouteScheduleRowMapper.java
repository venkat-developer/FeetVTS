package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.VehicleToRouteSchedule;

public class VehicleToRouteScheduleRowMapper implements  RowMapper{

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		VehicleToRouteSchedule vehicleToRouteSchedule = new VehicleToRouteSchedule(rs.getLong("vehicleid"), 
																					rs.getLong("routeid"), 
																					rs.getTime("scheduletime"));
		
		return vehicleToRouteSchedule;
	}

}
