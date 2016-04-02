package com.i10n.db.entity;

import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class DriverHistory implements IEntity<DriverHistory>{

    private LongPrimaryKey driverid;
    private Timestamp updatedtime;
    private int driverstatus;
    private String updatedtime1;
  
	private Long updatedbyuser;
    
	public DriverHistory(LongPrimaryKey driverid,Timestamp t,int driverstatus,Long updatedbyuser)
	{
		super();
		this.driverid = driverid;
		this.updatedtime=t;
		this.driverstatus=driverstatus;
		this.updatedbyuser= updatedbyuser;
	} 
	public DriverHistory(LongPrimaryKey driverid,String updatedtime1,int driverstatus,Long updatedbyuser)
	{
		super();
		this.driverid = driverid;
		this.updatedtime1=updatedtime1;
		this.driverstatus=driverstatus;
		this.updatedbyuser= updatedbyuser;
	} 
	
	public LongPrimaryKey getDriverid() {
		return driverid;
	}
	public void setDriverid(LongPrimaryKey driverid) {
		this.driverid = driverid;
	}
	public Timestamp getUpdatedtime() {
		return updatedtime;
	}
	public void setUpdatedtime(Timestamp updatedtime) {
		this.updatedtime = updatedtime;
	}
	public int getDriverstatus() {
		return driverstatus;
	}
	public void setDriverstatus(int driverstatus) {
		this.driverstatus = driverstatus;
	}
	public String getUpdatedtime1() {
		return updatedtime1;
	}
	public void setUpdatedtime1(String updatedtime1) {
		this.updatedtime1 = updatedtime1;
	}
	public Long getUpdatedbyuser() {
		return updatedbyuser;
	}
	public void setUpdatedbyuser(Long updatedbyuser) {
		this.updatedbyuser = updatedbyuser;
	}
	@Override
	public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(getDriverid().getId()).append(" ").append(getUpdatedtime1()).append(" ").append(getDriverstatus()).append(" ").append(getUpdatedbyuser());
        return strBuilder.toString();
    }

    @Override
    public boolean equalsEntity(DriverHistory object) {
        return false;
    }   
}

