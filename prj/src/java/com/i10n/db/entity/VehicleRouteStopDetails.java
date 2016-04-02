//package com.i10n.db.entity;
//
//import java.sql.Time;
//
//import com.i10n.db.entity.IEntity.IEntity;
///**
// * 
// * @author dharmaraju v
// *
// */
//public class VehicleRouteStopDetails implements IEntity<VehicleRouteStopDetails>{
//
//	private long vehicleId;
//	private long routeId;
//	private long stopId;
//	private int sequenceNumber;
//	private Time expectedTime;
//	private float estimatedDistance;
//	private int spanDay;
//	
//	public VehicleRouteStopDetails(long vehicleId, long routeId, long stopId, int sequenceNumber,Time expectedTime, float estimatedDistance, int spanDay) {
//		this.vehicleId = vehicleId;
//		this.routeId = routeId;
//		this.stopId = stopId;
//		this.sequenceNumber = sequenceNumber;
//		this.expectedTime = expectedTime;
//		this.estimatedDistance = estimatedDistance;
//		this.spanDay = spanDay;
//	}
//	
//	public VehicleRouteStopDetails() {
//		// TODO Auto-generated constructor stub
//	}
//
//	public long getRouteId() {
//		return routeId;
//	}
//
//	public void setRouteId(long routeId) {
//		this.routeId = routeId;
//	}
//
//	public long getStopId() {
//		return stopId;
//	}
//
//	public void setStopId(long stopId) {
//		this.stopId = stopId;
//	}
//
//	public int getSequenceNumber() {
//		return sequenceNumber;
//	}
//
//	public void setSequenceNumber(int sequenceNumber) {
//		this.sequenceNumber = sequenceNumber;
//	}
//
//	public Time getExpectedTime() {
//		return expectedTime;
//	}
//
//	public void setExpectedTime(Time expectedTime) {
//		this.expectedTime = expectedTime;
//	}
//
//	public float getEstimatedDistance() {
//		return estimatedDistance;
//	}
//
//	public void setEstimatedDistance(float estimatedDistance) {
//		this.estimatedDistance = estimatedDistance;
//	}
//
//	public int getSpanDay() {
//		return spanDay;
//	}
//
//	public void setSpanDay(int spanDay) {
//		this.spanDay = spanDay;
//	}
//
//	public long getVehicleId() {
//		return vehicleId;
//	}
//
//	public void setVehicleId(long vehicleId) {
//		this.vehicleId = vehicleId;
//	}
//
//	@Override
//	public boolean equals(VehicleRouteStopDetails object) {
//		return false;
//	}
//
//	public String toString(){
//		StringBuffer returnString = new StringBuffer();
//		returnString.append("VehicleId = ");
//		returnString.append(vehicleId);
//		returnString.append(", RouteId = ");
//		returnString.append(routeId);
//		returnString.append(", StopId = ");
//		returnString.append(stopId);
//		returnString.append(", SequenceNo = ");
//		returnString.append(sequenceNumber);
//		returnString.append(", ExpectedTime = ");
//		returnString.append(expectedTime);
//		returnString.append(", SpanDays = ");
//		returnString.append(spanDay);
//		return returnString.toString();
//	}
//}
