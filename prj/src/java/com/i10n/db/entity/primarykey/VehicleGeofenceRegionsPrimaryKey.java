package com.i10n.db.entity.primarykey;

import com.i10n.db.entity.IEntity.IPrimaryKey.IPrimaryKey;

public class VehicleGeofenceRegionsPrimaryKey implements IPrimaryKey{
	private long vehicleid;
    private long regionid;

	public VehicleGeofenceRegionsPrimaryKey(long vehicleid, long regionid) {
		super();
		this.vehicleid = vehicleid;
		this.regionid = regionid;
	}

	public long getVehicleid() {
		return vehicleid;
	}

	public void setVehicleid(long vehicleid) {
		this.vehicleid = vehicleid;
	}

	public long getRegionId() {
		return regionid;
	}

	public void setRegionId(long regionid) {
		this.regionid = regionid;
	}
}
