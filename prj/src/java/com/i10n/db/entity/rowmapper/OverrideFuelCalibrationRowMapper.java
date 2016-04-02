package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.OverrideFuelCalibration;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
   

public class OverrideFuelCalibrationRowMapper implements RowMapper{

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		// TODO Auto-generated method stub
OverrideFuelCalibration newride=new OverrideFuelCalibration(new LongPrimaryKey(rs.getLong("tripid")),
	        		
	                rs.getLong("calibrationid"));
	             return newride;
	}

	
}


