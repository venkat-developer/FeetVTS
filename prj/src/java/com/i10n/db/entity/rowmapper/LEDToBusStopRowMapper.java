package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.LEDToBusStop;

public class LEDToBusStopRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int rowNumber) throws SQLException {
		LEDToBusStop ledToBusStop = new LEDToBusStop(rs.getString("IMEI"), 
										rs.getLong("busstopid"), 
										rs.getInt("ledtype"));
		return ledToBusStop;
	}

}
