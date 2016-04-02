package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class VehicleRowMapper implements RowMapper{
	
    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
    	
        Vehicle vehicle = new Vehicle(new LongPrimaryKey(rs.getLong("id")),
        		                rs.getString("displayname"),
                                rs.getString("make"),
                                rs.getString("model"),
                                rs.getString("year"),
                                rs.getLong("imeiid"),
                                rs.getDate("odometer_updatedat"),
                                rs.getInt("odometer_value"),
                                rs.getLong("fuelcaliberationid"),
                                rs.getInt("vehicle_icon_pic_id"),
        						rs.getLong("groupid"),
        						rs.getBoolean("deleted"),
        						rs.getString("type")
        						);
        return vehicle;
    }

}
