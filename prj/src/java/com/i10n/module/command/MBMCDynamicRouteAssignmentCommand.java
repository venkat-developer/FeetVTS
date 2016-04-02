package com.i10n.module.command;

import java.util.Calendar;
import java.util.Date;


public class MBMCDynamicRouteAssignmentCommand implements ICommandBean {

	private String IMEI;
	private long routeId;
	private Date routeScheduleTime; 
	private double latitude;
	private double longitude;
	private Date packetTime;
	
	public MBMCDynamicRouteAssignmentCommand() {
		// TODO Auto-generated constructor stub
	}
	
	public MBMCDynamicRouteAssignmentCommand(String IMEI, long routeId, Date routeScheduleTime,
			double latitude, double longitude, Date packetTime) {
		setIMEI(IMEI);
		setRouteId(routeId);
		setRouteScheduleTime(routeScheduleTime);
		setLatitude(latitude);
		setLongitude(longitude);
		setPacketTime(packetTime);
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

	public Date getRouteScheduleTime() {
		return routeScheduleTime;
	}

	public void setRouteScheduleTime(Date routeScheduleTime) {
		this.routeScheduleTime = routeScheduleTime;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public Date getPacketTime() {
		return packetTime;
	}

	public void setPacketTime(Date packetTime) {
		this.packetTime = packetTime;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Command getCommandID() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString(){
		StringBuffer toString = new StringBuffer();
		toString.append("MBMCDynamicRouteAssignmentCommand :: IMEI = "+IMEI);
		toString.append(", RouteId = "+routeId);
		Calendar cal = Calendar.getInstance();
		cal.setTime(routeScheduleTime);
		toString.append(", RouteScheduleTime = "+cal.get(Calendar.HOUR)+":"+cal.get(Calendar.MINUTE));
		toString.append(", Latitude = "+latitude);
		toString.append(", Longitude = "+longitude);
		toString.append(", PacketTime = "+packetTime.toString());
		
		return toString.toString();
	}

}
