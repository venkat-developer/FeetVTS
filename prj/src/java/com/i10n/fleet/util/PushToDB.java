package com.i10n.fleet.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.IdlePointsDaoImp;
import com.i10n.db.dao.LiveVehicleStatusDaoImp;
import com.i10n.db.dao.TrackHistoryDaoImpl;
import com.i10n.db.entity.AlertOrViolation;
import com.i10n.db.entity.IdlePoints;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.TrackHistory;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.queryhelper.PushToDBQueryHelper;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;
import com.i10n.dbCacheManager.LoadTripDetails;
import com.i10n.fleet.container.BulkUpdateDataBean;
import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.helper.ViolationsHelper;
import com.i10n.mina.utils.Utils;
import com.i10n.module.dataprocessor.DataPool;
import com.i10n.module.dataprocessor.PacketProcessingException;

/**
 * 
 * Code to read data objects and push it into the database
 * 
 * @author Vishnu
 * @update Dharmaraju V
 */
public class PushToDB implements Runnable {

	Logger LOG = Logger.getLogger(PushToDB.class);

	int threadId;
	public PushToDB (int threadId) {
		this.threadId = threadId;
	}

	/**
	 * Thread which processes the packets.
	 */
	@Override
	public void run() {
		while (!DataBasePushHandler.shutdownInitiated) {

			/* Fetch the dataPacket from the DataPool and use that datapacket to store the data into the Database..*/
			Calendar packetTimeFromPool = Calendar.getInstance();
			GWTrackModuleDataBean dataPacket = DataPool.getPacket();
			if(dataPacket == null){
				LOG.error("Datapacket is null");
				continue;
			}
			LOG.info("Time taken to get the data packet from the pool : "+(Calendar.getInstance().getTimeInMillis()-packetTimeFromPool.getTimeInMillis()));

			try {	
				Date startProcessing = new Date ();

				/* Fetching the Object from the HashMap in the Memory */
				LiveVehicleStatus liveVehicleObject = LoadLiveVehicleStatusRecord.getInstance().retrieve(dataPacket.getImei());

				checkForRejection(liveVehicleObject, dataPacket);
				cleansePacket(dataPacket, liveVehicleObject);
				List<AlertOrViolation> alertsList = new ArrayList<AlertOrViolation>();
				/* Process each packet inside bulk data */
				BulkUpdateDataBean prevBulkData = new BulkUpdateDataBean(liveVehicleObject);

				//	Stores the filtered data for ETA specific client processing
				HashMap<BulkUpdateDataBean, HashMap<Long, Boolean>> bulkDataStatusMap = new HashMap<BulkUpdateDataBean, HashMap<Long,Boolean>>();

				try {
					boolean isPing = false;

					try{
						if(ClientSpecificationsHandler.isTncscClient(liveVehicleObject.getVehicleId())){
							ClientSpecificationsHandler.updateVehicleIcon(dataPacket.getAnalogue2(),liveVehicleObject.getVehicleId());
						}
						//	Processing each bulk part of the packet
						for (BulkUpdateDataBean bulkData : dataPacket.getBulkUpdateData()){
							if(checkPacketrepetitionForV5(dataPacket,bulkData,liveVehicleObject)){
								LOG.error("V5 : bulk record was repeated hence skipping record and continuing with next records");
								continue;
							}
							
							/**
							 * If the device is of version 101 and if its having no GPS fix then just update the livevehiclestatus with current time and previous known
							 */
							if(Utils.isVersionV5_101(dataPacket.getFirmwareVersion())){
								if(bulkData.isNoGPSLock()){
									LOG.debug("No GPS lock available");
									// Set current time as liveVehicleUpdate time 
									// Do not update the location details.
									liveVehicleObject.setModuleUpdateTime(dataPacket.getModuleUpdateTime());
									sendPushletEvent(liveVehicleObject);
									continue;
								}
							}
							
							if(Boolean.valueOf((EnvironmentInfo.getProperty("IS_FRS_EMRI_CLIENT")))){
								ClientSpecificationsHandler.processButtonData(liveVehicleObject, dataPacket, bulkData, isPing);
							}
							
							/*	Get the status of the module*/
							isPing = getPingStatus(liveVehicleObject, dataPacket, prevBulkData, bulkData);
							processEachBulkPacket(liveVehicleObject, dataPacket, bulkData, 
									alertsList, isPing, bulkDataStatusMap);
							prevBulkData = bulkData;
						}
					} catch (Exception e){
						LOG.error("Error while processing bulk packets",e);
					}

					try{						
						//	Update Live vehicle status for the  
						if(Utils.isTeltonikaDevice((int)dataPacket.getFirmwareVersion())){
							liveVehicleObject.setMaxSpeed((float)prevBulkData.getSpeed());
							liveVehicleObject.setCourse((float)prevBulkData.getDirection());
						}

						liveVehicleObject.setFirmwareVersion(dataPacket.getFirmwareVersion());
						liveVehicleObject = PushToDBQueryHelper.formulateLiveVehicleStatusEntity(liveVehicleObject, dataPacket, prevBulkData, isPing);

						try {
							((LiveVehicleStatusDaoImp)DBManager.getInstance().getDao(DAOEnum.LIVE_VEHICLE_STATUS_DAO)).update(liveVehicleObject);
						} catch (OperationNotSupportedException e) {
							LOG.error("",e);
						}

					} catch (Exception e){
						LOG.error("Error while processing Live vehicle status update ",e);
					}

					try{
						//	Update Live Vehicle Status cache with the latest packet
						LoadLiveVehicleStatusRecord.store(dataPacket.getImei(), liveVehicleObject);
					} catch (Exception e){
						LOG.error("Error while updating LiveVehicleStatus cache",e);
					}

					try{
						List<AlertOrViolation> alertList = ViolationsHelper.getInstance().checkForAlerts(liveVehicleObject, dataPacket);
						alertsList.addAll(alertList);
						ViolationsHelper.doNecessaryAlertProcessing(alertsList, liveVehicleObject, dataPacket);
					} catch (Exception e){
						LOG.error("Error while processing Alerts ",e);
					}
				} catch (Exception e){
					LOG.fatal("Exception while processing packet "+dataPacket.toString()+"" +
							e.getStackTrace()+" Message "+e.getMessage(), e);
				} 	
				LoadLiveVehicleStatusRecord.store(dataPacket.getImei(), liveVehicleObject);
				Date endProcessing = new Date ();
				long pushToDBProcessingTime = (endProcessing.getTime()-startProcessing.getTime());
				LOG.info("Thread "+threadId+" time for processing imei "+ dataPacket.getImei() +" packet : "+
						pushToDBProcessingTime+" for pkt count "+dataPacket.getBulkUpdateData().size());
			}catch (PacketProcessingException e) {
				LOG.error("Packet discarded : "+dataPacket.toString(), e);
			} catch (Exception e){
				LOG.error("",e);
			}
		}
	}
	/**
	 * For Version-V5 instead of checking SQD and SQG, now checking previous
	 * module update time and current module update time.
	 * If both are same skipping that bulk record from processing and
	 * continuing with the next bulk records.
	 * @param dataPacket
	 * @param bulkData
	 * @param liveVehicleObject
	 * @return
	 */
	private boolean checkPacketrepetitionForV5(GWTrackModuleDataBean dataPacket, BulkUpdateDataBean bulkData, LiveVehicleStatus liveVehicleObject) {
		if(Utils.isVersionV5_6((int)dataPacket.getFirmwareVersion()) || Utils.isVersionV5_8((int)dataPacket.getFirmwareVersion())){
			try{
				if(bulkData.getOccurredAt().getTime() <= liveVehicleObject.getLastUpdatedAt().getTime()){
					LOG.warn("V5 Packet : Bulk data time is same as previous "+bulkData.toString());
					return true;
				}	
			}catch (Exception e) {
				LOG.error("Error while chekcing packet repaetion ",e);
			}	
		}
		return false;
	}

