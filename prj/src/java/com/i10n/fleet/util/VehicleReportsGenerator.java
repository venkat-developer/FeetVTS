package com.i10n.fleet.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.MailinglistReportDaoImp;
import com.i10n.db.dao.TrackHistoryDaoImpl;
import com.i10n.db.dao.VehicleDaoImpl;
import com.i10n.db.entity.DateRange;
import com.i10n.db.entity.DriverReport;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.MailingListReport;
import com.i10n.db.entity.TrackHistory;
import com.i10n.db.entity.User;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;
import com.i10n.dbCacheManager.LoadUserDetails;

public class VehicleReportsGenerator {

	private static Logger LOG = Logger.getLogger(VehicleReportsGenerator.class);

	@SuppressWarnings("deprecation")
	public static String createVehicleStatisticsReport(DateRange dateRange, long reportId, Long userId) {

		Writer output = null;
		Date date = new Date();

		String path = "/usr/local/tomcat6/temp/";

		File directory = new File(path);
		directory.mkdir();
		LOG.debug("directory created");
		directory = new File(path);
		directory.mkdir();
		// If any one changes path here please change the path in MailUtils.java too
		StringBuilder vehicleStatisticsPath = new StringBuilder();
		vehicleStatisticsPath.append("VehicleStatistics_").append(date.getDate()).append("_");
		vehicleStatisticsPath.append((date.getMonth() + 1)).append("_").append(date.getYear() + 1900).append(".txt");
		File file = new File(path + "/" + vehicleStatisticsPath.toString());

		try {
			file.createNewFile();
			LOG.debug("File has been created");
			output = new BufferedWriter(new FileWriter(file));
			String header = "Vehicle Statistics for the duration from "+ dateRange.getStart() + " to " + dateRange.getEnd()+ "\n\n";
			String str = header+ "Imei , Make, Model, Year, Total Distance, Max Speed, Avg Speed, Start Fuel, End Fuel";
			output.write(str);

			User user = LoadUserDetails.getInstance().retrieve(userId);
			if(user == null){
				try{
					if(output != null){
						output.close();
					}
				} catch (Exception e){
					LOG.error(e);
				}
				return "";
			}
			List<Vehicle> vehicles = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).
					getReportAssignedVehicles(reportId, user.getGroupId());

			if (vehicles != null) {
				for (int i = 0; i < vehicles.size(); i++) {
					Vehicle veh = vehicles.get(i);
					String writeStr = "\n" + veh.getImeiId() + ", " + veh.getMake() + ", " + veh.getModel() + "," + veh.getModelYear() + ",";

					List<DriverReport> resultset = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO))
							.getAvgAndMaxSpeedAndCumulativeDistanceForVehicle(veh.getId().getId(), dateRange.getStart(), dateRange.getEnd());

					for (Iterator<DriverReport> iter = resultset.iterator(); iter.hasNext();) {
						DriverReport element = (DriverReport) iter.next();
						writeStr = writeStr + element.getMaxspeed() + ","+ element.getAvgspeed() + ","+ element.getDistance() + ",";
					}

					List<TrackHistory> lastfuel = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
							.getFuel(veh.getId().getId(),  dateRange.getStart(), dateRange.getEnd());

					String fuelStr = null;
					for (Iterator<TrackHistory> iter4 = lastfuel.iterator(); iter4.hasNext();) {
						TrackHistory fuels = (TrackHistory) iter4.next();
						fuelStr = fuels.getFuel() + ","+ fuels.getFuel() + "3";
					}
					writeStr = writeStr+fuelStr;
					LOG.debug("the string data is: " + writeStr);
					output.write(writeStr);
				}
				output.close();
				return file.getAbsolutePath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@SuppressWarnings("deprecation")
	public static String createVehicleStatusReport(DateRange dateRange,
			Long reportId, Long userId) {

		long MILLISECONDSPERMINUTE = 60 * 1000;

		Writer output = null;

		Date date = new Date();

		String path = "/usr/local/tomcat6/temp/";

		File directory = new File(path);
		directory.mkdir();
		LOG.debug("directory created");
		directory = new File(path);
		directory.mkdir();

		StringBuilder vehicleStatusPath = new StringBuilder();

		vehicleStatusPath.append("VehicleStatus_").append(date.getDate())
		.append("_");
		vehicleStatusPath.append((date.getMonth() + 1)).append("_").append(
				date.getYear() + 1900).append(".txt");

		File file = new File(path + "/" + vehicleStatusPath.toString());

		Boolean dataLossFlag = false;
		String endTime = null;

		try {
			file.createNewFile();
			LOG.debug("File has been created");

			output = new BufferedWriter(new FileWriter(file));
			String header = "Vehicle Status Report at " + date + "\n\n";
			String str = header
					+ "Imei, Make, Model, Year, Distance, Start Fuel, End Fuel, End Time, DataLoss?";

			output.write(str);

			User user = LoadUserDetails.getInstance().retrieve(userId);
			if(user == null){
				try{
					if(output != null){
						output.close();
					}
				} catch (Exception e){
					LOG.error(e);
				}
				return "";
			}
			List<Vehicle> vehicles = ((VehicleDaoImpl) DBManager.getInstance()
					.getDao(DAOEnum.VEHICLE_DAO)).getReportAssignedVehicles(reportId, user.getGroupId());
			if (vehicles != null) {
				for (int i = 0; i < vehicles.size(); i++) {
					Vehicle veh = vehicles.get(i);

					String writeStr = "\n" + veh.getImeiId() + ", "
							+ veh.getMake() + ", " + veh.getModel() + ","
							+ veh.getModelYear() + ",";

					float distance = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
							.getCumulativeDistanceForVehicle(veh.getId().getId(), dateRange.getStart(), dateRange.getEnd());
					writeStr = writeStr + distance + ",";

					/*List<TrackHistory> lastfuel = ((TrackHistoryDaoImpl) DBManager
							.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
							.getFuel(veh.getId().getId(), dateRange
									.getStart(), dateRange.getEnd());*/
					List<TrackHistory> lastfuel = ((TrackHistoryDaoImpl) DBManager
							.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
							.getFuel(veh.getId().getId(),  dateRange
									.getStart(), dateRange.getEnd());

					//LOG.debug("Last fuel result list is"+lastfuel);
					//LOG.debug("startdate: "+dateRange.getStart() +" enddate: "+dateRange.getEnd());
					String fuelStr = null;
					for (int j=1; j<lastfuel.size(); j++) {
						TrackHistory prevfuel, presfuel = null;
						prevfuel = lastfuel.get(j-1);
						presfuel = lastfuel.get(j);
						Date prevDataTime = prevfuel.getOccurredat();

						if (presfuel.isMrs()) {
							long milliseconds = (presfuel.getOccurredat()
									.getTime() - prevDataTime.getTime());
							long tempMinutes = milliseconds
									/ MILLISECONDSPERMINUTE;
							if (tempMinutes > 30)
								dataLossFlag = true;
						}

						fuelStr = presfuel.getFuel() + ","
								+ presfuel.getFuel() + "3";

					}

					writeStr = writeStr+fuelStr+",";

					LiveVehicleStatus lvs = LoadLiveVehicleStatusRecord.getInstance().retrieveByVehicleId(veh.getId().getId());
					if (lvs!=null) {
						long time = lvs.getLastUpdatedAt().getTime();
						endTime = new Date(time).getHours() + ":" + new Date(time).getMinutes() + ":" + new Date(time).getSeconds();
					}

					writeStr = writeStr + endTime + "," + dataLossFlag;

					LOG.debug("the string data is: " + writeStr);
					output.write(writeStr);
				}
				output.close();
				return file.getAbsolutePath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@SuppressWarnings("deprecation")
	public static String createOfflineVehicleReport(DateRange dateRange, Long reportId, Long userId) {

		Writer output = null;
		Date date = new Date();

		long currTime = date.getTime();
		String path = "/usr/local/tomcat6/temp/";

		File directory = new File(path);
		directory.mkdir();
		LOG.debug("directory created");
		//path += Long.toString(userId) + "/";
		directory = new File(path);
		directory.mkdir();

		StringBuilder offlineVehiclePath = new StringBuilder();
		offlineVehiclePath.append("OfflineVehicles_").append(date.getDate())
		.append("_");
		offlineVehiclePath.append((date.getMonth() + 1)).append("_").append(
				date.getYear() + 1900).append(".txt");
		File file = new File(path + "/" + offlineVehiclePath.toString());

		try {
			file.createNewFile();
			LOG.debug("File has been created");
			output = new BufferedWriter(new FileWriter(file));

			String header = "List of Offline Vehicles for the duration from "+ dateRange.getStart() + " to " + dateRange.getEnd()+ "\n\n";
			String str = header + "VehicleName , UserName, Model, Year, Imei, Reason for Offline, LastUpdatedat";
			output.write(str);
			String userName = null;
			User user = LoadUserDetails.getInstance().retrieve(userId);
			if(user == null){
				try{
					if(output != null){
						output.close();
					}
				} catch (Exception e){
					LOG.error(e);
				}
				return "";
			}
			List<Vehicle> vehiclesOfTheUser = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).
					getReportAssignedVehicles(reportId, user.getGroupId());

			List<MailingListReport> userDetails = ((MailinglistReportDaoImp) DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_REPORT_DAO))
					.selectByPrimaryKey(new LongPrimaryKey(reportId));
			if(!userDetails.isEmpty())
			{
				MailingListReport mailUser = userDetails.get(0);
				userName = mailUser.getName();
			}

			if (vehiclesOfTheUser != null) {
				for (int i = 0; i < vehiclesOfTheUser.size(); i++) {
					Vehicle veh = vehiclesOfTheUser.get(i);
					String writeStr = null;
					List<LiveVehicleStatus> activeVehicles = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).
							getActiveVehicles(veh.getId().getId());

					if (activeVehicles.size()!=0) {
						for (int j = 0; j < activeVehicles.size(); j++) {
							LiveVehicleStatus status = activeVehicles.get(j);

							String reason = null;

							long lastUpdatedTime = status.getLastUpdatedAt().getTime();
							long diff = currTime-lastUpdatedTime;
							long diffDay = diff/(24*60*60*1000);
							if(diffDay<1)
								diffDay = -(diffDay);
							if(diffDay >= 1){
								writeStr = "\n" + veh.getDisplayName() + "," + userName + ", "+ veh.getMake() + ", " + veh.getModel() + ","
										+ veh.getModelYear() + "," + veh.getImeiId()+ ",";

								if (status.getBatteryVoltage() < 3600) {
									reason = "TAMPERED";
								} else if (status.getGsmStrength() < 10) {
									reason = "Bad GSM Signal";
								} else {
									reason = "Unknown";
								}
								writeStr = writeStr + reason + ","+ status.getLastUpdatedAt();
							}
						}
					}
					try{
						if(writeStr != null){
							output.write(writeStr);
						}
					}catch (Exception e){
						e.printStackTrace();
					}
				}
				output.close();
				return file.getAbsolutePath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
}