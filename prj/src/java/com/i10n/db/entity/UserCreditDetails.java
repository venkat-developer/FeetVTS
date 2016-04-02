package com.i10n.db.entity;

import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class UserCreditDetails implements IEntity<UserCreditDetails>{

   private LongPrimaryKey userid;
   private int totalcreditpurchased;
   private int creditused;
   private Timestamp lastcreditpurchasedate;
   private Long lastupdatedbyemp;
   private String lastcreditpurchasedate1;
   private Long updatedbyemp;
    
	public UserCreditDetails(LongPrimaryKey userid,int totalcreditpurchased,int creditused,Timestamp lastcreditpurchasedate,Long lastupdatedbyemp)
	{
		super();
		this.userid = userid;
		this.totalcreditpurchased=totalcreditpurchased;
		this.lastcreditpurchasedate=lastcreditpurchasedate;
		this.lastupdatedbyemp= lastupdatedbyemp;
	} 
	public UserCreditDetails(LongPrimaryKey userid,int totalcreditpurchased,int creditused,String lastcreditpurchasedate,Long lastupdatedbyemp)
	{
		super();
		this.userid = userid;
		this.totalcreditpurchased=totalcreditpurchased;
		this.lastcreditpurchasedate1=lastcreditpurchasedate;
		this.lastupdatedbyemp= lastupdatedbyemp;
	} 
	
	public LongPrimaryKey getUserid() {
		return userid;
	}
	public void setUserid(LongPrimaryKey userid) {
		this.userid = userid;
	}
	public int getTotalcreditpurchased() {
		return totalcreditpurchased;
	}
	public void setTotalcreditpurchased(int totalcreditpurchased) {
		this.totalcreditpurchased = totalcreditpurchased;
	}
	public Timestamp getLastcreditpurchasedate() {
		return lastcreditpurchasedate;
	}
	
	public int getCreditused() {
		return creditused;
	}
	public void setCreditused(int creditused) {
		this.creditused = creditused;
	}
	public void setLastcreditpurchasedate(Timestamp lastcreditpurchasedate) {
		this.lastcreditpurchasedate = lastcreditpurchasedate;
	}
	public Long getLastupdatedbyemp() {
		return lastupdatedbyemp;
	}
	public void setLastupdatedbyemp(Long lastupdatedbyemp) {
		this.lastupdatedbyemp = lastupdatedbyemp;
	}
	public String getLastcreditpurchasedate1() {
		return lastcreditpurchasedate1;
	}
	public void setLastcreditpurchasedate1(String lastcreditpurchasedate1) {
		this.lastcreditpurchasedate1 = lastcreditpurchasedate1;
	}
	public Long getUpdatedbyemp() {
		return updatedbyemp;
	}
	public void setUpdatedbyemp(Long updatedbyemp) {
		this.updatedbyemp = updatedbyemp;
	}
	
	@Override
	public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(getUserid().getId()).append(" ").append(getTotalcreditpurchased()).append(" ").append(getCreditused()).append(" ").append(getLastcreditpurchasedate1()).append(" ").append(getLastupdatedbyemp());
        return strBuilder.toString();
    }

    @Override
    public boolean equalsEntity(UserCreditDetails object) {
        return false;
    }   
}

