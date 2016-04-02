package com.i10n.db.entity;

import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class VehicleCreationAndStatusInfo  implements IEntity<VehicleCreationAndStatusInfo>  {
	
private LongPrimaryKey vehicleid;

private Timestamp createdat;

private Long   lastupdateduserid;
private int status;
private Long  createduserid;
private Long currentownerid;
private String createdat1;
public VehicleCreationAndStatusInfo(LongPrimaryKey vehicleid,
		Timestamp createdat, Long lastupdateduserid, int status,
		Long createduserid, Long currentownerid) {
	super();
	this.vehicleid = vehicleid;
	this.createdat = createdat;
	this.lastupdateduserid = lastupdateduserid;
	this.status = status;
	this.createduserid = createduserid;
	this.currentownerid = currentownerid;
}
public VehicleCreationAndStatusInfo(LongPrimaryKey vehicleid,
		Long lastupdateduserid, int status, Long createduserid,
		Long currentownerid, String createdat1) {
	super();
	this.vehicleid = vehicleid;
	this.lastupdateduserid = lastupdateduserid;
	this.status = status;
	this.createduserid = createduserid;
	this.currentownerid = currentownerid;
	this.createdat1 = createdat1;
}
public VehicleCreationAndStatusInfo(LongPrimaryKey vehicleid2, String string,
		long long1, int int1, long long2, long long3) {
	super();
	this.vehicleid = vehicleid2;
	this.lastupdateduserid =long1;
	this.status = int1;
	this.createduserid = long2;
	this.currentownerid =long3;
	this.createdat1 =string;

}
public LongPrimaryKey getVehicleid() {
	return vehicleid;
}
public void setVehicleid(LongPrimaryKey vehicleid) {
	this.vehicleid = vehicleid;
}
public Timestamp getCreatedat() {
	return createdat;
}
public void setCreatedat(Timestamp createdat) {
	this.createdat = createdat;
}
public Long getLastupdateduserid() {
	return lastupdateduserid;
}
public void setLastupdateduserid(Long lastupdateduserid) {
	this.lastupdateduserid = lastupdateduserid;
}
public int getStatus() {
	return status;
}
public void setStatus(int status) {
	this.status = status;
}
public Long getCreateduserid() {
	return createduserid;
}
public void setCreateduserid(Long createduserid) {
	this.createduserid = createduserid;
}
public Long getCurrentownerid() {
	return currentownerid;
}
public void setCurrentownerid(Long currentownerid) {
	this.currentownerid = currentownerid;
}
public String getCreatedat1() {
	return createdat1;
}
public void setCreatedat1(String createdat1) {
	this.createdat1 = createdat1;
}

@Override
public boolean equalsEntity(VehicleCreationAndStatusInfo object) {
// TODO Auto-generated method stub
return false;

}



@Override
public String toString(){
    StringBuilder strBuilder = new StringBuilder();
    strBuilder.append(getVehicleid().getId()).append("-").append(getCreatedat1()).append("-").append(getLastupdateduserid()).append("-").append(getStatus()).append("-").append(getCreateduserid()).append("-").append(getCurrentownerid());
  return strBuilder.toString();
}




	}
