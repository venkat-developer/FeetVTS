package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.ACLVehiclePrimaryKey;

public class ACLVehicle implements IEntity<ACLVehicle> {

	private ACLVehiclePrimaryKey primaryKey;
	
	public ACLVehicle(long vehicleid, long userid){
		primaryKey = new ACLVehiclePrimaryKey(vehicleid, userid);
	}
	
	public long getVehicleid() {
		return primaryKey.getVehicleid();
	}

	public void setVehicleid(long vehicleid) {
		this.primaryKey.setVehicleid(vehicleid);
	}

	public long getUserid() {
		return this.primaryKey.getUserid();
	}

	public void setUserid(long userid) {
		this.primaryKey.setUserid(userid);
	}

    @Override
    public boolean equalsEntity(ACLVehicle object) {
        // TODO Auto-generated method stub
        return false;
    }
}
