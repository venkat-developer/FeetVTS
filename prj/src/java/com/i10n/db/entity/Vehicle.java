package com.i10n.db.entity;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.postgis.Geometry;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;
import com.i10n.dbCacheManager.LoadTripDetails;
import com.i10n.fleet.util.EnvironmentInfo;

public class Vehicle implements IEntity<Vehicle> {

	private static Logger LOG = Logger.getLogger(Vehicle.class);

	private LongPrimaryKey id;
	private String displayName;
	private String make;
	private String model;
	private String modelYear;
	private Long imeiId;
	private String imei;
	private Date odometerUpdatedAt;
	private int odometerValue;
	private long fuelcaliberationid;
	private int vehicleIconPicId;
	private String optional1;
	private String optional2;
	private String optional3;
	private long groupId;
	private String groupName;

	private boolean deleted = false;
	private String type;

	/**
	 * These attributes doesnt include to this entity and hence have to moved
	 * from this entity
	 */

	private long driverId;
	private String driverFirstName;
	private String driverLastName;
	private long driverGroupId;
	private String vehicleGroupValue;
	private String driverGroupValue;
	private long tripId;
	private long maxSpeed;
	private Geometry vehicleLocation;
	private float gpsStrength;
	private float gsmStrength;
	private float batteryVoltage;
	private int fuelAd;
	private Date lastUpdatedAt;

	// Vehicle Icon ids from DB for particular circumstance
	// : Create the icons for the following circumstances
	public static final int DEFAULT_ICON = 21; // 15;
	public static final int ICON_WITH_LOAD = 21;
	public static final int ICON_WITH_OUT_LOAD = 20;
	public static final int TRIP_GODOWN_TO_CLIENT = 21; // 20;
	public static final int TRIP_CLIENT_TO_GODOWN = 20; // 15;
	// Set to default icon as of now  : Create new icon after the business
	// logic implementation
	public static final int TRIP_END = 22;

	public Vehicle() {
		this.id = null;
		this.displayName = "";
		this.make = "";
		this.model = "";
		this.modelYear = "";
		this.imeiId = new Long(0);
		this.odometerUpdatedAt = null;
		this.odometerValue = 0;
		this.fuelcaliberationid = 0;
		this.vehicleIconPicId = 0;
		this.optional1 = "";
		this.optional2 = "";
		this.optional3 = "";
		this.groupId = 0;
		this.deleted = false;
	}

	public Vehicle(String displayName, String make, String model,
			String modelYear, Long imeiId, String imei, int vehicleIconPicId) {
		this.displayName = displayName;
		this.make = make;
		this.model = model;
		this.modelYear = modelYear;
		this.imeiId = imeiId;
		this.vehicleIconPicId = vehicleIconPicId;
	}

	public Vehicle(LongPrimaryKey vehicleId, String displayName, String make,
			String model, String modelYear, Long imeiId, int vehicleIconPicId) {
		this.id = vehicleId;
		this.displayName = displayName;
		this.make = make;
		this.model = model;
		this.modelYear = modelYear;
		this.imeiId = imeiId;
		this.vehicleIconPicId = vehicleIconPicId;
	}

	public Vehicle(LongPrimaryKey id, String displayName, String make,
			String model, String modelYear, Long imeiId,
			Date odometerUpdatedAt, int odometerValue, long fuelcaliberationid,
			int vehicleIconPicId, long groupId, boolean deleted, String type) {
		super();
		this.id = id;
		this.displayName = displayName;
		this.make = make;
		this.model = model;
		this.modelYear = modelYear;
		this.imeiId = imeiId;
		this.odometerUpdatedAt = odometerUpdatedAt;
		this.odometerValue = odometerValue;
		this.fuelcaliberationid = fuelcaliberationid;
		this.vehicleIconPicId = vehicleIconPicId;
		this.groupId = groupId;
		this.deleted = deleted;
		this.type = type;
	}

