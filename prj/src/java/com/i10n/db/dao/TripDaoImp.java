package com.i10n.db.dao;

import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.Driver;
import com.i10n.db.entity.Trip;
import com.i10n.db.entity.User;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.DriverRowMapper;
import com.i10n.db.entity.rowmapper.TripRowMapper;
import com.i10n.db.entity.rowmapper.VehicleRowMapper;
import com.i10n.db.idao.ITripDAO;
import com.i10n.dbCacheManager.LoadTripDetails;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.web.utils.SessionUtils;

@SuppressWarnings({"deprecation","unchecked"})
public class TripDaoImp implements ITripDAO {

	private static Logger LOG = Logger.getLogger(TripDaoImp.class);
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer tripsIdIncrementer;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setTripsIdIncrementer(
			DataFieldMaxValueIncrementer bookIncrementer) {
		this.tripsIdIncrementer = bookIncrementer;
	}

	public DataFieldMaxValueIncrementer getTripsIdIncrementer() {
		return tripsIdIncrementer;
	}

	@Override
	public Trip delete(Trip entity) throws OperationNotSupportedException {
		return null;
	}

	@Override
	public Trip insert(Trip entity) throws OperationNotSupportedException {
		Long id = tripsIdIncrementer.nextLongValue();
		String tripName = entity.getTripName()+ id;
		entity.setId(new LongPrimaryKey(id));
		String sql = "insert into trips(id,tripname,tripstartdate,overridefuelcalibration,speedlimit,vehicleid,"
				+ "driverid,destination,scheduledtrip,enddate,active,cumulativedistance,idlepointstimelimit) values (?,?,?,?,?,?,?,?,?,?,?,?,?) ";
		Object args[] = new Object[] { id, tripName,entity.getStartDate(), entity.isOverrideFuelCalibration(), entity.getSpeedLimit(),
				entity.getVehicleId(), entity.getDriverId(), entity.getDestination(), entity.isScheduledTrip(),
				entity.getEndDate(), entity.isActiveTrip(), entity.getCumulativeDistance(), entity.getIdlePointsTimeLimit() };
		int types[] = new int[] { Types.BIGINT, Types.VARCHAR, Types.TIMESTAMP, Types.BOOLEAN, Types.REAL, Types.BIGINT, Types.BIGINT,
				Types.VARCHAR, Types.BOOLEAN, Types.TIMESTAMP, Types.BOOLEAN, Types.BIGINT, Types.INTEGER };
		jdbcTemplate.update(sql, args, types);
		jdbcTemplate.execute("NOTIFY trips");
		LOG.debug("sql quey for inserting is "+sql+" and values are "+ id+" , "+ tripName+" , "+ entity.getStartDate()+" , "+ entity.isOverrideFuelCalibration()+" , "+ entity.getSpeedLimit()+" , "+ 
				entity.getVehicleId()+" , "+  entity.getDriverId()+" , "+  entity.getDestination()+" , "+ entity.isScheduledTrip()+" , "+entity.getEndDate()+" , "+ entity.isActiveTrip()+" , "+ entity.getCumulativeDistance()+" , "+  entity.getIdlePointsTimeLimit());
		return entity;
	}

	@Override
	public Trip update(Trip entity) throws OperationNotSupportedException {
		return null;
	}

	public void updatePauseStatus(char active, Long tripId)
			throws OperationNotSupportedException {
		String sql = "update trips set active='" + active + "' where id="+ tripId;
		jdbcTemplate.update(sql);
		jdbcTemplate.execute("NOTIFY update");
	}

	public void updateStartStatus(char active, Long tripId)
			throws OperationNotSupportedException {
		String sql = "update trips set active ='" + active + "' where id =" + tripId + "and enddate is null";
		jdbcTemplate.update(sql);
		jdbcTemplate.execute("NOTIFY update");
	}

