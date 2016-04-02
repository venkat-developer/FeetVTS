package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.Stops;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class StopsRowMapper implements RowMapper{

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		Stops stops = new Stops(new LongPrimaryKey(rs.getLong("id")),
				rs.getString("stopname"), 
				rs.getString("knownas"), 
				rs.getDouble("lat"), 
				rs.getDouble("lon"),
				rs.getLong("ownerid"));
		return stops;
	}

}
