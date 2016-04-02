package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.postgis.PGgeometry;
import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.TrackHistory;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class TrackHistoryRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int currentRowNumber) throws SQLException {
		
		PGgeometry geom= (PGgeometry)rs.getObject("location");
		TrackHistory tHistory = new TrackHistory(new LongPrimaryKey(rs.getLong("id")),
				rs.getLong("tripid"),
				rs.getFloat("gpssignal"),
				rs.getFloat("gsmsignal"),
				rs.getFloat("direction"),
				rs.getLong("sqd"),
				rs.getLong("sqg"),
				rs.getFloat("batteryvoltage"),
				rs.getFloat("cd"),
				rs.getFloat("speed"),
				rs.getInt("analogue1"),
				rs.getInt("analogue2"),
				rs.getInt("error"),
				rs.getLong("lac"),
				rs.getLong("cid"),
				rs.getBoolean("isping"),
				rs.getBoolean("ismrs"),
				rs.getBoolean("ischargerconnected"),
				rs.getBoolean("isrestart"),
				rs.getBoolean("digital1"),
				rs.getBoolean("digital2"),
				rs.getBoolean("digital3"),
				rs.getBoolean("ispanic"),
				
				new Date(rs.getTimestamp("occurredat").getTime()),
				geom.getGeometry(),
				
				rs.getFloat("fuel"),
				rs.getFloat("distance"),
				rs.getLong("pingcounter"),
				rs.getInt("gps_fix_information")
				);
		return tHistory;
	}

}
