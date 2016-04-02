package com.harman.db.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.postgis.Geometry;
import org.postgis.Point;

import com.harman.db.entity.primarykey.LongPrimaryKey;
import com.i10n.fleet.container.BulkUpdateDataBean;
import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.fleet.util.DateUtils;

/**
 * Entity class for holding the latest know values of the vehicle and the tracking device
 * @author DVasudeva
 *
 */
public class LiveVehicleStatus implements Serializable{

	private static final long serialVersionUID = 3L;


	// Logger instance
	private static Logger LOG = Logger.getLogger(LiveVehicleStatus.class);


	// Unique ID associated with each device mapping to the vehicle
	private LongPrimaryKey tripId;
	// Location of the vehicle 
	private Geometry location;
	// GSM and GPS strength of the device
	private float gsmStrength;
	private float gpsStrength;
	// Device battery voltage
	private float batteryVoltage;
	// Distance between the previous known point and the current location
	protected float distance;
	// Device connection status with the vehicle
	private boolean chargerConnected;
	// Total packets sent
	private long sqd;
	// Successful data packet sent
	private long sqg;
	// Master restart count of the device
	private int mrs;
	// Direction of travel of the device
	private float course;
	// Idle status of the vehicle
	private boolean isIdle;
	// Offroad status of the vehicle
	private boolean isOffroad;
	// Current speed value of the device 
	private float maxSpeed;
	// Latest know fuel ad value
	private int fuelAd;
	private int cdcCounter;
	// Total distance from the spawn of the device
	private float cumulativeDistance;
	// Mail alert/ report update status to the client
	private boolean mailSent;
	// Previous packet's total packet sent count
	private long prevSqd;
	// Cell ID of the location where packet was generated
	private int cellId;
	// Location area code of the location where packet was generated
	private int locationAreaCode;
	// String representation of the vehicle's location 
	private String locationString;
	// Latest know time at which the packet was generated
	private Date lastUpdatedAt;
	/**
	 * Time at which the server received the data from the device 
	 */
	private Date moduleUpdateTime;
	// Restart count of the device 
	private int rs;

	/** Derived fields **/

	/** For quicker access for updates in the table **/
	private long trackHistoryRowID;
	// Increment this for each ping packet from the server
	private int pingCount;
	// Unique ID of the vehicle fitted with the tracking device
	private long vehicleId;
	// Unique ID of the driver associated to the vehicle  fitted with the tracking device
	private long driverId;
	// Start time of the idle status of the vehicle
	private Date idleStartTime;
	// End time of the idle status of the vehicle
	private Date idleEndTime;

	private int gps_fix_information;

	private double speedLimit;

	private boolean isScheduleTrip;
	// Vehicle subscribed to server from  
	private Date tripStartDate;
	// Vehicle subscribed to server till 
	private Date tripEndDate;
	// Digital element 
	private boolean digital1;
	private boolean digital2;
	// Is the idle row created? 
	public boolean idleRowActive;

	public int onLineCount;
	public int totalOffLineCount;
	public int offroadCount;

	private double firmwareVersion;

	private String imei;
	
	// GVK 102 Related
	private int latestButtonPressed;
	
	private String buttonSequence;

	/** 
	 * This is to maintain the latest Id of the Idlepoint DB insertion for the vehicle
	 */
	public long idleRowId;
	
	public enum VehicleStatus{
		ONLINE(1),
		IDLE(2),
		OFFLINE(3),
		OFFLINE_LOW_GSM(4),
		OFFLINE_LOW_GPS(5),
		OFFLINE_CHARGER_DISCONNECTED(6),
		OFFROAD(7),
		MOVING(8),
		INVALID(999);
		
		private int value;
	    private static final Map<Integer, VehicleStatus> lookup = new HashMap<Integer, VehicleStatus>();

	    private VehicleStatus(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }

