package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class MobileNumber implements IEntity<MobileNumber> {

	private LongPrimaryKey id;
	
	private String name;

	private Long userid;

	private Long mobilenumber;

	private boolean overspeeding;

	private boolean geofencing;

	private boolean chargerdisconnected;
	
	private boolean tncsc;

	public MobileNumber(Long id, String name, Long userid, Long mobilenumber,
			boolean overspeeding, boolean geofencing, boolean chargerdisconnected) {

		super();
		this.id = new LongPrimaryKey(id);
		this.name = name;
		this.userid = userid;
		this.mobilenumber = mobilenumber;
		this.overspeeding = overspeeding;
		this.geofencing = geofencing;
		this.chargerdisconnected = chargerdisconnected;
		}
	
	public MobileNumber(String name, Long userid, Long mobilenumber,
			boolean overspeeding, boolean geofencing,
			boolean chargerdisconnected) {

		super();
		
		this.name = name;
		this.userid = userid;
		this.mobilenumber = mobilenumber;
		this.overspeeding = overspeeding;
		this.geofencing = geofencing;
		this.chargerdisconnected = chargerdisconnected;
		}

	public LongPrimaryKey getId(){
		return id;
	}
	
	public void setId(LongPrimaryKey id){
		this.id = id;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public Long getUserid(){
		return userid;
	}
	
	public void setUserId(Long userid){
		this.userid = userid;
	}
	
	public Long getMobileNumber(){
		return mobilenumber;
	}
	
	public void setMobileNumber(Long mobilenumber){
		this.mobilenumber = mobilenumber;
	}
	
	public boolean getOverSpeeding(){
		return overspeeding;
	}
	
	public void setOverSpeeding(boolean overspeeding){
		this.overspeeding = overspeeding;
	}
	
	public boolean getGeoFencing(){
		return geofencing;
	}
	
	public void setGeoFencing(boolean geofencing){
		this.geofencing = geofencing;
	}
	
	public boolean getChargerDisConnected(){
		return chargerdisconnected;
	}
	
	public void setChargerDisConnected(boolean chargerdisconnected){
		this.chargerdisconnected = chargerdisconnected;
	}
	
	public boolean isTncsc() {
		return tncsc;
	}

	public void setTncsc(boolean tncsc) {
		this.tncsc = tncsc;
	}

	@Override
	public boolean equalsEntity(MobileNumber object) {
		// TODO Auto-generated method stub
		return false;
	}

}
