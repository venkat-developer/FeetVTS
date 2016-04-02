package com.i10n.db.idao;

import java.util.List;

import com.i10n.db.entity.VehicleGeofenceRegions;
import com.i10n.db.entity.primarykey.VehicleGeofenceRegionsPrimaryKey;

public interface IVehicleGeofenceRegionsDAO extends IDAO<VehicleGeofenceRegions, VehicleGeofenceRegionsPrimaryKey>{
	List<VehicleGeofenceRegions> selectByRegionId(Long regionId);
}
