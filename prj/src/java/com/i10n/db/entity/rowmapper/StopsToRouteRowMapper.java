package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.StopsToRoute;

public class StopsToRouteRowMapper implements RowMapper {
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		StopsToRoute driver=new StopsToRoute(rs.getLong("stopid"), rs.getLong("routeid"));
		return driver;
	}

}
