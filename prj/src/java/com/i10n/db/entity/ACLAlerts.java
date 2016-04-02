package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.ACLAlertsPrimaryKey;

public class ACLAlerts implements IEntity<ACLAlerts>{

	private ACLAlertsPrimaryKey primaryKey;
	
	public ACLAlerts(long vehicleId, long alertUserId){
		primaryKey = new ACLAlertsPrimaryKey(vehicleId, alertUserId);
	}
	
	public long getVehicleId() {
		return primaryKey.getVehicleId();
	}

	public void setVehicleId(long vehicleId) {
		this.primaryKey.setVehicleId(vehicleId);
	}

	public long getalertUserId() {
		return this.primaryKey.getAlertUserId();
	}

	public void setalertUserId(long alertUserId) {
		this.primaryKey.setAlertUserId(alertUserId);
	}
	
	@Override
	public boolean equalsEntity(ACLAlerts object) {
		// TODO Auto-generated method stub
		return false;
	}

}
