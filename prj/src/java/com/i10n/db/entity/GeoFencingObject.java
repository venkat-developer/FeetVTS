package com.i10n.db.entity;

import org.postgis.Geometry;

/**
 * @author HEMANT
 */
public class GeoFencingObject {

	private long vehicleid;
	
	private long regionid;
	
	private boolean insideRegion;//mailsent;
	
	/*private String mailList;
	private String smsList;*/
	
	private String regionName;
	private double speed;
	private long userid;
	private int shapeType;
	private Geometry polygon;
	
	/**
     * @author : Dharmaraju.v 
     * 
     * Differentiating among the 
     * 1) Normal Region meant of violations 			- 0
     * 2) For TNCSC Client - Fair Price Shops notation 	- 1
     * 3) For TNCSC Client - Warehouse notation			- 2
     */ 
    private int regionType;
    
    private String regionCode;
	
	
	//private boolean isCurrentInsideGeoFenceArea;
	
	
	/**
	 * @return the vehicleid
	 */
	public long getVehicleid() {
		return vehicleid;
	}
	/**
	 * @param vehicleid the vehicleid to set
	 */
	public void setVehicleid(long vehicleid) {
		this.vehicleid = vehicleid;
	}
	/**
	 * @return the regionid
	 */
	public long getRegionid() {
		return regionid;
	}
	/**
	 * @param regionid the regionid to set
	 */
	public void setRegionid(long regionid) {
		this.regionid = regionid;
	}
	
	public boolean isInsideRegion() {
		return insideRegion;
	}
	
	public void setInsideRegion(boolean insideRegion) {
		this.insideRegion = insideRegion;
	}
	/**
	 * @return the mailList
	 *//*
	public String getMailList() {
		return mailList;
	}
	*//**
	 * @param mailList the mailList to set
	 *//*
	public void setMailList(String mailList) {
		this.mailList = mailList;
	}
	*//**
	 * @return the smsList
	 *//*
	public String getSmsList() {
		return smsList;
	}
	*//**
	 * @param smsList the smsList to set
	 *//*
	public void setSmsList(String smsList) {
		this.smsList = smsList;
	}*/
	/**
	 * @return the regionName
	 */
	public String getRegionName() {
		return regionName;
	}
	/**
	 * @param regionName the regionName to set
	 */
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}
	/**
	 * @return the speed
	 */
	public double getSpeed() {
		return speed;
	}
	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	/**
	 * @return the userid
	 */
	public long getUserid() {
		return userid;
	}
	/**
	 * @param userid the userid to set
	 */
	public void setUserid(long userid) {
		this.userid = userid;
	}
	/**
	 * @return the shapeType
	 */
	public int getShapeType() {
		return shapeType;
	}
	/**
	 * @param shapeType the shapeType to set
	 */
	public void setShapeType(int shapeType) {
		this.shapeType = shapeType;
	}
	/**
	 * @return the polygon
	 */
	public Geometry getPolygon() {
		return polygon;
	}
	/**
	 * @param polygon the polygon to set
	 */
	public void setPolygon(Geometry polygon) {
		this.polygon = polygon;
	}
	/**
	 * @return the isCurrentInsideGeoFenceArea
	 */
	/*
	public boolean isCurrentInsideGeoFenceArea() {
		return isCurrentInsideGeoFenceArea;
	}
	*//**
	 * @param isCurrentInsideGeoFenceArea the isCurrentInsideGeoFenceArea to set
	 *//*
	public void setCurrentInsideGeoFenceArea(boolean isCurrentInsideGeoFenceArea) {
		this.isCurrentInsideGeoFenceArea = isCurrentInsideGeoFenceArea;
	}*/
	
	public int getRegionType() {
		return regionType;
	}
	public void setRegionType(int regionType) {
		this.regionType = regionType;
	}
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}
	public String getRegionCode() {
		return regionCode;
	}
}
