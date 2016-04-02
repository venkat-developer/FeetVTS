package com.i10n.db.entity;

import java.sql.Time;
import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

/**
 * Entity class for StopHistory table.
 * @author Dharmaraju V
 *
 */
public class StopHistory implements IEntity<StopHistory>{

	private LongPrimaryKey id ;
	private long stopId;
	private long vehicleId;
	private long routeId;
	private String routeScheduleId;
	private Timestamp expectedTime;
	private Timestamp actualTime;
	
	private Time mbmcExpectedTime;
	private Time mbmcActualTime;
	private float estimateDistance;
	private float actualDistance;
	private boolean active;
	private int seqNo; 
	private long trackHistoryId;
	
	public StopHistory(LongPrimaryKey id, long stopId, long vehicleId, long routeId, String routeScheduleId, Time expectedtime2, 
			Time actualtime2, float estimatedDistance, float actualDistance, boolean active,int seqNo, long trackHistoryId) {
		this.id = id;
		this.stopId = stopId;
		this.vehicleId = vehicleId;
		this.routeId = routeId;
		this.routeScheduleId = routeScheduleId;
		this.mbmcExpectedTime = expectedtime2;
		this.mbmcActualTime = actualtime2;
		this.estimateDistance = estimatedDistance;
		this.actualDistance = actualDistance;
		this.active = active;
		this.seqNo = seqNo;
		this.trackHistoryId = trackHistoryId;
	}
	
	public StopHistory() {
	}

	public StopHistory(long stopId, long vehicleId, long routeId, String routeScheduleId, Timestamp expectedTime, Timestamp actualTime,
			float estimatedDistance, float actualDistance, boolean active,int sequenceNumber, long trackHistoryId) {
		this.stopId = stopId;
		this.vehicleId = vehicleId;
		this.routeId = routeId;
		this.routeScheduleId = routeScheduleId;
		this.expectedTime = expectedTime;
		this.actualTime = actualTime;
		this.estimateDistance = estimatedDistance;
		this.actualDistance = actualDistance;
		this.active = active;
		this.seqNo = sequenceNumber;
		this.trackHistoryId = trackHistoryId;
	}

	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public long getStopId() {
		return stopId;
	}

	public void setStopId(long stopId) {
		this.stopId = stopId;
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

	public String getRouteScheduleId() {
		return routeScheduleId;
	}

	public void setRouteScheduleId(String routeScheduleId) {
		this.routeScheduleId = routeScheduleId;
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

	public float getEstimateDistance() {
		return estimateDistance;
	}

	public void setEstimateDistance(float estimateDistance) {
		this.estimateDistance = estimateDistance;
	}

	public float getActualDistance() {
		return actualDistance;
	}

	public void setActualDistance(float actualDistance) {
		this.actualDistance = actualDistance;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	@Override
	public boolean equalsEntity(StopHistory object) {
		if(object.getId().getId() == id.getId()){
			return true;
		}
		return false;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public long getTrackHistoryId() {
		return trackHistoryId;
	}

	public void setTrackHistoryId(long trackHistoryId) {
		this.trackHistoryId = trackHistoryId;
	}

	public String toString(){
		StringBuffer returnsString = new StringBuffer();
		returnsString.append("StopHistoryId = ");
		returnsString.append(id.getId());
		returnsString.append(", VehicleId = ");
		returnsString.append(vehicleId);
		returnsString.append(", RouteId = ");
		returnsString.append(routeId);
		returnsString.append(", StopId = ");
		returnsString.append(stopId);
		returnsString.append(", SequenceNo = ");
		returnsString.append(seqNo);
		returnsString.append(", ExpectedTime = ");
		returnsString.append(mbmcExpectedTime);
		returnsString.append(", ActualTime = ");
		returnsString.append(mbmcActualTime);
		returnsString.append(", TrackhistoryId = ");
		returnsString.append(trackHistoryId);
		returnsString.append(", IsActive = ");
		returnsString.append(active);
		return returnsString.toString();
	}

	public Time getMbmcExpectedTime() {
		return mbmcExpectedTime;
	}

	public void setMbmcExpectedTime(Time mbmcExpectedTime) {
		this.mbmcExpectedTime = mbmcExpectedTime;
	}

	public Time getMbmcActualTime() {
		return mbmcActualTime;
	}

	public void setMbmcActualTime(Time mbmcActualTime) {
		this.mbmcActualTime = mbmcActualTime;
	}
}
