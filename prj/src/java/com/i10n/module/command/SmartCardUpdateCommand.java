/**
 * 
 */
package com.i10n.module.command;

import java.util.Date;

/**
 * @author joshua
 *
 */
public class SmartCardUpdateCommand implements ICommandBean {
	private String m_imei;
	private String m_smartcardid;
	private double m_latitude;
	private double m_longitude;
	private Date m_occurredat;



	public SmartCardUpdateCommand(String mImei, String mSmartcardid,
			double mLatitude, double mLongitude,Date occurredat) {
		super();
		m_imei = mImei;
		m_smartcardid = mSmartcardid;
		m_latitude = mLatitude;
		m_longitude = mLongitude;
		m_occurredat=occurredat;
	}

	public Date getM_occurredat() {
		return m_occurredat;
	}

	public void setM_occurredat(Date mOccurredat) {
		m_occurredat = mOccurredat;
	}

	@Override
	public Command getCommandID() {
//		return Command.SMARTCARDALERT;
		return Command.DEFAULT;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "SmartCardUpdateCommand";
	}

	public String getM_imei() {
		return m_imei;
	}

	public void setM_imei(String mImei) {
		m_imei = mImei;
	}

	public String getM_smartcardid() {
		return m_smartcardid;
	}

	public void setM_smartcardid(String mSmartcardid) {
		m_smartcardid = mSmartcardid;
	}

	public double getM_latitude() {
		return m_latitude;
	}

	public void setM_latitude(double mLatitude) {
		m_latitude = mLatitude;
	}

	public double getM_longitude() {
		return m_longitude;
	}

	public void setM_longitude(double mLongitude) {
		m_longitude = mLongitude;
	}

	@Override
	public String toString() {
		return "SmartCardUpdateCommand [m_imei=" + m_imei + ", m_latitude="
				+ m_latitude + ", m_longitude=" + m_longitude
				+ ", m_occurredat=" + m_occurredat + ", m_smartcardid="
				+ m_smartcardid + "]";
	}



}
