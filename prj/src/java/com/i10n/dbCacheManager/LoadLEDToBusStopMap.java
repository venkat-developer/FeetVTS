package com.i10n.dbCacheManager;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.LEDToBusStopDAOImpl;
import com.i10n.db.entity.LEDToBusStop;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.util.EnvironmentInfo;

/**
 * Caching the mapping of LED/Module to the Bus stops associated
 * @author Dharmaraju V
 *
 */
public class LoadLEDToBusStopMap {

	private static Logger LOG = Logger.getLogger(LoadLEDToBusStopMap.class);
	
	private static LoadLEDToBusStopMap _instance = null;
	
	private static final long LED_TO_BUSSTOP_CACHE_REFRESH_DURATION_MS = Long.valueOf(EnvironmentInfo.getProperty("LED_TO_BUSSTOP_CACHE_REFRESH_DURATION_MS"));
	
	/**
	 * Mapping deviceID to the entity containing detail of the bustop and the led type
	 */
	private static HashMap<String, LEDToBusStop> cacheLEDToBusStopMap = new HashMap<String, LEDToBusStop>();
	
	public LoadLEDToBusStopMap() {
		loadCache();
		TimerTask refreshCache = new TimerTask() {			
			@Override
			public void run() {
				LOG.debug("Timertask refreshing LED to BusStop Cache list at "+new Date());
				refresh();
			}
		};
		Timer refreshCacheTimer = new Timer();
		refreshCacheTimer.scheduleAtFixedRate(refreshCache, LED_TO_BUSSTOP_CACHE_REFRESH_DURATION_MS, LED_TO_BUSSTOP_CACHE_REFRESH_DURATION_MS);
	}
	
	public static LoadLEDToBusStopMap getInstance(){
		if(_instance == null){
			_instance = new LoadLEDToBusStopMap();
		}
		return _instance;
	}

	/**
	 * 
	 * @param deviceID
	 * @return
	 * 1) LEDToBusStop entity mapped with the deviceID 
	 * 2) May return null 
	 */
	public LEDToBusStop retrieve(String deviceID){
		if(!cacheLEDToBusStopMap.containsKey(deviceID)){
			getLEDToBusStopMapOfNewlyAddedDevice(deviceID);
			if(!cacheLEDToBusStopMap.containsKey(deviceID)){
				return null;
			}
		}
		return cacheLEDToBusStopMap.get(deviceID);
	}

	private void loadCache() {
		LOG.debug("Initializing the LED to bus stop map");
		List<LEDToBusStop> ledToBusStopList = ((LEDToBusStopDAOImpl)DBManager.getInstance().getDao(DAOEnum.LED_TO_BUS_STOP_DAO)).selectAll();
		processCacheUpdate(ledToBusStopList);
	}

	private void processCacheUpdate(List<LEDToBusStop> ledToBusStopList) {
		if(ledToBusStopList != null){
			for(LEDToBusStop ledToBusStop : ledToBusStopList){
				cacheLEDToBusStopMap.put(ledToBusStop.getDeviceID(), ledToBusStop);
			}
		}

	}

	/**
	 * Retrieve and cache the LEDToBusStop details of the Module(deviceID) specified 
	 * @param deviceID
	 */
	private void getLEDToBusStopMapOfNewlyAddedDevice(String deviceID) {
		LOG.debug("Getting LED to bus stop map detail for the deviceid : "+deviceID);
		List<LEDToBusStop> ledToBusStopList = ((LEDToBusStopDAOImpl)DBManager.getInstance().getDao(DAOEnum.LED_TO_BUS_STOP_DAO)).selectByDeviceId(deviceID);
		processCacheUpdate(ledToBusStopList);
	}

	private void refresh(){
		clearCache();
		loadCache();
	}

	private void clearCache() {
		cacheLEDToBusStopMap.clear();
	}
}
