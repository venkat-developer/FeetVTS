package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.ACLReportsPrimaryKey;

public class ACLReports implements IEntity<ACLReports>{

	private ACLReportsPrimaryKey primaryKey;
	
	public ACLReports(long vehicleId, long reportId){
		primaryKey = new ACLReportsPrimaryKey(vehicleId, reportId);
	}
	
	public long getVehicleid() {
		return primaryKey.getVehicleId();
	}

	public void setVehicleid(long vehicleId) {
		this.primaryKey.setVehicleId(vehicleId);
	}

	public long getReportId() {
		return this.primaryKey.getReportId();
	}

	public void setReportId(long reportId) {
		this.primaryKey.setReportId(reportId);
	}
	
	@Override
	public boolean equalsEntity(ACLReports object) {
		// TODO Auto-generated method stub
		return false;
	}

}
