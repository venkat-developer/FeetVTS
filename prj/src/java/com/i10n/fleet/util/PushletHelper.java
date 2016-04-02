package com.i10n.fleet.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.harman.db.entity.LiveVehicleStatus;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.EmriVehiclesBaseStationDaoImp;
import com.i10n.db.dao.LogsDaoImp;
import com.i10n.db.dao.UserDaoImp;
import com.i10n.db.entity.Address;
import com.i10n.db.entity.EmriVehiclesBaseStation;
import com.i10n.db.entity.HardwareModule;
import com.i10n.db.entity.Logs;
import com.i10n.db.entity.User;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadDriverDetails;
import com.i10n.dbCacheManager.LoadEmriVehiclesBaseStationDetails;
import com.i10n.dbCacheManager.LoadHardwareModuleDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.update.IStreamUpdate;
import com.i10n.fleet.update.PositionUpdate;
import com.i10n.fleet.update.StatusUpdate;
import com.i10n.fleet.update.VehicleMapUpdate;

/**
 * Class handling pushlet updates
 * @author Dharmaraju V
 *
 */
public class PushletHelper implements MessageListener,ExceptionListener{

	private static Logger LOG = Logger.getLogger(PushletHelper.class);

	int pushletId;

	public PushletHelper(int pushletId){
		this.pushletId = pushletId;
	}

