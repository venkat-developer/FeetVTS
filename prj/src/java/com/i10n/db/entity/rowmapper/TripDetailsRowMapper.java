package com.i10n.db.entity.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.postgis.PGgeometry;
import org.springframework.jdbc.core.RowMapper;

import com.i10n.db.entity.Driver;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.Trip;
import com.i10n.db.entity.TripDetails;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.dbCacheManager.LoadDriverDetails;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;
import com.i10n.dbCacheManager.LoadVehicleDetails;

public class TripDetailsRowMapper implements RowMapper{

	private static Logger LOG = Logger.getLogger(TripDetailsRowMapper.class);

	@Override
	public Object mapRow(ResultSet resultSet, int currentRowNumber) throws SQLException {

		LongPrimaryKey tripId = new LongPrimaryKey(resultSet.getLong("id"));

		Trip staticTripDetails = new Trip(tripId.getId(),
				resultSet.getString("tripname"),
				resultSet.getDate("tripstartdate"),
				resultSet.getBoolean("overridefuelcalibration"),
				resultSet.getFloat("speedlimit"),
				resultSet.getLong("vehicleid"),
				resultSet.getLong("driverid"),
				resultSet.getString("destination"),
				resultSet.getBoolean("scheduledtrip"),
				resultSet.getDate("enddate"),
				resultSet.getBoolean("active"),
				resultSet.getLong("cumulativedistance"),
				resultSet.getLong("geofencerefid"),
				resultSet.getBoolean("mail_sent"),
				resultSet.getInt("start_ad"),
				resultSet.getInt("idlepointstimelimit"));

		PGgeometry location = (PGgeometry)resultSet.getObject("vehiclelocation");

		LiveVehicleStatus dynamicTripStatus = new LiveVehicleStatus(
				tripId.getId(),
				location.getGeometry(),
				resultSet.getFloat("gsmstrength"),
				resultSet.getFloat("gpsstrength"),
				resultSet.getFloat("batvolt"),
				resultSet.getFloat("distance"),
				resultSet.getBoolean("cc"),
				resultSet.getLong("sqd"),
				resultSet.getLong("sqg"),
				resultSet.getInt("mrs"),
				resultSet.getFloat("course"),
				resultSet.getBoolean("isidle"),
				resultSet.getFloat("maxspeed"),
				resultSet.getInt("fuelad"),
				resultSet.getInt("cdc_counter"),
				resultSet.getFloat("cumulativedistance"),
				resultSet.getBoolean("mailsent"),
				resultSet.getLong("prevsqd"),
				resultSet.getInt("cid"),
				resultSet.getInt("lac"),
				resultSet.getString("geolocation"),
				new Date(resultSet.getTimestamp("lastupdatedat").getTime()),
				resultSet.getInt("rs"),
				new Date(resultSet.getTimestamp("moduleupdatetime").getTime()),
				resultSet.getBoolean("offroad"));
		
		Vehicle vehicle = new Vehicle(
				new LongPrimaryKey(resultSet.getLong("id")),
				resultSet.getString("displayname"),
				resultSet.getString("make"),
				resultSet.getString("model"),
				resultSet.getString("year"),
				resultSet.getLong("imeiid"),
				resultSet.getDate("odometer_updatedat"), 
				resultSet.getInt("odometer_value"), 
				resultSet.getLong("fuelcaliberationid"),
				resultSet.getInt("vehicle_icon_pic_id"),
				resultSet.getLong("groupid"),
				resultSet.getBoolean("deleted"),
				resultSet.getString("type"));

		Driver driver = new Driver(
				resultSet.getLong("id"),
				resultSet.getString("firstname"),
				resultSet.getString("lastname"),
				resultSet.getString("licenseno"),
				resultSet.getString("photo"),
				resultSet.getLong("groupid"),
				resultSet.getBoolean("deleted"));       
		
		TripDetails tripDetails = new TripDetails(tripId, staticTripDetails, dynamicTripStatus, vehicle, driver);
		return tripDetails;
	}

	/**
	 * To fetch details of all the required vehicles from cache
	 * @param resultSet
	 * @return TripDetails List
	 * @throws SQLException
	 */
	 public List<TripDetails> getTripDetailList (List<Trip> tripList) {
		List<TripDetails> tripDetailsList = new ArrayList<TripDetails>();	
		if(tripDetailsList != null){
			for (Trip trip : tripList) {

				Vehicle vehicle = LoadVehicleDetails.getInstance().retrieve(trip.getVehicleId());
				if (vehicle == null) {
					LOG.error("Expecting vehicle with id "+trip.getVehicleId()+" which is not in cache as well as in DB");
					continue;
				}

				LiveVehicleStatus lvs = LoadLiveVehicleStatusRecord.getInstance().retrieve(vehicle.getImei());
				if (lvs == null) {
					LOG.error("Expecting lvos with imei "+vehicle.getImei()+" which is not in cache as well as in DB");
					continue;
				}

				Driver driver = LoadDriverDetails.getInstance().retrieve(trip.getDriverId());
				if (driver == null) {
					LOG.error("Expecting driver with id "+trip.getDriverId()+" which is not in cache as well as in DB");
					continue;
				}
				//LOG.debug("VehicleId and MaxSpeed "+lvs.getVehicleId() +lvs.getMaxSpeed());
				TripDetails tripDetails = new TripDetails(trip.getId().getId(), trip, lvs, vehicle, driver);
				tripDetailsList.add(tripDetails);			
			}
		}

		LOG.debug("Fetched live track data from cache ");
		return tripDetailsList;
	 }
} 