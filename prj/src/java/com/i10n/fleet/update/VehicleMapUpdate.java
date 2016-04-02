package com.i10n.fleet.update;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.i10n.db.entity.Address;
import com.i10n.db.entity.User;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.util.GeoUtils;

/**
 * Provides the live update to the 
 * vehicle map report tab
 * @author Dharmaraju V
 *
 */
public class VehicleMapUpdate implements IStreamUpdate{
	
	private static Logger LOG = Logger.getLogger(VehicleMapUpdate.class);
	
	/**
	 * Set of user name who are having this vehicle in the their
	 * Login
	 */
	private List<User> assignedUsers ;
	/**
	 * Id for the vehicle update
	 */
	long vehicleID;
	String displayName;
	/**
	 * location of the Vehicle 
	 */
	double latitude;
	double longitude;
	
	/**
	 * Current travel speed 
	 */
	double speed;
	
	/**
	 * Direction of travel of the vehicle 
	 */
	float course;
	
	/**
	 * Current location of the vehicle
	 */
	String location;
	
	/**
	 * Gives the GPS strength of the module in the  vehicle
	 * */
	float gpsStrength;
	/**
	 * Gives the GSM strength of the module in the Vehicles
	 * */

	float gsmStrength;
	
	/**
	 * Tells the  update time
	 */
	Date lastUpdatedAt;
	
	/**
	 * This tells the type of the update
	 * to the pushletstreamer
	 */
	private final String TYPE = "vehiclemapreport";
	
	private String groupValue;
	
	private String distirct;

	private String baselocation;

	private Long crewno;
	
	private String imei;
	
	public VehicleMapUpdate(List<User> assignedUsers,long vehicleId,String displayName, double latitude, double longitude, 
			String location, double speed, float gps, float gsm, float course, Date lastupdated,Long groupValue,String distirct, String baselocation, Long crewno, String imei){
		this.assignedUsers=assignedUsers;
		this.vehicleID=vehicleId;
		this.displayName =displayName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.location=location;
		this.speed = speed;
		this.course = course;
		this.gpsStrength=gps;
		this.gsmStrength=gsm;
		this.lastUpdatedAt=lastupdated;
		this.groupValue="group-"+groupValue;
		this.distirct = distirct;
		this.baselocation = baselocation;
		this.crewno = crewno;
		this.imei = imei;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public float getCourse() {
		return course;
	}

	public void setCourse(float course) {
		this.course = course;
	}

	public Date getLastUpdatedAt() {
		return lastUpdatedAt;
	}

	public void setLastUpdatedAt(Date lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public Date getLastupdatedat() {
		return lastUpdatedAt;
	}

	public void setLastupdatedat(Date lastupdatedat) {
		lastUpdatedAt = lastupdatedat;
	}

	
	public long getVehicleID() {
		return vehicleID;
	}

	public void setVehicleID(long vehicleID) {
		this.vehicleID = vehicleID;
	}

	public List<User> getAssignedUsers() {
		return assignedUsers;
	}

	public void setAssignedUsers(List<User> assignedUsers) {
		this.assignedUsers = assignedUsers;
	}

	@Override
	public long getId() {
		return getVehicleID();
	}

	@Override
	public String[] getLogin() {
		String[] returnString = new String[assignedUsers.size()];
		int i = 0;
		for(User user : assignedUsers){
			returnString[i++] = user.getLogin();
		}
		return returnString;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return TYPE;
	}

	@Override
	public String toJSONString() {
		
		String location="No position match";
		if (Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_POSITION_UPDATE_ENABLED"))) {
			Address address = GeoUtils.fetchNearestLocationFromDB(this.getLatitude(), this.getLongitude());
			if(address != null){
				location = address.toString();
			}
		}
		StringBuffer content = new StringBuffer();
		content.append("{\"id\":\" vehicle-");
		content.append(this.getVehicleID());
		content.append("\"");
		content.append(",\"latitude\":");
		content.append(this.getLatitude());
		content.append(",\"longitude\":");
		content.append(this.getLongitude());
		content.append(",\"location\":\"");
		content.append(location);
		content.append("\"");
		content.append(",\"lastupdated\":\"");
		content.append(DateUtils.convertJavaDateToJsDate(this.getLastupdatedat()));
		content.append("\"");
		content.append(",\"gsm\":");
		content.append(this.getGsmStrength());
		content.append(",\"gps\":");
		content.append(this.getGpsStrength());
		content.append(",\"speed\":");
		content.append(this.getSpeed());
		content.append(",\"course\":");
		content.append(this.getCourse());
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
		content.append(",\"name\":\"");
		content.append(this.getDisplayName());
		content.append("\"");
		content.append(",\"select\":");
		content.append(true);
		content.append("}");

		LOG.debug("Formulated vehiclemap update event");
		return content.toString();
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

	public Long getCrewno() {
		return crewno;
	}

	public void setCrewno(Long crewno) {
		this.crewno = crewno;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

}
