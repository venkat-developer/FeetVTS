package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.postgis.PGgeometry;
import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class VehicleDetailsRowMapper implements RowMapper{
	
    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
    	
    	PGgeometry geom = (PGgeometry) rs.getObject("vehiclelocation");
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
                                rs.getString("optional1"),
                                rs.getString("optional2"),
                                rs.getString("optional3"),
        						rs.getLong("groupid"),
        						rs.getBoolean("deleted"),
        						rs.getString("type"),
        						rs.getLong("maxspeed"),
        						geom.getGeometry(),
        						rs.getLong("driverid"),
        						rs.getString("firstname"),
        						rs.getString("lastname"),
        						rs.getLong("drivergroupid"),
        						rs.getString("vehicleGroupValue"),
        						rs.getString("drivergroupvalue"),
        						rs.getFloat("gpsstrength"),
        						rs.getFloat("gsmstrength"),
        						rs.getFloat("batvolt"),
        						rs.getInt("fuelad"),
        						rs.getLong("tripid"),
        						new Date(rs.getTimestamp("lastupdatedat").getTime())
        						);
        return vehicle;
    }

}
