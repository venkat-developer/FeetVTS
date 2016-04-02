package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.BusRoutes;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
public class BusRoutesRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		BusRoutes newBusroute = new BusRoutes(new LongPrimaryKey(rs.getLong("id")),
				rs.getLong("busstopid"),
				rs.getInt("busstopsequence"),
				rs.getInt("busroutenumber"),
				rs.getString("expectedtime"),
		        rs.getInt("shiftnumber"));
		return newBusroute;
	}

}