	    public static VehicleStatus get(int value) {
	    	if(lookup.get(value) == null ){
	    		return INVALID;
	    	}else{
	    		return lookup.get(value);
	    	}
	    }
	    
	    static {
	        for (VehicleStatus s : EnumSet.allOf(VehicleStatus.class)){
	            lookup.put(s.getValue(), s);
	        }
	    }
	}

	public LiveVehicleStatus(Long tripId, Geometry location,
			float gsmStrength, float gpsStrength, float batteryVoltage,
			float distance, boolean chargerConnected, long sqd, long sqg,
			int mrs, float course, boolean isIdle, float maxSpeed, int fuelAd,
			int cdcCounter, float cd, boolean mailSent, long prevSqd, int cellId, int locationAreaCode,
			String locString, Date lastUpdatedAt, int rs, Date moduleUpdateTime, boolean isOffroad) {
		super();
		setTripId(new LongPrimaryKey(tripId));
		setLocation(location);
		setGsmStrength(gsmStrength);
		setGpsStrength(gpsStrength);
		setBatteryVoltage(batteryVoltage);
		setDistance(distance);
		setChargerConnected(chargerConnected);
		setSqd(sqd);
		setSqg(sqg);
		setMrs(mrs);
		setCourse(course);
		setIdle(isIdle);
		setMaxSpeed(maxSpeed);
		setFuelAd(fuelAd);
		setCdcCounter(cdcCounter);
		setCumulativeDistance(cd);
		setMailSent(mailSent);
		setCellId(cellId);
		setLocationAreaCode(locationAreaCode);
		setLocationString(locString);
		setLastUpdatedAt(lastUpdatedAt);
		setRs(rs);
		setModuleUpdateTime(moduleUpdateTime);
		setOffroad(isOffroad);
	}

	public LiveVehicleStatus() {
		super();		
		idleEndTime = null;
		idleStartTime = null;
		idleRowActive = false;
		trackHistoryRowID =0;
	}

	public LiveVehicleStatus(int onlineVehiclesCount, int offlineVehicleCount, int offroadCount) {
		setOnLineCount(onlineVehiclesCount);
		setTotalOffLineCount(offlineVehicleCount);
		setOffroadCount(offroadCount);
	}

	public void update (GWTrackModuleDataBean dataPacket, BulkUpdateDataBean bulkData, boolean isPing, boolean isOldPacket) {
		if(isOldPacket){
			// Updating the live vehicle status only on receipt of new packet
			setModuleUpdateTime(dataPacket.getModuleUpdateTime());
			return ;
		}
		setBatteryVoltage((float)dataPacket.getModuleBatteryVoltage());
		setCumulativeDistance(dataPacket.getCumulativeDistance());
		setGpsStrength((float)dataPacket.getGpsSignalStrength());
		setGsmStrength((float)dataPacket.getGsmSignalStrength());
		setLastUpdatedAt(bulkData.getOccurredAt());
		setModuleUpdateTime(dataPacket.getModuleUpdateTime());
		setPrevSqd(getSqd());
		setMaxSpeed((float)dataPacket.getMaxSpeed());
		setSqd(dataPacket.getNumberOfPacketSendingAttempts());
		setSqg(dataPacket.getNumberOfSuccessPackets());
		setChargerConnected(dataPacket.isChargerConnected());
		setLocation((Geometry) (new Point ( bulkData.getLongitude(),  bulkData.getLatitude() )));
		if (!isPing) {
			setPingCount(0);
		}
		else {
			setPingCount(getPingCount()+1);
		}
		LOG.debug("Before Setting PingCount is "+pingCount);
		if(pingCount > 1){
			LOG.debug("Its a continuing ping packet Hence setting distance to last distance received from the data packet "+distance);
			bulkData.setDeltaDistance(distance);
		}else{
			distance = (float)bulkData.getDeltaDistance();
			LOG.debug("Its a data packet/first ping packet hence using the distance from the module after clensing the packet. Distance = "+distance);
		}
		setCourse((float)dataPacket.getVehicleCourse());
	}


