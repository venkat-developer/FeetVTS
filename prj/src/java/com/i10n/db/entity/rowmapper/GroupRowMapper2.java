package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.Group;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class GroupRowMapper2 implements RowMapper{
	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		Group group = new Group(new LongPrimaryKey(rs.getLong("id")),
												 rs.getString("name"));
		return group;
	}
}
