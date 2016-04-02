package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;

public class TeltonikaAVLData implements IEntity<TeltonikaAVLData> {
	private long utcTime = 0;
	private int priority = 0;
	// Following are the GPS elements

	private int longitude = 0;
	private int latitude = 0;
	private int altitude = 0;
	private int direction = 0;
	private int satelliteCount = 0;
	private int speed = 0;
	// The IO elements are read and discared as we are not utilizing the values. 
	
	public TeltonikaAVLData() {
		
	}
	
	public long getUtcTime() {
		return utcTime;
	}

	public void setUtcTime(long utcTime) {
		this.utcTime = utcTime;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	public int getAltitude() {
		return altitude;
	}

	public void setAltitude(int altitude) {
		this.altitude = altitude;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getSatelliteCount() {
		return satelliteCount;
	}

	public void setSatelliteCount(int satelliteCount) {
		this.satelliteCount = satelliteCount;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	@Override
	public boolean equalsEntity(TeltonikaAVLData object) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
