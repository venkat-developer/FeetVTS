package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.UserHistory;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class UserHistoryRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		UserHistory f = new UserHistory(new LongPrimaryKey(rs.getLong("userid")),
				rs.getInt("userstatus"),
				rs.getString("updatedat"));
		return f;
	}
}
