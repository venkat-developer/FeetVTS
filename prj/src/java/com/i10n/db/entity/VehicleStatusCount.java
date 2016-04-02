package com.i10n.db.entity;

/**
 * Class for processing vehicle status count for Dashboard
 * @author DVasudeva
 *
 */
public class VehicleStatusCount {
	private boolean lastupdatedatdiff;
	private boolean moduleupdatetimediff;
	
	public boolean isLastupdatedatdiff() {
		return lastupdatedatdiff;
	}
	
	public void setLastupdatedatdiff(boolean lastupdatedatdiff) {
		this.lastupdatedatdiff = lastupdatedatdiff;
	}
	
	public boolean isModuleupdatetimediff() {
		return moduleupdatetimediff;
	}
	
	public void setModuleupdatetimediff(boolean moduleupdatetimediff) {
		this.moduleupdatetimediff = moduleupdatetimediff;
	}

}
