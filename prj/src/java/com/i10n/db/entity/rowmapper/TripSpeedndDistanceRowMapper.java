package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.TripDistancendSpeed;

/**
 * @author Prashanth
 *
 */

public class TripSpeedndDistanceRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		
		TripDistancendSpeed trip=new TripDistancendSpeed(
				rs.getDouble("max"),
				rs.getDouble("sum")
				
		);
		
		return trip;
	}

}
