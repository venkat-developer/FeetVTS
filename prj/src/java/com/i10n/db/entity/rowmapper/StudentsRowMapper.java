package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.Students;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class StudentsRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		Students students = new Students(new LongPrimaryKey(rs.getLong("id")), 
				rs.getString("studentname"), 
				rs.getLong("stopid"), 
				rs.getLong("routeid"), 
				rs.getLong("mobileno"), 
				rs.getInt("alertmin"),rs.getLong("ownerid"));
		return students;
	}

}
