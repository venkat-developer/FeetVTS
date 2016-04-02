package com.i10n.db.entity;

import java.sql.Time;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

/**
 * Detail of the Route with the time schedule of the same
 * @author Dharamaraju V., Mrunal, Gobinath.G
 *
 */
public class RouteSchedule implements IEntity<RouteSchedule> {
	
	/**
	 * Value of format "RouteId-Hours:Minutes:seconds" where Hours and minutes and seconds are starting time of the Route
	 */
	private LongPrimaryKey id;
	private long stopId;
	private long routeId;
	private long ownerId;
	private Time time;
	private long estimatedDistance;
	private int spanDay;
	private int sequenceNumber;
	private String routeName;
	private String stopName;
	private String routeScheduleId;
	
	
	/**
	 * Default Constructor
	 */
	public RouteSchedule() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Constructor which retrieve all the data of RouteSchedules Table
	 * @param routeScheduleId
	 */
	
	public RouteSchedule(String routeScheduleId) {
		this.routeScheduleId = routeScheduleId;
	
	}
	/**
	 * Constructor which retrieve all the data of RouteSchedules Table
	 * @param id
	 * @param routeScheduleId
	 * @param routeId
	 * @param stopId
	 * @param sequenceNumber
	 * @param time
	 * @param estimatedDistance
	 * @param spanDay
	 */
	
	public RouteSchedule(LongPrimaryKey id,String routeScheduleId, long routeId, long stopId, int sequenceNumber,Time time, long estimatedDistance, int spanDay) {
		this.id = id;
		this.routeScheduleId = routeScheduleId;
		this.routeId = routeId;
		this.stopId = stopId;
		this.sequenceNumber = sequenceNumber;
		this.time = time;
		this.estimatedDistance = estimatedDistance;
		this.spanDay = spanDay;
	
	}

	
	/**
	 * Constructor Retrieves All The Data Of RouteSchedules Table and Also required parameters from the Routes And Stops 
	 * @param id
	 * @param routescheduleID
	 * @param routeid
	 * @param stopid
	 * @param sequenceno
	 * @param time
	 * @param distance
	 * @param spanday
	 * @param routename
	 * @param stopname
	 */
	public RouteSchedule(LongPrimaryKey id,String routescheduleID,long routeid,long stopid,int sequenceno,Time time,long distance,int spanday,String routename,String stopname){
		this.id = id;
		this.routeScheduleId = routescheduleID;
		this.routeId = routeid;
		this.stopId = stopid;
		this.time = time;
		this.sequenceNumber = sequenceno;
		this.estimatedDistance = distance;
		this.spanDay = spanday;
		this.routeName=routename;
		this.stopName=stopname;
	}
	public RouteSchedule(LongPrimaryKey id,String routescheduleID,long routeid,long stopid,int sequenceno,Time time,long distance,int spanday,String routename,String stopname,long ownerid){
		this.id = id;
		this.routeScheduleId = routescheduleID;
		this.routeId = routeid;
		this.stopId = stopid;
		this.time = time;
		this.sequenceNumber = sequenceno;
		this.estimatedDistance = distance;
		this.spanDay = spanday;
		this.routeName=routename;
		this.stopName=stopname;
		this.ownerId = ownerid;
	}
	
	//constructor which gets the routename,stopname,time to display in listboxes
	public RouteSchedule(LongPrimaryKey id,String routename, String stopname, Time ts) {
		this.id = id;
		this.routeName = routename;
		this.stopName = stopname;
		this.time = ts;
	}

	
	//returns values to addRouteSchedule
	public RouteSchedule(String routescheduleID,Long rid,Long sid,int sequenceNo, Time ts){
		this.routeScheduleId = routescheduleID;
		this.routeId = rid;
		this.stopId = sid;
		this.sequenceNumber = sequenceNo;
		this.time = ts;
	}

	//returns values to editRouteSchedule
	public RouteSchedule(LongPrimaryKey id, String routeName,
			String stopName, int sequenceNo, Time t, long estimatedDist,
			int spanDay) {
			this.id = id;
			this.routeName = routeName;
			this.stopName = stopName;
			this.sequenceNumber	= sequenceNo;
			this.time = t;
			this.estimatedDistance = estimatedDist;
			this.spanDay = spanDay;
	}
	