	/**
	 * Process each bulk packet. Do necessary updates/insertion
	 * 
	 * @param liveVehicleObject
	 * @param dataPacket
	 * @param prevBulkData
	 * @param bulkData
	 * @param alertsList 
	 * @param isPing 
	 * @param bulkDataStatusMap 
	 * @throws SQLException
	 */
	private void processEachBulkPacket(LiveVehicleStatus liveVehicleObject, GWTrackModuleDataBean dataPacket,
			BulkUpdateDataBean bulkData, List<AlertOrViolation> alertsList, 
			boolean isPing, HashMap<BulkUpdateDataBean,HashMap<Long,Boolean>> bulkDataStatusMap) throws SQLException {


		boolean isOldPacket = isOldPacket(bulkData.getOccurredAt(), liveVehicleObject.getLastUpdatedAt());

		liveVehicleObject.update(dataPacket, bulkData, isPing, isOldPacket);

		if (isPing) {
			/*	Its Ping Packet*/
			if(!isOldPacket){
				processPingPacket(liveVehicleObject, dataPacket, bulkData, alertsList);
			} else {
				LOG.debug("This is a old packet and hence skipping ping packet processing");
			}
		} else {
			/* 	Its Data Packet */
			processDataPacket(liveVehicleObject, dataPacket, bulkData, alertsList, isOldPacket);
		}

		sendPushletEvent(liveVehicleObject);
		if(!isOldPacket){
			ClientSpecificationsHandler.processPacketBasedOnClientSpecification(liveVehicleObject, isPing, dataPacket, bulkData, alertsList, bulkDataStatusMap);
		} else {
			LOG.debug("This is a old packet and hence skipping client specification handling");
		}
	}

