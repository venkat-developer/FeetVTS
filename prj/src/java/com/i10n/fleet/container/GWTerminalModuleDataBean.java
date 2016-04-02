/**
 * 
 */
package com.i10n.fleet.container;

import java.util.Date;
import java.util.Vector;

import com.i10n.fleet.container.GWTrackModuleDataBean.ModuleVersion;

/**
 * @author joshua
 *
 *@i10n-2010
 */
public class GWTerminalModuleDataBean {


	/**
	 * GSM Signal Strength. It can take value from 0 to 30.
	 * Higher the value better the signal. Depends on the availability of the operator.
	 * If the signal is low, there is a high possibility that data updation may
	 * not happen.
	 * V3 module
	 * */
	private double GSMStrength=0.0;
	/**
	 * Previously called as SQD. Actually a sequence number that identifies the packet.
	 * On successful packet sending completion, sequence number will be updated.
	 * If the same sequence number as the previous packet is received, it means,
	 * the hardware module hasnt received the acknowledgment the server sent back.
	 * So, appropriately delete the data so that there is no repetition of data.
	 * V3 modules denote this parameter as sqd. 
	 */
	private long numberOfSuccessPackets;
	
	/**
	 * Previously called as SQG. This parameter is not used in the server side.
	 * The hardware module keeps count of the number of attempts made to send
	 * packets. The difference between this parameter and the numberOfSuccessPackets
	 * will tell the number of failure in sending packets to the server from the 
	 * hardware module.
	 * V3 modules denote this parameter as sqg.
	 */
	private long numberOfPacketSendingAttempts;

	/**
	 * Tells the battery voltage of the hardware module. Contains the battery 
	 * voltage in the format (original_battery_voltage * 1000). This can range
	 * from 3500 to 4100. Because the highest and lowest battery voltage possible
	 * are 4.1v and 3.5v respectively. If the battery voltage is received as 0v,
	 * tells that the battery is dead and its life time is expired.
	 * V3 modules denote this parameter as bv.
	 */
	
	private double moduleBatteryVoltage;
	/**
	 * The number of errors that have occurred in the hardware module. If this error 
	 * parameter reaches a particular limit, the module will restart itself.
	 * V3 modules denote this parameter as e.
	 */
	
	private int numberOfErrorsInHardwareModule;
	
	/**
	 * Location Area Code. This parameter is retrieved from the hardware's GSM module.
	 * The value of this parameter depends on the availability of the GSM network.
	 * Intended to retrieve this parameter to use these values in trackut mobile
	 * to identify the location of a person using location area code, cellID, MCC and MNC.
	 * V3 modules denote this parameter as lac.
	 */
	private int locationAreaCode;
	
	/**
	 * CellID. This parameter is retrieved from the hardware's GSM Module. The value
	 * of this parameter depends on the availability of the GSM network. Intended to 
	 * retrieve this parameter to use these values in trackut mobile
	 * to identify the location of a person using location area code, cellID, MCC and MNC.
	 * V3 modules denote this parameter as ci.
	 */
	private int cellId;

	/**
	 * Tells whether the incoming packet is a ping packet or a data packet. If true, the
	 * packet is a ping packet. If false, packet is a data packet.
	 * V3 modules denote this parameter as pf.
	 */
	private boolean pingFlag;
	
	/**
	 * Tells whether the hardware's battery charger is connected to the attached vehicle's
	 * battery. If true, the battery charger is connected to vehicle's battery. If false,
	 * the hardware module stands alone and runs on its own battery power.
	 * V3 modules denote this parameter as cc. 
	 */
	private boolean chargerConnected;
	
	/**
	 * Tells whether the hardware module underwent a complete hardware level restart.
	 * The in-memory parameters of the hardware module will be erased. Only those persisted
	 * can be restored. If true, the hardware module underwent a complete hardware level
	 * restart. If false, the hardware module works as fine as ever.
	 * V3 modules denote this parameter as mrs.
	 */
	private boolean masterHardwareLevelRestart;
	
