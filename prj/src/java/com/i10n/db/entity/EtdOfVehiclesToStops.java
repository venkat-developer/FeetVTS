package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;

public class EtdOfVehiclesToStops implements IEntity<EtdOfVehiclesToStops> {

	private long vehicleId;
	private long stopId;
	private int etdMinutes;
	
	
	public EtdOfVehiclesToStops(long vehicleId, long stopId, int etdMinutes){
		this.vehicleId = vehicleId;
		this.stopId = stopId;
		this.etdMinutes = etdMinutes;
	}
	
	public long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public long getStopId() {
		return stopId;
	}

	public void setStopId(long stopId) {
		this.stopId = stopId;
	}

	public int getEtdMinutes() {
		return etdMinutes;
	}

	public void setEtdMinutes(int etdMinutes) {
		this.etdMinutes = etdMinutes;
	}

	@Override
	public boolean equalsEntity(EtdOfVehiclesToStops object) {
		// TODO Auto-generated method stub
		return false;
	}

}
