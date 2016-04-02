package com.i10n.db.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.DriverReport;
import com.i10n.db.entity.TrackHistory;
import com.i10n.db.entity.TripDistancendSpeed;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.DriverReportRowMapper;
import com.i10n.db.entity.rowmapper.TrackHistoryDistanceAndSpeedRowMapper;
import com.i10n.db.entity.rowmapper.TrackHistoryRowMapper;
import com.i10n.db.entity.rowmapper.TripSpeedndDistanceRowMapper;
import com.i10n.db.idao.ITrackHistoryDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.CustomCoordinates;
import com.i10n.fleet.util.EnvironmentInfo;

@SuppressWarnings({"unchecked","deprecation"})

public class TrackHistoryDaoImpl implements ITrackHistoryDAO {

	private static Logger LOG = Logger.getLogger(TrackHistoryDaoImpl.class);

	private JdbcTemplate jdbcTemplate;

	private DataFieldMaxValueIncrementer trackHistoryIdIncrementer;
	
	public static List<TrackHistory> BATCH_INSERTION_ENTITY_LIST = new ArrayList<TrackHistory>();
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public DataFieldMaxValueIncrementer getTrackHistoryIdIncrementer() {
		return trackHistoryIdIncrementer;
	}

	public void setTrackHistoryIdIncrementer(DataFieldMaxValueIncrementer trackHistoryIdIncrementer) {
		this.trackHistoryIdIncrementer = trackHistoryIdIncrementer;
	}

	public TrackHistory selectUniqueFromKey(long id) {
		String sql = "select * from trackhistory where id = " + id;
		return (TrackHistory) jdbcTemplate.query(sql,new TrackHistoryRowMapper()).get(0);
	}

	public boolean deletePreviouslyInsertedPacketsFromCurrentData(LongPrimaryKey id, long sqd, float gsmStrength, float direction, float batteryVoltage) {
		String sql = "delete from trackhistory where id = " + id.getId()+ " and sqd = " + sqd + " and gsmsignal = " + gsmStrength
				+ " and direction = " + direction + " and batteryvoltage = "+ batteryVoltage;
		jdbcTemplate.execute(sql);
		return false;
	}