	public void updateStopStatus(Long tripId)
			throws OperationNotSupportedException {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		java.util.Date date = new java.util.Date();
		String dateStr = dateFormat.format(date);
		Date date2 = null;
		try {
			date2 = dateFormat.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int year = date2.getYear() + 1900;
		int month = date2.getMonth() + 1;
		int today = date2.getDate();
		String endDate = year + "-" + month + "-" + today + " "+ date2.getHours() + ":" + date2.getMinutes() + ":"+ date2.getSeconds() + " " + date2.getTimezoneOffset();

		String sql = "update trips set enddate='" + endDate+ "',active ='f' where id=" + tripId;
		jdbcTemplate.update(sql);
		jdbcTemplate.execute("NOTIFY update");
	}

	public void updateStopStatus(Long tripId , Date clientTime)
			throws OperationNotSupportedException {

		//adjusting trip's end date to local time before updating in db 
		Calendar cal = Calendar.getInstance();
		Date currDate = cal.getTime();
		Date endDate = DateUtils.adjustToServerTime(currDate, clientTime);

		String sql = "update trips set enddate=?, active ='f' where id=?";
		Object[] args = new Object[]{endDate,tripId};
		int[] types = new int[]{Types.TIMESTAMP,Types.BIGINT};
		jdbcTemplate.update(sql,args,types);
		jdbcTemplate.execute("NOTIFY update");
	}

	public Trip selectTripById(long id) {
		Trip trip = null;
		String sql = "select * from trips where id = " + id;
		List<Trip> trips = jdbcTemplate.query(sql, new TripRowMapper());
		if (trips != null) {
			trips.get(0);
		}
		return trip;
	}

	public Trip selectActiveTripByVehicleImei(String imei) {
		String sql = "select * from trips where vehicleid = (select id from vehicles where imeiid = (select id from hardwaremodules where imei = '"
				+ imei + "')) and active = true";
		List<Trip> trips = jdbcTemplate.query(sql, new TripRowMapper());
		return (trips == null || trips.size() == 0) ? null : trips.get(0);
	}

	@Override
	public List<Trip> selectAll() {
		String sql = "select * from trips";
		return jdbcTemplate.query(sql, new TripRowMapper());
	}

	public List<Trip> selectAllActiveTrips() {
		User user = SessionUtils.getCurrentlyLoggedInUser();
		Long userId = user.getId();
		String sql = "select * from trips where enddate is null and id in (select id from trips where vehicleid in " +
				"(select vehicleid from aclvehicle where userid = "+ userId +")) order by id";
		return jdbcTemplate.query(sql, new TripRowMapper());
	}

	@Override
	public List<Trip> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from trips where id = " + primaryKey.getId();
		return jdbcTemplate.query(sql, new TripRowMapper());
	}

	public List<Trip> getActiveTrips(Long vehicleid) {
		String sql = "select * from trips where vehicleid=? and active='true'";
		Object[] args = new Object[] { vehicleid };
		int[] types = new int[] { Types.BIGINT };
		return jdbcTemplate.query(sql, args, types, new TripRowMapper());
	}

	public List<Trip> selectTripByVehicleId(Long vehicleId) {
		List<Trip> tripList = new ArrayList<Trip>();
		for(Long tripId : LoadTripDetails.getInstance().cachedTrips.keySet()){
			Trip trip = LoadTripDetails.getInstance().retrieve(tripId);
			if(trip.getVehicleId() == vehicleId){
				tripList.add(trip);
			}
		}
		return tripList;
	}

	/**
	 * Get the trip details based on driverid
	 * @param vehicleId
	 * @return
	 * Added By Kiran
	 */
	public List<Trip> selectTripByDriverId(Long driverId) {
		List<Trip> tripList = new ArrayList<Trip>();
		for(Long tripId : LoadTripDetails.getInstance().cachedTrips.keySet()){
			Trip trip = LoadTripDetails.getInstance().retrieve(tripId);
			if(trip.getDriverId() == driverId){
				tripList.add(trip);
			}
		}
		return tripList;
	}
	public List<Trip> selectActiveOrPausedTripByVehicleId(Long vehicleId) {
		String sql = "select * from trips where enddate is null and vehicleid = " + vehicleId;
		return jdbcTemplate.query(sql, new TripRowMapper());
	}