	/**
	 * Tells whether the hardware module underwent a code level restart. When this occurs,
	 * the in-memory parameters are not lost and simply the module starts executing the 
	 * firmware code from the 1st line of code.
	 * V3 modules denote this parameter as rs.
	 */
	private boolean moduleCodeLevelRestart;
	
	/**
	 * Digital Data sent from the module.
	 * <b>Currently not used in the server side.</b>
	 */
	private String digitalInput3;
	
	/**
	 * Digital Data sent from the module.
	 * <b>Currently not used in the server side.</b>
	 */
	private String digitalInput2;
	
	/**
	 * Digital Data sent from the module.
	 * <b>Currently not used in the server side.</b>
	 */
	private String digitalInput1;
	
	/**
	 * Set to true, whenever the panic button is pressed in the hardware module.
	 * This feature should be as an emergency situation alert to intimate the owner
	 * or asking for some help.
	 * V3 modules denote this parameter as d5.
	 */
	private boolean panicData; 
	
	/**
	 * Firmware Version tells the version of the firmware code that is flashed on to the 
	 * hardware module. This is for the hardware developers' reference. For each customer,
	 * a specific requirement need to be added. To identify such features, this version 
	 * number is used. 
	 */
	private double firmwareVersion;
	
	/**
	 * Firmware Version tells the version of the firmware code that is flashed on to the 
	 * hardware module. This is for the hardware developers' reference. For each customer,
	 * a specific requirement need to be added. To identify such features, this version 
	 * number is used. 
	 */
	private ModuleVersion moduleVersion;
	
	/**
	 * IMEI identifies the hardware module. Usually the IMEI will be of length 15.
	 * Unavailability of this packet leads to ignoring this packet.
	 * V3 modules denote this parameter as id.
	 */
	private String imei;
	
	/**
	 * This Vector contains the bulk packets that the hardware module has accumulated to send.
	 * If no packets are accumulated, then there will be only one update. If this is a
	 * V3 module, the lat, long, distance and fuel parameters will be sent thru this vector.
	 */
	private Vector<GWTerminalBulkUpdateBean> bulkUpdateData;
	
	/**
	 * Time of insertion of the packet inside ModuleUpdateProcessor. This time has to be
	 * used while checking the 10-minutes interval in spike detection because, when the 
	 * current system time is used, there are chances that the updates from the
	 * ModuleUpdateProcessor are processed 10 minutes after insertion into it leading to 
	 * correct data being rejected as spike data. This data should be new Date() before 
	 * inserting into ModuleUpdateProcessor.
	 **/
	private Date moduleUpdateTime;
	


	public double getGSMStrength() {
		return GSMStrength;
	}

	public void setGSMStrength(double gSMStrength) {
		GSMStrength = gSMStrength;
	}

	public long getNumberOfSuccessPackets() {
		return numberOfSuccessPackets;
	}

	public void setNumberOfSuccessPackets(long numberOfSuccessPackets) {
		this.numberOfSuccessPackets = numberOfSuccessPackets;
	}

	public long getNumberOfPacketSendingAttempts() {
		return numberOfPacketSendingAttempts;
	}

	public void setNumberOfPacketSendingAttempts(long numberOfPacketSendingAttempts) {
		this.numberOfPacketSendingAttempts = numberOfPacketSendingAttempts;
	}

	public double getModuleBatteryVoltage() {
		return moduleBatteryVoltage;
	}

	public void setModuleBatteryVoltage(double moduleBatteryVoltage) {
		this.moduleBatteryVoltage = moduleBatteryVoltage;
	}

	public int getNumberOfErrorsInHardwareModule() {
		return numberOfErrorsInHardwareModule;
	}

	public void setNumberOfErrorsInHardwareModule(int numberOfErrorsInHardwareModule) {
		this.numberOfErrorsInHardwareModule = numberOfErrorsInHardwareModule;
	}

