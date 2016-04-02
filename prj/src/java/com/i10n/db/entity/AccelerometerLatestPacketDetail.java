package com.i10n.db.entity;

import java.util.Date;

import org.postgis.Geometry;

import com.i10n.db.entity.IEntity.IEntity;

/**
 * @author joshua
 * 
 */
public class AccelerometerLatestPacketDetail implements
IEntity<AccelerometerLatestPacketDetail> {

    private String imei="";
   
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

	private int xmin;

	private int xmax;

	private int ymin;

	private int ymax;

	private int zmin;

	private int zmax;

	private boolean isPanic;

	private Date occurredat;

	private Geometry location;

	private double fuel;

	private float distance;

	private double avgspeed;

	private double lat;

	private double lng;

	public double getLat() {
		return lat;
	}


	public void setLat(double lat) {
		this.lat = lat;
	}


	public double getLng() {
		return lng;
	}


	public void setLng(double lng) {
		this.lng = lng;
	}


	@Override
	public boolean equalsEntity(AccelerometerLatestPacketDetail object) {
		// TODO Auto-generated method stub
		return false;
	}


	public String getImei() {
		return imei;
	}


	public void setImei(String imei) {
		this.imei = imei;
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

	public boolean isChargerConnected() {
		return isChargerConnected;
	}

	public void setChargerConnected(boolean isChargerConnected) {
		this.isChargerConnected = isChargerConnected;
	}

	public boolean isRestart() {
		return isRestart;
	}

	public void setRestart(boolean isRestart) {
		this.isRestart = isRestart;
	}

	public int getXmin() {
		return xmin;
	}

	public void setXmin(int xmin) {
		this.xmin = xmin;
	}

	public int getXmax() {
		return xmax;
	}

	public void setXmax(int xmax) {
		this.xmax = xmax;
	}

	public int getYmin() {
		return ymin;
	}

	public void setYmin(int ymin) {
		this.ymin = ymin;
	}

	public int getYmax() {
		return ymax;
	}

	public void setYmax(int ymax) {
		this.ymax = ymax;
	}

	public int getZmin() {
		return zmin;
	}

	public void setZmin(int zmin) {
		this.zmin = zmin;
	}

	public int getZmax() {
		return zmax;
	}

	public void setZmax(int zmax) {
		this.zmax = zmax;
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

	public double getFuel() {
		return fuel;
	}

	public void setFuel(double fuel) {
		this.fuel = fuel;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public double getAvgspeed() {
		return avgspeed;
	}

	public void setAvgspeed(double avgspeed) {
		this.avgspeed = avgspeed;
	}


	@Override
	public String toString() {
		return "AccelerometerLatestPacketDetail [analog1=" + analog1
				+ ", analog2=" + analog2 + ", avgspeed=" + avgspeed
				+ ", batteryVoltage=" + batteryVoltage + ", cid=" + cid
				+ ", cumulativeDistance=" + cumulativeDistance + ", direction="
				+ direction + ", distance=" + distance + ", error=" + error
				+ ", fuel=" + fuel + ", gpsSignal=" + gpsSignal
				+ ", gsmSignal=" + gsmSignal + ", imei=" + imei
				+ ", isChargerConnected=" + isChargerConnected + ", isMrs="
				+ isMrs + ", isPanic=" + isPanic + ", isPing=" + isPing
				+ ", isRestart=" + isRestart + ", lac=" + lac + ", lat=" + lat
				+ ", lng=" + lng + ", location=" + location + ", occurredat="
				+ occurredat + ", speed=" + speed + ", sqd=" + sqd + ", sqg="
				+ sqg + ", xmax=" + xmax + ", xmin=" + xmin + ", ymax=" + ymax
				+ ", ymin=" + ymin + ", zmax=" + zmax + ", zmin=" + zmin + "]";
	}

	
}
