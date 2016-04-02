package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class Students implements IEntity<Students>{

	private LongPrimaryKey id;
	private String studentName;
	private long stopId;
	private long routeId;
	private long mobileNo;
	private int alertMinutes;
	private long ownerid;
	
	public  Students(LongPrimaryKey id,String studentName,long stopId, long routeId, long mobileNo, int alertMinutes, long ownerid) {
		this.id = id;
		this.studentName = studentName;
		this.stopId = stopId;
		this.routeId = routeId;
		this.mobileNo = mobileNo;
		this.alertMinutes = alertMinutes;
		this.ownerid = ownerid;
	}
	
	
	public Students() {
		// TODO Auto-generated constructor stub
	}


	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public String getStudentName() {
		return studentName;
	}



	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public long getStopId() {
		return stopId;
	}



	public void setStopId(long stopId) {
		this.stopId = stopId;
	}

	public long getRouteId() {
		return routeId;
	}

	public void setRouteId(long routeId) {
		this.routeId = routeId;
	}

	public long getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(long mobileNo) {
		this.mobileNo = mobileNo;
	}

	public int getAlertMinutes() {
		return alertMinutes;
	}

	public void setAlertMinutes(int alertMinutes) {
		this.alertMinutes = alertMinutes;
	}

	@Override
	public boolean equalsEntity(Students object) {
		// TODO Auto-generated method stub
		return false;
	}

	public void setOwnerId(long ownerid) {
		this.ownerid = ownerid;
	}


	public long getOwnerId() {
		return ownerid;
	}

}
