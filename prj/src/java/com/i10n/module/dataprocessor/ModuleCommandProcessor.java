package com.i10n.module.dataprocessor;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.postgis.Geometry;
import org.postgis.Point;

import com.i10n.db.dao.AccelerometerLatestPacketDetailDAOImpl;
import com.i10n.db.dao.AlertInsertManager;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.DriverDaoImp;
import com.i10n.db.dao.EtaDisplayDaoImp;
import com.i10n.db.dao.EtdOfVehiclesToStopsDaoImp;
import com.i10n.db.dao.RoutesDaoImp;
import com.i10n.db.dao.StopHistoryDaoImp;
import com.i10n.db.dao.VehicleToRouteScheduleDaoImp;
import com.i10n.db.entity.AlertOrViolation;
import com.i10n.db.entity.Driver;
import com.i10n.db.entity.EtaDisplay;
import com.i10n.db.entity.EtdOfVehiclesToStops;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.Routes;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.VehicleToRouteSchedule;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;
import com.i10n.dbCacheManager.LoadRouteSchedule;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.dbCacheManager.LoadVehicleRouteAssociationDetails;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.providers.mock.EtaDisplayDataHelper;
import com.i10n.module.command.AlertOrViolationCommand;
import com.i10n.module.command.AuthenticationCommandBean;
import com.i10n.module.command.DefaultSuccessResponse;
import com.i10n.module.command.DeleteDriverCommand;
import com.i10n.module.command.ETAAryaCommand;
import com.i10n.module.command.ETAChineseCommand;
import com.i10n.module.command.ETAGujratCommand;
import com.i10n.module.command.ETAGujratResponse;
import com.i10n.module.command.EngineStartStopCommandBean;
import com.i10n.module.command.ICommandBean;
import com.i10n.module.command.IResponse;
import com.i10n.module.command.MBMCDynamicRouteAssignmentCommand;
import com.i10n.module.command.NewDriverAddedCommandBean;
import com.i10n.module.command.PollCommandBean;
import com.i10n.module.command.SequenceRequestCommandBean;
import com.i10n.module.command.SmartCardUpdateCommand;
import com.i10n.module.led.AryaLEDHandler;
import com.i10n.module.led.ChineseLEDHandler;

/**
 * Processes the commands received from the module
 * @author sreekanth 
 * @update dharmaraju v
 */
public class ModuleCommandProcessor {

	public static String drivername;
	public static Long vehicleid;
	public static String imei;
	private static String alertType;
	//	private static AlertDaoImpl AlertDao = ((AlertDaoImpl) DBManager.getInstance().getDao(DAOEnum.ALERT_DAO));
	private static final String SMART_CARD = "SmartCardAlert";
	private static Logger LOG = Logger.getLogger(ModuleCommandProcessor.class);

	public static IResponse processCommand(ICommandBean cmd) {
		IResponse resp = new DefaultSuccessResponse();
		switch (cmd.getCommandID()) {
		/*case AUTHENTICATION: {
			resp = processAuthenticationCommand((AuthenticationCommandBean) cmd);
		}
		break;
		case POLL: {
			resp = processPollCommand((PollCommandBean) cmd);
		}
		break;
		case NEWDRIVERADDED: {
			resp = processNewDriverAddedCommand((NewDriverAddedCommandBean) cmd);
		}
		break;
		case ENGINESTARTSTOP: {
			resp = processEngineStartStopCommand((EngineStartStopCommandBean) cmd);
		}
		break;
		case DELETEDRIVER: {
			resp = processDeleteDriverCommand((DeleteDriverCommand) cmd);
		}
		break;
		case SMARTCARDALERT: {
			resp = processSmartCardCommand((SmartCardUpdateCommand) cmd);
		}
		break;*/
		case ETA_GUJRAT: {
			resp = processETAGujratCommand((ETAGujratCommand) cmd);
		}
		break;

		case ETA_BHOPAL_CHINESE: {
			resp = processETABhopalCommandForChineseDisplay((ETAChineseCommand) cmd);
		}
		break;

		case ETA_BHOPAL_ARYA : {
			resp = processETABhopalCommandForAryaDisplay((ETAAryaCommand)cmd);
		}
		break;

		case SEQUENCEREQUEST:{
			resp = processSequenceRequest((SequenceRequestCommandBean)cmd);
		}
		break;

		case ALERT_OR_VIOLATION : {
			resp = processAlertsCommandRequest((AlertOrViolationCommand)cmd);
		}
		break;

		case MBMC_DYNAMIC_ROUTE_ASSIGN : {
			resp = processDynamicRouteAssignmentRequest((MBMCDynamicRouteAssignmentCommand) cmd);
		}
		break;

		case DEFAULT: {
			// Return default success response
		}
		default:
			break;
		}
		return resp;
	}

