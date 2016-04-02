package com.i10n.db.entity;

import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class RouteDeviation implements IEntity<RouteDeviation> {

	private LongPrimaryKey id;

	private Long vehicleId;

	private String vehicleName;

	private Long stopId;

	private String stopName;

	private Long routeId;

	private String routeName;

	private float estimatedDistance;

	private float actualDistance;
	
	private Timestamp occurredat;


	public RouteDeviation(Long id, Long vehicleId, String vehicleName,
			Long stopId, String stopName, Long routeId, String routeName,
			float estimatedDistance, float actualDistance, Timestamp occurredat) {
		
		super();
		this.id = new LongPrimaryKey(id);
		this.vehicleId = vehicleId;
		this.vehicleName = vehicleName;
		this.stopId = stopId;
		this.stopName = stopName;
		this.routeId = routeId;
		this.routeName = routeName;
		this.estimatedDistance = estimatedDistance;
		this.actualDistance = actualDistance;
		this.occurredat = occurredat;

	}

	public RouteDeviation( Long vehicleId, String vehicleName,
			Long stopId, String stopName, Long routeId, String routeName,
			float estimatedDistance, float actualDistance, Timestamp occurredat) {
		
		super();
		this.vehicleId = vehicleId;
		this.vehicleName = vehicleName;
		this.stopId = stopId;
		this.stopName = stopName;
		this.routeId = routeId;
		this.routeName = routeName;
		this.estimatedDistance = estimatedDistance;
		this.actualDistance = actualDistance;
		this.occurredat = occurredat;

	}
	
	public RouteDeviation() {
		// TODO Auto-generated constructor stub
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
	
	public String getStopName(){
		return stopName;
	}
	
	public void setStopName(String stopName){
		this.stopName = stopName;
	}
	
	public long getRouteId(){
		return routeId;
	}
	
	public void setRouteId(Long routeId){
		this.routeId = routeId;
	}
	
	public String getRouteName(){
		return routeName;
	}
	
	public void setRouteName(String routeName){
		this.routeName = routeName;
	}
	
	public float getEstimatedDistance(){
		return estimatedDistance;
	}
	
	public void setEstimatedDistance(float estimatedDistance){
		this.estimatedDistance = estimatedDistance;
	}
	
	public float getActualDistance(){
		return actualDistance;
	}
	
	public void setActualDistance(float actualDistance){
		this.actualDistance = actualDistance;
	}

	@Override
	public boolean equalsEntity(RouteDeviation object) {
		// TODO Auto-generated method stub
		return false;
	}

	public Timestamp getOccurredAt() {
		// TODO Auto-generated method stub
		return occurredat;
	}

	public void setOccurredAt(Timestamp occurredat) {
		this.occurredat = occurredat;
	}

}
