package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.EtaArya;

public class EtaAryaRowMapper implements RowMapper {
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		EtaArya etaarya = new EtaArya(
				rs.getLong("id"),
				rs.getLong("stopid"), 
				rs.getTimestamp("rec_time"));
		       
		return etaarya;
	}
}
