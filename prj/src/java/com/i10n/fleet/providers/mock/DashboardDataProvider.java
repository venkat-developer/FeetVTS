package com.i10n.fleet.providers.mock;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import com.i10n.db.dao.ACLVehicleDaoImp;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.LiveVehicleStatusDaoImp;
import com.i10n.db.dao.LogsDaoImp;
import com.i10n.db.dao.UserDaoImp;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.LiveVehicleStatus.VehicleStatus;
import com.i10n.db.entity.Logs;
import com.i10n.db.entity.User;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadAclVehicleDetails;
import com.i10n.dbCacheManager.LoadUserDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.providers.managers.AlertManager;
import com.i10n.fleet.providers.managers.IAlertManager;
import com.i10n.fleet.providers.mock.managers.ILiveVehicleStatusManager;
import com.i10n.fleet.providers.mock.managers.IVehicleManager;
import com.i10n.fleet.providers.mock.managers.LiveVehicleStatusManager;
import com.i10n.fleet.providers.mock.managers.VehicleManager;
import com.i10n.fleet.util.Constants;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.util.Helper;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * Mock : Mock Data Provider for Dashboard View. This class will be removed in
 * future.
 * 
 * @author Sabarish
 * 
 */
public class DashboardDataProvider implements IDataProvider {
	private Logger LOG =Logger.getLogger(DashboardDataProvider.class);

	private static final int MAX_VACANT_VEHICLES = 5;
	private static final int MAX_HEALTH_VEHICLES = 5;
	private static final int MAX_VIOLATIONS_VEHICLES = 5;
	private static final String ADMIN_GROUP = "admin";

	private IVehicleManager m_vehicleManager = new VehicleManager();

	private ILiveVehicleStatusManager m_liveVehicleStatus = new LiveVehicleStatusManager();
	private IAlertManager m_alertStatus = new AlertManager();
	private String localTimeZone = null, localTime = null;
	//	private Date clientTime;

