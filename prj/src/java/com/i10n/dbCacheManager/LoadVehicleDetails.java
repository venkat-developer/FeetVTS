package com.i10n.dbCacheManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.VehicleDaoImpl;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.tools.DBManager;

/**
 * 
 * @author DVasudeva
 *
 */
public class LoadVehicleDetails {

	private static final Logger LOG = Logger.getLogger(LoadVehicleDetails.class);
	public ConcurrentHashMap<Long, Vehicle> cacheVehicleRecords = new ConcurrentHashMap<Long, Vehicle>();

	static private LoadVehicleDetails _instance = null;
	private LoadVehicleDetails(){};

	public static LoadVehicleDetails getInstance() {
		if(null == _instance){
			_instance = new LoadVehicleDetails();
			_instance.loadDataForVehicles();
		}
		return _instance;
	}

	/**
	 * Retrieves Vehicle corresponding to the imei
	 * @param imei
	 * @return
	 */
	public Vehicle retrieve (long vehicleId) {
		// This may return null
		Vehicle vehicle = cacheVehicleRecords.get(vehicleId);

		if (vehicle == null || vehicle.getImei()==null || vehicle.getImei().isEmpty()) {
			/* Try to retrieve from DB */
			getDataForNewlyAddedVehicle (vehicleId);
			/*LOG.debug ("Populating IMEI for VID "+vehicleId+" IMEI "+vehicle.getImei());
			LOG.debug ("VehicleId and MaxSpeed "+vehicleId +" , "+vehicle.getMaxSpeed());*/
		}
		return cacheVehicleRecords.get(vehicleId);
	}

	private void loadDataForVehicles(){
		LOG.debug("Loading Vehicle Cache");
		List<Vehicle> vehicleList = ((VehicleDaoImpl)DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).selectAllForCache();
		processCacheUpdate(vehicleList);
		LOG.debug("Loading Vehicle Cache successful");
	}

	private void processCacheUpdate(List<Vehicle> vehicleList) {
		if(vehicleList != null){
			for(Vehicle vehicle : vehicleList){
				cacheVehicleRecords.put(vehicle.getId().getId(), vehicle);
			}
		}
	}

	public void getDataForNewlyAddedVehicle(Long vehicleId){
		List<Vehicle> vehicleList = ((VehicleDaoImpl)DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).selectByVehicleIdForCache(vehicleId);
		processCacheUpdate(vehicleList);
	}
	
	public void refresh() {
		clearCache();
		loadDataForVehicles();
	}

	private void clearCache() {
		cacheVehicleRecords.clear();
	}

}