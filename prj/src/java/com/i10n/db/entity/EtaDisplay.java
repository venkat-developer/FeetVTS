package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;

public class EtaDisplay implements IEntity<EtaDisplay>{

	private long vehicleId;
	private long routeId;
	private long stopId;
	private Integer arrivalTime;
	private String routeName;
	private int type;
	private boolean deleted;
	private int sequneceNumber;
	
	public EtaDisplay( long vehicleId , long routeId, long stopId, int arrivalTime,String routeName, int type, boolean deleted, int sequenceNumber){
		this.vehicleId = vehicleId;
		this.routeId = routeId;
		this.stopId = stopId;
		this.arrivalTime =arrivalTime;
		this.routeName = routeName;
		this.type = type;
		this.deleted = deleted;
		this.sequneceNumber = sequenceNumber;
	}
	
	public EtaDisplay() {
		// TODO Auto-generated constructor stub
	}

	public long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public long getRouteId() {
		return routeId;
	}

	public void setRouteId(long routeId) {
		this.routeId = routeId;
	}

	public long getStopId() {
		return stopId;
	}

	public void setStopId(long stopId) {
		this.stopId = stopId;
	}

	public Integer getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	public int getSequneceNumber() {
		return sequneceNumber;
	}

	public void setSequneceNumber(int sequneceNumber) {
		this.sequneceNumber = sequneceNumber;
	}

	public String toString(){
		StringBuffer returnString = new StringBuffer(); 
		returnString.append("VehicleId = ");
		returnString.append(vehicleId);
		returnString.append(", RouteId = ");
		returnString.append(routeId);
		returnString.append(", StopId = ");
		returnString.append(stopId);
		returnString.append(", SequenceNumber = ");
		returnString.append(sequneceNumber);
		returnString.append(", ArrivalTime = ");
		returnString.append(arrivalTime);
		returnString.append(", Type = ");
		returnString.append(type);
		/*returnString.append(", IsDeleted = ");
		returnString.append(deleted)*/;
		return returnString.toString();
		
	}

	@Override
	public boolean equalsEntity(EtaDisplay object) {
		// TODO Auto-generated method stub
		return false;
	}

}
