package com.i10n.db.entity.rowmapper;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgis.PGgeometry;
import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.BusStops;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class BusStopsRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		PGgeometry geom = (PGgeometry)rs.getObject("location");
		
		BusStops newBusstop = new BusStops(new LongPrimaryKey(rs.getLong("id")),
				rs.getString("busstopname"),
				geom.getGeometry());
		return newBusstop;
	}

}