	/**
	 * If the packet update time is less than the latest known update time then packet is termed as old packet 
	 * @param bulkUpdateTime
	 * @param liveVehicleObjectLastUpdateTime
	 * @return
	 */
	private boolean isOldPacket(Date bulkUpdateTime, Date liveVehicleObjectLastUpdateTime) {
		if(bulkUpdateTime.getTime() < liveVehicleObjectLastUpdateTime.getTime()){
			LOG.warn("This is a old packet");
			return true;
		}
		return false;
	}

	/**
	 * Process DataPacket for IdlePoint end time update and TrackHistory insert
	 * 
	 * @param liveVehicleObject
	 * @param dataPacket
	 * @param bulkData
	 * @param alertsList 
	 * @param isOldPacket 
	 * @throws SQLException
	 */
	private void processDataPacket(LiveVehicleStatus liveVehicleObject, GWTrackModuleDataBean dataPacket, BulkUpdateDataBean bulkData,
			List<AlertOrViolation> alertsList, boolean isOldPacket) throws SQLException {

		//	Make an entry into the trackhistory table with this update
		TrackHistory trackHistory = null;
		if(Utils.isTeltonikaDevice((int)dataPacket.getFirmwareVersion())){
			liveVehicleObject.setMaxSpeed((float) bulkData.getSpeed());
			liveVehicleObject.setCourse((float) bulkData.getDirection());
			trackHistory = PushToDBQueryHelper.formulateTeltonikaDataPacketTrackHistoryEntity(liveVehicleObject.getTripIdLong(), 
					dataPacket, bulkData, false, liveVehicleObject.getPrevSqd(), liveVehicleObject.getMaxSpeed(), liveVehicleObject.getCourse());
		} else{
			trackHistory = PushToDBQueryHelper.formulateDataPacketTrackHistoryEntity(liveVehicleObject, 
					dataPacket, bulkData, false, liveVehicleObject.getPrevSqd());
		}
		try {
			trackHistory = ((TrackHistoryDaoImpl)DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO)).processInsertion(trackHistory);
			liveVehicleObject.setTrackHistoryRowID(trackHistory.getId().getId());
		} catch (OperationNotSupportedException e) {
			LOG.error("",e);
		}

