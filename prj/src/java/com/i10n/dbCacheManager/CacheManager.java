package com.i10n.dbCacheManager;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import com.i10n.fleet.util.EnvironmentInfo;

/**
 * Manager for Cache load and Cache refresh management 
 * @author DVasudeva
 *
 */
public class CacheManager {
	
	private static Logger LOG = Logger.getLogger(CacheManager.class);
	
	private static final long FUNDAMENTAL_CACHE_REFRESH_DURATION_MS = Long.valueOf(EnvironmentInfo.getProperty("FUNDAMENTAL_CACHE_REFRESH_DURATION_MS"));
	private static final long ALERTS_CACHE_REFRESH_DURATION_MS = Long.valueOf(EnvironmentInfo.getProperty("ALERTS_CACHE_REFRESH_DURATION_MS"));
	private static final long ETA_CACHE_REFRESH_DURATION_MS = Long.valueOf(EnvironmentInfo.getProperty("ETA_CACHE_REFRESH_DURATION_MS"));
	private static final long OTHERS_CACHE_REFRESH_DURATION_MS = Long.valueOf(EnvironmentInfo.getProperty("OTHERS_CACHE_REFRESH_DURATION_MS"));

	public static void loadCache() {
		loadFundamentalCacheList();
		LOG.info("Loaded loadFundamentalCacheList");
		loadAlertsCacheList();
		LOG.info("Loaded loadAlertsCacheList");
		//loadETACacheList();
		
		loadOtherCacheList();
		LOG.info("Loaded loadAlertsCacheList");
		refreshCache();
	}
	
	private static void refreshCache() {
		initializeFundamentalCacheTimerTask();
		
		initializeAlertCacheTimerTask();
		
		initializeETACacheTimerTask();
		
		initializeOtherCacheTimerTask();
		
	}

	private static void initializeFundamentalCacheTimerTask() {
		TimerTask refreshFundamentalData = new TimerTask() {			
			@Override
			public void run() {
				LOG.debug("Timertask refreshing Fundamental Cache list at "+new Date());
				refreshFundamentalCacheList();
			}
		};
		Timer refreshFundamentalTimer = new Timer();
		refreshFundamentalTimer.scheduleAtFixedRate(refreshFundamentalData, FUNDAMENTAL_CACHE_REFRESH_DURATION_MS, FUNDAMENTAL_CACHE_REFRESH_DURATION_MS);
		
	}

	private static void initializeAlertCacheTimerTask() {
		TimerTask refreshAlertData = new TimerTask() {			
			@Override
			public void run() {
				LOG.debug("Timertask refreshing Alert Cache list at "+new Date());
				refreshAlertsCacheList();
			}
		};
		Timer refreshAlertTimer = new Timer();
		refreshAlertTimer.scheduleAtFixedRate(refreshAlertData, ALERTS_CACHE_REFRESH_DURATION_MS, ALERTS_CACHE_REFRESH_DURATION_MS);
		
	}

	private static void initializeETACacheTimerTask() {
		TimerTask refreshETAData = new TimerTask() {			
			@Override
			public void run() {
				LOG.debug("Timertask refreshing ETA Cache list at "+new Date());
				refreshETACacheList();
			}
		};
		Timer refreshETATimer = new Timer();
		refreshETATimer.scheduleAtFixedRate(refreshETAData, ETA_CACHE_REFRESH_DURATION_MS, ETA_CACHE_REFRESH_DURATION_MS);
		
	}

	private static void initializeOtherCacheTimerTask() {
		TimerTask refreshOtherCacheData = new TimerTask() {
			@Override
			public void run() {
				LOG.debug("Timertask refreshing Other Cache list at "+new Date());
				refreshOtherCacheList();
			}
		};
		Timer refreshOtherCacheTimer = new Timer();
		refreshOtherCacheTimer.scheduleAtFixedRate(refreshOtherCacheData, OTHERS_CACHE_REFRESH_DURATION_MS, OTHERS_CACHE_REFRESH_DURATION_MS);
		
	}

