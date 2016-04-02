package com.i10n.db.entity;

import java.sql.Timestamp;
import java.util.Date;

import org.postgis.Geometry;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

/**
 * Entity class for TrackHistory table
 * 
 * @author vijaybharath
 * 
 */
public class TrackHistory implements IEntity<TrackHistory> {

	@Override
	public String toString() {
		StringBuffer toString = new StringBuffer();
		toString.append("TrackHistory [ id = ");
		toString.append(id.getId());
		toString.append(", tripId = ");
		toString.append(tripid);
		toString.append(", gpsSignal = ");
		toString.append(gpsSignal);
		toString.append(", gsmSignal = ");
		toString.append(gsmSignal);
		toString.append(", direction");
		toString.append(direction);
		toString.append(", sqd = ");
		toString.append(sqd);
		toString.append(", sqg = ");
		toString.append(sqg);
		toString.append(", batteryVoltage = ");
		toString.append(getBatteryVoltage());
		toString.append(", cumulativeDistance = ");
		toString.append(getCumulativeDistance());
		toString.append(", speed = ");
		toString.append(getSpeed());
		toString.append(", analog1 = ");
		toString.append(getAnalog1());
		toString.append(", analog2 = ");
		toString.append(getAnalog2());
		toString.append(", error = ");
		toString.append(getError());
		toString.append(", lac = ");
		toString.append(getLac());
		toString.append(", cid = ");
		toString.append(getCid());
		toString.append(", isPing = ");
		toString.append(isPing());
		toString.append(", isMrs = ");
		toString.append(isMrs());
		toString.append(", isCC = ");
		toString.append(isChargerConnected());
		toString.append(", isRestart = ");
		toString.append(isRestart());
		toString.append(", digital1 =");
		toString.append(isDigital1());
		toString.append(", digital2 =");
		toString.append(isDigital2());
		toString.append(", digital3 =");
		toString.append(isDigital3());
		toString.append(", isPanic = ");
		toString.append(isPanic());
		toString.append(", occurredAt = '");
		toString.append(new Timestamp(getOccurredat().getTime()));
		toString.append("', location = (");
		toString.append(getLocation().getFirstPoint().getX());
		toString.append(", ");
		toString.append(getLocation().getFirstPoint().getY());
		toString.append("), fuel = ");
		toString.append(getFuel());
		toString.append(", distance = ");
		toString.append(getDistance());
		toString.append(", pingCounter = ");
		toString.append(getPingCounter());
		toString.append(", gpsFixInfo = ");
		toString.append(getGpsFixInformation());
		toString.append(", airDistance = ");
		toString.append(getAirDistance());
		toString.append("] \n");
		return toString.toString();
	}

	public TrackHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	private LongPrimaryKey id;

	private long tripid;

	private float gpsSignal;

	private float gsmSignal;

	private float direction;

	private long sqd;

	private long sqg;

	private float batteryVoltage;

	private float cumulativeDistance;

	private float speed;

	private int analog1;

	private int analog2;

	private int error;

	private long lac;

	private long cid;

	private boolean isPing;

	private boolean isMrs;

	private boolean isChargerConnected;

	private boolean isRestart;

	private boolean digital1;

	private boolean digital2;

	private boolean digital3;

	private boolean isPanic;

	private Date occurredat;

	private Geometry location;

	private float fuel;

	private float distance;

	private long pingCounter;

	private int gpsFixInformation;

	private double avgspeed;

	private float airDistance;
	
	// GVK 102 Related
	private int latestButtonPressed;

	private String buttonSequence;

	public TrackHistory(LongPrimaryKey id, long tripid, float gpsSignal,
			float gsmSignal, float direction, long sqd, long sqg,
			float batteryVoltage, float cumulativeDistance, float speed,
			int analog1, int analog2, int error, long lac, long cid,
			boolean isPing, boolean isMrs, boolean isChargerConnected, boolean isRestart, boolean digital1,
			boolean digital2, boolean digital3, boolean isPanic,
			Date occurredat, Geometry location, float fuel, float distance, long pingCounter, int gpsFixInformation) {
		super();
		this.id = id;
		this.tripid = tripid;
		this.gpsSignal = gpsSignal;
		this.gsmSignal = gsmSignal;
		this.direction = direction;
		this.sqd = sqd;
		this.sqg = sqg;
		this.batteryVoltage = batteryVoltage;
		this.cumulativeDistance = cumulativeDistance;
		this.speed = speed;
		this.analog1 = analog1;
		this.analog2 = analog2;
		this.error = error;
		this.lac = lac;
		this.cid = cid;
		this.isPing = isPing;
		this.isMrs = isMrs;
		this.isChargerConnected = isChargerConnected;
		this.isRestart = isRestart;
		this.digital1 = digital1;
		this.digital2 = digital2;
		this.digital3 = digital3;
		this.isPanic = isPanic;
		this.occurredat = occurredat;
		this.location = location;
		this.fuel = fuel;
		this.distance = distance;
		this.pingCounter = pingCounter;
		this.gpsFixInformation = gpsFixInformation;
	}