	/**
	 * @see IDataProvider#getDataset(RequestParameters)
	 */
	public IDataset getDataset(RequestParameters params) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		localTime = (String)WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_TIME);
		localTimeZone = (String)WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_TIMEZone);
		String group=(String)WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_GROUP);

		IDataset result = new Dataset();
		if (group!=ADMIN_GROUP){
			result.put("vehicles.vacant", getVacantData());
			/*LiveVehicleStatus vehicleStatusCount = ((LiveVehicleStatusDaoImp) DBManager.getInstance().
					getDao(DAOEnum.LIVE_VEHICLE_STATUS_DAO)).getVehicleStatusCount(SessionUtils.getCurrentlyLoggedInUser().getId());*/
			List<LiveVehicleStatus> liveVehicleStatusList = ((LiveVehicleStatusDaoImp) DBManager.getInstance().
					getDao(DAOEnum.LIVE_VEHICLE_STATUS_DAO)).fetchLiveVehicleStatusOfUser(SessionUtils.getCurrentlyLoggedInUser().getId());
			HashMap<String, Integer> statusCount = new HashMap<String, Integer>();
			for(LiveVehicleStatus liveStatus : liveVehicleStatusList){
				int count = 0;
				VehicleStatus vehicleStatus = new DashboardDataProvider().getVehicleStatus(liveStatus);
				if(statusCount.get(vehicleStatus.toString()) != null){
					count = statusCount.get(vehicleStatus.toString());
					statusCount.put(vehicleStatus.toString(), ++count);
				} else {
					statusCount.put(vehicleStatus.toString(), ++count);
				}
			}

			int subTotal =0;
			int vehiclesOnlineCount= 0;
			if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_SHSM_CLIENT"))){
				vehiclesOnlineCount = statusCount.get(VehicleStatus.MOVING.toString()) == null ? 0 : statusCount.get(VehicleStatus.MOVING.toString());
				result.put("vehiclesonlinecount", vehiclesOnlineCount);
			}else{
				vehiclesOnlineCount = statusCount.get(VehicleStatus.ONLINE.toString()) == null ? 0 : statusCount.get(VehicleStatus.ONLINE.toString());
				result.put("vehiclesonlinecount",vehiclesOnlineCount);
			}

			int offroadCount = statusCount.get(VehicleStatus.OFFROAD.toString()) == null ? 0 
					: statusCount.get(VehicleStatus.OFFROAD.toString()); 

			int offlinecDCCount = statusCount.get(VehicleStatus.OFFLINE_CHARGER_DISCONNECTED.toString()) == null ? 0 
					: statusCount.get(VehicleStatus.OFFLINE_CHARGER_DISCONNECTED.toString());

			int offlineLowGpsCount = statusCount.get(VehicleStatus.OFFLINE_LOW_GPS.toString()) == null ? 0 
					: statusCount.get(VehicleStatus.OFFLINE_LOW_GPS.toString());

			int offlineLowGsmCount = statusCount.get(VehicleStatus.OFFLINE_LOW_GSM.toString()) == null ? 0
					: statusCount.get(VehicleStatus.OFFLINE_LOW_GSM.toString());

			int offlineCount = statusCount.get(VehicleStatus.OFFLINE.toString()) == null ? 0 
					: statusCount.get(VehicleStatus.OFFLINE.toString());

			
			LoadUserDetails.getInstance().getDataForNewlyAddedUser(SessionUtils.getCurrentlyLoggedInUser().getId());
			User loggedInUser = LoadUserDetails.getInstance().retrieve(SessionUtils.getCurrentlyLoggedInUser().getId());
			
			if(offlineCount > loggedInUser.getOffroadCount()){
				LOG.info("Offroad Count from the DB for the user : "+loggedInUser.getLogin()
						+" is : "+loggedInUser.getOffroadCount());
				offlineCount -= loggedInUser.getOffroadCount();
				offroadCount += loggedInUser.getOffroadCount();
			}
			int noGPRSCount = loggedInUser.getNoGPRSCount();
			if(offlineCount > noGPRSCount){
				LOG.info("NoGPRS Count from the DB for the user : "+loggedInUser.getLogin()
						+" is : "+loggedInUser.getNoGPRSCount());
				offlineCount -= noGPRSCount;
				subTotal = (vehiclesOnlineCount+offroadCount+offlinecDCCount+offlineLowGpsCount+offlineLowGsmCount+noGPRSCount);
				result.put("vehiclesnogprscount", noGPRSCount);
			} else {
				subTotal = (vehiclesOnlineCount+offroadCount+offlinecDCCount+offlineLowGpsCount+offlineLowGsmCount);
				result.put("vehiclesnogprscount", 0);
			}
			

			int grandTotal = (subTotal+offlineCount);
			
			LOG.info("Sub total is "+subTotal);
			
			result.put("vehiclesoffroadcount",  offroadCount);
			result.put("vehiclesofflinecdccount", offlinecDCCount);
			result.put("vehiclesofflinelowgpscount",  offlineLowGpsCount);
			result.put("vehiclesofflinelowgsmcount", offlineLowGsmCount);
			result.put("subTotal", subTotal);
			result.put("grandTotal", grandTotal);
			result.put("vehiclesofflinecount", offlineCount);

			result.put("vehicles.status", getStatusData());
			result.put("vehicles.health", getVehicleHealthData());
			result.put("violations", getViolationsData());
			result.put("quicklinks", getQuickLinksData(params));
			result.put("groupsdata", getGroupsData(params));
		}else{
			result.put("quicklinks", getQuickLinksData(params));
			result.put("userDetails", getUserDetails());
			result.put("userInforms", getUserInfo());
			result.put("user", getUserdata());
		}
		return result;
	}

	private VehicleStatus getVehicleStatus(LiveVehicleStatus liveVehicleStatus) {
		VehicleStatus statusOfVehicle = VehicleStatus.OFFROAD;
		if (liveVehicleStatus == null) {
			LOG.error("LVOS is neither in cache nor in db");			
			return statusOfVehicle;
		}
		if(liveVehicleStatus.isOffroad()){
			LOG.debug("Vehicle with trip id : "+liveVehicleStatus.getTripId().getId()+" is Offroad");
			return statusOfVehicle;
		}
		long lastupdatedDay = liveVehicleStatus.getLastUpdatedAt().getTime();
		long moduleUpdateTime = liveVehicleStatus.getModuleUpdateTime().getTime();
		Calendar cal = Calendar.getInstance();
		long currDate = cal.getTimeInMillis(); 
		long lastUpdatediff =	currDate-lastupdatedDay; 
		long moduleUpdatediff =	currDate-moduleUpdateTime; 
		double lastUpdatedDiffDays = lastUpdatediff/(24*60*60*1000);
		double moduleUpdatediffDay = moduleUpdatediff/(24*60*60*1000);
		//to prevent negative values
		if(lastUpdatedDiffDays < 1 || moduleUpdatediffDay < 1){
			lastUpdatedDiffDays = -(lastUpdatedDiffDays);
			moduleUpdatediffDay = -(moduleUpdatediffDay);
		}
		if( liveVehicleStatus.isIdle() && (lastUpdatedDiffDays == 0 || moduleUpdatediffDay ==0)){
			// IDLE vehicle counted in ONLINE status 
			if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_SHSM_CLIENT"))){
				statusOfVehicle = VehicleStatus.MOVING;
			}else{
				statusOfVehicle = VehicleStatus.ONLINE;
			}
		} else if( lastUpdatedDiffDays > 0 && moduleUpdatediffDay > 0 ) { 
			statusOfVehicle = VehicleStatus.OFFLINE; 

			if(!liveVehicleStatus.isChargerConnected()){
				statusOfVehicle = VehicleStatus.OFFLINE_CHARGER_DISCONNECTED;
			} else if(liveVehicleStatus.getGsmStrength() < 18){
				statusOfVehicle = VehicleStatus.OFFLINE_LOW_GSM;
			} else if(liveVehicleStatus.getGpsStrength() > 1){					//<= 0.5 || liveVehicleStatus.getGpsStrength() >= 1.5){
				statusOfVehicle = VehicleStatus.OFFLINE_LOW_GPS;
			}

		} else {
			if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_SHSM_CLIENT"))){
				statusOfVehicle = VehicleStatus.MOVING;
			}else{
				statusOfVehicle = VehicleStatus.ONLINE;
			}
		}
		return statusOfVehicle;
	}

	/**
	 * Returns Quick Links Data.
	 * 
	 * @param params
	 * @return
	 */
	public List<IDataset> getUserInfo() {
		List<IDataset> logs = new ArrayList<IDataset>();
		User user = SessionUtils.getCurrentlyLoggedInUser();
		Calendar c = Calendar.getInstance();
		String Userstatus = null;
		IDataset userdata = new Dataset();
		Logs lastlog = null;
		List<User> resultset1 = (((UserDaoImp) (DBManager.getInstance().getDao(DAOEnum.USER_DAO))).getUsersForOwner(user.getId()));

		for (int i = 0; i < resultset1.size(); i++) {
			User     userlogin = resultset1.get(i);
			userdata.put("userid", userlogin.getLogin());
			List<Logs> resultset = ((LogsDaoImp) (DBManager.getInstance().getDao(DAOEnum.LOGS_DAO))).selectLastLogin(userlogin.getId());
			for (int j = 0; j < resultset.size(); j++) {
				lastlog = resultset.get(j);
				long currenttime = c.getTime().getTime();
				long oldtime = lastlog.getDat().getTime();
				long diff = oldtime - currenttime;
				long Day = diff / (24 * 60 * 60 * 1000);
				Day=-(Day);
				if (Day<7) {
					Userstatus = "ACTIVE";
				}      
				else {
					Userstatus = "INACTIVE";
				}
				userdata.put("status", Userstatus);
				if(resultset.size()==2){
					lastlog=resultset.get(1);
				}
				else{
					lastlog=resultset.get(0);
				}
				String actualTimeStr = DateUtils.adjustToClientTime(localTimeZone, lastlog.getDat());
				userdata.put("lastLogin", actualTimeStr);
			}
			if(userdata.get("lastLogin")==null)
				userdata.put("lastLogin", "NO PREIVIOUS LOGIN");
			if(userdata.get("status")==null)
				userdata.put("status", "INACTIVE");             
			Long result = (((ACLVehicleDaoImp) (DBManager.getInstance().getDao(DAOEnum.ACL_VEHICLE_DAO))).selectVehicleCountForUsers(userlogin.getId()));
			userdata.put("vehicleAllotted", result);
			logs.add(userdata);
			userdata = new Dataset();
		}     
		return logs;
	} 

	private IDataset getQuickLinksData(RequestParameters params) {
		IDataset dataset = new Dataset();
		List<String> linkIDs = new ArrayList<String>();
		if (ADMIN_GROUP.equals(params.getParameter(RequestParams.usergroup))) {
			linkIDs.add("controlpanel");
			linkIDs.add("controlpanel.adminmanage");
			linkIDs.add("controlpanel.adminmanage.logs");
		} else {
			linkIDs.add("livetrack.livetrack");
			linkIDs.add("reports.vehiclereport.idlepointsreport");
			if(!Boolean.valueOf(EnvironmentInfo.getProperty("IS_FRS_EMRI_CLIENT"))){
				linkIDs.add("controlpanel");
			}
		}
		dataset.put("links", linkIDs);
		return dataset;
	}
	/**
	 * Displyaing Groups On dashboard.
	 * @param params
	 * @return
	 */
	private IDataset getGroupsData(RequestParameters params) {
		IDataset dataset = new Dataset();
		List<String> linkIDs = new ArrayList<String>();
		Vector< Long> vehiclesList = LoadAclVehicleDetails.getInstance().cacheAclVehicleDetails.get(SessionUtils.getCurrentlyLoggedInUser().getId());
		Map<String, Boolean> processedMap=new HashMap<String, Boolean>();
		for(long vehicleId : vehiclesList){
			Vehicle vehicle = LoadVehicleDetails.getInstance().retrieve(vehicleId);
			if(!processedMap.containsKey(vehicle.getGroupName())){
				LOG.info("Groupd Name : "+vehicle.getGroupName()+" not added to list");
				linkIDs.add(vehicle.getGroupName().toUpperCase());
				processedMap.put(vehicle.getGroupName(), true);
			}
		}
		Collections.sort(linkIDs);
		dataset.put("groups", linkIDs);
		return dataset;
	}

	private IDataset getUserDetails() {
		IDataset userdetails = new Dataset();
		Long userid = SessionUtils.getCurrentlyLoggedInUser().getId();
		String userName = SessionUtils.getCurrentlyLoggedInUser().getFirstname()+ SessionUtils.getCurrentlyLoggedInUser().getLastname();
		userdetails.put("id", userid);
		userdetails.put("name", userName);
		return userdetails;
	}

	/**
	 * returns a {@link IDataset} containing top MAX_VACANT_VEHICLES vacant
	 * vehicles.
	 * 
	 * @return
	 */
	private IDataset getVacantData() {
		IDataset vehicleData = m_vehicleManager.getVacantVehicleForDashBoard();
		IDataset result = new Dataset();
		result.put("vehicles", getTopData(MAX_VACANT_VEHICLES, vehicleData));
		result.put("count", vehicleData.size());
		return result;
	}
	private IDataset getViolationsData() {
		IDataset result = new Dataset();
		IDataset vehiclestatusdata = new Dataset();
		IDataset alertstatusdata = new Dataset();
		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_DASHBOARD_DATA_ENABLED"))){
			result.put("vehiclestatus",getTopViolationsData(MAX_VIOLATIONS_VEHICLES,m_liveVehicleStatus.getDataForDashboard()));
			result.put("alertstatus",getTopViolationsData(MAX_VIOLATIONS_VEHICLES,m_alertStatus.getData(getViolationsDataOptions())));
		} else { 
			result.put("vehiclestatus",vehiclestatusdata);
			result.put("alertstatus",alertstatusdata);
		}
		result.put("user", getUserdata());


		return result;
	}

	/**
	 * returns a {@link IDataset} containing top MAX_VACANT_VEHICLES vacant
	 * vehicles.
	 * 
	 * @return
	 */

	private IDataset getViolationsDataOptions() {
		IDataset options = new Dataset();
		options.put("localTime", localTime);
		options.put("localTimeZone", localTimeZone);
		options.put("from", "dashboard");
		return options;
	}

	public IDataset getUserdata(){
		User user=SessionUtils.getCurrentlyLoggedInUser();
		IDataset userdata = new Dataset();
		Logs lastlog=null;
		List<Logs> resultset= ((LogsDaoImp)(DBManager.getInstance().getDao(DAOEnum.LOGS_DAO)))
				.selectLastLogin(user.getId());
		if(resultset.size()==2){
			lastlog=resultset.get(1);
		}
		else{
			lastlog=resultset.get(0);
		}
		userdata.put("username", user.getLogin());
		userdata.put("role", user.getRole());
		String actualTimeStr = DateUtils.adjustToClientTime(localTimeZone, lastlog.getDat());
		userdata.put("lastlogin", actualTimeStr);
		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_FRS_EMRI_CLIENT")) ){
			if(user.getLogin().equalsIgnoreCase(EnvironmentInfo.getProperty("RAJASTHAN_USER"))){
				userdata.put("isVehicleCountEnabled", "true");
			}else{
				userdata.put("isVehicleCountEnabled", "false");
			}
		}else {
			userdata.put("isVehicleCountEnabled", "true");
		}
		Helper.printDataSet(userdata);
		return userdata;
	}

	/**
	 * Similar to {@link #getTopData(int, IDataset)} but checks for nested list
	 * inside the entries.
	 * 
	 * @param max
	 * @param data
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private IDataset getTopViolationsData(int max, IDataset data) {
		IDataset result = new Dataset();
		for (Entry<String, Object> entry : data.entrySet()) {
			String vehicleID = entry.getKey();
			List<IDataset> value = (List<IDataset>) entry.getValue();if (value.size() > 0) {
				result.put(vehicleID, value.get(0));
			}
			if (result.size() == max) {
				break;
			}
		}
		return result;
	}

	public ILiveVehicleStatusManager getM_liveVehicleStatus() {
		return m_liveVehicleStatus;
	}

	public void setM_liveVehicleStatus(ILiveVehicleStatusManager mLiveVehicleStatus) {
		m_liveVehicleStatus = mLiveVehicleStatus;
	}

	/**
	 * Returns the {@link IDataset} for Vehicle Health
	 * 
	 * @return
	 */
	private IDataset getVehicleHealthData() {
		IDataset options = getBasicDataOptions();
		options.put("filter.assigned", "true");
		options.put("skip.gps", false);
		options.put("skip.gsm", false);
		options.put("skip.battery", false);
		options.put("skip.fuel", false);
		IDataset vehicleData = m_vehicleManager.getVehicleHealthDataForDahsboard();
		IDataset result = new Dataset();
		result.put("vehicles", getTopData(MAX_HEALTH_VEHICLES, vehicleData));
		result.put("count", vehicleData.size());
		return result;
	}


	/**
	 * 
	 * @return
	 */
	private IDataset getStatusData() {
		IDataset options = getBasicDataOptions();
		options.put("filter.assigned", "true");
		options.put("skip.status", false);
		IDataset vehicleData = m_vehicleManager.getStatusDataForDashboard();
		IDataset result = new Dataset();
		for (Entry<String, Object> entry : vehicleData.entrySet()) {
			IDataset vehicle = (IDataset) entry.getValue();
			String status = vehicle.getValue("status");
			Object statusCount = result.get(status);
			if (null == statusCount) {
				result.put(status, 1);
			} else {
				result.put(status, ((Integer) statusCount).intValue() + 1);
			}
		}
		return result;
	}

	/**
	 * returns data options for retriveing basic data which includes vehicle id,
	 * vehicle name, vehicle status from vehicle manager.
	 * 
	 * @return
	 */
	private IDataset getBasicDataOptions() {
		IDataset options = new Dataset();
		options.put("skip.make", true);
		options.put("skip.model", true);
		options.put("skip.driverid", true);
		options.put("skip.drivername", true);
		options.put("skip.driverstatus", true);
		options.put("skip.driverfirstname", true);
		options.put("skip.driverlastname", true);
		options.put("skip.driverlicense", true);
		options.put("skip.driverassigned", true);
		options.put("skip.drivergroupid", true);
		options.put("skip.drivergroupname", true);
		options.put("skip.status", true);
		options.put("skip.icon", true);
		options.put("skip.lat", true);
		options.put("skip.lon", true);
		options.put("skip.gps", true);
		options.put("skip.gsm", true);
		options.put("skip.battery", true);
		options.put("skip.assigned", true);
		options.put("skip.lastupdated", true);
		options.put("skip.trip", true);
		options.put("skip.groupid", true);
		options.put("skip.groupname", true);
		options.put("skip.location", true);
		options.put("skip.speed", true);
		options.put("skip.chargerdc", true);
		options.put("skip.startlocation", true);
		options.put("skip.startfuel", true);
		options.put("skip.distance", true);
		options.put("skip.maxspeed", true);
		options.put("skip.fuel", true);
		return options;
	}

	/**
	 * Returns the top n records of the given dataset.
	 * 
	 * @param max
	 *            - n
	 * @param data
	 *            - data
	 * @return
	 */
	private IDataset getTopData(int max, IDataset data) {
		IDataset result = new Dataset();
		int count = 0;
		for (Entry<String, Object> entry : data.entrySet()) {
			result.put(entry.getKey(), entry.getValue());
			count++;
			if (count == max) {
				break;
			}
		}
		return result;
	}

	/**
	 * @see IDataProvider#getName()
	 */
	public String getName() {
		return "view";
	}

	/**
	 * Returns the Vehicle Manager
	 * 
	 * @return
	 */
	public IVehicleManager getVehicleManager() {
		return m_vehicleManager;
	}

	/**
	 * Sets the Vehicle Manager
	 * 
	 * @param vehicleManager
	 */
	public void setVehicleManager(IVehicleManager vehicleManager) {
		this.m_vehicleManager = vehicleManager;
	}
}