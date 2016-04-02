/**
 * 
 */
package com.i10n.fleet.util;

import java.util.Date;

import com.i10n.db.dao.AccelerometerLatestPacketDetailDAOImpl;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.entity.ACModuleDataBean;
import com.i10n.db.entity.AccelerometerLatestPacketDetail;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.container.BulkUpdateDataBean;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

/**
 * @author joshua
 *
 */
public class AccelerometerModuleUpdateHandler {
	
	private ACModuleDataBean acModuleDataBean;
	
	double latitude = 0.0, longitude = 0.0, maxSpeed = 0.0, distance = 0.0;
	double gpsSignalStrength = 0.0, gsmSignalStrength = 0.0,
	batteryVoltage = 0.0;
	double vehicleBatteryVoltage = 0.0;

	boolean chargerConnected = true, moduleRestarted = false,
	moduleCharging = false;
	boolean pingFlag = false, statusFlag = false, masterRestart = false,
	d5Panic = false;
	double firmwareVersion = 6;
	int lac = 0, cellId = 0;
	long numberOfSuccessPackets = 0, numberOfPacketSendingAttempts = 0;
	Date currentDate = null;
	long cumulativeDistance = 0;
	boolean isPingPacket = false;
	boolean previousPacketRepeated = false;
	boolean dataPacketAfterASeriesOfPingPackets = false;
	String imei = "";
	double fuelInLitres = 0.0;
	int xmin;
	int xmax;
	int ymin;
	int ymax;
	int zmin;
	int zmax;
	double vehicleCourse = 0.0, ad = 0.0;
	
	private AccelerometerLatestPacketDetailDAOImpl latestpacketDao=(AccelerometerLatestPacketDetailDAOImpl)DBManager.getInstance().getDao(DAOEnum.AC_LATEST_PACKET_DETAIL_DAO);
	
	public AccelerometerModuleUpdateHandler(ACModuleDataBean acModuleDataBean){
		this.acModuleDataBean=acModuleDataBean;
	}
	
	public void UpdatetoDb(){
		checkandConvertNonBulkParameters(acModuleDataBean);
		AccelerometerLatestPacketDetail aclatest=bulkDataUpdate(acModuleDataBean);
		if(aclatest!=null){
			try {
				latestpacketDao.insert(aclatest);
				System.out
				.println("Values updated successfully for Accelerometer imei is"
						+ imei);
			} catch (OperationNotSupportedException e) {
				// TODO Auto-generated catch block
				System.out
				.println("ERROR:While updating Values  for Accelerometer imei is"
						+ imei);
				e.printStackTrace();
			}

		
		}
		
	}
	
	public void checkandConvertNonBulkParameters(
			ACModuleDataBean ModuleUpdateBean) {
		imei = ModuleUpdateBean.getImei();
		gsmSignalStrength = ModuleUpdateBean.getGsmSignalStrength();
		gpsSignalStrength=ModuleUpdateBean.getGpsSignalStrength();
		vehicleCourse=ModuleUpdateBean.getVehicleCourse();
		numberOfPacketSendingAttempts = ModuleUpdateBean
		.getNumberOfPacketSendingAttempts();
		numberOfSuccessPackets = ModuleUpdateBean
		.getNumberOfSuccessPackets();
		batteryVoltage = ModuleUpdateBean.getModuleBatteryVoltage();
		moduleRestarted = ModuleUpdateBean.isModuleCodeLevelRestart();
		pingFlag = ModuleUpdateBean.isPingFlag();
		
		chargerConnected = ModuleUpdateBean.isChargerConnected();
		lac = ModuleUpdateBean.getLocationAreaCode();
		cellId = ModuleUpdateBean.getCellId();
		masterRestart = ModuleUpdateBean
		.isMasterHardwareLevelRestart();
		cumulativeDistance = (ModuleUpdateBean.getCumulativeDistance())/1000;
		d5Panic = ModuleUpdateBean.isPanicData();
		maxSpeed = ModuleUpdateBean.getMaxSpeed();
		xmin=ModuleUpdateBean.getXmin();
		xmax=ModuleUpdateBean.getXmax();
		ymin=ModuleUpdateBean.getYmin();
		ymax=ModuleUpdateBean.getYmax();
		zmin=ModuleUpdateBean.getZmin();
		zmax=ModuleUpdateBean.getZmax();
		
	}
	public AccelerometerLatestPacketDetail bulkDataUpdate(
			ACModuleDataBean AcModule) {
		AccelerometerLatestPacketDetail latest = null;
		for (int i = 0; i < AcModule.getBulkUpdateData().size(); i++) {
		  BulkUpdateDataBean gw = bulkDataFromIndex(i);
			if (gw != null) {
				latest = getLatestpacket();
			}
		}
		return latest;

	}
	public BulkUpdateDataBean bulkDataFromIndex(int indexOfbulkModule) {


		if (indexOfbulkModule > acModuleDataBean.getBulkUpdateData()
				.size() - 1) {
			return null;

		} else {

			BulkUpdateDataBean bulkUpdateDataBean = acModuleDataBean.getBulkUpdateData().get(indexOfbulkModule);
			latitude = bulkUpdateDataBean.getLatitude();
			longitude = bulkUpdateDataBean.getLongitude();
			distance = bulkUpdateDataBean.getDeltaDistance();
			distance = distance / 1000;// Converting to Kilometers from Metres.
			ad = bulkUpdateDataBean.getFuel();
			currentDate = bulkUpdateDataBean.getOccurredAt();

			return bulkUpdateDataBean;
		}


	}
	
	public AccelerometerLatestPacketDetail getLatestpacket() {
		AccelerometerLatestPacketDetail latestPacketDetail = new AccelerometerLatestPacketDetail();
		latestPacketDetail.setImei(imei);
		latestPacketDetail.setGsmSignal(((float) gsmSignalStrength));
		latestPacketDetail.setGpsSignal((float)gpsSignalStrength);
		latestPacketDetail.setSqd(numberOfSuccessPackets);
		latestPacketDetail.setSqg(numberOfPacketSendingAttempts);
		latestPacketDetail.setBatteryVoltage((float) batteryVoltage);
		latestPacketDetail.setLac(lac);
		latestPacketDetail.setCid(cellId);
		latestPacketDetail.setChargerConnected(chargerConnected);
		latestPacketDetail.setMrs(masterRestart);
		latestPacketDetail.setRestart(moduleRestarted);
		latestPacketDetail.setCumulativeDistance(cumulativeDistance);
		latestPacketDetail.setLat(latitude);
		latestPacketDetail.setLng(longitude);
		latestPacketDetail.setDistance((float)distance);
		latestPacketDetail.setFuel(ad);
		latestPacketDetail.setOccurredat(currentDate);
		latestPacketDetail.setXmin(xmin);
		latestPacketDetail.setXmax(xmax);
		latestPacketDetail.setYmin(ymin);
		latestPacketDetail.setYmax(ymax);
		latestPacketDetail.setZmin(zmin);
		latestPacketDetail.setZmin(zmin);
		latestPacketDetail.setSpeed((float)maxSpeed);

		return latestPacketDetail;
	}
	
}
