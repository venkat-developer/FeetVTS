package com.i10n.fleet.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.postgis.Geometry;
import org.postgis.LinearRing;
import org.postgis.Point;
import org.postgis.Polygon;

import com.i10n.db.dao.AlertDaoImpl;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.VehicleGeofenceRegionsDaoImp;
import com.i10n.db.entity.Address;
import com.i10n.db.entity.AlertOrViolation;
import com.i10n.db.entity.GeoFenceRegions;
import com.i10n.db.entity.HardwareModule;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.Trip;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.VehicleGeofenceRegions;
import com.i10n.db.entity.AlertOrViolation.AlertType;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadGeoFencingDetails;
import com.i10n.dbCacheManager.LoadHardwareModuleDetails;
import com.i10n.dbCacheManager.LoadTripDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.container.BulkUpdateDataBean;
import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.AlertEmailSMSService;
import com.i10n.fleet.util.ClientSpecificationsHandler;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.util.GeoUtils;
import com.i10n.fleet.util.InstantViolationAlerts;
import com.i10n.fleet.util.StringUtils;

/**
 * @author venkat
 *
 */
public class ViolationsHelper {

	private static Logger LOG = Logger.getLogger(ViolationsHelper.class);

	private static ViolationsHelper _instance = null;
	private static HashMap< Long,Boolean> speedAlertStatus=new HashMap<Long,Boolean>();
	private static HashMap< Long,Boolean> ccAlertStatus=new HashMap<Long,Boolean>();

	public static ViolationsHelper getInstance(){
		if (_instance == null){
			_instance = new ViolationsHelper();
		}
		return _instance;
	}

	/**
	 * Tests for alert conditions to be triggered
	 * @param dataPacket
	 */
	public List<AlertOrViolation> checkForAlerts (LiveVehicleStatus liveVehicleObject, GWTrackModuleDataBean dataPacket) {
		List<AlertOrViolation> alertTypeList = new ArrayList<AlertOrViolation>();
		Point point = new Point(dataPacket.getBulkUpdateData().get(0).getLongitude(), dataPacket.getBulkUpdateData().get(0).getLatitude());
		Geometry alertLocation = (Geometry) point;
		AlertOrViolation alert = new AlertOrViolation(liveVehicleObject.getVehicleId(), 
				liveVehicleObject.getDriverId(), dataPacket.getModuleUpdateTime(), new Date(), alertLocation);

		alert.setAlertLocationReferenceId(0L);
		Address address = null;
		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_VIOLATIONS_ENABLED"))){
			address = GeoUtils.fetchNearestLocation(alertLocation.getFirstPoint().y, 
					alertLocation.getFirstPoint().x, false);
			StringBuffer location=StringUtils.formulateAddress(address, liveVehicleObject.getVehicleId(),
					alertLocation.getFirstPoint().y, alertLocation.getFirstPoint().x);
			alert.setAlertLocationText(location.toString());
			if(address != null){
				alert.setAlertLocationReferenceId(address.getId());	
			}
		} else {
			StringBuffer latlongs = new StringBuffer();
			latlongs.append(" [Lat : "+alertLocation.getFirstPoint().y+", Lng : "+alertLocation.getFirstPoint().x+"]");
			alert.setAlertLocationText(latlongs.toString());
		}

