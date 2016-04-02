package com.i10n.fleet.container;

import java.util.Date;

import com.i10n.db.entity.LiveVehicleStatus;

/**
 * 
 * @author Antony S (c) i10n 2009
 */
public class BulkUpdateDataBean implements Comparable<BulkUpdateDataBean>{

	/**
	 * Latitude of the Module Update. If both the latitude and longitude are 0, it means
	 * the GPS hasn't got fixed yet.
	 * <b>The accuracy of this parameter depends on the strength of the GPS Signal.</b>
	 * V3 modules denote this parameter as lt.
	 */
	private double latitude;

	/**
	 * Latitude of the Module Update. If both the latitude and longitude are 0, it means
	 * the GPS hasn't got fixed yet.
	 * <b>The accuracy of this parameter depends on the strength of the GPS Signal.</b>
	 * V3 modules denote this parameter as lg.
	 */
	private double longitude;

	/**
	 * Tells the distance covered by the module between the previous update and the 
	 * current update.
	 * V3 modules denote this parameter as d.
	 */
	private double deltaDistance;

	/**
	 * This is an analogue value. Tells the value retrieved from the fuel valves.
	 * This may range from 0 to 1100. The original calibration and the range may vary
	 * according to every vehicle and its fuel tank's size and capacity and the vehicle
	 * model and make.
	 */
	private int fuel;
	/**
	 * Tells the speed of the vehicle
	 */
	private int speed;
	/**
	 * Tells the direction of the vehicle traveled
	 */
	private double direction;
	/**
	 * Tells the time at which the update has occurred in the module.
	 * <b>The accuracy of this parameter depends on the strength of the GPS Signal.</b>
	 * V3 doesn't have this parameter.
	 */
	private Date occurredAt = new Date();

	private int sequenceNumber = 0;

	private int weekNumber;

	private String imei;

	// TODO : to be removed after testing
	private double airDistance;

	private int	batteryVoltage;

	private int	analogue;

	private boolean isNoGPSLock;

	public BulkUpdateDataBean(){

	}

	public BulkUpdateDataBean(LiveVehicleStatus lvos) {
		this.latitude = lvos.getLocation().getFirstPoint().getY();
		this.longitude = lvos.getLocation().getFirstPoint().getX();
		this.occurredAt = lvos.getLastUpdatedAt();
	}

	public BulkUpdateDataBean(double latitude, double longitude, double deltaDistance, Date occurredAt, int analogue) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.deltaDistance = deltaDistance;
		this.occurredAt = occurredAt;
		this.analogue = analogue;
	}

	public BulkUpdateDataBean(double latitude, double longitude, double deltaDistance, Date occurredAt,int fuel,int speed,double direction) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.deltaDistance = deltaDistance;
		this.occurredAt = occurredAt;
		this.fuel=fuel;
		this.speed=speed;
		this.direction=direction;
	}

	public BulkUpdateDataBean(double latitude, double longitude, double deltaDistance, int weeknumber, int sequnce, int fuel, Date occurredAt) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.deltaDistance = deltaDistance;
		this.weekNumber = weeknumber;
		this.sequenceNumber = sequnce;
		this.occurredAt = occurredAt;
		this.fuel = fuel;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getDeltaDistance() {
		return deltaDistance;
	}

	public double getDirection() {
		return direction;
	}
	public int getSpeed() {
		return speed;
	}
	public int getFuel() {
		return fuel;
	}

	public Date getOccurredAt() {
		return  occurredAt;
	}

	public void setDeltaDistance(double deltaDistance) {
		this.deltaDistance = deltaDistance;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}
	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	public int getWeekNumber() {
		return weekNumber;
	}
	public void setWeekNumber(int weekNumber) {
		this.weekNumber = weekNumber;
	}

	public void setFuel(int fuel) {
		this.fuel = fuel;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public void setDirection(double direction) {
		this.direction = direction;
	}

	public double getAirDistance() {
		return airDistance;
	}

	public void setAirDistance(double airDistance) {
		this.airDistance = airDistance;
	}

	public int getBatteryVoltage() {
		return batteryVoltage;
	}

	public void setBatteryVoltage(int batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}

	public int getAnalogue() {
		return analogue;
	}

	public void setAnalogue(int analogue) {
		this.analogue = analogue;
	}

	public boolean isNoGPSLock() {
		return isNoGPSLock;
	}

	public void setNoGPSLock(boolean isNoGPSLock) {
		this.isNoGPSLock = isNoGPSLock;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setOccurredAt(Date occurredAt) {
		this.occurredAt = occurredAt;
	}

	@Override
	public int compareTo(BulkUpdateDataBean bulkBean) {
		return occurredAt.compareTo(bulkBean.getOccurredAt());
	}

	@Override
	public String toString(){
		StringBuffer returnString = new StringBuffer();
		returnString.append(" Latitude : ");
		returnString.append(getLatitude());
		returnString.append(", Longitude : ");
		returnString.append(getLongitude());
		returnString.append(", DeltaDistance : ");
		returnString.append(getDeltaDistance());
		returnString.append(", AirDistance : ");
		returnString.append(getAirDistance());
		returnString.append(", TOW : ");
		returnString.append(getOccurredAt().toString());
		returnString.append(", BatteryVoltage : ");
		returnString.append(getBatteryVoltage());
		returnString.append(", Speed : ");
		returnString.append(getSpeed());
		returnString.append(", Analogue : ");
		returnString.append(getAnalogue());
		returnString.append(", NoGPSLOCK : ");
		returnString.append(isNoGPSLock());

		return returnString.toString();
	}

}
