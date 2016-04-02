package com.i10n.fleet.util;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.VehicleDaoImpl;
import com.i10n.db.entity.AlertOrViolation;
import com.i10n.db.entity.AlertOrViolation.AlertType;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.MobileNumber;
import com.i10n.db.entity.User;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadAclDriverDetails;
import com.i10n.dbCacheManager.LoadAclMobileDetails;
import com.i10n.dbCacheManager.LoadAclVehicleDetails;
import com.i10n.dbCacheManager.LoadSMSAlertUserDetails;
import com.i10n.dbCacheManager.LoadUserDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.alerts.ViolationAlertFinalizer;
import com.i10n.fleet.container.BulkUpdateDataBean;
import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.hashmaps.EMRIHashMaps;
import com.i10n.fleet.hashmaps.EMRIHashMaps.ButtonCode;
import com.i10n.fleet.helper.ViolationsHelper;
import com.i10n.fleet.providers.mock.EtaDisplayDataHelper;
import com.i10n.mina.utils.Utils;

/**
 * General class to handle the client specific updates
 * 
 * @author Dharmaraju V
 *
 */
public class ClientSpecificationsHandler {

	private static Logger LOG = Logger.getLogger(ClientSpecificationsHandler.class);

	/**
	 * Process for resume alert (currently for TNCSC client).
	 * @param liveVehicleObject
	 * @param bulkData
	 * @param alertsList
	 */
	public static void  isResumed(LiveVehicleStatus liveVehicleObject, BulkUpdateDataBean bulkData,
			List<AlertOrViolation> alertsList) {
		if(liveVehicleObject.isTncsc_Is_Prev_Idle() &&(liveVehicleObject.getTncsc_Prev_Idle_Time_Seconds() > 900/*15 minutes*/) 
				&&(liveVehicleObject.getTncsc_Prev_Idle_Time_Seconds() < 1800)) {
			LOG.debug("Vehicle Id : "+liveVehicleObject.getVehicleId()+" has resumed from pause after its 15+ minutes pause");
			//	Resetting the flags after trip resume .
			liveVehicleObject.setTncsc_Is_Prev_Idle(false);
			liveVehicleObject.setTncsc_Prev_Idle_Time_Seconds(0);
			liveVehicleObject.setTncsc_Prev_Location(liveVehicleObject.getLocation());
			AlertOrViolation alert=InstantViolationAlerts.createAlertObject(bulkData, liveVehicleObject);
			alert.setAlertType(AlertType.TNCSC_RESUME_15);	
			alert.setAlertTypeValue("-");
			alertsList.add(alert);
			LOG.debug("ALERT : TNCSC resume Alert for Vehicle Id : "+liveVehicleObject.getVehicleId()+" at "+new Date());
		}
	}
	/**
	 * update vehicle icon based on module like Load On and Loadoff icons .
	 * @param iconCommand
	 * @param vehicleId
	 */

