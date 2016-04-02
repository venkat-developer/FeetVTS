package com.i10n.dbCacheManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.StopDeviationDaoImp;
import com.i10n.db.entity.StopDeviation;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;

/**
 * @author Dharmaraju V
 *
 */
public class LoadSASStopDeviation {
	private static final Logger LOG = Logger.getLogger(LoadSASStopDeviation.class);

	static private LoadSASStopDeviation _instance = null;
	private LoadSASStopDeviation(){};

	public static LoadSASStopDeviation getInstance() {
		if(null == _instance){
			_instance = new LoadSASStopDeviation();
		}
		return _instance;
	}

	public ConcurrentHashMap<Long, StopDeviation> cacheSASStopDeviation = new ConcurrentHashMap<Long, StopDeviation>();

	/**
	 * 
	 * @param stopDeviationId
	 * @return
	 */
	public StopDeviation getDetailsForSASStopDeviation (Long stopDeviationId ){
		List<StopDeviation> stopDeviationList = ((StopDeviationDaoImp)DBManager.getInstance().getDao(DAOEnum.STOP_DEVIATIONS_DAO)).
				selectByPrimaryKey(new LongPrimaryKey(stopDeviationId));
		processCacheUpdate(stopDeviationList);
		return cacheSASStopDeviation.get(stopDeviationId);
	}

	private void processCacheUpdate(List<StopDeviation> stopDeviationList) {
		if(stopDeviationList != null){
			for(StopDeviation stopDeviation : stopDeviationList){
				cacheSASStopDeviation.put(stopDeviation.getId().getId(), stopDeviation);
			}
		}
	}

	public synchronized void removeEntriesForVehicle(long vehicleId) {
		LOG.debug("Updating StopDeviation violation cache ");
		for(Long deviationId : cacheSASStopDeviation.keySet()){
			StopDeviation deviation = cacheSASStopDeviation.get(deviationId);
			if(deviation.getVehicleId() == vehicleId){
				cacheSASStopDeviation.remove(deviationId);
			}
		}		
	}

	/**
	 * 
	 * @param stopDeviationId
	 * @return StopDeviation entity corresponding to @param stopDeviationId 
	 */
	public StopDeviation retrieve(Long stopDeviationId){
		StopDeviation stopDeviation = cacheSASStopDeviation.get(stopDeviationId);
		if(stopDeviation == null){
			stopDeviation = getDetailsForSASStopDeviation(stopDeviationId);
		}
		return stopDeviation;
	}

}
