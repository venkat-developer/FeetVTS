/**
 * 
 */
package com.i10n.db.entity.primarykey;

import com.i10n.db.entity.IEntity.IPrimaryKey.IPrimaryKey;

/**
 * @author joshua
 *
 */
public class ACLDriverPrimaryKey implements IPrimaryKey {
	private Long driverid;
	
	private Long userid;
	
public ACLDriverPrimaryKey(Long driverid,Long userid){
	this.driverid=driverid;
	this.userid=userid;
}
	public Long getDriverid() {
		return driverid;
	}

	public void setDriverid(Long driverid) {
		this.driverid = driverid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}
	

}
