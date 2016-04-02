package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.postgis.PGgeometry;
import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.AlertOrViolation;
import com.i10n.db.entity.AlertOrViolation.AlertType;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

/**
 * Alert architecture rowmapper class
 * 
 * @author Dharmaraju V
 *
 */
public class AlertOrViolationRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		PGgeometry geom = (PGgeometry) rs.getObject("alertlocation");
		AlertOrViolation alert=new AlertOrViolation(new LongPrimaryKey(rs.getLong("id")),
								rs.getLong("vehicleid"),
								rs.getLong("driverid"),
								new Date(rs.getTimestamp("alerttime").getTime()),
								new Date(rs.getTimestamp("occurredat").getTime()),
								AlertType.get(rs.getInt("alerttype")),
								rs.getString("alerttypevalue"),
								geom.getGeometry(),
								rs.getLong("alertlocationreferenceid"),
								rs.getString("alertlocationtext"),
								rs.getBoolean("isdisplayed"),
								rs.getBoolean("isusernotified"));
		return alert;
	}
}

