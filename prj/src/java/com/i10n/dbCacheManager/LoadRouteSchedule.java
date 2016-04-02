package com.i10n.dbCacheManager;

import java.sql.Time;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.RouteScheduleDaoImpl;
import com.i10n.db.entity.RouteSchedule;
import com.i10n.db.tools.DBManager;

/**
 * @author Dharmaraju V
 *
 */
public class LoadRouteSchedule {

	private static final Logger LOG = Logger.getLogger(LoadRouteSchedule.class);
	private static LoadRouteSchedule _instance = null;


	public ConcurrentHashMap<String, Vector<RouteSchedule>> cacheRouteSchedule= new  ConcurrentHashMap<String, Vector<RouteSchedule>>();

	private LoadRouteSchedule(){};

	public static LoadRouteSchedule getInstance() {
		if(null == _instance){
			_instance = new LoadRouteSchedule();
			_instance.loadVehicleRouteStopDetails();
		}
		return _instance;
	}

	private void loadVehicleRouteStopDetails (){
		LOG.debug("Caching RouteSchedule details ...");
		/* 
		 * In this we are fetching the data from the Database and then we will store all the data into the HashMap so that we don't hit
		 * Database again and again.. 
		 */
		/*
		 *1) Get Vehicle to Route mapping
		 *2) Fetch the complete route schedule details
		 *3) Map the vehicle to route schedule  
		 */

		List<RouteSchedule> routeScheduleList = ((RouteScheduleDaoImpl)DBManager.getInstance().getDao(DAOEnum.ROUTE_SCHEDULE_DAO)).selectAll();
		processCacheUpdate(routeScheduleList);

	}

	@SuppressWarnings("deprecation")
	private void processCacheUpdate(List<RouteSchedule> routeScheduleList) {
		if(routeScheduleList != null){
			Vector<RouteSchedule>  scheduleList = null;
			for(RouteSchedule routeSchedule : routeScheduleList){
				String routeScheduleKey = new String();
				Time time = routeSchedule.getExpectedTime();
				time.setSeconds(0);
				routeSchedule.setExpectedTime(time); 
				routeScheduleKey = routeSchedule.getRouteScheduleId();

				if(cacheRouteSchedule.containsKey(routeScheduleKey)){
					scheduleList = cacheRouteSchedule.get(routeScheduleKey);
					scheduleList.add(routeSchedule);
					cacheRouteSchedule.put(routeScheduleKey, scheduleList);
				} else{
					scheduleList = new Vector<RouteSchedule>();
					scheduleList.add(routeSchedule);
					cacheRouteSchedule.put(routeScheduleKey, scheduleList);
				}
			}
		}
	}

	public Vector<RouteSchedule> retrieve(String routeScheduleId){
		Vector<RouteSchedule> routeScheduleList = cacheRouteSchedule.get(routeScheduleId);
		if(routeScheduleList == null){
			getDetailsforNewlyAddedVehicleRouteStops(routeScheduleId);
			routeScheduleList = cacheRouteSchedule.get(routeScheduleId);
		}
		if( routeScheduleList !=null ) {
			// Testing after reloading from DB
			if (routeScheduleList.size() == 0){
				cacheRouteSchedule.remove(routeScheduleId);
				return null;
			}
		}
		LOG.debug("Returning RouteSchedulelist of size : "+routeScheduleList.size());
		return routeScheduleList;
	}

	public void getDetailsforNewlyAddedVehicleRouteStops (String routeScheduleId){
		List<RouteSchedule> routeScheduleList = ((RouteScheduleDaoImpl)DBManager.getInstance().getDao(DAOEnum.ROUTE_SCHEDULE_DAO)).
				selectByRouteScheduleId(routeScheduleId);
		processCacheUpdate(routeScheduleList);

	}

	public String toString(){
		for(String key: cacheRouteSchedule.keySet()){
			for(RouteSchedule routeSchedule : cacheRouteSchedule.get(key)){
				LOG.debug(routeSchedule.toString());
			}
		}
		return null;
	}
	
	public void refresh() {
		clearCache();
		loadVehicleRouteStopDetails();
	}

	private void clearCache() {
		cacheRouteSchedule.clear();
	}
}
