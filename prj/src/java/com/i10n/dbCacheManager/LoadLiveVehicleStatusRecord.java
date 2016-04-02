package com.i10n.dbCacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.LiveVehicleStatusDaoImp;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.tools.DBManager;

/**
 * @author HEMANT
 * @Update Vishnu, Dharmaraju V
 *
 */
public class LoadLiveVehicleStatusRecord {
	private static final Logger LOG = Logger.getLogger(LoadLiveVehicleStatusRecord.class);

	static private LoadLiveVehicleStatusRecord _instance = null;
	private LoadLiveVehicleStatusRecord(){};

	public static LoadLiveVehicleStatusRecord getInstance() {
		if(null == _instance){
			_instance = new LoadLiveVehicleStatusRecord();
			_instance.loadDataFromLiveVehicles();
		}
		return _instance;
	}


	/*
	 * The HashMap is in the form of IMEI and the values which we need are storing in ArrayList Object... 
	 * In Object we are storing in TripId, cummulative
	 */
	public static ConcurrentHashMap<String, LiveVehicleStatus> cacheLiveVehicleStatus = new ConcurrentHashMap<String, LiveVehicleStatus>();

	/**
	 * Retrieves live vehicle status corresponding to the imei
	 * @param imei
	 * @return
	 */
	public LiveVehicleStatus retrieve (String imei) {
		// This may return null
		if (imei == null) {
			return null;
		}
		//		LiveVehicleStatus lvso = cacheLiveVehicleStatus.get(imei);
		//		if (lvso == null) {
		retrieveIMEIDataFromDBIfNotInMemory(imei);
		return cacheLiveVehicleStatus.get(imei);
		//		}
		//		return lvso;	
	}

	/**
	 * Retrieves live vehicle status corresponding to the userId
	 * @param imei
	 * @return
	 */
	public  LiveVehicleStatus retrieveByVehicleId (Long vehicleId) {
		retrieveVehicleDataFromDBIfNotInMemory(vehicleId);
		for (String imei : cacheLiveVehicleStatus.keySet()) {
			LiveVehicleStatus lvs = cacheLiveVehicleStatus.get(imei);
			if (lvs.getVehicleId() == vehicleId) {
				return lvs;
			}	
		}
		return null;	
	}


	/**
	 * Retrieves live vehicle status corresponding to the driverID
	 * @param imei
	 * @return
	 */
	public LiveVehicleStatus retrieveByDriverId (long driverId) {
		retrieveDriverDataFromDBIfNotInMemory(driverId);
		for (String imei : cacheLiveVehicleStatus.keySet()) {
			LiveVehicleStatus lvs = cacheLiveVehicleStatus.get(imei);
			if (lvs.getDriverId() == driverId) {
				return lvs;
			}	
		}
		return null;
	}


	/**
	 * Retrieves live vehicle status corresponding to the driverID
	 * @param imei
	 * @return
	 */
	public LiveVehicleStatus retrieveByTripId (long tripId) {
		retrieveTripDataFromDBIfNotInMemory(tripId);
		for (String imei : cacheLiveVehicleStatus.keySet()) {
			LiveVehicleStatus lvs = cacheLiveVehicleStatus.get(imei);
			if (lvs.getTripId().getId() == tripId) {
				return lvs;
			}	
		}
		return null;
	}


	/**
	 * Retrieve All
	 * @return
	 */
	public List<LiveVehicleStatus> retrieveAll() {
		loadDataFromLiveVehicles();
		return new ArrayList <LiveVehicleStatus> (cacheLiveVehicleStatus.values());
	}

	/**
	 * Store live vehicle status corresponding to the imei
	 * @param imei
	 * @return
	 */
	public static void store (String imei, LiveVehicleStatus lvos) {
		if(lvos==null){
			LOG.debug("Trip Is Not Assigned to vehicle");
		}
		else{
			cacheLiveVehicleStatus.put(imei, lvos);
		}

	}


	/**
	 * Store live vehicle status corresponding to the imei
	 * @return
	 */
	public void refresh() {
		clearCache();
		loadDataFromLiveVehicles();
	}

	private void clearCache() {
		cacheLiveVehicleStatus.clear();
	}

	private void loadDataFromLiveVehicles(){
		LOG.debug("Caching Livevehicle values.");
		List<LiveVehicleStatus> liveVehicleStatusList = ((LiveVehicleStatusDaoImp)DBManager.getInstance().getDao(DAOEnum.LIVE_VEHICLE_STATUS_DAO)).
				selectAllForCache();
		processCacheUpdate(liveVehicleStatusList);
		LOG.debug("Caching Livevehicle values successful");

	}
	private void processCacheUpdate(List<LiveVehicleStatus> liveVehicleStatusList) {
		if(liveVehicleStatusList != null){
			for(LiveVehicleStatus liveVehicleStatus : liveVehicleStatusList){
				cacheLiveVehicleStatus.put(liveVehicleStatus.getImei(), liveVehicleStatus);
			}
		}
	}

	public boolean addLiveObjectToCache(LiveVehicleStatus mLiveObject,String imei){
		boolean flag=false;
		if(cacheLiveVehicleStatus.containsKey(imei)){
			//LOG.debug("The Imei already added in the Cache so No need to add it again ::::::::::::::");
			return flag;
		}else{
			//LiveVehicleStatus temp=cacheLiveVehicleStatus.put(imei, mLiveObject);
			retrieveIMEIDataFromDBIfNotInMemory(imei);
		}

		return flag ;
	}

	public void retrieveIMEIDataFromDBIfNotInMemory(String imei){
		List<LiveVehicleStatus> liveVehicleStatusList = ((LiveVehicleStatusDaoImp)DBManager.getInstance().getDao(DAOEnum.LIVE_VEHICLE_STATUS_DAO)).
				selectAllForCacheByImei(imei);
		processCacheUpdate(liveVehicleStatusList);
	}

	private void retrieveVehicleDataFromDBIfNotInMemory(Long vehicleId) {
		List<LiveVehicleStatus> liveVehicleStatusList = ((LiveVehicleStatusDaoImp)DBManager.getInstance().getDao(DAOEnum.LIVE_VEHICLE_STATUS_DAO)).
				selectAllForCacheByVehicleId(vehicleId);
		processCacheUpdate(liveVehicleStatusList);
	}


	private void retrieveDriverDataFromDBIfNotInMemory(long driverId) {
		List<LiveVehicleStatus> liveVehicleStatusList = ((LiveVehicleStatusDaoImp)DBManager.getInstance().getDao(DAOEnum.LIVE_VEHICLE_STATUS_DAO)).
				selectAllForCacheByDriverId(driverId);
		processCacheUpdate(liveVehicleStatusList);
	}
	
	private  void retrieveTripDataFromDBIfNotInMemory(long tripId) {
		List<LiveVehicleStatus> liveVehicleStatusList = ((LiveVehicleStatusDaoImp)DBManager.getInstance().getDao(DAOEnum.LIVE_VEHICLE_STATUS_DAO)).
				selectAllForCacheByTripId(tripId);
		processCacheUpdate(liveVehicleStatusList);
	}

}