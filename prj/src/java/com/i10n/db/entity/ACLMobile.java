package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.ACLMobilePrimaryKey;

public class ACLMobile implements IEntity<ACLMobile>{

	private ACLMobilePrimaryKey primaryKey;
	
	public ACLMobile(long vehicleId, long alertUserid){
		primaryKey = new ACLMobilePrimaryKey(vehicleId, alertUserid);
	}
	
	public long getVehicleid() {
		return primaryKey.getVehicleId();
	}

	public void setVehicleid(long vehicleId) {
		this.primaryKey.setVehicleId(vehicleId);
	}

	public long getMobileId() {
		return this.primaryKey.getMobileId();
	}

	public void setMobileId(long mobileId) {
		this.primaryKey.setMobileId(mobileId);
	}
	
	@Override
	public boolean equalsEntity(ACLMobile object) {
		// TODO Auto-generated method stub
		return false;
	}

}
