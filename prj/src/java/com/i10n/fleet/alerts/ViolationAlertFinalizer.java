package com.i10n.fleet.alerts;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.AlertDaoImpl;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.entity.AlertOrViolation;
import com.i10n.db.entity.AlertOrViolation.AlertType;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.RouteSchedule;
import com.i10n.db.entity.Stops;
import com.i10n.db.entity.VehicleToRouteSchedule;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;
import com.i10n.dbCacheManager.LoadRouteSchedule;
import com.i10n.dbCacheManager.LoadStopsDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.dbCacheManager.LoadVehicleRouteAssociationDetails;
import com.i10n.fleet.container.BulkUpdateDataBean;
import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.fleet.eta.ETAUtils;
import com.i10n.fleet.util.CustomCoordinates;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.util.InstantViolationAlerts;

/**
 * Taking care of few types of alerts to be inserted in DB 
 * @author Dharmaraju V
 *
 */
public class ViolationAlertFinalizer {

	private static Logger LOG = Logger.getLogger(ViolationAlertFinalizer.class);

	/**
	 * Vehicle on the same route with different schedule time running within a distance of 400 meters is Bunching 
	 * @param liveVehicle
	 * @param dataPacket
	 */
	//	public static void bunchingAlertProcessor(LiveVehicleStatus liveVehicle, Statement statement, GWTrackModuleDataBean dataPacket){
	public static void bunchingAlertProcessor(LiveVehicleStatus liveVehicle, GWTrackModuleDataBean dataPacket){
		//	Get the routeId of the vehicle 
		ConcurrentHashMap<VehicleToRouteSchedule,Boolean> hashMaps = 
				LoadVehicleRouteAssociationDetails.getInstance().retrieve(liveVehicle.getVehicleId());

		if(hashMaps == null)
			return;

		Long routeId = 0L;
		VehicleToRouteSchedule activeVehicleToRouteSchedule = null;
		for(VehicleToRouteSchedule vehicleToRouteSchedule : hashMaps.keySet()){
			if(hashMaps.get(vehicleToRouteSchedule)){
				routeId = vehicleToRouteSchedule.getRouteId();
				activeVehicleToRouteSchedule = vehicleToRouteSchedule;
				break;
			}
		}

		if(routeId == 0L){
			LOG.debug("No Route Active for the vehicle to get the Bunch Violation");
			return;

		}
		// Get the vehicles running on the same route as the current vehicle
		Vector<Long> vehicleList = getVehiclesOnTheSameRoute(liveVehicle.getVehicleId(), routeId);
		LOG.debug("Bunching Vehicle Alert Testing for Vehicle "
				+LoadVehicleDetails.getInstance().retrieve(liveVehicle.getVehicleId()).getDisplayName()+" and Route : "+routeId);
		for(Long vehicleid : vehicleList){
			LOG.debug("Checking for bunching with vehicle "+vehicleid);
			if(vehicleid == liveVehicle.getVehicleId()){
				continue;
			}
			LiveVehicleStatus liveVehicleStatus = LoadLiveVehicleStatusRecord.getInstance().retrieveByVehicleId(vehicleid);
			if((CustomCoordinates.distance(liveVehicleStatus.getLocation().getFirstPoint().y, liveVehicleStatus.getLocation().getFirstPoint().x,
					liveVehicle.getLocation().getFirstPoint().y	, liveVehicle.getLocation().getFirstPoint().x)*1000) <
					Integer.parseInt((EnvironmentInfo.getProperty("BUS_BUNCHING_TRIGGER_DISTANCE")))){
				LOG.debug("Distance between the vehicles is < "+(EnvironmentInfo.getProperty("BUS_BUNCHING_TRIGGER_DISTANCE"))+" mtrs");
				// Checking for vehicle to have the route active
				hashMaps = LoadVehicleRouteAssociationDetails.getInstance().retrieve(vehicleid);
				if(hashMaps != null)
					for(VehicleToRouteSchedule vehicleToRouteSchedule : hashMaps.keySet()){
						if(vehicleToRouteSchedule.getRouteId() == activeVehicleToRouteSchedule.getRouteId())
							if((hashMaps.get(vehicleToRouteSchedule) != null) && hashMaps.get(vehicleToRouteSchedule)){
								//Added logic from BhopalVisitDebugging
								if(liveVehicle.getCourse() > liveVehicleStatus.getCourse()){
									if((liveVehicle.getCourse()-liveVehicleStatus.getCourse()) > 45){
										LOG.debug("BUNCHING : Vehicle in opposite direction");
										return;
									}
								} else if((liveVehicleStatus.getCourse()-liveVehicle.getCourse()) > 45){
									LOG.debug("BUNCHING : Vehicle in opposite direction");
									return;
								} // upto here 
								LOG.debug("Bunching with vehicle "+vehicleid);
								// 	Vehicles within 400 meters distance ...
								//	Buses approaching bunching ...
								AlertOrViolation alert=InstantViolationAlerts.createAlertObject(dataPacket.getBulkUpdateData().get(0), liveVehicle);
								try {
									alert.setAlertType(AlertType.BUNCHING);
									String bunchingAlertValue = LoadVehicleDetails.getInstance().retrieve(vehicleid).getDisplayName();
									alert.setAlertTypeValue(bunchingAlertValue);
									alert.setAlertTypeValue("with "+
											LoadVehicleDetails.getInstance().retrieve(liveVehicleStatus.getVehicleId()).getDisplayName());
									((AlertDaoImpl)DBManager.getInstance().getDao(DAOEnum.ALERT_DAO)).insert(alert);
								} catch (Exception e) {
									LOG.error("Error while inserting bunching alert instance into DB",e);
								}
							}
					}
			} else {
				LOG.debug("Distance between the vehicles is greater than "+(EnvironmentInfo.getProperty("BUS_BUNCHING_TRIGGER_DISTANCE"))
						+" meters with vehicle "+vehicleid);
			}
		}

	}

