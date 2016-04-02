package com.i10n.db.entity;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.EmriVehiclesBaseStationDaoImp;
import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadEmriVehiclesBaseStationDetails;
import com.i10n.dbCacheManager.LoadHardwareModuleDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.hashmaps.EMRIHashMaps.ButtonCode;
import com.i10n.fleet.util.Constants;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.util.GeoUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.mina.utils.Utils;

/**
 * Entity class for trip of the vehicle
 * @author Dharmaraju V
 *
 */
public class TripDetails implements IEntity<TripDetails>{

	private static final Logger LOG = Logger.getLogger(TripDetails.class);

	private LongPrimaryKey id;

	private Trip staticTripDetails;

	private LiveVehicleStatus dynamicTripStatus;

	private Vehicle vehicle;

	private Driver driver;

	private String groupName;

	private Long groupValue;

	private String alertstatus;


	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	String localTime = (String)WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_TIME);
	String localTimeZone = (String)WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_TIMEZone);

	public TripDetails(){

	}

	public TripDetails(Long tripId, Trip staticTripDetails, LiveVehicleStatus dynamicTripStatus, Vehicle vehicle, Driver driver){
		this(new LongPrimaryKey(tripId), staticTripDetails, dynamicTripStatus,vehicle, driver);
	}


	public TripDetails(LongPrimaryKey tripId, Trip staticTripDetails, LiveVehicleStatus dynamicTripStatus, Vehicle vehicle, Driver driver){
		this.id = tripId;
		this.staticTripDetails = staticTripDetails;
		this.dynamicTripStatus = dynamicTripStatus;
		this.vehicle = vehicle;
		this.driver = driver;
		groupName = "default";
		groupValue = this.vehicle.getGroupId();
	}

	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public Trip getStaticTripDetails() {
		return staticTripDetails;
	}

	public void setStaticTripDetails(Trip staticTripDetails) {
		this.staticTripDetails = staticTripDetails;
	}

	public LiveVehicleStatus getDynamicTripStatus() {
		return dynamicTripStatus;
	}

	public void setDynamicTripStatus(LiveVehicleStatus dynamicTripStatus) {
		this.dynamicTripStatus = dynamicTripStatus;
	}

	public String getAlertstatus() {
		return alertstatus;
	}

	public void setAlertstatus(String alertstatus) {
		this.alertstatus = alertstatus;
	}

	public IDataset toMap(){
		//LOG.debug("Inside toMap()");
		IDataset tripDetailsMap = new Dataset();
		StringBuffer location=new StringBuffer();

		tripDetailsMap.put("id","vehicle-"+vehicle.getId().getId());
		tripDetailsMap.put("make", vehicle.getMake()+"");
		tripDetailsMap.put("model", vehicle.getModel()+"");
		this.groupValue = vehicle.getGroupId();
		tripDetailsMap.put("groupid", "group-" + this.getGroupValue());

		/* Fetch Group Name from Cache */
		Vehicle cachedVehicle = LoadVehicleDetails.getInstance().retrieve(vehicle.getId().getId());
		if (cachedVehicle!=null) {
			this.groupName = cachedVehicle.getGroupName(); 
			tripDetailsMap.put("groupname", this.groupName+"");
		}
		else{
			tripDetailsMap.put("groupname", "default");
		} 
		double a = dynamicTripStatus.getLocation().getFirstPoint().y;
		double b = dynamicTripStatus.getLocation().getFirstPoint().x;
		String statusofVeh = vehicle.getStatus(vehicle.getImei());
		tripDetailsMap.put("status", statusofVeh+"");
		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_TRIP_DETAILS_ENABLED"))){
			Address locationFetch = GeoUtils.fetchNearestLocationFromCache(a, b);
			location=StringUtils.formulateAddress(locationFetch,vehicle.getId().getId(),a,b);
		}else {
			location.append("Trip ");
			location.append(StringUtils.addressFetchDisabled(vehicle.getId().getId(),a,b).toString());
		}
		//		location.append(a+":"+b);
		tripDetailsMap.put("refid","not Available");
		tripDetailsMap.put("alertstatus","Status Not Available");
		tripDetailsMap.put("violationtype","Alert Not Available");

		tripDetailsMap.put("driverid", driver.getId().getId()+"");
		tripDetailsMap.put("drivername", driver.getFirstName()+"");
		tripDetailsMap.put("driverstatus", dynamicTripStatus.isIdle()?"idle":"online");
		tripDetailsMap.put("driverfirstname", driver.getFirstName()+"");
		tripDetailsMap.put("mobilenumber", driver.getLastName()+"");
		tripDetailsMap.put("driverlicense", "license-1");
		tripDetailsMap.put("drivermaxspeed", dynamicTripStatus.getMaxSpeed()+"");
		tripDetailsMap.put("driveravgspeed", 01);
		tripDetailsMap.put("driverdistance", 53);
		tripDetailsMap.put("driverassigned", true);
		tripDetailsMap.put("drivergroupid", "group-10");
		tripDetailsMap.put("drivergroupname", "South Zone");
		tripDetailsMap.put("course", dynamicTripStatus.getCourse()+"");
		
		if(statusofVeh.equalsIgnoreCase("offline") || statusofVeh.equalsIgnoreCase("offroad")){
			tripDetailsMap.put("speed", 0+"");
			tripDetailsMap.put("maxspeed", 0+"");
		}else{
			tripDetailsMap.put("speed", Utils.doubleForDisplay(dynamicTripStatus.getMaxSpeed())+"");
			tripDetailsMap.put("maxspeed", Utils.doubleForDisplay(dynamicTripStatus.getMaxSpeed())+"");
		}

		tripDetailsMap.put("location", location.toString());
		tripDetailsMap.put("icon", vehicle.getVehicleIconPicId()+"");
		tripDetailsMap.put("lat", dynamicTripStatus.getLocation().getFirstPoint().getY()+"");
		tripDetailsMap.put("lon", dynamicTripStatus.getLocation().getFirstPoint().getX()+"");
		tripDetailsMap.put("cc", dynamicTripStatus.isChargerConnected() ? "Yes" : "No");
		tripDetailsMap.put("startlocation", "Bommanahalli");
		tripDetailsMap.put("seatbelt","Not Available");
		tripDetailsMap.put("ignition","Not Available");
		EmriVehiclesBaseStation cachedEmriRajasthan = null;

		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_FRS_EMRI_CLIENT"))){
			List<EmriVehiclesBaseStation> emriList = ((EmriVehiclesBaseStationDaoImp)DBManager.getInstance().
					getDao(DAOEnum.EMRI_RAJASTHAN_DAO)).selectByVehicleId(vehicle.getId().getId());
			if(emriList != null && emriList.size() > 0){
				Long emriRajasthanId = emriList.get(0).getID().getId();
				cachedEmriRajasthan = LoadEmriVehiclesBaseStationDetails.getInstance().retrieve(emriRajasthanId);
			}
			tripDetailsMap.put("latestbuttonpressed", ButtonCode.get(dynamicTripStatus.getLatestButtonPressed())+"");
			tripDetailsMap.put("name", vehicle.getDisplayName());	
			tripDetailsMap.put("displaydata", vehicle.getDisplayName() + "    [" + driver.getFirstName() +"]");
			tripDetailsMap.put("displaydata", vehicle.getDisplayName() + "    [" + driver.getFirstName() +"]");
		} else {
			tripDetailsMap.put("name", vehicle.getDisplayName() + "\n[" + driver.getFirstName() +"]");
			tripDetailsMap.put("latestbuttonpressed", ButtonCode.get(dynamicTripStatus.getLatestButtonPressed())+"");
		}
		if(cachedEmriRajasthan != null){
			tripDetailsMap.put("district",cachedEmriRajasthan.getDistrict());
			tripDetailsMap.put("baselocation",cachedEmriRajasthan.getBaseLocation());
			tripDetailsMap.put("crewno", cachedEmriRajasthan.getCrewNo());
		}else{
			tripDetailsMap.put("district","Not Available");
			tripDetailsMap.put("baselocation","Not Available");
			tripDetailsMap.put("crewno", 0);
		}
		tripDetailsMap.put("startfuel", 3);
		tripDetailsMap.put("distance", dynamicTripStatus.getDistance()+"");
		HardwareModule imeino = LoadHardwareModuleDetails.getInstance().retrieve(vehicle.getImeiId());
		tripDetailsMap.put("imei", vehicle.getImeiId()+"");
		tripDetailsMap.put("imeino", imeino.getImei()+"");
		tripDetailsMap.put("year", vehicle.getModelYear()+"");
		tripDetailsMap.put("geofenceid", "region-6");
		tripDetailsMap.put("gps", StringUtils.getGPSSS(dynamicTripStatus.getGpsStrength()));
		tripDetailsMap.put("gsm", StringUtils.getGSMSS(dynamicTripStatus.getGsmStrength()));
		tripDetailsMap.put("battery", StringUtils.getBatteryStrength(dynamicTripStatus.getBatteryVoltage()));
		tripDetailsMap.put("fuel", 3);
		tripDetailsMap.put("assigned", true);
		long actualTime = DateUtils.adjustToClientTimeInMilliSeconds(localTimeZone, dynamicTripStatus.getLastUpdatedAt());
		//		Live track Vehicle Status Page Update 
		String actualDateTime = DateUtils.adjustToClientTime(localTimeZone, dynamicTripStatus.getLastUpdatedAt());
		long moduleupdatetime = DateUtils.adjustToClientTimeInMilliSeconds(localTimeZone, dynamicTripStatus.getModuleUpdateTime());
		tripDetailsMap.put("lastupdated", actualDateTime+"");
		tripDetailsMap.put("timeinmilliseconds", actualTime+"");
		tripDetailsMap.put("moduleupdatetime", moduleupdatetime+"");

		tripDetailsMap.put("trip", staticTripDetails.getTripName()+"");
		LOG.debug("Completed processing toMap()");
		return tripDetailsMap;
	}



	public IDataset toAlertMap() {
		IDataset liveAlerts = new Dataset();
		//		AlertOrViolation alertresult = null;
		//		List<AlertOrViolation> alertData = null; 
		//			((AlertDaoImpl) DBManager.getInstance().getDao(DAOEnum.ALERT_DAO)).selectalerts();

		/*if (alertData != null) {
			for (int i = 0; i < alertData.size(); i++) {
				alertresult = alertData.get(i);

				liveAlerts.put("id" + (i + 1) + ".refid", alertresult.getId().getId());
				if(LoadVehicleDetails.getInstance().retrieve(alertresult.getVehicleId()) != null){
					liveAlerts.put("id" + (i + 1) + ".vehiclename", LoadVehicleDetails.getInstance().retrieve(alertresult.getVehicleId()).getDisplayName());
				}else{
					liveAlerts.put("id" + (i + 1) + ".vehiclename", " ");
				}
				liveAlerts.put("id" + (i + 1) + ".driver", LoadDriverDetails.getInstance().retrieve(alertresult.getDriverId()).getFirstName());
				// TODO: Commented for new alert architecture implementation 
//				liveAlerts.put("id" + (i + 1) + ".violationtype", alertresult.getType());
				String actualTime = StringUtils.adjustToClientTime(localTimeZone, alertresult.getAlertTime());
				liveAlerts.put("id" + (i + 1) + ".time", actualTime);
				liveAlerts.put("id" + (i + 1) + ".location", "No Location Available");//alertresult.getLocation());
//				liveAlerts.put("id" + (i + 1) + ".alert", (alertresult.isStatus()?"Closed":"Opened"));
				liveAlerts.put("id" + (i + 1) + ".alert", "");
			}
		} else*/ {
			liveAlerts.put("id1.refid", 1);
			liveAlerts.put("id1.vehiclename", "AlertTesting");
			liveAlerts.put("id1.driver", "AlertTesting");
			liveAlerts.put("id1.violationtype", "AlertTesting");
			liveAlerts.put("id1.time", new Date());
			liveAlerts.put("id1.location", "AlertTesting");
			liveAlerts.put("id1.alert", "Opened");
			liveAlerts.put("id1.alert", 1);

		}
		return liveAlerts;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Long getGroupValue() {
		return groupValue;
	}

	public void setGroupValue(Long groupValue) {
		this.groupValue = groupValue;
	}

	@Override
	public boolean equalsEntity(TripDetails object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString(){
		StringBuffer toString = new StringBuffer();
		toString.append("ID : ");
		toString.append(getId().getId());
		toString.append("Trip : "+getStaticTripDetails().toString());
		toString.append("LiveVehicleStatus : "+getDynamicTripStatus().toString());
		toString.append("Vehicle : "+getVehicle().toString());
		toString.append("Driver : "+getDriver().toString());
		toString.append("GroupName : "+getGroupName());

		return toString.toString();
	}
}