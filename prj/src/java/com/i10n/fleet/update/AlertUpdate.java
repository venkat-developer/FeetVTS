package com.i10n.fleet.update;

import com.i10n.db.entity.Driver;
import com.i10n.db.entity.Vehicle;

public class AlertUpdate implements IStreamUpdate {

	private String[] login;
	private long vehicleID;
	private Vehicle vehicle;
	private Driver driver;
	private int status;
	private String digitalinput;
	private long trackhistoryid; 
	private  String type;

//	private static final Logger LOG = Logger.getLogger(PositionUpdate.class);

	public AlertUpdate(String[] login, long vehicleID,Vehicle vehicle,Driver driver,String d2, int i,String type) {
		this.login=login;
		this.vehicleID=vehicleID;
		this.digitalinput=d2;
		this.status=i;
		this.vehicle=vehicle;
		this.type=type;
	}

	public String getDigitalinput() {
		return digitalinput;
	}

	public void setDigitalinput(String digitalinput) {
		this.digitalinput = digitalinput;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public void setLogin(String[] login) {
		this.login = login;
	}

	public void setVehicleID(long vehicleID) {
		this.vehicleID = vehicleID;
	}

	public String[] getLogin() {
		return login;
	}

	public long getVehicleID() {
		return vehicleID;
	}

	public void setTrackHistoryID(long trackhistoryid) {
		this.trackhistoryid = trackhistoryid;
	}

	public long getTrackHistoryID() {
		return trackhistoryid;
	}

	@Override
	public long getId() {
		return getVehicleID();
	}

	@Override
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toJSONString() {
		StringBuffer content = new StringBuffer();
		content.append("{\"id\":");
		content.append(vehicleID);
		content.append(",\"driverfirstname\":\"");
		content.append(this.getDriver().getFirstName());
		content.append("\""); 
		content.append(",\"driverlastname\":\"");
		content.append(this.getDriver().getLastName());
		content.append("\"");
		content.append(",\"vehiclemodel\":\"");
		content.append(this.getVehicle().getModel());
		content.append("\""); 
		content.append(",\"vehicleDisplayname\":\"");
		content.append(this.getVehicle().getDisplayName());
		content.append("\"");
		content.append(",\"vehiclemake\":\"");
		content.append(this.getVehicle().getMake());
		content.append("\"");
		content.append(",\"status\":");
		content.append(this.getStatus());
		content.append(",\"vehicleicon\":");
		content.append(this.getVehicle().getVehicleIconPicId());
		content.append(",\"digitalinput\":");
		content.append(this.getDigitalinput());
		content.append( "}");

		return content.toString();
	}


}



