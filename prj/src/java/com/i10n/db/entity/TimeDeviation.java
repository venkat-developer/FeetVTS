package com.i10n.db.entity;

import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class TimeDeviation implements IEntity<TimeDeviation> {

	private LongPrimaryKey id;

	private Long vehicleId;

	private String vehicleName;

	private Long stopId;

	private String stopName;

	private Long routeId;

	private String routeName;

	private Timestamp expectedTime;

	private Timestamp actualTime;

	public TimeDeviation() {
	}

	public TimeDeviation(Long id, Long vehicleId, String vehicleName,
			Long stopId, String stopName, Long routeId, String routeName,
			Timestamp expectedTime, Timestamp actualTime) {

		super();
		this.id = new LongPrimaryKey(id);
		this.vehicleId = vehicleId;
		this.vehicleName = vehicleName;
		this.stopId = stopId;
		this.stopName = stopName;
		this.routeId = routeId;
		this.routeName = routeName;
		this.expectedTime = expectedTime;
		this.actualTime = actualTime;

	}
	
	public TimeDeviation(Long vehicleId, String vehicleName,
			Long stopId, String stopName, Long routeId, String routeName,
			Timestamp expectedTime, Timestamp actualTime) {

		super();
		this.vehicleId = vehicleId;
		this.vehicleName = vehicleName;
		this.stopId = stopId;
		this.stopName = stopName;
		this.routeId = routeId;
		this.routeName = routeName;
		this.expectedTime = expectedTime;
		this.actualTime = actualTime;

	}

	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(Long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getVehicleName() {
		return vehicleName;
	}

	public void setVehicleName(String vehicleName) {
		this.vehicleName = vehicleName;
	}

	public long getStopId() {
		return stopId;
	}

	public void setStopId(Long stopId) {
		this.stopId = stopId;
	}

	public String getStopName() {
		return stopName;
	}

	public void setStopName(String stopName) {
		this.stopName = stopName;
	}

	public long getRouteId() {
		return routeId;
	}

	public void setRouteId(Long routeId) {
		this.routeId = routeId;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public Timestamp getExpectedTime() {
		return expectedTime;
	}

	public void setExpectedTime(Timestamp expectedTime) {
		this.expectedTime = expectedTime;
	}

	public Timestamp getActualTime() {
		return actualTime;
	}

	public void setActualTime(Timestamp actualTime) {
		this.actualTime = actualTime;
	}

	@Override
	public boolean equalsEntity(TimeDeviation object) {
		// TODO Auto-generated method stub
		return false;
	}

}
