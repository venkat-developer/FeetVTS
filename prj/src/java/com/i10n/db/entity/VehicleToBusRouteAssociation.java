package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class VehicleToBusRouteAssociation  implements IEntity<VehicleToBusRouteAssociation>{
	private LongPrimaryKey id;
	private  String busroutenumber ;
	private	long  vehicleid ;
	private int lastcrossedstopsequence ;
	private int  currentshift;
	private int  initialdelayduration;

	public VehicleToBusRouteAssociation(LongPrimaryKey id, String busroutenumber, long vehicleid, int lastcrossedstopsequence,
			int currentshift, int initialdelayduration) {
		super();
		this.id = id;
		this.busroutenumber = busroutenumber;
		this.vehicleid = vehicleid;
		this.lastcrossedstopsequence = lastcrossedstopsequence;
		this.currentshift = currentshift;
		this.initialdelayduration = initialdelayduration;
	}
	public VehicleToBusRouteAssociation(Long long1, String string, long long2, int int1, int int2, int int3, int i) {
		// TODO Auto-generated constructor stub
	}
	public LongPrimaryKey getId() {
		return id;
	}
	public  void setId(LongPrimaryKey id) {
		this.id = id;
	}
	public String getBusroutenumber() {
		return busroutenumber;
	}
	public void setBusroutenumber(String busroutenumber) {
		this.busroutenumber = busroutenumber;
	}
	public long getVehicleid() {
		return vehicleid;
	}
	public void setVehicleid(long vehicleid) {
		this.vehicleid = vehicleid;
	}
	public int getLastcrossedstopsequence() {
		return lastcrossedstopsequence;
	}
	public void setLastcrossedstopsequence(int lastcrossedstopsequence) {
		this.lastcrossedstopsequence = lastcrossedstopsequence;
	}
	public int getCurrentshift() {
		return currentshift;
	}
	public void setCurrentshift(int currentshift) {
		this.currentshift = currentshift;
	}
	public int getInitialdelayduration() {
		return initialdelayduration;
	}
	public void setInitialdelayduration(int initialdelayduration) {
		this.initialdelayduration = initialdelayduration;
	}
	@Override
	public boolean equalsEntity(VehicleToBusRouteAssociation object) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(getId().getId()).append("-").append(getBusroutenumber()).append("-").append(getVehicleid()).append("-");
		strBuilder.append(getLastcrossedstopsequence());
		strBuilder.append(getCurrentshift());
		strBuilder.append(getInitialdelayduration());
		return strBuilder.toString();
	}
}




