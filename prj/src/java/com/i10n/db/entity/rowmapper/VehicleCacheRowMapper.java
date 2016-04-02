package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class VehicleCacheRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		Vehicle vehicle = new Vehicle();
		vehicle.setId(new LongPrimaryKey(rs.getLong("id")));
		vehicle.setMake(rs.getString("make"));
		vehicle.setModel(rs.getString("model"));
		vehicle.setModelYear(rs.getString("year"));
		vehicle.setFuelcaliberationid(rs.getLong("fuelcaliberationid"));
		vehicle.setOdometerUpdatedAt(rs.getDate("odometer_updatedat"));
		vehicle.setOdometerValue(rs.getInt("odometer_value"));
		vehicle.setVehicleIconPicId(rs.getInt("vehicle_icon_pic_id"));
		vehicle.setGroupId(rs.getLong("groupid"));
		vehicle.setDisplayName(rs.getString("displayname"));
		vehicle.setDeleted(rs.getBoolean("deleted"));
		vehicle.setGroupName(rs.getString("groupname"));
		vehicle.setImei(rs.getString("imei"));
		vehicle.setImeiId(rs.getLong("imeiid"));
		return vehicle;
	}

}
