package com.i10n.fleet.update;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.i10n.db.entity.Address;
import com.i10n.db.entity.Driver;
import com.i10n.db.entity.User;
import com.i10n.db.entity.Vehicle;
import com.i10n.fleet.hashmaps.EMRIHashMaps.ButtonCode;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.util.GeoUtils;
import com.i10n.fleet.util.StringUtils;

public class PositionUpdate implements IStreamUpdate {

	private List<User> assignedUsers;

	private long vehicleID;

	private double latitude;

	private double longitude;

	private double course;

	private double speed;

	private Vehicle vehicle;

	private Driver driver;

	private String status;

	private Date lastUpdatedAt;

	private int d5;

	private long trackhistoryid; 

	private long panicId;

	private final String TYPE = "position";

	private static final Logger LOG = Logger.getLogger(PositionUpdate.class);

	private String groupValue;

	private String distirct;

	private String baselocation;

	private Long crewno;

	private String imei;
	
	private float gps;
	
	private float gsm;
	
	private boolean chargerConnected;
	
	private float batteryVoltage;
	
	private int latestButtonPresssed;

	public PositionUpdate(List<User> assignedUsers, long vehicleId, double lat, double lg, double course,
			double speed, Vehicle vehicle, Driver driver, Date lastUpdatedAt, int d5, 
			String status,Long groupValue,String distirct, String baselocation, Long crewno, String imei,
			float gps, float gsm, boolean chargerConnected, float batteryVoltage, int latestButtonPresssed) {
		this.assignedUsers = assignedUsers;
		this.vehicleID = vehicleId;
		latitude = lat;
		longitude = lg;
		this.course = course;
		this.vehicle = vehicle;
		this.driver = driver;
		this.speed = speed;
		this.lastUpdatedAt = lastUpdatedAt;
		this.d5 = d5;
		this.status = status;
		this.groupValue="group-"+groupValue;
		this.distirct = distirct;
		this.baselocation = baselocation;
		this.crewno = crewno;
		this.imei = imei;
		this.gps = gps;
		this.gsm = gsm;
		this.chargerConnected = chargerConnected;
		this.batteryVoltage = batteryVoltage;
		this.latestButtonPresssed = latestButtonPresssed;
	}

	public int getD5() {
		return d5;
	}

