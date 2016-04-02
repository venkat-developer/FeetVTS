package com.i10n.dbCacheManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.StopHistoryDaoImp;
import com.i10n.db.dao.VehicleToRouteScheduleDaoImp;
import com.i10n.db.entity.StopHistory;
import com.i10n.db.entity.VehicleToRouteSchedule;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.eta.ETAUtils;

/**
 * @author Dharmaraju V
 *
 */
public class LoadVehicleRouteAssociationDetails {

	private static final Logger LOG = Logger.getLogger(LoadVehicleRouteAssociationDetails.class);

	private static LoadVehicleRouteAssociationDetails _instance = null;

	/**	VehiceId mapping to list of assigned routes with its status*/
	public ConcurrentHashMap<Long/*VehicleId*/,ConcurrentHashMap<VehicleToRouteSchedule,Boolean/*RouteStatus*/>> 
	cacheVehicleToRouteAssociation = new ConcurrentHashMap<Long, ConcurrentHashMap<VehicleToRouteSchedule,Boolean>>();

	private LoadVehicleRouteAssociationDetails(){};

	public static LoadVehicleRouteAssociationDetails getInstance() {
		if(null == _instance){
			_instance = new LoadVehicleRouteAssociationDetails();
			_instance.loadVehicleRouteStatusDetials();
		}
		return _instance;
	}

	public ConcurrentHashMap<VehicleToRouteSchedule,Boolean> retrieve(Long vehicleId){
		ConcurrentHashMap<VehicleToRouteSchedule,Boolean> routeStatusList = cacheVehicleToRouteAssociation.get(vehicleId);
		if(routeStatusList == null){
			getVehicleRouteScheduleForNewlyAddedVehicle(vehicleId);
			routeStatusList = cacheVehicleToRouteAssociation.get(vehicleId);
		}
		if(routeStatusList != null){
			if(routeStatusList.size() == 0){
				cacheVehicleToRouteAssociation.remove(vehicleId);
				return null;
			}
		}
		return routeStatusList;
	}

	public void refresh(){
		clearCache();
		loadVehicleRouteStatusDetials();
	}

	private void clearCache() {
		cacheVehicleToRouteAssociation.clear();
	}

	/**
	 * 1) Retrieve the Vehicle to route schedule map list
	 * 2) Get each route running status and maintain the same in the cache
	 */
	private void loadVehicleRouteStatusDetials(){
		LOG.debug("Caching VehicleRouteAssociation details ..... ");
		List<VehicleToRouteSchedule> vehicleToRouteScheduleList = ((VehicleToRouteScheduleDaoImp)DBManager.getInstance().getDao(DAOEnum.VEHICLE_TO_ROUTE_SCHEDULE_DAO)).selectAll();
		processCacheUpdate(vehicleToRouteScheduleList);
	}

	private void processCacheUpdate(List<VehicleToRouteSchedule> vehicleToRouteScheduleList) {
		if(vehicleToRouteScheduleList != null){
			ConcurrentHashMap<VehicleToRouteSchedule, Boolean> routeStatus = new ConcurrentHashMap<VehicleToRouteSchedule, Boolean>();
			for(VehicleToRouteSchedule vehicleToRouteSchedule: vehicleToRouteScheduleList){
				String routeScheduleId = ETAUtils.getRouteScheduleId(vehicleToRouteSchedule);
				boolean isRouteActive = false;
				List<StopHistory> stopHistoryList = ((StopHistoryDaoImp)DBManager.getInstance().getDao(DAOEnum.STOPHISTORY_DAO)).
						selectByVehicleIdAndRouteId(vehicleToRouteSchedule.getVehicleId(), vehicleToRouteSchedule.getRouteId(), routeScheduleId);
				if(stopHistoryList != null && stopHistoryList.size() > 0 ){
					isRouteActive = true;
				}
				if(cacheVehicleToRouteAssociation.containsKey(vehicleToRouteSchedule.getVehicleId())){
					routeStatus =  cacheVehicleToRouteAssociation.get(vehicleToRouteSchedule.getVehicleId());
					routeStatus.put(vehicleToRouteSchedule, isRouteActive);
					cacheVehicleToRouteAssociation.put(vehicleToRouteSchedule.getVehicleId(), routeStatus);
				}else{
					routeStatus = new ConcurrentHashMap<VehicleToRouteSchedule, Boolean>();
					routeStatus.put(vehicleToRouteSchedule, isRouteActive);
					cacheVehicleToRouteAssociation.put(vehicleToRouteSchedule.getVehicleId(), routeStatus);
				}
			}
		}

	}

	/**
	 * 1) Retrieve the Vehicle to route schedule map list for the vehicle requested
	 * 2) Get each route running status for the vehicle and maintain the same in the cache
	 */
	private void getVehicleRouteScheduleForNewlyAddedVehicle(Long vehicleId){
		List<VehicleToRouteSchedule> vehicleToRouteScheduleList = ((VehicleToRouteScheduleDaoImp)DBManager.getInstance().
				getDao(DAOEnum.VEHICLE_TO_ROUTE_SCHEDULE_DAO)).
				selectByVehicleId(vehicleId);
		processCacheUpdate(vehicleToRouteScheduleList);
	}

}