	/**
	 * Process the dynamic route assignment
	 * @param cmd
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private static IResponse processDynamicRouteAssignmentRequest(MBMCDynamicRouteAssignmentCommand cmd) {

		String requestedKey = getRouteScheduleKey(cmd);
		/*
		 * 1) Get the vehicle assigned to the request sending route scheduler device
		 * 2) Stop the active route of the vehicle associated if the route and schedule doesn't match 
		 * 3) Prepare to start the route scheduled
		 */

		//	1) Get the vehicle assigned to the request sending route scheduler device
		Vehicle vehicle = LoadVehicleDetails.getInstance().retrieve(LoadLiveVehicleStatusRecord.getInstance().retrieve(cmd.getIMEI()).getVehicleId());
		Time scheduleTime = new Time(cmd.getRouteScheduleTime().getHours(), cmd.getRouteScheduleTime().getMinutes(), 0);
		VehicleToRouteSchedule vehicleToRouteSchedule = new VehicleToRouteSchedule(vehicle.getId().getId(), cmd.getRouteId(),
				scheduleTime);

		//	2) Stop the active route of the vehicle associated if the route and schedule doesn't match
		ConcurrentHashMap<VehicleToRouteSchedule, Boolean> vehicleRouteScheduleStatusList =  
				LoadVehicleRouteAssociationDetails.getInstance().retrieve(vehicle.getId().getId());
		VehicleToRouteSchedule activeVehicleRouteSchedule = null;
		for(VehicleToRouteSchedule vehRouteSchedule : vehicleRouteScheduleStatusList.keySet()){
			if(!vehicleToRouteSchedule.equalsEntity(vehRouteSchedule)){
				if(vehicleRouteScheduleStatusList.get(vehRouteSchedule)){
					//	Got the active route schedule for the current vehicle
					activeVehicleRouteSchedule = vehRouteSchedule;
					stopTheActiveRoute(activeVehicleRouteSchedule, cmd);
					break;
				}
			} else {
				if(vehicleRouteScheduleStatusList.get(vehicleToRouteSchedule))
					LOG.debug("Reqeuested route schedule already map and its active as well");
				LOG.debug("Vehicle Route Schedule map already exist .. This is a duplicate request");
				return new DefaultSuccessResponse();
			}
		}

		/*
		 * Prepare to start the route scheduled
		 * 
		 * 1) Get to know the correctness of the schedule time
		 * 2) Map the vehicle to the route schedule by making entry in VehicleToRouteSchedule
		 * 3) Update the mapping done in cache  
		 */

