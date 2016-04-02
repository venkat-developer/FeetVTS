package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.EtaDisplay;

public class EtaDisplayRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		EtaDisplay etaDisplay = new EtaDisplay(
				rs.getLong("vehicleid"),
				rs.getLong("routeid"), 
				rs.getLong("stopid"), 
				rs.getInt("arrivaltime"),
				rs.getString("routename"),
				rs.getInt("type"),
				rs.getBoolean("deleted"),
				rs.getInt("seqno"));
		return etaDisplay;
	}
}
