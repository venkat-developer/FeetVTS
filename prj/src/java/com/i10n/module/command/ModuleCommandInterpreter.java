
package com.i10n.module.command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.postgis.Geometry;
import org.postgis.Point;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.LiveVehicleStatusDaoImp;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.AlertOrViolation.AlertType;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;
import com.i10n.module.dataprocessor.BytesPosition;
import com.i10n.module.dataprocessor.ModuleCommandProcessor;
import com.i10n.module.dataprocessor.ModuleDataException;

/**
 * 
 * @author Praveen DS (c) i10n 2009
 * @author Dharmaraju V
 */
public class ModuleCommandInterpreter {
	private static Logger LOG = Logger
			.getLogger(ModuleCommandInterpreter.class);
	public static byte COMMAND_SWITCH = 0;

	public ModuleCommandInterpreter() {

	}

	public CommandInterpretationResult interpretCommandFromStream( IoBuffer dis) {
		byte crc = 0; // This is the precondition for a command
		ICommandBean cmd = null;
		IResponse resp = new DefaultSuccessResponse();
		ByteArrayInputStream bis = null;
		DataInputStream tdis = null;

		try {
			short command = dis.getShort();
			byte cb1 = (byte) ((command >> 8) & (byte) 0xff);
			byte cb2 = (byte) (command & (byte) 0xff);
			crc ^= cb1;
			crc ^= cb2;

			LOG.info("Command ID : " + command);

			switch (Command.get(command)) {

			case SEQUENCEREQUEST: {
				LOG.debug("Sequence Request Command");
				byte[] requestBytes = checkCRC(crc, dis, Command.getLengthForCommand(Command.SEQUENCEREQUEST));
				bis = new ByteArrayInputStream(requestBytes);
				tdis = new DataInputStream(bis);
				byte[] imeiBytes = new byte[15];
				tdis.read(imeiBytes);
				String requestedImei = new String(imeiBytes);
				cmd = new SequenceRequestCommandBean(requestedImei);
				LOG.debug(cmd.toString() + " " + "Imei:" + requestedImei);
			}
			break;

			case ETA_GUJRAT: {
				long routeId = dis.getShort();
				long stopId = dis.getShort();
				LOG.debug("ETAETAETAETAETA busId :" + routeId);
				LOG.debug("ETAETAETAETAETA stopId :" + stopId);
				cmd = new ETAGujratCommand(routeId,stopId);
			}
			break;

			case ETA_BHOPAL_CHINESE:{
				LOG.debug("ETAETAETAETAETACHINESE Received Command for ETA Bhopal. ");
				long busStopId = dis.getShort();
				LOG.debug("ETAETAETAETAETACHINESE BusStopId : "+busStopId);
				int languageFlag = unsigned(dis.get());
				cmd = new ETAChineseCommand(busStopId, languageFlag);
			}
			break;

			case ETA_BHOPAL_ARYA:{
				LOG.debug("ETAETAETAETAETAARYA Received Command for ETA Bhopal. ");
				long busStopId = dis.getShort();
				int languageFlag = unsigned(dis.get());
				byte[] imeiBytes = new byte[15];
				dis.get(imeiBytes);
				String IMEI = new String(imeiBytes);
				cmd = new ETAAryaCommand(busStopId, languageFlag, IMEI);
				LOG.info("ETA_BHOPAL_ARYA Request : "+cmd.toString());
			}
			break;

			case ALERT_OR_VIOLATION : {
				LOG.debug("ALERTS/VIOLATIONS : Received Alert command dump is "+dis.getHexDump());
				byte[] imeiBytes = new byte[15];
				dis.get(imeiBytes);
				String IMEI = new String(imeiBytes);
				int alertType = dis.getShort();
				double latitude = dis.getInt() / 10000000.0;
				double longitude = dis.getInt() / 10000000.0;
				Date alertTime = new Date(dis.getLong());
				AlertOrViolationCommand alert = new AlertOrViolationCommand(IMEI, AlertType.get(alertType), latitude, longitude, alertTime);
				// If the alerts is of SOS types then get the number to/from which the alert is initiated
				if(alert.getAlertType() == AlertType.OVERSPEED || alert.getAlertType() == AlertType.BATTERY_LOW ||
						alert.getAlertType() == AlertType.SOS_CALL_IN || alert.getAlertType() == AlertType.SOS_CALL_OUT){
					alert.setAlertTypeValue(dis.getLong());
				}
				cmd = alert;

				// This is a snippet for updating live vehicle status with the alert packet update.  
				try{
					LOG.debug("UPDATE_LVS_FROM_ALERTPACKET : Trying to update LiveVehicleStatus from Alert packet data update");
					if((latitude > 90 || latitude < -90) || (longitude > 180 || longitude < -180)){
						LOG.error("UPDATE_LVS_FROM_ALERTPACKET : Invalid lat long values. Hence not updating LiveVehicleStatus");
					} else {
						updateLVSWithAlertPacketData(IMEI, latitude, longitude, alertTime);
					}
					LOG.debug("UPDATE_LVS_FROM_ALERTPACKET : Successfully updated LiveVehicleStatus with Alert Packet data");
				} catch (Exception e){
					LOG.error("UPDATE_LVS_FROM_ALERTPACKET : Error while updating live vehicle status cache",e);
				}
				LOG.debug("ALERTS/VIOLATIONS : "+cmd.toString());

			}
			break;

			case MBMC_DYNAMIC_ROUTE_ASSIGN : {
				LOG.debug("MBMC : Command for Dynamic route assignment");
				byte[] imeiBytes = new byte[15];
				dis.get(imeiBytes);
				String IMEI = new String(imeiBytes);
				long routeId = dis.getLong();
				byte[] scheduleTime = new byte[5];
				dis.get(scheduleTime);

				int routeScheduleHours = Integer.parseInt(new String(scheduleTime, 0, 2));
				int routeScheduleMinutes = Integer.parseInt(new String(scheduleTime, 3, 2));
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR, routeScheduleHours);
				cal.set(Calendar.MINUTE, routeScheduleMinutes);
				Date routeScheduleTime = cal.getTime();

				double latitude = dis.getInt() / 10000000.0;
				double longitude = dis.getInt() / 10000000.0;
				Date packetTime = new Date(dis.getLong());
				cmd = new MBMCDynamicRouteAssignmentCommand(IMEI, routeId, routeScheduleTime, latitude, longitude, packetTime);
				LOG.info("MBMC : "+cmd.toString());

			}
			break;

			case DEFAULT : {
				LOG.debug("Unknown Commandid received");
				cmd = new DefaultCommand();
			}
			default:
				// By default send Success only.
			}
		} catch (IOException e) {
			LOG.debug("Unable to interpret command", e);
			resp = new DefaultFailureResponse();
		} catch (ModuleDataException e) {
			LOG.debug("Unable to interpret command", e);
			resp = new DefaultFailureResponse();
		} finally {
			IOUtils.closeQuietly(tdis);
			IOUtils.closeQuietly(bis);
			// dis should be closed by the calling function
		}
		// Process the command

