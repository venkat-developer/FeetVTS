package com.i10n.dbCacheManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.RoutesDaoImp;
import com.i10n.db.entity.Routes;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;

/**
 * @author Dharmaraju V
 *
 */
public class LoadRoutesDetails {

	private static final Logger LOG = Logger.getLogger(LoadRoutesDetails.class);
	static private LoadRoutesDetails _instance = null;

	private LoadRoutesDetails(){};

	public static LoadRoutesDetails getInstance() {
		if(null == _instance){
			_instance = new LoadRoutesDetails();
			_instance.loadRoutesData();
		}
		return _instance;
	}

	public Routes retrieve(Long routeId){
		Routes route = cacheRoute.get(routeId);
		if(route == null){
			getDetailsForNewlyAddedRoute(routeId);
			return cacheRoute.get(routeId);
		}
		return route;
	}

	/**	Mapping routeId with its entity*/
	public ConcurrentHashMap<Long, Routes> cacheRoute = new ConcurrentHashMap<Long, Routes>();

	private void loadRoutesData (){
		LOG.debug("Caching Route Details ..... ");
		List<Routes> routeList = ((RoutesDaoImp)DBManager.getInstance().getDao(DAOEnum.ROUTES_DAO)).selectAll();
		LOG.debug("In loadRoutesData Got route List "+routeList);
		processCacheUpdate(routeList);
		LOG.debug("Caching Route Details Successful..... ");
	}

	private void processCacheUpdate(List<Routes> routeList) {
		LOG.debug("In processing processCacheUpdate routeList is "+routeList);
		if(routeList != null){
			for(Routes route : routeList){
				LOG.debug("Route Id is "+route.getId().getId()+" route is "+route.toString());
				cacheRoute.put(route.getId().getId(), route);
			}
		}
	}

	public void getDetailsForNewlyAddedRoute(Long routeId){
			List<Routes> routeList = ((RoutesDaoImp)DBManager.getInstance().getDao(DAOEnum.ROUTES_DAO)).selectByPrimaryKey(new LongPrimaryKey(routeId));
			processCacheUpdate(routeList);
	}

	public String toString(){
		LOG.debug("Cached Routes are");
		for(Routes route: cacheRoute.values()){
			LOG.debug(route.toString());
		}
		return "";
	}
	
	public void refresh() {
		clearCache();
		loadRoutesData();
	}

	private void clearCache() {
		cacheRoute.clear();
	}

}
