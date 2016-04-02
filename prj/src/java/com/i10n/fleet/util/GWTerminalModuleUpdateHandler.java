package com.i10n.fleet.util;

import java.util.Date;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.GWLatestPacketDetailDaoImpl;
import com.i10n.db.entity.GWTerminalLatestPacketDetails;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.container.GWTerminalBulkUpdateBean;
import com.i10n.fleet.container.GWTerminalModuleDataBean;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

/**
 * @author joshua
 * 
 */
public class GWTerminalModuleUpdateHandler implements Runnable {

	private static final Logger LOG = Logger.getLogger(GWTerminalModuleUpdateHandler.class);

	private GWTerminalModuleDataBean gwterminalModuleDataBean;

	private Thread callbackThread;

	double gpsSignalStrength = 0.0, gsmSignalStrength = 0.0,
	batteryVoltage = 0.0;
	double vehicleBatteryVoltage = 0.0;

	boolean chargerConnected = true, moduleRestarted = false,
	moduleCharging = false;
	boolean pingFlag = false, statusFlag = false, masterRestart = false,
	d5Panic = false;
	double firmwareVersion = 5;
	int lac = 0, cellId = 0;
	long numberOfSuccessPackets = 0, numberOfPacketSendingAttempts = 0;
	Date currentDate = null;
	boolean isPingPacket = false;
	boolean previousPacketRepeated = false;
	boolean dataPacketAfterASeriesOfPingPackets = false;
	String imei = "";
	int analogue1;
	int analogue2;
	int analogue3;
	int analogue4;
	int analogue5;
	String digital = "";

	private GWLatestPacketDetailDaoImpl GwterminalLastestpacketdao = (GWLatestPacketDetailDaoImpl) DBManager
	.getInstance().getDao(DAOEnum.GT_LATEST_PACKET_DETAIL_DAO);

	/**
	 * Constructor which accepts the params from GWTModuleUpdateProcessor
	 * 
	 * @param gwterminal
	 *            -GWTerminalModuleDataBean which is going to be updated
	 * 
	 * @param callback
	 *            - callback thread for synchronization
	 */

	public GWTerminalModuleUpdateHandler(GWTerminalModuleDataBean gwterminal, Thread callback) {
		this.gwterminalModuleDataBean = gwterminal;
		this.callbackThread = callback;
	}

	@Override
	public void run() {
		try{
			checkandConvertNonBulkParameters(gwterminalModuleDataBean);
			GWTerminalLatestPacketDetails gwbulk = bulkDataUpdate(gwterminalModuleDataBean);
			if (gwbulk != null) {
				try {
					GwterminalLastestpacketdao.insert(gwbulk);
					LOG.debug("Values updated successfully for GWTerminal imei is"+ imei);
				} catch (OperationNotSupportedException e) {
					LOG.error("ERROR:While updating Values  for GWTerminal imei is"+ imei+"",e);
				}
				callbackThread.start();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * This method set the gwterminalModuleUpdateBean to the local values.It is
	 * called in the run method
	 * 
	 * @param gwterminalModuleUpdateBean
	 *            -update read from the client
	 */
	public void checkandConvertNonBulkParameters(GWTerminalModuleDataBean gwterminalModuleUpdateBean) {
		imei = gwterminalModuleUpdateBean.getImei();
		gsmSignalStrength = gwterminalModuleUpdateBean.getGSMStrength();
		numberOfPacketSendingAttempts = gwterminalModuleUpdateBean.getNumberOfPacketSendingAttempts();
		numberOfSuccessPackets = gwterminalModuleUpdateBean.getNumberOfSuccessPackets();
		batteryVoltage = gwterminalModuleUpdateBean.getModuleBatteryVoltage();
		moduleRestarted = gwterminalModuleUpdateBean.isModuleCodeLevelRestart();
		pingFlag = gwterminalModuleUpdateBean.isPingFlag();
		chargerConnected = gwterminalModuleUpdateBean.isChargerConnected();
		lac = gwterminalModuleUpdateBean.getLocationAreaCode();
		cellId = gwterminalModuleUpdateBean.getCellId();
		masterRestart = gwterminalModuleUpdateBean.isMasterHardwareLevelRestart();
		d5Panic = gwterminalModuleUpdateBean.isPanicData();
	}

	/**
	 * This function used to extract the bulk data from
	 * gwterminalModuleUpdateBean.
	 * 
	 * @param gwterminal
	 *            -its gwterminalModuleUpdateBean.
	 * @return-the GWTerminalLatestPacketDetails entity to the run method.
	 */

	public GWTerminalLatestPacketDetails bulkDataUpdate(GWTerminalModuleDataBean gwterminal) {
		GWTerminalLatestPacketDetails latest = null;
		for (int i = 0; i < gwterminal.getBulkUpdateData().size(); i++) {
			GWTerminalBulkUpdateBean gw = bulkDataFromIndex(i);
			if (gw != null) {
				latest = getLatestpacket();
			}
		}
		return latest;

	}

	/**
	 * This function used to extract the GWTerminalBulkUpdateBean data Using the
	 * index
	 * 
	 * @param indexOfbulkModule
	 *            -index of the GWTerminalBulkUpdateBean in that vector
	 * @return GWTerminalBulkUpdateBean
	 */
	public GWTerminalBulkUpdateBean bulkDataFromIndex(int indexOfbulkModule) {
		if (indexOfbulkModule > gwterminalModuleDataBean.getBulkUpdateData().size() - 1) {
			return null;
		} else {
			GWTerminalBulkUpdateBean gw = gwterminalModuleDataBean.getBulkUpdateData().get(indexOfbulkModule);
			LOG.debug("inside bulkDataFromIndex"+gw.toString());
			analogue1 = gw.getAnalogue1();
			analogue2 = gw.getAnalogue2();
			analogue3 = gw.getAnalogue3();
			analogue4 = gw.getAnalogue4();
			analogue5 = gw.getAnalogue5();
			digital = gw.getDigitalinput();
			currentDate = gw.getOccurredAt();
			return gw;
		}
	}

	/***
	 * This function will return the latest packet details from the last update
	 * 
	 * @return
	 */

	public GWTerminalLatestPacketDetails getLatestpacket() {
		GWTerminalLatestPacketDetails latestPacketDetail = new GWTerminalLatestPacketDetails();
		latestPacketDetail.setImei(imei);
		latestPacketDetail.setGsmStrength((float) gsmSignalStrength);
		latestPacketDetail.setSqd(numberOfSuccessPackets);
		latestPacketDetail.setSqg(numberOfPacketSendingAttempts);
		latestPacketDetail.setBatteryVoltage((float) batteryVoltage);
		latestPacketDetail.setLac(lac);
		latestPacketDetail.setCellId(cellId);
		latestPacketDetail.setChargerConnected(chargerConnected);
		latestPacketDetail.setMrs(masterRestart);
		latestPacketDetail.setRs(moduleRestarted);
		latestPacketDetail.setAnalogue1(analogue1);
		latestPacketDetail.setAnalogue2(analogue2);
		latestPacketDetail.setAnalogue3(analogue3);
		latestPacketDetail.setAnalogue4(analogue4);
		latestPacketDetail.setAnalogue5(analogue5);
		latestPacketDetail.setDigitalinput(digital);
		latestPacketDetail.setOccuredat(currentDate);
		return latestPacketDetail;
	}
}
