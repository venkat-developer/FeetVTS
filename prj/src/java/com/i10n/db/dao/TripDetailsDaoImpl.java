package com.i10n.db.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.i10n.db.entity.Trip;
import com.i10n.db.entity.TripDetails;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.entity.rowmapper.TripDetailsRowMapper;
import com.i10n.db.idao.ITripDetailsDAO;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

public class TripDetailsDaoImpl implements ITripDetailsDAO{

	@SuppressWarnings("unused")
	private static Logger LOG = Logger.getLogger(TripDetailsDaoImpl.class);

	@SuppressWarnings("unused")
	private JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public List<TripDetails> getActiveTripDetailsWithLiveStatusForTheUser(LongPrimaryKey userId) {
		return getActiveTripDetailsWithLiveStatusForTheUser(userId.getId());
	}
	
	public List<TripDetails> getActiveTripDetailsWithLiveStatusForTheUserForLiveData(LongPrimaryKey userId) {
		return getActiveTripDetailsWithLiveStatusForTheUserForLiveData(userId.getId());
	}


	public List<TripDetails> getActiveTripDetailsWithLiveStatusForTheUserForDashboard(LongPrimaryKey userId) {
		return getActiveTripDetailsWithLiveStatusForTheUserForDashboard(userId.getId());
	}
	
	public List<TripDetails> getActiveTripDetailsWithLiveStatusForTheUser(long userId) {
		List<TripDetails> activeTripDetailsList = new ArrayList<TripDetails>();
		List<Trip> tripList = ((TripDaoImp)DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).
				selectAllActiveTripsByUserId(userId);
		TripDetailsRowMapper rowMapper = new TripDetailsRowMapper();
		activeTripDetailsList =  rowMapper.getTripDetailList(tripList);
		return activeTripDetailsList;
	}

	public List<TripDetails> getActiveTripDetailsWithLiveStatusForTheUserForDashboard(long userId) {
		List<TripDetails> activeTripDetailsList = new ArrayList<TripDetails>();
		List<Trip> tripList = ((TripDaoImp)DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).
				selectAllActiveTripsByUserIdForDashboard(userId);
		TripDetailsRowMapper rowMapper = new TripDetailsRowMapper();
		activeTripDetailsList =  rowMapper.getTripDetailList(tripList);
		return activeTripDetailsList;
	}
	
	public List<TripDetails> getActiveTripDetailsWithLiveStatusForTheUserForLiveData(long userId) {
		List<TripDetails> activeTripDetailsList = new ArrayList<TripDetails>();
		List<Trip> tripList = ((TripDaoImp)DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).
				selectAllActiveTripsByUserIdForLiveData(userId);
		TripDetailsRowMapper rowMapper = new TripDetailsRowMapper();
		activeTripDetailsList =  rowMapper.getTripDetailList(tripList);
		return activeTripDetailsList;
	}

	public List<TripDetails> getActiveTripDetailsWithLiveStatusForTheUserForStats(long userId, String vehicleName) {
		List<TripDetails> activeTripDetailsList = new ArrayList<TripDetails>();
		List<Trip> tripList = ((TripDaoImp)DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).
				selectAllActiveTripsByUserIdForStats(userId,vehicleName);
		TripDetailsRowMapper rowMapper = new TripDetailsRowMapper();
		activeTripDetailsList =  rowMapper.getTripDetailList(tripList);
		return activeTripDetailsList;
	}

	@Override
	public TripDetails delete(TripDetails entity) throws OperationNotSupportedException {
		throw new OperationNotSupportedException("Delete Operation Not Supported! This may be a decorator class.");
	}

	@Override
	public TripDetails insert(TripDetails entity) throws OperationNotSupportedException {
		throw new OperationNotSupportedException("Delete Operation Not Supported! This may be a decorator class.");
	}

	@Override
	public TripDetails update(TripDetails entity) throws OperationNotSupportedException {
		throw new OperationNotSupportedException("Delete Operation Not Supported! This may be a decorator class.");
	}

	@Override
	public List<TripDetails> selectAll() {
		return null;
	}

	@Override
	public List<TripDetails> selectByPrimaryKey(LongPrimaryKey primaryKey) {
		return null;
	}


}
