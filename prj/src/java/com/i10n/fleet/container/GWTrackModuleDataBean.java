package com.i10n.fleet.container;

import java.util.ArrayList;
import java.util.Date;

import com.i10n.db.entity.IEntity.IEntity;



/**
 * This is a bean class which holds the updates sent by a GWTrack Hardware Module
 * @author Antony
 */
public class GWTrackModuleDataBean implements IEntity<GWTrackModuleDataBean> {
	
	/** Number of bulk packets **/
	private int pktCount;
	
	public enum ModuleVersion{
		MODULE_VERSION_3,
		MODULE_VERSION_3_5,
		MODULE_VERSION_5,
		MODULE_VERSION_5_1;
	}

	/**
	 * GPS Signal Strength from the module. It can take values from 0.0 upto 3.0 or 4.0
	 * Lower the value, better the signal.
	 * V3 modules denote this parameter as ss.
	 */
	private double gpsSignalStrength = 0.0;
	
	/**
	 * Vehicle course. Tells the direction in which the vehicle is moving. This value
	 * can range between 0 and 360. 
	 * <b>The accuracy of this parameter depends on the strength of the GPS signal.<b>
	 * V3 modules denote this parameter as dir.
	 */
	private double vehicleCourse = 0.0;
	
	/**
	 * GSM Signal Strength. It can take value from 0 to 30.
	 * Higher the value better the signal. Depends on the availability of the operator.
	 * If the signal is low, there is a high possibility that data updation may
	 * not happen.
	 * V3 modules denote this parameter as gs.
	 */
	private double gsmSignalStrength = 0.0;
	
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
	 * The Cumulative Distance tells the distance travelled by this hardware module
	 * in its life time presumably. But this parameter may get reset when the 
	 * range of the value goes out of limits. This parameter will not change 
	 * across module restarts.
	 * V3 modules denote this parameter as cd.
	 */
	private long cumulativeDistance;
	
	/**
	 * Specifies the maximum Speed of the vehicle over a period of the previous packet
	 * sending and the current packet sending.
	 * <b>The accuracy of this parameter depends on the strength of the GPS Signal.</b>
	 * V3 modules denote this parameter as mx.
	 */
	private double maxSpeed;
	
	/**
	 * Analog Data sent from the module.
	 * V3.5 Modules contain Vehicle Battery Voltage in this parameter.
	 */
	private int analogue1;
	
	/**
	 * Analog Data sent from the module.
	 * <b>Currently not used in the server side.</b>
	 */
	private int analogue2;
	
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
	private int firmwareVersion;
	
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
	private ArrayList<BulkUpdateDataBean> bulkUpdateData;
	
	/**
	 * Time of insertion of the packet inside ModuleUpdateProcessor. This time has to be
	 * used while checking the 10-minutes interval in spike detection because, when the 
	 * current system time is used, there are chances that the updates from the
	 * ModuleUpdateProcessor are processed 10 minutes after insertion into it leading to 
	 * correct data being rejected as spike data. This data should be new Date() before 
	 * inserting into ModuleUpdateProcessor.
	 */
	private Date moduleUpdateTime;
	

	/**
	 * year the data was received
	 */

	private int year;

	/**
	 * month the data was received
	 */

	private int month;


	/**
	 * dat the data was received
	 */

	private int day;

	/**
	 * boolean saying more data is to come from this imei
	 */

	private boolean moreToFollow;


	
	public GWTrackModuleDataBean(){
		digitalInput1 = new String();
		digitalInput2 = new String();
		digitalInput3 = new String();
	}
	
	public GWTrackModuleDataBean(double gpsSignalStrength,
			double vehicleCourse, double gsmSignalStrength,
			long numberOfSuccessPackets, long numberOfPacketSendingAttempts,
			double moduleBatteryVoltage, long cumulativeDistance,
			double maxSpeed, int analogue1, int analogue2,
			int numberOfErrorsInHardwareModule, int locationAreaCode,
			int cellId, boolean pingFlag, boolean chargerConnected,
			boolean masterHardwareLevelRestart, boolean moduleCodeLevelRestart, boolean moreToFollow,
			String digitalInput1, String digitalInput2,String digitalInput3, 
			boolean panicData, int firmwareVersion,
			 int year, int month, int day, String imei,
		    Date moduleUpdateTime) {
		super();
		this.gpsSignalStrength = gpsSignalStrength;
		this.vehicleCourse = vehicleCourse;
		this.gsmSignalStrength = gsmSignalStrength;
		this.numberOfSuccessPackets = numberOfSuccessPackets;
		this.numberOfPacketSendingAttempts = numberOfPacketSendingAttempts;
		this.moduleBatteryVoltage = moduleBatteryVoltage;
		this.cumulativeDistance = cumulativeDistance;
		this.maxSpeed = maxSpeed;
		this.analogue1 = analogue1;
		this.analogue2 = analogue2;
		this.numberOfErrorsInHardwareModule = numberOfErrorsInHardwareModule;
		this.locationAreaCode = locationAreaCode;
		this.cellId = cellId;
		this.pingFlag = pingFlag;
		this.chargerConnected = chargerConnected;
		this.masterHardwareLevelRestart = masterHardwareLevelRestart;
		this.moduleCodeLevelRestart = moduleCodeLevelRestart;
		this.digitalInput3 = digitalInput3;
		this.digitalInput2 = digitalInput2;
		this.digitalInput1 = digitalInput1;
		this.panicData = panicData;
		this.firmwareVersion = firmwareVersion;
//		this.moduleVersion = moduleVersion;
		this.imei = imei;
		this.moduleUpdateTime = moduleUpdateTime;
		this.year = year;
		this.month = month;
		this.day = day;
		this.moreToFollow = moreToFollow;
	}

