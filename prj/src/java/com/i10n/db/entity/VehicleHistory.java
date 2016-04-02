package com.i10n.db.entity;

import java.sql.Date;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class VehicleHistory implements IEntity<VehicleHistory>{

	private LongPrimaryKey vehicleid;
	private Date updatedtime;
	private String vehiclestatus;
	private String updatedbyuser;
	private String vehicleattended;
	private String imei;
	private float versionupdate;
	private String simid;
	private boolean battrychanged;
	private boolean fusechanged;
	private Date lastupdatedat;

	public VehicleHistory(LongPrimaryKey vehicleid,Date updatedtime,String vehiclestatus,String updatedbyuser)
	{
		super();
		this.vehicleid = vehicleid;
		this.updatedtime=updatedtime;
		this.vehiclestatus=vehiclestatus;
		this.updatedbyuser= updatedbyuser;
	} 

	public VehicleHistory(LongPrimaryKey vehicleid,String updatedtime1,String vehiclestatus,String updatedbyuser)
	{
		super();
		this.vehicleid = vehicleid;
		this.vehiclestatus=vehiclestatus;
		this.updatedbyuser= updatedbyuser;
	} 
	public VehicleHistory(LongPrimaryKey vehicleid, String vehicleattended,
			String imei, boolean battrychanged,
			boolean fusechanged, String updatedbyuser, Date updatedtime) {
		this.vehicleid = vehicleid;
		this.vehicleattended = vehicleattended;
		this.imei = imei;
		this.battrychanged = battrychanged;
		this.fusechanged = fusechanged;
		this.updatedtime = updatedtime;
		this.updatedbyuser = updatedbyuser;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(getVehicleid().getId()).append(" ").append(getUpdatedtime()).append(" ").
		append(getVehiclestatus()).append(" ").append(getUpdatedbyuser());
		return strBuilder.toString();
	}

	public LongPrimaryKey getVehicleid() {
		return vehicleid;
	}
	public void setVehicleid(LongPrimaryKey vehicleid) {
		this.vehicleid = vehicleid;
	}
	public Date getUpdatedtime() {
		return updatedtime;
	}
	public void setUpdatedtime(Date updatedtime) {
		this.updatedtime = updatedtime;
	}
	public String getVehiclestatus() {
		return vehiclestatus;
	}
	public void setVehiclestatus(String vehiclestatus) {
		this.vehiclestatus = vehiclestatus;
	}
	public String getUpdatedbyuser() {
		return updatedbyuser;
	}
	public void setUpdatedbyuser(String updatedbyuser) {
		this.updatedbyuser = updatedbyuser;
	} 
	
	public String getVehicleattended() {
		return vehicleattended;
	}
	public void setVehicleattended(String vehicleattended) {
		this.vehicleattended = vehicleattended;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public float getVersionupdate() {
		return versionupdate;
	}
	public void setVersionupdate(float versionupdate) {
		this.versionupdate = versionupdate;
	}
	public String getSimid() {
		return simid;
	}
	public void setSimid(String simid) {
		this.simid = simid;
	}
	public boolean isBattrychanged() {
		return battrychanged;
	}
	public void setBattrychanged(boolean battrychanged) {
		this.battrychanged = battrychanged;
	}
	public Date getLastupdatedat() {
		return lastupdatedat;
	}
	public void setLastupdatedat(Date lastupdatedat) {
		this.lastupdatedat = lastupdatedat;
	}
	public boolean isFusechanged() {
		return fusechanged;
	}
	public void setFusechanged(boolean fusechanged) {
		this.fusechanged = fusechanged;
	}   
	@Override
	public boolean equalsEntity(VehicleHistory object) {
		return false;
	}   
}

