package com.i10n.db.entity;

import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class DriverCreationAndStatusInfo implements IEntity<DriverCreationAndStatusInfo> {
	
	private LongPrimaryKey driverid;
	private Long lastupdateduserid;
	private Long createduserid;
	private int status;
	private String createdat1;
	private Timestamp createdat;
	private Long currentownerid;
	
	public DriverCreationAndStatusInfo(LongPrimaryKey driverid,Timestamp createdat,Long lastupdateduserid,int status,Long createduserid,Long currentownerid)
	{ 
		super();
		this.driverid = driverid;
		this.createdat = createdat;
		this.lastupdateduserid = lastupdateduserid;
		this.status = status;
		this.createduserid=createduserid;
		this.createduserid=createduserid;
		this.currentownerid=currentownerid;
	}
	public DriverCreationAndStatusInfo(LongPrimaryKey driverid,String createdat1,Long lastupdateduserid,int status,Long createduserid,Long currentownerid)
	{ 
		super();
		this.driverid = driverid;
		this.createdat1 = createdat1;
		this.lastupdateduserid = lastupdateduserid;
		this.status = status;
		this.createduserid=createduserid;
		this.currentownerid=currentownerid;
	}
	
	public LongPrimaryKey getDriverid() {
		return driverid;
	}
	public void setDriverid(LongPrimaryKey driverid) {
		this.driverid = driverid;
	}
	public Long getLastupdateduserid() {
		return lastupdateduserid;
	}
	public void setLastupdateduserid(Long lastupdateduserid) {
		this.lastupdateduserid = lastupdateduserid;
	}
	public Long getCreateduserid() {
		return createduserid;
	}
	public void setCreateduserid(Long createduserid) {
		this.createduserid = createduserid;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getCreatedat1() {
		return createdat1;
	}
	public void setCreatedat1(String createdat1) {
		this.createdat1 = createdat1;
	}
	public Timestamp getCreatedat() {
		return createdat;
	}
	public void setCreatedat(Timestamp createdat) {
		this.createdat = createdat;
	}
	public Long getCurrentownerid() {
		return currentownerid;
	}
	public void setCurrentownerid(Long currentownerid) {
		this.currentownerid = currentownerid;
	}
	@Override
	public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(getDriverid().getId()).append(" ").append(getCreatedat1()).append(" ").append(getLastupdateduserid()).append(" ").append(getStatus()).append(" ").append(getCreateduserid()).append(" ").append(getCurrentownerid());
        return strBuilder.toString();
    }
    @Override
    public boolean equalsEntity(DriverCreationAndStatusInfo object) {
        // TODO Auto-generated method stub
        return false;
    }
}

