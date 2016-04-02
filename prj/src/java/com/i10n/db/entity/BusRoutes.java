package com.i10n.db.entity;

import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class BusRoutes implements IEntity<BusRoutes> {
	
	private LongPrimaryKey id;
	private Long busstopid;
	private int busstopsequence;
	private int busroutenumber;
	private Timestamp expectedtime;
	private int shiftnumber;
	private String expectedtime1; 
	public BusRoutes(LongPrimaryKey id,Long busstopid,int busstopsequence,int busroutenumber,Timestamp timestamp,int shiftnumber)
	{ 
		super();
		this.id = id;
		this.busstopid = busstopid;
		this.busstopsequence = busstopsequence;
		this.busroutenumber = busroutenumber;
		this.expectedtime=timestamp;
		this.shiftnumber=shiftnumber;
	}
	public BusRoutes(LongPrimaryKey id,Long busstopid,int busstopsequence,int busroutenumber,String expectedtime,int shiftnumber)
	{
		super();
		this.id = id;
		this.busstopid = busstopid;
		this.busstopsequence = busstopsequence;
		this.busroutenumber = busroutenumber;
		this.expectedtime1=expectedtime;
		this.shiftnumber=shiftnumber;
	}
	
	public LongPrimaryKey getId() {
		return id;
	}
	public void setId(LongPrimaryKey id) {
		this.id = id;
	}
	public Long getBusstopid() {
		return busstopid;
	}
	public void setBusstopid(Long busstopid) {
		this.busstopid = busstopid;
	}
	public int getBusstopsequence() {
		return busstopsequence;
	}
	public void setBusstopsequence(int busstopsequence) {
		this.busstopsequence = busstopsequence;
	}
	public int getBusroutenumber() {
		return busroutenumber;
	}
	public void setBusroutenumber(int busroutenumber) {
		this.busroutenumber = busroutenumber;
	}
	public Timestamp getExpectedtime() {
		return expectedtime;
	}
	public void setExpectedtime(Timestamp expectedtime) {
		this.expectedtime = expectedtime;
	}
	public int getShiftnumber() {
		return shiftnumber;
	}
	public void setShiftnumber(int shiftnumber) {
		this.shiftnumber = shiftnumber;
	}
	public String getExpectedtime1() {
		return expectedtime1;
	}
	public void setExpectedtime1(String expectedtime1) {
		this.expectedtime1 = expectedtime1;
	}
	@Override
	public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(getId().getId()).append(" ").append(getBusstopid()).append(" ").append(getBusstopsequence()).append(" ").append(getBusroutenumber()).append(" ").append(getExpectedtime1()).append(" ").append(getShiftnumber());
        return strBuilder.toString();
    }
    @Override
    public boolean equalsEntity(BusRoutes object) {
        // TODO Auto-generated method stub
        return false;
    }
}