	@Override
	public TrackHistory delete(TrackHistory entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TrackHistory insert(TrackHistory trackHistory)
			throws OperationNotSupportedException {
		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO trackhistory(id, tripid, gpssignal, gsmsignal, direction, sqd, sqg, batteryvoltage,cd,");
		sql.append(" speed, analogue1, analogue2, error, lac, cid, isping, ismrs,ischargerconnected, isrestart, ");
		sql.append("digital1, digital2, digital3, ispanic, occurredat, location, fuel, distance, pingcounter, gps_fix_information, airdistance, latestbuttonpressed, buttonsequence)");
		sql.append(" VALUES (");
		sql.append(trackHistory.getId().getId());
		sql.append(", ");
		sql.append(trackHistory.getTripid());
		sql.append(", ");
		sql.append(trackHistory.getGpsSignal());
		sql.append(", ");
		sql.append(trackHistory.getGsmSignal());
		sql.append(", ");
		sql.append(trackHistory.getDirection());
		sql.append(", ");
		sql.append(trackHistory.getSqd());
		sql.append(", ");
		sql.append(trackHistory.getSqg());
		sql.append(", ");
		sql.append(trackHistory.getBatteryVoltage());
		sql.append(", ");
		sql.append(trackHistory.getCumulativeDistance());
		sql.append(", ");
		sql.append(trackHistory.getSpeed());
		sql.append(", ");
		sql.append(trackHistory.getAnalog1());
		sql.append(", ");
		sql.append(trackHistory.getAnalog2());
		sql.append(", ");
		sql.append(trackHistory.getError());
		sql.append(", ");
		sql.append(trackHistory.getLac());
		sql.append(", ");
		sql.append(trackHistory.getCid());
		sql.append(", ");
		sql.append(trackHistory.isPing());
		sql.append(", ");
		sql.append(trackHistory.isMrs());
		sql.append(", ");
		sql.append(trackHistory.isChargerConnected());
		sql.append(", ");
		sql.append(trackHistory.isRestart());
		sql.append(", ");
		sql.append(trackHistory.isDigital1());
		sql.append(", ");
		sql.append(trackHistory.isDigital2());
		sql.append(", ");
		sql.append(trackHistory.isDigital3());
		sql.append(", ");
		sql.append(trackHistory.isPanic());
		sql.append(", '");
		sql.append(new Timestamp(trackHistory.getOccurredat().getTime()));
		sql.append("', ");
		sql.append("GeometryFromText('POINT (");
		sql.append(trackHistory.getLocation().getFirstPoint().getX());
		sql.append(" ");
		sql.append(trackHistory.getLocation().getFirstPoint().getY());
		sql.append(")',-1)");
		sql.append(", ");
		sql.append(trackHistory.getFuel());
		sql.append(", ");
		sql.append(trackHistory.getDistance());
		sql.append(", ");
		sql.append(trackHistory.getPingCounter());
		sql.append(", ");
		sql.append(trackHistory.getGpsFixInformation());
		sql.append(", ");
		sql.append(trackHistory.getAirDistance());
		sql.append(", ");
		sql.append(trackHistory.getLatestButtonPressed());
		sql.append(", '");
		sql.append(trackHistory.getButtonSequence());
		sql.append("') ");
		LOG.debug("Trackhistory insert query is "+sql.toString());
		jdbcTemplate.update(sql.toString());
		return trackHistory;
	}
	
	public synchronized TrackHistory processInsertion(TrackHistory entity) throws OperationNotSupportedException{
		if(Boolean.parseBoolean(EnvironmentInfo.getProperty("IS_BATCH_INSERTION_ENABLED"))){
			BATCH_INSERTION_ENTITY_LIST.add(entity);
			if(BATCH_INSERTION_ENTITY_LIST.size() == Integer.parseInt(EnvironmentInfo.getProperty("BATCH_INSERTION_SIZE_LIMIT"))){
				LOG.info("Batch inserting Trackhistory entires when pool size is : "+BATCH_INSERTION_ENTITY_LIST.size());
				insertBatch(BATCH_INSERTION_ENTITY_LIST);
				BATCH_INSERTION_ENTITY_LIST.clear();
			} else {
				LOG.debug("Batch insertion : Size < "+Integer.parseInt(EnvironmentInfo.getProperty("BATCH_INSERTION_SIZE_LIMIT"))+" : "+BATCH_INSERTION_ENTITY_LIST.size());
			}
			return entity;
		} else {
			LOG.debug("Batch insertion disabled");
			return insert(entity);
		}
		
		
	}
	
	public void updateAllOnServerShutDown(){
		LOG.info("SHUTDOWN : Batch updating all on server shutdown. Batch size : "+BATCH_INSERTION_ENTITY_LIST.size());
		insertBatch(BATCH_INSERTION_ENTITY_LIST);
		BATCH_INSERTION_ENTITY_LIST.clear();
	}
	
	/**
	 * Batch inserting trackhistory values
	 * @param trackHistoryList
	 */
	public void insertBatch(final List<TrackHistory> trackHistoryList){

		StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO trackhistory(id, tripid, gpssignal, gsmsignal, direction, sqd, sqg, batteryvoltage,cd,");
		sql.append(" speed, analogue1, analogue2, error, lac, cid, isping, ismrs,ischargerconnected, isrestart, ");
		sql.append("digital1, digital2, digital3, ispanic, occurredat, location, fuel, distance, pingcounter, gps_fix_information, airdistance, latestbuttonpressed, buttonsequence)");
		sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ");
		sql.append("GeometryFromText(?, -1) ");
		sql.append(", ?, ?, ?, ?, ?, ?, ?)");
		LOG.debug("Trackhistory insert query is "+sql.toString());

		jdbcTemplate.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				TrackHistory trackHistory = trackHistoryList.get(i);
				ps.setLong(1, trackHistory.getId().getId());
				ps.setLong(2, trackHistory.getTripid());
				ps.setFloat(3, trackHistory.getGpsSignal());
				ps.setFloat(4, trackHistory.getGsmSignal());
				ps.setFloat(5, trackHistory.getDirection());
				ps.setLong(6, trackHistory.getSqd());
				ps.setLong(7, trackHistory.getSqg());
				ps.setFloat(8, trackHistory.getBatteryVoltage());
				ps.setFloat(9, trackHistory.getCumulativeDistance());
				ps.setFloat(10, trackHistory.getSpeed());
				ps.setInt(11, trackHistory.getAnalog1());
				ps.setInt(12, trackHistory.getAnalog2());
				ps.setInt(13, trackHistory.getError());
				ps.setLong(14, trackHistory.getLac());
				ps.setLong(15, trackHistory.getCid());
				ps.setBoolean(16, trackHistory.isPing());
				ps.setBoolean(17, trackHistory.isMrs());
				ps.setBoolean(18, trackHistory.isChargerConnected());
				ps.setBoolean(19, trackHistory.isRestart());
				ps.setBoolean(20, trackHistory.isDigital1());
				ps.setBoolean(21, trackHistory.isDigital2());
				ps.setBoolean(22, trackHistory.isDigital3());
				ps.setBoolean(23, trackHistory.isPanic());
				ps.setTimestamp(24, new Timestamp(trackHistory.getOccurredat().getTime()));
				PGgeometry point = new PGgeometry(new Point(trackHistory.getLocation().getFirstPoint().getX(), trackHistory.getLocation().getFirstPoint().getY()));
				ps.setObject(25, point, Types.OTHER);
				ps.setFloat(26, trackHistory.getFuel());
				ps.setFloat(27, trackHistory.getDistance());
				ps.setLong(28, trackHistory.getPingCounter());
				ps.setInt(29, trackHistory.getGpsFixInformation());
				ps.setFloat(30, trackHistory.getAirDistance());
				ps.setInt(31, trackHistory.getLatestButtonPressed());
				ps.setString(32, trackHistory.getButtonSequence());
			}

			@Override
			public int getBatchSize() {
				return trackHistoryList.size();
			}
		});
	}

