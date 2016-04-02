package com.i10n.db.entity.primarykey;

import com.i10n.db.entity.IEntity.IPrimaryKey.IPrimaryKey;

/**
 * Represents the primary key for ACLVehicle
 * @author sreekanth
 *
 */
public class ACLVehiclePrimaryKey implements IPrimaryKey {
	
	private long vehicleid;
	
	private long userid;

	public ACLVehiclePrimaryKey(long vehicleid, long userid) {
		super();
		this.vehicleid = vehicleid;
		this.userid = userid;
	}

	public long getVehicleid() {
		return vehicleid;
	}

	public void setVehicleid(long vehicleid) {
		this.vehicleid = vehicleid;
	}

	public long getUserid() {
		return userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}
	
}
