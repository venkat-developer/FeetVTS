package com.i10n.db.entity;

import java.sql.Timestamp;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class HardwareModuleHistory implements IEntity<HardwareModuleHistory>{

    private LongPrimaryKey hardwareid;
    private Timestamp updatedat;
    private int modulestatus;
    private String updatedat1;
    private Long updatedbyemp;
    
	public HardwareModuleHistory(LongPrimaryKey hardwareid,Timestamp updatedat,int status,Long updatedbyemp)
	{
		super();
		this.hardwareid = hardwareid;
		this.updatedat=updatedat;
		this.modulestatus=status;
		this.updatedbyemp= updatedbyemp;
	} 
	public HardwareModuleHistory(LongPrimaryKey hardwareid,String updatedat1,int status,Long updatedbyemp)
	{
		super();
		this.hardwareid = hardwareid;
		this.updatedat1=updatedat1;
		this.modulestatus=status;
		this.updatedbyemp= updatedbyemp;
	} 
	
	
	public LongPrimaryKey getHardwareid() {
		return hardwareid;
	}
	public void setHardwareid(LongPrimaryKey hardwareid) {
		this.hardwareid = hardwareid;
	}
	public Timestamp getUpdatedat() {
		return updatedat;
	}
	public void setUpdatedat(Timestamp updatedat) {
		this.updatedat = updatedat;
	}
	
	public int getModulestatus() {
		return modulestatus;
	}
	public void setModulestatus(int modulestatus) {
		this.modulestatus = modulestatus;
	}
	public String getUpdatedat1() {
		return updatedat1;
	}
	public void setUpdatedat1(String updatedat1) {
		this.updatedat1 = updatedat1;
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
        strBuilder.append(getHardwareid().getId()).append(" ").append(getUpdatedat1()).append(" ").append(getModulestatus()).append(" ").append(getUpdatedbyemp());
        return strBuilder.toString();
    }

    @Override
    public boolean equalsEntity(HardwareModuleHistory object) {
        return false;
    }   
}

