package com.i10n.db.entity;

import java.util.Date;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

/**
 * 
 * @author vijaybharath
 *
 */

public class HardwareModule implements IEntity<HardwareModule> {
	
	private LongPrimaryKey id;
	private String imei;
	private float moduleVersion;
	private Date createdAt;
	private Date statusLastUpdatedAt;
	private int status;
	private Long lastUpdatedEmpIp;
	private float firmwareversion;
	private boolean deleted;
	private Long ownerId;
	private String mobileNumber;
	private String simId;
	private String simProvider;
	
	
	public HardwareModule(String imei, float moduleVersion, Date createdAt, Date statusLastUpdatesAt,
			int status, Long lastUpdatedEmpId,float firmwareversion,String mobileNumber,String simId,String simProvider){
		this.imei = imei;
		this.moduleVersion = moduleVersion;
		this.createdAt = createdAt;
		this.statusLastUpdatedAt = statusLastUpdatesAt;
		this.status = status;
		this.lastUpdatedEmpIp = lastUpdatedEmpId;
		this.firmwareversion=firmwareversion;
		this.mobileNumber = mobileNumber;
		this.simId = simId;
		this.simProvider = simProvider;
	}
	
	public HardwareModule(long id, String imei, float moduleVersion, Date createdAt, 
			Date statusLastUpdatesAt, int status, Long lastUpdatedEmpId,float firmwareversion,boolean deleted,Long ownerId, String mobileNumber,String simId,String simProvider){
		this.id = new LongPrimaryKey(id);
		this.imei = imei;
		this.moduleVersion = moduleVersion;
		this.createdAt = createdAt;
		this.statusLastUpdatedAt = statusLastUpdatesAt;
		this.status = status;
		this.lastUpdatedEmpIp = lastUpdatedEmpId;
		this.firmwareversion=firmwareversion;
		this.deleted  = deleted;
		this.ownerId = ownerId;
		this.mobileNumber = mobileNumber;
		this.simId = simId;
		this.simProvider = simProvider;
	}
	
	public HardwareModule(long id, String imei, float moduleVersion, Date createdAt, 
			Date statusLastUpdatesAt, int status, Long lastUpdatedEmpId,float firmwareversion){
		this.id = new LongPrimaryKey(id);
		this.imei = imei;
		this.moduleVersion = moduleVersion;
		this.createdAt = createdAt;
		this.statusLastUpdatedAt = statusLastUpdatesAt;
		this.status = status;
		this.lastUpdatedEmpIp = lastUpdatedEmpId;
		this.firmwareversion=firmwareversion;
	}
	public HardwareModule(long id, String imei, float moduleVersion, Date createdAt, 
			Date statusLastUpdatesAt, int status, Long lastUpdatedEmpId,float firmwareversion,String mobileNumber,String simId,String simProvider){
		this.id = new LongPrimaryKey(id);
		this.imei = imei;
		this.moduleVersion = moduleVersion;
		this.createdAt = createdAt;
		this.statusLastUpdatedAt = statusLastUpdatesAt;
		this.status = status;
		this.lastUpdatedEmpIp = lastUpdatedEmpId;
		this.firmwareversion=firmwareversion;
		this.mobileNumber=mobileNumber;
		this.simId=simId;
		this.simProvider = simProvider;
	}
	

	@Override
	public boolean equalsEntity(HardwareModule object) {
		// TODO Auto-generated method stub
		return false;
	}

	public float getFirmwareversion() {
		return firmwareversion;
	}

	public void setFirmwareversion(float firmwareversion) {
		this.firmwareversion = firmwareversion;
	}

	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public long getLastUpdatedEmpIp() {
		return lastUpdatedEmpIp;
	}

	public void setLastUpdatedEmpIp(long lastUpdatedEmpIp) {
		this.lastUpdatedEmpIp = lastUpdatedEmpIp;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public float getModuleVersion() {
		return moduleVersion;
	}

	public void setModuleVersion(float moduleVersion) {
		this.moduleVersion = moduleVersion;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getStatusLastUpdatedAt() {
		return statusLastUpdatedAt;
	}

	public void setStatusLastUpdatedAt(Date statusLastUpdatedAt) {
		this.statusLastUpdatedAt = statusLastUpdatedAt;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public boolean getDeletedStatus() {
		return deleted;
	}
	
	public void setDeletedStatus(boolean deleted) {
		this.deleted = deleted;
	}
	
	public Long getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId (Long ownerId) {
		this.ownerId= ownerId;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	public String getSimId(){
		return simId;
	}
	public void setSimId(String simId){
		this.simId = simId;
	}
	
	public String getSimProvider(){
		return simProvider;
	}
	
	public void setSimProvider(String simProvider){
		this.simProvider = simProvider;
	}
	public String toString() {
		StringBuffer stb=new StringBuffer();
		stb.append("Id "+this.id.getId());
		stb.append("IMEI : "+this.imei);
		stb.append("Mobile Number : "+this.mobileNumber);
		stb.append("SIM id : "+this.simId);
		return stb.toString();
	}
}
