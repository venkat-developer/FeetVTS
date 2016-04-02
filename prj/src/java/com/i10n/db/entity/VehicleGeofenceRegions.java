package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.VehicleGeofenceRegionsPrimaryKey;

public class VehicleGeofenceRegions implements IEntity<VehicleGeofenceRegions> {

		private VehicleGeofenceRegionsPrimaryKey primaryKey;
		
		private boolean insideRegion;//mail_sent;

		public VehicleGeofenceRegions(long vehicleid, long regionId){
			primaryKey = new VehicleGeofenceRegionsPrimaryKey(vehicleid, regionId);
		}
		public VehicleGeofenceRegions(long vehicleid, long regionId,boolean insideRegion){
			primaryKey = new VehicleGeofenceRegionsPrimaryKey(vehicleid, regionId);
			this.insideRegion=insideRegion;
		}
		
		public VehicleGeofenceRegions() {
			// TODO Auto-generated constructor stub
		}
		public boolean isInsideRegion() {
			return insideRegion;
		}
		public void setInsideRegion(boolean insideRegion) {
			this.insideRegion = insideRegion;
		}
		public long getVehicleId() {
			return this.primaryKey.getVehicleid();
		}

		public void setVehicleId(long vehicleId) {
			this.primaryKey.setVehicleid(vehicleId);
		}

		public long getRegionId() {
			return this.primaryKey.getRegionId();
		}

		public void setRegionId(long regionId) {
			this.primaryKey.setRegionId(regionId);
		}

	    @Override
	    public boolean equalsEntity(VehicleGeofenceRegions object) {
	        // TODO Auto-generated method stub
	        return false;
	    }

}
