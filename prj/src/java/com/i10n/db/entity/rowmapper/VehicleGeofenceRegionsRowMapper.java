package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.VehicleGeofenceRegions;

public class VehicleGeofenceRegionsRowMapper implements RowMapper{

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		VehicleGeofenceRegions aclRegion = new VehicleGeofenceRegions(rs.getLong("vehicleid"), 
				rs.getLong("regionid"),rs.getBoolean("mail_sent")); 
		return aclRegion;
	}

}
