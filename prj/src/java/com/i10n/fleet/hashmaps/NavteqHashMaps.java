/*package com.i10n.fleet.hashmaps;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.i10n.navteq.NavteqIMEIData;

public class NavteqHashMaps {
	
	public static NavteqHashMaps _instance = null;
	
		Caching per day data for IMEI-VID pair
	public ConcurrentHashMap<String, ArrayList<NavteqIMEIData>> imeiDataMap = new 
		ConcurrentHashMap<String, ArrayList<NavteqIMEIData>>();
	
	public static NavteqHashMaps getInstance(){
		if(_instance == null){
			_instance = new NavteqHashMaps();
		}
		return _instance;
	}
	
}
*/