package com.i10n.db.entity.primarykey;

import com.i10n.db.entity.IEntity.IPrimaryKey.IPrimaryKey;

/**
 * Represents the primary key for ACLVehicle
 * @author dharmaraju.v
 *
 */
public class ACLAlertsPrimaryKey implements IPrimaryKey {
	
	private long vehicleId;
	
	private long alertUserId;

	public ACLAlertsPrimaryKey(long vehicleId, long alertUserId) {
		super();
		this.vehicleId = vehicleId;
		this.alertUserId = alertUserId;
	}

	public long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public long getAlertUserId() {
		return alertUserId;
	}

	public void setAlertUserId(long alertUserId) {
		this.alertUserId = alertUserId;
	}
	
}
