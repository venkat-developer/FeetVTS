package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.User;
import com.i10n.db.entity.User.UserRole;
import com.i10n.db.entity.User.UserStatus;

public class UserRowMapper implements RowMapper{

	@Override
	public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		UserRole role = UserRole.getUserRole(rs.getInt("role")); 
		
		UserStatus status = UserStatus.getUserStatus(rs.getInt("userstatus"));
		
		User newUser = new User(rs.getLong("id"),
								rs.getString("username"),
								rs.getString("pass"),
								role,
								status,
								rs.getLong("groupid"),
								rs.getLong("owner_id"),
								rs.getString("firstname"),
								rs.getString("lastname"),
								rs.getInt("offroadcount"),
								rs.getInt("nogprscount"));
		return newUser;
	}

}
