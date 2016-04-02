/**
 * 
 */
package com.i10n.db.entity;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.StopsToRoutePrimaryKey;

/**
 * @author roopa.kn
 * 
 */
public class StopsToRoute implements IEntity<StopsToRoute> {

	private StopsToRoutePrimaryKey primarykey;

	public StopsToRoute(Long stopid, Long routeid) {

		primarykey = new StopsToRoutePrimaryKey(stopid, routeid);

	}

	public long getStopid() {
		return this.primarykey.getStopid();
	}

	public void setStopid(long stopid) {
		this.primarykey.setStopid(stopid);
	}

	public Long getRouteid() {
		return this.primarykey.getRouteid();
	}

	public void setRouteid(Long routeid) {
		this.primarykey.setRouteid(routeid);
	}

	@Override
	public boolean equalsEntity(StopsToRoute object) {
		// TODO Auto-generated method stub
		return false;
	}

}
