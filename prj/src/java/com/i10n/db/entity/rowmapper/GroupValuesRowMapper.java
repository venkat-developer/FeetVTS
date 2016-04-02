package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.GroupValues;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class GroupValuesRowMapper implements RowMapper{

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		GroupValues groupValue = new GroupValues(new LongPrimaryKey(rs.getLong("id")),
												 rs.getString("group_value"),
												 rs.getLong("groupid"),
												 rs.getBoolean("deleted"));
		return groupValue;
	}
	
}
