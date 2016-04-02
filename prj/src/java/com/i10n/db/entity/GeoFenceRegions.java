package com.i10n.db.entity;


import org.postgis.Geometry;
import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class GeoFenceRegions implements IEntity<GeoFenceRegions>{
	private long vehicleId;
	private LongPrimaryKey id; 
	private long regionId;
	private boolean insideRegion;
	private String regionName;
	private double speed;
	private long userId;
	private int shape;
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


	public GeoFenceRegions(LongPrimaryKey primaryKey, Geometry location,
			double speed, String regionName, long userId, int shape) {
		setId(primaryKey);
		setPolygon(location);
		setSpeed(speed);;
		setRegionName(regionName);
		setUserId(userId);
		setShape(shape);
	}

	public GeoFenceRegions() {
	}

	public GeoFenceRegions(long vehicleId, long regionId, boolean insideRegion, String regionName,
			double speed, long userId, int shape, Geometry polygon, int regionType,
			String regionCode) {
		this.vehicleId=vehicleId;
		this.regionId=regionId;
		this.insideRegion=insideRegion;
		this.regionName=regionName;
		this.speed=speed;
		this.userId=userId;
		this.shape=shape;
		this.polygon=polygon;
		this.regionType=regionType;
		this.regionCode=regionCode;
	}

	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public boolean isInsideRegion() {
		return insideRegion;
	}

	public void setInsideRegion(boolean insideRegion) {
		this.insideRegion = insideRegion;
	}

	public String getRegionName() {
		return regionName;
	}
	/**
	 * @param regionName the regionName to set
	 */
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	public long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public long getRegionId() {
		return regionId;
	}

	public void setRegionId(long regionId) {
		this.regionId = regionId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
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

	public int getShape() {
		return shape;
	}

	public void setShape(int shape) {
		this.shape = shape;
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
	@Override
	public boolean equalsEntity(GeoFenceRegions object) {
		// TODO Auto-generated method stub
		return false;
	}

}