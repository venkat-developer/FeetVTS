package com.i10n.fleet.web.controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.postgis.Geometry;
import org.postgis.Point;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.LiveVehicleStatusDaoImp;
import com.i10n.db.dao.TripDaoImp;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.Trip;
import com.i10n.db.entity.User;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;
import com.i10n.dbCacheManager.LoadTripDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.providers.mock.LogsDataProvider;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * 
 * @author DVasudeva
 *
 */
@SuppressWarnings("unused")
public class TripSettingsController extends SimpleFormController {

	private static Logger LOG = Logger.getLogger(TripSettingsController.class);

	public enum TripSettings {
		TRIP_SETTINGS("trip_settings"),STATUS_TYPE("status_type"),TRIP_SETTINGS_EDIT_IDLE("trip_settings_edit_idle"),
		TRIP_SETTINGS_EDIT_SPEED("trip_settings_edit_speed");

		private static Map<String, TripSettings> lookup = new HashMap<String, TripSettings>();
		private String val;

		private TripSettings(String val) {
			this.val = val;
		}

		public String getVal() {
			return val;
		}

		public static TripSettings get(String val) {
			return lookup.get(val);
		}

		static {
			for (TripSettings s : EnumSet.allOf(TripSettings.class)) {
				lookup.put(s.getVal(), s);
			}
		}
	}

	/**
	 * Handles login/logout requests and checks redirects to appproaprate view.
	 * If its a logout request, it removes the session attribute and redirects
	 * to login page. If a login request handling is passed to
	 * {@link #onSubmit(Object)} which on success returns a {@link RedirectView}
	 * to {@link #getSuccessView()} page and then finally adds the currents user
	 * as a session attribute. This will be reimplemented once the user database
	 * is available.
	 *
	 * @see SimpleFormController#handleRequest(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Override
	public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = processRequest(request);
		return modelAndView;

	}

	/**
	 * Handles the login submit requests. It will validate if the user/password
	 * are matching and on success will return a {@link RedirectView} to
	 * {@link #getSuccessView()} page.
	 *
	 */

	/**
	 * Handles the login submit requests. It will validate if the user/password
	 * are matching and on success will return a {@link RedirectView} to
	 * {@link #getSuccessView()} page.
	 *
	 */
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
	throws Exception {
		ModelAndView modelAndView = processRequest(request);
		return modelAndView;
	}

