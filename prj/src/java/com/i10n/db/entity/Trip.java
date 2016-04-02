package com.i10n.db.entity;

import java.util.Calendar;
import java.util.Date;

import com.i10n.db.entity.IEntity.IEntity;
import com.i10n.db.entity.primarykey.LongPrimaryKey;

public class Trip implements IEntity<Trip> {

	private LongPrimaryKey id;
	private String tripName;
	private Date startDate;
	private boolean overrideFuelCalibration;
	private float speedLimit;
	private long vehicleId;
	private long driverId;
	private String destination;
	private boolean scheduledTrip;
	private Date endDate;
	private boolean activeTrip;
	private float cumulativeDistance;
	private int idlePointsTimeLimit;
	// change this with the geofence class in the end

	private long geoFenceId;
	private boolean maileSent;
	private int startAdValue;
    
    public Trip(Long id, String tripName, Date startDate, boolean overrideFuelCalibration, float speedLimit, long vehicleId,
            long driverId, String destination, boolean scheduledTrip, Date endDate, boolean activeTrip, float cumulativeDistance,
            long geoFenceId, boolean mailSent, int startADValue,int idlePointsTimeLimit) {
        super();
        this.id = new LongPrimaryKey(id);
        this.tripName = tripName;
        this.startDate = startDate;
        this.overrideFuelCalibration = overrideFuelCalibration;
        this.speedLimit = speedLimit;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.destination = destination;
        this.scheduledTrip = scheduledTrip;
        this.endDate = endDate;
        this.activeTrip = activeTrip;
        this.cumulativeDistance = cumulativeDistance;
        this.geoFenceId = geoFenceId;
        this.maileSent = mailSent;
        this.startAdValue = startADValue;
        this.idlePointsTimeLimit = idlePointsTimeLimit;
     
    }
 
	public Trip(float speedLimit, long vehicleId, long driverId, int idlePointsTimeLimit,String tripName) {
		super();
		Calendar calendar = Calendar.getInstance();
		Date date=calendar.getTime();
		this.tripName = tripName;
		this.overrideFuelCalibration = false;
		this.speedLimit = speedLimit;
		this.vehicleId = vehicleId;
		this.driverId = driverId;
		this.destination = "delhi";
		this.scheduledTrip = false;
		this.activeTrip = true;
		this.startDate = date;
		this.endDate = null;
		this.cumulativeDistance = 0;

		this.idlePointsTimeLimit = idlePointsTimeLimit;
	}

	public long getGeoFenceId() {
		return geoFenceId;
	}

	public void setGeoFenceId(long geoFenceId) {
		this.geoFenceId = geoFenceId;
	}

	public boolean isMaileSent() {
		return maileSent;
	}

	public void setMaileSent(boolean maileSent) {
		this.maileSent = maileSent;
	}

	public int getStartAdValue() {
		return startAdValue;
	}

	public void setStartAdValue(int startAdValue) {
		this.startAdValue = startAdValue;
	}

	public LongPrimaryKey getId() {
		return id;
	}

	public void setId(LongPrimaryKey id) {
		this.id = id;
	}

	public String getTripName() {
		return tripName;
	}

	public void setTripName(String tripName) {
		this.tripName = tripName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public boolean isOverrideFuelCalibration() {
		return overrideFuelCalibration;
	}

	public void setOverrideFuelCalibration(boolean overrideFuelCalibration) {
		this.overrideFuelCalibration = overrideFuelCalibration;
	}

	public float getSpeedLimit() {
		return speedLimit;
	}

	public void setSpeedLimit(float speedLimit) {
		this.speedLimit = speedLimit;
	}

	public long getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(long vehicleId) {
		this.vehicleId = vehicleId;
	}

	public long getDriverId() {
		return driverId;
	}

	public void setDriverId(long driverId) {
		this.driverId = driverId;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public boolean isScheduledTrip() {
		return scheduledTrip;
	}

	public void setScheduledTrip(boolean scheduledTrip) {
		this.scheduledTrip = scheduledTrip;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isActiveTrip() {
		return activeTrip;
	}

	public void setActiveTrip(boolean activeTrip) {
		this.activeTrip = activeTrip;
	}

	public float getCumulativeDistance() {
		return cumulativeDistance;
	}

	public void setCumulativeDistance(float cumulativeDistance) {
		this.cumulativeDistance = cumulativeDistance;
	}
	
	public int getIdlePointsTimeLimit() {
        return idlePointsTimeLimit;
    }
     
    public void setIdlePointsTimeLimit(int idlePointsTimeLimit) {
        this.idlePointsTimeLimit = idlePointsTimeLimit;
    }

    @Override
    public boolean equalsEntity(Trip object) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public String toString(){
    	StringBuffer returnString = new StringBuffer();
    	returnString.append("TripID = ");
    	returnString.append(id.getId());
    	returnString.append(", TripName = ");
    	returnString.append(tripName);
    	returnString.append(", VehicleID = ");
    	returnString.append(vehicleId);
    	returnString.append(", DriverID = ");
    	returnString.append(driverId);
    	returnString.append(", StartDate = ");
    	returnString.append(startDate.toString());
    	returnString.append(", EndDate = ");
    	if(endDate != null){
    		returnString.append(endDate.toString());
    	} else {
    		returnString.append("null");
    	}
    	returnString.append(", IsActive = ");
    	returnString.append(activeTrip);
    	return returnString.toString();
    }
}
