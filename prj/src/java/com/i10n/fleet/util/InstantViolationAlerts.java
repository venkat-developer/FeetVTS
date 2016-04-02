package com.i10n.fleet.util;

import java.util.Date;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.postgis.Geometry;
import org.postgis.Point;

import com.i10n.db.entity.Address;
import com.i10n.db.entity.AlertOrViolation;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.MailingListAlert;
import com.i10n.db.entity.MobileNumber;
import com.i10n.db.entity.AlertOrViolation.AlertType;
import com.i10n.dbCacheManager.LoadAclAlertDetails;
import com.i10n.dbCacheManager.LoadAclMobileDetails;
import com.i10n.dbCacheManager.LoadDriverDetails;
import com.i10n.dbCacheManager.LoadMailingAlertUserDetails;
import com.i10n.dbCacheManager.LoadSMSAlertUserDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.container.BulkUpdateDataBean;

/**
 * Thread which sends instant email and sms alerts when occurred
 * @author Dharmaraju V
 *
 */
public class InstantViolationAlerts implements Runnable {

	private static Logger LOG = Logger.getLogger(InstantViolationAlerts.class);
	private int smsCount=0;

	public InstantViolationAlerts() {

	}

	@Override
	public void run() {
		// Sending Instant Email and SMS alerts for the subscribed users.
		while(true){
			try {
				AlertOrViolation alert = AlertEmailSMSService.getInstance().retrieve();
				LOG.debug("Preparing to send alerts for the subscribed users on violation");
				// Getting all Email alert subscribed users
				Vector<String> emailIdList = new Vector<String>();
				ConcurrentHashMap<Long, MobileNumber> cacheSMSAlertUsers=LoadSMSAlertUserDetails.getInstance().cacheSMSAlertUsers;
				for(Long emailUserId : LoadAclAlertDetails.getInstance().cacheAclAlertsDetails.keySet()){
					Vector<Long> vehicleList = LoadAclAlertDetails.getInstance().retrieve(emailUserId);
					LOG.debug("Vehicles list size assinged to email user : "+vehicleList.size());
					if(vehicleList.size() == 0){
						LOG.debug("No vehicles assigned to the email user : "+emailUserId);
						continue;
					}
					if(vehicleList.contains(alert.getVehicleId())){
						MailingListAlert mailingListAlert = LoadMailingAlertUserDetails.getInstance().retrieve(emailUserId);
						if(mailingListAlert == null){
							LOG.error("Mailing list entity with id "+emailUserId+" has to be cached");
							continue;
						}
						/**
						 *  adding geofence alerts only for bp client  
						 */
						if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_BP_CLIENT"))){
							if(mailingListAlert.getGeoFencing()){
								if(alert.getAlertType() == AlertType.GEOFENCING_OUT || alert.getAlertType() == AlertType.GEOFENCING_IN){
									emailIdList.add(mailingListAlert.getMailId());
								}
							}	
						}else{
							emailIdList.add(mailingListAlert.getMailId());
						}
					}
				}

				// Getting all the mobile alert subscribed users
				Vector<Long> mobileNumberList = new Vector<Long>();
				for(Long mobileId : LoadAclMobileDetails.getInstance().cacheAclMobileDetails.keySet()){
					Vector<Long> vehicleList = LoadAclMobileDetails.getInstance().retrieve(mobileId);
					if(vehicleList.size() == 0){
						continue;
					}
					if(vehicleList.contains(alert.getVehicleId())){
						MobileNumber mob=cacheSMSAlertUsers.get(mobileId);
						if(cacheSMSAlertUsers.containsKey(mobileId)){
							if(mob.getChargerDisConnected() &&(alert.getAlertType()==AlertType.CHARGER_CONNECTED ||alert.getAlertType()==AlertType.CHARGER_DISCONNECTED)){
								mobileNumberList.add(cacheSMSAlertUsers.get(mobileId).getMobileNumber());	
							}else if(mob.getOverSpeeding() &&(alert.getAlertType()==AlertType.OVERSPEED)){
								mobileNumberList.add(cacheSMSAlertUsers.get(mobileId).getMobileNumber());	
							}else if(mob.getGeoFencing() &&(alert.getAlertType()==AlertType.GEOFENCING_OUT ||alert.getAlertType()==AlertType.GEOFENCING_IN)){
								mobileNumberList.add(cacheSMSAlertUsers.get(mobileId).getMobileNumber());	
							}else{
								mobileNumberList.add(cacheSMSAlertUsers.get(mobileId).getMobileNumber());	
							}
						}
					}
				}
				if(emailIdList.size() != 0){
					String emailIdArrayList[] = new String[emailIdList.size()];
					for(int i=0 ; i< emailIdList.size(); i++){
						emailIdArrayList[i] = emailIdList.get(i);
					}
					// Sending Email alerts
					String vehicleName = LoadVehicleDetails.getInstance().retrieve(alert.getVehicleId()).getDisplayName();
					String driverName = LoadDriverDetails.getInstance().retrieve(alert.getDriverId()).getFirstName();
					MailSender.postMail(emailIdArrayList, "Violation Alert", "Vehicle : "+vehicleName+"\n Driver : "
							+driverName+"\n Violation Type : "+alert.getAlertType().toString()+" \n "+alert.getAlertTypeValue(), "text/html");
				}
				// Sending SMS alerts
				if(mobileNumberList.size() != 0 ){
					String vehicleName = LoadVehicleDetails.getInstance().retrieve(alert.getVehicleId()).getDisplayName();
					String driverName = LoadDriverDetails.getInstance().retrieve(alert.getDriverId()).getFirstName();
					for(Long mobileNumber : mobileNumberList){
						//For tncsc alerts like  trip end ,trip start,halting alert,pause alerts 
						if(alert.getAlertType()==AlertType.HALTING_ALERT ||alert.getAlertType()==AlertType.TNCSC_TRIP_START
								||alert.getAlertType()==AlertType.TNCSC_TRIP_END || alert.getAlertType()==AlertType.TNCSC_PAUSE_15 || alert.getAlertType()==AlertType.TNCSC_RESUME_15){
							LOG.info("TNCSC Alerts : Sending SMS on "+alert.getAlertTypeValue()+" at "+new Date()+" for "+vehicleName);
							StringBuffer message=formulateMessageForTncscAlerts(vehicleName,alert);
							SMSGateWay.sendSMSToNumber(mobileNumber+"",message.toString());
							smsCount=smsCount+1;
						}else{
							LOG.info("Normal Alerts : Sending SMS on "+alert.getAlertTypeValue()+" at "+new Date()+" for "+vehicleName);
							StringBuffer message= formulateMessageForNormalAlerts(vehicleName,driverName,alert);
							SMSGateWay.sendSMSToNumber(mobileNumber+"",message.toString());
							smsCount=smsCount+1;
						}
					}
					LOG.debug("Total SMS are sent "+smsCount+" up to "+new Date());
				}else{
					LOG.debug("No mobile users subscribed");
				}
			} catch (InterruptedException e) {
				LOG.error("Exception while sending alerts ",e);
			} catch (Exception e){
				LOG.error("Caught exception",e);
			}

		}
	}

	private StringBuffer formulateMessageForNormalAlerts(String vehicleName,String driverName, AlertOrViolation alert) {
		StringBuffer message=new StringBuffer();
		message.append("Vehicle : ");
		message.append(vehicleName);
		message.append(" Driver : ");
		message.append(driverName);
		message.append(" Violation Type : ");
		message.append(alert.getAlertType().toString());
		//over speed framing 
		if(alert.getAlertType()==AlertType.OVERSPEED){
			message.append(" with speed ");
			message.append(alert.getAlertTypeValue());
			//message.append(" at ");
			//message.append(new Date());
		}
		if(alert.getAlertType()==AlertType.GEOFENCING_OUT){
			message.append(" Of ");
			message.append(alert.getGeofenceArea());
			//message.append(" at ");
			//message.append(new Date());
		}
		if(alert.getAlertType()==AlertType.GEOFENCING_IN){
			message.append(" to ");
			message.append(alert.getGeofenceArea());
			//message.append(" at ");
			//message.append(new Date());
		}
		/**
		 * Panic alert.
		 */
		if(alert.getAlertType() == AlertType.PANIC){
			message.append("  ");
			message.append(alert.getAlertTypeValue());
		}
		return message;
	}

	private StringBuffer formulateMessageForTncscAlerts(String vehicleName,AlertOrViolation alert) {
		StringBuffer tncsMessage=new StringBuffer();
		tncsMessage.append("Vehicle : ");
		tncsMessage.append(vehicleName);
		if(alert.getAlertType()==AlertType.HALTING_ALERT){
			tncsMessage.append(" Halting more than 30 minutes");
			tncsMessage.append(" at ");
			tncsMessage.append(new Date());
		}
		if(alert.getAlertType()==AlertType.TNCSC_TRIP_START){
			tncsMessage.append(" Trip Started");
			tncsMessage.append(" at ");
			tncsMessage.append(new Date());
		}
		if(alert.getAlertType()==AlertType.TNCSC_TRIP_END){
			tncsMessage.append(" Trip Ended");
			tncsMessage.append(" at ");
			tncsMessage.append(new Date());
		}
		if(alert.getAlertType()==AlertType.TNCSC_PAUSE_15){
			tncsMessage.append(" has Pause for 15 minutes");
			tncsMessage.append(" at ");
			tncsMessage.append(new Date());
		}
		if(alert.getAlertType()==AlertType.TNCSC_RESUME_15){
			tncsMessage.append(" has resumed from 15+ minutes halt");
			tncsMessage.append(" at ");
			tncsMessage.append(new Date());
		}
		return tncsMessage;
	}

	public static AlertOrViolation createAlertObject(BulkUpdateDataBean bulkData, LiveVehicleStatus liveVehicleObject) {
		//Long alertId = IDGenerator.getInstance().getNewId();
		Point point = new Point(bulkData.getLongitude(), bulkData.getLatitude());
		Geometry alertLocation = (Geometry) point;
		AlertOrViolation alert = new AlertOrViolation(/*new LongPrimaryKey(0L),*/ liveVehicleObject.getVehicleId(), 
				liveVehicleObject.getDriverId(), bulkData.getOccurredAt(), new Date(), alertLocation);

		alert.setAlertLocationReferenceId(0L);
		Address address = null;
		StringBuffer location=new StringBuffer();
		alert.setAlertLocationReferenceId(0L);
		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_VIOLATIONS_ENABLED"))){
			address = GeoUtils.fetchNearestLocation(alertLocation.getFirstPoint().y,alertLocation.getFirstPoint().x, false);
			location=StringUtils.formulateAddress(address,liveVehicleObject.getVehicleId(),alertLocation.getFirstPoint().y
					,alertLocation.getFirstPoint().x);
			alert.setAlertLocationText(location.toString());
			if(address!=null){
				alert.setAlertLocationReferenceId(address.getId());
			}
		}else{
			location.append("Alert ");
			location.append(StringUtils.addressFetchDisabled(liveVehicleObject.getVehicleId(),alertLocation.getFirstPoint().y
					,alertLocation.getFirstPoint().x).toString());
			alert.setAlertLocationText(location.toString());
		}
		return alert;
	}
}