	public int getRs() {
		return rs;
	}


	public void setRs(int rs) {
		this.rs = rs;
	}


	public float getMaxSpeed() {
		return maxSpeed;
	}


	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}


	public int getFuelAd() {
		return fuelAd;
	}


	public void setFuelAd(int fuelAd) {
		this.fuelAd = fuelAd;
	}


	public int getCdcCounter() {
		return cdcCounter;
	}


	public void setCdcCounter(int cdcCounter) {
		this.cdcCounter = cdcCounter;
	}


	public float getCumulativeDistance() {
		return cumulativeDistance;
	}


	public void setCumulativeDistance(float cumulativeDistance) {
		this.cumulativeDistance = cumulativeDistance;
	}


	public boolean isMailSent() {
		return mailSent;
	}


	public void setMailSent(boolean mailSent) {
		this.mailSent = mailSent;
	}


	public long getPrevSqd() {
		return prevSqd;
	}


	public void setPrevSqd(long prevSqd) {
		this.prevSqd = prevSqd;
	}


	public int getCellId() {
		return cellId;
	}


	public void setCellId(int cellId) {
		this.cellId = cellId;
	}


	public int getLocationAreaCode() {
		return locationAreaCode;
	}


	public void setLocationAreaCode(int locationAreaCode) {
		this.locationAreaCode = locationAreaCode;
	}


	public String getLocationString() {
		return locationString;
	}


	public void setLocationString(String locationString) {
		this.locationString = locationString;
	}


	public Date getLastUpdatedAt() {
		return lastUpdatedAt;
	}


	public void setLastUpdatedAt(Date lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}

	public Date getModuleUpdateTime() {
		return moduleUpdateTime;
	}

	public void setModuleUpdateTime(Date moduleUpdateTime) {
		this.moduleUpdateTime = moduleUpdateTime;
	}

	public LongPrimaryKey getTripId() {
		return tripId;
	}

	public Long getTripIdLong() {
		return tripId.getId();
	}


	public void setTripId(LongPrimaryKey tripId) {
		this.tripId = tripId;
	}

	public void setTripId(Long tripId) {
		this.tripId = new LongPrimaryKey(tripId);
	}



	public Geometry getLocation() {
		return location;
	}


	public void setLocation(Geometry location) {
		this.location = location;
	}


	public float getGsmStrength() {
		return gsmStrength;
	}


	public void setGsmStrength(float gsmStrength) {
		this.gsmStrength = gsmStrength;
	}


	public float getGpsStrength() {
		return gpsStrength;
	}


	public void setGpsStrength(float gpsStrength) {
		this.gpsStrength = gpsStrength;
	}


	public float getBatteryVoltage() {
		return batteryVoltage;
	}


	public void setBatteryVoltage(float batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}


	public float getDistance() {
		return distance;
	}


	public void setDistance(float distance) {
		this.distance = distance;
	}


	public boolean isChargerConnected() {
		return chargerConnected;
	}


	public void setChargerConnected(boolean chargerConnected) {
		this.chargerConnected = chargerConnected;
	}


	public long getSqd() {
		return sqd;
	}


	public void setSqd(long sqd) {
		this.sqd = sqd;
	}


	public long getSqg() {
		return sqg;
	}


	public void setSqg(long sqg) {
		this.sqg = sqg;
	}


	public int getMrs() {
		return mrs;
	}


	public void setMrs(int mrs) {
		this.mrs = mrs;
	}


	public float getCourse() {
		return course;
	}


	public void setCourse(float course) {
		this.course = course;
	}


	public boolean isIdle() {
		return isIdle;
	}


	public void setIdle(boolean isIdle) {
		this.isIdle = isIdle;
	}


	public boolean isOffroad() {
		return isOffroad;
	}

	public void setOffroad(boolean isOffroad) {
		this.isOffroad = isOffroad;
	}

	public long getTrackHistoryRowID() {
		return trackHistoryRowID;
	}

	public void setTrackHistoryRowID(long trackHistoryRowID) {
		this.trackHistoryRowID = trackHistoryRowID;
	}


	public boolean isIdleRowActive() {
		return idleRowActive;
	}

	public void setIdleRowActive(boolean idleRowActive) {
		this.idleRowActive = idleRowActive;
		// Setting Idle status depending on idle duration
		setIdle(idleRowActive);
	}


	public Date getIdleStartTime() {
		return idleStartTime;
	}

	public void setIdleStartTime(Date idleStartTime) {
		this.idleStartTime = idleStartTime;
	}

	public Date getIdleEndTime() {
		return idleEndTime;
	}

	public void setIdleEndTime(Date idleEndTime) {
		this.idleEndTime = idleEndTime;
	}


	/**
	 * @return the id
	 */
	public long getVehicleId() {
		return vehicleId;
	}

	/**
	 * @param id the id to set
	 */
	public void setVehicleId(long vehicleId) {
		this.vehicleId = vehicleId;
	}


	/**
	 * @return the gps_fix_information
	 */
	public int getGps_fix_information() {
		return gps_fix_information;
	}

	/**
	 * @param gps_fix_information the gps_fix_information to set
	 */
	public void setGps_fix_information(int gps_fix_information) {
		this.gps_fix_information = gps_fix_information;
	}

	/**
	 * @return the speedLimit
	 */
	public double getSpeedLimit() {
		return speedLimit;
	}

	/**
	 * @param speedLimit the speedLimit to set
	 */
	public void setSpeedLimit(double speedLimit) {
		this.speedLimit = speedLimit;
	}

	/**
	 * @return the isScheduleTrip
	 */
	public boolean isScheduleTrip() {
		return isScheduleTrip;
	}

	/**
	 * @param isScheduleTrip the isScheduleTrip to set
	 */
	public void setScheduleTrip(boolean isScheduleTrip) {
		this.isScheduleTrip = isScheduleTrip;
	}

	/**
	 * @return the tripStartDate
	 */
	public Date getTripStartDate() {
		return tripStartDate;
	}

	/**
	 * @param tripStartDate the tripStartDate to set
	 */
	public void setTripStartDate(Date tripStartDate) {
		this.tripStartDate = tripStartDate;
	}

	/**
	 * @return the tripEndDate
	 */
	public Date getTripEndDate() {
		return tripEndDate;
	}

	/**
	 * @param tripEndDate the tripEndDate to set
	 */
	public void setTripEndDate(Date tripEndDate) {
		this.tripEndDate = tripEndDate;
	}

	/**
	 * @return the digital1
	 */
	public boolean isDigital1() {
		return digital1;
	}

	/**
	 * @param digital1 the digital1 to set
	 */
	public void setDigital1(boolean digital1) {
		this.digital1 = digital1;
	}

	/**
	 * @return the digital2
	 */
	public boolean isDigital2() {
		return digital2;
	}

	/**
	 * @param digital2 the digital2 to set
	 */
	public void setDigital2(boolean digital2) {
		this.digital2 = digital2;
	}

	/**
	 * @return the driverId
	 */
	public long getDriverId() {
		return driverId;
	}

	/**
	 * @param driverId the driverId to set
	 */
	public void setDriverId(long driverId) {
		this.driverId = driverId;
	}

	public int getPingCount() {
		return pingCount;
	}

	public void setPingCount(int pingCount) {
		this.pingCount = pingCount;
	}


	/**
	 * returns idle duration in seconds
	 * @return
	 */
	public long getIdleDuration () {
		if (idleStartTime!=null && idleEndTime!=null) {
			long duration = idleEndTime.getTime() - idleStartTime.getTime();
			if (duration<=0) {
				return 0;
			}
			return duration/1000;
		}
		return 0;
	}

	//	Variable for TNCSC
	private boolean tncsc_Is_Prev_Idle;
	private long tncsc_Prev_Idle_Time_Seconds;
	private Geometry tncsc_Prev_Location;

	public boolean isTncsc_Is_Prev_Idle() {
		return tncsc_Is_Prev_Idle;
	}

	public void setTncsc_Is_Prev_Idle(boolean tncsc_Is_Prev_Idle) {
		this.tncsc_Is_Prev_Idle = tncsc_Is_Prev_Idle;
	}

	public long getTncsc_Prev_Idle_Time_Seconds() {
		return tncsc_Prev_Idle_Time_Seconds;
	}

	public void setTncsc_Prev_Idle_Time_Seconds(long tncsc_Prev_Idle_Time_Seconds) {
		this.tncsc_Prev_Idle_Time_Seconds = tncsc_Prev_Idle_Time_Seconds;
	}

	public Geometry getTncsc_Prev_Location() {
		return tncsc_Prev_Location;
	}

	public void setTncsc_Prev_Location(Geometry tncsc_Prev_Location) {
		this.tncsc_Prev_Location = tncsc_Prev_Location;
	}

	public int getOnLineCount() {
		return onLineCount;
	}

	public void setOnLineCount(int onLineCount) {
		this.onLineCount = onLineCount;
	}

	public int getTotalOffLineCount() {
		return totalOffLineCount;
	}

	public void setTotalOffLineCount(int totalOffLineCount) {
		this.totalOffLineCount = totalOffLineCount;
	}

	public int getOffroadCount() {
		return offroadCount;
	}

	public void setOffroadCount(int offroadCount) {
		this.offroadCount = offroadCount;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public double getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(double firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public int getLatestButtonPressed() {
		return latestButtonPressed;
	}

	public void setLatestButtonPressed(int latestButtonPressed) {
		this.latestButtonPressed = latestButtonPressed;
	}

	public String getButtonSequence() {
		return buttonSequence;
	}

	public void setButtonSequence(String buttonSequence) {
		this.buttonSequence = buttonSequence;
	}

	@Override
	public String toString(){
		StringBuffer toString = new StringBuffer();
		toString.append("TripID : ");
		toString.append(getTripIdLong());
		toString.append("Location : [");
		toString.append(getLocation().getFirstPoint().y);
		toString.append(", ");
		toString.append(getLocation().getFirstPoint().x);
		toString.append("], GSM : ");
		toString.append(getGsmStrength());
		toString.append(", GPS : ");
		toString.append(getGpsStrength());
		toString.append(", BatteryVoltage : ");
		toString.append(getBatteryVoltage());
		toString.append(", Distance : ");
		toString.append(getDistance());
		toString.append(", CC : ");
		toString.append(isChargerConnected());
		toString.append(", Direction : ");
		toString.append(getCourse());
		toString.append(", IsPing : ");
		toString.append(isIdle());
		toString.append(", MaxSpeed : ");
		toString.append(getMaxSpeed());
		toString.append(", LastUpdatedAt : ");
		toString.append(DateUtils.convertJavaDateToSQLDate(getLastUpdatedAt()));
		toString.append(", ModuleUpdateTime : ");
		toString.append(DateUtils.convertJavaDateToSQLDate(getModuleUpdateTime()));
		toString.append(", TrackHistoryRowID : ");
		toString.append(getTrackHistoryRowID());
		toString.append(", PingCount : ");
		toString.append(getPingCount());
		toString.append(", FirmWareVersion : ");
		toString.append(getFirmwareVersion());
		toString.append(", IMEI : ");
		toString.append(getImei());
		return toString.toString();
	}

	/**
	 * @return the idleRowId
	 */
	public long getIdleRowId() {
		return idleRowId;
	}

	/**
	 * @param idleRowId the idleRowId to set
	 */
	public void setIdleRowId(long idleRowId) {
		this.idleRowId = idleRowId;
	}
}