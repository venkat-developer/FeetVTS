package com.i10n.fleet.dataobject;

import java.util.Date;

/**
 * @author HEMANT
 *
 */
public class LiveIdlePointObject {
	
	private long id;
	
	private long tripid;
	
	private String idlelocation;
	
	private String locationname;
	
	private Date starttime;
	
	private Date endtime;
	
	private boolean rowcreated;
	
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
	 * @return the tripid
	 */
	public long getTripid() {
		return tripid;
	}
	/**
	 * @param tripid the tripid to set
	 */
	public void setTripid(long tripid) {
		this.tripid = tripid;
	}
	/**
	 * @return the idlelocation
	 */
	public String getIdlelocation() {
		return idlelocation;
	}
	/**
	 * @param idlelocation the idlelocation to set
	 */
	public void setIdlelocation(String idlelocation) {
		this.idlelocation = idlelocation;
	}
	/**
	 * @return the locationname
	 */
	public String getLocationname() {
		return locationname;
	}
	/**
	 * @param locationname the locationname to set
	 */
	public void setLocationname(String locationname) {
		this.locationname = locationname;
	}
	/**
	 * @return the starttime
	 */
	public Date getStarttime() {
		return starttime;
	}
	/**
	 * @param starttime the starttime to set
	 */
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
	/**
	 * @return the endtime
	 */
	public Date getEndtime() {
		return endtime;
	}
	/**
	 * @param endtime the endtime to set
	 */
	public void setEndtime(Date endtime) {
		this.endtime = endtime;
	}
	/**
	 * @return the rowcreated
	 */
	public boolean isRowcreated() {
		return rowcreated;
	}
	/**
	 * @param rowcreated the rowcreated to set
	 */
	public void setRowcreated(boolean rowcreated) {
		this.rowcreated = rowcreated;
	}
		
}
