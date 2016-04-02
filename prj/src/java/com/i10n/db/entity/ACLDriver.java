/**
 * 
 */
package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.ACLDriverPrimaryKey;

/**
 * @author joshua
 *
 */
public class ACLDriver implements IEntity<ACLDriver> {

	private ACLDriverPrimaryKey primarykey;
	
public ACLDriver(Long driverid,Long userid){
	
	primarykey=new ACLDriverPrimaryKey(driverid, userid);
	
}
public long getUserid() {
	return this.primarykey.getUserid();
}

public void setUserid(long userid) {
	this.primarykey.setUserid(userid);
}
public Long getDriverid(){
	return this.primarykey.getDriverid();
}
public void setDriverid(Long driverid){
	this.primarykey.setDriverid(driverid);
}









	@Override
	public boolean equalsEntity(ACLDriver object) {
		// TODO Auto-generated method stub
		return false;
	}

}
