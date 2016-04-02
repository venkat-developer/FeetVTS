package com.i10n.db.entity;
import java.sql.Time;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;


public class ShiftTimings implements IEntity< ShiftTimings> 

{
	 private LongPrimaryKey  id ;
	 private Long vehicleId;
	 private Time startTime ;
	 private Time endTime;

	 public ShiftTimings(LongPrimaryKey id, Long vehicleId,
			Time startTime, Time endTime) {
		super();
		this.id = id;
		this.vehicleId = vehicleId;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public LongPrimaryKey getId() {
		return id;
	}
	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public Long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(Long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Time getEndTime() {
		return endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

	@Override
	public boolean equalsEntity(ShiftTimings  object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toString(){
	    StringBuilder strBuilder = new StringBuilder();
	    strBuilder.append(getId().getId()).append("-").append(getVehicleId()).append("-").append(getStartTime()).append("-")
	    .append(getEndTime());
	    strBuilder.append("-");
	  return strBuilder.toString();
	}
}