package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;

/**
 * Entity having the mapping of the Module/LED to the BusStop of the Route
 * 
 * @author Dharmaraju V
 *
 */
public class LEDToBusStop implements IEntity<LEDToBusStop> {
	
	public static final int CHINESE_LED = 0;
	public static final int ARYA_LED = 1;
	
	// Identity of the Module  
	private String deviceID ;
	
	// Identity of the Stop of the Route
	private Long busStopId;
	
	// General identity of the device
	// 0 - Chinese LED
	// 1 - Arya LED 
	private int ledType;
	
	public LEDToBusStop(String deviceID, Long busStopId, int ledType) {
		this.deviceID = deviceID;
		this.busStopId = busStopId;
		this.ledType = ledType;
	}
	
	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public Long getBusStopId() {
		return busStopId;
	}

	public void setBusStopId(Long busStopId) {
		this.busStopId = busStopId;
	}

	public int getLedType() {
		return ledType;
	}

	public void setLedType(int ledType) {
		this.ledType = ledType;
	}

	@Override
	public boolean equalsEntity(LEDToBusStop object) {
		// TODO Auto-generated method stub
		return false;
	}

}
