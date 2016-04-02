package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.postgis.PGgeometry;
import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.LiveVehicleStatus;

public class LiveVehicleStatusRowMapper implements RowMapper {

	
	
	@Override
	public Object mapRow(ResultSet rs, int currentRowNumber) throws SQLException {
		PGgeometry geom = (PGgeometry)rs.getObject("vehiclelocation");
		
		LiveVehicleStatus vehicleStatus = new LiveVehicleStatus(rs.getLong("tripid"),
											geom.getGeometry(),
											rs.getFloat("gsmstrength"),
											rs.getFloat("gpsstrength"),
											rs.getFloat("batvolt"),
											rs.getFloat("distance"),
											rs.getBoolean("cc"),
											rs.getLong("sqd"),
											rs.getLong("sqg"),
											rs.getInt("mrs"),
											rs.getFloat("course"),
											rs.getBoolean("isidle"),
											rs.getFloat("maxspeed"),
											rs.getInt("fuelad"),
											rs.getInt("cdc_counter"),
											rs.getFloat("cumulativedistance"),
											rs.getBoolean("mailsent"),
											rs.getLong("prevsqd"),
											rs.getInt("cid"),
											rs.getInt("lac"),
											rs.getString("geolocation"),
											new Date(rs.getTimestamp("lastupdatedat").getTime()),
											rs.getInt("rs"),
											new Date(rs.getTimestamp("moduleupdatetime").getTime()), 
											rs.getBoolean("isoffroad"));
		return vehicleStatus;
	}

}
