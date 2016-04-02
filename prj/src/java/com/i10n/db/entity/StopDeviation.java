package com.i10n.db.entity;

import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class StopDeviation implements IEntity<StopDeviation> {

	private LongPrimaryKey id;

	private Long vehicleId;

	private String vehicleName;

	private Long stopId;

	private String missedStopName;

	private Long routeId;

	private Timestamp expectedTime;
	
	private Timestamp occurredat;

	public StopDeviation() {
		// TODO Auto-generated constructor stub
	}

	public StopDeviation(Long id, Long vehicleId, String vehicleName,
			Long stopId, String missedStopName, Long routeid, Timestamp expectedTime, Timestamp occurredat) {

		super();
		this.id = new LongPrimaryKey(id);
		this.vehicleId = vehicleId;
		this.vehicleName = vehicleName;
		this.stopId = stopId;
		this.routeId = routeid;
		this.missedStopName = missedStopName;
		this.expectedTime = expectedTime;
		this.occurredat = occurredat;
	}
	
	public StopDeviation(Long vehicleId, String vehicleName,
			Long stopId, String missedStopName, Long routeid, Timestamp stopExpectedTime, Timestamp occurredat) {

		super();
		this.vehicleId = vehicleId;
		this.vehicleName = vehicleName;
		this.stopId = stopId;
		this.routeId = routeid;
		this.missedStopName = missedStopName;
		this.expectedTime = stopExpectedTime;
		this.occurredat = occurredat;
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
	
	public long getStopId(){
		return stopId;
	}
	
	public void setStopId(Long stopId){
		this.stopId = stopId;
	}
	
	public String getMissedStopName(){
		return missedStopName;
	}
	
	public void setMissedStopName(String missedStopName){
		this.missedStopName = missedStopName;
	}
	
	public long getRouteId(){
		return routeId;
	}
	
	public void setRouteId(Long routeId){
		this.routeId = routeId;
	}
	
	public Timestamp getExpectedTime(){
		return expectedTime;
	}
	
	public void setExpectedTime(Timestamp expectedTime){
		this.expectedTime = expectedTime;
	}

	@Override
	public boolean equalsEntity(StopDeviation object) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setOccurredat(Timestamp occurredat) {
		this.occurredat = occurredat;
	}

	public Timestamp getOccurredat() {
		return occurredat;
	}

}