	private static void loadFundamentalCacheList() {
		//LoadHardwareModuleDetails.getInstance();
		LoadUserDetails.getInstance();
		LOG.debug("Loading cache provider for Group Details");
		LoadGroupDetails.getInstance();
		LoadDriverDetails.getInstance();
		LOG.debug("Loading cached providers of LoadDriverDetails");
		LoadVehicleDetails.getInstance();
		LoadTripDetails.getInstance();
		LOG.debug("Loading cached providers of LoadVehicleDetails");
		LoadLiveVehicleStatusRecord.getInstance();
		LOG.debug("Loading cached providers of LoadLiveVehicleStatusRecord");

		// Loading acl tables into cache
		LOG.debug("Loading cache provider of aclvehicle and acldrivers");
		LoadAclVehicleDetails.getInstance();
		LoadAclDriverDetails.getInstance();
		
	}
	
	public static void refreshFundamentalCacheList() {
		LOG.debug("Refreshing Fundamental cache");
		
		LOG.debug("Refreshing HardwareModuleDetails");
		LoadHardwareModuleDetails.getInstance().refresh();
		LOG.debug("Successfully refreshed HardwareModuleDetails");
		LOG.debug("Refreshing UserDetails");
		LoadUserDetails.getInstance().refresh();
		LOG.debug("Successfully refreshed UserDetails");
		LOG.debug("Refreshing GroupDetails");
		LoadGroupDetails.getInstance().refresh();
		LOG.debug("Successfully refreshed GroupDetails");
		LOG.debug("Refreshing DriverDetails");
		LoadDriverDetails.getInstance().refresh();
		LOG.debug("Successfully refreshed DriverDetails");
		LOG.debug("Refreshing VehicleDetails");
		LoadVehicleDetails.getInstance().refresh();
		LOG.debug("Successfully refreshed VehicleDetails");
		LOG.debug("Refreshing TripDetails");
		LoadTripDetails.getInstance().refresh();
		LOG.debug("Successfully refreshed TripDetails");
		LOG.debug("Refreshing LiveVehicleStatusRecord");
		LoadLiveVehicleStatusRecord.getInstance().refresh();
		LOG.debug("Successfully refreshed LiveVehicleStatusRecord");

		// Loading acl tables into cache
		LOG.debug("Loading cache provider of aclvehicle and acldrivers");
		LOG.debug("Refreshing AclVehicleDetails");
		LoadAclVehicleDetails.getInstance().refresh();
		LOG.debug("Successfully refreshed AclVehicleDetails");
		LOG.debug("Refreshing AclDriverDetails");
		LoadAclDriverDetails.getInstance().refresh();
		LOG.debug("Successfully refreshed AclDriverDetails");
		
	}

