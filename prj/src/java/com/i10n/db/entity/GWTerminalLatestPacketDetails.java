/**
 * 
 */
package com.i10n.db.entity;

import java.util.Date;

import com.i10n.db.entity.IEntity.IEntity;

/**
 * @author joshua
 * 
 */
public class GWTerminalLatestPacketDetails implements
		IEntity<GWTerminalLatestPacketDetails> {

	private String imei;
	private float gsmStrength;
	private float batteryVoltage;
	private boolean chargerConnected;
	private long sqd;
	private long sqg;
	private boolean mrs;
	private float course;
	private boolean isIdle;
	private long prevSqd;
	private int cellId;
	private int lac;
	// private String locationString;
	private Date lastupdatedat;
	private boolean rs;
	private int analogue1;
	private int analogue2;
	private int analogue3;
	private int analogue4;
	private int analogue5;
	private String digitalinput;
	private Date Occuredat;

	public Date getOccuredat() {
		return Occuredat;
	}

	public void setOccuredat(Date occuredat) {
		Occuredat = occuredat;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getDigitalinput() {
		return digitalinput;
	}

	public void setDigitalinput(String digitalinput) {
		this.digitalinput = digitalinput;
	}

	public float getGsmStrength() {
		return gsmStrength;
	}

	public void setGsmStrength(float gsmStrength) {
		this.gsmStrength = gsmStrength;
	}

	public float getBatteryVoltage() {
		return batteryVoltage;
	}

	public void setBatteryVoltage(float batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}

	public boolean isChargerConnected() {
		return chargerConnected;
	}

	public void setChargerConnected(boolean chargerConnected) {
		this.chargerConnected = chargerConnected;
	}

	public long getSqd() {
		return sqd;
	}

	public void setSqd(long sqd) {
		this.sqd = sqd;
	}

	public long getSqg() {
		return sqg;
	}

	public void setSqg(long sqg) {
		this.sqg = sqg;
	}

	public float getCourse() {
		return course;
	}

	public void setCourse(float course) {
		this.course = course;
	}

	public boolean isIdle() {
		return isIdle;
	}

	public void setIdle(boolean isIdle) {
		this.isIdle = isIdle;
	}

	public long getPrevSqd() {
		return prevSqd;
	}

	public void setPrevSqd(long prevSqd) {
		this.prevSqd = prevSqd;
	}

	public int getCellId() {
		return cellId;
	}

	public void setCellId(int cellId) {
		this.cellId = cellId;
	}

	public int getLac() {
		return lac;
	}

	public void setLac(int lac) {
		this.lac = lac;
	}

	public Date getLastupdatedat() {
		return lastupdatedat;
	}

	public void setLastupdatedat(Date lastupdatedat) {
		this.lastupdatedat = lastupdatedat;
	}

	public boolean isMrs() {
		return mrs;
	}

	public void setMrs(boolean mrs) {
		this.mrs = mrs;
	}

	public boolean isRs() {
		return rs;
	}

	public void setRs(boolean rs) {
		this.rs = rs;
	}

	public int getAnalogue1() {
		return analogue1;
	}

	public void setAnalogue1(int analogue1) {
		this.analogue1 = analogue1;
	}

	public int getAnalogue2() {
		return analogue2;
	}

	public void setAnalogue2(int analogue2) {
		this.analogue2 = analogue2;
	}

	public int getAnalogue3() {
		return analogue3;
	}

	public void setAnalogue3(int analogue3) {
		this.analogue3 = analogue3;
	}

	public int getAnalogue4() {
		return analogue4;
	}

	public void setAnalogue4(int analogue4) {
		this.analogue4 = analogue4;
	}

	public int getAnalogue5() {
		return analogue5;
	}

	public void setAnalogue5(int analogue5) {
		this.analogue5 = analogue5;
	}
	

	@Override
	public String toString() {
		return "GWTerminalLatestPacketDetails [Occuredat=" + Occuredat
				+ ", analogue1=" + analogue1 + ", analogue2=" + analogue2
				+ ", analogue3=" + analogue3 + ", analogue4=" + analogue4
				+ ", analogue5=" + analogue5 + ", batteryVoltage="
				+ batteryVoltage + ", cellId=" + cellId + ", chargerConnected="
				+ chargerConnected + ", course=" + course + ", digitalinput="
				+ digitalinput + ", gsmStrength=" + gsmStrength + ", imei="
				+ imei + ", isIdle=" + isIdle + ", lac=" + lac
				+ ", lastupdatedat=" + lastupdatedat + ", mrs=" + mrs
				+ ", prevSqd=" + prevSqd + ", rs=" + rs + ", sqd=" + sqd
				+ ", sqg=" + sqg + "]";
	}

	@Override
	public boolean equalsEntity(GWTerminalLatestPacketDetails object) {
		// TODO Auto-generated method stub
		return false;
	}

}
