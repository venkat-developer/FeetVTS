package com.i10n.db.entity;

import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.postgis.Geometry;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

/**
 * Alert entity general architecture
 * 
 * @author Dharmaraju V
 *
 */
public class AlertOrViolation implements IEntity<AlertOrViolation> {
	
	Logger LOG = Logger.getLogger(AlertOrViolation.class);

	// Unique ID of Entity
	private LongPrimaryKey id;
	// Vehicle detail which is responsible
	private Long vehicleId;
	// Driver detail which is responsible
	private Long driverId;
	// Time at which the alert occurred
	private Date alertTime;
	// Time at which alert is received
	private Date occurredAt;
	/*	Type of alert
	 * 
	 * 	1	- CC True
	 * 	2	- CC False
	 * 	3	- OverSpeeding 
	 * 	4	- GeoFencing Out
	 * 	5	- GeoFencing In
	 * 	6	- GPS Loss
	 * 	7	- GSM Loss
	 * 	8	- Hardware Down
	 * 	9	- Software Down
	 * 	10	- Battery Low
	 * 	11	- SOS Call In
	 * 	12	- SOS Call Out
	 * 	13	- Accident
	 * 	14	- Sudden Halt
	 * 	15	- Idle
	 * 	16 	- Bunching
	 * 	17 	- GPRS_OUT
	 * 	18	- GPRS_IN
	 * 	19	- SERVICE
	 *	20 	- SeatBelt On
	 *	21	- SeatBelt Removed
	 *	22 	- Ignition On
	 *	23	- Ignition Off
	 *	24	- Halting Alert
	 *	25	- TNCSC Trip Start
	 *	26	- TNCSC Trip End
	 *	27	- TNCSC Pause 15
	 *	28	- TNCSC Resume 15
	 *	29	- Power Off
	 *	30	- Reboot
	 *	31	- AirPlane Mode ON
	 *	32	- AirPlane Mode OFF
	 */
	public enum AlertType{
		CHARGER_CONNECTED(1),			//{"1", 	"Charger Connected"},
		CHARGER_DISCONNECTED(2),		//{"2", 	"Charger DisConnected"},
		OVERSPEED(3),					//{"3", 	"OverSpeed"},				//	What is the speed?
		GEOFENCING_OUT(4),				//{"4", 	"Geofencing Out"},			
		GEOFENCING_IN(5),				//{"5", 	"Geofencing In"},
		GPS_LOSS(6),					//{"6", 	"GPS Loss"},
		GSM_LOSS(7),					//{"7", 	"GSM Loss"},
		HARDWARE_DOWN(8),				//{"8", 	"Hardware Down"},
		SOFTWARE_DOWN(9),				//{"9", 	"Software Down"},
		BATTERY_LOW(10),				//{"10", 	"Battery Low"},				// 	What is the value?
		SOS_CALL_IN(11),				//{"11", 	"SOS Call In"},				// 	From which number?
		SOS_CALL_OUT(12),				//{"12", 	"SOS Call Out"},			// 	To which number?
		ACCIDENT(13),					//{"13", 	"Accident"},
		SUDDEN_HALT(14),				//{"14", 	"Sudden Halt"},
		IDLE(15),						//{"15", 	"Idle"},
		BUNCHING(16),					//{"16", 	"Bunching"},				//	With which Vehicle
		GPRS_OUT(17),					//{"17", 	"GPRS Out"},
		GPRS_IN(18),					//{"18", 	"GPRS In"},
		SERVICE(19),					//{"19", 	"Service"},
		SEATBELT_ON(20),				//{"20", 	"SeatBelt On"},
		SEATBELT_REMOVED(21),			//{"21", 	"SeatBelt Removed"},
		IGNITION_ON(22),				//{"22", 	"Ignition On"},
		IGNITION_OFF(23),				//{"23", 	"Ignition Off"},
		HALTING_ALERT(24),				//{"24",	"Halting Alert"},
		TNCSC_TRIP_START(25),			//{"25",	"TNCSC Trip Start"},
		TNCSC_TRIP_END(26),				//{"26",	"TNCSC Trip End"},
		TNCSC_PAUSE_15(27),				//{"27",	"TNCSC Pause 15"},
		TNCSC_RESUME_15(28),			//{"28",	"TNCSC Resume 15"},
		POWER_OFF(29),					//{"29",	"Power Off"},
		REBOOT(30),						//{"30",	"Reboot"},
		AIRPLANE_MODE_ON(31),			//{"31",	"Airplane Mode On"},
		AIRPLANE_MODE_OFF(32),			//{"32",	"Airplane Mode Off"},
		PANIC(33),						//{"33",	"Panic"}
		INVALID(9999);
		
		private int value;
	    private static final Map<Integer, AlertType> lookup = new HashMap<Integer, AlertType>();

	    private AlertType(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }

	    public static AlertType get(int value) {
	    	if(lookup.get(value) == null ){
	    		return INVALID;
	    	}else{
	    		return lookup.get(value);
	    	}
	    }
	    
	    static {
	        for (AlertType s : EnumSet.allOf(AlertType.class)){
	            lookup.put(s.getValue(), s);
	        }
	    }
	}
	
	private AlertType alertType;
	// Get the value of certain alert type 
	private String alertTypeValue;
	// Location at which violation happened
	private Geometry alertLocation;
	// String value reference from DB
	private Long alertLocationReferenceId;
	// Location at which violation happened in String format
	private String alertLocationText;
	// Has this alert notified to the user
	private boolean isDisplayed;
	// Has the alert notified to the user via mail or SMS or both
	private boolean isUserNotified;
	
