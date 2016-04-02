package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.StudentHistory;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class StudentHistoryRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		StudentHistory studentHistory = new StudentHistory(new LongPrimaryKey(rs.getLong("id")),
				rs.getLong("studentid"),
				rs.getLong("stopid"), 
				rs.getLong("vehicleid"), 
				rs.getLong("routeid"), 
				rs.getInt("alertmins"),
				rs.getTimestamp("smssenttime"), 
				rs.getTimestamp("expectedtime"), 
				rs.getTimestamp("actualtime"),
				rs.getBoolean("smssent"));
		return studentHistory ;
	}

}
