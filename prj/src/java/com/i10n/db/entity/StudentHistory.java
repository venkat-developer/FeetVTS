package com.i10n.db.entity;

import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class StudentHistory implements IEntity<StudentHistory>{
	
	private LongPrimaryKey id;
	private long studentId;
	private long stopId;
	private long vehicleId;
	private long routeId;
	private Timestamp expectedTime;
	private Timestamp actualTime;
	private Timestamp smsSentTime;
	private int alertMin;
	private boolean isSMSSent;
	
	public StudentHistory(){
		
	}
	
	public StudentHistory(LongPrimaryKey id,long studentId,long stopId,long vehicleId,long routeId,int alertMin,
			Timestamp smsSentTime,Timestamp actualTime,Timestamp expectedTime,boolean smsSent){
		this.id = id;
		this.studentId = studentId;
		this.stopId = stopId;
		this.routeId = routeId;
		this.vehicleId = vehicleId;
		this.alertMin = alertMin;
		this.expectedTime = expectedTime;
		this.actualTime = actualTime;
		this.smsSentTime = smsSentTime;
		this.isSMSSent = smsSent;
	}
	
	public StudentHistory(long studentId,long stopId,long vehicleId,long routeId,int alertMin,
			Timestamp smsSentTime,Timestamp expectedTime){
		this.studentId = studentId;
		this.stopId = stopId;
		this.routeId = routeId;
		this.vehicleId = vehicleId;
		this.alertMin = alertMin;
		this.expectedTime = expectedTime;
		this.smsSentTime = smsSentTime;
	}
	
	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}
	
	public long getStudentId() {
		return studentId;
	}

	public void setStudentId(long studentId) {
		this.studentId = studentId;
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

	public Timestamp getSmsSentTime() {
		return smsSentTime;
	}

	public void setSmsSentTime(Timestamp smsSentTime) {
		this.smsSentTime = smsSentTime;
	}

	public int getAlertMin() {
		return alertMin;
	}

	public void setAlertMin(int alertMin) {
		this.alertMin = alertMin;
	}

	public boolean isSMSSent() {
		return isSMSSent;
	}

	public void setSMSSent(boolean isSMSSent) {
		this.isSMSSent = isSMSSent;
	}

	@Override
	public boolean equalsEntity(StudentHistory object) {
		// TODO Auto-generated method stub
		return false;
	}

}