		resp = ModuleCommandProcessor.processCommand(cmd);

		return new CommandInterpretationResult(cmd, resp);
	}

	private void updateLVSWithAlertPacketData(String IMEI, double latitude,
			double longitude, Date alertTime) {
		LiveVehicleStatus liveVehicleStatus = LoadLiveVehicleStatusRecord.getInstance().retrieve(IMEI);
		
		if(liveVehicleStatus != null){
			
			if (latitude == 0 || longitude == 0){
				LOG.debug("UPDATE_LVS_FROM_ALERTPACKET : Lat Long values are 0. Hence not updating location details");
			} else {
				Point point = new Point(longitude, latitude);
				Geometry location = (Geometry)point;
				liveVehicleStatus.setLocation(location);
			}
			if(alertTime.getTime() > liveVehicleStatus.getLastUpdatedAt().getTime() ){
				LOG.info("Alert packet is older last updated packet time");
				liveVehicleStatus.setLastUpdatedAt(alertTime);
			}
			liveVehicleStatus.setModuleUpdateTime(new Date());
			try{
				((LiveVehicleStatusDaoImp)DBManager.getInstance().getDao(DAOEnum.LIVE_VEHICLE_STATUS_DAO)).updateFromAlertPacket(liveVehicleStatus);
			} catch(Exception e){
				LOG.error("UPDATE_LVS_FROM_ALERTPACKET : Error while updating alert time to Livevehiclestatus DB",e);
			}

			LoadLiveVehicleStatusRecord.store(IMEI, liveVehicleStatus);

		} else {
			LOG.debug("UPDATE_LVS_FROM_ALERTPACKET : LiveVehicle status doesnt exist for the IMEI : "+IMEI);
		}
	}

	public static int unsigned(byte b) {
		return b & 0xFF;
	}

	/**
	 * Java can handle only network byte order or high endian in a stream, not
	 * little endian. Hence this function
	 * 
	 * @param bp
	 * @param offset
	 * @param dis
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private int processLittleEndianInt(BytesPosition bp, int offset,
			DataInputStream dis) throws IOException {
		if (bp.getLength() != 4) {
			throw new IOException("An int is 4 bytes long, but the passed value is ["+ bp.getLength() + "] long");
		}
		int[] temp = new int[bp.getLength()];
		int t = 0;
		for (int i = 0; i < bp.getLength(); i++) {
			temp[i] = unsigned(dis.readByte());
		}
		for (int i = bp.getLength() - 1; i >= 0; i--) {
			int k = temp[i];
			k <<= i * 8;
			t += k;
		}
		return t;
	}

	public byte[] checkCRC(byte crc, IoBuffer dis, int length)
			throws ModuleDataException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			for (int i = 0; i < length; i++) {
				dos.write(dis.get());
			}
		} catch (IOException e) {
			LOG.error("Exception while reading the bytes in CRC of smartcard Handler",e);
		} finally {
			IOUtils.closeQuietly(baos);
			IOUtils.closeQuietly(dos);
		}
		byte[] toTest = baos.toByteArray();
		for (int i = 0; i < length - 1; i++) {
			crc ^= toTest[i];
		}
		if (crc != toTest[length - 1]) {
			throw new ModuleDataException("CRC from module ["+ toTest[length - 1] + "] != calculated CRC [" + crc + "]",ModuleDataException.ErrorCode.CRC_FAILED);
		}
		LOG.debug("crc check" + toTest);
		return toTest;
	}
}