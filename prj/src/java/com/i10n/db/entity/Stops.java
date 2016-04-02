package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class Stops implements IEntity<Stops>{

	private LongPrimaryKey id;
	private String stopName;
	private String knowAs;
	private double latPoint;
	private double lonPoint;
	private long ownerid;
	
	public Stops(LongPrimaryKey id,String stopName, String knowAs, double latPoint, double lonPoint, long ownerid){
		this.id = id;
		this.stopName = stopName;
		this.knowAs = knowAs;
		this.latPoint = latPoint;
		this.lonPoint = lonPoint;
		this.ownerid = ownerid;
	}
	
	public Stops() {
		// TODO Auto-generated constructor stub
	}

	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public String getStopName() {
		return stopName;
	}

	public void setStopName(String stopName) {
		this.stopName = stopName;
	}

	public String getKnowAs() {
		return knowAs;
	}

	public void setKnowAs(String knowAs) {
		this.knowAs = knowAs;
	}

	public double getLatPoint() {
		return latPoint;
	}

	public void setLatPoint(double latPoint) {
		this.latPoint = latPoint;
	}

	public double getLonPoint() {
		return lonPoint;
	}

	public void setLonPoint(double lonPoint) {
		this.lonPoint = lonPoint;
	}

	@Override
	public boolean equalsEntity(Stops object) {
		if(object.getId().getId() == id.getId()){
			return true;
		}
		return false;
	}

	public void setOwnerId(long ownerid) {
		this.ownerid = ownerid;
	}

	public long getOwnerId() {
		return ownerid;
	}
	
	public String toString(){
		StringBuffer returnString = new StringBuffer();
		returnString.append("StopId = ");
		returnString.append(id.getId());
		returnString.append(", StopName = ");
		returnString.append(stopName);
		
		return returnString.toString();
	}

}