		if(!isOldPacket){
			//	Updating IdlePoint end time if missing
			handleIdlePointCondition(liveVehicleObject, bulkData, alertsList, dataPacket.getImei());
			ClientSpecificationsHandler.processDataPacketBasedOnClientSpecifications(liveVehicleObject, dataPacket, bulkData, alertsList);
		} else {
			LOG.debug("This is a old packet and hence skipping idle point handling and client specification handling");
		}
	}

	/**
	 * Process PingPacket for IdlePoint insert or update, Also TrackHistory update 
	 * 
	 * @param liveVehicleObject
	 * @param dataPacket
	 * @param bulkData
	 * @param alertsList 
	 * @throws SQLException
	 */
	private void processPingPacket(LiveVehicleStatus liveVehicleObject, GWTrackModuleDataBean dataPacket, 
			BulkUpdateDataBean bulkData, List<AlertOrViolation> alertsList) throws SQLException {

		//	Need to set speed 0 for trackHistory update
		dataPacket.setMaxSpeed(0);

		//	For ping count set Speed as zero for pushletEvent
		liveVehicleObject.setMaxSpeed(0);

		//	Decide on IdlePoint entry or update or none
		handleIdlePointCondition(liveVehicleObject, bulkData,alertsList, dataPacket.getImei());
		// 	Update the corresponding trackhistory row with increased ping count and other details
		TrackHistory trackHistory = null;
		if(Utils.isTeltonikaDevice((int)dataPacket.getFirmwareVersion())){// Teltonika Device sends speed and direction of travel in all the bulk packets
			liveVehicleObject.setMaxSpeed((float) bulkData.getSpeed());
			liveVehicleObject.setCourse((float) bulkData.getDirection());
			trackHistory = PushToDBQueryHelper.formulatePingPacketTrackHistoryEntity(liveVehicleObject, dataPacket, bulkData, (bulkData.getDeltaDistance()/1000));
		} else{
			liveVehicleObject.setMaxSpeed((float) dataPacket.getMaxSpeed());
			liveVehicleObject.setCourse((float) dataPacket.getVehicleCourse());
			trackHistory = PushToDBQueryHelper.formulatePingPacketTrackHistoryEntity(liveVehicleObject, dataPacket, bulkData, (bulkData.getAirDistance()/1000));
		}
		try {
			trackHistory.setId(new LongPrimaryKey(liveVehicleObject.getTrackHistoryRowID()));
			((TrackHistoryDaoImpl)DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO)).update(trackHistory);
		} catch (OperationNotSupportedException e) {
			LOG.error("",e);
		}
	}

	/**
	 * Deduces the status of the module being idle or not
	 * 
	 * @param liveVehicleObject
	 * @param dataPacket
	 * @param prevBulkData
	 * @param bulkData
	 * @return Status of the module
	 */
	private boolean getPingStatus(LiveVehicleStatus liveVehicleObject, GWTrackModuleDataBean dataPacket, 
			BulkUpdateDataBean prevBulkData, BulkUpdateDataBean bulkData) {
		boolean isPing = false;

		/*	If server has been started now then make and mandatory trackhistory entry 
		 * 	Else process packet to find out the status of the module*/
		if (liveVehicleObject.getTrackHistoryRowID() == 0) {
			isPing = false;
		} else {
			/*	If the vehicle is within 20 meters then consider the packet as ping */
			double airDistance = CustomCoordinates.distance(prevBulkData.getLatitude(), prevBulkData.getLongitude(), bulkData.getLatitude(), bulkData.getLongitude());
			isPing = (airDistance < Double.parseDouble(EnvironmentInfo.getProperty("DISTANCE_FOR_PING_CLASSIFICATION_KM")) || dataPacket.isPingFlag() );
			/* 	If CC status has changed in idle mode, new trackhistory entry has to be made*/
			if(isPing){
				if(liveVehicleObject.isChargerConnected() != dataPacket.isChargerConnected()){
					isPing = false;
				}
			}
		}

		return isPing;
	}

	/**
	 * Sending pushlet event for the current update received from the module
	 * @param liveVehicleObject
	 * @param isPing
	 * @throws SQLException
	 */
	private void sendPushletEvent(LiveVehicleStatus liveVehicleObject) throws SQLException {
		try{
			//PushletHelper.sendEvent(liveVehicleObject);
		} catch (Exception e){
			LOG.error("Handling exception throwed while pushlet update",e);
		}
	}

	/**
	 * Create or update idle points when required
	 * @param liveVehicleObject
	 * @param bulkData
	 * @param alertsList 
	 * @throws SQLException
	 */
	private void handleIdlePointCondition (LiveVehicleStatus liveVehicleObject, BulkUpdateDataBean bulkData,
			List<AlertOrViolation> alertsList, String imei) throws SQLException {		
		if (liveVehicleObject.getPingCount()!= 0) {
			/**
			 * 	It is ping continuation or current idle point ended
			 * 	Or it could have ended as well and switched to data packet mode 
			 * */
			liveVehicleObject.setIdleEndTime(bulkData.getOccurredAt());
			/**
			 * Idle limit cchecking with the idle limit. 
			 */
			int idleLimitInMinutes=LoadTripDetails.getInstance().retrieve(liveVehicleObject.getTripIdLong()).getIdlePointsTimeLimit();
			int idleLimitInSeconds=idleLimitInMinutes*60;

			if (liveVehicleObject.getIdleDuration() >  idleLimitInSeconds) {
				IdlePoints idlePoints = null;
				if (!liveVehicleObject.isIdleRowActive()) {
					LOG.debug("Idle row is not active for Vehicle Id : "+liveVehicleObject.getVehicleId());
					/* 	Time to make an idle entry */
					liveVehicleObject.setIdleRowActive(true);
					idlePoints = PushToDBQueryHelper.formulateIdlePointEntity(liveVehicleObject);
					try {
						LOG.debug("Making idle entry into DB for "+liveVehicleObject.getVehicleId());
						((IdlePointsDaoImp)DBManager.getInstance().getDao(DAOEnum.IDLE_POINTS_DAO)).insert(idlePoints);
						liveVehicleObject.setIdleRowId(idlePoints.getId().getId());
					} catch (OperationNotSupportedException e) {
						LOG.error("",e);
					}
				} else {
					/*	Idle entry already exits hence just update it*/
					idlePoints = PushToDBQueryHelper.formulateIdlePointEntity(liveVehicleObject);
					LOG.debug("Idle row is active for Vehicle Id : "+liveVehicleObject.getVehicleId());
					idlePoints.setId(new LongPrimaryKey(liveVehicleObject.getIdleRowId()));
					try {
						LOG.debug("Idle row acitve hence updating for Vehicle Id : "+liveVehicleObject.getVehicleId());
						((IdlePointsDaoImp)DBManager.getInstance().getDao(DAOEnum.IDLE_POINTS_DAO)).update(idlePoints);
					} catch (OperationNotSupportedException e) {
						LOG.error("",e);
					}
				}
			}
		}
		else {
			/* 	From idle mode, vehicle has moved to data packet mode */
			liveVehicleObject.setIdleStartTime(bulkData.getOccurredAt());
			liveVehicleObject.setIdleRowActive(false);
		}
		LoadLiveVehicleStatusRecord.store(imei, liveVehicleObject);
	}


	/**
	 * Tests for presence of all the critical parameters
	 * else throws exception
	 */
	private void checkForRejection (LiveVehicleStatus liveVehicleObject, GWTrackModuleDataBean dataPacket) throws PacketProcessingException {
		// No Trip added
		if (liveVehicleObject == null ||  liveVehicleObject.getTripIdLong()==0) {
			throw new PacketProcessingException (PacketProcessingException.PacketErrorCode.TRIP_NOT_ADDED);
		}

		// Vehicle ID 0
		if (liveVehicleObject.getVehicleId()==0) {
			throw new PacketProcessingException (PacketProcessingException.PacketErrorCode.VEHICLE_ID_IS_ZERO);			
		}
		/**
		 * For Version-V5 SQD and SQG values are not considering .
		 */
		if( Utils.isAndroidFirmware((int)dataPacket.getFirmwareVersion()) || Utils.isVersionV5_6((int)dataPacket.getFirmwareVersion()) 
				|| Utils.isVersionV5_8((int)dataPacket.getFirmwareVersion())|| Utils.isTeltonikaDevice((int)dataPacket.getFirmwareVersion())){
		}else{
			// Same SQD, SQG
			if ( liveVehicleObject.getSqg() == dataPacket.getNumberOfSuccessPackets() &&
					liveVehicleObject.getSqd() == dataPacket.getNumberOfPacketSendingAttempts() ) {
				//	throw new PacketProcessingException (PacketProcessingException.PacketErrorCode.PACKET_ERRATA);						
			}

			// Same SQG
			if ( liveVehicleObject.getSqg() == dataPacket.getNumberOfSuccessPackets() ) {
				//throw new PacketProcessingException (PacketProcessingException.PacketErrorCode.PACKET_REPEATED);						
			}
		}

		// Bulk data empty
		if (dataPacket.getBulkUpdateData().isEmpty()) {
			throw new PacketProcessingException (PacketProcessingException.PacketErrorCode.BULK_DATA_EMPTY);									
		}
	}

	/**
	 * Cleans data in the data packet
	 * 
	 * @param dataPacket
	 * @param liveVehicleObject 
	 * @throws PacketProcessingException
	 */
	private void cleansePacket (GWTrackModuleDataBean dataPacket, LiveVehicleStatus liveVehicleObject)  {
		// 	Speed too high?
		if (dataPacket.getMaxSpeed() > Integer.parseInt(EnvironmentInfo.getProperty("MAX_SPEED"))) {
			dataPacket.setMaxSpeed(0);
		}

		BulkUpdateDataBean prevBulkData = null;
		for (BulkUpdateDataBean bulkData : dataPacket.getBulkUpdateData()) {
			// 	We could use air distance to approximate it
			LOG.debug("Distance from the module : "+bulkData.getDeltaDistance());
			if (dataPacket.getMaxSpeed() == 0) {
				bulkData.setDeltaDistance(0);
			}
			double airDistance = 0;
			if(!isOldPacket(bulkData.getOccurredAt(), liveVehicleObject.getLastUpdatedAt())){
				if (prevBulkData != null) {
					/* This is an instance of the the packet continuation*/
					airDistance = 1000 * (CustomCoordinates.distance(prevBulkData.getLatitude(), prevBulkData.getLongitude(), bulkData.getLatitude(), bulkData.getLongitude()));
					LOG.debug("DISTANCE : Trying to get the distance between the points PrevBulkData : ("+prevBulkData.getLatitude()+", "+prevBulkData.getLongitude()
							+") and ("+bulkData.getLatitude()+", "+bulkData.getLongitude()+") ");
				} else {
					/* This is the first/only packet from the device for this instance */
					airDistance = 1000 * (CustomCoordinates.distance(liveVehicleObject.getLocation().getFirstPoint().y, liveVehicleObject.getLocation().getFirstPoint().x, bulkData.getLatitude(), bulkData.getLongitude()));
					LOG.debug("DISTANCE : Trying to get the distance between the points : ("+liveVehicleObject.getLocation().getFirstPoint().y+", "
							+liveVehicleObject.getLocation().getFirstPoint().x+") and ("+bulkData.getLatitude()+", "+bulkData.getLongitude()+") ");
				}
				// 	Setting distance in meters
				if (bulkData.getDeltaDistance() < airDistance ) {
					LOG.debug("DISTANCE : Updating the delta distance : "+bulkData.getDeltaDistance()
							+" with the air distance : "+(airDistance));
					// 	Using air distance when bulkupdate delta distance is less than air distance
					if(Utils.isTeltonikaDevice((int) dataPacket.getFirmwareVersion())){
						// 	Using air distance when bulkupdate delta distance is less than air distance
						bulkData.setDeltaDistance(airDistance + (airDistance*0.3));
						LOG.debug("Teltonika air distance:"+bulkData.getDeltaDistance());
					} else {
						bulkData.setDeltaDistance(airDistance);
						bulkData.setAirDistance(airDistance);
						LOG.debug("GWtrack air distance:"+airDistance);
					}
				}
				// 	If distance is > 10 KM then set the distance to 0
				if(bulkData.getDeltaDistance() > Long.parseLong(EnvironmentInfo.getProperty("MAX_DELTA_DISTANCE_M"))){
					bulkData.setDeltaDistance(0);
				}
			} else {
				// Setting the distance to zero as the packet is old
				bulkData.setDeltaDistance(0);
			}

			prevBulkData = bulkData;

		}		
	}
}