	public int getLocationAreaCode() {
		return locationAreaCode;
	}

	public void setLocationAreaCode(int locationAreaCode) {
		this.locationAreaCode = locationAreaCode;
	}

	public int getCellId() {
		return cellId;
	}

	public void setCellId(int cellId) {
		this.cellId = cellId;
	}

	public boolean isPingFlag() {
		return pingFlag;
	}

	public void setPingFlag(boolean pingFlag) {
		this.pingFlag = pingFlag;
	}

	public boolean isChargerConnected() {
		return chargerConnected;
	}

	public void setChargerConnected(boolean chargerConnected) {
		this.chargerConnected = chargerConnected;
	}

	public boolean isMasterHardwareLevelRestart() {
		return masterHardwareLevelRestart;
	}

	public void setMasterHardwareLevelRestart(boolean masterHardwareLevelRestart) {
		this.masterHardwareLevelRestart = masterHardwareLevelRestart;
	}

	public boolean isModuleCodeLevelRestart() {
		return moduleCodeLevelRestart;
	}

	public void setModuleCodeLevelRestart(boolean moduleCodeLevelRestart) {
		this.moduleCodeLevelRestart = moduleCodeLevelRestart;
	}

	public String getDigitalInput3() {
		return digitalInput3;
	}

	public void setDigitalInput3(String digitalInput3) {
		this.digitalInput3 = digitalInput3;
	}

	public String getDigitalInput2() {
		return digitalInput2;
	}

	public void setDigitalInput2(String digitalInput2) {
		this.digitalInput2 = digitalInput2;
	}

	public String getDigitalInput1() {
		return digitalInput1;
	}

	public void setDigitalInput1(String digitalInput1) {
		this.digitalInput1 = digitalInput1;
	}

	public boolean isPanicData() {
		return panicData;
	}

	public void setPanicData(boolean panicData) {
		this.panicData = panicData;
	}

	public double getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(double firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public ModuleVersion getModuleVersion() {
		return moduleVersion;
	}

	public void setModuleVersion(ModuleVersion moduleVersion) {
		this.moduleVersion = moduleVersion;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}


	public Vector<GWTerminalBulkUpdateBean> getBulkUpdateData() {
		return bulkUpdateData;
	}

	public void setBulkUpdateData(Vector<GWTerminalBulkUpdateBean> bulkUpdateData) {
		this.bulkUpdateData = bulkUpdateData;
	}

	public Date getModuleUpdateTime() {
		return moduleUpdateTime;
	}

	public void setModuleUpdateTime(Date moduleUpdateTime) {
		this.moduleUpdateTime = moduleUpdateTime;
	}


	@Override
	public String toString() {
		return "GWTerminalModuleDataBean [GSMStrength=" + GSMStrength
				+ ", bulkUpdateData=" + bulkUpdateData + ", cellId=" + cellId
				+ ", chargerConnected=" + chargerConnected + ", digitalInput1="
				+ digitalInput1 + ", digitalInput2=" + digitalInput2
				+ ", digitalInput3=" + digitalInput3 + ", firmwareVersion="
				+ firmwareVersion + ", imei=" + imei + ", locationAreaCode="
				+ locationAreaCode + ", masterHardwareLevelRestart="
				+ masterHardwareLevelRestart + ", moduleBatteryVoltage="
				+ moduleBatteryVoltage + ", moduleCodeLevelRestart="
				+ moduleCodeLevelRestart + ", moduleUpdateTime="
				+ moduleUpdateTime + ", moduleVersion=" + moduleVersion
				+ ", numberOfErrorsInHardwareModule="
				+ numberOfErrorsInHardwareModule
				+ ", numberOfPacketSendingAttempts="
				+ numberOfPacketSendingAttempts + ", numberOfSuccessPackets="
				+ numberOfSuccessPackets + ", panicData=" + panicData
				+ ", pingFlag=" + pingFlag + "]";
	}
}
