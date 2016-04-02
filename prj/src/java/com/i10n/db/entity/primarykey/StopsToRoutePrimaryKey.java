/**
 * 
 */
package com.i10n.db.entity.primarykey;

import com.i10n.db.entity.IEntity.IPrimaryKey.IPrimaryKey;

/**
 * @author roopa.kn
 *
 */
public class StopsToRoutePrimaryKey implements IPrimaryKey {
	private Long routeid;
	
	private Long stopid;
	
public StopsToRoutePrimaryKey(Long stopid,Long routeid){
	this.routeid=routeid;
	this.stopid=stopid;
}
	public Long getRouteid() {
		return routeid;
	}

	public void setRouteid(Long routeid) {
		this.routeid = routeid;
	}

	public Long getStopid() {
		return stopid;
	}

	public void setStopid(Long stopid) {
		this.stopid =stopid;
	}
	

}
