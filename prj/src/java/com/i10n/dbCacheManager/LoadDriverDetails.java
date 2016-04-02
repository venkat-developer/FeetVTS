/**
 * 
 */
package com.i10n.dbCacheManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.DriverDaoImp;
import com.i10n.db.entity.Driver;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;

/**
 * @author HEMANT
 *
 */
public class LoadDriverDetails {

	private static final Logger LOG = Logger.getLogger(LoadDriverDetails.class);
	public ConcurrentHashMap<Long, Driver> cacheDriverRecords = new ConcurrentHashMap<Long, Driver>();

	static private LoadDriverDetails _instance = null;
	private LoadDriverDetails(){};


	/**
	 * Retrieves Driver corresponding to the imei
	 * @param driverId
	 * @return
	 */
	public Driver retrieve (Long driverId) {
		// This may return null
		Driver driver = cacheDriverRecords.get(driverId);
		if (driver == null) {
			retrieveIMEIDataFromDBIfNotInMemory(driverId);
			return cacheDriverRecords.get(driverId);
		}
		return driver;
	}

	public static LoadDriverDetails getInstance() {
		if(null == _instance){
			_instance = new LoadDriverDetails();
			_instance.loadDataForDrivers();
		}

		return _instance;
	}

	private void loadDataForDrivers(){
		LOG.debug("Loading Driver details to cache");
		List<Driver> driverList = ((DriverDaoImp)DBManager.getInstance().getDao(DAOEnum.DRIVER_DAO)).selectAll();
		processCacheUpdate(driverList);
		LOG.debug("Loading Driver details to cache successful");
	}	

	private void processCacheUpdate(List<Driver> driverList) {
		if(driverList != null){
			for(Driver driver : driverList){
				cacheDriverRecords.put(driver.getId().getId(), driver);
			}
		}
	}

	public void retrieveIMEIDataFromDBIfNotInMemory(Long driverId){
			List<Driver> driverList = ((DriverDaoImp)DBManager.getInstance().getDao(DAOEnum.DRIVER_DAO)).selectByPrimaryKey(new LongPrimaryKey(driverId));
			processCacheUpdate(driverList);
	}	

	/**
	 * Just refreshes the field if the data is already cached
	 * @param vehicleId
	 */
	public void updateDriverObjectFields (Driver updatedObject) {
		Driver driver = retrieve (updatedObject.getId().getId());
		if (driver!=null) {
			driver.setDisplayName(updatedObject.getDisplayName());
			driver.setFirstName(updatedObject.getFirstName());	
			driver.setPhoto(updatedObject.getPhoto());
			driver.setLicenseno(updatedObject.getLicenseno());
			cacheDriverRecords.put(updatedObject.getId().getId(), driver);
		}
	}
	
	public void refresh() {
		clearCache();
		loadDataForDrivers();
	}

	private void clearCache() {
		cacheDriverRecords.clear();
	}
}
