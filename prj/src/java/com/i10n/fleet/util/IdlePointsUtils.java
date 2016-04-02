/**
 * 
 */
package com.i10n.fleet.util;

import java.util.LinkedList;

import com.i10n.db.entity.IdlePoints;

/**
 * @author HEMANT
 *
 */
public class IdlePointsUtils {
	
	private static IdlePointsUtils instance;
	
	
	public static IdlePointsUtils getInstance () {			
		if (instance == null) {
			instance = new IdlePointsUtils ();
		}		
		return instance;
	}
	
	//Used to store the idle point computed  from moduleupdateHandler
	LinkedList<IdlePoints> IdlePointList=new LinkedList<IdlePoints>();

	/**
	 * @return the idlePointList
	 */
	public LinkedList<IdlePoints> getIdlePointList() {
		return IdlePointList;
	}

	/**
	 * @param idlePointList the idlePointList to set
	 */
	public void setIdlePointList(LinkedList<IdlePoints> idlePointList) {
		synchronized (IdlePointList) {
			IdlePointList = idlePointList;
		}
		
	}
	
	
	public LinkedList<IdlePoints> getIdlePointsList(){
		synchronized (IdlePointList) {
			return IdlePointList;	
		}
	}

}
