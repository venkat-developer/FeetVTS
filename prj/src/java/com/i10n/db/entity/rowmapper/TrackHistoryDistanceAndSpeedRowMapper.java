package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.TrackHistory;

public class TrackHistoryDistanceAndSpeedRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		TrackHistory tHistory = new TrackHistory(
				rs.getFloat("sum"),
				rs.getFloat("max")				
				);
		return tHistory;
	}

}
