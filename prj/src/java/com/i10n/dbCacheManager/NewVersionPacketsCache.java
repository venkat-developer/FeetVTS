package com.i10n.dbCacheManager;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.fleet.container.GWTrackModuleDataBean;

/**
 * Saving data packets in new version modules when more to follow bit is set.
 * @author dharmaraju
 *
 */
public class NewVersionPacketsCache {
	
    private final static Logger LOG = Logger.getLogger(NewVersionPacketsCache.class);
	
	/**	Cache to maintain the bulk packets corresponding to the IMEI before pushing to DB when more to follow bit is set*/
	public ConcurrentHashMap<String, GWTrackModuleDataBean> newVersionPacketsCache;
	
	private static NewVersionPacketsCache _instance = null;
	
	public NewVersionPacketsCache(){
		newVersionPacketsCache = new ConcurrentHashMap<String, GWTrackModuleDataBean>();
	}
	
	public static NewVersionPacketsCache getInstance(){
		if(_instance == null ){
			_instance = new NewVersionPacketsCache();
		}
		return _instance;
	}
	
	public GWTrackModuleDataBean retrieve(String IMEI){
		return newVersionPacketsCache.get(IMEI);
	}
	
	public void store(String IMEI, GWTrackModuleDataBean gwTrackModuleDataBean){
		newVersionPacketsCache.put(IMEI, gwTrackModuleDataBean);
		LOG.error("More To Follow Packets waiting in cache "+newVersionPacketsCache.size());
	}

}