	private static void loadAlertsCacheList() {
		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_GEOFENCING_ENABLED"))){
			LoadGeoFencingDetails.getInstance();
		}
		LOG.debug("Loading cache providers for violation alerts");
		LoadMailingAlertUserDetails.getInstance();
		LoadSMSAlertUserDetails.getInstance();
		LoadAclAlertDetails.getInstance();
		LoadAclMobileDetails.getInstance();
	}
	
	public static void refreshAlertsCacheList() {
		LOG.debug("Refreshing Alerts cache");
		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_GEOFENCING_ENABLED"))){
			LOG.debug("Refreshing GeoFencingDetails");
			LoadGeoFencingDetails.getInstance().refresh();
			LOG.debug("Successfully refreshed GeoFencingDetails");
		} else {
			LOG.debug("Not an Geofencing client");
		}
		LOG.debug("Loading cache providers for violation alerts");
		LOG.debug("Refreshing MailingAlertUserDetails");
		LoadMailingAlertUserDetails.getInstance().refresh();
		LOG.debug("Successfully refreshed MailingAlertUserDetails");
		LOG.debug("Refreshing SMSAlertUserDetails");
		LoadSMSAlertUserDetails.getInstance().refresh();
		LOG.debug("Successfully refreshed SMSAlertUserDetails");
		LOG.debug("Refreshing AclAlertDetails");
		LoadAclAlertDetails.getInstance().refresh();
		LOG.debug("Successfully refreshed AclAlertDetails");
		LOG.debug("Refreshing AclMobileDetails");
		LoadAclMobileDetails.getInstance().refresh();
		LOG.debug("Successfully refreshed AclMobileDetails");
	}

	private static void loadETACacheList() {
		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ETA_ENABLED"))){
			/**	Caching ETA related Details*/
			LoadRoutesDetails.getInstance();
			LoadStopsDetails.getInstance();
			LoadActiveStopHistoryDetails.getInstance();
			LoadVehicleRouteAssociationDetails.getInstance();
			LoadRouteSchedule.getInstance();
			LoadActiveVehicleStopHistoryDetails.getInstance();
			LoadEtaDisplayDetails.getInstance();
			LoadRouteTrackDetails.getInstance();
			// As of now Violations and Student alerts are disabled.
			/*if(DataProcessorParameters.IS_SAS_VIOLATIONS_ENABLED){
				LoadSASRouteDeviation.getInstance();
				LoadSASStopDeviation.getInstance();
				LoadSASTimeDeviations.getInstance();
			}
			if(DataProcessorParameters.IS_STUDENT_CLIENT){
				LoadStudentHistoryDetails.getInstance();
				LoadStudentsDetails.getInstance();
				LoadVehicleStudentAssociationDetails.getInstance();
				LoadActiveVehicleStudentHistoryDetails.getInstance();
			}*/

			// This is for LED Data push
			LoadLEDToBusStopMap.getInstance();
		}
	}
	
	public static void refreshETACacheList() {
		LOG.debug("Refreshing ETA cache");
		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ETA_ENABLED"))){
			/**	Caching ETA related Details*/
			LOG.debug("Refreshing RoutesDetails");
			LoadRoutesDetails.getInstance().refresh();
			LOG.debug("Successfully refreshed RoutesDetails");
			LOG.debug("Refreshing StopsDetails");
			LoadStopsDetails.getInstance().refresh();
			LOG.debug("Successfully refreshed StopsDetails");
			LOG.debug("Refreshing ActiveStopHistory");
			LoadActiveStopHistoryDetails.getInstance().refresh();
			LOG.debug("Successfully refreshed ActiveStopHistory");
			LOG.debug("Refreshing VehicleRouteAssociation");
			LoadVehicleRouteAssociationDetails.getInstance().refresh();
			LOG.debug("Successfully refreshed VehicleRouteAssociation");
			LOG.debug("Refreshing RouteSchedule");
			LoadRouteSchedule.getInstance().refresh();
			LOG.debug("Successfully refreshed RouteSchedule");
			LOG.debug("Refreshing ActiveVehicleStopHistoryDetails");
			LoadActiveVehicleStopHistoryDetails.getInstance().refresh();
			LOG.debug("Successfully refreshed ActiveVehicleStopHistoryDetails");
			LOG.debug("Refreshing EtaDisplayDetails");
			LoadEtaDisplayDetails.getInstance().refresh();
			LOG.debug("Successfully refreshed EtaDisplayDetails");
			LOG.debug("Refreshing RouteTrackDetails");
			LoadRouteTrackDetails.getInstance().refresh();
			LOG.debug("Successfully refreshed RouteTrackDetails");
			// As of now Violations and Student alerts are disabled.
			/*if(DataProcessorParameters.IS_SAS_VIOLATIONS_ENABLED){
				LoadSASRouteDeviation.getInstance();
				LoadSASStopDeviation.getInstance();
				LoadSASTimeDeviations.getInstance();
			}
			if(DataProcessorParameters.IS_STUDENT_CLIENT){
				LoadStudentHistoryDetails.getInstance();
				LoadStudentsDetails.getInstance();
				LoadVehicleStudentAssociationDetails.getInstance();
				LoadActiveVehicleStudentHistoryDetails.getInstance();
			}*/
		} else {
			LOG.debug("Not an ETA client");
		}
	}

	private static void loadOtherCacheList() {
//		LoadProxyDetails.getInstance();
		
		LoadImeiSequenceMap.getInstance();
		NewVersionPacketsCache.getInstance();
		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_FRS_EMRI_CLIENT"))){
			LoadEmriVehiclesBaseStationDetails.getInstance();
		}
	}
	
	public static void refreshOtherCacheList() {
		LOG.debug("Refreshing Others cache");
		LOG.debug("Refreshing ImeiSequenceMap");
		LoadImeiSequenceMap.getInstance().refresh();
		LOG.debug("Successfully refreshed ImeiSequenceMap");
		
		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_FRS_EMRI_CLIENT"))){
			LOG.debug("Refreshing EmriVehiclesBaseStation");
			LoadEmriVehiclesBaseStationDetails.getInstance().refresh();
			LOG.debug("Successfully refreshed EmriVehiclesBaseStation");
		} else{
			LOG.debug("Not an FRS Client");
		}
	}

}