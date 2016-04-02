package com.i10n.module.command;

import java.util.Date;

import com.i10n.db.entity.AlertOrViolation.AlertType;

/**
 * 
 * @author Dharmaraju V
 *
 */
public class AlertOrViolationCommand implements ICommandBean{
	
	private String IMEI;
	
	private AlertType alertType;
	private double latitude;
	private double longitude;
	private Date alertTime;
	private Long alertTypeValue;
	
	public AlertOrViolationCommand(String IMEI, AlertType alertType, double latitude, double longitude, Date alertTime) {
		this.IMEI = IMEI;
		this.alertType = alertType;
		this.latitude = latitude;
		this.longitude = longitude;
		this.alertTime = alertTime;
	}
	
	public String getIMEI() {
		return IMEI;
	}

	public void setIMEI(String iMEI) {
		IMEI = iMEI;
	}
	
	public AlertType getAlertType() {
		return alertType;
	}

	public void setAlertType(AlertType alertType) {
		this.alertType = alertType;
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

	public Date getAlertTime() {
		return alertTime;
	}

	public void setAlertTime(Date alertTime) {
		this.alertTime = alertTime;
	}

	public Long getAlertTypeValue() {
		return alertTypeValue;
	}

	public void setAlertTypeValue(Long alertTypeValue) {
		this.alertTypeValue = alertTypeValue;
	}

	@Override
	public String toString (){
		final StringBuffer returnString = new StringBuffer();
		returnString.append("AlertOrViolationCommand : IMEI = ");
		returnString.append(getIMEI());
		returnString.append(", AlertType = ");
		returnString.append(getAlertType().toString());
		returnString.append(", Latitude = ");
		returnString.append(getLatitude());
		returnString.append(", Longitude = ");
		returnString.append(getLongitude());
		returnString.append(", AlertTime = ");
		returnString.append(getAlertTime().toString());
		returnString.append(", AlertTypeValue = ");
		returnString.append(getAlertTypeValue());
		return returnString.toString();
	}

	@Override
	public Command getCommandID() {
		return Command.ALERT_OR_VIOLATION;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

}
