package com.i10n.db.entity.primarykey;

import com.i10n.db.entity.IEntity.IPrimaryKey.IPrimaryKey;

/**
 * Represents the primary key for ACLVehicle
 * @author dharmaraju.v
 *
 */
public class ACLReportsPrimaryKey implements IPrimaryKey {
	
	private long vehicleId;
	
	private long reportId;

	public ACLReportsPrimaryKey(long vehicleId, long reportId) {
		super();
		this.vehicleId = vehicleId;
		this.reportId = reportId;
	}

	public long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public long getReportId() {
		return reportId;
	}

	public void setReportId(long reportId) {
		this.reportId = reportId;
	}
	
}