		for(String routeScheduleKey: LoadRouteSchedule.getInstance().cacheRouteSchedule.keySet()){
			/*
			 * Check the correctness of the requested schedule time
			 */
			if(requestedKey.equalsIgnoreCase(routeScheduleKey)){
				/*
				 * Insert into the DB 
				 */
				try {
					((VehicleToRouteScheduleDaoImp)DBManager.getInstance().getDao(DAOEnum.VEHICLE_TO_ROUTE_SCHEDULE_DAO)).insert(vehicleToRouteSchedule);
				} catch (OperationNotSupportedException e) {
					LOG.error(e);
				}

				/*
				 * Update the cache
				 */
				ConcurrentHashMap<VehicleToRouteSchedule,Boolean> vehicleRouteScheduleStatus =
						LoadVehicleRouteAssociationDetails.getInstance().retrieve(vehicle.getId().getId());
				if(vehicleRouteScheduleStatus == null){
					vehicleRouteScheduleStatus = new ConcurrentHashMap<VehicleToRouteSchedule,Boolean>();
				}
				vehicleRouteScheduleStatus.put(vehicleToRouteSchedule, false);
				LoadVehicleRouteAssociationDetails.getInstance().cacheVehicleToRouteAssociation.put(vehicle.getId().getId(), vehicleRouteScheduleStatus);

				return new DefaultSuccessResponse();
			}
		}
		LOG.error("Request is not a correct one ");
		return new DefaultSuccessResponse();
	}

	private static void stopTheActiveRoute(VehicleToRouteSchedule activeVehicleRouteSchedule, MBMCDynamicRouteAssignmentCommand cmd) {
		StopHistoryDaoImp stopHistoryDao = (StopHistoryDaoImp) DBManager.getInstance().getDao(DAOEnum.STOPHISTORY_DAO);
		stopHistoryDao.deactivateRoute(activeVehicleRouteSchedule.getVehicleId(), activeVehicleRouteSchedule.getRouteId());
		Long latestTrackHistoryIdForTheRequestedModule = LoadLiveVehicleStatusRecord.getInstance().retrieve(cmd.getIMEI()).getTrackHistoryRowID();
		new EtaDisplayDataHelper().insertOrUpdateRouteTrack(cmd.getIMEI(), activeVehicleRouteSchedule.getVehicleId(), 
				activeVehicleRouteSchedule.getRouteId(), 0, cmd.getPacketTime(), latestTrackHistoryIdForTheRequestedModule);

		/*
		 * Update the cache
		 */
		ConcurrentHashMap<VehicleToRouteSchedule,Boolean> vehicleRouteScheduleStatus =
				LoadVehicleRouteAssociationDetails.getInstance().retrieve(activeVehicleRouteSchedule.getVehicleId());
		if(vehicleRouteScheduleStatus == null){
			vehicleRouteScheduleStatus = new ConcurrentHashMap<VehicleToRouteSchedule,Boolean>();
		}

		vehicleRouteScheduleStatus.put(activeVehicleRouteSchedule, false);
		LoadVehicleRouteAssociationDetails.getInstance().cacheVehicleToRouteAssociation.
		put(activeVehicleRouteSchedule.getVehicleId(), vehicleRouteScheduleStatus);
}

	@SuppressWarnings("deprecation")
	private static String getRouteScheduleKey(MBMCDynamicRouteAssignmentCommand cmd) {
		String hours = cmd.getRouteScheduleTime().getHours()+"";
		String minutes = cmd.getRouteScheduleTime().getMinutes()+"";
		if(hours.length() == 1)
			hours = "0"+hours;
		if(minutes.length() == 1)
			minutes = "0"+minutes;

		return cmd.getRouteId()+"-"+hours+":"+minutes;
	}

	/**
	 * Process the alert command request and update the alerts received in DB
	 * @param cmd
	 * @return
	 */
	private static IResponse processAlertsCommandRequest(AlertOrViolationCommand cmd) {
		LiveVehicleStatus liveVehicleStatus = LoadLiveVehicleStatusRecord.getInstance().retrieve(cmd.getIMEI());
		try{
			Point point = new Point(cmd.getLongitude(), cmd.getLatitude());
			Geometry alertLocation = (Geometry)point;
			if(liveVehicleStatus!=null){
				AlertOrViolation alert = new AlertOrViolation(/*new LongPrimaryKey(alertId),*/ liveVehicleStatus.getVehicleId(), 
						liveVehicleStatus.getDriverId(), cmd.getAlertTime(), new Date(), cmd.getAlertType(), "", 
						alertLocation);	
				if(cmd.getAlertTypeValue()!=null){
					alert.setAlertTypeValue(cmd.getAlertTypeValue()+"");
				}
				alert.setAlertLocationReferenceId(0L);
				/*try{
					if(DataProcessorParameters.IS_ADDRESS_FETCH_VIOLATIONS_ENABLED){
						if(alert.getAlertType()!=29 || alert.getAlertType()!=30 || alert.getAlertType()!=31){
							Address address = GeoUtils.fetchNearestLocation(cmd.getLatitude(), cmd.getLongitude(), false);
							if(address != null){
								alert.setAlertLocationText(address.toString());
								alert.setAlertLocationReferenceId(address.getId());
							}	
						}else{
							alert.setAlertLocationText("Lat : "+cmd.getLatitude()+", Lng : "+cmd.getLongitude());
						}
					} else{
						alert.setAlertLocationText("Lat : "+cmd.getLatitude()+", Lng : "+cmd.getLongitude());
					}	
				}catch (Exception e) {
					LOG.error("Error while fetching address fetch ",e);
				}*/
				alert.setAlertLocationText("Lat : "+cmd.getLatitude()+", Lng : "+cmd.getLongitude());

				AlertInsertManager alertInsertManager = new AlertInsertManager(alert);
				Thread alertInsertionThread = new Thread(alertInsertManager);
				alertInsertionThread.start();
			}		
		}catch (Exception e) {
			LOG.error("Error while processing alert command packet ",e);
		}
		return new DefaultSuccessResponse();
	}

	/**
	 * Processes Bhopal ETA command and saves the formulated bitmap response for Arya LED Display.
	 * @param cmd
	 * @return
	 */
	private static IResponse processETABhopalCommandForAryaDisplay(ETAAryaCommand cmd) {
		return (new AryaLEDHandler()).getLEDData(cmd);
	}

	/**
	 * Processes Bhopal ETA command and saves the formulated bitmap response for Chinese LED Display.
	 * @param cmd
	 * @return
	 */
	private static IResponse processETABhopalCommandForChineseDisplay(ETAChineseCommand cmd) {
		return (new ChineseLEDHandler()).getLEDData(cmd);
	}

	@SuppressWarnings("unused")
	private static IResponse processNewDriverAddedCommand(NewDriverAddedCommandBean command) {
		LOG.debug("Processing the new driver added command");
		return new DefaultSuccessResponse();
	}

	private static IResponse processSequenceRequest(SequenceRequestCommandBean cmd) {
		return cmd.getResponse();
	}

	/**
	 * Processes poll command
	 * 
	 * @param pollCommand
	 * @return PollCommandResponse - If a new driver is enrolled via iPhone UI
	 *         and waiting for module's response DeleteDriverPollResponse - If a
	 *         driver is marked for deletion via iPhone UI and waiting for
	 *         module's response DeafultSuccessResponse - If neither of the
	 *         above are valid
	 */
	@SuppressWarnings("unused")
	private static IResponse processPollCommand(PollCommandBean pollCommand) {
		LOG.debug("Processing the poll command");
		return new DefaultSuccessResponse();
	}

	/**
	 * Processing the ETA command to return time for ETA Display.
	 * 
	 * @param cmd
	 * @return
	 * @author dharmaraju.v
	 */
	@SuppressWarnings("deprecation")
	private static IResponse processETAGujratCommand(ETAGujratCommand cmd) {
		List<EtaDisplay> etaDisplayList = ((EtaDisplayDaoImp) DBManager.getInstance().getDao(DAOEnum.ETA_DISPLAY_DAO)).selectByStopIdAndRouteId(cmd.getStopId(), cmd.getRouteId());
		ETAGujratResponse sasResponse = null;
		/** List of bus stops mapping to gujrati text id **/
		String[][] gujratiStopIdList = { { "KrishnaNagar", "41" },
				{ "ahmedabad", "01" }, { "dhagandhra", "42" },
				{ "Maliya", "43" }, { "Bhachau", "46" },
				{ "gandhidham", "44" }, { "Anjar", "36" }, { "Mundra", "45" } };

		/** If any vehicle approaching then send the details to display **/
		if (etaDisplayList.size() != 0) {
			List<Routes> routes = ((RoutesDaoImp) DBManager.getInstance().getDao(DAOEnum.ROUTES_DAO)).selectByPrimaryKey(new LongPrimaryKey(cmd.getRouteId()));
			String gujratiStopId = "";
			for (int i = 0; i < gujratiStopIdList.length; i++) {
				if (routes.get(0).getEndPoint().equalsIgnoreCase(gujratiStopIdList[i][0])) {
					gujratiStopId = gujratiStopIdList[i][1];
					break;
				}
			}
			List<EtdOfVehiclesToStops> etdOfVehiclesToStops = ((EtdOfVehiclesToStopsDaoImp) DBManager
					.getInstance().getDao(DAOEnum.ETD_OF_VEHICLESTOSTOPS))
					.getByVehicleIdAndStopId(etaDisplayList.get(0).getVehicleId(), etaDisplayList.get(0).getStopId());
			Calendar calEta = Calendar.getInstance();
			calEta.add(Calendar.MINUTE, etaDisplayList.get(0).getArrivalTime());
			Date etaTime = calEta.getTime();
			calEta.add(Calendar.MINUTE, etdOfVehiclesToStops.get(0).getEtdMinutes());
			Date etdTime = calEta.getTime();
			String etaMinutes = "";
			String etdMinutes = "";
			String etaHours = "";
			String etdHours = "";
			if (etaTime.getMinutes() < 10) {
				etaMinutes = "0" + etaTime.getMinutes();
			} else {
				etaMinutes = etaTime.getMinutes() + "";
			}
			if (etdTime.getMinutes() < 10) {
				etdMinutes = "0" + etdTime.getMinutes();
			} else {
				etdMinutes = etdTime.getMinutes() + "";
			}
			if (etaTime.getHours() < 10) {
				etaHours = "0" + etaTime.getHours();
			} else {
				etaHours = etaTime.getHours() + "";
			}
			if (etdTime.getHours() < 10) {
				etdHours = "0" + etdTime.getHours();
			} else {
				etdHours = etdTime.getHours() + "";
			}
			sasResponse = new ETAGujratResponse(routes.get(0).getRouteName(), gujratiStopId, etaHours + ":" + etaMinutes, etdHours + ":"+ etdMinutes);
		} else {
			/** No Vehicle Approaching the stop **/
			sasResponse = new ETAGujratResponse();
		}
		return sasResponse;
	}

	@SuppressWarnings("unused")
	private static IResponse processSmartCardCommand(SmartCardUpdateCommand smartcard) {
		LOG.debug("Processing the Smartcard Command");
		LOG.debug("The smartcard data to be processed is"+ smartcard.toString());
		AccelerometerLatestPacketDetailDAOImpl latestpacketDao = (AccelerometerLatestPacketDetailDAOImpl) DBManager
				.getInstance().getDao(DAOEnum.AC_LATEST_PACKET_DETAIL_DAO);

		smartcard = latestpacketDao.insertSmartcardDetails(smartcard);

		if (smartcard != null) {
			List<Driver> resultset = ((DriverDaoImp) DBManager.getInstance().getDao(DAOEnum.DRIVER_DAO)).selectDriverName(smartcard.getM_smartcardid());
			drivername = resultset.get(0).getFirstName();
			vehicleid = resultset.get(0).getVehicleId();
			imei = resultset.get(0).getImei();
			alertType = SMART_CARD;
			insertAlertIntoTable(vehicleid, imei, drivername, alertType);
		}
		return new DefaultSuccessResponse();
	}

	private static void insertAlertIntoTable(long vehicleId, String Imei,
			String driverName, String alertType) {
		// TODO : Do something
	}

	@SuppressWarnings("unused")
	private static IResponse processAuthenticationCommand(AuthenticationCommandBean command) {
		LOG.debug("Processing the Authentication command");
		return new DefaultSuccessResponse();
	}

	@SuppressWarnings("unused")
	private static IResponse processEngineStartStopCommand(EngineStartStopCommandBean command) {
		LOG.debug("Processing the Engine Start/Stop command");
		return new DefaultSuccessResponse();
	}

	@SuppressWarnings("unused")
	private static IResponse processDeleteDriverCommand(DeleteDriverCommand deleteDriverCommand) {
		LOG.debug("Processing the Delete Driver command");
		return new DefaultSuccessResponse();
	}
}
