package com.i10n.dbCacheManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.StopsDaoImp;
import com.i10n.db.entity.Stops;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;

/**
 * @author Dharmaraju V
 *
 */
public class LoadStopsDetails {

	private static final Logger LOG = Logger.getLogger(LoadStopsDetails.class);
	static private LoadStopsDetails _instance = null;

	private LoadStopsDetails(){};

	public static LoadStopsDetails getInstance() {
		if(null == _instance){
			_instance = new LoadStopsDetails();
			_instance.loadStopsDetails();
		}
		return _instance;
	}

	public Stops retrieve(Long stopId){
		Stops stop = cacheStop.get(stopId);
		if(stop == null){
			getDetailsForNewlyAddedStop(stopId);
			return cacheStop.get(stopId);
		}
		return stop;
	}

	/**	Mapping stopId with its respective entity*/
	public ConcurrentHashMap<Long, Stops> cacheStop = new ConcurrentHashMap<Long, Stops>();

	private void loadStopsDetails (){
		LOG.debug("Caching Stop Details ");
		List<Stops> stopList = ((StopsDaoImp)DBManager.getInstance().getDao(DAOEnum.STOPS_DAO)).selectAll();
		processCacheUpdate(stopList);
	}

	private void processCacheUpdate(List<Stops> stopList) {
		if(stopList != null){
			for(Stops stop : stopList){
				cacheStop.put(stop.getId().getId(), stop);
			}
		}
	}

	public void getDetailsForNewlyAddedStop(Long stopId){
		LOG.debug("Caching newly added Stop Details ");
		List<Stops> stopList = ((StopsDaoImp)DBManager.getInstance().getDao(DAOEnum.STOPS_DAO)).selectByPrimaryKey(new LongPrimaryKey(stopId));
		processCacheUpdate(stopList);
	}

	public String toString(){
		LOG.debug("Cached Stops are ");
		for(Stops stop : cacheStop.values()){
			LOG.debug(stop.toString());
		}
		return null;
	}
	
	public void refresh() {
		clearCache();
		loadStopsDetails();
	}

	private void clearCache() {
		cacheStop.clear();
	}
}
