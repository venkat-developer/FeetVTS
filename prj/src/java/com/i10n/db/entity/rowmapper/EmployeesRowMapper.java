package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.Employees;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class EmployeesRowMapper implements RowMapper{

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		Employees emp= new Employees(
        		new LongPrimaryKey(rs.getLong("id")),
        		rs.getString("name"));
                        return emp;
    

	
	}

}