	@SuppressWarnings("deprecation")
	private ModelAndView processRequest(HttpServletRequest request) {
		ModelAndView modelAndView = null;
		String action;
		String operation = request.getParameter("command_type");

		switch (TripSettings.get(operation)) {
		case TRIP_SETTINGS: {
			//LOG.debug("In tripSetting Controller");
			String vacantVehicle = request.getParameter("vacant-vehilce");
			String localTime = request.getParameter("localTime");
			//Date clientTime = new Date(localTime);
			String[] vacantVehicleArray = vacantVehicle.split("-");
			String vacantVehicleVariable = vacantVehicleArray[vacantVehicleArray.length-1];
			Long vacantVehicleId=Long.parseLong(StringUtils.stripCommas(vacantVehicleVariable));
			String vacantDriver= request.getParameter("vacant-driver");
			Long vacantDriverId=Long.parseLong(StringUtils.stripCommas(vacantDriver.trim()));
			//			String geoFenceLimit= request.getParameter("geo-fence-limit");
			String speedlimit = request.getParameter("speed-limit");
			Float speedLimit = Float.parseFloat(speedlimit.trim());
			String idlePointTimeLimit = request.getParameter("idle-point-time-limit");
			String tripName=request.getParameter("tripName");

			int idlePointTimeLimitint = Integer.parseInt(idlePointTimeLimit.trim());
			Trip trip= new Trip(speedLimit, vacantVehicleId, vacantDriverId,idlePointTimeLimitint,tripName);
			try {
				trip=((TripDaoImp)DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).insert(trip);
				/* Update to Trip Cache */
				LoadTripDetails.getInstance().getDetailsForNewlyAddedTrips(trip.getId().getId());

				Point p=new org.postgis.Point(77.632957, 12.901421);
				Long tripId=trip.getId().getId();
				Geometry location = (Geometry)p;
				Date lastupdated = Calendar.getInstance().getTime();
				Date adjustedDate = DateUtils.adjustToServerTime(lastupdated, new Date());
				LiveVehicleStatus liveVehicle = new LiveVehicleStatus(tripId, location, 0, 0, 0, 0, true, 0, 0, 0, 0, true, 0, 0, 0, 0, false, 0, 0, 0, " ",
						new Date(), 0, new Date(), false);
				liveVehicle = ((LiveVehicleStatusDaoImp)DBManager.getInstance().getDao(DAOEnum.LIVE_VEHICLE_STATUS_DAO)).insert(liveVehicle);
				String imei=((LiveVehicleStatusDaoImp)DBManager.getInstance().getDao(DAOEnum.LIVE_VEHICLE_STATUS_DAO)).getImeiUsingTripId(trip.getId().getId());
				if((imei!=null)){
					//LOG.debug("Imei is not null and IMEI is "+imei);
					LoadLiveVehicleStatusRecord.getInstance().addLiveObjectToCache(liveVehicle,imei);
				}
				LoadLiveVehicleStatusRecord.getInstance().retrieve(imei);
				if (trip!=null) {
					modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
				}
			}
			catch(Exception e){
				LOG.error("", e);
			}
		}
		break;

		case STATUS_TYPE:{
			String status = request.getParameter("status");
			Long tripId = Long.parseLong(StringUtils.getValueFromKVP(request.getParameter("tripId")));
			Trip trip = LoadTripDetails.getInstance().retrieve(tripId);
			if(status.equals("start")){  
				try {
					((TripDaoImp)DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).updateStartStatus('t', tripId);
					trip.setActiveTrip(true);
				} catch (NumberFormatException e) {
					LOG.error("", e);
				} catch (OperationNotSupportedException e) {
					LOG.error("", e);
				}
				action=" HAS STARTED A TRIP "+tripId;
				StringUtils.insertLogs(action);
			}
			else if(status.equals("pause")){
				try {
					((TripDaoImp)DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).updatePauseStatus('f', tripId);
					trip.setActiveTrip(false);
				} catch (NumberFormatException e) {
					LOG.error("", e);
				} catch (OperationNotSupportedException e) {
					LOG.error("", e);
				}
				action=" HAS PAUSED A TRIP "+tripId;
				StringUtils.insertLogs(action);
			}
			else if( status.equals("stop")) {
				String localTime = request.getParameter("localTime");
				Date clientTime = new Date(localTime);
				try {
					((TripDaoImp)DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).updateStopStatus(tripId,clientTime);
					trip.setEndDate(clientTime);
				} catch (NumberFormatException e) {
					LOG.error("", e);
				} catch (OperationNotSupportedException e) {
					LOG.error("", e);
				}
				action=" HAS STOPPED A TRIP ";
				StringUtils.insertLogs(action);
			}
			LoadTripDetails.getInstance().cachedTrips.put(tripId, trip);
			/* Update to Trip Cache */
			LoadLiveVehicleStatusRecord.getInstance().refresh();
		}
		break;

		case TRIP_SETTINGS_EDIT_SPEED:{
			LogsDataProvider log = new LogsDataProvider();
			String localTime = request.getParameter("localTime");
			String remoteip = request.getRemoteAddr();
			//Date clientTime = new Date(localTime);
			Calendar cal = Calendar.getInstance();
			//Date dateAdjusted = DateUtils.adjustToServerTime(cal.getTime(), clientTime);
			User currentUser = SessionUtils.getCurrentlyLoggedInUser();
			Long uuid = null;
			String username = null;
			String sSpeed = request.getParameter("speed");
			Long tripId = Long.parseLong(StringUtils.getValueFromKVP(request.getParameter("tripId")));
			Float speed = Float.parseFloat(StringUtils.getValueFromKVP(sSpeed.trim()));
			try{
				((TripDaoImp)DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).updateSpeed(tripId,speed);
				/* Update to Trip Cache */
				Trip trip = LoadTripDetails.getInstance().retrieve(tripId);
				trip.setSpeedLimit(speed);
				LoadTripDetails.getInstance().store(trip);
			}catch(Exception e){
				LOG.error("", e);
			}
			uuid = currentUser.getId();
			Long userid1 = Long.parseLong(StringUtils.stripCommas(uuid + ""));
			Trip trip=LoadTripDetails.getInstance().retrieve(tripId);
			username = currentUser.getLogin();
			action = username.toUpperCase()+ " HAS UPDATED SPEED LIMIT FOR "+
			LoadVehicleDetails.getInstance().retrieve(trip.getVehicleId()).getDisplayName();
			log.getupDatedlogs(userid1, new Date(), remoteip, username, action);
			break;
		}

		case TRIP_SETTINGS_EDIT_IDLE :{
			String sIdle = request.getParameter("idle");
			Integer idle = Integer.parseInt(StringUtils.getValueFromKVP(sIdle.trim()));
			Long tripId = Long.parseLong(StringUtils.getValueFromKVP(request.getParameter("tripId")));
			try{
				((TripDaoImp)DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).updateIdleLimit(tripId,idle);
				/* Update to Trip Cache */
				Trip trip = LoadTripDetails.getInstance().retrieve(tripId);
				trip.setIdlePointsTimeLimit(idle);
				LoadTripDetails.getInstance().store(trip);
			}catch(Exception e){
				LOG.error("", e);
			}
		}
		break;

		default :
			break;
		}
		return modelAndView;
	}
}

