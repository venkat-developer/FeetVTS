package com.i10n.db.queryhelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.postgis.Geometry;
import org.postgis.Point;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.TrackHistoryDaoImpl;
import com.i10n.db.entity.IdlePoints;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.TrackHistory;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.container.BulkUpdateDataBean;
import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.EnvironmentInfo;


/**
 * 
 * Code to read data objects and push it into the database
 * 
 * @author Vishnu
 *
 */
public class PushToDBQueryHelper  {

	private static Logger LOG = Logger.getLogger(PushToDBQueryHelper.class);

	/**
	 * Query for updating track history
	 * @param tripId
	 * @param recordId
	 * @param nonBulk
	 * @param bulk
	 * @param isPing
	 * @param prevSqd
	 */
	public static String forTrackHistoryInsert (long tripId, long recordId, GWTrackModuleDataBean nonBulk, BulkUpdateDataBean bulk, boolean isPing, long prevSqd,
			float speed,float direction) {

		final StringBuffer queryBodyForTrackHistoryInsert = new StringBuffer(); 
		queryBodyForTrackHistoryInsert.append("insert into trackhistory (id,tripid,gpssignal,gsmsignal,direction,sqg,sqd,batteryvoltage,");
		queryBodyForTrackHistoryInsert.append(" cd,speed,lac,cid,isping,ismrs,ischargerconnected,isrestart,ispanic,");
		queryBodyForTrackHistoryInsert.append(" occurredat,location,fuel,distance,pingcounter," );
		queryBodyForTrackHistoryInsert.append(" analogue1,analogue2,error,digital1,digital2,digital3,gps_fix_information,airdistance) " );
		queryBodyForTrackHistoryInsert.append(" values (");
		queryBodyForTrackHistoryInsert.append(recordId);
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(tripId);
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getGpsSignalStrength());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getGsmSignalStrength());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(direction);
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getNumberOfSuccessPackets());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getNumberOfPacketSendingAttempts());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getModuleBatteryVoltage());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getCumulativeDistance());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(speed);
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getLocationAreaCode());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getCellId());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(isPing);
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.isMasterHardwareLevelRestart());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.isChargerConnected());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.isModuleCodeLevelRestart());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.isPanicData());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append("'");
		queryBodyForTrackHistoryInsert.append(DateUtils.convertJavaDateToSQLDate(bulk.getOccurredAt()));
		queryBodyForTrackHistoryInsert.append("'");
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append("GeometryFromText('POINT ("+ bulk.getLongitude() + " " + bulk.getLatitude() +")',-1)");
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(bulk.getFuel());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append((bulk.getDeltaDistance()/1000));
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(0);
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getAnalogue1());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getAnalogue2());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getNumberOfErrorsInHardwareModule());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append((nonBulk.getDigitalInput1().equals("0") ? false : true));
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append((nonBulk.getDigitalInput2().equals("0") ? false : true));
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append((nonBulk.getDigitalInput3().equals("0") ? false : true));
		queryBodyForTrackHistoryInsert.append(",");

		final boolean gpsFixAvailable = (bulk.getLatitude() != 0 && bulk.getLongitude() != 0
				&& nonBulk.getGpsSignalStrength() > 0 && nonBulk.getGpsSignalStrength() < 
				Float.parseFloat((EnvironmentInfo.getProperty("GPS_SIGNAL_STRENGTH_THRESHOLD"))));
		queryBodyForTrackHistoryInsert.append(gpsFixAvailable?1:0);
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(bulk.getDeltaDistance()/1000);// For Teltonika device air distance is same as delta distance
		queryBodyForTrackHistoryInsert.append(")");
		LOG.debug("Query framed "+queryBodyForTrackHistoryInsert.toString());

		return queryBodyForTrackHistoryInsert.toString();
	}

	/**
	 * Query for updating track history
	 * @param tripId
	 * @param recordId
	 * @param nonBulk
	 * @param bulk
	 * @param isPing
	 * @param prevSqd
	 */


	//	Airdistance Insertion Caliculation 


	public static String forTrackHistoryInsert (long tripId, long recordId, GWTrackModuleDataBean nonBulk, BulkUpdateDataBean bulk, boolean isPing, long prevSqd) {

		StringBuffer queryBodyForTrackHistoryInsert = new StringBuffer(); 
		queryBodyForTrackHistoryInsert.append("insert into trackhistory (id,tripid,gpssignal,gsmsignal,direction,sqg,sqd,batteryvoltage,");
		queryBodyForTrackHistoryInsert.append(" cd,speed,lac,cid,isping,ismrs,ischargerconnected,isrestart,ispanic,");
		queryBodyForTrackHistoryInsert.append(" occurredat,location,fuel,distance,pingcounter," );
		queryBodyForTrackHistoryInsert.append(" analogue1,analogue2,error,digital1,digital2,digital3,gps_fix_information,airdistance )" );
		queryBodyForTrackHistoryInsert.append(" values (");
		queryBodyForTrackHistoryInsert.append(recordId);
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(tripId);
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getGpsSignalStrength());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getGsmSignalStrength());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getVehicleCourse());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getNumberOfSuccessPackets());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getNumberOfPacketSendingAttempts());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getModuleBatteryVoltage());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getCumulativeDistance());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getMaxSpeed());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getLocationAreaCode());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getCellId());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(isPing);
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.isMasterHardwareLevelRestart());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.isChargerConnected());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.isModuleCodeLevelRestart());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.isPanicData());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append("'");
		queryBodyForTrackHistoryInsert.append(DateUtils.convertJavaDateToSQLDate(bulk.getOccurredAt()));
		queryBodyForTrackHistoryInsert.append("'");
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append("GeometryFromText('POINT ("+ bulk.getLongitude() + " " + bulk.getLatitude() +")',-1)");
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(bulk.getFuel());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append((bulk.getDeltaDistance()/1000));
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(0);
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getAnalogue1());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getAnalogue2());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(nonBulk.getNumberOfErrorsInHardwareModule());
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append((nonBulk.getDigitalInput1().equals("0") ? false : true));
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append((nonBulk.getDigitalInput2().equals("0") ? false : true));
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append((nonBulk.getDigitalInput3().equals("0") ? false : true));
		queryBodyForTrackHistoryInsert.append(",");

		boolean gpsFixAvailable = (bulk.getLatitude() != 0 && bulk.getLongitude() != 0
				&& nonBulk.getGpsSignalStrength() > 0 && nonBulk.getGpsSignalStrength() <
				Float.parseFloat((EnvironmentInfo.getProperty("GPS_SIGNAL_STRENGTH_THRESHOLD"))));

		queryBodyForTrackHistoryInsert.append(gpsFixAvailable?1:0);
		queryBodyForTrackHistoryInsert.append(",");
		queryBodyForTrackHistoryInsert.append(bulk.getAirDistance()/1000);
		queryBodyForTrackHistoryInsert.append(")");
		LOG.debug("Query framed "+queryBodyForTrackHistoryInsert.toString());

		return queryBodyForTrackHistoryInsert.toString();
	}

	/**
	 * Update TrackHistory Update
	 * @return
	 */
	public static String forTrackHistoryUpdate (long tripIdValue, long trackHistoryId, int pingCount, GWTrackModuleDataBean nonBulk, BulkUpdateDataBean bulk,float speed,float direction) {
		final StringBuffer updateIntoTrackhistory = new StringBuffer();
		updateIntoTrackhistory.append("Update Trackhistory set pingcounter = ");
		updateIntoTrackhistory.append(pingCount);
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" gpssignal = ");
		updateIntoTrackhistory.append(nonBulk.getGpsSignalStrength());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" gsmsignal = ");
		updateIntoTrackhistory.append(nonBulk.getGsmSignalStrength());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" direction = ");
		updateIntoTrackhistory.append(direction);
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" sqg = ");
		updateIntoTrackhistory.append(nonBulk.getNumberOfSuccessPackets());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" sqd = ");
		updateIntoTrackhistory.append(nonBulk.getNumberOfPacketSendingAttempts());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" batteryvoltage = ");
		updateIntoTrackhistory.append(nonBulk.getModuleBatteryVoltage());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" cd = ");
		updateIntoTrackhistory.append(nonBulk.getCumulativeDistance());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" speed = ");
		updateIntoTrackhistory.append(speed);
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" lac = ");
		updateIntoTrackhistory.append(nonBulk.getLocationAreaCode());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" cid = ");
		updateIntoTrackhistory.append(nonBulk.getCellId());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" ismrs = ");
		updateIntoTrackhistory.append(nonBulk.isMasterHardwareLevelRestart());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" ischargerconnected = ");
		updateIntoTrackhistory.append(nonBulk.isChargerConnected());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" isrestart = ");
		updateIntoTrackhistory.append(nonBulk.isModuleCodeLevelRestart());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" ispanic = ");
		updateIntoTrackhistory.append(nonBulk.isPanicData());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" location = ");
		updateIntoTrackhistory.append("GeometryFromText('POINT ("+ bulk.getLongitude() + " " + bulk.getLatitude() +")',-1)");
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" fuel = ");
		updateIntoTrackhistory.append(bulk.getFuel());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" distance = ");
		updateIntoTrackhistory.append((bulk.getDeltaDistance()/1000));
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" analogue1 = ");
		updateIntoTrackhistory.append(nonBulk.getAnalogue1());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" analogue2 = ");
		updateIntoTrackhistory.append(nonBulk.getAnalogue2());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" error = ");
		updateIntoTrackhistory.append(nonBulk.getNumberOfErrorsInHardwareModule());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" digital1 = ");
		updateIntoTrackhistory.append((nonBulk.getDigitalInput1().equals("0") ? false : true));
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" digital2 = ");
		updateIntoTrackhistory.append((nonBulk.getDigitalInput2().equals("0") ? false : true));
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" digital3 = ");
		updateIntoTrackhistory.append((nonBulk.getDigitalInput3().equals("0") ? false : true));
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" gps_fix_information = ");

		final boolean gpsFixAvailable = (bulk.getLatitude() != 0 && bulk.getLongitude() != 0
				&& nonBulk.getGpsSignalStrength() > 0 && nonBulk.getGpsSignalStrength() < 
				Float.parseFloat((EnvironmentInfo.getProperty("GPS_SIGNAL_STRENGTH_THRESHOLD"))));

		updateIntoTrackhistory.append(gpsFixAvailable?1:0);
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" airdistance = ");
		updateIntoTrackhistory.append(bulk.getDeltaDistance()/1000);
		updateIntoTrackhistory.append(", isping = true where id = ");
		updateIntoTrackhistory.append(trackHistoryId);

		LOG.debug("Query framed "+updateIntoTrackhistory.toString());

		return updateIntoTrackhistory.toString();
	}
	/**
	 * Update TrackHistory Update
	 * @return
	 */

	public static String forTrackHistoryUpdate (long tripIdValue, long trackHistoryId, int pingCount, GWTrackModuleDataBean nonBulk, BulkUpdateDataBean bulk) {
		final StringBuffer updateIntoTrackhistory = new StringBuffer();
		updateIntoTrackhistory.append("Update Trackhistory set pingcounter = ");
		updateIntoTrackhistory.append(pingCount);
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" gpssignal = ");
		updateIntoTrackhistory.append(nonBulk.getGpsSignalStrength());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" gsmsignal = ");
		updateIntoTrackhistory.append(nonBulk.getGsmSignalStrength());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" direction = ");
		updateIntoTrackhistory.append(nonBulk.getVehicleCourse());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" sqg = ");
		updateIntoTrackhistory.append(nonBulk.getNumberOfSuccessPackets());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" sqd = ");
		updateIntoTrackhistory.append(nonBulk.getNumberOfPacketSendingAttempts());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" batteryvoltage = ");
		updateIntoTrackhistory.append(nonBulk.getModuleBatteryVoltage());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" cd = ");
		updateIntoTrackhistory.append(nonBulk.getCumulativeDistance());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" speed = ");
		updateIntoTrackhistory.append(nonBulk.getMaxSpeed());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" lac = ");
		updateIntoTrackhistory.append(nonBulk.getLocationAreaCode());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" cid = ");
		updateIntoTrackhistory.append(nonBulk.getCellId());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" ismrs = ");
		updateIntoTrackhistory.append(nonBulk.isMasterHardwareLevelRestart());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" ischargerconnected = ");
		updateIntoTrackhistory.append(nonBulk.isChargerConnected());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" isrestart = ");
		updateIntoTrackhistory.append(nonBulk.isModuleCodeLevelRestart());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" ispanic = ");
		updateIntoTrackhistory.append(nonBulk.isPanicData());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" location = ");
		updateIntoTrackhistory.append("GeometryFromText('POINT ("+ bulk.getLongitude() + " " + bulk.getLatitude() +")',-1)");
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" fuel = ");
		updateIntoTrackhistory.append(bulk.getFuel());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" distance = ");
		updateIntoTrackhistory.append((bulk.getDeltaDistance()/1000));
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" analogue1 = ");
		updateIntoTrackhistory.append(nonBulk.getAnalogue1());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" analogue2 = ");
		updateIntoTrackhistory.append(nonBulk.getAnalogue2());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" error = ");
		updateIntoTrackhistory.append(nonBulk.getNumberOfErrorsInHardwareModule());
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" digital1 = ");
		updateIntoTrackhistory.append((nonBulk.getDigitalInput1().equals("0") ? false : true));
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" digital2 = ");
		updateIntoTrackhistory.append((nonBulk.getDigitalInput2().equals("0") ? false : true));
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" digital3 = ");
		updateIntoTrackhistory.append((nonBulk.getDigitalInput3().equals("0") ? false : true));
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" gps_fix_information = ");

		final boolean gpsFixAvailable = (bulk.getLatitude() != 0 && bulk.getLongitude() != 0
				&& nonBulk.getGpsSignalStrength() > 0 && nonBulk.getGpsSignalStrength() < 
				Float.parseFloat((EnvironmentInfo.getProperty("GPS_SIGNAL_STRENGTH_THRESHOLD"))));

		updateIntoTrackhistory.append(gpsFixAvailable?1:0);
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" airdistance = ");
		updateIntoTrackhistory.append(bulk.getAirDistance()/1000);
		updateIntoTrackhistory.append(", isping = true where id = ");
		updateIntoTrackhistory.append(trackHistoryId);

		LOG.debug("Query framed "+updateIntoTrackhistory.toString());

		return updateIntoTrackhistory.toString();
	}

	/**
	 * Update TrackHistory Update
	 * @return
	 *//*
	public static String forTrackHistoryUpdate (long tripIdValue, long trackHistoryId, int pingCount, GWTrackModuleDataBean nonBulk, BulkUpdateDataBean bulk,float speed,float direction) {
		final StringBuffer updateIntoTrackhistory = new StringBuffer();
        updateIntoTrackhistory.append("Update Trackhistory set pingcounter = ");
        updateIntoTrackhistory.append(pingCount);
        updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" gpssignal = ");
        updateIntoTrackhistory.append(nonBulk.getGpsSignalStrength());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" gsmsignal = ");
        updateIntoTrackhistory.append(nonBulk.getGsmSignalStrength());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" direction = ");
        updateIntoTrackhistory.append(direction);
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" sqg = ");
        updateIntoTrackhistory.append(nonBulk.getNumberOfSuccessPackets());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" sqd = ");
        updateIntoTrackhistory.append(nonBulk.getNumberOfPacketSendingAttempts());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" batteryvoltage = ");
        updateIntoTrackhistory.append(nonBulk.getModuleBatteryVoltage());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" cd = ");
        updateIntoTrackhistory.append(nonBulk.getCumulativeDistance());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" speed = ");
        updateIntoTrackhistory.append(speed);
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" lac = ");
        updateIntoTrackhistory.append(nonBulk.getLocationAreaCode());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" cid = ");
        updateIntoTrackhistory.append(nonBulk.getCellId());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" ismrs = ");
        updateIntoTrackhistory.append(nonBulk.isMasterHardwareLevelRestart());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" ischargerconnected = ");
        updateIntoTrackhistory.append(nonBulk.isChargerConnected());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" isrestart = ");
        updateIntoTrackhistory.append(nonBulk.isModuleCodeLevelRestart());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" ispanic = ");
        updateIntoTrackhistory.append(nonBulk.isPanicData());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" location = ");
        updateIntoTrackhistory.append("GeometryFromText('POINT ("+ bulk.getLongitude() + " " + bulk.getLatitude() +")',-1)");
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" fuel = ");
        updateIntoTrackhistory.append(bulk.getFuel());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" distance = ");
        updateIntoTrackhistory.append((bulk.getDeltaDistance()/1000));
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" analogue1 = ");
        updateIntoTrackhistory.append(nonBulk.getAnalogue1());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" analogue2 = ");
        updateIntoTrackhistory.append(nonBulk.getAnalogue2());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" error = ");
        updateIntoTrackhistory.append(nonBulk.getNumberOfErrorsInHardwareModule());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" digital1 = ");
        updateIntoTrackhistory.append((nonBulk.getDigitalInput1().equals("0") ? false : true));
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" digital2 = ");
        updateIntoTrackhistory.append((nonBulk.getDigitalInput2().equals("0") ? false : true));
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" digital3 = ");
        updateIntoTrackhistory.append((nonBulk.getDigitalInput3().equals("0") ? false : true));
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" gps_fix_information = ");

		final boolean gpsFixAvailable = (bulk.getLatitude() != 0 && bulk.getLongitude() != 0
				&& nonBulk.getGpsSignalStrength() > 0 && nonBulk.getGpsSignalStrength() < DataProcessorParameters.GPS_SIGNAL_STRENGTH_THRESHOLD);

		updateIntoTrackhistory.append(gpsFixAvailable?1:0);
		updateIntoTrackhistory.append(",");
		updateIntoTrackhistory.append(" distance = ");
		updateIntoTrackhistory.append(bulk.getDeltaDistance());
        updateIntoTrackhistory.append(", isping = true where id = ");
        updateIntoTrackhistory.append(trackHistoryId);

		LOG.debug("Query framed "+updateIntoTrackhistory.toString());

        return updateIntoTrackhistory.toString();
	}*/
	/*public static String forTrackHistoryUpdate (long tripIdValue, long trackHistoryId, int pingCount, GWTrackModuleDataBean nonBulk, BulkUpdateDataBean bulk) {
		StringBuffer updateIntoTrackhistory = new StringBuffer();
        updateIntoTrackhistory.append("Update Trackhistory set pingcounter = ");
        updateIntoTrackhistory.append(pingCount);
        updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" gpssignal = ");
        updateIntoTrackhistory.append(nonBulk.getGpsSignalStrength());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" gsmsignal = ");
        updateIntoTrackhistory.append(nonBulk.getGsmSignalStrength());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" direction = ");
        updateIntoTrackhistory.append(nonBulk.getVehicleCourse());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" sqg = ");
        updateIntoTrackhistory.append(nonBulk.getNumberOfSuccessPackets());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" sqd = ");
        updateIntoTrackhistory.append(nonBulk.getNumberOfPacketSendingAttempts());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" batteryvoltage = ");
        updateIntoTrackhistory.append(nonBulk.getModuleBatteryVoltage());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" cd = ");
        updateIntoTrackhistory.append(nonBulk.getCumulativeDistance());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" speed = ");
        updateIntoTrackhistory.append(nonBulk.getMaxSpeed());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" lac = ");
        updateIntoTrackhistory.append(nonBulk.getLocationAreaCode());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" cid = ");
        updateIntoTrackhistory.append(nonBulk.getCellId());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" ismrs = ");
        updateIntoTrackhistory.append(nonBulk.isMasterHardwareLevelRestart());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" ischargerconnected = ");
        updateIntoTrackhistory.append(nonBulk.isChargerConnected());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" isrestart = ");
        updateIntoTrackhistory.append(nonBulk.isModuleCodeLevelRestart());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" ispanic = ");
        updateIntoTrackhistory.append(nonBulk.isPanicData());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" location = ");
        updateIntoTrackhistory.append("GeometryFromText('POINT ("+ bulk.getLongitude() + " " + bulk.getLatitude() +")',-1)");
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" fuel = ");
        updateIntoTrackhistory.append(bulk.getFuel());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" distance = ");
        updateIntoTrackhistory.append((bulk.getDeltaDistance()/1000));
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" analogue1 = ");
        updateIntoTrackhistory.append(nonBulk.getAnalogue1());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" analogue2 = ");
        updateIntoTrackhistory.append(nonBulk.getAnalogue2());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" error = ");
        updateIntoTrackhistory.append(nonBulk.getNumberOfErrorsInHardwareModule());
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" digital1 = ");
        updateIntoTrackhistory.append((nonBulk.getDigitalInput1().equals("0") ? false : true));
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" digital2 = ");
        updateIntoTrackhistory.append((nonBulk.getDigitalInput2().equals("0") ? false : true));
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" digital3 = ");
        updateIntoTrackhistory.append((nonBulk.getDigitalInput3().equals("0") ? false : true));
		updateIntoTrackhistory.append(",");
        updateIntoTrackhistory.append(" gps_fix_information = ");

		boolean gpsFixAvailable = (bulk.getLatitude() != 0 && bulk.getLongitude() != 0
				&& nonBulk.getGpsSignalStrength() > 0 && nonBulk.getGpsSignalStrength() < DataProcessorParameters.GPS_SIGNAL_STRENGTH_THRESHOLD);

		updateIntoTrackhistory.append(gpsFixAvailable?1:0);
        updateIntoTrackhistory.append(", isping = true where id = ");
        updateIntoTrackhistory.append(trackHistoryId);

		LOG.debug("Query framed "+updateIntoTrackhistory.toString());

        return updateIntoTrackhistory.toString();
	}*/


	/**
	 * Insert IdlePoint query generation
	 * @return
	 */
	public static String forIdlePointInsert (LiveVehicleStatus lvos) {
		StringBuffer queryBodyForIdlePointsInsert = new StringBuffer();
		queryBodyForIdlePointsInsert.append("insert into idlePoints(id, tripid,idlelocation,locationname,starttime,endtime,rowcreated)");
		queryBodyForIdlePointsInsert.append(" values(");
		queryBodyForIdlePointsInsert.append(lvos.getTrackHistoryRowID());
		queryBodyForIdlePointsInsert.append(", ");
		queryBodyForIdlePointsInsert.append(lvos.getTripIdLong());
		queryBodyForIdlePointsInsert.append(", ");
		queryBodyForIdlePointsInsert.append("GeometryFromText('POINT ("+ lvos.getLocation().getFirstPoint().x + " " + lvos.getLocation().getFirstPoint().y +")',-1)");
		queryBodyForIdlePointsInsert.append(", ' ");
		// As of now location is not fetched while processing the packets
		/*if (DataProcessorParameters.IS_ADDRESS_FETCH_ENABLED) {
			Address addressBean = GeoUtils.fetchNearestLocationForSQLQuery(lvos.getVehicleLocation().getPoint(0).x, lvos.getVehicleLocation().getPoint(0).y);
			queryBodyForIdlePointsInsert.append(GeoUtils.removeSpecialCharacter(addressBean.toString()));
		}*/		
		queryBodyForIdlePointsInsert.append("','");
		queryBodyForIdlePointsInsert.append(DateUtils.convertJavaDateToSQLDate(lvos.getIdleStartTime()));
		queryBodyForIdlePointsInsert.append("','");
		queryBodyForIdlePointsInsert.append(DateUtils.convertJavaDateToSQLDate(lvos.getIdleEndTime()));
		queryBodyForIdlePointsInsert.append("',true)");

		LOG.debug("Query framed "+queryBodyForIdlePointsInsert.toString());

		return queryBodyForIdlePointsInsert.toString();
	}

	/**
	 * Update Idlepoint query generation
	 * @param tripIdValue
	 * @param pingCount
	 * @return
	 */
	public static String forIdlePointUpdate (LiveVehicleStatus lvos) {
		StringBuffer queryBodyForIdlePointsUpdate = new StringBuffer(); 
		queryBodyForIdlePointsUpdate.append("update IdlePoints set idlelocation = ");
		queryBodyForIdlePointsUpdate.append("GeometryFromText('POINT ("+ lvos.getLocation().getFirstPoint().x + " " + lvos.getLocation().getFirstPoint().y +")',-1)");
		queryBodyForIdlePointsUpdate.append(", endtime='");
		queryBodyForIdlePointsUpdate.append(DateUtils.convertJavaDateToSQLDate(lvos.getIdleEndTime()));
		queryBodyForIdlePointsUpdate.append("', locationname=' " );
		// As of now location is not fetched while processing the packets
		/*if (DataProcessorParameters.IS_ADDRESS_FETCH_ENABLED) {
			Address addressBean = GeoUtils.fetchNearestLocationForSQLQuery(lvos.getVehicleLocation().getPoint(0).x, lvos.getVehicleLocation().getPoint(0).y);
			queryBodyForIdlePointsUpdate.append(GeoUtils.removeSpecialCharacter(addressBean.toString()));
		}*/		
		queryBodyForIdlePointsUpdate.append("' where tripid = ");
		queryBodyForIdlePointsUpdate.append(lvos.getTripIdLong());
		queryBodyForIdlePointsUpdate.append(" and id = ");
		queryBodyForIdlePointsUpdate.append(lvos.getTrackHistoryRowID());

		LOG.debug("Query framed "+queryBodyForIdlePointsUpdate.toString());

		return queryBodyForIdlePointsUpdate.toString();
	}


	/**
	 * List of users to whom the vehicles are assigned
	 * @param vehicleId
	 * @return
	 */
	public static String getUsersToWhichTheVehicleIsAssigned(long vehicleId) {
		StringBuffer sqlQueryForUserList = new StringBuffer();
		sqlQueryForUserList.append("Select username from users where id in (Select userid from aclvehicle where vehicleid = ");
		sqlQueryForUserList.append(vehicleId);
		sqlQueryForUserList.append(")");
		LOG.debug("Query framed "+sqlQueryForUserList.toString());
		return sqlQueryForUserList.toString();
	}

	/**
	 * Query for inserting into alerts table
	 * @param recordId
	 * @param nonBulk
	 * @param bulk
	 * @param isPing
	 * @param prevSqd
	 */
	public static String forAlertInsert (long recordId, long vehicleId, GWTrackModuleDataBean nonBulk, String alert) {

		if (nonBulk.getBulkUpdateData().size()==0) {
			LOG.error("No packets but alerts found");
		}
		// Retrieve first location
		BulkUpdateDataBean bulk = nonBulk.getBulkUpdateData().get(0);

		StringBuffer queryBodyForAlertInsert = new StringBuffer(); 
		queryBodyForAlertInsert.append("Insert into alerts_status(id, location, vehicleid, time, occurredat, type) values(");
		queryBodyForAlertInsert.append(recordId);
		queryBodyForAlertInsert.append(",");
		queryBodyForAlertInsert.append("GeometryFromText('POINT ("+ bulk.getLongitude() + " " + bulk.getLatitude() +")',-1)");
		queryBodyForAlertInsert.append(",");
		queryBodyForAlertInsert.append(vehicleId);
		queryBodyForAlertInsert.append(",'");
		queryBodyForAlertInsert.append(DateUtils.convertJavaDateToSQLDate(bulk.getOccurredAt()));
		queryBodyForAlertInsert.append("','");
		queryBodyForAlertInsert.append(DateUtils.convertJavaDateToSQLDate(bulk.getOccurredAt()));
		queryBodyForAlertInsert.append("','");
		queryBodyForAlertInsert.append(alert);
		queryBodyForAlertInsert.append("')");
		LOG.debug("Query for alert framed "+queryBodyForAlertInsert.toString());

		return queryBodyForAlertInsert.toString();
	}

	/**
	 * @param vehicleId
	 * @param dataPacket
	 * @param bulkData
	 * @return Formulated query to be sent for Navteq
	 */
	public static String forNavteqInsertion(Long vehicleId, GWTrackModuleDataBean dataPacket, BulkUpdateDataBean bulkData) {
		StringBuffer queryForNavteqDataInsertion = new StringBuffer();
		queryForNavteqDataInsertion.append("Insert into locationdata values (");
		queryForNavteqDataInsertion.append(vehicleId);
		queryForNavteqDataInsertion.append(",");
		queryForNavteqDataInsertion.append(bulkData.getLatitude());
		queryForNavteqDataInsertion.append(",");
		queryForNavteqDataInsertion.append(bulkData.getLongitude());
		queryForNavteqDataInsertion.append(",");
		queryForNavteqDataInsertion.append(dataPacket.getVehicleCourse());
		queryForNavteqDataInsertion.append(",");
		queryForNavteqDataInsertion.append(dataPacket.getMaxSpeed());
		queryForNavteqDataInsertion.append(",'");
		queryForNavteqDataInsertion.append(convertJavaDateToNavteqDate(bulkData.getOccurredAt()));
		queryForNavteqDataInsertion.append("')");
		LOG.debug("Qurey Framed for Navteq Insertion is "+queryForNavteqDataInsertion);
		return queryForNavteqDataInsertion.toString();
	}

	public static String convertJavaDateToNavteqDate(Date occuredAt) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		final String utcTime = sdf.format(occuredAt);
		return utcTime;
		//return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").format(occuredAt);
	}


	public static TrackHistory formulateTeltonikaDataPacketTrackHistoryEntity(long tripId, GWTrackModuleDataBean nonBulk, BulkUpdateDataBean bulk, boolean isPing, long prevSqd,
			float speed,float direction) {
		TrackHistory trackHistory = new TrackHistory();
		TrackHistoryDaoImpl trackHistoryDao = ((TrackHistoryDaoImpl)DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO));
		trackHistory.setId(new LongPrimaryKey(trackHistoryDao.getTrackHistoryIdIncrementer().nextLongValue()));
		trackHistory.setTripid(tripId);
		trackHistory.setGpsSignal((float) nonBulk.getGpsSignalStrength());
		trackHistory.setGsmSignal((float) nonBulk.getGsmSignalStrength());
		trackHistory.setDirection(direction);
		trackHistory.setSqg(nonBulk.getNumberOfSuccessPackets());
		trackHistory.setSqd(nonBulk.getNumberOfPacketSendingAttempts());
		trackHistory.setCumulativeDistance(nonBulk.getCumulativeDistance());
		trackHistory.setBatteryVoltage((float) nonBulk.getModuleBatteryVoltage());
		trackHistory.setSpeed(speed);
		trackHistory.setLac(nonBulk.getLocationAreaCode());
		trackHistory.setCid(nonBulk.getCellId());
		trackHistory.setPing(isPing);
		trackHistory.setMrs(nonBulk.isMasterHardwareLevelRestart());
		trackHistory.setChargerConnected(nonBulk.isChargerConnected());
		trackHistory.setRestart(nonBulk.isModuleCodeLevelRestart());
		trackHistory.setPanic(nonBulk.isPanicData());
		trackHistory.setOccurredat(bulk.getOccurredAt());
		Point p=new Point(bulk.getLongitude(), bulk.getLatitude());
		Geometry location = (Geometry)p;
		trackHistory.setLocation(location);
		trackHistory.setFuel(bulk.getFuel());
		trackHistory.setDistance((float) (bulk.getDeltaDistance()/1000));
		trackHistory.setPingCounter(0);
		trackHistory.setAnalog1(nonBulk.getAnalogue1());
		trackHistory.setAnalog2(nonBulk.getAnalogue2());
		trackHistory.setError(nonBulk.getNumberOfErrorsInHardwareModule());
		trackHistory.setDigital1((nonBulk.getDigitalInput1().equals("0") ? false : true));
		trackHistory.setDigital2((nonBulk.getDigitalInput2().equals("0") ? false : true));
		trackHistory.setDigital3((nonBulk.getDigitalInput3().equals("0") ? false : true));
		final boolean gpsFixAvailable = (bulk.getLatitude() != 0 && bulk.getLongitude() != 0
				&& nonBulk.getGpsSignalStrength() > 0 && nonBulk.getGpsSignalStrength() < 
				Long.parseLong(EnvironmentInfo.getProperty("GPS_SIGNAL_STRENGTH_THRESHOLD")));
		trackHistory.setGpsFixInformation(gpsFixAvailable?1:0);
		trackHistory.setAirDistance((float) (bulk.getDeltaDistance()/1000));
		return trackHistory;
	}

	public static TrackHistory formulateDataPacketTrackHistoryEntity(LiveVehicleStatus liveVehicleObject,
			GWTrackModuleDataBean nonBulk, BulkUpdateDataBean bulk, boolean isPing, long prevSqd) {
		TrackHistory trackHistory = new TrackHistory();
		TrackHistoryDaoImpl trackHistoryDao = ((TrackHistoryDaoImpl)DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO));
		trackHistory.setId(new LongPrimaryKey(trackHistoryDao.getTrackHistoryIdIncrementer().nextLongValue()));
		trackHistory.setTripid(liveVehicleObject.getTripIdLong());
		trackHistory.setGpsSignal((float) nonBulk.getGpsSignalStrength());
		trackHistory.setGsmSignal((float) nonBulk.getGsmSignalStrength());
		trackHistory.setDirection((float) nonBulk.getVehicleCourse());
		trackHistory.setSqg(nonBulk.getNumberOfSuccessPackets());
		trackHistory.setSqd(nonBulk.getNumberOfPacketSendingAttempts());
		trackHistory.setCumulativeDistance(nonBulk.getCumulativeDistance());
		trackHistory.setBatteryVoltage((float) nonBulk.getModuleBatteryVoltage());
		trackHistory.setSpeed((float) nonBulk.getMaxSpeed());
		trackHistory.setLac(nonBulk.getLocationAreaCode());
		trackHistory.setCid(nonBulk.getCellId());
		trackHistory.setPing(isPing);
		trackHistory.setMrs(nonBulk.isMasterHardwareLevelRestart());
		trackHistory.setChargerConnected(nonBulk.isChargerConnected());
		trackHistory.setRestart(nonBulk.isModuleCodeLevelRestart());
		trackHistory.setPanic(nonBulk.isPanicData());
		trackHistory.setOccurredat(bulk.getOccurredAt());
		Point p=new Point(bulk.getLongitude(), bulk.getLatitude());
		Geometry location = (Geometry)p;
		trackHistory.setLocation(location);
		trackHistory.setFuel(bulk.getFuel());
		trackHistory.setDistance((float) (bulk.getDeltaDistance()/1000));
		trackHistory.setPingCounter(0);
		trackHistory.setAnalog1(nonBulk.getAnalogue1());
		trackHistory.setAnalog2(nonBulk.getAnalogue2());
		trackHistory.setError(nonBulk.getNumberOfErrorsInHardwareModule());
		trackHistory.setDigital1((nonBulk.getDigitalInput1().equals("0") ? false : true));
		trackHistory.setDigital2((nonBulk.getDigitalInput2().equals("0") ? false : true));
		trackHistory.setDigital3((nonBulk.getDigitalInput3().equals("0") ? false : true));
		final boolean gpsFixAvailable = (bulk.getLatitude() != 0 && bulk.getLongitude() != 0
				&& nonBulk.getGpsSignalStrength() > 0 && nonBulk.getGpsSignalStrength() < Long.parseLong(EnvironmentInfo.getProperty("GPS_SIGNAL_STRENGTH_THRESHOLD")));
		trackHistory.setGpsFixInformation(gpsFixAvailable?1:0);
		trackHistory.setAirDistance((float) (bulk.getAirDistance()/1000));
		trackHistory.setLatestButtonPressed(liveVehicleObject.getLatestButtonPressed());
		trackHistory.setButtonSequence(liveVehicleObject.getButtonSequence());
		
		return trackHistory;
	}

	public static LiveVehicleStatus formulateLiveVehicleStatusEntity(LiveVehicleStatus liveVehicleObject,
			GWTrackModuleDataBean nonBulk, BulkUpdateDataBean bulk, boolean isPing) {
		LOG.debug("In formulateLiveVehicleStatusEntity bulk OccurredAt is "+bulk.getOccurredAt());
		liveVehicleObject.setMrs((nonBulk.isMasterHardwareLevelRestart() ? 1 : 0));
		liveVehicleObject.setIdle(isPing);
		liveVehicleObject.setFuelAd(bulk.getFuel());
		liveVehicleObject.setCellId(nonBulk.getCellId());
		liveVehicleObject.setLocationAreaCode(nonBulk.getLocationAreaCode());
		liveVehicleObject.setRs((nonBulk.isModuleCodeLevelRestart() ? 1 : 0));
		return liveVehicleObject;
	}

	public static TrackHistory formulatePingPacketTrackHistoryEntity(LiveVehicleStatus liveVehicleObject,
			GWTrackModuleDataBean nonBulk, BulkUpdateDataBean bulk, double airDistance) {
		LOG.debug("Ping Counter is "+liveVehicleObject.getPingCount());
		TrackHistory trackHistory = new TrackHistory();
		trackHistory.setTripid(liveVehicleObject.getTripIdLong());
		trackHistory.setPingCounter(liveVehicleObject.getPingCount());
		trackHistory.setGpsSignal((float) nonBulk.getGpsSignalStrength());
		trackHistory.setGsmSignal((float) nonBulk.getGsmSignalStrength());
		trackHistory.setDirection(liveVehicleObject.getCourse());
		trackHistory.setSqg(nonBulk.getNumberOfSuccessPackets());
		trackHistory.setSqd(nonBulk.getNumberOfPacketSendingAttempts());
		trackHistory.setBatteryVoltage((float) nonBulk.getModuleBatteryVoltage());
		trackHistory.setCumulativeDistance(nonBulk.getCumulativeDistance());
		trackHistory.setSpeed(liveVehicleObject.getMaxSpeed());
		trackHistory.setLac(nonBulk.getLocationAreaCode());
		trackHistory.setCid(nonBulk.getCellId());
		trackHistory.setMrs(nonBulk.isMasterHardwareLevelRestart());
		trackHistory.setChargerConnected(nonBulk.isChargerConnected());
		trackHistory.setPing(true);
		trackHistory.setRestart(nonBulk.isModuleCodeLevelRestart());
		trackHistory.setPanic(nonBulk.isPanicData());
		trackHistory.setOccurredat(bulk.getOccurredAt());
		Point p=new Point(bulk.getLongitude(), bulk.getLatitude());
		Geometry location = (Geometry)p;
		trackHistory.setLocation(location);
		trackHistory.setFuel(bulk.getFuel());
		trackHistory.setDistance((float) (bulk.getDeltaDistance()/1000));
		//trackHistory.setPingCounter(0);
		trackHistory.setAnalog1(nonBulk.getAnalogue1());
		trackHistory.setAnalog2(nonBulk.getAnalogue2());
		trackHistory.setError(nonBulk.getNumberOfErrorsInHardwareModule());
		trackHistory.setDigital1((nonBulk.getDigitalInput1().equals("0") ? false : true));
		trackHistory.setDigital2((nonBulk.getDigitalInput2().equals("0") ? false : true));
		trackHistory.setDigital3((nonBulk.getDigitalInput3().equals("0") ? false : true));
		final boolean gpsFixAvailable = (bulk.getLatitude() != 0 && bulk.getLongitude() != 0
				&& nonBulk.getGpsSignalStrength() > 0 && nonBulk.getGpsSignalStrength() < Long.parseLong(EnvironmentInfo.getProperty("GPS_SIGNAL_STRENGTH_THRESHOLD")));
		trackHistory.setGpsFixInformation(gpsFixAvailable?1:0);
		trackHistory.setAirDistance((float) (airDistance));
		trackHistory.setLatestButtonPressed(liveVehicleObject.getLatestButtonPressed());
		trackHistory.setButtonSequence(liveVehicleObject.getButtonSequence());

		return trackHistory;
	}
	public static IdlePoints formulateIdlePointEntity(LiveVehicleStatus liveVehicleObject) {
		IdlePoints idlePoints = new IdlePoints();
		idlePoints.setTripid(liveVehicleObject.getTripIdLong());
		idlePoints.setIdleLocation(liveVehicleObject.getLocation());
		idlePoints.setLocationName("");
		idlePoints.setStarttime(liveVehicleObject.getIdleStartTime());
		idlePoints.setEndtime(liveVehicleObject.getIdleEndTime());
		idlePoints.setRowCreated(true);
		return idlePoints;
	}
}