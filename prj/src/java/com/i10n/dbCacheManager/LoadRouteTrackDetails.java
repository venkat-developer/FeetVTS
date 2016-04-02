package com.i10n.dbCacheManager;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.RouteTrackDaoImp;
import com.i10n.db.entity.RouteTrack;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.EnvironmentInfo;

/**
 * @author Dharmaraju V
 *
 */
public class LoadRouteTrackDetails {
	private static final Logger LOG = Logger.getLogger(LoadRouteTrackDetails.class);

	static private LoadRouteTrackDetails _instance = null;
	
	private static final Long ROUTE_TRACK_CACHE_REFRESH_DURATION_MS = Long.valueOf(EnvironmentInfo.getProperty("ROUTE_TRACK_CACHE_REFRESH_DURATION_MS"));

	private LoadRouteTrackDetails(){
		removeOldEntriesFromDB();
		loadRouteTrackData();
		TimerTask refreshData = new TimerTask() {			
			@Override
			public void run() {
				LOG.debug("Timertask refreshing RouteTrack list at "+new Date());
				refresh();
			}
		};
		Timer timer = new Timer();
	    timer.scheduleAtFixedRate(refreshData, ROUTE_TRACK_CACHE_REFRESH_DURATION_MS, ROUTE_TRACK_CACHE_REFRESH_DURATION_MS);
	};
	
	public static LoadRouteTrackDetails getInstance() {
		if(null == _instance){
			_instance = new LoadRouteTrackDetails();
		}
		return _instance;
	} 

	private void removeOldEntriesFromDB() {
			LOG.debug("Deleteing older entries from DB");
			Calendar threeDaysBack = Calendar.getInstance();
			threeDaysBack.add(Calendar.DATE, 3);
			String sqlQueryForRouteTrackData = "delete from routetrack where startdate < '"+DateUtils.convertJavaDateToSQLDate(threeDaysBack.getTime())+"' and endtrackhistoryid != 0";
			LOG.debug("Deleted routetrack entires which are older by 3 days "+sqlQueryForRouteTrackData);
			((RouteTrackDaoImp)DBManager.getInstance().getDao(DAOEnum.ROUTE_TRACK_DAO)).deleteOldEntries(threeDaysBack.getTime());
	}

	public Vector<RouteTrack> retrieve(Long routeId){
		Vector<RouteTrack> routeTrackList = cacheRouteTrack.get(routeId);
		if(routeTrackList == null){
			getLatestRouteTrackData(routeId);
			return cacheRouteTrack.get(routeId);
		}else if (routeTrackList.size() == 0){
			cacheRouteTrack.remove(routeId);
			return null;
		}
		return routeTrackList;
	}

	/** Mapping of routeId to the list of routetrack details from the table.*/
	public ConcurrentHashMap<Long, Vector<RouteTrack>> cacheRouteTrack = new ConcurrentHashMap<Long, Vector<RouteTrack>>();

	private void loadRouteTrackData (){
		LOG.debug("Caching RouteTrack details ...");
		List<RouteTrack> routeTrackList = ((RouteTrackDaoImp)DBManager.getInstance().getDao(DAOEnum.ROUTE_TRACK_DAO)).selectAll();
		processCacheUpdate(routeTrackList);
		LOG.debug("Caching RouteTrack details Successful...");
	}

	private void processCacheUpdate(List<RouteTrack> routeTrackList) {
		if(routeTrackList != null){
			Vector<RouteTrack> routeTracks = null;
			for(RouteTrack routeTrack: routeTrackList){
				Long routeId = routeTrack.getRouteId();
				if(cacheRouteTrack.containsKey(routeId)){
					routeTracks = cacheRouteTrack.get(routeId);
					routeTracks.add(routeTrack);
					cacheRouteTrack.put(routeId, routeTracks);
				}else{
					routeTracks = new Vector<RouteTrack>();
					routeTracks.add(routeTrack);
					cacheRouteTrack.put(routeId, routeTracks);
				}
			}
		}
	}

	public void getLatestRouteTrackData (Long routeId){
		LOG.debug("Caching new RouteTrack details");
		List<RouteTrack> routeTrackList = ((RouteTrackDaoImp)DBManager.getInstance().getDao(DAOEnum.ROUTE_TRACK_DAO)).selectByRouteId(routeId);
		processCacheUpdate(routeTrackList);
		LOG.debug("Caching new RouteTrack details Successful");
	}

	public String toString(){
		LOG.debug("Cached RouteTrack details are ");
		for(Long routeId : cacheRouteTrack.keySet()){
			LOG.debug("RouteId = "+routeId);
			for(RouteTrack routeTrack : cacheRouteTrack.get(routeId)){
				LOG.debug(routeTrack.toString());
			}
		}
		return null;
	}
	
	public void refresh() {
		removeOldEntriesFromDB();
		clearCache();
		loadRouteTrackData();
	}

	private void clearCache() {
		cacheRouteTrack.clear();
	}
}