	/**
	 * 
	 * @param vehicleId
	 * @param routeId
	 * @return List of vehicle running on the same route as the requested vehicle
	 */
	private  static Vector<Long> getVehiclesOnTheSameRoute(long vehicleId, long routeId) {
		Vector<Long> vehiclesOnTheSameRoute = new Vector<Long>();
		ConcurrentHashMap<Long,ConcurrentHashMap<VehicleToRouteSchedule,Boolean>> cacheVehicleToRouteAssociation = 
				LoadVehicleRouteAssociationDetails.getInstance().cacheVehicleToRouteAssociation;
		for(Long vehicleIdFromHashMap : cacheVehicleToRouteAssociation.keySet()){
			ConcurrentHashMap<VehicleToRouteSchedule,Boolean> routeStatusMap = cacheVehicleToRouteAssociation.get(vehicleIdFromHashMap);
			for(VehicleToRouteSchedule vehicleToRouteSchedule : routeStatusMap.keySet()){
				if(vehicleToRouteSchedule.getRouteId() == routeId){
					if(vehicleId != vehicleIdFromHashMap){
						LOG.debug("Vehicle on the Same Route as "+vehicleId+" is : "+vehicleIdFromHashMap);
						if(!vehiclesOnTheSameRoute.contains(vehicleIdFromHashMap))
							vehiclesOnTheSameRoute.add(vehicleIdFromHashMap);
					}
				}
			}
		}
		return vehiclesOnTheSameRoute;
	}

	/**
	 * Processing the packet for Idle alert for BMC client
	 * @param liveVehicleObject
	 * @param alertsList
	 * @param bulkData
	 */
	public static void idleAlertProcessor(LiveVehicleStatus liveVehicleObject, List<AlertOrViolation> alertsList, BulkUpdateDataBean bulkData) {
		// 	Checking for Idle alert
		if(liveVehicleObject.isIdleRowActive()){
			if(!(liveVehicleObject.getPingCount() > 1)){
				// 	Get to know whether the vehicle is at its start/end points
				if(isVehicleAtEndPoint(liveVehicleObject, bulkData)){
					LOG.debug("Vehicle at End Points. Idle alert calculation postponed");
					// Not processing for alert as the vehicle is not idle for more time at end points
					return;
				}
				LOG.debug("Vehicle Idle at non end point");
				processAlertInsertion(liveVehicleObject, alertsList, bulkData);

			} else {
				LOG.debug("PingCount greater than 1. Hence checking for end point Idle alert");
				if(liveVehicleObject.getIdleDuration() > Integer.parseInt((EnvironmentInfo.getProperty("END_POINT_IDLE_TRIGGER_DURATION_SECS")))){
					LOG.debug("Idle duration exceeding the trigger");
					//	Get to know whether the vehicle is at its start/end points
					if(isVehicleAtEndPoint(liveVehicleObject, bulkData)){
						LOG.debug("Issuing Idle alert at End Point for vehicle : "+
								LoadVehicleDetails.getInstance().retrieve(liveVehicleObject.getVehicleId()).getDisplayName());
						processAlertInsertion(liveVehicleObject, alertsList, bulkData);
					} else {
						LOG.debug("Vehicle not at any End point");
					}
				} else {
					LOG.debug("Idle duration not exceeding the trigger");
				}
			}
		}

	}

	/**
	 * Formulate the alert and queue it for insertion
	 * @param liveVehicleObject
	 * @param alertsList
	 * @param bulkData
	 */
	private static void processAlertInsertion(LiveVehicleStatus liveVehicleObject, List<AlertOrViolation> alertsList, 
			BulkUpdateDataBean bulkData) {
		StringBuffer alertTypeValue=DateUtils.dateFormatForIdleAlert(liveVehicleObject);
		AlertOrViolation alert=InstantViolationAlerts.createAlertObject(bulkData, liveVehicleObject);
		alert.setAlertType(AlertType.IDLE);
		alert.setAlertTypeValue(alertTypeValue.toString());
		alertsList.add(alert);
	}

