package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.postgis.PGgeometry;
import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.LiveVehicleStatus;

public class LiveVehicleStatusCacheRowMapper implements RowMapper {

	@Override
	public Object mapRow(ResultSet rs, int arg1) throws SQLException {
		LiveVehicleStatus liveVehicleObject = new LiveVehicleStatus();
		
		liveVehicleObject.setTripId(rs.getLong("tripid"));
		liveVehicleObject.setImei(rs.getString("imei"));
		liveVehicleObject.setCumulativeDistance(rs.getFloat("cumulativedistance"));
		liveVehicleObject.setLastUpdatedAt(new Date(rs.getTimestamp("lastupdatedat").getTime()));
		liveVehicleObject.setGsmStrength(rs.getFloat("gsmstrength"));
		liveVehicleObject.setGpsStrength(rs.getFloat("gpsstrength"));
		liveVehicleObject.setSqd(rs.getLong("sqd"));
		liveVehicleObject.setPrevSqd(rs.getLong("sqd"));
		liveVehicleObject.setSqg(rs.getLong("sqg"));
		liveVehicleObject.setBatteryVoltage(rs.getFloat("batvolt"));
		liveVehicleObject.setOffroad(rs.getBoolean("isoffroad"));
		liveVehicleObject.setChargerConnected(rs.getBoolean("cc"));
		liveVehicleObject.setMaxSpeed(rs.getFloat("maxspeed"));
		liveVehicleObject.setVehicleId(rs.getLong("vehicleid"));
		liveVehicleObject.setDriverId(rs.getLong("driverid"));
		liveVehicleObject.setSpeedLimit(rs.getDouble("speedlimit"));
		liveVehicleObject.setScheduleTrip(rs.getBoolean("scheduledtrip"));
		liveVehicleObject.setTripStartDate(rs.getDate("tripstartdate"));
		liveVehicleObject.setTripEndDate(rs.getDate("enddate"));
		liveVehicleObject.setDigital1(rs.getBoolean("digital1"));
		liveVehicleObject.setDigital2(rs.getBoolean("digital2"));
		liveVehicleObject.setGps_fix_information(rs.getInt("gps_fix_information"));
		liveVehicleObject.setModuleUpdateTime(new Date(rs.getTimestamp("moduleupdatetime").getTime()));
		PGgeometry geom = (PGgeometry)rs.getObject("vehiclelocation");
		liveVehicleObject.setLocation(geom.getGeometry());
		liveVehicleObject.setLatestButtonPressed(rs.getInt("latestbuttonpressed"));
		liveVehicleObject.setIdle(rs.getBoolean("isidle"));
		liveVehicleObject.setCourse(rs.getFloat("course"));
		return liveVehicleObject;
	}

}
