/**
 * 
 */
package com.i10n.dbCacheManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.EmriVehiclesBaseStationDaoImp;
import com.i10n.db.entity.EmriVehiclesBaseStation;
import com.i10n.db.tools.DBManager;

/**
 * 
 * @author DVasudeva
 *
 */
public class LoadEmriVehiclesBaseStationDetails {

	private static final Logger LOG = Logger.getLogger(LoadEmriVehiclesBaseStationDetails.class);
	public ConcurrentHashMap<Long, EmriVehiclesBaseStation> cacheEmriRajasthanRecords = new ConcurrentHashMap<Long, EmriVehiclesBaseStation>();

	static private LoadEmriVehiclesBaseStationDetails _instance = null;
	private LoadEmriVehiclesBaseStationDetails(){};


	/**
	 * Retrieves EmriRajasthan corresponding to the imei
	 * @param EmriRajasthanId
	 * @return
	 */
	public EmriVehiclesBaseStation retrieve (Long id) {
		// This may return null
		EmriVehiclesBaseStation emri = cacheEmriRajasthanRecords.get(id);
		if (emri == null) {
			retrieveEmriBaseStationDataFromDBIfNotInMemory(id);
			return cacheEmriRajasthanRecords.get(id);
		}
		return emri;
	}

	public static LoadEmriVehiclesBaseStationDetails getInstance() {
		LOG.debug("In LoadEmriRajasthanDetails getInstance");
		if(null == _instance){
			_instance = new LoadEmriVehiclesBaseStationDetails();
			_instance.loadDataForEmriRajasthans();
		}

		return _instance;
	}

	private void loadDataForEmriRajasthans(){
		LOG.debug("Loadinf EmriRajasthanDetails cache");
		List<EmriVehiclesBaseStation> emriVehiclesBaseStations = ((EmriVehiclesBaseStationDaoImp)DBManager.getInstance()
				.getDao(DAOEnum.EMRI_RAJASTHAN_DAO)).selectAll();
		processCacheUpdate(emriVehiclesBaseStations);
		LOG.debug("Loadinf EmriRajasthanDetails cache successful");
	}	

	private void processCacheUpdate(List<EmriVehiclesBaseStation> emriVehiclesBaseStations) {
		if(emriVehiclesBaseStations != null){
			for(EmriVehiclesBaseStation emriVehiclesBaseStation : emriVehiclesBaseStations){
				cacheEmriRajasthanRecords.put(emriVehiclesBaseStation.getID().getId(), emriVehiclesBaseStation);
			}
		}

	}

	public void retrieveEmriBaseStationDataFromDBIfNotInMemory(Long vehicleid){
		LOG.debug("In LoadEmriRajasthanDetails retrieveIMEIDataFromDBIfNotInMemory: "+vehicleid);
		List<EmriVehiclesBaseStation> emriVehiclesBaseStations = ((EmriVehiclesBaseStationDaoImp)DBManager.getInstance()
				.getDao(DAOEnum.EMRI_RAJASTHAN_DAO)).selectByVehicleId(vehicleid);
		processCacheUpdate(emriVehiclesBaseStations);
	}	

	public void refresh() {
		clearCache();
		loadDataForEmriRajasthans();
	}

	private void clearCache() {
		cacheEmriRajasthanRecords.clear();
	}
}
