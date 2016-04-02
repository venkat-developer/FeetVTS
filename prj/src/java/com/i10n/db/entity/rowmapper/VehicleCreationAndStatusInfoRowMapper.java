package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.VehicleCreationAndStatusInfo;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class VehicleCreationAndStatusInfoRowMapper implements RowMapper{

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		VehicleCreationAndStatusInfo sms = new VehicleCreationAndStatusInfo(
	        		new LongPrimaryKey(rs.getLong("vehicleid")),
	        		rs.getString("createdat"),
	                rs.getLong("lastupdateduserid"),
	                rs.getInt("status"),
	                rs.getLong("createduserid"),
	                rs.getLong("currentownerid"));
	            return sms;
	    
	} 
}


