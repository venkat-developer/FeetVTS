package com.i10n.db.dao;

import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;

import com.i10n.db.entity.StopHistory;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.StopHistoryRowMapper;
import com.i10n.db.idao.IStopHistoryDAO;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.DateUtils;

@SuppressWarnings("unchecked")
public class StopHistoryDaoImp implements IStopHistoryDAO {

	private static Logger LOG = Logger.getLogger(StopHistoryDaoImp.class);
	private JdbcTemplate jdbcTemplate;
	private DataFieldMaxValueIncrementer stopHistoryIdIncrementer;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setStopHistoryIdIncrementer(DataFieldMaxValueIncrementer bookIncrementer) {
		this.stopHistoryIdIncrementer = bookIncrementer;
	}

	public DataFieldMaxValueIncrementer getStopHistoryIdIncrementer() {
		return stopHistoryIdIncrementer;
	}

	@Override
	public StopHistory delete(StopHistory entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StopHistory insert(StopHistory entity)
			throws OperationNotSupportedException {
		Long stopHistoryId = stopHistoryIdIncrementer.nextLongValue();
		entity.setId(new LongPrimaryKey(stopHistoryId));
		String sql = "INSERT INTO stophistory(id, stopid, vehicleid, routeid, routescheduleid, expectedtime, actualtime, estimateddistance,"+ 
				"actualdistance, active, seqno, trackhistoryid)VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Object[] args = new Object[]{entity.getId().getId(),entity.getStopId(),entity.getVehicleId(),entity.getRouteId(),
				entity.getRouteScheduleId(), entity.getExpectedTime(),entity.getActualTime(),entity.getEstimateDistance(),
				entity.getActualDistance(), entity.isActive(),entity.getSeqNo(), entity.getTrackHistoryId()};
		int[] types = new int[]{Types.BIGINT,Types.BIGINT,Types.BIGINT,Types.BIGINT,Types.TIMESTAMP,Types.TIMESTAMP,Types.REAL,
				Types.REAL,Types.BOOLEAN,Types.INTEGER, Types.BIGINT};
		jdbcTemplate.update(sql, args, types);
		return entity;
	}

	public StopHistory insertIntoStopHistory(StopHistory entity){
		LOG.debug("ETAETAETAETAETANODB : Inserting into stophistory table for route : "+entity.getRouteId()+", vehilce : "+
				entity.getVehicleId()+" and stop : "+entity.getStopId());
		Long stopHistoryId = stopHistoryIdIncrementer.nextLongValue();
		entity.setId(new LongPrimaryKey(stopHistoryId));
		String stopHistoryInsertQuery = "INSERT INTO stophistory(id, stopid, vehicleid, routeid, routescheduleid, expectedtime," +
				" actualtime, estimateddistance,actualdistance, active, seqno, trackhistoryid)VALUES ("+stopHistoryId+
				", "+entity.getStopId()+", "+entity.getVehicleId()+", "+entity.getRouteId()+", '"+entity.getRouteScheduleId()+
				"', '"+entity.getExpectedTime()+"', '"+entity.getActualTime()+"', "+entity.getEstimateDistance()+", "+
				entity.getActualDistance()+", "+entity.isActive()+", "+entity.getSeqNo()+", "+entity.getTrackHistoryId()+" )";
		LOG.debug("MBMC : stopHistoryInsertQuery is "+stopHistoryInsertQuery);
		jdbcTemplate.update(stopHistoryInsertQuery);
		return entity;
	}

	@Override
	public List<StopHistory> selectAll() {
		String sql = "select * from stophistory where active = true order by id desc";
		return jdbcTemplate.query(sql, new StopHistoryRowMapper());
	}

	public List<StopHistory> selectAllActiveVehicleStopHistories() {
		String sql = "select * from stophistory where active = true order by routeid asc,vehicleid desc,seqno asc ";
		return jdbcTemplate.query(sql, new StopHistoryRowMapper());
	}


