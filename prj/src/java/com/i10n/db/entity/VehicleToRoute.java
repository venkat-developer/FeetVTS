package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.VehicleToRoutePrimaryKey;

/**
 * Mapping Vehicle to Route for trip scheduling
 * @author roopa.kn
 * 
 */
public class VehicleToRoute implements IEntity<VehicleToRoute> {

	private VehicleToRoutePrimaryKey primarykey;

	public VehicleToRoute(Long vehicleId, Long routeId) {
		primarykey = new VehicleToRoutePrimaryKey(vehicleId, routeId);
	}

	public VehicleToRoute() {
		// TODO Auto-generated constructor stub
	}

	public long getVehicleId() {
		return this.primarykey.getVehicleId();
	}

	public void setVehicleId(long vehicleId) {
		this.primarykey.setVehicleId(vehicleId);
	}

	public Long getRouteId() {
		return this.primarykey.getRouteId();
	}

	public void setRouteId(Long routeId) {
		this.primarykey.setRouteId(routeId);
	}

	@Override
	public boolean equalsEntity(VehicleToRoute object) {
		// TODO Auto-generated method stub
		return false;
	}

}