	/**
	 * gets the primary key Id of RouteSchedules
	 * @return id
	 */
	public LongPrimaryKey getId() {                  
		return id;
	}

	/**
	 * sets the primary key id
	 * @param id
	 */
	public void setId(LongPrimaryKey id) {          
		this.id = id;
	}
	/**
	 * gets the RouteScheduleID(RouteID-Time(HH:MM:SS))
	 * @return routescheduleID
	 */
	public String getRouteScheduleId() {			
		return routeScheduleId;
	}

	/**
	 * sets the RouteScheduleID
	 * @param routeScheduleID
	 */
	public void setRouteScheduleId(String routeScheduleID) {
		this.routeScheduleId = routeScheduleID;
	}

	/**
	 * gets the RouteId which is Foreign Key of id referred from table Routes
	 * @return routeId
	 */
	public long getRouteId() {
		return routeId;
	}

	/**
	 * sets the RouteId
	 * @param routeId
	 */
	public void setRouteId(long routeId) {
		this.routeId = routeId;
	}
	/**
	 * gets the StopId which is Foreign Key of id referred from table stops
	 * @return stopId
	 */
	
	public long getStopId() {
		return stopId;
	}
	/**
	 * sets the StopId
	 * @param stopId
	 */
	public void setStopId(long stopId) {
		this.stopId = stopId;
	}
	/**
	 * gets the SequenceNumber of RouteSchedule
	 * @return sequenceNumber
	 */
	public int getSequenceNumber() {
		return sequenceNumber;
	}
	/**
	 * sets the sequenceNumber 
	 * @param sequenceNumber
	 */

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	/**
	 * gets the time of RouteSchedule
	 * @return time
	 */
	public Time getExpectedTime() {
		return time;
	}

	public void setExpectedTime(Time time) {
		this.time = time;
	}
	/**
	 * Gets the EstimatedDistance of RouteSchedule between two consecutive Stops
	 * @return estimatedDistance
	 */
	public long getEstimatedDistance() {
		return estimatedDistance;
	}

	/**
	 * 	Sets the EstimatedDistance
	 * @param estimatedDistance
	 */
	public void setEstimatedDistance(long estimatedDistance) {
		this.estimatedDistance = estimatedDistance;
	}
	
	/**
	 * gets the Spanday of RouteSchedule
	 * @return spanday
	 */
	public int getSpanDay() {
		return spanDay;
	}
	/**
	 * sets the spanday
	 * @param spanDay
	 */
	public void setSpanDay(int spanDay) {
		this.spanDay = spanDay;
	}
	
	/**
	 * gets the RouteName from Routes by the Reference of RouteId
	 * @return routeName
	 */
	public String getRouteName(){					
		return routeName;
	}
	
	public void setRouteName(String routeName){ 	
		this.routeName = routeName;
	}
				
	/**
	 * gets the StopName from Stops by the Reference of StopId
	 * @return stopName
	 */

	public String getStopName(){					
		return stopName;
	}
	
	public void setStopName(String stopName){		
		this.stopName = stopName;
	}
	/**
	 * Gets the ownerID,an ID of a user who has been currently logged in 
	 * @return ownerID
	 */
	public long getOwnerId() {
		return ownerId;
	}
	/**
	 * Sets the OwnerID
	 * @param ownerID
	 */
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}

	@Override
	public boolean equalsEntity(RouteSchedule object) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Creates A String Buffer and Stores All The Data of RouteSchedules
	 */
	@Override
	public String toString(){
		StringBuffer string = new StringBuffer();
		string.append("RouteSchedule : RouteScheduleId = ");
		string.append(routeScheduleId);
		string.append(", RouteId = ");
		string.append(routeId);
		string.append(", StopId = ");
		string.append(stopId);
		string.append(", SequenceNumber = ");
		string.append(sequenceNumber);
		string.append(", Time = ");
		string.append(time);
		string.append(", EstimatedDistance = ");
		string.append(estimatedDistance);
		string.append(", SpanDay = ");
		string.append(spanDay);
		string.append("");
		return string.toString();
	}
}
