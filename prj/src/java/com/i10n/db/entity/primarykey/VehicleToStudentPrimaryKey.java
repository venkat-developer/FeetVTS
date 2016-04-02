/**
 * 
 */
package com.i10n.db.entity.primarykey;

import com.i10n.db.entity.IEntity.IPrimaryKey.IPrimaryKey;

/**
 * @author roopa.kn
 *
 */
public class VehicleToStudentPrimaryKey implements IPrimaryKey {
	private Long vehicleid;
	
	private Long studentid;
	
public VehicleToStudentPrimaryKey(Long vehicleid,Long studentid){
	this.studentid=studentid;
	this.vehicleid=vehicleid;
}
	public Long getStudentid() {
		return studentid;
	}

	public void setStudentid(Long studentid) {
		this.studentid = studentid;
	}

	public Long getVehicleid() {
		return vehicleid;
	}

	public void setVehicleid(Long vehicleid) {
		this.vehicleid =vehicleid;
	}
	

}
