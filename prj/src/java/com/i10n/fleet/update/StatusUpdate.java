package com.i10n.fleet.update;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.i10n.db.entity.User;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.util.StringUtils;

/**
 * Provides the live update to the 
 * vehicle status tab in the livetrack
 * @author Dharmaraju V
 *
 */
public class StatusUpdate implements IStreamUpdate{

	private static Logger LOG = Logger.getLogger(StatusUpdate.class);

	/**
	 * Set of user name who are having this vehicle in the their
	 * Login
	 */
	private List<User> assignedUsers ;
	/**
	 * Id for the vehicle update
	 */
	long vehicleID;

	/**
	 * The display name of the vehicle
	 */
	String Displayname;
	/**
	 * The status of the vehicle
	 * 1-online
	 * 2-idle
	 * 3-offline
	 * 4-stopped
	 */
	int status;
	/**
	 * Boolean which tells the charger of the module is
	 * connected or not
	 */
	boolean cc;
	/**
	 * give the current location of the vehicle in string
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
	 * Gives the battery voltage  of the module in the Vehicles
	 * */

	float batteryvoltage;

	/**
	 * Tells the  update time
	 */
	Date Lastupdatedat;
	/**
	 * Tells the speed
	 */
	float speed;

	/**
	 * This tells the type of the update
	 * to the pushletstreamer
	 */
	private final String TYPE = "statusUpdate";

	private String groupValue;
	
	private String distirct;

	private String baselocation;

	private Long crewno;
	
	private String imei;

	public StatusUpdate(List<User> assignedUsers,long vehicleid,String displayname,int status,boolean cc,String location,
			float gps,float gsm,float battery,Date lastupdated,Long groupValue,float speed, String distirct, String baselocation, Long crewno, String imei){
		this.assignedUsers=assignedUsers;
		this.vehicleID=vehicleid;
		this.Displayname=displayname;
		this.status=status;
		this.cc=cc;
		this.location=location;
		this.gpsStrength=gps;
		this.gsmStrength=gsm;
		this.batteryvoltage=battery;
		this.Lastupdatedat=lastupdated;
		this.groupValue="group-"+groupValue;
        this.speed=speed;
        this.distirct = distirct;
		this.baselocation = baselocation;
		this.crewno = crewno;
		this.imei = imei;

	}

	public String getDisplayname() {
		return Displayname;
	}
	
	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public void setDisplayname(String displayname) {
		Displayname = displayname;
	}


	public String getStatus() {
		if(status ==1){
			if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_SHSM_CLIENT"))){
				return "Moving";
			}else{
				return "Online";
			}
		}else if(status == 2){
			return "Idle";
		}else if(status == 3){
			return "Offline";
		}else if(status == 4){
			return "Stopped";
		}
		return "Stopped";
	}
	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isCc() {
		return cc;
	}

	public void setCc(boolean cc) {
		this.cc = cc;
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

	public float getBatteryvoltage() {
		return batteryvoltage;
	}

	public void setBatteryvoltage(float batteryvoltage) {
		this.batteryvoltage = batteryvoltage;
	}

	public Date getLastupdatedat() {
		return Lastupdatedat;
	}

	public void setLastupdatedat(Date lastupdatedat) {
		Lastupdatedat = lastupdatedat;
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
		return TYPE;
	}

	@Override
	public String toJSONString() {

		StringBuffer content = new StringBuffer();
		content.append("{\"id\":\" vehicle-");
		content.append(this.getVehicleID());
		content.append("\"");
		content.append(",\"location\":\"");
		content.append(this.getLocation());
		content.append("\"");
		content.append(",\"timeinmilliseconds\":\"");
		content.append(this.getLastupdatedat().getTime());
		content.append("\"");
		content.append(",\"battery\":\"");
		content.append(StringUtils.getBatteryStrength(this.getBatteryvoltage()));
		content.append("\",\"gsm\":\"");
		content.append(StringUtils.getGSMSS(this.getGsmStrength()));
		content.append("\",\"gps\":\"");
		content.append(StringUtils.getGPSSS(this.getGpsStrength()));
		content.append("\",\"cc\":\"");
		content.append(this.isCc() ? "Yes" : "No");
		content.append("\"");
		content.append(",\"status\":\"");
		content.append(this.getStatus());
		content.append("\"");
		content.append(",\"groupid\":\"");
		content.append(this.getGroupValue());
		content.append("\"");
		content.append(",\"name\":\"");
		content.append(this.getDisplayname());
		content.append("\"");
		content.append(",\"speed\":\"");
		content.append(this.getSpeed());
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
		content.append(",\"select\":");
		content.append(true);
		content.append("}");

		LOG.debug("Formulated status update event");
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