	public static void updateVehicleIcon(int iconCommand, Long vehicleId) {
		Vehicle vehicle = LoadVehicleDetails.getInstance().retrieve(vehicleId);
		final int LOAD_ON_ICON	= 1;
		final int LOAD_OFF_ICON	= 2;
		final int TRIP_FORWARD	= 3;		// 	Trip Start
		final int TRIP_END	= 4;			//	Trip End
		final int TRIP_ACTIVE=0;
		switch(iconCommand){
		case LOAD_ON_ICON: {
			if(vehicle.getVehicleIconPicId() != Vehicle.ICON_WITH_LOAD){
				vehicle.setVehicleIconPicId(Vehicle.ICON_WITH_LOAD);
				sendSMSAlert(vehicle, "\n Load On");
			}
		}
		break;
		case LOAD_OFF_ICON:	{
			if(vehicle.getVehicleIconPicId() != Vehicle.ICON_WITH_OUT_LOAD){
				vehicle.setVehicleIconPicId(Vehicle.ICON_WITH_OUT_LOAD);
				sendSMSAlert(vehicle, "\n Load Off");
			}
		}
		break;
		case TRIP_FORWARD:	{ 
			/* Dharma
			 * 1) Check for type of route ( FCI to Godown or Godown to FPS)
			 * 2) Change the icon colour accordingly 
			 * 3) No forward and reverse trip henceforth
			 * 
			 */
			//if trip is inactive then only start the trip
			if(vehicle.getVehicleIconPicId()!=Vehicle.TRIP_GODOWN_TO_CLIENT){
				vehicle.setVehicleIconPicId(Vehicle.TRIP_GODOWN_TO_CLIENT);	
			}
		}
		break;
		case TRIP_END:	{
			if(vehicle.getVehicleIconPicId()!=Vehicle.TRIP_END){
				vehicle.setVehicleIconPicId(Vehicle.TRIP_END);
			}

		}
		break;
		case TRIP_ACTIVE:{
			if(vehicle.getVehicleIconPicId()!=Vehicle.ICON_WITH_LOAD&&vehicle.getVehicleIconPicId()!=Vehicle.ICON_WITH_OUT_LOAD){
				vehicle.setVehicleIconPicId(Vehicle.TRIP_GODOWN_TO_CLIENT);
			}
		} 
		default :
			break;
		}
		try {
			((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).update(vehicle);
		} catch (OperationNotSupportedException e) {
			LOG.error("Error while updating vehicle icon in db ",e);
		}
		//	Update the latest changes
		LoadVehicleDetails.getInstance().cacheVehicleRecords.put(vehicle.getId().getId(), vehicle);

	}

	/**
	 * SMS Alert to the subscribed mobile numbers
	 * @param vehicle
	 * @param message
	 */
	public static void sendSMSAlert(Vehicle vehicle, String message) {
		try{
			LOG.debug("TNCSC : Checking for Mobiles subscribed for alerts");
			Vector<Long> mobileIds = LoadAclMobileDetails.getInstance().retrieveFromVehicleId(vehicle.getId().getId());
			if(mobileIds.size() != 0){
				for(Long mobileId : mobileIds){
					MobileNumber mobileNumber = LoadSMSAlertUserDetails.getInstance().retrieve(mobileId);
					if(mobileNumber.isTncsc()){
						LOG.debug("TNCSC : Sending SMS to "+mobileNumber.getMobileNumber());
						message=message+" at "+new Date();
						SMSGateWay.sendSMSToNumber(mobileNumber.getMobileNumber()+"", "Vehicle : "+vehicle.getDisplayName()+" "+message);
					}
				}
			} else{
				LOG.debug("TNCSC : No mobiles subscribed for SMS alerts");
			}
		} catch(Exception e){
			LOG.error("TNCSC : Error while sending sms alert",e);
		}

	}

	/**
	 * from groups value Tncsc group id set as 3
	 * @param vehicleId
	 * @return
	 */
	public static boolean isTncscClient(long vehicleId) {
		boolean isTNCSFlag=false;
		for(long userId : LoadAclVehicleDetails.getInstance().cacheAclVehicleDetails.keySet()){
			Vector<Long> acldata =LoadAclVehicleDetails.getInstance().cacheAclVehicleDetails.get(userId);
			for(int i=0;i<acldata.size();i++){
				if(acldata.get(i)==vehicleId){
					User user=LoadUserDetails.getInstance().cacheUserDetails.get(userId);
					if(user.getGroupId()==User.TNCSC_GROUP_ID && Boolean.valueOf((EnvironmentInfo.getProperty("IS_TNCSC_CLIENT")))){
						isTNCSFlag=true;
					}
				}
			}
		}
		return isTNCSFlag;
	}
	
	
	/**
	 * Checking whether the client belongs to tncsc or not by driver id
	 * @param vehicleId
	 * @return
	 */
	public static boolean isTncscClientByDriverId(long driverId) {
		boolean isTNCSFlag=false;
		for(long userId : LoadAclDriverDetails.getInstance().cacheAclDriverDetails.keySet()){
			Vector<Long> acldata =LoadAclDriverDetails.getInstance().cacheAclDriverDetails.get(userId);
			for(int i=0;i<acldata.size();i++){
				if(acldata.get(i)==driverId){
					User user=LoadUserDetails.getInstance().cacheUserDetails.get(userId);
					if(user.getGroupId()==User.TNCSC_GROUP_ID && Boolean.valueOf((EnvironmentInfo.getProperty("IS_TNCSC_CLIENT")))){
						isTNCSFlag=true;
					}
				}
			}
		}
		return isTNCSFlag;
	}
	/**
	 * Processing Allpackets based on client specification like ETA and others specifications
	 * 
	 * @param liveVehicleObject
	 * @param isPing
	 * @param dataPacket
	 * @param bulkData
	 * @param alertsList 
	 * @param bulkDataStatusMap 
	 */
	public static void processPacketBasedOnClientSpecification(LiveVehicleStatus liveVehicleObject, boolean isPing,
			GWTrackModuleDataBean dataPacket, BulkUpdateDataBean bulkData,
//			Statement statement, List<AlertOrViolation> alertsList, HashMap<BulkUpdateDataBean, HashMap<Long, Boolean>> bulkDataStatusMap) {
			List<AlertOrViolation> alertsList, HashMap<BulkUpdateDataBean, HashMap<Long, Boolean>> bulkDataStatusMap) {
		
		if(Boolean.valueOf((EnvironmentInfo.getProperty("IS_ETA_ENABLED")))){
			HashMap<Long, Boolean> trackHistoryIdAndStatusMap = new HashMap<Long, Boolean>();
			trackHistoryIdAndStatusMap.put(liveVehicleObject.getTrackHistoryRowID(), isPing);
			bulkDataStatusMap.put(bulkData, trackHistoryIdAndStatusMap);
		}

		//	Alert to be sent for Idle status of the vehicle for BMC Client
		if(Boolean.valueOf((EnvironmentInfo.getProperty("IS_BMC_CLIENT")))){
			//	Checking for Idle alert 
			try{
				ViolationAlertFinalizer.idleAlertProcessor(liveVehicleObject, alertsList, bulkData);
			} catch (Exception e){
				LOG.error("Error while calculating Idle Alert",e);
			}

			//	Checking for Bunching of buses alert
			try{
//				ViolationAlertFinalizer.bunchingAlertProcessor(liveVehicleObject, statement, dataPacket);
				ViolationAlertFinalizer.bunchingAlertProcessor(liveVehicleObject, dataPacket);
			} catch (Exception e){
				LOG.error("Error while calculating Bunching Alert",e);
			}
		}
	}
	
	/**
	 * 1) Check for data to be ignored
	 * 		a) latest button = last pressed
	 * 		b) button type = idle
	 * 2) Update button list for the device
	 * 3) Update button sequence if new button is pressed
	 * 4) Button id = latest button pressed
	 * 5) Button id = 88 when vehicle is idle
	 * @param liveVehicleObject
	 * @param dataPacket
	 * @param bulkData
	 * @param isPing
	 */
	public static void processButtonData(LiveVehicleStatus liveVehicleObject,
			GWTrackModuleDataBean dataPacket, BulkUpdateDataBean bulkData,
			boolean isPing) {
		LOG.debug("Processing Button data for imei : "+liveVehicleObject.getImei());
		int buttonId = bulkData.getAnalogue();
		LOG.debug("Button ID : "+buttonId);
		if(!isButtonDataToBeIgnored(buttonId, liveVehicleObject.getLatestButtonPressed(), isPing)){
			Vector<Integer> buttonList = EMRIHashMaps.simToButtonListMap.get(liveVehicleObject.getImei());
			if(buttonList == null){
				buttonList = new Vector<Integer>();
			}
			LOG.debug("ButtonList : "+ buttonList.toString());
			if(buttonList.size() == 0 || (buttonList.get((buttonList.size()-1)) != buttonId)){
				LOG.debug("Updating button data relateds");
				liveVehicleObject.setLatestButtonPressed(buttonId);
				buttonList.add(buttonId);
				String buttonSequence = "";
				for(int i = 0; i < buttonList.size(); i++){

					//buttonSequence += (ID+"|");
					if(i == (buttonList.size()-1)){
						//LOG.debug("Enter into If condition in buttonsequence"+ID);
						buttonSequence += (buttonList.get(i)+"");

					} else {
						//LOG.debug("Enter into else condition in buttonsequence"+ID);
						buttonSequence += (buttonList.get(i)+"|");
					}
				}
				liveVehicleObject.setButtonSequence(buttonSequence);
				
				if(buttonId == ButtonCode.END_OF_TRIP.getValue()){
					EMRIHashMaps.simToButtonListMap.remove(liveVehicleObject.getImei());
				} else {
					EMRIHashMaps.simToButtonListMap.put(liveVehicleObject.getImei(), buttonList);
				}
				
			} else {
				LOG.debug("Button ID : "+buttonId+" is same as the latest button pressed : "+buttonList.get(buttonList.size()-1)+" hence now updatating in the list ");
			}
		}
	}
	
	/**
	 * Check for data to be ignored
	 * 		a) latest button = last pressed
	 * 		b) button type = idle, batteryConnected, batter disconnected, GPRS to no GPRS, no GPRS to GPRS, antenna connected, antenna disconnected, unauthorized start
	 * @param buttonId
	 * @param latestButtonPressed
	 * @param isPing
	 * @return
	 */
	private static boolean isButtonDataToBeIgnored(int buttonId,
			int latestButtonPressed, boolean isPing) {
		
		if(latestButtonPressed == buttonId){
			LOG.debug("Duplication of the button press");
			return true;
		}
		
		if(buttonId == ButtonCode.IDLE.getValue() || buttonId == ButtonCode.BATTERY_CONNECTED.getValue() || buttonId == ButtonCode.BATTERY_DISCONNECTED.getValue()
				|| buttonId == ButtonCode.GPRS_TO_NON_GPRS.getValue() || buttonId == ButtonCode.NON_GPRS_TO_GPRS.getValue() || 
				buttonId == ButtonCode.ANTENNA_CONNECTED.getValue() || buttonId == ButtonCode.ANTENNA_DISCONNECTED.getValue() ||buttonId == ButtonCode.UNAUTHORIZED_START.getValue()){
			LOG.debug("Invalid button data");
			return true;
		}
		return false;
	}
	/**
	 * Client specific data processing 
	 * 
	 * @param liveVehicleObject
	 * @param dataPacket
	 * @param bulkData
	 * @param statement
	 * @param alertsList 
	 */
	public static void processDataPacketBasedOnClientSpecifications(LiveVehicleStatus liveVehicleObject,
//			GWTrackModuleDataBean dataPacket, BulkUpdateDataBean bulkData, Statement statement, List<AlertOrViolation> alertsList) {
			GWTrackModuleDataBean dataPacket, BulkUpdateDataBean bulkData, List<AlertOrViolation> alertsList) {

		//	If Client has Geofencing enabled do the following
		if(Boolean.valueOf((EnvironmentInfo.getProperty("IS_GEOFENCING_ENABLED")))){
//			ArrayList<AlertOrViolation> geoFenceViolationList = ViolationsHelper.getInstance().checkForGeoFencingViolation(liveVehicleObject, dataPacket, bulkData, statement);
			ArrayList<AlertOrViolation> geoFenceViolationList = ViolationsHelper.getInstance().checkForGeoFencingViolation(liveVehicleObject, dataPacket, bulkData);
			for(AlertOrViolation geoFenceViolation : geoFenceViolationList)
				if(geoFenceViolation.getAlertType() != null){
					LOG.debug("ALERT : Adding the entity \n"+geoFenceViolation.toString()+"\n into alertList with size : "+alertsList.size());
					alertsList.add(geoFenceViolation);
				}
		}
	}
	
	/**
	 * Processing ETA for ETA enabled clients
	 * @param liveVehicleObject
	 * @param dataPacket
	 * @param statement
	 * @param bulkDataStatusMap
	 */
	public static void processETA(LiveVehicleStatus liveVehicleObject, GWTrackModuleDataBean dataPacket,
			Statement statement, HashMap<BulkUpdateDataBean, HashMap<Long, Boolean>> bulkDataStatusMap) {
		if(Boolean.valueOf((EnvironmentInfo.getProperty("IS_ETA_ENABLED")))){
			LOG.debug("Client is ETA enabled");
			updateETAClients(liveVehicleObject, dataPacket, statement, bulkDataStatusMap);
		}		
	}
	
	/**
	 * 
	 * @param liveVehicleObject
	 * @param dataPacket
	 * @param statement
	 * @param bulkDataStatusMap
	 */
	private static void updateETAClients(LiveVehicleStatus liveVehicleObject, GWTrackModuleDataBean dataPacket, Statement statement,
			HashMap<BulkUpdateDataBean, HashMap<Long, Boolean>> bulkDataStatusMap) {
		boolean packetPingStatus = false;
		ArrayList<BulkUpdateDataBean> bulkDataList = new ArrayList<BulkUpdateDataBean>();
		for(BulkUpdateDataBean bulkData : bulkDataStatusMap.keySet()){
			bulkDataList.add(bulkData);
		}
		Utils.sortBulkUpdateData(bulkDataList);
		Date etaStartTime = Calendar.getInstance().getTime();
		for(BulkUpdateDataBean bulkData : bulkDataList){
			for(Long trackHistoryId : bulkDataStatusMap.get(bulkData).keySet()){
				packetPingStatus = bulkDataStatusMap.get(bulkData).get(trackHistoryId);
				updateETAForSasClients(liveVehicleObject, trackHistoryId, bulkData, dataPacket, packetPingStatus, statement);
			}
		}
		Date etaEndTime = Calendar.getInstance().getTime();
		long timeDiff = (etaEndTime.getTime() - etaStartTime.getTime());
		if(timeDiff > 5000){
			LOG.warn("ETA processing time is : "+timeDiff);
		}
		LOG.debug("Finished updating ETA clients");
	}
	
	/**
	 * Update the ETA values WRT to the current position of the vehicle
	 * @param liveVehicleObject
	 * @param trackHistoryId 
	 * @param bulkData
	 * @param dataPacket
	 * @param isPing 
	 * @param statement
	 */
	private static void updateETAForSasClients( LiveVehicleStatus liveVehicleObject, Long trackHistoryId, BulkUpdateDataBean bulkData, 
			GWTrackModuleDataBean dataPacket, boolean isPing, Statement statement) {
		LOG.debug("MBMC : Processing ETA ");
		new EtaDisplayDataHelper().processETA(liveVehicleObject.getVehicleId(), liveVehicleObject.getTripIdLong(),
				dataPacket.getImei(), trackHistoryId, bulkData.getOccurredAt(), dataPacket.getModuleUpdateTime(),
				bulkData.getLatitude(), bulkData.getLongitude(), isPing);
	}

}