	public TrackHistory(float speed, Date occuredat, Geometry location, float cumulativeDistance,
			float distance,float gsmsignal,float gpssignal) {
		this.speed = speed;
		this.occurredat = occuredat;
		this.location = location;
		this.distance = distance;
		this.gpsSignal=gpssignal;
		this.gsmSignal=gsmsignal;
		this.cumulativeDistance = cumulativeDistance;
	}

	public TrackHistory(float distance, float speed) {
		this.distance = distance;
		this.speed = speed;
	}

	public int getGpsFixInformation() {
		return gpsFixInformation;
	}

	public void setGpsFixInformation(int gpsFixInformation) {
		this.gpsFixInformation = gpsFixInformation;
	}

	public boolean isChargerConnected() {
		return isChargerConnected;
	}

	public void setChargerConnected(boolean isChargerConnected) {
		this.isChargerConnected = isChargerConnected;
	}

	public LongPrimaryKey getId() {
		return id;
	}

	public long getPingCounter() {
		return pingCounter;
	}

	public void setPingCounter(long pingCounter) {
		this.pingCounter = pingCounter;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public long getTripid() {
		return tripid;
	}

	public void setTripid(long tripid) {
		this.tripid = tripid;
	}

	public float getGpsSignal() {
		return gpsSignal;
	}

	public void setGpsSignal(float gpsSignal) {
		this.gpsSignal = gpsSignal;
	}

	public float getGsmSignal() {
		return gsmSignal;
	}

	public void setGsmSignal(float gsmSignal) {
		this.gsmSignal = gsmSignal;
	}

	public float getDirection() {
		return direction;
	}

	public void setDirection(float direction) {
		this.direction = direction;
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

	public float getBatteryVoltage() {
		return batteryVoltage;
	}

	public void setBatteryVoltage(float batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}

	public float getCumulativeDistance() {
		return cumulativeDistance;
	}

	public void setCumulativeDistance(float cumulativeDistance) {
		this.cumulativeDistance = cumulativeDistance;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public int getAnalog1() {
		return analog1;
	}

	public void setAnalog1(int analog1) {
		this.analog1 = analog1;
	}

	public int getAnalog2() {
		return analog2;
	}

	public void setAnalog2(int analog2) {
		this.analog2 = analog2;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public long getLac() {
		return lac;
	}

	public void setLac(long lac) {
		this.lac = lac;
	}

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public boolean isPing() {
		return isPing;
	}

	public void setPing(boolean isPing) {
		this.isPing = isPing;
	}

	public boolean isMrs() {
		return isMrs;
	}

	public void setMrs(boolean isMrs) {
		this.isMrs = isMrs;
	}

	public boolean isRestart() {
		return isRestart;
	}

	public void setRestart(boolean isRestart) {
		this.isRestart = isRestart;
	}

	public boolean isDigital1() {
		return digital1;
	}

	public void setDigital1(boolean digital1) {
		this.digital1 = digital1;
	}

	public boolean isDigital2() {
		return digital2;
	}

	public void setDigital2(boolean digital2) {
		this.digital2 = digital2;
	}

	public boolean isDigital3() {
		return digital3;
	}

	public void setDigital3(boolean digital3) {
		this.digital3 = digital3;
	}

	public boolean isPanic() {
		return isPanic;
	}

	public void setPanic(boolean isPanic) {
		this.isPanic = isPanic;
	}

	public Date getOccurredat() {
		return occurredat;
	}

	public void setOccurredat(Date occurredat) {
		this.occurredat = occurredat;
	}

	public Geometry getLocation() {
		return location;
	}

	public void setLocation(Geometry location) {
		this.location = location;
	}

	public float getFuel() {
		return fuel;
	}

	public void setFuel(float fuel) {
		this.fuel = fuel;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public double getAvgSpeed(){
		return avgspeed; 
	}
	public void setAvgSpeed(double avgspeed){
		this.avgspeed = avgspeed;
	}
	@Override
	public boolean equalsEntity(TrackHistory object) {
		// TODO Auto-generated method stub
		return false;
	}
	public float getAirDistance() {
		return airDistance;
	}

	public void setAirDistance(float airDistance) {
		this.airDistance = airDistance;
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
}