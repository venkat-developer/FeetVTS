package com.i10n.db.entity;

import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;


public class DigitalEventOccur implements IEntity< DigitalEventOccur> {
   
	

private	LongPrimaryKey tripid ;
private Long eventid ;
private Timestamp truedate;
private Timestamp falsedate;
private String truedate1;
private String falsedate1;


public DigitalEventOccur(LongPrimaryKey tripid, Long eventid,
		Timestamp truedate, Timestamp falsedate) {
	super();
	this.tripid = tripid;
	this.eventid = eventid;
	this.truedate = truedate;
	this.falsedate = falsedate;
}

public DigitalEventOccur(LongPrimaryKey tripid, Long eventid, String truedate1,
		String falsedate1) {
	super();
	this.tripid = tripid;
	this.eventid = eventid;
	this.truedate1 = truedate1;
	this.falsedate1 = falsedate1;
}

@Override
public boolean equalsEntity(DigitalEventOccur object) {
    // TODO Auto-generated method stub
    return false;
}
    public LongPrimaryKey getTripid() {
	return tripid;
}

public void setTripid(LongPrimaryKey tripid) {
	this.tripid = tripid;
}

public Long getEventid() {
	return eventid;
}

public void setEventid(Long eventid) {
	this.eventid = eventid;
}

public Timestamp getTruedate() {
	return truedate;
}

public void setTruedate(Timestamp truedate) {
	this.truedate = truedate;
}

public Timestamp getFalsedate() {
	return falsedate;
}

public void setFalsedate(Timestamp falsedate) {
	this.falsedate = falsedate;
}

public String getTruedate1() {
	return truedate1;
}

public void setTruedate1(String truedate1) {
	this.truedate1 = truedate1;
}

public String getFalsedate1() {
	return falsedate1;
}

public void setFalsedate1(String falsedate1) {
	this.falsedate1 = falsedate1;
}

	@Override
    public String toString(){
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(getTripid().getId()).append("-").append(getEventid()).append("-").append(getTruedate1()).append("-").append(getFalsedate1());
        
        return strBuilder.toString();
    }
	



}
