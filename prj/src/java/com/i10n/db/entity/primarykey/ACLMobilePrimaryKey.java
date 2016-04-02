package com.i10n.db.entity.primarykey;

import com.i10n.db.entity.IEntity.IPrimaryKey.IPrimaryKey;

/**
 * Represents the primary key for ACLVehicle
 * @author dharmaraju.v
 *
 */
public class ACLMobilePrimaryKey implements IPrimaryKey {
	
	private long vehicleId;
	
	private long mobileId;

	public ACLMobilePrimaryKey(long vehicleId, long mobileId) {
		super();
		this.vehicleId = vehicleId;
		this.mobileId = mobileId;
	}

	public long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public long getMobileId() {
		return mobileId;
	}

	public void setMobileId(long mobileId) {
		this.mobileId = mobileId;
	}
	
}
