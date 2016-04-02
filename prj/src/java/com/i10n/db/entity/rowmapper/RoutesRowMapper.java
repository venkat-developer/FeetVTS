package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.Routes;

public class RoutesRowMapper implements RowMapper{
	
	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		Routes routes = new Routes(rs.getLong("id"),
				rs.getString("routename"), 
				rs.getString("startpoint"), 
				rs.getString("endpoint"),
				rs.getTime("starttime"),
				rs.getTime("endtime"),
				rs.getLong("ownerid"),
				rs.getInt("spanningdays"),
				rs.getString("routebitmaphex"),
				rs.getString("destinationbitmaphex"),
				rs.getString("englishrouteanddestinationbitmaphex"),
				rs.getString("localrouteanddestinationbitmaphex"));
		return routes;
	}
}