	public List<TrackHistory> selectByNextSqd(long tripId, long sqd) {
		String sql = null;
		if (sqd != 0) {
			sql = "select * from trackhistory where id = " + tripId + " and sqd < " + sqd + " order by occurredat desc limit 1";
		} else {
			sql = "select * from trackhistory where id = " + tripId + " and sqd > 1 order by occurredat desc limit 1";
		}
		return jdbcTemplate.query(sql, new TrackHistoryRowMapper());
	}

	public List<TrackHistory> selectTrackHistoryOrderedByTime(long tripid) {
		String sql = "select * from trackhistory where id = ? order by occurredat desc limit 2";
		Object[] args = { tripid };
		int[] types = { Types.BIGINT };
		return jdbcTemplate.query(sql, args, types, new TrackHistoryRowMapper());
	}

	@Override
	public List<TrackHistory> selectAll() {
		String sql = "select * from trackhistory";
		return jdbcTemplate.query(sql, new TrackHistoryRowMapper());
	}

	public List<DriverReport> getAvgAndMaxSpeedAndCumulativeDistanceForVehicle(Long vehicleId, Date startDate, Date endDate) {
		String sql = "select max(speed),avg(speed),sum(distance) from trackhistory where trackhistory.tripid in"
				+ " (select id from trips where trips.vehicleid=?) and occurredat>? and occurredat <?";
		Object[] args = { vehicleId, startDate, endDate };
		int[] types = { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		List<DriverReport> result = jdbcTemplate.query(sql, args, types, new DriverReportRowMapper());
		return result;
	}
	
	public List<DriverReport> getAvgAndMaxSpeedAndCumulativeDistanceForDriver(Long driverId, Date startDate, Date endDate) {
		String sql = "select max(speed),avg(speed),sum(distance) from trackhistory where trackhistory.tripid in"
				+ " (select id from trips where trips.driverid=?) and occurredat>? and occurredat <?";
		Object[] args = { driverId, startDate, endDate };
		int[] types = { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		List<DriverReport> result = jdbcTemplate.query(sql, args, types, new DriverReportRowMapper());
		return result;
	}

	public List<DriverReport> getAvgSpeed(long vehicleId) {
		String sql = "select max(speed), avg(speed), sum(distance) from trackhistory where trackhistory.tripid in (select id from trips where trips.vehicleid=?)";
		Object[] args = { vehicleId };
		int[] types = { Types.BIGINT };
		List<DriverReport> result = jdbcTemplate.query(sql, args, types, new DriverReportRowMapper());
		return result;
	}

	@Override
	public List<TrackHistory> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from trackhistory where id = " + primaryKey.getId();
		return jdbcTemplate.query(sql, new TrackHistoryRowMapper());
	}

	@Override
	public TrackHistory update(TrackHistory tHistory)
			throws OperationNotSupportedException {
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE trackhistory ");
		sql.append("SET tripid=");
		sql.append(tHistory.getTripid());
		sql.append(", gpssignal=");
		sql.append(tHistory.getGpsSignal());
		sql.append(", gsmsignal=");
		sql.append(tHistory.getGsmSignal());
		sql.append(", direction=");
		sql.append(tHistory.getDirection());
		sql.append(", sqd=");
		sql.append(tHistory.getSqd());
		sql.append(",sqg=");
		sql.append(tHistory.getSqg());
		sql.append(", batteryvoltage=");
		sql.append(tHistory.getBatteryVoltage());
		sql.append(", cd=");
		sql.append(tHistory.getCumulativeDistance());
		sql.append(", speed=");
		sql.append(tHistory.getSpeed());
		sql.append(", analogue1=");
		sql.append(tHistory.getAnalog1());
		sql.append(", analogue2=");
		sql.append(tHistory.getAnalog2());
		sql.append(", error=");
		sql.append(tHistory.getError());
		sql.append(", lac=");
		sql.append(tHistory.getLac());
		sql.append(", cid=");
		sql.append(tHistory.getCid());
		sql.append(", isping=");
		sql.append(tHistory.isPing());
		sql.append(", ismrs=");
		sql.append(tHistory.isMrs());
		sql.append(", ischargerconnected=");
		sql.append(tHistory.isChargerConnected());
		sql.append(", isrestart=");
		sql.append(tHistory.isRestart());
		sql.append(", digital1=");
		sql.append(tHistory.isDigital1());
		sql.append(", digital2=");
		sql.append(tHistory.isDigital2());
		sql.append(", digital3=");
		sql.append(tHistory.isDigital3());
		sql.append(", ispanic=");
		sql.append(tHistory.isPanic());
		sql.append(", occurredat='");
		sql.append(new Timestamp(tHistory.getOccurredat().getTime()));
		sql.append("', location=");
		sql.append("GeometryFromText('POINT (");
		sql.append(tHistory.getLocation().getFirstPoint().getX() + " ");
		sql.append(tHistory.getLocation().getFirstPoint().getY() + ")',-1)");
		sql.append(", fuel=");
		sql.append(tHistory.getFuel());
		sql.append(", distance=");
		sql.append(tHistory.getDistance());
		sql.append(", pingcounter = ");
		sql.append(tHistory.getPingCounter());
		sql.append(", gps_fix_information = ");
		sql.append(tHistory.getGpsFixInformation());
		sql.append(", latestbuttonpressed = ");
		sql.append(tHistory.getLatestButtonPressed());
		sql.append(", buttonsequence = '");
		sql.append(tHistory.getButtonSequence());
		sql.append("' WHERE id = ");
		sql.append(tHistory.getId().getId());
		LOG.debug("Trackhistory update query is "+sql.toString());
		jdbcTemplate.update(sql.toString());
		return tHistory;
	}

	public long getCumulativeDistance(Long tripId) {
		String sql = "select sum(distance) from trackhistory where tripid=" + tripId;
		return jdbcTemplate.queryForLong(sql);
	}

	public float getCumulativeDistanceBetweenStops(Long tripId, long trackHistoryId) {
		String sqlQueryToCummulativeDistanceBetweenStops =  "select sum(distance),max(speed) from trackhistory where tripid = "+tripId
				+" and id > "+trackHistoryId;
		LOG.debug("ETAETAETAETAETANODB : Getting cumulative distance between stops : Query : "+sqlQueryToCummulativeDistanceBetweenStops);
		List<TrackHistory> trackHistories =  jdbcTemplate.query(sqlQueryToCummulativeDistanceBetweenStops, new TrackHistoryDistanceAndSpeedRowMapper());
		if(trackHistories != null && trackHistories.size() > 0 ){
			return trackHistories.get(0).getDistance();
		}
		return 0;
	}

	public long getMaxSpeedBetweenDates(Long tripId, Date startDate, Date endDate) {
		String sql = "select max(speed) from trackhistory where tripid=? and occurredat>=? and occurredat <=?";
		Object[] arg = new Object[] { tripId, startDate, endDate };
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		return jdbcTemplate.queryForLong(sql, arg, type);
	}

	public List<TrackHistory> getCumulativeDistanceAndMaxSpeedBetweenDates(long vehicleId, Date startDate, Date endDate) {
		String sql = "select sum(distance),max(speed) from trackhistory where tripid in (select id from trips where vehicleid = ? )"+
				" and occurredat >= ? and occurredat <= ?";
		Object[] arg = new Object[] { vehicleId, startDate, endDate };
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		return jdbcTemplate.query(sql, arg, type, new TrackHistoryDistanceAndSpeedRowMapper());
	}

	/**
	 * 
	 * select the max  speed avg speed and cumulative distance between the interval given based on the driver selected
	 * @param driverId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<TrackHistory> getCumulativeDistanceAndMaxSpeedBetweenDatesByDriverId(long driverId, Date startDate, Date endDate) {
		String sql = "select sum(distance),max(speed) from trackhistory where tripid in (select id from trips where driverid = ? "
				+"   and active='t' ) and occurredat >= ? and occurredat <= ? ";
		Object[] arg = new Object[] { driverId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())};
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		return jdbcTemplate.query(sql, arg, type, new TrackHistoryDistanceAndSpeedRowMapper());
	}

	public long getMaxSpeed(Long tripId) {
		String sql = "select max(speed) from trackhistory where tripid=?";
		Object[] args = new Object[] { tripId };
		int[] types = new int[] { Types.BIGINT };
		return jdbcTemplate.queryForLong(sql, args, types);
	}

	public long getAvgSpeedBetweenDates(Long tripId, String startDate,String endDate) {
		String sql = "select avg(speed) from trackhistory where tripid=? and occurredat>=? and occurredat <=?";
		Object[] arg = new Object[] { tripId, new Timestamp(new Date(startDate).getTime()), new Timestamp(new Date(endDate).getTime()) };
		int[] types = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		return jdbcTemplate.queryForLong(sql, arg, types);
	}

	public long getAvgSpeedForTheVehicleForLast15minutes(Long vehicleId) {
		Calendar cal = Calendar.getInstance();
		Date currentTime = cal.getTime();
		cal.add(Calendar.MINUTE, -15);
		Date timeBefore15Mins = cal.getTime();

		String sql = "select avg(speed) from trackhistory where tripid in(select id from trips where trips.vehicleid= ?)"
				+ " and occurredat < ? and occurredat > ?";
		Object[] arg = new Object[] { vehicleId, new Timestamp(currentTime.getTime()), new Timestamp(timeBefore15Mins.getTime()) };
		int[] types = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		return jdbcTemplate.queryForLong(sql, arg, types);
	}

	/**
	 * Get Avg speed for last ten trackHistory entries.
	 * 
	 * @param vehicleId
	 * @param statement 
	 * @return
	 */
	public long getAvgSpeedForTheVehicleFromLast10Entries(long vehicleId) {
		String sql = "select * from trackhistory where tripid in(select id from trips where trips.vehicleid = ?) order by id desc limit 10";
		Object[] arg = new Object[] { vehicleId };
		int[] type = new int[] { Types.BIGINT };
		List<TrackHistory> result = jdbcTemplate.query(sql, arg, type, new TrackHistoryRowMapper());
		long avgSpeed = 0L;
		if(result != null){
			for(TrackHistory trackHistory : result){
				avgSpeed += trackHistory.getSpeed();
			}
		}
		return avgSpeed;
	}

	public List<TrackHistory> getVehicleSpeedList(Long vehicleId, Date startDate, Date endDate) {
		String sql = "select * from trackhistory where trackhistory.tripid in(select id from trips where trips.vehicleid=?)"
				+ " and occurredat>? and occurredat <? order by occurredat";
		Object[] arg = new Object[] { vehicleId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())};
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		List<TrackHistory> result = jdbcTemplate.query(sql, arg, type, new TrackHistoryRowMapper());
		return result;
	}

