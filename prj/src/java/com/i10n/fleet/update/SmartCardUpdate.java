package com.i10n.fleet.update;

import com.i10n.module.command.SmartCardUpdateCommand;

public class SmartCardUpdate implements IStreamUpdate {
	private String drivername;
	private SmartCardUpdateCommand smrtcardupdatecmd;
	private String TYPE="smartcard";
	private String[] login;


	public SmartCardUpdate(String[] login2, String drivername2,
			SmartCardUpdateCommand smartcardupdatecommand) {
		this.login=login2;
		this.drivername=drivername2;
		this.smrtcardupdatecmd=smartcardupdatecommand;
	}

	public String getDrivername() {
		return drivername;
	}

	public void setDrivername(String drivername) {
		this.drivername = drivername;
	}

	public SmartCardUpdateCommand getSmrtcardupdatecmd() {
		return smrtcardupdatecmd;
	}

	public void setSmrtcardupdatecmd(SmartCardUpdateCommand smrtcardupdatecmd) {
		this.smrtcardupdatecmd = smrtcardupdatecmd;
	}

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String[] getLogin() {
		return login;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public String toJSONString() {
		StringBuffer content = new StringBuffer();
		content.append("{driverfirstname\":\"");
		content.append(this.getDrivername());
		content.append("\"");
		content.append(",\"latitude\":\"");
		content.append(this.smrtcardupdatecmd.getM_latitude());
		content.append("\""); 
		content.append(",\"longitude\":\"");
		content.append(this.smrtcardupdatecmd.getM_longitude());
		content.append("\"");
		content.append(",\"occurdate\":\"");
		content.append(this.smrtcardupdatecmd.getM_occurredat());
		content.append("\"");
		content.append(",\"imei\":");
		content.append(this.smrtcardupdatecmd.getM_imei());
		content.append("\"");
		content.append("}");

		return content.toString();
	}
}