	public Vehicle(LongPrimaryKey id, String displayName, String make,
			String model, String modelYear, Long imeiId,
			Date odometerUpdatedAt, int odometerValue, long fuelcaliberationid,
			int vehicleIconPicId, String optional1, String optional2,
			String optional3, long groupId, boolean deleted, String type,
			Long maxSpeed, Geometry vehicleLocation, long driverId,
			String driverFirstName, String driverLastName, long driverGroupId,
			String vehicleGroupValue, String driverGroupValue,
			float gpsStrength, float gsmStrength, float batteryVoltage,
			int fuelAd, long tripId, Date lastUpdatedAt) {
		super();
		this.id = id;
		this.displayName = displayName;
		this.make = make;
		this.model = model;
		this.modelYear = modelYear;
		this.imeiId = imeiId;
		this.odometerUpdatedAt = odometerUpdatedAt;
		this.odometerValue = odometerValue;
		this.fuelcaliberationid = fuelcaliberationid;
		this.vehicleIconPicId = vehicleIconPicId;
		this.optional1 = optional1;
		this.optional2 = optional2;
		this.optional3 = optional3;
		this.groupId = groupId;
		this.deleted = deleted;
		this.type = type;
		this.driverId = driverId;
		this.driverFirstName = driverFirstName;
		this.driverLastName = driverLastName;
		this.driverGroupId = driverGroupId;
		this.vehicleGroupValue = vehicleGroupValue;
		this.driverGroupValue = driverGroupValue;
		this.tripId = tripId;
		this.maxSpeed = maxSpeed;
		this.vehicleLocation = vehicleLocation;
		this.gpsStrength = gpsStrength;
		this.gsmStrength = gsmStrength;
		this.batteryVoltage = batteryVoltage;
		this.fuelAd = fuelAd;
		this.lastUpdatedAt = lastUpdatedAt;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(getId().getId()).append("-").append(getMake())
		.append("-").append(getModel()).append("-");
		strBuilder.append(getModelYear()).append("-").append(getImeiId())
		.append("-").append("-").append(getOdometerValue()).append("-")
		.append(getVehicleIconPicId());
		strBuilder.append(getDisplayName());
		strBuilder.append("-").append(groupId);
		return strBuilder.toString();
	}

	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getModelYear() {
		return modelYear;
	}

	public void setModelYear(String modelYear) {
		this.modelYear = modelYear;
	}

	public Long getImeiId() {
		return imeiId;
	}

	public void setImeiId(Long imeiId) {
		this.imeiId = imeiId;
	}

	public Date getOdometerUpdatedAt() {
		return odometerUpdatedAt;
	}

	public void setOdometerUpdatedAt(Date odometerUpdatedAt) {
		this.odometerUpdatedAt = odometerUpdatedAt;
	}

	public int getOdometerValue() {
		return odometerValue;
	}

	public void setOdometerValue(int odometerValue) {
		this.odometerValue = odometerValue;
	}

	public long getFuelcaliberationid() {
		return fuelcaliberationid;
	}

