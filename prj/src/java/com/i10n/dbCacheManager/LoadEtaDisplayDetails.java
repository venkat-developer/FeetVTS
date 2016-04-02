package com.i10n.dbCacheManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.EtaDisplayDaoImp;
import com.i10n.db.entity.EtaDisplay;
import com.i10n.db.tools.DBManager;

/**
 * @author Dharmaraju V
 *
 */
public class LoadEtaDisplayDetails {
	private static final Logger LOG = Logger.getLogger(LoadEtaDisplayDetails.class);

	static private LoadEtaDisplayDetails _instance = null;
	private LoadEtaDisplayDetails(){};

	public static LoadEtaDisplayDetails getInstance() {
		if(null == _instance){
			_instance = new LoadEtaDisplayDetails();
			_instance.loadEtaDisplayDetails();
		}
		return _instance;
	}

	public void refresh(){
		clearCache();
		loadEtaDisplayDetails();
	}
	
	public void clearCache(){
		cacheEtaDisplayDetails.clear();
	}

	/**
	 *  Hashmap<VehicleID, Hashmap<RouteId, Hashmap<StopId, ETADisplayList>>> 
	 */
	public ConcurrentHashMap<Long, ConcurrentHashMap<Long, ConcurrentHashMap<Long, EtaDisplay>>> cacheEtaDisplayDetails = 
			new ConcurrentHashMap<Long, ConcurrentHashMap<Long,ConcurrentHashMap<Long,EtaDisplay>>>();


	/**
	 * Initially loading the DB to cache 
	 */
	private void loadEtaDisplayDetails (){
		LOG.debug("Caching EtaDisplay details ...");
		List<EtaDisplay> etaDisplayList = ((EtaDisplayDaoImp)DBManager.getInstance().getDao(DAOEnum.ETA_DISPLAY_DAO)).selectAll();
		processCacheUdpated(etaDisplayList);
		LOG.debug("Caching EtaDisplay details was Successful...");
	}

	public void getEtaDisplayDetailsForTheVehicle (Long vehicleId, Long routeId, Long stopId){
		List<EtaDisplay> etaDisplayList = ((EtaDisplayDaoImp)DBManager.getInstance().getDao(DAOEnum.ETA_DISPLAY_DAO)).selectByVehicleIdRouteIdAndStopId(vehicleId, routeId, stopId);
		processCacheUdpated(etaDisplayList);
	}

	private ConcurrentHashMap<Long, ConcurrentHashMap<Long, EtaDisplay>> getEtaDisplayDetailsForTheVehicle(long vehicleId) {
		List<EtaDisplay> etaDisplayList = ((EtaDisplayDaoImp)DBManager.getInstance().getDao(DAOEnum.ETA_DISPLAY_DAO)).selectByVehicleId(vehicleId);
		processCacheUdpated(etaDisplayList);
		return cacheEtaDisplayDetails.get(vehicleId);
	}

	/**
	 * Push the data pulled from the DB to push into the cache
	 */
	private void processCacheUdpated(List<EtaDisplay> etaDisplayList) {

		//VehicleId -> RouteId -> StopId -> ETA Object
		if(etaDisplayList != null){
			for(EtaDisplay etaDisplayEntity : etaDisplayList){

				ConcurrentHashMap<Long, ConcurrentHashMap<Long, EtaDisplay>> routeList = cacheEtaDisplayDetails.get(etaDisplayEntity.getVehicleId());
				ConcurrentHashMap<Long, EtaDisplay> stopList ;
				if(routeList == null){
					routeList = new ConcurrentHashMap<Long, ConcurrentHashMap<Long,EtaDisplay>>();
				}
				stopList = routeList.get(etaDisplayEntity.getRouteId());
				if (stopList ==  null) {
					stopList = new ConcurrentHashMap<Long, EtaDisplay>();						
				}
				stopList.put(etaDisplayEntity.getStopId(), etaDisplayEntity);					
				routeList.put(etaDisplayEntity.getRouteId(), stopList);

				cacheEtaDisplayDetails.put(etaDisplayEntity.getVehicleId(), routeList);
			}
		}
	}

	public EtaDisplay retrieve(Long vehicleId, Long routeId, Long stopId) {
		ConcurrentHashMap<Long, ConcurrentHashMap<Long, EtaDisplay>> routesOfVehicle = cacheEtaDisplayDetails.get(vehicleId);
		if (routesOfVehicle!=null) {
			ConcurrentHashMap<Long, EtaDisplay> stopsOfRoute = routesOfVehicle.get(routeId);
			if (stopsOfRoute != null) {
				EtaDisplay displayDetails = stopsOfRoute.get(stopId);
				if (displayDetails != null) {
					return displayDetails ;
				}
			}
		} else {
			getEtaDisplayDetailsForTheVehicle(vehicleId, routeId, stopId);
			return cacheEtaDisplayDetails.get(vehicleId).get(routeId).get(stopId);
		}
		return null;
	}

	/**
	 * @param vehicleId
	 * @return EtaDisplay list for cached for the vehicle @param vehicleId
	 */
	public ConcurrentHashMap<Long, ConcurrentHashMap<Long, EtaDisplay>> retrieve(long vehicleId) {
		ConcurrentHashMap<Long, ConcurrentHashMap<Long, EtaDisplay>> hashMap = cacheEtaDisplayDetails.get(vehicleId);
		if(hashMap == null){
			hashMap = getEtaDisplayDetailsForTheVehicle(vehicleId);
		}
		return hashMap;
	}

}
