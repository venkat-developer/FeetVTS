package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.EmriVehiclesBaseStation;

public class EmriVehiclesBaseStationRowMapper implements RowMapper {

	
	
	@Override
	public Object mapRow(ResultSet rs, int currentRowNumber) throws SQLException {
		EmriVehiclesBaseStation emrirajasthan = new EmriVehiclesBaseStation(rs.getLong("id"),
											rs.getLong("vehicleid"),
											rs.getString("district"),
											rs.getString("baselocation"),
		                                    rs.getLong("crewno"));
		return emrirajasthan;
	}

}
