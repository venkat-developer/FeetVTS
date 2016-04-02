/**
 * 
 */
package com.i10n.db.entity.primarykey;

import com.i10n.db.entity.IEntity.IPrimaryKey.IPrimaryKey;

/**
 * @author roopa.kn
 *
 */
public class VehicleToRoutePrimaryKey implements IPrimaryKey {
	
	private Long vehicleId;
	private Long routeId;

	public VehicleToRoutePrimaryKey(Long vehicleId,Long routeId){
		this.routeId = routeId;
		this.vehicleId = vehicleId;
	}
	public Long getRouteId() {
		return routeId;
	}

	public void setRouteId(Long routeId) {
		this.routeId = routeId;
	}

	public Long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(Long vehicleId) {
		this.vehicleId = vehicleId;
	}


}