	public static boolean isVehicleAtEndPoint(LiveVehicleStatus liveVehicleObject, BulkUpdateDataBean bulkData) {

		float vehicleEndPointDistanceLimit = 0.10f;		// Limiting the radius to 100 meters
		try{
			LOG.debug("Checking for Vehicle to be at end points");
			ConcurrentHashMap<VehicleToRouteSchedule, Boolean> routeHashMapList = 
					LoadVehicleRouteAssociationDetails.getInstance().retrieve(liveVehicleObject.getVehicleId());
			if(routeHashMapList != null){
				LOG.debug("RouteHashmap list retrieved for vehicle : "+liveVehicleObject.getVehicleId()+" and the size is : "+routeHashMapList.size());
				for(VehicleToRouteSchedule vehicleToRouteSchedule : routeHashMapList.keySet()){
					LOG.debug("Idle : RouteID : "+vehicleToRouteSchedule.getRouteId());
					List<Stops> firstStopsList = getFirstStopList(vehicleToRouteSchedule);
					List<Stops> lastStopsList = getLastStopList(vehicleToRouteSchedule);
					LOG.debug("Checking with first stops");
					for(Stops stop : firstStopsList){
						if(CustomCoordinates.distance(stop.getLatPoint(), stop.getLonPoint(), 
								bulkData.getLatitude(), bulkData.getLongitude()) < 
								(vehicleEndPointDistanceLimit/* vehicle within 100 meters*/)){
							LOG.debug("Vehicle at first stop");
							return true;
						}
					}
					LOG.debug("Checking with last stops");
					for(Stops stop : lastStopsList){
						if(CustomCoordinates.distance(stop.getLatPoint(), stop.getLonPoint(), 
								bulkData.getLatitude(), bulkData.getLongitude()) < 
								(vehicleEndPointDistanceLimit/* vehicle within 100 meters*/)){
							LOG.debug("Vehicle at last stop");
							return true;
						}
					}
				}
			}
		} catch (Exception e){
			LOG.error("Error while checking for End point idling",e);
		}
		return false;
	}

	private static List<Stops> getLastStopList(VehicleToRouteSchedule vehicleToRouteSchedule) {
		List<Stops> lastStops = new ArrayList<Stops>();
		try{
			List<Long> stopIds = new ArrayList<Long>();
			LOG.debug("Getting laststops for vehicle : "+vehicleToRouteSchedule.getVehicleId()
					+" and route : "+vehicleToRouteSchedule.getRouteId());
			String routeScheduleId = ETAUtils.getRouteScheduleId(vehicleToRouteSchedule);
			Vector<RouteSchedule> routeScheduleList = LoadRouteSchedule.getInstance().retrieve(routeScheduleId);
			int lastSeqNo = getLastSeqNoForTheRoute(routeScheduleList);
			for(RouteSchedule routeSchedule : routeScheduleList){
				if(routeSchedule.getSequenceNumber() == lastSeqNo){
					Stops lastStop = LoadStopsDetails.getInstance().retrieve(routeSchedule.getStopId());
					LOG.debug("Last stop got is : "+routeSchedule.getStopId()+" stopname : "+lastStop.getStopName());
					if(!stopIds.contains(lastStop.getId().getId())){
						stopIds.add(lastStop.getId().getId());
						lastStops.add(lastStop);
					}
				}
			}
		} catch (Exception e){
			LOG.error("Error while getting last stop list",e);
			return new ArrayList<Stops>();
		}
		return lastStops;
	}

	private static int getLastSeqNoForTheRoute(Vector<RouteSchedule> routeScheduleList) {
		int lastSeqNo = 1;
		for(RouteSchedule routeSchedule : routeScheduleList){
			if(routeSchedule.getSequenceNumber() > lastSeqNo){
				lastSeqNo = routeSchedule.getSequenceNumber();
			}
		}
		return lastSeqNo;
	}

	private static List<Stops> getFirstStopList(VehicleToRouteSchedule vehicleToRouteSchedule) {
		List<Stops> firstStops = new ArrayList<Stops>();
		try{
			List<Long> stopIds = new ArrayList<Long>();
			LOG.debug("Getting firstStops for vehicle : "+vehicleToRouteSchedule.getVehicleId()
					+" and route : "+vehicleToRouteSchedule.getRouteId());
			String routeScheduleId = ETAUtils.getRouteScheduleId(vehicleToRouteSchedule);
			Vector<RouteSchedule> routeScheduleList = LoadRouteSchedule.getInstance().retrieve(routeScheduleId);
			for(RouteSchedule routeSchedule : routeScheduleList){
				if(routeSchedule.getSequenceNumber() == 1){
					Stops firstStop = LoadStopsDetails.getInstance().retrieve(routeSchedule.getStopId());
					LOG.debug("First Stop got is : "+routeSchedule.getStopId()+" stopname : "+firstStop.getStopName());
					if(!stopIds.contains(firstStop.getId().getId())){
						stopIds.add(firstStop.getId().getId());
						firstStops.add(firstStop);
					}
				}
			}
		} catch (Exception e){
			LOG.error("Error while getting first stop list",e);
			return new ArrayList<Stops>();
		}
		return firstStops;
	}

}