	public void setFuelcaliberationid(long fuelcaliberationid) {
		this.fuelcaliberationid = fuelcaliberationid;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getVehicleIconPicId() {
		return vehicleIconPicId;
	}

	public void setVehicleIconPicId(int vehicleIconPicId) {
		this.vehicleIconPicId = vehicleIconPicId;
	}

	public String getOptional1() {
		return optional1;
	}

	public void setOptional1(String optional1) {
		this.optional1 = optional1;
	}

	public String getOptional2() {
		return optional2;
	}

	public void setOptional2(String optional2) {
		this.optional2 = optional2;
	}

	public String getOptional3() {
		return optional3;
	}

	public void setOptional3(String optional3) {
		this.optional3 = optional3;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public long getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(long maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public Geometry getVehicleLocation() {
		return vehicleLocation;
	}

	public void setVehicleLocation(Geometry vehicleLocation) {
		this.vehicleLocation = vehicleLocation;
	}

	public long getDriverId() {
		return driverId;
	}

	public void setDriverId(long driverId) {
		this.driverId = driverId;
	}

	public String getDriverFirstName() {
		return driverFirstName;
	}

	public void setDriverFirstName(String driverFirstName) {
		this.driverFirstName = driverFirstName;
	}

	public String getDriverLastName() {
		return driverLastName;
	}

	public void setDriverLastName(String driverLastName) {
		this.driverLastName = driverLastName;
	}

	public long getDriverGroupId() {
		return driverGroupId;
	}

	public void setDriverGroupId(long driverGroupId) {
		this.driverGroupId = driverGroupId;
	}

	public String getVehicleGroupValue() {
		return vehicleGroupValue;
	}

	public void setVehicleGroupValue(String vehicleGroupValue) {
		this.vehicleGroupValue = vehicleGroupValue;
	}

	public String getDriverGroupValue() {
		return driverGroupValue;
	}

	public void setDriverGroupValue(String driverGroupValue) {
		this.driverGroupValue = driverGroupValue;
	}

	public float getGpsStrength() {
		return gpsStrength;
	}

	public void setGpsStrength(float gpsStrength) {
		this.gpsStrength = gpsStrength;
	}

	public float getGsmStrength() {
		return gsmStrength;
	}

	public void setGsmStrength(float gsmStrength) {
		this.gsmStrength = gsmStrength;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getImei() {
		return imei;
	}

	public float getBatteryVoltage() {
		return batteryVoltage;
	}

	public void setBatteryVoltage(float batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}

	public int getFuelAd() {
		return fuelAd;
	}

	public void setFuelAd(int fuelAd) {
		this.fuelAd = fuelAd;
	}

	public long getTripId() {
		return tripId;
	}

	public void setTripId(long tripId) {
		this.tripId = tripId;
	}

	public Date getLastUpdatedAt() {
		return lastUpdatedAt;
	}

	public void setLastUpdatedAt(Date lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus(String imei) {
		String statusOfVehicle = "offroad";
		LiveVehicleStatus liveVehicleObjectStatus = LoadLiveVehicleStatusRecord
				.getInstance().retrieve(imei);
		Trip tripObject = LoadTripDetails.getInstance().retrieve(liveVehicleObjectStatus.getTripId().getId());
		if (liveVehicleObjectStatus == null) {
			LOG.error("LVOS for imei " + imei+ " which is neither in cache nor in db");
			return statusOfVehicle;
		}
		if (liveVehicleObjectStatus.isOffroad()) {
			LOG.debug("Status of " + statusOfVehicle + " of the IMEI " + imei);
			return statusOfVehicle;
		}
		long lastupdatedDay = liveVehicleObjectStatus.getLastUpdatedAt().getTime();

		long moduleUpdateTime = liveVehicleObjectStatus.getModuleUpdateTime().getTime();
		Calendar cal = Calendar.getInstance();
		long currDate = cal.getTimeInMillis();
		LOG.debug("the lastupdated day is :"+lastupdatedDay+"\t the current date is "+currDate+"\t moduleupdate time is "+moduleUpdateTime);
		long lastUpdatediff = currDate - lastupdatedDay;
		long moduleUpdatediff = currDate - moduleUpdateTime;
		//		double lastUpdatedDiffDays = lastUpdatediff / (24 * 60 * 60 * 1000);
		//		double moduleUpdatediffDay = moduleUpdatediff / (24 * 60 * 60 * 1000);
		// to prevent negative values
		//		if (lastUpdatedDiffDays < 1) {
		//			lastUpdatedDiffDays = -(lastUpdatedDiffDays);
		//		}
		//		if (moduleUpdatediffDay < 1) {
		//			moduleUpdatediffDay = -(moduleUpdatediffDay);
		//		}
		if (lastUpdatediff < 0) {
			lastUpdatediff = -(lastUpdatediff);
		}
		if (moduleUpdatediff < 0) {
			moduleUpdatediff = -(moduleUpdatediff);
		}
		long mOfflineThreshold=Integer.valueOf(EnvironmentInfo.getProperty("VEHICLE_OFFLINE_THRESHOLD"));
		LOG.debug("The mOfflineThreshold value for Offline is "+mOfflineThreshold);

		//		if (liveVehicleObjectStatus.isIdle()
		//				&& (lastUpdatedDiffDays == mOfflineThreshold || moduleUpdatediffDay == 0)) {
		//			statusOfVehicle = "idle";
		//	} else if (lastUpdatedDiffDays > 0 && moduleUpdatediffDay > 0) {
		//		statusOfVehicle = "offline";

		int lastUpdatedDiffhours 	= 	(int) 	(lastUpdatediff/(1000*60*60));
		int lastUpdatedDiffMinutes 	= 	(int) 	(lastUpdatediff/(1000*60));
		int moduleUpdatediffHours	=	(int)	(moduleUpdatediff/(1000*60*60));
		int moduleUpdatediffMinutes =	(int)	(moduleUpdatediff/(1000*60));

		LOG.debug("lastUpdatedDiffhours Diff is "+lastUpdatedDiffhours+"\t lastUpdatedDiffMinutes "+lastUpdatedDiffMinutes
				+"\t moduleUpdatediffHours:"+moduleUpdatediffHours+"\t moduleUpdatediffMinutes: "+moduleUpdatediffMinutes);
		LOG.debug("Idle Limit "+tripObject.getIdlePointsTimeLimit()+" , Last updated minutes Diff : "+lastUpdatedDiffMinutes+" , Moduleupdated Minutes Diff"
				+moduleUpdatediffMinutes+" of "+displayName);
		if ((lastUpdatedDiffMinutes > tripObject.getIdlePointsTimeLimit() || moduleUpdatediffMinutes > tripObject.getIdlePointsTimeLimit()) 
				&& (lastUpdatedDiffhours < mOfflineThreshold && moduleUpdatediffHours < mOfflineThreshold)) {
			statusOfVehicle = "idle";
		} else if (lastUpdatedDiffhours >= mOfflineThreshold && moduleUpdatediffHours >= mOfflineThreshold) {
			statusOfVehicle = "offline";
		} else {
			if (Boolean.valueOf(EnvironmentInfo.getProperty("IS_SHSM_CLIENT"))) {
				statusOfVehicle = "moving";
			} else {
				statusOfVehicle = "online";
			}
		}
		LOG.debug("Status of " + statusOfVehicle + " of the IMEI " + imei);
		return statusOfVehicle;
	}

	@Override
	public boolean equalsEntity(Vehicle object) {
		return false;
	}
}
