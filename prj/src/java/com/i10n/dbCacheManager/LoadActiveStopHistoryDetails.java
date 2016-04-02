package com.i10n.dbCacheManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.StopHistoryDaoImp;
import com.i10n.db.entity.StopHistory;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;

/**
 * @author Dharmaraju V
 *
 */
public class LoadActiveStopHistoryDetails {

	private static final Logger LOG = Logger.getLogger(LoadActiveStopHistoryDetails.class);
	private static LoadActiveStopHistoryDetails _instance = null;
	private LoadActiveStopHistoryDetails(){};

	public static LoadActiveStopHistoryDetails getInstance() {
		if(null == _instance){
			_instance = new LoadActiveStopHistoryDetails();
			_instance.loadStopHistoryDetails();
		}
		return _instance;
	}

	public StopHistory retrieve(Long stopHistoryId){
		StopHistory stopHistoryEntity = cacheStopHistory.get(stopHistoryId);
		if(stopHistoryEntity == null){
			getDetailsForNewlyAddedStopHistory(stopHistoryId);
			return cacheStopHistory.get(stopHistoryId);
		}
		return stopHistoryEntity;
	}

	public void refresh(){
		clearCache();
		loadStopHistoryDetails();
	}
	
	private void clearCache() {
		cacheStopHistory.clear();
	}

	/**	StopHistoryId mapping to StopHistory Entity*/
	public ConcurrentHashMap<Long, StopHistory> cacheStopHistory= new ConcurrentHashMap<Long, StopHistory>();

	private void loadStopHistoryDetails (){
		LOG.debug("Caching StopHistory Details ..... ");
		List<StopHistory> stopHistoryList = ((StopHistoryDaoImp)DBManager.getInstance().getDao(DAOEnum.STOPHISTORY_DAO)).selectAll();
		processCacheUpdate(stopHistoryList);
	}

	private void processCacheUpdate(List<StopHistory> stopHistoryList) {
		if(stopHistoryList != null){
			for(StopHistory stopHistory: stopHistoryList){
				cacheStopHistory.put(stopHistory.getId().getId(),stopHistory);
			}
		}
	}

	public void getDetailsForNewlyAddedStopHistory (Long stopHistoryId){
			List<StopHistory> stopHistoryList = ((StopHistoryDaoImp)DBManager.getInstance().getDao(DAOEnum.STOPHISTORY_DAO)).selectByPrimaryKey(new LongPrimaryKey(stopHistoryId));
			processCacheUpdate(stopHistoryList);
	}

	public String toString(){
		LOG.debug("Cached StopHistory entries are ");
		for(StopHistory stopHistory: cacheStopHistory.values()){
			LOG.debug(stopHistory.toString());
		}
		return null;
	}
}
