package com.i10n.dbCacheManager;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.StopHistoryDaoImp;
import com.i10n.db.entity.StopHistory;
import com.i10n.db.tools.DBManager;

/**
 * @author Dharmaraju V
 *
 */
public class LoadActiveVehicleStopHistoryDetails {

	private static final Logger LOG = Logger.getLogger(LoadActiveVehicleStopHistoryDetails.class);

	static private LoadActiveVehicleStopHistoryDetails _instance = null;

	/**	Map from VehicleId and RouteId pair to the StopHistories it has resulted to*/
	public ConcurrentHashMap<String, Vector<Long>> cacheActiveVehicleStopHistories = new ConcurrentHashMap<String, Vector<Long>>();

	private LoadActiveVehicleStopHistoryDetails(){};

	public static LoadActiveVehicleStopHistoryDetails getInstance() {
		if(null == _instance){
			_instance = new LoadActiveVehicleStopHistoryDetails();
			_instance.loadActiveVehicleStopHistoryDetails();
		}
		return _instance;
	}

	public Vector<Long> retrieve(Long vehicleId, Long routeId){
		String key = (vehicleId+"-"+routeId).replace(" ", "").trim();
		Vector<Long> stopHistoryIdList = cacheActiveVehicleStopHistories.get(key);
		if(stopHistoryIdList == null){
			getDetailsforNewlyAddedVehicleRouteStops(vehicleId, routeId);
			return cacheActiveVehicleStopHistories.get(key);
		}else if(stopHistoryIdList.size() == 0){
			cacheActiveVehicleStopHistories.remove(key);
			return null;
		}
		return stopHistoryIdList;

	}

	public void refresh(){
		cacheActiveVehicleStopHistories.clear();
		loadActiveVehicleStopHistoryDetails();
	}

	private void loadActiveVehicleStopHistoryDetails (){
		LOG.debug("Caching ActiveVehicleStopHistory details ...");
		List<StopHistory> stopHistoryList = ((StopHistoryDaoImp)DBManager.getInstance().
				getDao(DAOEnum.STOPHISTORY_DAO)).selectAllActiveVehicleStopHistories();
		processCacheUpdate(stopHistoryList);
	}

	public void getDetailsforNewlyAddedVehicleRouteStops (Long vehicleId, Long routeId){
		List<StopHistory> stopHistoryList = ((StopHistoryDaoImp)DBManager.getInstance().
				getDao(DAOEnum.STOPHISTORY_DAO)).selectStopHistoriesForVehicleAndRoute(vehicleId, routeId);
		processCacheUpdate(stopHistoryList);

	}

	private void processCacheUpdate(List<StopHistory> stopHistoryList) {
		String activeVehicleRouteAssociation = null;
		Vector<Long>  stopHistoryIdList = new Vector<Long>();

		if(stopHistoryList != null){
			for(StopHistory stopHistory : stopHistoryList){	
				activeVehicleRouteAssociation = new String();
				activeVehicleRouteAssociation=((stopHistory.getVehicleId())+"-"+(stopHistory.getRouteId())).replace(" ", "").trim();
				if(cacheActiveVehicleStopHistories.containsKey(activeVehicleRouteAssociation)){
					stopHistoryIdList = cacheActiveVehicleStopHistories.get(activeVehicleRouteAssociation);
					stopHistoryIdList.add(stopHistory.getId().getId());
					cacheActiveVehicleStopHistories.put(activeVehicleRouteAssociation, stopHistoryIdList);
				}else{
					stopHistoryIdList = new Vector<Long>();
					stopHistoryIdList.add(stopHistory.getId().getId());
					cacheActiveVehicleStopHistories.put(activeVehicleRouteAssociation, stopHistoryIdList);
				}
			}
		}
	}

	public String toString(){
		LOG.debug("Cached ActiveVehicleStopHistory details are ");
		for(String key : cacheActiveVehicleStopHistories.keySet()){
			LOG.debug("VehicleIdRouteId pair = "+key);
			for(Long stopHistoryId : cacheActiveVehicleStopHistories.get(key)){
				LOG.debug("StopHistoryId = "+stopHistoryId);
			}
		}
		return null;
	}
}
