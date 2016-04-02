package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.VehicleToStudent;

public class VehicleToStudentRowMapper implements RowMapper {
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		VehicleToStudent driver=new VehicleToStudent(rs.getLong("vehicleid"), rs.getLong("studentid"));
		return driver;
	}

}
