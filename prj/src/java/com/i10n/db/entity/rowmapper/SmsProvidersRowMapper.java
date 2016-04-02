package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.SmsProviders;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class SmsProvidersRowMapper implements RowMapper{

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		  SmsProviders sms = new SmsProviders(
	        		new LongPrimaryKey(rs.getLong("providerid")),
	        		rs.getString("providername"),
	                rs.getBoolean("multiplesmssupport"),
	                rs.getBoolean("reportsupport"),
	                rs.getString("username"),
	                rs.getString("password"),
	                rs.getInt("numberofsmssent"),
	                rs.getString("lastsmssentat"),
	                rs.getString("lastsmssenttomobile"),
rs.getString("lastsmstext"));


	        return sms;
	    
	} 
	
	   

}
