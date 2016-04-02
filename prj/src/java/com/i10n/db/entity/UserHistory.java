package com.i10n.db.entity;

import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class UserHistory implements IEntity<UserHistory>{

    private LongPrimaryKey userid;
    private Timestamp updatedat;
    private int userstatus;
    private String updatedat1;
  
	public UserHistory(LongPrimaryKey userid,int userstatus,Timestamp t)
	{
		super();
		this.userid = userid;
		this.userstatus=userstatus;
		this.updatedat= t;
	} 
	public UserHistory(LongPrimaryKey userid,int userstatus,String t)
	{
		super();
		this.userid = userid;
		this.userstatus=userstatus;
		this.updatedat1= t;
	}
	
	public LongPrimaryKey getUserid() {
		return userid;
	}
	public void setUserid(LongPrimaryKey userid) {
		this.userid = userid;
	}
	public Timestamp getUpdatedat() {
		return updatedat;
	}
	public void setUpdatedat(Timestamp updatedat) {
		this.updatedat = updatedat;
	}
	public int getUserstatus() {
		return userstatus;
	}
	public void setUserstatus(int userstatus) {
		this.userstatus = userstatus;
	}
	public String getUpdatedat1() {
		return updatedat1;
	}
	public void setUpdatedat1(String updatedat1) {
		this.updatedat1 = updatedat1;
	}
	
	@Override
	public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(getUserid().getId()).append(" ").append(getUserstatus()).append(" ").append(getUpdatedat1());
        return strBuilder.toString();
    }

    @Override
    public boolean equalsEntity(UserHistory object) {
        return false;
    }   
}