	public List<Vehicle> getVehicledetails(Long vehicleId) {
		String sql = "select * from vehicles where id=?";
		Object[] arg = new Object[]{vehicleId};
		int[] type = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, arg, type, new VehicleRowMapper());
	}

	public List<Driver> getDriverDetails(Long driverId) {
		String sql = "select * from drivers where id=?";
		Object[] arg = new Object[]{driverId};
		int[] type = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, arg, type, new DriverRowMapper());

	}

	public Long getTripByName(String tripname) {
		String sql = "select id from trips where tripname=?";
		Object[] arg = new Object[]{tripname}; 
		int[] type = new int[]{Types.VARCHAR};
		return jdbcTemplate.queryForLong(sql,arg,type);
	}

	public Long getTripidByImeiid(Long imeiid) {
		String sql = "SELECT id from trips where vehicleid=(select id from vehicles where imeiid=?)";
		Object[] arg = new Object[]{imeiid}; 
		int[] type = new int[]{Types.BIGINT};
		return jdbcTemplate.queryForLong(sql,arg,type);
	}

	public List<Trip> getActiveTripsForDriver(Long driverid) {
		String sql = "select * from trips where driverid=? and active='true'";
		Object[] args = new Object[] { driverid };
		int[] types = new int[] { Types.BIGINT };
		return jdbcTemplate.query(sql, args, types, new TripRowMapper());
	}

	public List<Vehicle> selectAssignedVehicle(String tripname) {
		String sql = "select * from vehicles where id in (select vehicleid from trips where tripname="+ tripname + ")";
		return jdbcTemplate.query(sql, new TripRowMapper());
	}

	public void updateCumulativeDistance(Trip currentTrip) {
		String sql = "update trips set cumulativedistance=? where id=?";
		Object[] args = new Object[]{currentTrip.getCumulativeDistance(),currentTrip.getId().getId()};
		int[] types = new int[]{Types.FLOAT,Types.BIGINT};
		jdbcTemplate.update(sql,args,types);
		jdbcTemplate.execute("NOTIFY update");
	}

	public void updateSpeed(Long tripId, float speed) {
		String sql = "update trips set speedlimit=? where id=?";
		Object[] args = new Object[]{speed,tripId};
		int[] types = new int[]{Types.FLOAT,Types.BIGINT};
		jdbcTemplate.update(sql,args,types);
		jdbcTemplate.execute("NOTIFY update");
	}

	public void updateIdleLimit(Long tripId, Integer idle) {
		String sql = "update trips set idlepointstimelimit=? where id=?";
		Object[] args = new Object[]{idle,tripId};
		int[] types = new int[]{Types.FLOAT,Types.BIGINT};
		jdbcTemplate.update(sql,args,types);
		jdbcTemplate.execute("NOTIFY update");
	}

	public List<Trip> selectAllActiveTripsByUserId(long userId) {
		String sql =  "select * from trips t where t.active = true and t.vehicleid in (select a.vehicleid from aclvehicle a where a.userid = "+ userId+")";
		return jdbcTemplate.query(sql, new TripRowMapper());
	}

	public List<Trip> selectAllActiveTripsByUserIdForDashboard(long userId) {
		String sql =  "select * from trips t where t.active = true and t.vehicleid in (select a.vehicleid from aclvehicle a where a.userid = "+ userId+") " +
				"and t.id in ( select tripid from livevehiclestatus where now() - moduleupdatetime < interval '2 min') limit 5";
		return jdbcTemplate.query(sql, new TripRowMapper());
	}

	public List<Trip> selectAllActiveTripsByUserIdForLiveData(long userId) {
		String sql =  "select * from trips t where t.active = true and t.vehicleid in (select a.vehicleid from aclvehicle a where a.userid = "+ userId+") " ;
				//"and t.id in ( select tripid from livevehiclestatus where now() - moduleupdatetime < interval '30 min')";
		return jdbcTemplate.query(sql, new TripRowMapper());
	}


	public List<Trip> selectAllActiveTripsByUserIdForStats(long userId, String vehicleName) {
		String sql ;
		if(vehicleName==null){
			sql =  "select * from trips t where t.active = true and t.vehicleid in (select a.vehicleid from aclvehicle a where a.userid = "+ userId+") order by id limit 15";
		}else{
			sql =  "select * from trips t where t.active = true and t.vehicleid in (select a.vehicleid from aclvehicle a where a.userid = "+ userId+") and  t.id > ( select id from trips where vehicleid in( select id from vehicles where displayname like '"+vehicleName+"' ))order by id limit 15";
		}
		return jdbcTemplate.query(sql, new TripRowMapper());
	}

}
