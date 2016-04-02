package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.ACLVehicle;

public class ACLVehicleRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		ACLVehicle aclVehicle = new ACLVehicle(rs.getLong("vehicleid"), 
									rs.getLong("userid")); 
		return aclVehicle;
	}

}