		checkForOverSpeedAlert(dataPacket,liveVehicleObject,alert,alertTypeList);
		checkForCCAlert(dataPacket,liveVehicleObject,alert,alertTypeList);
		if(dataPacket.isPanicData()){
			LOG.info("PNIC ALERT : for IMEI : "+dataPacket.getImei());
			Vehicle vehicle=LoadVehicleDetails.getInstance().retrieve(liveVehicleObject.getVehicleId());
			HardwareModule hardwareModuledetails=LoadHardwareModuleDetails.getInstance().retrieve(vehicle.getImeiId());
			if(hardwareModuledetails.getMobileNumber() != null && !hardwareModuledetails.getMobileNumber().equals("0")){
				alert.setAlertTypeValue("Ph : "+hardwareModuledetails.getMobileNumber());	
			}else{
				alert.setAlertTypeValue("IMEI: "+dataPacket.getImei());
			}
			alert.setAlertType(AlertType.PANIC);
			alertTypeList.add(alert);
		}
		return alertTypeList;
	}
	/**
	 * Processing for Charger connected/disconnected alerts.
	 * @param alert 
	 * @param liveVehicleObject 
	 * @param dataPacket 
	 * @param alertTypeList 
	 */
	private void checkForCCAlert(GWTrackModuleDataBean dataPacket,LiveVehicleStatus liveVehicleObject, AlertOrViolation alert,
			List<AlertOrViolation> alertTypeList) {
		AlertOrViolation alertsStatus=new AlertOrViolation();
		/**
		 *  alerting charger connected status change for other than tncsc client.
		 */
		if(!ClientSpecificationsHandler.isTncscClient(liveVehicleObject.getVehicleId())){
			if (!dataPacket.isChargerConnected() && liveVehicleObject.isChargerConnected()) {
				alert.setAlertType(AlertType.CHARGER_DISCONNECTED);
				alert.setAlertTypeValue("-");
				alertTypeList.add(alert);
				LOG.debug("ALERT : Charger Disconnected Alert for Vehicle Id : "+liveVehicleObject.getVehicleId()+" at "+new Date());
			}
			else if (dataPacket.isChargerConnected() && !liveVehicleObject.isChargerConnected()) {
				alert.setAlertType(AlertType.CHARGER_CONNECTED);
				alert.setAlertTypeValue("-");
				alertTypeList.add(alert);	
				LOG.debug("ALERT : Charger Connected Alert for Vehicle Id : "+liveVehicleObject.getVehicleId()+" at "+new Date());
			}
		}
		/**
		 *  alerting tncsc client if module's voltage reduced to 70%.
		 *  1).CC alert is cached or not.
		 *  2).CC alert already sent or not.		
		 */
		if(ClientSpecificationsHandler.isTncscClient(liveVehicleObject.getVehicleId())){
			if(ccAlertStatus.get(liveVehicleObject.getVehicleId())!=null){
				/**
				 * checking module battery volatage lessthan the specified limit 
				 * consider is as charger disconnected alert for TNCSC client. 
				 */
				if(dataPacket.getModuleBatteryVoltage() < Integer.parseInt(EnvironmentInfo.getProperty("TNCSC_CLINET_MODULE_BATTERY_VOLATGE"))){
					LOG.info("TNCSC : Module Volatage is lessthan specified volatege for vehicle "+liveVehicleObject.getVehicleId());
					if(!(ccAlertStatus.get(liveVehicleObject.getVehicleId()))){
						alert.setAlertType(AlertType.CHARGER_DISCONNECTED);
						alert.setAlertTypeValue("-");
						alertTypeList.add(alert);
						alertsStatus.setAlertSent(true);
						ccAlertStatus.put(liveVehicleObject.getVehicleId(),true);
						LOG.info("ALERT : TNCSC Charger DisConnected Alert for Vehicle Id : "+liveVehicleObject.getVehicleId()+" at "+new Date());
					}else{
						LOG.info("TNCSC : but CC alert already sent for Vehicle Id : "+liveVehicleObject.getVehicleId());
					}
				}	
			}else {
				ccAlertStatus.put(liveVehicleObject.getVehicleId(),false);
			}
		}
	}

	/**
	 * Processing for Over speed.
	 * @param alert 
	 * @param liveVehicleObject 
	 * @param dataPacket 
	 * @param alertTypeList 
	 */
	private void checkForOverSpeedAlert(GWTrackModuleDataBean dataPacket, LiveVehicleStatus liveVehicleObject, AlertOrViolation alert,
			List<AlertOrViolation> alertTypeList) {
		AlertOrViolation alertsStatus=new AlertOrViolation();
		/**
		 *  Checking for overspeed violation for other than tncsc Client
		 */
		Trip trip=LoadTripDetails.getInstance().retrieve(liveVehicleObject.getTripIdLong());
		if(!ClientSpecificationsHandler.isTncscClient(liveVehicleObject.getVehicleId()) && 
				(dataPacket.getMaxSpeed() > trip.getSpeedLimit())){				//DataProcessorParameters.SPEED_LIMIT)) {
			/*	Generalize the type values*/
			/**
			 * 1).Speed alert is cached or not.
			 * 2).Speed alert already sent or not.
			 */
			if(speedAlertStatus.get(liveVehicleObject.getVehicleId())!=null){
				if(!(speedAlertStatus.get(liveVehicleObject.getVehicleId()))){
					alert.setAlertType(AlertType.OVERSPEED);		
					alert.setAlertTypeValue(dataPacket.getMaxSpeed()+"");
					alertTypeList.add(alert);
					alertsStatus.setAlertSent(true);
					/**
					 * updating cache with alert sent
					 */
					speedAlertStatus.put(liveVehicleObject.getVehicleId(),true);
					LOG.info("ALERT : speed Alert for Vehicle Id : "+liveVehicleObject.getVehicleId()+" at "+new Date());
				}else{
					LOG.info("Speed alert already sent for Vehicle Id : "+liveVehicleObject.getVehicleId());
				}
			}else{
				speedAlertStatus.put(liveVehicleObject.getVehicleId(),false);
			}
		}else {
			speedAlertStatus.put(liveVehicleObject.getVehicleId(),false);
		}

	}

	public ArrayList<AlertOrViolation> checkForGeoFencingViolation(LiveVehicleStatus liveVehicleObject,
			GWTrackModuleDataBean dataPacket, BulkUpdateDataBean bulkData) {

		ArrayList<AlertOrViolation> alertList = new ArrayList<AlertOrViolation>();

		ArrayList<GeoFenceRegions> geoFencingList = null;
		geoFencingList = LoadGeoFencingDetails.getInstance().retrieve(liveVehicleObject.getVehicleId());
		if(geoFencingList != null){
			for(GeoFenceRegions geoFencingObject : geoFencingList){
				double[] polygonX;
				double[] polygonY;
				Polygon polygon = (Polygon) geoFencingObject.getPolygon();
				if (null != geoFencingObject.getPolygon()) {
					if (geoFencingObject.getPolygon().getTypeString().equals("POINT")) {
						continue;
					} else {
					}
				}
				if (null != polygon) {
					LinearRing ring = polygon.getRing(0);
					if (null != ring) {
						Point[] points = ring.getPoints();
						polygonX = new double[points.length];
						polygonY = new double[points.length];
						for (int j = 0; j < points.length; j++) {
							polygonX[j] = points[j].x;
							polygonY[j] = points[j].y;
						}
						//checking whether geofence voilated or not
						BulkUpdateDataBean bulk=dataPacket.getBulkUpdateData().get(0);

						boolean isInsideGeoFenceRegion = isInsideGeoFenceRegion(polygonX, polygonY, points, 
								bulk.getLatitude(), bulk.getLongitude(),geoFencingObject.getShape());
						boolean isGeoFenceOccurred = false;
						LOG.info("isInsideGeoFenceRegion:"+isInsideGeoFenceRegion);
						if(!isInsideGeoFenceRegion){
							/**
							 * Vehicle Violated Geofence Regions
							 * checking weather alert sent or not  
							 */

							isGeoFenceOccurred = isViolationOccurred(geoFencingObject, liveVehicleObject);

							if(isGeoFenceOccurred){
								LOG.debug("isGeoFenceOccurred:"+isGeoFenceOccurred);
								/**
								 * alert not sent now here sending alert
								 * If geofence type is zero then it is normal geofence region need to add alert.
								 *  
								 */
								LOG.debug("Vehicle Id : "+liveVehicleObject.getVehicleId()+" is out of "+geoFencingObject.getRegionName()+" region ");
								/**
								 * 1).Check for TNCSC client.
								 * 2).Is Geofence region type is route.
								 * a).region type = 0 is normal Geofence.
								 */
								AlertOrViolation alert=InstantViolationAlerts.createAlertObject(bulkData, liveVehicleObject);
								if(geoFencingObject.getRegionType()==0){
									updateAlertObject(alert, AlertType.GEOFENCING_OUT, " Out of "+geoFencingObject.getRegionName(), geoFencingObject.getRegionName(), alertList);
									LOG.debug("ALERT : TNCSC Geofence out alert for Vehicle Id : "+liveVehicleObject.getVehicleId()+" at "+new Date());	
								}
							}
							geoFencingObject.setInsideRegion(false);
						}else{
							LOG.debug("Vehicle Id : "+liveVehicleObject.getVehicleId()+" inside its geofence region "+geoFencingObject.getRegionName());
							/**
							 * vehicle is inside any one of the regions
							 * checking is it entered just now entered geofence or not 
							 */
							isGeoFenceOccurred = isIncomingVehicleToGeoFenceArea(geoFencingObject, liveVehicleObject);
							if(isGeoFenceOccurred){
								LOG.debug("This is first time need to add geofence In violation alert for vehicle Id : "
										+liveVehicleObject.getVehicleId()+" region "+geoFencingObject.getRegionName());
									AlertOrViolation alert = InstantViolationAlerts.createAlertObject(bulkData, liveVehicleObject);
									updateAlertObject(alert, AlertType.GEOFENCING_IN, " to "+geoFencingObject.getRegionName(), geoFencingObject.getRegionName(), alertList);
									geoFencingObject.setInsideRegion(true);
									LOG.debug("ALERT : TNCSC Geofence in alert for Vehicle Id : "+liveVehicleObject.getVehicleId()+" at "+new Date());
							}
						}
					}
					LoadGeoFencingDetails.getInstance().cacheGeoFencingRecords.put(liveVehicleObject.getVehicleId(),geoFencingList);
				}
			}
		}
		LOG.debug("Finally geofence arraylist size is "+alertList.size());
		return alertList;
	}

	/**
	 * Helper method to update alert object
	 * @param alert
	 * @param alertType
	 * @param alertValue
	 * @param regionName
	 * @param alertList
	 */
	private void updateAlertObject(AlertOrViolation alert, AlertType alertType, String alertValue, String regionName, ArrayList<AlertOrViolation> alertList) {
		alert.setAlertType(alertType);
		alert.setAlertTypeValue(alertValue);
		alert.setGeofenceArea(regionName);
		alertList.add(alert);		
	}

	/**
	 * Get end point registered status for the region
	 * @param geoFencingObject
	 * @return
	 */
	private boolean isViolationOccurred(GeoFenceRegions geoFencingObject,LiveVehicleStatus liveVehicleObject) {
		// The vehicle is outside of there region
		boolean isOutVehicle = false;  
		try {
			if (geoFencingObject.isInsideRegion()) {
				/**
				 * up to now vehicle is inside the region
				 * returning violation not occured 
				 * now vehicle is out of region need to send geofence 
				 * alert to mobile and update in db as well 
				 *
				 */
				LOG.info("Checking isViolations occured for region "+geoFencingObject.getRegionName()+" for the vehicleId : "+liveVehicleObject.getVehicleId());
				geoFencingObject.setInsideRegion(false);
				isOutVehicle = true;
				VehicleGeofenceRegions vehicleGeofenceRegionsEntity = new VehicleGeofenceRegions();
				vehicleGeofenceRegionsEntity.setVehicleId(geoFencingObject.getVehicleId());
				vehicleGeofenceRegionsEntity.setRegionId(geoFencingObject.getRegionId());
				vehicleGeofenceRegionsEntity.setInsideRegion(false);
				try {
					vehicleGeofenceRegionsEntity = ((VehicleGeofenceRegionsDaoImp)DBManager.getInstance().getDao(DAOEnum.VEHICLE_GEOFENCE_REGIONS_DAO)).
							update(vehicleGeofenceRegionsEntity);
				} catch (OperationNotSupportedException e) {
					LOG.error(e);
				}

			}
		} catch (Exception e) {
			LOG.error("Error While updating the vehicle regions table",e);
		}
		return isOutVehicle;

	}

	private boolean isIncomingVehicleToGeoFenceArea(GeoFenceRegions geoFencingObject,LiveVehicleStatus liveVehicleObject) {
		boolean isIncomingVehicle = false;
		if(!geoFencingObject.isInsideRegion()){
			/**
			 * just now vehicle is entered into geofence 
			 */
			geoFencingObject.setInsideRegion(true);
			isIncomingVehicle = true;
			VehicleGeofenceRegions vehicleGeofenceRegionsEntity = new VehicleGeofenceRegions();
			vehicleGeofenceRegionsEntity.setVehicleId(geoFencingObject.getVehicleId());
			vehicleGeofenceRegionsEntity.setRegionId(geoFencingObject.getRegionId());
			vehicleGeofenceRegionsEntity.setInsideRegion(true);
			try {
				vehicleGeofenceRegionsEntity = ((VehicleGeofenceRegionsDaoImp)DBManager.getInstance().getDao(DAOEnum.VEHICLE_GEOFENCE_REGIONS_DAO)).
						update(vehicleGeofenceRegionsEntity);
			} catch (OperationNotSupportedException e) {
				LOG.error(e);
			}
		}
		return isIncomingVehicle;
	}

	private boolean isInsideGeoFenceRegion(double[] polygonX,double[] polygonY, Point[] points, double latitude, 
			double longitude,
			int shapeType) {
		LOG.info("ShapeType:"+shapeType);
		//For circle
		if(shapeType==0){
			return GeoUtils.isInsideCircleRegion(points, latitude, longitude);
		}else if (shapeType==1) {
			//For Square
			return GeoUtils.isInsideSquareRegion(points, latitude, longitude);
		}else {
			//For Polygon
			return GeoUtils.pointInPolygon(latitude, longitude, polygonX, polygonY);
		}
	}

	public static AlertOrViolation checkForTripStart(LiveVehicleStatus liveVehicleObject, BulkUpdateDataBean bulkData) {
		Point point = new Point(bulkData.getLongitude(), bulkData.getLatitude());
		Geometry alertLocation = (Geometry) point;
		AlertOrViolation alert = new AlertOrViolation(/*new LongPrimaryKey(alertId), */liveVehicleObject.getVehicleId(),
				liveVehicleObject.getDriverId(), bulkData.getOccurredAt(), new Date(), alertLocation);
		LOG.info("TNCSC : but not activated yet, adding start alert for Vehcile Id : "+liveVehicleObject.getVehicleId());
		Address address = null;
		alert.setAlertLocationReferenceId(0L);
		StringBuffer location=new StringBuffer();
		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_VIOLATIONS_ENABLED"))){
			address = GeoUtils.fetchNearestLocation(bulkData.getLatitude(),bulkData.getLongitude(),false);
			location=StringUtils.formulateAddress(address, liveVehicleObject.getVehicleId(),bulkData.getLatitude(),
					bulkData.getLongitude());
			alert.setAlertLocationText(location.toString());
			if(address != null){
				alert.setAlertLocationReferenceId(address.getId());	
			}
		}else{
			location.append("Alert ");
			location.append(StringUtils.addressFetchDisabled(liveVehicleObject.getVehicleId()
					,bulkData.getLatitude(),bulkData.getLongitude()).toString());
			alert.setAlertLocationText(location.toString());
		}
		alert.setAlertType(AlertType.TNCSC_TRIP_START);
		alert.setAlertTypeValue("-");
		LOG.info("ALERT : trip start alert added for Vehcile Id : "+liveVehicleObject.getVehicleId()+" at "+new Date());
		return alert;
	}
	/**
	 * Update the alert subscribed users 
	 * @param alertsList 
	 * 
	 * @param liveVehicleObject
	 * @param dataPacket
	 * @throws SQLException
	 */
	public static void doNecessaryAlertProcessing(List<AlertOrViolation> alertsList, LiveVehicleStatus liveVehicleObject, 
			GWTrackModuleDataBean dataPacket) throws SQLException {

		for (AlertOrViolation alert : alertsList) {
			try {
				((AlertDaoImpl)DBManager.getInstance().getDao(DAOEnum.ALERT_DAO)).insert(alert);
			} catch (OperationNotSupportedException e) {
				LOG.error(e);
			}
			AlertEmailSMSService.getInstance().notifyAlerts(alert);
		}
	}
}
