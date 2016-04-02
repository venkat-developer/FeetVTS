package com.i10n.dbCacheManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.RouteDeviationDaoImp;
import com.i10n.db.entity.RouteDeviation;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;

/**
 * @author Dharmaraju V
 *
 */
public class LoadSASRouteDeviation {
	private static final Logger LOG = Logger.getLogger(LoadSASRouteDeviation.class);
	
	static private LoadSASRouteDeviation _instance = null;
	private LoadSASRouteDeviation(){};
	
	public static LoadSASRouteDeviation getInstance() {
		if(null == _instance){
			_instance = new LoadSASRouteDeviation();
		}
		return _instance;
	}
	
	public ConcurrentHashMap<Long, RouteDeviation> cacheSASRouteDeviation = new ConcurrentHashMap<Long, RouteDeviation>();
	
	public RouteDeviation getDetailsForSASRouteDeviation (Long routeDeviationId ){
			List<RouteDeviation> routeDeviationList = ((RouteDeviationDaoImp)DBManager.getInstance().getDao(DAOEnum.ROUTE_DEVIATIONS_DAO)).
					selectByPrimaryKey(new LongPrimaryKey(routeDeviationId));
			processCacheUpdate(routeDeviationList);
		
		return cacheSASRouteDeviation.get(routeDeviationId);
	}
	
	private void processCacheUpdate(List<RouteDeviation> routeDeviationList) {
		if(routeDeviationList != null){
			for(RouteDeviation routeDeviation : routeDeviationList){
				cacheSASRouteDeviation.put(routeDeviation.getId().getId(), routeDeviation);
			}
		}
	}

	public synchronized void removeEntriesForVehicle(Long vehicleId) {
		LOG.debug("Updating RouteDeviation violation cache ");
		for(Long deviationId : cacheSASRouteDeviation.keySet()){
			RouteDeviation deviation = cacheSASRouteDeviation.get(deviationId);
			if(deviation.getVehicleId() == vehicleId){
				cacheSASRouteDeviation.remove(deviationId);
			}
		}
	}

	/**
	 * @param routeDeviationId
	 * @return RouteDeviation entity corresponding to @param routeDeviationId
	 */
	public RouteDeviation retrieve(Long routeDeviationId) {
		RouteDeviation routeDeviation = cacheSASRouteDeviation.get(routeDeviationId);
		if(routeDeviation == null){
			routeDeviation = getDetailsForSASRouteDeviation(routeDeviationId);
		}
		return routeDeviation;
	}
}
