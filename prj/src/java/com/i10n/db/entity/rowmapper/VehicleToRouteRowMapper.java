package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.VehicleToRoute;

public class VehicleToRouteRowMapper implements RowMapper {
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		VehicleToRoute driver=new VehicleToRoute(rs.getLong("vehicleid"), rs.getLong("routeid"));
		return driver;
	}

}