	public List<TrackHistory> getDriverSpeedList(Long driverId, Date startDate, Date endDate) {
		String sql = "select * from trackhistory where trackhistory.tripid in(select id from trips where trips.driverid=?) and occurredat>=? and occurredat <=?";
		Object[] arg = new Object[] { driverId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())};
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		List<TrackHistory> result = jdbcTemplate.query(sql, arg, type, new TrackHistoryRowMapper());
		return result;
	}

	public List<TrackHistory> selectLastEntryForVehicleBetweenDates(Long vehicleId, Date startDate, Date endDate) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from trackhistory where trackhistory.tripid in(select id from trips where trips.vehicleid=?)");
		sql.append(" and occurredat>? and occurredat <? order by occurredat desc limit 1 ;");
		Object[] arg = new Object[] { vehicleId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())};
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		List<TrackHistory> result = jdbcTemplate.query(sql.toString(), arg, type, new TrackHistoryRowMapper());
		return result;
	}

	public List<TrackHistory> getTrackForVehicle(Long vehicleId) {
		String sql = "select * from trackhistory where trackhistory.tripid in(select id from trips where trips.vehicleid=?)";
		Object[] args = new Object[] { vehicleId };
		int[] types = new int[] { Types.BIGINT };
		List<TrackHistory> result = jdbcTemplate.query(sql, args, types, new TrackHistoryRowMapper());
		return result;
	}

	public List<TrackHistory> selectAllBetweenDates(Long tripId, Date startDate, Date endDate) {
		String sql = "select * from trackhistory where tripid=? and occurredat>? and occurredat<?";
		Object[] arg = new Object[] { tripId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())};
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		List<TrackHistory> result = jdbcTemplate.query(sql, arg, type, new TrackHistoryRowMapper());
		return result;
	}

	public List<TrackHistory> getInitialfuel(Long vid, Date startDate, Date endDate) {
		String sql = "select * from trackhistory where trackhistory.tripid in (select id from trips where trips.vehicleid=?) and occurredat>? and occurredat<? order by occurredat asc limit 1";
		Object[] arg = new Object[] { vid, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())};
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		List<TrackHistory> result = jdbcTemplate.query(sql, arg, type, new TrackHistoryRowMapper());
		return result;
	}

	public List<TrackHistory> getFuel(Long vid, Date startDate, Date endDate) {
		String sql = "select * from trackhistory where trackhistory.tripid in (select id from trips where trips.vehicleid=?) and occurredat>=? and occurredat<=?";
		Object[] arg = new Object[] { vid, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())};
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		return jdbcTemplate.query(sql, arg, type, new TrackHistoryRowMapper());
	}

	public List<TrackHistory> getVehicleStartLocation(Long vehicleId, Date startDate, Date endDate) {
		String sql = "select * from trackhistory where trackhistory.tripid in(select id from trips where trips.vehicleid=?) and occurredat> ?"
				+ " and occurredat < ? order by occurredat limit 1";
		Object[] arg = new Object[] { vehicleId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())};
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		List<TrackHistory> result = jdbcTemplate.query(sql, arg, type, new TrackHistoryRowMapper());
		return result;
	}

	public List<TrackHistory> getLatestVehicleLocation(long vehicleId) {
		String sql = "select * from trackhistory where trackhistory.tripid in(select id from trips where trips.vehicleid=?) order by occurredat desc limit 1";
		Object[] arg = new Object[] { vehicleId };
		int[] type = new int[] { Types.BIGINT };
		List<TrackHistory> result = jdbcTemplate.query(sql, arg, type, new TrackHistoryRowMapper());
		return result;
	}

	public List<TrackHistory> getTrackHistoryForVehicle(Long vehicleId) {
		String sql = "select * from trackhistory where trackhistory.tripid in(select id from trips where trips.vehicleid=?) order by occurredat desc limit 1";
		Object[] args = new Object[] { vehicleId };
		int[] types = new int[] { Types.BIGINT };
		List<TrackHistory> result = jdbcTemplate.query(sql, args, types, new TrackHistoryRowMapper());
		return result;
	}

	public List<TrackHistory> getVehicleFuelList(Long vehicleId, Date startDate, Date endDate) {
		String sql = "select * from trackhistory where trackhistory.tripid in(select id from trips where trips.vehicleid=?) and occurredat>? and occurredat <? and fuel <> 0 order by occurredat";
		Object[] arg = new Object[] { vehicleId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())};
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		List<TrackHistory> result = jdbcTemplate.query(sql, arg, type, new TrackHistoryRowMapper());
		return result;
	}

	public List<TrackHistory> selectBetweenDates(long vehicleId, Date startDate, Date endDate) {
		StringBuffer sql = new StringBuffer();
		sql.append("select *  from trackhistory where tripid in(select id from trips where vehicleid=?) and occurredat>=?");
		sql.append(" and occurredat<=? order by occurredat  ;");
		Object[] arg = new Object[] { vehicleId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())};
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		List<TrackHistory> result = jdbcTemplate.query(sql.toString(), arg, type, new TrackHistoryRowMapper());
		if(result == null){
			return new ArrayList<TrackHistory>();
		}
		return result;
	}
	
	/**
	 * fetch first 15 trackhistory records between the given dates when the  interval is zero 
	 * @param vehicleId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<TrackHistory> selectBetweenDatesIntervalNotZero(long vehicleId, Date startDate, Date endDate,int interval) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from trackhistory t where t.tripid in(select id from trips where vehicleid=?) and occurredat>=?");
		sql.append(" and occurredat<=? ");
		sql.append("and MOD(extract(epoch from (t.occurredat::timestamp - ? ::timestamp))::int,?)=0 order by occurredat limit 15");
		Object[] arg = new Object[] { vehicleId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime()),
				new Timestamp(startDate.getTime()),interval};
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP,Types.TIMESTAMP ,Types.BIGINT};
		List<TrackHistory> result = jdbcTemplate.query(sql.toString(), arg, type, new TrackHistoryRowMapper());
		if(result == null){
			return new ArrayList<TrackHistory>();
		}
		return result;
	}
	
	
	/**
	 * fetch all trackhistory records between the given dates when the  interval is zero 
	 * @param vehicleId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<TrackHistory> selectBetweenDatesIntervalNotZeroWithNoLimit(long vehicleId, Date startDate, Date endDate,int interval) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from trackhistory t where t.tripid in(select id from trips where vehicleid=?) and occurredat>=?");
		sql.append(" and occurredat<=? ");
		sql.append("and MOD(extract(epoch from (t.occurredat::timestamp - ? ::timestamp))::int,?)=0 order by occurredat");
		Object[] arg = new Object[] { vehicleId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime()),
				new Timestamp(startDate.getTime()),interval};
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP,Types.TIMESTAMP ,Types.BIGINT};
		List<TrackHistory> result = jdbcTemplate.query(sql.toString(), arg, type, new TrackHistoryRowMapper());
		if(result == null){
			return new ArrayList<TrackHistory>();
		}
		return result;
	}
	
	/**
	 * Fetch first 15 trackhistory between the provided start end dates with 0 interval
	 * @param vehicleId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<TrackHistory> selectBetweenDatesFroZeroInterval(long vehicleId, Date startDate, Date endDate) {
		StringBuffer sql = new StringBuffer();
		sql.append("select *  from trackhistory where tripid in(select id from trips where vehicleid=?) and occurredat>=?");
		sql.append(" and occurredat<=? order by occurredat limit 15 ;");
		Object[] arg = new Object[] { vehicleId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())};
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		List<TrackHistory> result = jdbcTemplate.query(sql.toString(), arg, type, new TrackHistoryRowMapper());
		if(result == null){
			return new ArrayList<TrackHistory>();
		}
		return result;
	}

	/**
	 * 
	 * To get the trackhistory details based on selected driver id in driver reports 
	 * @param driverId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<TrackHistory> selectBetweenDatesByDriverId(long driverId, Date startDate, Date endDate) {
		StringBuffer sql = new StringBuffer();
		sql.append("select *  from trackhistory where tripid in(select id from trips where driverid=?) and occurredat>=?");
		sql.append(" and occurredat<=? order by occurredat ;");
		Object[] arg = new Object[] { driverId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())};
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		List<TrackHistory> result = jdbcTemplate.query(sql.toString(), arg, type, new TrackHistoryRowMapper());
		return result;
	}


	public List<TrackHistory> getStartDetails(long vehicleId, Date startDate, Date endDate) {
		String sql = "SELECT * FROM trackhistory where tripid in (select id from trips where vehicleid=?) and id in(select min(id) from trackhistory group by tripid) and  occurredat>=? and occurredat<=?";
		Object[] arg = new Object[] { vehicleId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())};
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		List<TrackHistory> result = jdbcTemplate.query(sql, arg, type, new TrackHistoryRowMapper());
		return result;
	}

	public List<TrackHistory> getEndDetails(long vehicleId, Date startDate, Date endDate) {
		String sql = "SELECT * FROM trackhistory where tripid in (select id from trips where vehicleid=?) and id in(select max(id) from trackhistory group by tripid) and  occurredat>=? and occurredat<=?";
		Object[] arg = new Object[] { vehicleId, new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime())};
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		List<TrackHistory> result = jdbcTemplate.query(sql, arg, type, new TrackHistoryRowMapper());
		return result;
	}

	public List<TripDistancendSpeed> getTripSpeedandDistance(long vehicleId, Date startDate, Date endDate) {
		String sql = "SELECT max(speed),sum(distance) FROM trackhistory group by tripid having tripid in (select id from trips where vehicleid=?)";
		Object[] arg = new Object[] { vehicleId };
		int[] type = new int[] { Types.BIGINT };
		List<TripDistancendSpeed> result = jdbcTemplate.query(sql, arg, type, new TripSpeedndDistanceRowMapper());
		return result;
	}

	/**
	 * Filter on trackHistory entries between the times.
	 * 
	 * @param vehicleId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<TrackHistory> selectTrackHistoryEntriesForThisLocationForAvgSpeedCalculation(long vehicleId, Date startTime, Date endTime) {
		String sql = "select * from trackhistory where tripid in(select id from trips where trips.vehicleid=?) "
				+ "and occurredat>? and occurredat<?";
		Object[] arg = new Object[] { vehicleId, startTime, endTime };
		int[] type = new int[] { Types.BIGINT, Types.TIMESTAMP, Types.TIMESTAMP };
		return jdbcTemplate.query(sql, arg, type, new TrackHistoryRowMapper());
	}

	public TrackHistory fetchNearestLocatedPacketForTheCurrentLocation(Long tripId, double currentLatitude, double currentLongitude,
			long startTrackHistoryId, long endTrackHistoryId) {
		long distance = 500;
		TrackHistory trackHistoryEntity = null;
		for (int i = 0; i < 3; i++) {
			String fetchNearestLocatedPacketForTheCurrentLocationQuery =  "SELECT * FROM trackhistory WHERE st_dwithin(transform(setsrid(location,4326), 2163),"
					+ " transform(GeomFromText('POINT(" + currentLongitude + " " + currentLatitude
					+ ")',4326),2163),"+distance+") and id >=  "+startTrackHistoryId
					+ " and id <= "+endTrackHistoryId+"  and tripid = "+tripId
					+ " order by st_distance(transform(setsrid(location,4326),26986), transform(GeomFromText('POINT("
					+ currentLongitude + " " + currentLatitude + ")',4326),26986)) " + " limit 1";

			LOG.debug("ETAETAETAETAETANODB : fetchNearestLocatedPacketForTheCurrentLocationQuery : "+fetchNearestLocatedPacketForTheCurrentLocationQuery);

			List<TrackHistory> trackList = jdbcTemplate.query(fetchNearestLocatedPacketForTheCurrentLocationQuery, new TrackHistoryRowMapper());

			if(trackList != null && trackList.size() > 0){
				trackHistoryEntity = trackList.get(0);
				LOG.debug("ETAETAETAETAETANODB : Selected trackhistory entity for current location  : "+trackHistoryEntity.getId().getId()+" trip : "+tripId);
			}
			if(trackHistoryEntity != null){
				break;
			}else{
				distance += 500;
			}
		}
		return trackHistoryEntity;
	}

	public TrackHistory fetchNearestLocatedPacketForTheStop(Long tripId, double latPoint, double lonPoint, 
			long startTrackHistoryId, long endTrackHistoryId) {
		TrackHistory trackHistoryEntity = null;
		String fetchNearestLocatedPacketForTheStopQuery =  "SELECT * FROM trackhistory WHERE st_dwithin(transform(setsrid(location,4326), 2163),"
				+ " transform(GeomFromText('POINT(" + lonPoint+ " "+ latPoint
				+ ")',4326),2163),500) and id >=  "+startTrackHistoryId
				+ " and id <= "+endTrackHistoryId+"  and tripid = "+tripId
				+ " order by st_distance(transform(setsrid(location,4326),26986), transform(GeomFromText('POINT("
				+ lonPoint + " " + latPoint + ")',4326),26986)) " + " limit 1";

		LOG.debug("ETAETAETAETAETANODB : fetchNearestLocatedPacketForTheStopQuery : "+fetchNearestLocatedPacketForTheStopQuery);

		List<TrackHistory> trackList = jdbcTemplate.query(fetchNearestLocatedPacketForTheStopQuery, new TrackHistoryRowMapper());
		if(trackList != null && trackList.size() > 0){
			trackHistoryEntity = trackList.get(0);
			LOG.debug("ETAETAETAETAETANODB : Selected trackhistory entity for stop location  : "+trackHistoryEntity.getId().getId()+" trip : "+tripId);
		}
		return trackHistoryEntity;
	}

	/**
	 * AirDistance is calculated between every data packets and cumulative value is returned 
	 * @param vehicleId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public float getCumulativeDistanceForVehicle(Long vehicleId, Date startDate, Date endDate) {
		List<TrackHistory> trackEntries = selectBetweenDates(vehicleId, startDate, endDate);
		float distance = getDistance(trackEntries);
		return distance;
	}
	
	/**
	 * AirDistance is calculated between every data packets and cumulative value is returned 
	 * @param driverId
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public float getCumulativeDistanceForDriver(Long driverId, Date startDate, Date endDate) {
		List<TrackHistory> trackEntries = selectBetweenDatesByDriverId(driverId, startDate, endDate);
		float distance = getDistance(trackEntries);
		return distance;
	}

	private float getDistance(List<TrackHistory> trackEntries) {
		float distance = 0;
		TrackHistory prevTrackHistory = null;
		for(TrackHistory trackEntry : trackEntries){
			if(prevTrackHistory != null){
				float airDistance = (float) CustomCoordinates.distance(prevTrackHistory.getLocation().getFirstPoint().getY(), prevTrackHistory.getLocation().getFirstPoint().getX(),
						trackEntry.getLocation().getFirstPoint().getY(), trackEntry.getLocation().getFirstPoint().getX());
				distance += (airDistance + (0.1 * airDistance));
			}
			prevTrackHistory = trackEntry;
		}
		return distance;
	}

}
