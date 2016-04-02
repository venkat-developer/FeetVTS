package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.Trip;

public class TripRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {

		Trip newTrip = new Trip(rs.getLong("id"),
								rs.getString("tripname"),
								rs.getDate("tripstartdate"),
								rs.getBoolean("overridefuelcalibration"),
								rs.getFloat("speedlimit"),
								rs.getLong("vehicleid"),
								rs.getLong("driverid"),
								rs.getString("destination"),
								rs.getBoolean("scheduledtrip"),
								rs.getDate("enddate"),
								rs.getBoolean("active"),
								rs.getLong("cumulativedistance"),
								rs.getLong("geofencerefid"),
								rs.getBoolean("mail_sent"),
								rs.getInt("start_ad"),
								rs.getInt("idlepointstimelimit"));
		return newTrip;
	}


}
