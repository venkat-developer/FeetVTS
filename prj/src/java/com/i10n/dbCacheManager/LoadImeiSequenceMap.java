package com.i10n.dbCacheManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.ImeiSequenceMapDaoImpl;
import com.i10n.db.entity.ImeiSequenceMap;
import com.i10n.db.tools.DBManager;

/**
 * @author Dharmaraju V
 *
 */
public class LoadImeiSequenceMap {
	private static final Logger LOG = Logger.getLogger(LoadImeiSequenceMap.class);

	static private LoadImeiSequenceMap _instance = null;
	private LoadImeiSequenceMap(){};

	public static LoadImeiSequenceMap getInstance() {
		if(null == _instance){
			_instance = new LoadImeiSequenceMap();
			_instance.loadDataFromImeiSequenceMap();
		}
		return _instance;
	}

	public static ConcurrentHashMap<String, Integer> cacheImeiSequenceMap = new ConcurrentHashMap<String, Integer>();

	/**
	 * @param imei
	 * @return
	 */
	public Integer retrieve (String imei) {
		if (!cacheImeiSequenceMap.containsKey(imei)) {
			retrieveImeiSequenceMapDetailsWhenNotInCache(imei);
			return cacheImeiSequenceMap.get(imei);
		}
		return cacheImeiSequenceMap.get(imei);	
	}

	/**
	 * @param imei
	 * @return
	 */
	public static void store (String imei, Integer sequenceNumber) {
		cacheImeiSequenceMap.put(imei, sequenceNumber);
	}


	private void loadDataFromImeiSequenceMap(){
		LOG.debug("Caching ImeiSequenceMap values.");
		List<ImeiSequenceMap> imeiSequenceMapList = ((ImeiSequenceMapDaoImpl)DBManager.getInstance().getDao(DAOEnum.IMEI_SEQUENCE_DAO)).selectAll();
		processCacheUpdate(imeiSequenceMapList);
		LOG.debug("Caching ImeiSequenceMap values successful");
	}

	private void processCacheUpdate(List<ImeiSequenceMap> imeiSequenceMapList) {
		if(imeiSequenceMapList != null){
			for(ImeiSequenceMap imeiSequenceMap : imeiSequenceMapList){
				cacheImeiSequenceMap.put(imeiSequenceMap.getImei(), (int)imeiSequenceMap.getSequenceNumber());
			}
		}
	}

	public void retrieveImeiSequenceMapDetailsWhenNotInCache(String imei){
		LOG.debug("Caching ImeiSequenceMap values.");
		List<ImeiSequenceMap> imeiSequenceMapList = ((ImeiSequenceMapDaoImpl)DBManager.getInstance().getDao(DAOEnum.IMEI_SEQUENCE_DAO)).selectByIMEI(imei);
		processCacheUpdate(imeiSequenceMapList);
	}
	
	public void refresh(){
		clearCache();
		loadDataFromImeiSequenceMap();
	}

	private void clearCache() {
		cacheImeiSequenceMap.clear();
	}

}