	public void addBulkUpdateData(BulkUpdateDataBean bulkUpdateDataBean){
		bulkUpdateData.add(bulkUpdateDataBean);
	}
	
	/**
	 * Adds a single BulkUpdateDataBean to the bulkUpdateData Vector
	 * @param bulkUpdateDataBean
	 */


	public ArrayList<BulkUpdateDataBean> getBulkUpdateData() {
		return bulkUpdateData;
	}
	
	public void setBulkUpdateData(ArrayList<BulkUpdateDataBean> bulkUpdateData) {
		this.bulkUpdateData = bulkUpdateData;
	}

	public double getGpsSignalStrength() {
		return gpsSignalStrength;
	}

	/**
	 * toString method which prints the contents of the object
	 * @return
	 * The String which contains the values of all the member data
	 */
	@Override
	public String toString(){
		StringBuffer returnString = new StringBuffer();
		returnString.append("GWTrackModuleDataBean : [  GpsSignalStrength = ");
		returnString.append(gpsSignalStrength);
		returnString.append(", VehicleCourse =  ");
		returnString.append(vehicleCourse);
		returnString.append(", GsmSignalStrength = ");
		returnString.append(gsmSignalStrength);
		returnString.append(", NumberOfPacketSendingAttempts = ");
		returnString.append(numberOfPacketSendingAttempts);
		returnString.append(", NumberOfSuccessPackets = ");
		returnString.append(numberOfSuccessPackets);
		returnString.append(", ModuleBatteryVoltage = ");
		returnString.append(moduleBatteryVoltage);
		returnString.append(", Cumulative Distance = ");
		returnString.append(cumulativeDistance);
		returnString.append(", MaxSpeed = ");
		returnString.append(maxSpeed);
		returnString.append(", AD Value = ");
		returnString.append(analogue1);
		returnString.append(", Analogue = ");
		returnString.append(analogue2);
		returnString.append(", NumberOfErrorsInHardwareModule = ");
		returnString.append(numberOfErrorsInHardwareModule);
		returnString.append(", PingFlag = ");
		returnString.append(pingFlag);
		returnString.append(", ChargerConnected = ");
		returnString.append(chargerConnected);
		returnString.append(", MasterHardwareLevelRestart = ");
		returnString.append(masterHardwareLevelRestart);
		returnString.append(", ModuleCodeLevelRestart = ");
		returnString.append(moduleCodeLevelRestart);
		returnString.append(", MoreToFollow = ");
		returnString.append(moreToFollow);
		returnString.append(", PanicData = ");
		returnString.append(panicData);
		returnString.append(", FirmwareVersion = ");
		returnString.append(firmwareVersion);
		returnString.append(", IMEI = ");
		returnString.append(imei);
		returnString.append(", ModuleUpdateTime = ");
		returnString.append(moduleUpdateTime);
		for(int i=0; i< bulkUpdateData.size(); i++){
			returnString.append("\n BulkData");
			returnString.append(i+1);
			returnString.append(bulkUpdateData.get(i).toString());
			returnString.append(" \n");
		}
		returnString.append("]");
		return returnString.toString();
	}
		
	public void setGpsSignalStrength(double gpsSignalStrength) {
		this.gpsSignalStrength = gpsSignalStrength;
	}

	public double getVehicleCourse() {
		return vehicleCourse;
	}

	public void setVehicleCourse(double vehicleCourse) {
		this.vehicleCourse = vehicleCourse;
	}

	public double getGsmSignalStrength() {
		return gsmSignalStrength;
	}

	public void setGsmSignalStrength(double gsmSignalStrength) {
		this.gsmSignalStrength = gsmSignalStrength;
	}

	public long getNumberOfSuccessPackets() {
		return numberOfSuccessPackets;
	}

	public void setNumberOfSuccessPackets(long numberOfSuccessPackets) {
		this.numberOfSuccessPackets = numberOfSuccessPackets;
	}

	public int getPktCount() {
		return pktCount;
	}

	public void setPktCount(int pktCount) {
		this.pktCount = pktCount;
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

	public long getCumulativeDistance() {
		return cumulativeDistance;
	}

	public void setCumulativeDistance(long cumulativeDistance) {
		this.cumulativeDistance = cumulativeDistance;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
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

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public Date getModuleUpdateTime() {
		return moduleUpdateTime;
	}

	public void setModuleUpdateTime(Date moduleUpdateTime) {
		this.moduleUpdateTime = moduleUpdateTime;
	}
	
	public int getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(int firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public ModuleVersion getModuleVersion() {
		return moduleVersion;
	}

	public void setModuleVersion(ModuleVersion moduleVersion) {
		this.moduleVersion = moduleVersion;
	}

	public int getYear() {
		return year;
}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public boolean isMoreToFollow() {
		return moreToFollow;
	}

	public void setMoreToFollow(boolean moreToFollow) {
		this.moreToFollow = moreToFollow;
	}

	@Override
	public boolean equalsEntity(GWTrackModuleDataBean object) {
		// TODO Auto-generated method stub
		return false;
	}




}