	@Override
	public List<StopHistory> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		String sql = "select * from stophistory where active = true and id = ?";
		Object[] args = new Object[]{primaryKey.getId()};
		int[] types = new int[]{Types.BIGINT,Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new StopHistoryRowMapper());
	}

	@Override
	public StopHistory update(StopHistory entity)
			throws OperationNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isPrevioustopReached(int seq) {
		boolean flag = false; 
		String sql = "select * from stophistory where stopid in(select stopid from stopdetails where seqno=?) and active=true";
		Object[] args = new Object[]{seq};
		int[] types = new int[]{Types.INTEGER};
		List<StopHistory> history = jdbcTemplate.query(sql, args, types, new StopHistoryRowMapper());
		if(history.size() != 0){
			flag = true;
		}
		return flag;
	}

	/**
	 * Check whether the current route is active for the current vehicle.
	 * @param vehicleId
	 * @param routeId
	 * @return
	 */
	public List<StopHistory> getVehicleRouteActive(long vehicleId, long routeId) {
		String sql = "select * from stophistory where vehicleid =? and routeid = ? and active = true order by id";
		Object[] args = new Object[]{vehicleId,routeId};
		int[] types = new int[]{Types.BIGINT,Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new StopHistoryRowMapper());
	}

	public StopHistory getLatestStopHistoryForTheVehicle(Long vehicleId, Long routeId) {
		String sql = "select * from stophistory where vehicleid =? and routeid = ? and active = true order by id desc limit 1";
		Object[] args = new Object[]{vehicleId,routeId};
		int[] types = new int[]{Types.BIGINT,Types.BIGINT};
		List<StopHistory> stopHistories = jdbcTemplate.query(sql, args, types, new StopHistoryRowMapper());
		if(stopHistories != null)
			return stopHistories.get(0);
		return null;
	}

	public void deactivateRoute( long vehicleId, long routeId) {
		LOG.debug("ETAETAETAETAETANODB : Deactivating the Route : "+routeId+", for Vehicle : "+vehicleId+" from table");
		String deactivateRouteQuery = "update stophistory set active = false where vehicleid ="+vehicleId+" and routeid = "+routeId+" and active = true";
		int numberOfRowsAffected = jdbcTemplate.update(deactivateRouteQuery);
		LOG.debug("ETAETAETAETAETANODB : Deactivating the route : "+routeId+", for vehicle : "+vehicleId+" from table with query : "+
				deactivateRouteQuery+" \n Number of Rows affected : "+numberOfRowsAffected);
	}

	public List<StopHistory> getRowForDeviationCheck(long vehicleId, long routeId,long stopId) {
		String sql = "select * from stophistory where vehicleid =? and routeid = ? and stopid = ? and active = true";
		Object[] args = new Object[]{vehicleId,routeId,stopId};
		int[] types = new int[]{Types.BIGINT,Types.BIGINT,Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new StopHistoryRowMapper());
	}

	public List<StopHistory> getActiveRouteForTheVehicle(long vehicleId) {
		String sql = "select * from stophistory where vehicleid =? and active = true order by id";
		Object[] args = new Object[]{vehicleId};
		int[] types = new int[]{Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new StopHistoryRowMapper());
	}

	@SuppressWarnings("deprecation")
	public StopHistory getFirstStopHistoryOfTheRoute(Long routeId, long vehicleId) {
		List<StopHistory> stopHistoryList = new ArrayList<StopHistory>();
		String sql = "select * from stophistory where vehicleid = "+vehicleId+" and routeid = "+routeId
				+" and active = true order by id limit 1";
		LOG.debug("MBMC : getFirstStopHistoryOfTheRoute query : "+sql);
		stopHistoryList = jdbcTemplate.query(sql, new StopHistoryRowMapper());
		if(stopHistoryList != null && stopHistoryList.size() > 0){
			StopHistory stopHistory = stopHistoryList.get(0);
			Timestamp expectedtime = stopHistory.getExpectedTime();
			expectedtime.setSeconds(0);
			stopHistory.setExpectedTime(expectedtime);

			Timestamp actualtime = stopHistory.getActualTime();
			actualtime.setSeconds(0);
			stopHistory.setActualTime(actualtime);
			return stopHistory;
		}
		return null;
	}

	public boolean checkForTripStarted(Long vehicleId, Long routeId, Timestamp expectedTime) {
		List<StopHistory> stopHistories = new ArrayList<StopHistory>();
		String sql = "select * from stophistory where seqno = 1 and vehicleid = "+vehicleId+" and routeid = "
				+routeId+" and expectedtime = '"+DateUtils.convertJavaDateToSQLDate(new Date(expectedTime.getTime()))+"'";
		LOG.debug("checkForTripStarted query : "+sql);
		stopHistories= jdbcTemplate.query(sql, new StopHistoryRowMapper());
		if(stopHistories != null && stopHistories.size() > 0){
			return true;
		}
		return false;
	}

	public List<StopHistory> selectStopHistoriesForVehicleAndRoute(Long vehicleId, Long routeId) {
		String sql = "select * from stophistory where vehicleid = ? and routeid = ? and active = true order by id desc ";
		Object[] args = new Object[]{vehicleId, routeId};
		int[] types = new int[]{Types.BIGINT,Types.BIGINT};
		return jdbcTemplate.query(sql, args, types, new StopHistoryRowMapper());
	}

	public List<StopHistory> selectByVehicleIdAndRouteId(Long vehicleId, Long routeId, String routeScheduleId) {
		String sql = "select * from stophistory where vehicleid = ? and routeid = ? and routescheduleid like ? and active = true limit 1";
		Object[] args = new Object[]{vehicleId, routeId, routeScheduleId};
		int[] types = new int[]{Types.BIGINT,Types.BIGINT, Types.VARCHAR};
		return jdbcTemplate.query(sql, args, types, new StopHistoryRowMapper());
	}


}