	public void setD5(int d5) {
		this.d5 = d5;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
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

	public Date getLastUpdatedAt() {
		return lastUpdatedAt;
	}

	public void setLastUpdatedAt(Date lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}

	public void setVehicleID(long vehicleID) {
		this.vehicleID = vehicleID;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setCourse(double course) {
		this.course = course;
	}

	public long getVehicleID() {
		return vehicleID;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getCourse(){
		return course;
	}

	public void setTrackHistoryID(long trackhistoryid) {
		this.trackhistoryid = trackhistoryid;
	}

	public long getTrackHistoryID() {
		return trackhistoryid;
	}

	public long getPanicId() {
		return panicId;
	}

	public void setPanicId(long panicId) {
		this.panicId = panicId;
	}

	@Override
	public long getId() {
		return getVehicleID();
	}

	@Override
	public String getType() {
		return TYPE;
	}

	public List<User> getAssignedUsers() {
		return assignedUsers;
	}

	public void setAssignedUsers(List<User> assignedUsers) {
		this.assignedUsers = assignedUsers;
	}

	public long getTrackhistoryid() {
		return trackhistoryid;
	}

	public void setTrackhistoryid(long trackhistoryid) {
		this.trackhistoryid = trackhistoryid;
	}

	public float getGps() {
		return gps;
	}

	public void setGps(float gps) {
		this.gps = gps;
	}

	public float getGsm() {
		return gsm;
	}

	public void setGsm(float gsm) {
		this.gsm = gsm;
	}

	public boolean isChargerConnected() {
		return chargerConnected;
	}

	public void setChargerConnected(boolean chargerConnected) {
		this.chargerConnected = chargerConnected;
	}

	public int getLatestButtonPresssed() {
		return latestButtonPresssed;
	}

	public void setLatestButtonPresssed(int latestButtonPresssed) {
		this.latestButtonPresssed = latestButtonPresssed;
	}

	public void setCrewno(Long crewno) {
		this.crewno = crewno;
	}

	public float getBatteryVoltage() {
		return batteryVoltage;
	}

	public void setBatteryVoltage(float batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}

	@Override
	public String toJSONString() {
		Address address = null;
		String location = null ;
		if (Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_POSITION_UPDATE_ENABLED"))) {
			address = GeoUtils.fetchNearestLocationFromDB(this.getLatitude(), this.getLongitude());
			if(address != null){
				location = address.toString();
			}
		}

		StringBuffer content = new StringBuffer();
		content.append("{\"vehicleId\":");
		content.append(this.getVehicleID());
		content.append(",\"lat\":");
		content.append(this.getLatitude()); 
		content.append(",\"lon\":");
		content.append(this.getLongitude()); 
		content.append(",\"location\":\"");
		content.append(location);
		content.append("\"");
		content.append(",\"course\":");
		content.append(this.getCourse()); 
		content.append(",\"drivername\":\"");
		content.append(this.getDriver().getFirstName());
		content.append("\""); 
		content.append(",\"mobilenumber\":\"");
		content.append(this.getDriver().getLastName());
		content.append("\"");
		content.append(",\"vehiclemodel\":\"");
		content.append(this.getVehicle().getModel());
		content.append("\""); 
		content.append(",\"vehicleDisplayname\":\"");
		content.append(this.getVehicle().getDisplayName());
		content.append("\""); 
		content.append(",\"make\":\"");
		content.append(this.getVehicle().getMake());
		content.append("\"");
		content.append(",\"lastupdated\":\"");
		content.append(this.getLastUpdatedAt());
		content.append("\"");
		content.append(",\"speed\":");
		content.append(this.getSpeed());
		content.append(",\"status\":\"");
		content.append(this.getStatus());
		content.append("\"");
		content.append(",\"groupid\":\"");
		content.append(this.getGroupValue());
		content.append("\"");
		content.append(",\"district\":\"");
		content.append(this.getDistirct());
		content.append("\"");
		content.append(",\"baselocation\":\"");
		content.append(this.getBaselocation());
		content.append("\"");
		content.append(",\"imeino\":\"");
		content.append(this.getImei());
		content.append("\"");
		content.append(",\"crewno\":\"");
		content.append(this.getCrewno());
		content.append("\"");
		content.append(",\"gps\":\"");
		content.append(StringUtils.getGPSSS(this.getGps()));
		content.append("\"");
		content.append(",\"gsm\":\"");
		content.append(StringUtils.getGSMSS(this.getGsm()));
		content.append("\"");
		content.append(",\"battery\":\"");
		content.append(StringUtils.getBatteryStrength(this.getBatteryVoltage()));
		content.append("\"");
		content.append(",\"latestbuttonpressed\":\"");
		content.append(ButtonCode.get(this.getLatestButtonPresssed()));
		content.append("\"");
		content.append(",\"cc\":\"");
		content.append(this.isChargerConnected() ? "Yes" : "No");
		content.append("\"");
		content.append(",\"icon\":");
		content.append(this.getVehicle().getVehicleIconPicId());
		content.append(",\"d5\":");
		content.append(this.getD5());
		content.append("}");
		return content.toString();
	}

	@Override
	public String[] getLogin() {
		String[] returnString = null;
		try {
			returnString = new String[assignedUsers.size()];
			int i=0; 
			for(User user : assignedUsers){
				returnString[i++] = user.getLogin();
			}
		} catch (Exception e ){
			LOG.error("",e);
		}
		return returnString;
	}

	public void setGroupValue(String groupValue) {
		this.groupValue = groupValue;
	}

	public String getGroupValue() {
		return groupValue;
	}
	public String getDistirct() {
		return distirct;
	}

	public void setDistirct(String distirct) {
		this.distirct = distirct;
	}

	public String getBaselocation() {
		return baselocation;
	}

	public void setBaselocation(String baselocation) {
		this.baselocation = baselocation;
	}

	public long getCrewno() {
		return crewno;
	}

	public void setCrewno(long crewno) {
		this.crewno = crewno;
	}



	public String getImei() {
		return imei;
	}



	public void setImei(String imei) {
		this.imei = imei;
	}




}
