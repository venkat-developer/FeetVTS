package com.i10n.db.entity;

import java.util.Date;

import org.postgis.Geometry;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class IdlePoints implements IEntity<IdlePoints>{

	@Override
	public String toString() {
		return "IdlePoints [endtime=" + endtime + ", id=" + id
				+ ", idleLocation=" + idleLocation + ", locationName="
				+ locationName + ", rowCreated=" + rowCreated + ", starttime="
				+ starttime + ", tripid=" + tripid + "]";
	}

	public IdlePoints() {
		// TODO Auto-generated constructor stub
	}

	LongPrimaryKey id;
	
	private long tripid;
	
	private Geometry idleLocation;
	
	private Date starttime;
	
	private Date endtime;
	
	private String locationName;
	
	private boolean rowCreated;
	
	
	
	

	public boolean isRowCreated() {
		return rowCreated;
	}

	public void setRowCreated(boolean rowCreated) {
		this.rowCreated = rowCreated;
	}

	public IdlePoints(LongPrimaryKey id, long tripid, Geometry idleLocation,
			Date starttime, Date endtime, String locationName) {
		super();
		this.id = id;
		this.tripid = tripid;
		this.idleLocation = idleLocation;
		this.starttime = starttime;
		this.endtime = endtime;
		this.locationName = locationName;
	}

	@Override
	public boolean equalsEntity(IdlePoints object) {
		// TODO Auto-generated method stub
		return false;
	}

	public long getTripid() {
		return tripid;
	}

	public void setTripid(long tripid) {
		this.tripid = tripid;
	}

	public Geometry getIdleLocation() {
		return idleLocation;
	}

	public void setIdleLocation(Geometry idleLocation) {
		this.idleLocation = idleLocation;
	}

	public Date getStarttime() {
		return starttime;
	}

	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}

	public Date getEndtime() {
		return endtime;
	}

	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}

	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

}