	private String geofenceArea; 
	
	private double previousMaxSpeed;
	
	private boolean sent;
	
	public AlertOrViolation(LongPrimaryKey id, Long vehicleId, Long driverId, Date alertTime, Date occurredAt, AlertType alertType,
			String alertTypeValue, Geometry alertLocation, Long alertLocationReferenceId, String alertLocationText, 
			boolean isDisplayed, boolean isUserNotified) {
		super();
		this.id = id;
		this.vehicleId = vehicleId;
		this.driverId = driverId;
		this.alertTime = alertTime;
		this.occurredAt = occurredAt;
		this.alertType = alertType;
		this.alertTypeValue = alertTypeValue;
		this.alertLocation = alertLocation;
		this.alertLocationReferenceId = alertLocationReferenceId;
		this.alertLocationText = alertLocationText;
		this.isDisplayed = isDisplayed;
		this.isUserNotified = isUserNotified;
	}
	
	public AlertOrViolation(/*LongPrimaryKey id, */Long vehicleId, Long driverId, Date alertTime, Date occurredAt, AlertType alertType, 
			String alertTypeValue, Geometry alertLocation) {
		super();
		this.vehicleId = vehicleId;
		this.driverId = driverId;
		this.alertTime = alertTime;
		this.occurredAt = occurredAt;
		this.alertType = alertType;
		this.alertTypeValue = alertTypeValue;
		this.alertLocation = alertLocation;
	}
	
	public AlertOrViolation(/*LongPrimaryKey id ,*/ Long vehicleId, long driverId, Date alertTime, Date occurredAt, Geometry alertLocation) {
		super();
		//this.id = id;
		this.vehicleId = vehicleId;
		this.driverId = driverId;
		this.alertTime = alertTime;
		this.occurredAt = occurredAt;
		this.alertLocation = alertLocation;
	}

	public AlertOrViolation() {
		// TODO Auto-generated constructor stub
	}

	public AlertOrViolation(boolean isSent, double previousSpeed) {
		this.sent=isSent;
		this.previousMaxSpeed=previousSpeed;
	}

	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public Long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(Long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public Long getDriverId() {
		return driverId;
	}

	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}

	public Date getAlertTime() {
		return alertTime;
	}

	public void setAlertTime(Date alertTime) {
		this.alertTime = alertTime;
	}

	public Date getOccurredAt() {
		return occurredAt;
	}

	public void setOccurredAt(Date occurredAt) {
		this.occurredAt = occurredAt;
	}

	public AlertType getAlertType() {
		return alertType;
	}
	public void name() {
		
	}
	
	public void setAlertType(AlertType alertType) {
		this.alertType = alertType;
	}
	

	public String getAlertTypeValue() {
		return alertTypeValue;
	}

	public void setAlertTypeValue(String alertTypeValue) {
		this.alertTypeValue = alertTypeValue;
	}

	public Geometry getAlertLocation() {
		return alertLocation;
	}

	public void setAlertLocation(Geometry alertLocation) {
		this.alertLocation = alertLocation;
	}

	public Long getAlertLocationReferenceId() {
		return alertLocationReferenceId;
	}

	public void setAlertLocationReferenceId(Long alertLocationReferenceId) {
		this.alertLocationReferenceId = alertLocationReferenceId;
	}

	public String getAlertLocationText() {
		return alertLocationText;
	}

	public void setAlertLocationText(String alertLocationText) {
		this.alertLocationText = alertLocationText;
	}

	public boolean isDisplayed() {
		return isDisplayed;
	}

	public void setDisplayed(boolean isDisplayed) {
		this.isDisplayed = isDisplayed;
	}

	public boolean isUserNotified() {
		return isUserNotified;
	}

	public void setUserNotified(boolean isUserNotified) {
		this.isUserNotified = isUserNotified;
	}

	@Override
	public String toString(){
		StringBuffer returnString = new StringBuffer();
		returnString.append("Alert : ");
		if(getId() != null){
			returnString.append("ID = ");
			returnString.append(getId().getId());
			returnString.append(", ");
		}
		returnString.append("vehicleId = "+getVehicleId());
		returnString.append(", driverId = "+getDriverId());
		returnString.append(", alertType = "+getAlertType());
		returnString.append(", alertTime = "+getAlertTime().toString());
		returnString.append(", occurredAt = "+getOccurredAt().toString());
		returnString.append(", alertLocation = "+getAlertLocation().getFirstPoint().y+" "+getAlertLocation().getFirstPoint().x);
		returnString.append(", alertLocationReferenceId = "+ getAlertLocationReferenceId());
		returnString.append(", alertLocationText = "+getAlertLocationText());
		returnString.append(", isDisplayed = "+isDisplayed());
		returnString.append(", isUserNotified ="+isUserNotified());
	    returnString.append(", region name = "+getGeofenceArea());
		return returnString.toString();
	}

	public boolean equals(AlertOrViolation object) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setGeofenceArea(String geofenceArea) {
		this.geofenceArea = geofenceArea;
	}

	public String getGeofenceArea() {
		return geofenceArea;
	}

	public void setPreviousMaxSpeed(double previousSpeed) {
		this.previousMaxSpeed = previousSpeed;
	}

	public double getPreviousMaxSpeed() {
		return previousMaxSpeed;
	}

	public void setAlertSent(boolean alertsStatus) {
		this.sent = alertsStatus;
	}

	public boolean isAlertSent() {
		return sent;
	}

	@Override
	public boolean equalsEntity(AlertOrViolation object) {
		// TODO Auto-generated method stub
		return false;
	}
}
