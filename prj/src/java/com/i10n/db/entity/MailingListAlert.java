package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class MailingListAlert implements IEntity<MailingListAlert> {

	private LongPrimaryKey id;
	
	private String name;

	private Long userid;

	private String mailid;

	private boolean overspeeding;

	private boolean geofencing;

	private boolean chargerdisconnected;
	
	private boolean ignition;

	public MailingListAlert(Long id, String name, Long userid, String mailid,
			boolean overspeeding, boolean geofencing,
			boolean chargerdisconnected, boolean ignition) {

		super();
		this.id = new LongPrimaryKey(id);
		this.name = name;
		this.userid = userid;
		this.mailid = mailid;
		this.overspeeding = overspeeding;
		this.geofencing = geofencing;
		this.chargerdisconnected = chargerdisconnected;
		this.ignition=ignition;

		}
	
	public MailingListAlert(String name, Long userid, String mailid,
			boolean overspeeding, boolean geofencing,
			boolean chargerdisconnected,boolean ignition) {

		super();
		this.name = name;
		this.userid = userid;
		this.mailid = mailid;
		this.overspeeding = overspeeding;
		this.geofencing = geofencing;
		this.chargerdisconnected = chargerdisconnected;
		this.ignition=ignition;
		}

	public MailingListAlert(String name2, Long userid2, String email,
			boolean parseBoolean, boolean parseBoolean2, boolean parseBoolean3) {
		// TODO Auto-generated constructor stub
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
	
	public String getMailId(){
		return mailid;
	}
	
	public void setMailId(String mailid){
		this.mailid = mailid;
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
	public boolean isIgnition() {
		return ignition;
	}

	public void setIgnition(boolean ignition) {
		this.ignition = ignition;
	}

	@Override
	public boolean equalsEntity(MailingListAlert object) {
		// TODO Auto-generated method stub
		return false;
	}

}
