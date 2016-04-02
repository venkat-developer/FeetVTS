package com.i10n.db.entity;

import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class MailingListReport implements IEntity<MailingListReport> {

	private LongPrimaryKey id;
	
	private String name;

	private Long userId;

	private String mailId;

	/**
	 * Value 	Type of mail subscription
	 * 
	 * 0		Daily
	 * 1		Weekly
	 * 2		Monthly 
	 */
	private int schedule;

	private Timestamp lastScheduledAt;

	private boolean vehicleStatistics;

	private boolean vehicleStatus;

	private boolean offlineVehicleReport;

	public MailingListReport(Long id, String name, Long userId, String mailId,
			int schedule, Timestamp lastScheduledAt,
			boolean vehicleStatistics, boolean vehicleStatus,
			boolean offlineVehicleReport) {

		super();
		this.id = new LongPrimaryKey(id);
		this.name = name;
		this.userId = userId;
		this.mailId = mailId;
		this.schedule = schedule;
		this.lastScheduledAt = lastScheduledAt;
		this.vehicleStatistics = vehicleStatistics;
		this.vehicleStatus = vehicleStatus;
		this.offlineVehicleReport = offlineVehicleReport;
	}

	public MailingListReport(String name, Long userid, String mailId,
			int schedule, Timestamp lastScheduledAt, boolean vehicleStatistics,
			boolean vehicleStatus, boolean offlineVehicleReport) {
		
		super();
		this.name = name;
		this.userId = userid;
		this.mailId = mailId;
		this.schedule = schedule;
		this.lastScheduledAt = lastScheduledAt;
		this.vehicleStatistics = vehicleStatistics;
		this.vehicleStatus = vehicleStatus;
		this.offlineVehicleReport = offlineVehicleReport;
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
	
	public Long getUserId(){
		return userId;
	}
	
	public void setUserId(Long userid){
		this.userId = userid;
	}
	
	public String getMailId(){
		return mailId;
	}
	
	public void setMailId(String mailId){
		this.mailId = mailId;
	}
	
	public int getSchedule(){
		return schedule;
	}
	
	public void setSchedule(int schedule){
		this.schedule = schedule; 
	}
	
	public Timestamp getLastScheduledAt(){
		return lastScheduledAt;
	}
	
	public void setLastScheduledAt(Timestamp lastScheduledAt){
		this.lastScheduledAt = lastScheduledAt;
	}
	
	public boolean getVehicleStatistics(){
		return vehicleStatistics;
	}
	
	public void setVehicleStatistics(boolean vehicleStatistics){
		this.vehicleStatistics = vehicleStatistics;
	}
	
	public boolean getVehicleStatus(){
		return vehicleStatus;
	}
	
	public void setVehicleStatus(boolean vehicleStatus){
		this.vehicleStatus = vehicleStatus;
	}
	
	public boolean getOfflineVehicleReport(){
		return offlineVehicleReport;
	}
	
	public void setOfflineVehicleReport(boolean offlineVehicleReport){
		this.offlineVehicleReport = offlineVehicleReport;
	}
	@Override
	public boolean equalsEntity(MailingListReport object) {
		// TODO Auto-generated method stub
		return false;
	}

}
