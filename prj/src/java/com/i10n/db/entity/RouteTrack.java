package com.i10n.db.entity;

import java.util.Date;

import com.i10n.db.entity.IEntity.IEntity;

/**
 * RouteTrack Entity with attributes 
 * RouteStartDate,
 * IMEI,
 * RouteId,
 * TripId,
 * StartTrackHistoryId,
 * EndTrackHistoryId
 * 
 * @author Dharmaraju V
 *
 */
public class RouteTrack implements IEntity<RouteTrack>{

	private Date routeStartDate;
	private String IMEI;
	private long routeId;
//	private long tripId;
	private long startTrackHistoryId;
	private long endTrackHistoryId;
	
	
	public RouteTrack(Date routeStartDate, String IMEI, long routeId, /*long tripId,*/ long startTrackHistoryId, long endTrackHistoryId){
		this.routeStartDate = routeStartDate;
		this.IMEI = IMEI;
		this.routeId = routeId;
//		this.tripId = tripId;
		this.startTrackHistoryId = startTrackHistoryId;
		this.endTrackHistoryId = endTrackHistoryId;
	}
	
	public RouteTrack() {
		// TODO Auto-generated constructor stub
	}

	public Date getRouteStartDate() {
		return routeStartDate;
	}

	public void setRouteStartDate(Date routeStartDate) {
		this.routeStartDate = routeStartDate;
	}

	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}

	public long getRouteId() {
		return routeId;
	}

	public void setRouteId(long routeId) {
		this.routeId = routeId;
	}

	/*public long getTripId() {
		return tripId;
	}

	public void setTripId(long tripId) {
		this.tripId = tripId;
	}*/

	public long getStartTrackHistoryId() {
		return startTrackHistoryId;
	}

	public void setStartTrackHistoryId(long startTrackHistoryId) {
		this.startTrackHistoryId = startTrackHistoryId;
	}

	public long getEndTrackHistoryId() {
		return endTrackHistoryId;
	}

	public void setEndTrackHistoryId(long endTrackHistoryId) {
		this.endTrackHistoryId = endTrackHistoryId;
	}

	@Override
	public boolean equalsEntity(RouteTrack object) {
		// TODO Auto-generated method stub
		return false;
	}

	public String toString(){
		StringBuffer returnString =  new StringBuffer();
		returnString.append("RouteId = ");
		returnString.append(routeId);
		/*returnString.append(", TripId = ");
		returnString.append(tripId);*/
		returnString.append(", IMEI = ");
		returnString.append(IMEI);
		returnString.append(", RouteStartDate = ");
		returnString.append(routeStartDate);
		returnString.append(", StartTrackHistoryId = ");
		returnString.append(startTrackHistoryId);
		returnString.append(", EndTrackHistoryId = ");
		returnString.append(endTrackHistoryId);
		return returnString.toString();
	}
}
