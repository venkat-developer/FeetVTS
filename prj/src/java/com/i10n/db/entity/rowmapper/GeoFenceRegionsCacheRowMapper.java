package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgis.PGgeometry;
import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.GeoFenceRegions;

public class GeoFenceRegionsCacheRowMapper implements RowMapper {
	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		GeoFenceRegions geoFenceRegion = new GeoFenceRegions(rs.getLong("vehicleid"),rs.getLong("regionid"),rs.getBoolean("insideregion"),rs.getString("name"),
				rs.getDouble("speed"),rs.getLong("userid"),rs.getInt("shape"),((PGgeometry)rs.getObject("polygon")).getGeometry(),rs.getInt("regiontype"),rs.getString("regioncode"));
		return geoFenceRegion;
	}
}