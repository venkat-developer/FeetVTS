package com.i10n.db.entity.rowmapper;


import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.ProvidersDetails;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class ProvidersDetailsRowMapper implements RowMapper{

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		// TODO Auto-generated method stub
ProvidersDetails newprovider=new ProvidersDetails(new LongPrimaryKey(rs.getLong("id")),
	        		rs.getBoolean("isget"),
	             rs.getString("baseurl"),
	                rs.getString("mobilenumberparamname"),
	                rs.getString("messageparamname"),
	                rs.getString("multiplenumberdelimiter"),
	                rs.getString("userparamname"),
	                rs.getString("passparamname"),
rs.getString("creditcheckurl"));

		
		return newprovider;
	}

	
}
