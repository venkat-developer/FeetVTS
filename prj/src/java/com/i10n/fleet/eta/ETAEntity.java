package com.i10n.fleet.eta;

import java.util.Date;

/**
 * Entity class to hold ETA processing required data
 * 
 * @author Dharmaraju V
 *
 */
public class ETAEntity {

	private Long vehicleId;
	
	private Long tripId;
	
	private String IMEI;
	
	private Long trackHistoryId;

	private Date bulkDataTime; 
	
	private Date moduleUpdateTime;
	
	private double latitude;
	
	private double longitude;
	
	private boolean isPing;
	
	public ETAEntity(Long vehicleId, Long tripId, String IMEI, Long trackHistoryId, Date bulkDataTime, Date moduleUpdateTime,
			double latitude, double longitude, boolean isPing) {
		
		setVehicleId(vehicleId);
		setTripId(tripId);
		setIMEI(IMEI);
		setTrackHistoryId(trackHistoryId);
		setBulkDataTime(bulkDataTime);
		setModuleUpdateTime(moduleUpdateTime);
		setLatitude(latitude);
		setLongitude(longitude);
		setPing(isPing);
	}

	public long getVehicleId() {
		return vehicleId.longValue();
	}

	public void setVehicleId(long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public long getTripId() {
		return tripId.longValue();
	}

	public void setTripId(long tripId) {
		this.tripId = tripId;
	}

	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}

	public long getTrackHistoryId() {
		return trackHistoryId.longValue();
	}

	public void setTrackHistoryId(long trackHistoryId) {
		this.trackHistoryId = trackHistoryId;
	}

	public Date getBulkDataTime() {
		return bulkDataTime;
	}

	public void setBulkDataTime(Date bulkDataTime) {
		this.bulkDataTime = bulkDataTime;
	}

	public Date getModuleUpdateTime() {
		return moduleUpdateTime;
	}

	public void setModuleUpdateTime(Date moduleUpdateTime) {
		this.moduleUpdateTime = moduleUpdateTime;
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

	public boolean isPing() {
		return isPing;
	}

	public void setPing(boolean isPing) {
		this.isPing = isPing;
	}
	
}
