package com.i10n.db.entity;

import java.sql.Time;

import com.i10n.db.entity.IEntity.IEntity;

/**
 * Entity representing the vehicle mapping to the route's schedule 
 * @author Dharmaraju V 
 *
 */
public class VehicleToRouteSchedule implements IEntity<VehicleToRouteSchedule> {
	
	private Long vehicleId; 
	private Long routeId; 
	private Time scheduleTime;
	
	public VehicleToRouteSchedule(Long vehicleId, Long routeId, Time scheduleTime) {
		setVehicleId(vehicleId);
		setRouteId(routeId);
		setScheduleTime(scheduleTime);
	}
	
	public Long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(Long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public Long getRouteId() {
		return routeId;
	}

	public void setRouteId(Long routeId) {
		this.routeId = routeId;
	}

	public Time getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(Time scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	@Override
	public boolean equalsEntity(VehicleToRouteSchedule object) {
		if((object.getRouteId() == routeId) && 
				(object.getVehicleId() == vehicleId) && 
				(object.getScheduleTime().getTime() == scheduleTime.getTime())){
			return true;
		}
		return false;
	}

}
