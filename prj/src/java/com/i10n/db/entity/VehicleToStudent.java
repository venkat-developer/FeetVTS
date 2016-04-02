/**
 * 
 */
package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.VehicleToStudentPrimaryKey;

/**
 * @author roopa.kn
 * 
 */
public class VehicleToStudent implements IEntity<VehicleToStudent> {

	private VehicleToStudentPrimaryKey primarykey;

	public VehicleToStudent(Long vehicleid, Long studentid) {

		primarykey = new VehicleToStudentPrimaryKey(vehicleid, studentid);

	}

	public long getVehicleid() {
		return this.primarykey.getVehicleid();
	}

	public void setVehicleid(long vehicleid) {
		this.primarykey.setVehicleid(vehicleid);
	}

	public Long getStudentid() {
		return this.primarykey.getStudentid();
	}

	public void setStudentid(Long studentid) {
		this.primarykey.setStudentid(studentid);
	}

	@Override
	public boolean equalsEntity(VehicleToStudent object) {
		// TODO Auto-generated method stub
		return false;
	}

}
