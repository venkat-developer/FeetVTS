package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.LatestPacketDetails;

public class LatestPacketDetailsRowMapper implements RowMapper{


	@Override
	public Object mapRow(ResultSet rs, int currentRowNumber) throws SQLException {
		LatestPacketDetails latestPacketDetails = new LatestPacketDetails(rs.getLong("liveid"),
													rs.getLong("trackhistory_id"), 
													rs.getLong("idle_points_id"), 
													rs.getLong("last_saved_trackhistory_id"),
													rs.getLong("latest_trackhistory_pingcounter"),
													rs.getLong("latest_but_one_trackhistory_pingcounter"),
													rs.getString("last_computed_idle_points_ids"),
													rs.getDate("last_computed_idle_point_end_time")
													);
		return latestPacketDetails;
	}

}
