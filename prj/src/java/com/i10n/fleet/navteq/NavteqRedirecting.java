/*package com.i10n.fleet.navteq;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;

import com.i10n.dbCacheManager.LoadNavteqIMEIVehicleIdMap;
import com.i10n.fleet.container.BulkUpdateDataBean;
import com.i10n.fleet.container.GWTrackModuleDataBean;
import com.i10n.fleet.hashmaps.NavteqHashMaps;
import com.i10n.module.dataprocessor.PacketProcessingException;
import com.i10n.navteq.NavteqIMEIData;

*//**
 * Sends the live data to the NAVTEQ server
 * 
 * @author Venkat
 * @Update and Reviewed  Dharmaraju V
 *
 *//*
public class NavteqRedirecting{
	
	//	Logger for this class
	private static final Logger LOG = Logger.getLogger(NavteqRedirecting.class);

	private static NavteqRedirecting _instance = null;

	public static NavteqRedirecting getInstance(){
		if(_instance == null){
			_instance = new NavteqRedirecting();
		}
		return _instance;
	}


	public void processData(GWTrackModuleDataBean dataPacket, BulkUpdateDataBean bulkdata, Statement statement, 
			int virtualVehicleIdIndex) {
		ArrayList<Long> vehicleIdList = LoadNavteqIMEIVehicleIdMap.getInstance().retrieve(dataPacket.getImei());
		if(vehicleIdList != null) {
			try{
				long vehicleId = vehicleIdList.get(virtualVehicleIdIndex);
				Date date = new Date();				 								//date for checking count for the day
				String day = convertJavaDateToNavteqDate(date);

				saveDataToPushToNavteq(dataPacket, vehicleId, day, bulkdata, statement, virtualVehicleIdIndex);
			}catch (Exception e) {
				try {
					throw new PacketProcessingException (PacketProcessingException.PacketErrorCode.NAVTEQ_IMEI);
				} catch (PacketProcessingException e1) {
					LOG.error(e1);
				}
			}
		}
	}

	*//**
	 * Deciding on the vehicle Id to insert for IMEI and inserting the final data to DB
	 * 
	 * @param dataPacket
	 * @param vehicleId
	 * @param currentDay
	 * @param bulkdata
	 * @param statement
	 * @param virtualVehicleIdIndex
	 *//*
	private void saveDataToPushToNavteq(GWTrackModuleDataBean dataPacket, long vehicleId, String currentDay, 
			BulkUpdateDataBean bulkdata, Statement statement, int virtualVehicleIdIndex) {
		LOG.debug("Navteq : saveDataToPushToNavteq");
		ArrayList<NavteqIMEIData> navteqIMEIDataList = NavteqHashMaps.getInstance().imeiDataMap.get(dataPacket.getImei());
		if (navteqIMEIDataList == null) {
			LOG.debug("Navteq : Entry not there for the day, Hence creating one for IMEI : "+dataPacket.getImei());
			//	Entry not there for the day, Hence creating one 
			saveIMEIVIDDataIntoHashMap(dataPacket.getImei(), vehicleId, currentDay);
			insertData(vehicleId,dataPacket,bulkdata,statement);
		} else {
			LOG.debug("Navteq : Entry there for the day for the "+dataPacket.getImei());
			//	Entry there for the day for the IMEI 
			int numberOfRows = navteqIMEIDataList.size();
			if (numberOfRows > 250) {
				LOG.debug("Navteq : Number of rows greater than 250 for IMEI : "+dataPacket.getImei());
				//	Empty the cache for this IMEI-VID pair 
				NavteqHashMaps.getInstance().imeiDataMap.remove(dataPacket.getImei());
				//	Get next vehicleId
				virtualVehicleIdIndex = virtualVehicleIdIndex + 1;
				processData(dataPacket,bulkdata,statement,virtualVehicleIdIndex);
			} else {
				LOG.debug("Navteq : Number of rows less than 250 for IMEI : "+dataPacket.getImei());
				saveIMEIVIDDataIntoHashMap(dataPacket.getImei(), vehicleId, currentDay);
				insertData(vehicleId,dataPacket,bulkdata,statement);
			}
		}
	}


	*//**
	 * Cache the current data received
	 * @param imei
	 * @param vehicleId
	 * @param currentDay
	 *//*
	private void saveIMEIVIDDataIntoHashMap(String imei, long vehicleId, String currentDay) {
		LOG.debug("Navteq : saveIMEIVIDDataIntoHashMap");
		NavteqIMEIData navteqIMEIData = new NavteqIMEIData(imei, vehicleId, currentDay);
		ArrayList<NavteqIMEIData> navteqIMEIDataList = NavteqHashMaps.getInstance().imeiDataMap.get(imei);
		if(navteqIMEIDataList == null){
			navteqIMEIDataList = new ArrayList<NavteqIMEIData>();
		}
		navteqIMEIDataList.add(navteqIMEIData);
		NavteqHashMaps.getInstance().imeiDataMap.put(imei, navteqIMEIDataList);
	}

	*//**
	 * Insert the data into DB for further processing (writing into CSV and sending it to server)
	 * @param vehicleid
	 * @param dataPacket
	 * @param bulkdata
	 * @param statement
	 *//*
	private void insertData(double vehicleid,GWTrackModuleDataBean dataPacket, BulkUpdateDataBean bulkdata,Statement statement) {
		LOG.debug("Navteq : Inserting in DB ");
		try{
			String insertQuery = "insert  into locationdata values(" + vehicleid+" , "+bulkdata.getLatitude() +" , "+bulkdata.getLongitude()+" , "+dataPacket.getVehicleCourse()+" , "+dataPacket.getMaxSpeed()+" , '"+convertJavaDateToNavteqDateToInsert(bulkdata.getOccurredAt())+"' )";
			LOG.debug(" Navteq::::::: Inserting query " + insertQuery);
			statement.executeUpdate(insertQuery);
		}catch (SQLException e) {
			LOG.error(" Navteq::::::: Error While Inserting Into LocationData",e); 
		}
	}		

	public String convertJavaDateToNavteqDate(Date date) {
		return new SimpleDateFormat("MM/dd/yyyy").format(date);
	}
	
	public static String convertJavaDateToNavteqDateToInsert(Date occuredAt) {
		String utcTime=DateFormatUtils.formatUTC(occuredAt, DateFormatUtils.ISO_DATETIME_TIME_ZONE_FORMAT.getPattern());
		return utcTime;
	}
	
}

*/