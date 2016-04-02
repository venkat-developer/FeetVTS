/**
 * 
 */
package com.i10n.fleet.dataobject;

import java.util.Date;

/**
 * @author HEMANT
 *
 */
public class Vehicle {
	private long id;                 
	private String make;               
	private String model;              
	private String year;               
	             
	private long fuelcaliberationid; 
	private Date odometer_updatedat; 
	private long odometer_value;     
	private long vehicle_icon_pic_id;
	private long groupid;            
	private String displayname;        
	private boolean deleted;            
	private long ownerid;
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
	/**
	 * @return the make
	 */
	public String getMake() {
		return make;
	}
	/**
	 * @param make the make to set
	 */
	public void setMake(String make) {
		this.make = make;
	}
	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}
	/**
	 * @return the year
	 */
	public String getYear() {
		return year;
	}
	/**
	 * @param year the year to set
	 */
	public void setYear(String year) {
		this.year = year;
	}
	
	/**
	 * @return the fuelcaliberationid
	 */
	public long getFuelcaliberationid() {
		return fuelcaliberationid;
	}
	/**
	 * @param fuelcaliberationid the fuelcaliberationid to set
	 */
	public void setFuelcaliberationid(long fuelcaliberationid) {
		this.fuelcaliberationid = fuelcaliberationid;
	}
	/**
	 * @return the odometer_updatedat
	 */
	public Date getOdometer_updatedat() {
		return odometer_updatedat;
	}
	/**
	 * @param odometer_updatedat the odometer_updatedat to set
	 */
	public void setOdometer_updatedat(Date odometer_updatedat) {
		this.odometer_updatedat = odometer_updatedat;
	}
	/**
	 * @return the odometer_value
	 */
	public long getOdometer_value() {
		return odometer_value;
	}
	/**
	 * @param odometer_value the odometer_value to set
	 */
	public void setOdometer_value(long odometer_value) {
		this.odometer_value = odometer_value;
	}
	/**
	 * @return the vehicle_icon_pic_id
	 */
	public long getVehicle_icon_pic_id() {
		return vehicle_icon_pic_id;
	}
	/**
	 * @param vehicle_icon_pic_id the vehicle_icon_pic_id to set
	 */
	public void setVehicle_icon_pic_id(long vehicle_icon_pic_id) {
		this.vehicle_icon_pic_id = vehicle_icon_pic_id;
	}
	/**
	 * @return the groupid
	 */
	public long getGroupid() {
		return groupid;
	}
	/**
	 * @param groupid the groupid to set
	 */
	public void setGroupid(long groupid) {
		this.groupid = groupid;
	}
	/**
	 * @return the displayname
	 */
	public String getDisplayname() {
		return displayname;
	}
	/**
	 * @param displayname the displayname to set
	 */
	public void setDisplayname(String displayname) {
		this.displayname = displayname;
	}
	/**
	 * @return the deleted
	 */
	public boolean isDeleted() {
		return deleted;
	}
	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	/**
	 * @return the ownerid
	 */
	public long isOwnerid() {
		return ownerid;
	}
	/**
	 * @param ownerid the ownerid to set
	 */
	public void setOwnerid(long ownerid) {
		this.ownerid = ownerid;
	}            

}
