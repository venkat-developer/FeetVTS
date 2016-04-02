package com.i10n.dbCacheManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.TimeDeviationDaoImp;
import com.i10n.db.entity.TimeDeviation;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;

/**
 * @author Dharmaraju V
 *
 */
public class LoadSASTimeDeviations {
	private static final Logger LOG = Logger.getLogger(LoadSASTimeDeviations.class);
	
	static private LoadSASTimeDeviations _instance = null;
	private LoadSASTimeDeviations(){};
	
	public static LoadSASTimeDeviations getInstance() {
		if(null == _instance){
			_instance = new LoadSASTimeDeviations();
		}
		return _instance;
	}
	
	public ConcurrentHashMap<Long, TimeDeviation> cacheSASTimeDeviation = new ConcurrentHashMap<Long, TimeDeviation>();
	
	/**
	 * 
	 * @param timeDeviationId
	 * @return
	 */
	public TimeDeviation getDetailsForSASTimeDeviation (Long timeDeviationId ){
			List<TimeDeviation> timeDeviationList = ((TimeDeviationDaoImp)DBManager.getInstance().getDao(DAOEnum.TIME_DEVIATIONS_DAO)).
					selectByPrimaryKey(new LongPrimaryKey(timeDeviationId));
			processCacheUpdate(timeDeviationList);
		return cacheSASTimeDeviation.get(timeDeviationId);
	}

	private void processCacheUpdate(List<TimeDeviation> timeDeviationList) {
		if(timeDeviationList != null){
			for(TimeDeviation timeDeviation : timeDeviationList){
				cacheSASTimeDeviation.put(timeDeviation.getId().getId(), timeDeviation);
			}
		}
	}

	public synchronized void removeEntriesForVehicle(long vehicleId) {
		LOG.debug("Updating TimeDeviation violation cache ");
		for(Long deviationId : cacheSASTimeDeviation.keySet()){
			TimeDeviation deviation = cacheSASTimeDeviation.get(deviationId);
			if(deviation.getVehicleId() == vehicleId){
				cacheSASTimeDeviation.remove(deviationId);
			}
		}		
	}

	/**
	 * 
	 * @param timeDeviationId
	 * @return Time deviation entity corresponding to @param timeDeviationId 
	 */
	public TimeDeviation retrieve(Long timeDeviationId) {
		TimeDeviation deviation = cacheSASTimeDeviation.get(timeDeviationId);
		if(deviation == null){
			deviation = getDetailsForSASTimeDeviation(timeDeviationId);
		}
		return deviation;
	}
}