	/**
	 * Sending comet event for the latest update got
	 * @param liveVehicleObject
	 * @param isPing 
	 * 	 * @throws SQLException
	 */
	public static void sendEvent(LiveVehicleStatus liveVehicleObject) {
		try {
			List<User> assignedUsers = ((UserDaoImp)DBManager.getInstance().getDao(DAOEnum.USER_DAO))
					.getUsersToWhichTheVehicleIsAssigned(liveVehicleObject.getVehicleId());
			List<User> activeLoggedInUsers = new ArrayList<User>();

			for (User user : assignedUsers){
				//Do check the login .if its more 30 minutes and remove it from the list.
				//if not keep the user in the list

				List<Logs> logedInUsers= ((LogsDaoImp)(DBManager.getInstance().getDao(DAOEnum.LOGS_DAO)))
						.selectLogedinUsers(user.getId());
				long loggedInTime = logedInUsers.get(0).getDat().getTime();
				long currentDate = new Date().getTime(); 
				long diff = currentDate-loggedInTime;
				long seconds =diff/1000;
				long minutes = seconds/60;
				if(minutes<30){
					LOG.info("Active User is:"+user.toString());
					activeLoggedInUsers.add(user);
				}
			}
			if (activeLoggedInUsers.size() == 0){
				LOG.info("There is no active logged in user");
				return;
			}else {
				LOG.info("Active users List is "+activeLoggedInUsers.size());
				int vehicleStatus = 1;
				if (liveVehicleObject.isIdleRowActive()){
					vehicleStatus = 2;
				}
				Vehicle vehicle = LoadVehicleDetails.getInstance().retrieve(liveVehicleObject.getVehicleId());
				EmriVehiclesBaseStation cachedEmriRajasthan =null;
				if(Boolean.parseBoolean(EnvironmentInfo.getProperty("IS_FRS_EMRI_CLIENT"))){
					List<EmriVehiclesBaseStation> emriRajasthanList = ((EmriVehiclesBaseStationDaoImp)DBManager.getInstance().getDao(DAOEnum.EMRI_RAJASTHAN_DAO)).
							selectByVehicleId(vehicle.getId().getId());
					if(emriRajasthanList != null){
						LOG.info("EMRI Rajasthan List Size "+emriRajasthanList.size());
						if(emriRajasthanList.size()!=0){
							LOG.info("In EMRI Rajastjan List"+emriRajasthanList.size());
							cachedEmriRajasthan = LoadEmriVehiclesBaseStationDetails.getInstance().retrieve(emriRajasthanList.get(0).getVehicleId());	
							LOG.info("Cached Rajasthan "+cachedEmriRajasthan);
						}
					}	
				}


				HardwareModule hardwareModule = LoadHardwareModuleDetails.getInstance().retrieve(vehicle.getImeiId());
				LOG.debug("vehicleId: "+vehicle.getId().getId());

				LOG.debug("Sending livetrack comet update for the users to which this vehicle is assigned vehicle id is"+liveVehicleObject.getVehicleId()+" MaxSpeed is "+liveVehicleObject.getMaxSpeed());
				/** Sending livetrack update*/
				PositionUpdate positionUpdate = new PositionUpdate(activeLoggedInUsers, liveVehicleObject.getVehicleId(), 
						liveVehicleObject.getLocation().getFirstPoint().y, 
						liveVehicleObject.getLocation().getFirstPoint().x,
						liveVehicleObject.getCourse(), (vehicleStatus == 1) ? liveVehicleObject.getMaxSpeed() : 0,
								LoadVehicleDetails.getInstance().retrieve(liveVehicleObject.getVehicleId()),
								LoadDriverDetails.getInstance().retrieve(liveVehicleObject.getDriverId()), 
								liveVehicleObject.getLastUpdatedAt(), 0, vehicle.getStatus(vehicle.getImei()),vehicle.getGroupId(),
								(cachedEmriRajasthan !=null) ? cachedEmriRajasthan.getDistrict() : "Not Available" ,
										(cachedEmriRajasthan !=null) ? cachedEmriRajasthan.getBaseLocation() : "Not Available",
												(cachedEmriRajasthan !=null) ? cachedEmriRajasthan.getCrewNo() : 0L,hardwareModule.getImei(),
														liveVehicleObject.getGpsStrength(), liveVehicleObject.getGsmStrength(), liveVehicleObject.isChargerConnected(),
														liveVehicleObject.getBatteryVoltage(), liveVehicleObject.getLatestButtonPressed());

				/**	Sending vehiclestatus update*/
				double a = liveVehicleObject.getLocation().getFirstPoint().y;
				double b = liveVehicleObject.getLocation().getFirstPoint().x;
				StringBuffer location=new StringBuffer();
				if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_PUSHLET_ENABLED"))){
					Address locationFetch = GeoUtils.fetchNearestLocation(a, b,false);
					location=StringUtils.formulateAddress(locationFetch,liveVehicleObject.getVehicleId(),a,b);
				}else{
					location.append("Trip ");
					location.append(StringUtils.addressFetchDisabled(liveVehicleObject.getVehicleId(),a,b).toString());
				}

				StatusUpdate statusUpdate = new StatusUpdate(activeLoggedInUsers, liveVehicleObject.getVehicleId(), 
						LoadVehicleDetails.getInstance().retrieve(liveVehicleObject.getVehicleId()).getDisplayName(), 
						vehicleStatus, liveVehicleObject.isChargerConnected(), location.toString(), liveVehicleObject.getGpsStrength(), 
						liveVehicleObject.getGsmStrength(), liveVehicleObject.getBatteryVoltage(), 
						liveVehicleObject.getLastUpdatedAt(),vehicle.getGroupId(),(vehicleStatus == 1) ? liveVehicleObject.getMaxSpeed() : 0,
								(cachedEmriRajasthan !=null) ? cachedEmriRajasthan.getDistrict() : "Not Available" ,
										(cachedEmriRajasthan !=null) ? cachedEmriRajasthan.getBaseLocation() : "Not Available",
												(cachedEmriRajasthan !=null) ? cachedEmriRajasthan.getCrewNo() : 0L,hardwareModule.getImei());


				/** Sending vehicle map report update event */
				VehicleMapUpdate vehicleMapUpdate = new VehicleMapUpdate(activeLoggedInUsers, liveVehicleObject.getVehicleId(), 
						LoadVehicleDetails.getInstance().retrieve(liveVehicleObject.getVehicleId()).getDisplayName(),
						liveVehicleObject.getLocation().getFirstPoint().y, liveVehicleObject.getLocation().getFirstPoint().x, 
						"", liveVehicleObject.getSpeedLimit(), liveVehicleObject.getGpsStrength(), liveVehicleObject.getGsmStrength(), 
						liveVehicleObject.getCourse(), liveVehicleObject.getLastUpdatedAt(),vehicle.getGroupId(),
						(cachedEmriRajasthan !=null) ? cachedEmriRajasthan.getDistrict() : "Not Available" ,
								(cachedEmriRajasthan !=null) ? cachedEmriRajasthan.getBaseLocation() : "Not Available",
										(cachedEmriRajasthan !=null) ? cachedEmriRajasthan.getCrewNo() : 0L,hardwareModule.getImei());

				List<IStreamUpdate> updates = new ArrayList<IStreamUpdate>();

				if (positionUpdate != null) {
					updates.add(positionUpdate);
				}
				if(statusUpdate != null){
					updates.add(statusUpdate);
				}
				if(vehicleMapUpdate != null){
					updates.add(vehicleMapUpdate);
				}
				CometQueue.getCometQueue().addStreamUpdates(updates);
			}
			LOG.debug("Successfully sent pushlet event");
		} catch(Exception e){
			LOG.error("Error while sending pushlet event",e);
		}
	}

	@Override
	public void onException(JMSException exception) {
		LOG.error("Error while procession Pushlet through activeMQ",exception);
	}

	@Override
	public void onMessage(Message message) {
		/**
		 * Add the Message from the activeMQ to the local pool and 
		 * make the thread to notify
		 * 
		 */
		//Forsecasted check ..it may happen/may not
		if(message==null){
			LOG.error("Message is null");
			return;
		}

		ObjectMessage msg = (ObjectMessage) message;
		try {
			if(msg.getObject() != null){
				LiveVehicleStatus liveVehicleStatus = (LiveVehicleStatus) msg.getObject();
				LOG.info("received: " + msg.getObject().toString());
				sendEvent(liveVehicleStatus);
				/*else{
					PacketProcessingException packetException = new PacketProcessingException(PacketProcessingException.PacketErrorCode.QUEUE_FULL);
					LOG.error("Skipped adding from IMEI : "+dataBean.getImei(), packetException);		
					if(DataProcessorParameters.IS_PACKET_BACKUP_ENABLED){
						//As the data pool is full writing the data packet into a file
						ReadWriteJsonToFIle.writeDataPacketToFile(dataBean);
					}
				}*/
			}else{
				LOG.info("Received message is null");
			}
		} catch (JMSException ex) {
			LOG.error("",ex);
		}
	}

}