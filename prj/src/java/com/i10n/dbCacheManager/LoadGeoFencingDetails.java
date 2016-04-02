package com.i10n.dbCacheManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.GeoFenceRegionsDaoImp;
import com.i10n.db.entity.GeoFenceRegions;
import com.i10n.db.tools.DBManager;

/**
 * 
 * @author DVasudeva
 *
 */
public class LoadGeoFencingDetails {
	private static final Logger LOG = Logger.getLogger(LoadGeoFencingDetails.class);

	/**
	 * Mapping the Vehicle to the list of Geofence regions assigned to it
	 */
	public HashMap<Long/*Vehicle*/, ArrayList<GeoFenceRegions>/*GeofenceRegionslist*/> cacheGeoFencingRecords = 
			new HashMap<Long, ArrayList<GeoFenceRegions>>();

	static private LoadGeoFencingDetails _instance = null;

	private LoadGeoFencingDetails(){
	};


	/**
	 * Retrieves GeoFenceRegions corresponding to the imei
	 * @param vehicleId
	 * @return
	 */
	public ArrayList<GeoFenceRegions> retrieve (long vehicleId) {
		// This may return null
		ArrayList<GeoFenceRegions> geofencingList = null;
		if(cacheGeoFencingRecords.containsKey(vehicleId)){
			geofencingList = cacheGeoFencingRecords.get(vehicleId);
		} else {
			geofencingList = retrieveGeofenceListForVehicle(vehicleId);
		}
		return geofencingList;
	}

	public static LoadGeoFencingDetails getInstance() {
		if(null == _instance){
			_instance = new LoadGeoFencingDetails();
			_instance.loadDataForGeoFencing();
		}
		return _instance;
	}

	public ArrayList<GeoFenceRegions> retrieveGeofenceListForVehicle(Long vehicleId){
		List<GeoFenceRegions> geoFenceRegionList = ((GeoFenceRegionsDaoImp)DBManager.getInstance().
				getDao(DAOEnum.GEO_FENCE_REGIONS_DAO)).selectByVehicleIdForCache(vehicleId);
		processCacheUpdate(geoFenceRegionList);
		return cacheGeoFencingRecords.get(vehicleId);
	}

	private void loadDataForGeoFencing(){
		LOG.debug("Loading Geofencing cache");
		List<GeoFenceRegions> geoFenceRegionList = ((GeoFenceRegionsDaoImp)DBManager.getInstance().
				getDao(DAOEnum.GEO_FENCE_REGIONS_DAO)).selectAllForCache();
		processCacheUpdate(geoFenceRegionList);
		LOG.debug("Successfully loaded Geofencing cache");
	}


	private void processCacheUpdate(List<GeoFenceRegions> geoFenceRegionList) {
		if(geoFenceRegionList != null){
			ArrayList<GeoFenceRegions> geoFenceList = null;
			for(GeoFenceRegions geoFenceRegions : geoFenceRegionList){
				if(!cacheGeoFencingRecords.containsKey(geoFenceRegions.getVehicleId())){
					geoFenceList = new ArrayList<GeoFenceRegions>();
				} else {
					geoFenceList = cacheGeoFencingRecords.get(geoFenceRegions.getVehicleId());
				}
				geoFenceList.add(geoFenceRegions);
				cacheGeoFencingRecords.put(geoFenceRegions.getVehicleId(), geoFenceList);
			}
		}
	}

	public void refresh() {
		clearCache();
		loadDataForGeoFencing();
	}

	private void clearCache() {
		cacheGeoFencingRecords.clear();
	}
}
