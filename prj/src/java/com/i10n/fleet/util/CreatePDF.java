package com.i10n.fleet.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.apache.log4j.Logger;
import org.postgis.Geometry;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import com.i10n.db.dao.AlertDaoImpl;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.EmriVehiclesBaseStationDaoImp;
import com.i10n.db.dao.IdlePointsDaoImp;
import com.i10n.db.dao.TrackHistoryDaoImpl;
import com.i10n.db.dao.TripDaoImp;
import com.i10n.db.dao.TripDetailsDaoImpl;
import com.i10n.db.entity.Address;
import com.i10n.db.entity.AlertOrViolation;
import com.i10n.db.entity.DateRange;
import com.i10n.db.entity.EmriVehiclesBaseStation;
import com.i10n.db.entity.IdlePoints;
import com.i10n.db.entity.TrackHistory;
import com.i10n.db.entity.Trip;
import com.i10n.db.entity.TripDetails;
import com.i10n.db.entity.User;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadDriverDetails;
import com.i10n.dbCacheManager.LoadEmriVehiclesBaseStationDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.utils.SessionUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class CreatePDF implements IDataProvider{
	private static Logger LOG = Logger.getLogger(CreatePDF.class);
	private String reportType,reportFormat;
	private String startdate,enddate,interval,localTime,localTimeZone;
	private DateRange dateRange;
	private int intervalInt;
	private long vehicleIdLong;
	private String vehicleId,vehicleName;
	private String pdfFilePath="";
	private String pdfFileName="";
	private String xlsFilePath="";
	private String xlsFileName="";
	public static final int ACTIVITY_REPORT=1;
	public static final int VIOLATION_REPORT=2;
	public static final int VEHICLE_STATS_REPORT=3;
	public static final int DRIVER_IDLE_POINTS_REPORT=4;
	public static final int VEHICLE_IDLE_POINTS_REPORT=5;
	public static final int VEHICLEHISTORY_REPORT=6;
	public static final int LIVEVEHICLE_STATUS=7;
	public static final String ACTIVITY_REPORT_STRING="acitivity";
	public static final String VEHICLE_STATS_REPORT_STRING="vehiclestats";
	public static final String VIOLATION_REPORT_STRING="violation";
	public static final String DRIVER_IDLE_REPORT_STRING="driveridlepoints";
	public static final String VEHICLE_IDLE_REPORT_STRING="vehicleidlepoints";
	public static final String VEHICLEHISTORY_REPORT_STRING="vehiclehistory";
	public static final String LIVE_VEHICLE_STATUS_STRING="livevehiclestatus";
	private String[] activity_report_headers = new String[] { "TIME","LOCATION", "LATITUDE", "LONGITUDE", "SPEED","CUMULATIVE DISTANCE" };
	private String[] violation_report_headers = new String[] { "S_NO","VEHICLE NAME", "DRIVER NAME", "ALERT TIME", "ALERT LOCATION","ALERT TYPE"," ALERT VALUE" };
	private String[] vehiclestats_report_headers = new String[] { "S_NO","VEHICLE NAME", "START TIME", "START LOCATION", "END TIME","CURRENT LOCATION","MAXIMUM SPEED","AVERAGE SPEED","DISTANCE" };
	private String[] idle_points_report_headers = new String[] { "START TIME", "END TIME","IDLE TIME","LOCATION"};
	private String[] vehicle_history_report_headers = new String[] { "S_NO", "UPDATED IMEI","UPDATED TIME","UPDATED BY","ATTENDED BY","BATTERY CHANGED","FUSE CHANGED"};
	private String[] live_vehiclestatus_headers = new String[] { "S_NO","VEHICLE NAME", "STATUS", "CHARGER CONNECTED", "CURRENT LOCATION","GPS STRENGTH","GSM STRENGTH","BATTERY VOLTAGE","LAST UPDATED" };
	private String[] live_frs_vehiclestatus_headers = new String[] { "S_NO","VEHICLE NAME","DISTRICT","BASE LOCATION","CREW NUMBER", "STATUS","SPEED", "CURRENT LOCATION","LAST UPDATED","UNIT NO" };
	private IDataset mDataToPrint;
	private List<IDataset> positionsData = new ArrayList<IDataset>();
	private WritableCellFormat timesBoldUnderline;
	private WritableCellFormat times;
	private String filePath=System.getenv("CATALINA_HOME").replace(";", "")+"/webapps/temp/";
	@Override
	public String getName() {
		return "pdfgeneration";
	}

	@Override
	public IDataset getDataset(RequestParameters params) {
		IDataset result = new Dataset();
		mDataToPrint= (IDataset) params.getSession().getAttribute("returnedData");
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		localTimeZone = (String) WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_TIMEZone);
		result.put("filepath", EnvironmentInfo.getProperty("SERVER_URL")+"/temp/"+this.getData(params));
		return result;
	}


	@SuppressWarnings("deprecation")
	private String getData(RequestParameters params) {
		reportType = params.getRequestParameter("reporttype");
		File mDirectory =new File(filePath);
		if(!mDirectory.exists())
			mDirectory.mkdir();	
		if(reportType.equalsIgnoreCase(ACTIVITY_REPORT_STRING)){
			reportFormat = params.getRequestParameter("reportformat");
			startdate = params.getRequestParameter("startdate");
			enddate = params.getRequestParameter("enddate");
			localTime = params.getRequestParameter("localTime");
			vehicleId =params.getRequestParameter("vehicleID");
			vehicleName=params.getRequestParameter("vehicleName");
			String vehicleidVariable = StringUtils.getValueFromKVP(vehicleId);
			vehicleIdLong = Long.parseLong(StringUtils.stripCommas(vehicleidVariable.trim()));
			interval=StringUtils.stripSpace(params.getRequestParameter("interval"));
			intervalInt=Integer.parseInt(interval);
			String vehiclename=vehicleName.replace(" ", "");
			dateRange=DateUtils.getModeOfReport(localTime,startdate,enddate);
			pdfFileName=SessionUtils.getCurrentlyLoggedInUser().getLogin()+"_activityreport_"+vehiclename+"_"+new Date(startdate).getTime()+"_"+new Date(enddate).getTime()+"_"+interval+".pdf";
			xlsFileName=SessionUtils.getCurrentlyLoggedInUser().getLogin()+"_activityreport_"+vehiclename+"_"+new Date(startdate).getTime()+"_"+new Date(enddate).getTime()+"_"+interval+".xls";
			pdfFilePath=filePath+pdfFileName;
			xlsFilePath=filePath+xlsFileName;
			if(reportFormat.equalsIgnoreCase("pdf")){
				return generatePDF(ACTIVITY_REPORT);
			}else if(reportFormat.equalsIgnoreCase("excel")){
				return generateXls(ACTIVITY_REPORT);
			}
		} else if(reportType.equalsIgnoreCase(VIOLATION_REPORT_STRING)){
			reportFormat = params.getRequestParameter("reportformat");
			startdate = params.getRequestParameter("startdate");
			enddate = params.getRequestParameter("enddate");
			localTime = params.getRequestParameter("localTime");
			dateRange=DateUtils.getModeOfReport(localTime,startdate,enddate);
			pdfFileName=SessionUtils.getCurrentlyLoggedInUser().getLogin()+"_violationreport_"+new Date(startdate).getTime()+"_"+new Date(enddate).getTime()+".pdf";
			xlsFileName=SessionUtils.getCurrentlyLoggedInUser().getLogin()+"_violationreport_"+new Date(startdate).getTime()+"_"+new Date(enddate).getTime()+".xls";
			pdfFilePath=filePath+pdfFileName;
			xlsFilePath=filePath+xlsFileName;
			if(reportFormat.equalsIgnoreCase("pdf")){
				return generatePDF(VIOLATION_REPORT);
			}else if(reportFormat.equalsIgnoreCase("excel")){
				return generateXls(VIOLATION_REPORT);
			}
		} else if(reportType.equalsIgnoreCase(VEHICLE_STATS_REPORT_STRING)){
			reportFormat = params.getRequestParameter("reportformat");
			startdate = params.getRequestParameter("startdate");
			enddate = params.getRequestParameter("enddate");
			localTime = params.getRequestParameter("localTime");
			dateRange=DateUtils.getModeOfReport(localTime,startdate,enddate);
			pdfFileName=SessionUtils.getCurrentlyLoggedInUser().getLogin()+"_vehiclestatsreport_"+new Date(startdate).getTime()+"_"+new Date(enddate).getTime()+".pdf";
			xlsFileName=SessionUtils.getCurrentlyLoggedInUser().getLogin()+"_vehiclestatsreport_"+new Date(startdate).getTime()+"_"+new Date(enddate).getTime()+".xls";
			pdfFilePath=filePath+pdfFileName;
			xlsFilePath=filePath+xlsFileName;
			if(reportFormat.equalsIgnoreCase("pdf")){
				return generatePDF(VEHICLE_STATS_REPORT);
			}else if(reportFormat.equalsIgnoreCase("excel")){
				return generateXls(VEHICLE_STATS_REPORT);
			}
		}else if(reportType.equalsIgnoreCase(DRIVER_IDLE_REPORT_STRING)){
			reportFormat = params.getRequestParameter("reportformat");
			startdate = params.getRequestParameter("startdate");
			enddate = params.getRequestParameter("enddate");
			localTime = params.getRequestParameter("localTime");
			vehicleId =params.getRequestParameter("vehicleID");
			vehicleName=params.getRequestParameter("vehicleName");
			String vehicleidVariable = StringUtils.getValueFromKVP(vehicleId);
			vehicleIdLong = Long.parseLong(StringUtils.stripCommas(vehicleidVariable.trim()));
			String vehiclename=vehicleName.replace(" ", "");
			dateRange=DateUtils.getModeOfReport(localTime,startdate,enddate);
			pdfFileName=SessionUtils.getCurrentlyLoggedInUser().getLogin()+"_driveridlepointsreport_"+vehiclename+"_"+new Date(startdate).getTime()+"_"+new Date(enddate).getTime()+".pdf";
			xlsFileName=SessionUtils.getCurrentlyLoggedInUser().getLogin()+"_driveridlepointsreport_"+vehiclename+"_"+new Date(startdate).getTime()+"_"+new Date(enddate).getTime()+".xls";
			pdfFilePath=filePath+pdfFileName;
			xlsFilePath=filePath+xlsFileName;
			if(reportFormat.equalsIgnoreCase("pdf")){
				return generatePDF(DRIVER_IDLE_POINTS_REPORT);
			}else if(reportFormat.equalsIgnoreCase("excel")){
				return generateXls(DRIVER_IDLE_POINTS_REPORT);
			}
		}else if(reportType.equalsIgnoreCase(VEHICLEHISTORY_REPORT_STRING)){
			reportFormat = params.getRequestParameter("reportformat");
			startdate = params.getRequestParameter("startdate");
			enddate = params.getRequestParameter("enddate");
			vehicleId =params.getRequestParameter("vehicleID");
			vehicleName=params.getRequestParameter("vehicleName");
			interval=StringUtils.stripSpace(params.getRequestParameter("interval"));
			String vehiclename=vehicleName.replace(" ", "");
			pdfFileName=SessionUtils.getCurrentlyLoggedInUser().getLogin()+"_vehiclehistoryreport_"+vehiclename+"_"+new Date(startdate).getTime()+"_"+new Date(enddate).getTime()+"_"+interval+".pdf";
			xlsFileName=SessionUtils.getCurrentlyLoggedInUser().getLogin()+"_vehiclehistoryreport_"+vehiclename+"_"+new Date(startdate).getTime()+"_"+new Date(enddate).getTime()+"_"+interval+".xls";
			pdfFilePath=filePath+pdfFileName;
			xlsFilePath=filePath+xlsFileName;
			if(reportFormat.equalsIgnoreCase("pdf")){
				return generatePDF(VEHICLEHISTORY_REPORT);
			}else if(reportFormat.equalsIgnoreCase("excel")){
				return generateXls(VEHICLEHISTORY_REPORT);
			}
		}else if(reportType.equalsIgnoreCase(VEHICLE_IDLE_REPORT_STRING)){
			reportFormat = params.getRequestParameter("reportformat");
			startdate = params.getRequestParameter("startdate");
			enddate = params.getRequestParameter("enddate");
			localTime = params.getRequestParameter("localTime");
			vehicleId =params.getRequestParameter("vehicleID");
			vehicleName=params.getRequestParameter("vehicleName");
			String vehicleidVariable = StringUtils.getValueFromKVP(vehicleId);
			vehicleIdLong = Long.parseLong(StringUtils.stripCommas(vehicleidVariable.trim()));
			String vehiclename=vehicleName.replace(" ", "");
			dateRange=DateUtils.getModeOfReport(localTime,startdate,enddate);
			pdfFileName=SessionUtils.getCurrentlyLoggedInUser().getLogin()+"_vehicleidlepointsreport_"+vehiclename+"_"+new Date(startdate).getTime()+"_"+new Date(enddate).getTime()+".pdf";
			xlsFileName=SessionUtils.getCurrentlyLoggedInUser().getLogin()+"_vehicleidlepointsreport_"+vehiclename+"_"+new Date(startdate).getTime()+"_"+new Date(enddate).getTime()+".xls";
			pdfFilePath=filePath+pdfFileName;
			xlsFilePath=filePath+xlsFileName;
			if(reportFormat.equalsIgnoreCase("pdf")){
				return generatePDF(VEHICLE_IDLE_POINTS_REPORT);
			}else if(reportFormat.equalsIgnoreCase("excel")){
				return generateXls(VEHICLE_IDLE_POINTS_REPORT);
			}
		}else if(reportType.equalsIgnoreCase(LIVE_VEHICLE_STATUS_STRING)){
			reportFormat = params.getRequestParameter("reportformat");
			pdfFileName=SessionUtils.getCurrentlyLoggedInUser().getLogin()+"_livevehiclestatus_"+new Date().getTime()+".pdf";
			xlsFileName=SessionUtils.getCurrentlyLoggedInUser().getLogin()+"_livevehiclestatus_"+new Date().getTime()+".xls";
			pdfFilePath=filePath+pdfFileName;
			xlsFilePath=filePath+xlsFileName;
			if(reportFormat.equalsIgnoreCase("pdf")){
				return generatePDF(LIVEVEHICLE_STATUS);
			}else if(reportFormat.equalsIgnoreCase("excel")){
				return generateXls(LIVEVEHICLE_STATUS);
			}
		}
		return null;
	}

	private String generateXls(int report) {

		return generateXLSReport(report);
	}

	private String generateXLSReport(int report) {

		File file = new File(xlsFilePath);
		WorkbookSettings wbSettings = new WorkbookSettings();
		try{
			wbSettings.setLocale(new Locale("en", "EN"));
			WritableWorkbook workbook = Workbook.createWorkbook(file, wbSettings);
			workbook.createSheet("Report", 0);
			WritableSheet excelSheet = workbook.getSheet(0);
			createLabel(excelSheet,report);
			workbook.write();
			workbook.close();
		}catch (Exception e) {
			LOG.error("in error of xls and error message is "+e.getMessage());
		}
		return xlsFileName;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	private void createLabel(WritableSheet sheet, int reportType) {
		try{
			// Lets create a times font
			WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
			// Define the cell format
			times = new WritableCellFormat(times10pt);
			// Lets automatically wrap the cells
			times.setWrap(true);

			// Create create a bold font with underlines
			WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,
					UnderlineStyle.SINGLE);
			timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
			// Lets automatically wrap the cells
			timesBoldUnderline.setWrap(true);
			CellView cv = new CellView();
			cv.setFormat(times);
			cv.setFormat(timesBoldUnderline);
			cv.setAutosize(true);
			switch (reportType) {

			case ACTIVITY_REPORT:
				for (int i = 0; i < activity_report_headers.length; i++) {
					addCaption(sheet, i, 0, activity_report_headers[i]);
				}
				List<TrackHistory> trackHistoryResultset;
				if(intervalInt == 0){
					trackHistoryResultset = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
							.selectBetweenDates(vehicleIdLong, dateRange.getStart(), dateRange.getEnd());
				}else{
					trackHistoryResultset = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
							.selectBetweenDatesIntervalNotZeroWithNoLimit(vehicleIdLong, dateRange.getStart(), dateRange.getEnd(),intervalInt);
				}

				List<TrackHistory> filteredResultSet = new ArrayList<TrackHistory>();
				float cumulativeDistance = 0.0F;

				TrackHistory prevTrackHistory = null;
				for (int j = 0; j <trackHistoryResultset.size(); j++) {
					Geometry points = trackHistoryResultset.get(j).getLocation();
					if(prevTrackHistory != null){
						Geometry prevPoints = prevTrackHistory.getLocation();
						LOG.debug("Lat1, Lng1 : "+prevPoints.getFirstPoint().getY()+", "+prevPoints.getFirstPoint().getX()+
								" Lat2, Lng2 : "+points.getFirstPoint().getY()+", "+points.getFirstPoint().getX());
						float calculatedDistance = (float) CustomCoordinates.distance(prevPoints.getFirstPoint().getY(), prevPoints.getFirstPoint().getX(),
								points.getFirstPoint().getY(), points.getFirstPoint().getX());
						trackHistoryResultset.get(j).setDistance((float)(calculatedDistance + (0.1*calculatedDistance)));
					}
					prevTrackHistory = trackHistoryResultset.get(j);
					LOG.debug("Calculated distance : "+trackHistoryResultset.get(j).getDistance());
					cumulativeDistance += trackHistoryResultset.get(j).getDistance();
					LOG.debug("Cumulative distance : "+cumulativeDistance);
					trackHistoryResultset.get(j).setDistance(cumulativeDistance);
					filteredResultSet.add(trackHistoryResultset.get(j));
				}
				DecimalFormat df = new DecimalFormat("0.##");
				for (int j = 0; j < filteredResultSet.size(); j++) {
					TrackHistory tHistory = filteredResultSet.get(j);
					StringBuffer location=new StringBuffer();
					Geometry points = tHistory.getLocation();
					if (Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_ACTIVITY_REPORT_ENABLED"))) {
						Address locationFetch = GeoUtils.fetchNearestLocation(tHistory.getLocation().getFirstPoint().y, 
								tHistory.getLocation().getFirstPoint().x,false);
						location.append(StringUtils.formulateAddress(locationFetch, vehicleIdLong, tHistory.getLocation().getFirstPoint().y,
								tHistory.getLocation().getFirstPoint().x).toString());
					} else {
						location.append("Activity ");
						location.append(StringUtils.addressFetchDisabled(vehicleIdLong,tHistory.getLocation().getFirstPoint().y,
								tHistory.getLocation().getFirstPoint().x).toString());
					}
					addLabel(sheet, 0, j+1,DateUtils.adjustToClientTime(localTimeZone,tHistory.getOccurredat()));
					addLabel(sheet, 1, j+1,location.toString());
					addLabel(sheet, 2, j+1,String.valueOf(points.getFirstPoint().getY()));
					addLabel(sheet, 3, j+1,String.valueOf(points.getFirstPoint().getX()));
					addLabel(sheet, 4, j+1,String.valueOf(df.format(tHistory.getSpeed())));
					addLabel(sheet, 5, j+1,String.valueOf(df.format(tHistory.getDistance())));
				}			
				break;

			case VIOLATION_REPORT:
				for (int i = 0; i < violation_report_headers.length; i++) {
					addCaption(sheet, i, 0, violation_report_headers[i]);
				}
				List<AlertOrViolation> alertOrViolationList = ((AlertDaoImpl) DBManager.getInstance().getDao(DAOEnum.ALERT_DAO)).
						selectByUserIdAndDuration(SessionUtils.getCurrentlyLoggedInUser().getId(), dateRange.getStart(), dateRange.getEnd());
				for (int j = 0; j < alertOrViolationList.size(); j++) {
					AlertOrViolation alert = alertOrViolationList.get(j);
					Address address = GeoUtils.fetchNearestLocation(alert.getAlertLocation().getFirstPoint().y,
							alert.getAlertLocation().getFirstPoint().x, false);
					StringBuffer location=StringUtils.formulateAddress(address, alert.getVehicleId(), 
							alert.getAlertLocation().getFirstPoint().y, alert.getAlertLocation().getFirstPoint().x);
					if(address != null){
						alert.setAlertLocationReferenceId(address.getId());
						alert.setAlertLocationText(location.toString());
					}
					addLabel(sheet, 0, j+1,String.valueOf(j+1));
					addLabel(sheet, 1, j+1,LoadVehicleDetails.getInstance().retrieve(alert.getVehicleId()).getDisplayName());
					addLabel(sheet, 2, j+1,LoadDriverDetails.getInstance().retrieve(alert.getDriverId()).getFirstName());
					addLabel(sheet, 3, j+1,DateUtils.adjustToClientTime(localTimeZone, alert.getAlertTime()));
					addLabel(sheet, 4, j+1,alert.getAlertLocationText());
					addLabel(sheet, 5, j+1,alert.getAlertType().toString());
					addLabel(sheet, 6, j+1,alert.getAlertTypeValue());
				}			
				break;

			case VEHICLE_STATS_REPORT:
				for (int i = 0; i < vehiclestats_report_headers.length; i++) {
					addCaption(sheet, i, 0, vehiclestats_report_headers[i]);
				}
				DecimalFormat df1 = new DecimalFormat("0.##");
				Vehicle vid=null;
				User user = SessionUtils.getCurrentlyLoggedInUser();
				Long uid = user.getId();
				List<TripDetails> tripList = ((TripDetailsDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRIP_DETAILS_DAO))
						.getActiveTripDetailsWithLiveStatusForTheUser(uid);
				if (tripList != null) {
					for (int i = 0; i < tripList.size(); i++) {
						float maxSpeed = 0;
						float distance = 0;
						float avgSpeed = 0;
						vid=tripList.get(i).getVehicle();
						if (vid != null) {
							List<TrackHistory> trackEntries = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO)).selectBetweenDates(vid.getId().getId(), dateRange.getStart(), dateRange.getEnd());
							if(trackEntries.size()>0){
								// This function will return the start location of the vehicle from the track history table
								TrackHistory firstTrackPoint = trackEntries.get(0);//vehiclestartlocation.get(0);
								double a = firstTrackPoint.getLocation().getFirstPoint().getY();
								double b = firstTrackPoint.getLocation().getFirstPoint().getX();
								StringBuffer startlocation= new StringBuffer();
								startlocation.append(a+":"+b);
								LOG.debug("Start Location Latitude "+a+" Longitude "+b);

								if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_VEHICLE_STATS_ENABLED"))){
									Address locationFetch = GeoUtils.fetchNearestLocation(a, b, false);
									startlocation=StringUtils.formulateAddress(locationFetch,vid.getId().getId(),a, b);
								}else {
									startlocation.append("STATS ");
									startlocation.append(StringUtils.addressFetchDisabled(vid.getId().getId(),a, b).toString());
								}
								TrackHistory lastTrackPoint = trackEntries.get(trackEntries.size()-1);//tracklist.get(0);
								double x = lastTrackPoint.getLocation().getFirstPoint().getY();
								double y = lastTrackPoint.getLocation().getFirstPoint().getX();
								LOG.debug("End Location Location Latitude "+x+" Longitude "+y);
								StringBuffer endLoaction=new StringBuffer();
								if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_VEHICLE_STATS_ENABLED"))){
									Address endLocationFetch = GeoUtils.fetchNearestLocation(x, y,false);
									endLoaction=StringUtils.formulateAddress(endLocationFetch,vid.getId().getId(), x, y);
								} else {
									endLoaction.append("STATS ");
									endLoaction.append(StringUtils.addressFetchDisabled(vid.getId().getId(), x, y).toString());
								}
								for (TrackHistory mHistory: trackEntries) {
									avgSpeed=avgSpeed+mHistory.getSpeed();
									distance=distance+mHistory.getDistance();
									if(maxSpeed < mHistory.getSpeed()){
										maxSpeed=mHistory.getSpeed();
									}
								}
								avgSpeed=avgSpeed/trackEntries.size();
								addLabel(sheet, 0, i+1,String.valueOf(i+1));
								addLabel(sheet, 1, i+1,vid.getDisplayName());
								addLabel(sheet, 2, i+1,firstTrackPoint.getOccurredat().toString());
								addLabel(sheet, 3, i+1,startlocation.toString());
								addLabel(sheet, 4, i+1,lastTrackPoint.getOccurredat().toString());
								addLabel(sheet, 5, i+1,endLoaction.toString());
								addLabel(sheet, 6, i+1,df1.format(maxSpeed)+"");
								addLabel(sheet, 7, i+1,df1.format(avgSpeed)+"");
								addLabel(sheet, 8, i+1,df1.format(distance)+"");
							}else{
								addLabel(sheet, 0, i+1,String.valueOf(i+1));
								addLabel(sheet, 1, i+1,vid.getDisplayName());
								addLabel(sheet, 2, i+1,"");
								addLabel(sheet, 3, i+1,"No Location Available");
								addLabel(sheet, 4, i+1,"");
								addLabel(sheet, 5, i+1,"No Location Available");
								addLabel(sheet, 6, i+1,"0.0");
								addLabel(sheet, 7, i+1,"0.0");
								addLabel(sheet, 8, i+1,"0.0");
							}
						}
					}
				}
				break;

			case VEHICLE_IDLE_POINTS_REPORT:
				for (int i = 0; i < idle_points_report_headers.length; i++) {
					addCaption(sheet, i, 0, idle_points_report_headers[i]);
				}
				List<Trip> vehicleTrips = ((TripDaoImp) DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).selectTripByVehicleId(vehicleIdLong);
				for (int i = 0; i < vehicleTrips.size(); i++) {
					Trip trip = vehicleTrips.get(i);
					List<IdlePoints> idlePointResultset = ((IdlePointsDaoImp) DBManager.getInstance().getDao(DAOEnum.IDLE_POINTS_DAO))
							.selectAllIdlePointsBetweenDates(trip.getId().getId(), dateRange.getStart(),dateRange.getEnd());
					for (int j = 0; j < idlePointResultset.size(); j++) {
						IdlePoints idlePoints = idlePointResultset.get(j);
						Date clientTime = new Date(localTime);
						Long startTimeInMillis = DateUtils.adjustToLocalTime(idlePoints.getStarttime(), clientTime).getTime();
						Long endTimeInMillis = DateUtils.adjustToLocalTime(idlePoints.getEndtime(), clientTime).getTime();
						String time=DateUtils.formatTimeDifference(endTimeInMillis,startTimeInMillis);
						Address locationFetch = GeoUtils.fetchNearestLocation(idlePoints.getIdleLocation().getFirstPoint().y, 
								idlePoints.getIdleLocation().getFirstPoint().x,false);
						StringBuffer location=StringUtils.formulateAddress(locationFetch, vehicleIdLong, idlePoints.getIdleLocation().
								getFirstPoint().y,idlePoints.getIdleLocation().getFirstPoint().x);
						addLabel(sheet, 0, j+1,DateUtils.adjustToClientTime(localTimeZone, idlePoints.getStarttime()));
						addLabel(sheet, 1, j+1,DateUtils.adjustToClientTime(localTimeZone, idlePoints.getEndtime()));
						addLabel(sheet, 2, j+1,time);
						addLabel(sheet, 3, j+1,location.toString());
					}
				}
				break;

			case VEHICLEHISTORY_REPORT:
				for (int i = 0; i < vehicle_history_report_headers.length; i++) {
					addCaption(sheet, i, 0, vehicle_history_report_headers[i]);
				}
				positionsData=(List<IDataset>) mDataToPrint.get("activity."+vehicleId+".positions");
				for (int j = 0; j < positionsData.size(); j++) {
					IDataset mObject=(IDataset) positionsData.get(j);
					addLabel(sheet, 0, j+1,String.valueOf(j));
					addLabel(sheet, 1, j+1,mObject.getValue("imei"));
					addLabel(sheet, 2, j+1,mObject.getValue("updatedtime"));
					addLabel(sheet, 3, j+1,String.valueOf(mObject.get("updatedbyuser")));
					addLabel(sheet, 4, j+1,String.valueOf(mObject.get("vehicleattended")));
					addLabel(sheet, 5, j+1,String.valueOf(mObject.get("batterychanged")));
					addLabel(sheet, 6, j+1,String.valueOf(mObject.get("fusechanged")));
				}			
				break;

			case DRIVER_IDLE_POINTS_REPORT:
				for (int i = 0; i < idle_points_report_headers.length; i++) {
					addCaption(sheet, i, 0, idle_points_report_headers[i]);
				}
				List<Trip> driverTrips = ((TripDaoImp) DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).selectTripByDriverId(vehicleIdLong);
				for (int i = 0; i < driverTrips.size(); i++) {
					Trip trip = driverTrips.get(i);
					List<IdlePoints> idlePointResultset = ((IdlePointsDaoImp) DBManager.getInstance().getDao(DAOEnum.IDLE_POINTS_DAO))
							.selectAllIdlePointsBetweenDates(trip.getId().getId(), dateRange.getStart(),dateRange.getEnd());
					for (int j = 0; j < idlePointResultset.size(); j++) {
						IdlePoints idlePoints = idlePointResultset.get(j);
						Date clientTime = new Date(localTime);
						Long startTimeInMillis = DateUtils.adjustToLocalTime(idlePoints.getStarttime(), clientTime).getTime();
						Long endTimeInMillis = DateUtils.adjustToLocalTime(idlePoints.getEndtime(), clientTime).getTime();
						String time=DateUtils.formatTimeDifference(endTimeInMillis,startTimeInMillis);
						Address locationFetch = GeoUtils.fetchNearestLocation(idlePoints.getIdleLocation().getFirstPoint().y, 
								idlePoints.getIdleLocation().getFirstPoint().x,false);
						StringBuffer location=StringUtils.formulateAddress(locationFetch, vehicleIdLong, idlePoints.getIdleLocation().
								getFirstPoint().y,idlePoints.getIdleLocation().getFirstPoint().x);
						addLabel(sheet, 0, j+1,DateUtils.adjustToClientTime(localTimeZone, idlePoints.getStarttime()));
						addLabel(sheet, 1, j+1,DateUtils.adjustToClientTime(localTimeZone, idlePoints.getEndtime()));
						addLabel(sheet, 2, j+1,time);
						addLabel(sheet, 3, j+1,location.toString());
					}
				}
				break;

			case LIVEVEHICLE_STATUS:

				if( Boolean.valueOf(EnvironmentInfo.getProperty("IS_FRS_EMRI_CLIENT"))){
					for (int i = 0; i < live_frs_vehiclestatus_headers.length; i++) {
						addCaption(sheet, i, 0, live_frs_vehiclestatus_headers[i]);
					}
				}else{
					for (int i = 0; i < live_vehiclestatus_headers.length; i++) {
						addCaption(sheet, i, 0, live_vehiclestatus_headers[i]);
					}
				}
				TripDetailsDaoImpl tripDetailsDaoImpl = (TripDetailsDaoImpl)DBManager.getInstance().getDao(DAOEnum.TRIP_DETAILS_DAO);
				List<TripDetails> tripDetailsList = tripDetailsDaoImpl.getActiveTripDetailsWithLiveStatusForTheUser(
						new LongPrimaryKey(SessionUtils.getCurrentlyLoggedInUser().getId()));
				if (tripDetailsList.size() != 0){
					Collections.sort(tripDetailsList, new Comparator<TripDetails>() {
						public int compare(TripDetails o1, TripDetails o2) {
							return o1.getVehicle().getDisplayName().compareTo(o2.getVehicle().getDisplayName());
						}});
					EmriVehiclesBaseStation cachedEmriRajasthan = null;
					for (int j = 0; j < tripDetailsList.size(); j++) {
						StringBuffer location=new StringBuffer();
						TripDetails mObject=(TripDetails) tripDetailsList.get(j);
						double a = mObject.getDynamicTripStatus().getLocation().getFirstPoint().y;
						double b = mObject.getDynamicTripStatus().getLocation().getFirstPoint().x;
						Address locationFetch = GeoUtils.fetchNearestLocationFromCache(a, b);
						if(locationFetch !=null){
							location.append(StringUtils.formulateAddress(locationFetch,mObject.getVehicle().getId().getId(),a,b).toString());
						}else{
							location.append(StringUtils.addressFetchDisabled(mObject.getVehicle().getId().getId(),a,b).toString());
						}
						if( Boolean.valueOf(EnvironmentInfo.getProperty("IS_FRS_EMRI_CLIENT"))){
							addLabel(sheet, 0, j+1,j+1+"");
							addLabel(sheet, 1, j+1,mObject.getVehicle().getDisplayName());
							List<EmriVehiclesBaseStation> emriList = ((EmriVehiclesBaseStationDaoImp)DBManager.getInstance().
									getDao(DAOEnum.EMRI_RAJASTHAN_DAO)).selectByVehicleId(mObject.getVehicle().getId().getId());
							if(emriList != null && emriList.size() > 0){
								Long emriRajasthanId = emriList.get(0).getID().getId();
								cachedEmriRajasthan = LoadEmriVehiclesBaseStationDetails.getInstance().retrieve(emriRajasthanId);
							}
							if(cachedEmriRajasthan != null){
								addLabel(sheet, 2, j+1,cachedEmriRajasthan.getDistrict());
								addLabel(sheet, 3, j+1,cachedEmriRajasthan.getBaseLocation());
								addLabel(sheet, 4, j+1,cachedEmriRajasthan.getCrewNo()+"");
							}else{
								addLabel(sheet, 2, j+1,"No District Available");
								addLabel(sheet, 3, j+1,"No Base Location Available");
								addLabel(sheet, 4, j+1,"No Crew No Available");
							}

							addLabel(sheet, 5, j+1,mObject.getVehicle().getStatus(mObject.getVehicle().getImei()));
							addLabel(sheet, 6, j+1,mObject.getDynamicTripStatus().getMaxSpeed()+"");
							addLabel(sheet, 7, j+1,location.toString());
							addLabel(sheet, 8, j+1,mObject.getDynamicTripStatus().getLastUpdatedAt().toString());
							addLabel(sheet, 9, j+1,mObject.getVehicle().getImei());
						}else{
							addLabel(sheet, 0, j+1,j+1+"");
							addLabel(sheet, 1, j+1,mObject.getVehicle().getDisplayName());
							addLabel(sheet, 2, j+1,mObject.getVehicle().getStatus(mObject.getVehicle().getImei()));
							addLabel(sheet, 3, j+1,mObject.getDynamicTripStatus().isChargerConnected()?"Yes": "No");
							addLabel(sheet, 4, j+1,location.toString());
							addLabel(sheet, 5, j+1,getGpsStrength(mObject.getDynamicTripStatus().getGpsStrength()));
							addLabel(sheet, 6, j+1,getGSMStrengthFromLevel(mObject.getDynamicTripStatus().getGsmStrength()));
							addLabel(sheet, 7, j+1,getBatteryStrengthFromLevel(mObject.getDynamicTripStatus().getBatteryVoltage()));
							addLabel(sheet, 8, j+1,mObject.getDynamicTripStatus().getLastUpdatedAt().toString());
						}	
					}
				}
				break;
			default:
				break;
			}
		}catch (Exception e) {
			LOG.error("in exception and exception is "+e.getMessage()+"\n"+e.getLocalizedMessage());
		}
	}

	private String getGpsStrength(float value) {
		String sStrength = "Normal";


		if(value>=0.1 && value <= 0.9){

			sStrength="Strong";
		}else if(value>=1.0 && value<=1.3)
		{
			sStrength="Normal";
		}
		else if(value>=1.4 && value<=1.5){
			sStrength="Weak";
		}
		else
		{
			sStrength="Very Weak";
		}
		return sStrength;
	}

	private String getGSMStrengthFromLevel(float sLevel){
		String sStrength = "Normal";


		if(sLevel>30){

			sStrength="Strong";
		}else if(sLevel >=25.0 && sLevel<=30.0)
		{
			sStrength="Normal";
		}
		else if(sLevel >=20.0 && sLevel<25.0){
			sStrength="Weak";
		}
		else
		{
			sStrength="Very Weak";
		}
		return sStrength;
	}

	private String getBatteryStrengthFromLevel(float sLevel){
		String sStrength = "Normal";


		if(sLevel>4000){

			sStrength="Strong";
		}else if(sLevel >=3900 && sLevel<=4000)
		{
			sStrength="Normal";
		}
		else if(sLevel >=3700 && sLevel<3900){
			sStrength="Weak";
		}
		else
		{
			sStrength="Very Weak";
		}
		return sStrength;
	}

	private void addCaption(WritableSheet sheet, int column, int row, String s)
			throws RowsExceededException, WriteException {
		Label label;
		label = new Label(column, row, s, timesBoldUnderline);
		sheet.addCell(label);
	}

	private void addLabel(WritableSheet sheet, int column, int row, String s)
			throws WriteException, RowsExceededException {
		Label label;
		label = new Label(column, row, s, times);
		sheet.addCell(label);
	}


	private String generatePDF(int reportFormatType2) {
		switch (reportFormatType2) {
		case ACTIVITY_REPORT:
			return generateActivityReportPDf();
		case VIOLATION_REPORT:
			return generateViolationReportPDf();
		case VEHICLE_STATS_REPORT:
			return generateVehicleStatsReportPDf();
		case DRIVER_IDLE_POINTS_REPORT:
			return generateDriverIdlePointsReportPDf();
		case VEHICLE_IDLE_POINTS_REPORT:
			return generateVehicleIdlePointsReportPDf();
		case VEHICLEHISTORY_REPORT:
			return generateVehicleHistoryReportPDf();
		case LIVEVEHICLE_STATUS:
			return generateLiveVehicleStatusPDf();
		default:
			break;
		}
		return pdfFileName;

	}

	private String generateLiveVehicleStatusPDf() {
		// Create a new document.
		Document document = new Document(PageSize.LETTER.rotate());
		try {
			// Get an instance of PdfWriter and create a Table.pdf file as an output.
			File mFile=new File(pdfFilePath);
			try {
				mFile.createNewFile(); 
			} catch (IOException e) {
				LOG.error("in io exception "+e.getMessage());
			}
			PdfWriter.getInstance(document, new FileOutputStream(mFile));
			document.open();
			String imageUrl =EnvironmentInfo.getProperty("SERVER_URL")+"/static/img/header/fleet-logo2.png";
			try{  
				Image image = Image.getInstance(new URL(imageUrl));
				image.setAlignment(Element.HEADER);
				document.add(image);
			}catch (Exception e) {
				LOG.error("in error "+e.getMessage());
			}
			Font titleFont = new Font();
			titleFont.setSize(20);
			titleFont.setStyle(Font.BOLDITALIC);
			Paragraph mTitle=new Paragraph("Live Vehicle Status.", titleFont);
			mTitle.setAlignment(Element.ALIGN_CENTER);
			document.add(mTitle);
			EmriVehiclesBaseStation cachedEmriRajasthan = null;
			PdfPTable table;
			if( Boolean.valueOf(EnvironmentInfo.getProperty("IS_FRS_EMRI_CLIENT"))){
				table = new PdfPTable(live_frs_vehiclestatus_headers.length);
				table.setSpacingBefore(10);
				for (int i = 0; i < live_frs_vehiclestatus_headers.length; i++) {
					String header = live_frs_vehiclestatus_headers[i];
					PdfPCell cell = new PdfPCell();
					cell.setGrayFill(0.9f);
					cell.setPhrase(new Phrase(header.toUpperCase(), new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD)));
					table.addCell(cell);
				}
			}else{
				table = new PdfPTable(live_vehiclestatus_headers.length);
				table.setSpacingBefore(10);
				for (int i = 0; i < live_vehiclestatus_headers.length; i++) {
					String header = live_vehiclestatus_headers[i];
					PdfPCell cell = new PdfPCell();
					cell.setGrayFill(0.9f);
					cell.setPhrase(new Phrase(header.toUpperCase(), new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD)));
					table.addCell(cell);
				}
			}
			table.completeRow();
			TripDetailsDaoImpl tripDetailsDaoImpl = (TripDetailsDaoImpl)DBManager.getInstance().getDao(DAOEnum.TRIP_DETAILS_DAO);
			List<TripDetails> tripDetailsList = tripDetailsDaoImpl.getActiveTripDetailsWithLiveStatusForTheUser(
					new LongPrimaryKey(SessionUtils.getCurrentlyLoggedInUser().getId()));
			if (tripDetailsList.size() != 0){
				Collections.sort(tripDetailsList, new Comparator<TripDetails>() {
					public int compare(TripDetails o1, TripDetails o2) {
						return o1.getVehicle().getDisplayName().compareTo(o2.getVehicle().getDisplayName());
					}});
				for (int j = 0; j < tripDetailsList.size(); j++) {
					StringBuffer location=new StringBuffer();
					TripDetails mObject=(TripDetails) tripDetailsList.get(j);
					double a = mObject.getDynamicTripStatus().getLocation().getFirstPoint().y;
					double b = mObject.getDynamicTripStatus().getLocation().getFirstPoint().x;
					Address locationFetch = GeoUtils.fetchNearestLocationFromCache(a, b);
					if(locationFetch !=null){
						location.append(StringUtils.formulateAddress(locationFetch,mObject.getVehicle().getId().getId(),a,b).toString());
					}else{
						location.append(StringUtils.addressFetchDisabled(mObject.getVehicle().getId().getId(),a,b).toString());
					}
					if( Boolean.valueOf(EnvironmentInfo.getProperty("IS_FRS_EMRI_CLIENT"))){
						List<EmriVehiclesBaseStation> emriList = ((EmriVehiclesBaseStationDaoImp)DBManager.getInstance().
								getDao(DAOEnum.EMRI_RAJASTHAN_DAO)).selectByVehicleId(mObject.getVehicle().getId().getId());
						if(emriList != null && emriList.size() > 0){
							Long emriRajasthanId = emriList.get(0).getID().getId();
							cachedEmriRajasthan = LoadEmriVehiclesBaseStationDetails.getInstance().retrieve(emriRajasthanId);
							LOG.debug("vehicleId: "+mObject.getVehicle().getId().getId());
						}
						PdfPCell cell = new PdfPCell();

						cell.setPhrase(new Phrase(j+1+"", new Font(
								Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
						table.addCell(cell);

						cell = new PdfPCell();
						cell.setPhrase(new Phrase(mObject.getVehicle().getDisplayName(), new Font(Font.FontFamily.HELVETICA,
								10, Font.NORMAL)));
						table.addCell(cell);
						if(cachedEmriRajasthan != null){
							cell = new PdfPCell();
							cell.setPhrase(new Phrase(cachedEmriRajasthan.getDistrict(), new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase(cachedEmriRajasthan.getBaseLocation(), new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase(cachedEmriRajasthan.getCrewNo()+"", new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

						}else{
							cell = new PdfPCell();
							cell.setPhrase(new Phrase("No District Available", new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase("No Base Location Available", new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase("No Crew No Available", new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

						}

						cell = new PdfPCell();
						cell.setPhrase(new Phrase(mObject.getVehicle().getStatus(mObject.getVehicle().getImei()), new Font(
								Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
						table.addCell(cell);

						cell = new PdfPCell();
						cell.setPhrase(new Phrase(mObject.getDynamicTripStatus().getMaxSpeed()+"", new Font(Font.FontFamily.HELVETICA, 10,
								Font.NORMAL)));
						table.addCell(cell);

						cell = new PdfPCell();
						cell.setPhrase(new Phrase(location.toString(), new Font(Font.FontFamily.HELVETICA, 10,
								Font.NORMAL)));
						table.addCell(cell);

						cell = new PdfPCell();
						cell.setPhrase(new Phrase(mObject.getDynamicTripStatus().getLastUpdatedAt().toString(), new Font(Font.FontFamily.HELVETICA, 10,
								Font.NORMAL)));
						table.addCell(cell);

						cell = new PdfPCell();
						cell.setPhrase(new Phrase(mObject.getVehicle().getImei(), new Font(
								Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
						table.addCell(cell);

					}else{

						PdfPCell cell = new PdfPCell();

						cell.setPhrase(new Phrase(j+1+"", new Font(
								Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
						table.addCell(cell);

						cell = new PdfPCell();
						cell.setPhrase(new Phrase(mObject.getVehicle().getDisplayName(), new Font(Font.FontFamily.HELVETICA,
								10, Font.NORMAL)));
						table.addCell(cell);

						cell = new PdfPCell();
						cell.setPhrase(new Phrase(mObject.getVehicle().getStatus(mObject.getVehicle().getImei()), new Font(
								Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
						table.addCell(cell);

						cell = new PdfPCell();
						cell.setPhrase(new Phrase(mObject.getDynamicTripStatus().isChargerConnected()?"Yes": "No", new Font(Font.FontFamily.HELVETICA, 10,
								Font.NORMAL)));
						table.addCell(cell);

						cell = new PdfPCell();
						cell.setPhrase(new Phrase(location.toString(), new Font(Font.FontFamily.HELVETICA, 10,
								Font.NORMAL)));
						table.addCell(cell);

						cell = new PdfPCell();
						cell.setPhrase(new Phrase(getGpsStrength(mObject.getDynamicTripStatus().getGpsStrength()), new Font(Font.FontFamily.HELVETICA, 10,
								Font.NORMAL)));
						table.addCell(cell);

						cell = new PdfPCell();
						cell.setPhrase(new Phrase(getGSMStrengthFromLevel(mObject.getDynamicTripStatus().getGsmStrength()), new Font(Font.FontFamily.HELVETICA, 10,
								Font.NORMAL)));
						table.addCell(cell);

						cell = new PdfPCell();
						cell.setPhrase(new Phrase(getBatteryStrengthFromLevel(mObject.getDynamicTripStatus().getBatteryVoltage()), new Font(Font.FontFamily.HELVETICA, 10,
								Font.NORMAL)));
						table.addCell(cell);

						cell = new PdfPCell();
						cell.setPhrase(new Phrase(mObject.getDynamicTripStatus().getLastUpdatedAt().toString(), new Font(Font.FontFamily.HELVETICA, 10,
								Font.NORMAL)));
						table.addCell(cell);

					}


					table.completeRow();
				}
				document.add(table);
			}
		} catch (DocumentException e) {
			LOG.error("in error "+e.getMessage());
		} catch (FileNotFoundException e) {
			LOG.error("in error "+e.getMessage());
		} finally {
			document.close();
		}
		return pdfFileName;
	}

	@SuppressWarnings({ "deprecation" })
	private String generateVehicleIdlePointsReportPDf() {
		// Create a new document.
		Document document = new Document(PageSize.LETTER.rotate());
		try {
			// Get an instance of PdfWriter and create a Table.pdf file as an output.
			File mFile=new File(pdfFilePath);
			List<Trip> trips = ((TripDaoImp) DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).selectTripByVehicleId(vehicleIdLong);
			try {
				mFile.createNewFile(); 
			} catch (IOException e) {
				LOG.error("in io exception "+e.getMessage());
			}
			PdfWriter.getInstance(document, new FileOutputStream(mFile));
			document.open();
			String imageUrl = EnvironmentInfo.getProperty("SERVER_URL")+"/static/img/header/fleet-logo2.png";
			try{  
				Image image = Image.getInstance(new URL(imageUrl));
				image.setAlignment(Element.HEADER);
				document.add(image);
			}catch (Exception e) {
				LOG.error("in error "+e.getMessage());
			}
			Font titleFont = new Font();
			titleFont.setSize(20);
			titleFont.setStyle(Font.BOLDITALIC);
			Paragraph mTitle=new Paragraph(vehicleName, titleFont);
			mTitle.setAlignment(Element.ALIGN_CENTER);
			document.add(mTitle);
			Font titleFont1 = new Font();
			titleFont1.setSize(16);
			titleFont.setStyle(Font.ITALIC);
			Paragraph mParagraph=new Paragraph("IDLE POINTS Report for the selected vehicle between "+startdate+" and "+enddate, titleFont1);
			document.add(mParagraph);
			PdfPTable table = new PdfPTable(idle_points_report_headers.length);
			table.setSpacingBefore(10);
			for (int i = 0; i < idle_points_report_headers.length; i++) {
				String header = idle_points_report_headers[i];
				PdfPCell cell = new PdfPCell();
				cell.setGrayFill(0.9f);
				cell.setPhrase(new Phrase(header.toUpperCase(), new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD)));
				table.addCell(cell);
			}
			table.completeRow();
			for (int i = 0; i < trips.size(); i++) {
				Trip trip = trips.get(i);
				List<IdlePoints> idlePointResultset = ((IdlePointsDaoImp) DBManager.getInstance().getDao(DAOEnum.IDLE_POINTS_DAO))
						.selectAllIdlePointsBetweenDates(trip.getId().getId(), dateRange.getStart(),dateRange.getEnd());

				for (int j = 0; j < idlePointResultset.size(); j++) {
					IdlePoints idlePoints = idlePointResultset.get(j);
					Date clientTime = new Date(localTime);
					Long startTimeInMillis = DateUtils.adjustToLocalTime(idlePoints.getStarttime(), clientTime).getTime();
					Long endTimeInMillis = DateUtils.adjustToLocalTime(idlePoints.getEndtime(), clientTime).getTime();
					String time=DateUtils.formatTimeDifference(endTimeInMillis,startTimeInMillis);
					Address locationFetch = GeoUtils.fetchNearestLocation(idlePoints.getIdleLocation().getFirstPoint().y, 
							idlePoints.getIdleLocation().getFirstPoint().x,false);
					StringBuffer location=StringUtils.formulateAddress(locationFetch, vehicleIdLong, idlePoints.getIdleLocation().
							getFirstPoint().y,idlePoints.getIdleLocation().getFirstPoint().x);
					PdfPCell cell = new PdfPCell();
					cell.setPhrase(new Phrase(DateUtils.adjustToClientTime(localTimeZone, idlePoints.getStarttime()), new Font(
							Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
					table.addCell(cell);

					cell = new PdfPCell();
					cell.setPhrase(new Phrase(DateUtils.adjustToClientTime(localTimeZone, idlePoints.getEndtime()), new Font(Font.FontFamily.HELVETICA,
							10, Font.NORMAL)));
					table.addCell(cell);

					cell = new PdfPCell();
					cell.setPhrase(new Phrase(time, new Font(
							Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
					table.addCell(cell);

					cell = new PdfPCell();
					cell.setPhrase(new Phrase(location.toString(), new Font(Font.FontFamily.HELVETICA, 10,
							Font.NORMAL)));
					table.addCell(cell);

					table.completeRow();
				}
			}
			document.add(table);
		} catch (DocumentException e) {
			LOG.error("in error "+e.getMessage());
		} catch (FileNotFoundException e) {
			LOG.error("in error "+e.getMessage());
		} finally {
			document.close();
		}
		return pdfFileName;
	}


	@SuppressWarnings({ "deprecation" })
	private String generateDriverIdlePointsReportPDf() {
		// Create a new document.
		Document document = new Document(PageSize.LETTER.rotate());
		try {
			// Get an instance of PdfWriter and create a Table.pdf file as an output.
			File mFile=new File(pdfFilePath);
			List<Trip> trips = ((TripDaoImp) DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).selectTripByDriverId(vehicleIdLong);
			try {
				mFile.createNewFile(); 
			} catch (IOException e) {
				LOG.error("in io exception "+e.getMessage());
			}
			PdfWriter.getInstance(document, new FileOutputStream(mFile));
			document.open();
			String imageUrl = EnvironmentInfo.getProperty("SERVER_URL")+"/static/img/header/fleet-logo2.png";
			try{  
				Image image = Image.getInstance(new URL(imageUrl));
				image.setAlignment(Element.HEADER);
				document.add(image);
			}catch (Exception e) {
				LOG.error("in error "+e.getMessage());
			}
			Font titleFont = new Font();
			titleFont.setSize(20);
			titleFont.setStyle(Font.BOLDITALIC);
			Paragraph mTitle=new Paragraph(vehicleName, titleFont);
			mTitle.setAlignment(Element.ALIGN_CENTER);
			document.add(mTitle);
			Font titleFont1 = new Font();
			titleFont1.setSize(16);
			titleFont.setStyle(Font.ITALIC);
			Paragraph mParagraph=new Paragraph("IDLE POINTS Report for the selected driver between "+startdate+" and "+enddate, titleFont1);
			document.add(mParagraph);
			PdfPTable table = new PdfPTable(idle_points_report_headers.length);
			table.setSpacingBefore(10);
			for (int i = 0; i < idle_points_report_headers.length; i++) {
				String header = idle_points_report_headers[i];
				PdfPCell cell = new PdfPCell();
				cell.setGrayFill(0.9f);
				cell.setPhrase(new Phrase(header.toUpperCase(), new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD)));
				table.addCell(cell);
			}
			table.completeRow();
			for (int i = 0; i < trips.size(); i++) {
				Trip trip = trips.get(i);
				List<IdlePoints> idlePointResultset = ((IdlePointsDaoImp) DBManager.getInstance().getDao(DAOEnum.IDLE_POINTS_DAO))
						.selectAllIdlePointsBetweenDates(trip.getId().getId(), dateRange.getStart(),dateRange.getEnd());

				for (int j = 0; j < idlePointResultset.size(); j++) {
					IdlePoints idlePoints = idlePointResultset.get(j);
					Date clientTime = new Date(localTime);
					Long startTimeInMillis = DateUtils.adjustToLocalTime(idlePoints.getStarttime(), clientTime).getTime();
					Long endTimeInMillis = DateUtils.adjustToLocalTime(idlePoints.getEndtime(), clientTime).getTime();
					String time=DateUtils.formatTimeDifference(endTimeInMillis,startTimeInMillis);
					Address locationFetch = GeoUtils.fetchNearestLocation(idlePoints.getIdleLocation().getFirstPoint().y, 
							idlePoints.getIdleLocation().getFirstPoint().x,false);
					StringBuffer location=StringUtils.formulateAddress(locationFetch, vehicleIdLong, idlePoints.getIdleLocation().
							getFirstPoint().y,idlePoints.getIdleLocation().getFirstPoint().x);
					PdfPCell cell = new PdfPCell();
					cell.setPhrase(new Phrase(DateUtils.adjustToClientTime(localTimeZone, idlePoints.getStarttime()), new Font(
							Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
					table.addCell(cell);

					cell = new PdfPCell();
					cell.setPhrase(new Phrase(DateUtils.adjustToClientTime(localTimeZone, idlePoints.getEndtime()), new Font(Font.FontFamily.HELVETICA,
							10, Font.NORMAL)));
					table.addCell(cell);

					cell = new PdfPCell();
					cell.setPhrase(new Phrase(time, new Font(
							Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
					table.addCell(cell);

					cell = new PdfPCell();
					cell.setPhrase(new Phrase(location.toString(), new Font(Font.FontFamily.HELVETICA, 10,
							Font.NORMAL)));
					table.addCell(cell);

					table.completeRow();
				}
			}
			document.add(table);
		} catch (DocumentException e) {
			LOG.error("Error In Driver Idle Point Report "+e.getMessage());
		} catch (FileNotFoundException e) {
			LOG.error("in error "+e.getMessage());
		} finally {
			document.close();
		}
		return pdfFileName;
	}

	private String generateVehicleStatsReportPDf() {
		// Create a new document.
		Document document = new Document(PageSize.LETTER.rotate());
		try {
			// Get an instance of PdfWriter and create a Table.pdf file as an output.
			File mFile=new File(pdfFilePath);
			User user = SessionUtils.getCurrentlyLoggedInUser();
			Long uid = user.getId();
			List<TripDetails> tripList = ((TripDetailsDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRIP_DETAILS_DAO))
					.getActiveTripDetailsWithLiveStatusForTheUser(uid);
			try {
				mFile.createNewFile(); 
			} catch (IOException e) {
				LOG.info("in io exception  "+e.getMessage());
			}
			PdfWriter.getInstance(document, new FileOutputStream(mFile));
			document.open();
			String imageUrl = EnvironmentInfo.getProperty("SERVER_URL")+"/static/img/header/fleet-logo2.png";
			try{  
				Image image = Image.getInstance(new URL(imageUrl));
				image.setAlignment(Element.HEADER);
				document.add(image);
			}catch (Exception e) {
				LOG.error("Error in vehiclestats PDF file"+e.getMessage());
			}
			Font titleFont = new Font();
			titleFont.setSize(20);
			titleFont.setStyle(Font.BOLDITALIC);
			Paragraph mTitle=new Paragraph("Vehicle Stats Report", titleFont);
			mTitle.setAlignment(Element.ALIGN_CENTER);
			document.add(mTitle);
			Font titleFont1 = new Font();
			titleFont1.setSize(16);
			titleFont.setStyle(Font.ITALIC);
			Paragraph mParagraph=new Paragraph("Vehicle Statistics Report for the available vehicles between "+startdate+" and "+enddate, titleFont1);
			document.add(mParagraph);
			PdfPTable table = new PdfPTable(vehiclestats_report_headers.length);
			table.setSpacingBefore(10);
			for (int k = 0; k < vehiclestats_report_headers.length; k++) {
				String header = vehiclestats_report_headers[k];
				PdfPCell cell = new PdfPCell();
				cell.setGrayFill(0.9f);
				cell.setPhrase(new Phrase(header.toUpperCase(), new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD)));
				table.addCell(cell);
			}
			table.completeRow();
			PdfPCell cell = new PdfPCell();
			DecimalFormat df = new DecimalFormat("0.##");
			if (tripList != null) {
				for (int i = 0; i < tripList.size(); i++) {
					float maxSpeed = 0;
					float distance = 0;
					float avgSpeed = 0;
					Vehicle vid=tripList.get(i).getVehicle();
					if (vid != null) {
						List<TrackHistory> trackEntries = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO)).selectBetweenDates(vid.getId().getId(), dateRange.getStart(), dateRange.getEnd());
						if(trackEntries.size()>0){
							// This function will return the start location of the vehicle from the track history table
							TrackHistory firstTrackPoint = trackEntries.get(0);//vehiclestartlocation.get(0);
							double a = firstTrackPoint.getLocation().getFirstPoint().getY();
							double b = firstTrackPoint.getLocation().getFirstPoint().getX();
							StringBuffer startlocation= new StringBuffer();
							startlocation.append(a+":"+b);
							LOG.debug("Start Location Latitude "+a+" Longitude "+b);

							if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_VEHICLE_STATS_ENABLED"))){
								Address locationFetch = GeoUtils.fetchNearestLocation(a, b, false);
								startlocation=StringUtils.formulateAddress(locationFetch,vid.getId().getId(),a, b);
							}else {
								startlocation.append("STATS ");
								startlocation.append(StringUtils.addressFetchDisabled(vid.getId().getId(),a, b).toString());
							}
							TrackHistory lastTrackPoint = trackEntries.get(trackEntries.size()-1);//tracklist.get(0);
							double x = lastTrackPoint.getLocation().getFirstPoint().getY();
							double y = lastTrackPoint.getLocation().getFirstPoint().getX();
							LOG.debug("End Location Location Latitude "+x+" Longitude "+y);
							StringBuffer endLoaction=new StringBuffer();
							if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_VEHICLE_STATS_ENABLED"))){
								Address endLocationFetch = GeoUtils.fetchNearestLocation(x, y,false);
								endLoaction=StringUtils.formulateAddress(endLocationFetch,vid.getId().getId(), x, y);
							} else {
								endLoaction.append("STATS ");
								endLoaction.append(StringUtils.addressFetchDisabled(vid.getId().getId(), x, y).toString());
							}
							for (TrackHistory mHistory: trackEntries) {
								avgSpeed=avgSpeed+mHistory.getSpeed();
								distance=distance+mHistory.getDistance();
								if(maxSpeed < mHistory.getSpeed()){
									maxSpeed=mHistory.getSpeed();
								}
							}
							avgSpeed=avgSpeed/trackEntries.size();
							cell.setPhrase(new Phrase(String.valueOf(i+1), new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase(vid.getDisplayName(), new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase( firstTrackPoint.getOccurredat()+"", new Font(Font.FontFamily.HELVETICA,
									10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase(startlocation.toString(), new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase(lastTrackPoint.getOccurredat()+"", new Font(Font.FontFamily.HELVETICA, 10,
									Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase(endLoaction.toString(), new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase(String.valueOf(df.format(maxSpeed)), new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase(String.valueOf(df.format(avgSpeed)), new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase(String.valueOf(df.format(distance)), new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

						}else {
							cell.setPhrase(new Phrase(String.valueOf(i+1), new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase(vid.getDisplayName(), new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase( "", new Font(Font.FontFamily.HELVETICA,
									10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase("No Location Available", new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase("", new Font(Font.FontFamily.HELVETICA, 10,
									Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase("No Location Available", new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase(String.valueOf(0.0), new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase(String.valueOf(0.0), new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

							cell = new PdfPCell();
							cell.setPhrase(new Phrase(String.valueOf(0.0), new Font(
									Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
							table.addCell(cell);

						}
						table.completeRow();
					}
				}
			}
			document.add(table);
		} catch (DocumentException e) {
			LOG.error("in error "+e.getMessage());
		} catch (FileNotFoundException e) {
			LOG.error("in error "+e.getMessage());
		} finally {
			document.close();
		}
		return pdfFileName;
	}

	/**
	 * Creates a pdf file in the specified location and writes the list of violations for the user based on time interval
	 * @return
	 */
	private String generateViolationReportPDf() {
		// Create a new document.
		Document document = new Document(PageSize.LETTER.rotate());
		try {
			// Get an instance of PdfWriter and create a Table.pdf file as an output.
			File mFile=new File(pdfFilePath);
			List<AlertOrViolation> alertOrViolationList = ((AlertDaoImpl) DBManager.getInstance().getDao(DAOEnum.ALERT_DAO)).
					selectByUserIdAndDuration(SessionUtils.getCurrentlyLoggedInUser().getId(), dateRange.getStart(), dateRange.getEnd());
			try {
				mFile.createNewFile();
			} catch (IOException e) {
				LOG.error("in io exception "+e.getMessage());
			}
			PdfWriter.getInstance(document, new FileOutputStream(mFile));
			document.open();
			String imageUrl = EnvironmentInfo.getProperty("SERVER_URL")+"/static/img/header/fleet-logo2.png";
			try{  
				Image image = Image.getInstance(new URL(imageUrl));
				image.setAlignment(Element.HEADER);
				document.add(image);
			}catch (Exception e) {
				LOG.info("in error "+e.getMessage());
			}
			Font titleFont = new Font();
			titleFont.setSize(20);
			titleFont.setStyle(Font.BOLDITALIC);
			Paragraph mTitle=new Paragraph(vehicleName, titleFont);
			mTitle.setAlignment(Element.ALIGN_CENTER);
			document.add(mTitle);
			Font titleFont1 = new Font();
			titleFont1.setSize(16);
			titleFont.setStyle(Font.ITALIC);
			Paragraph mParagraph=new Paragraph("Violation Report for the available vehicles between "+startdate+" and "+enddate, titleFont1);
			document.add(mParagraph);
			PdfPTable table = new PdfPTable(violation_report_headers.length);
			table.setSpacingBefore(10);
			for (int i = 0; i < violation_report_headers.length; i++) {
				String header = violation_report_headers[i];
				PdfPCell cell = new PdfPCell();
				cell.setGrayFill(0.9f);
				cell.setPhrase(new Phrase(header.toUpperCase(), new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD)));
				table.addCell(cell);
			}
			table.completeRow();

			for (int i = 0; i < alertOrViolationList.size(); i++) {
				AlertOrViolation alert = alertOrViolationList.get(i);
				Address address = GeoUtils.fetchNearestLocation(alert.getAlertLocation().getFirstPoint().y,
						alert.getAlertLocation().getFirstPoint().x, false);
				StringBuffer location=StringUtils.formulateAddress(address, alert.getVehicleId(), 
						alert.getAlertLocation().getFirstPoint().y, alert.getAlertLocation().getFirstPoint().x);
				if(address != null){
					alert.setAlertLocationReferenceId(address.getId());
					alert.setAlertLocationText(location.toString());
				}

				PdfPCell cell = new PdfPCell();
				cell.setPhrase(new Phrase(i+"", new Font(
						Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setPhrase(new Phrase(LoadVehicleDetails.getInstance().retrieve(alert.getVehicleId()).getDisplayName(), new Font(
						Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setPhrase(new Phrase(LoadDriverDetails.getInstance().retrieve(alert.getDriverId()).getFirstName(), new Font(Font.FontFamily.HELVETICA,
						10, Font.NORMAL)));
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setPhrase(new Phrase( DateUtils.adjustToClientTime(localTimeZone, alert.getAlertTime()), new Font(
						Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setPhrase(new Phrase(alert.getAlertLocationText(), new Font(Font.FontFamily.HELVETICA, 10,
						Font.NORMAL)));
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setPhrase(new Phrase(alert.getAlertType().toString(), new Font(
						Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setPhrase(new Phrase(alert.getAlertTypeValue(), new Font(
						Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
				table.addCell(cell);

				table.completeRow();
			}
			document.add(table);
		} catch (DocumentException e) {
			LOG.error("in error "+e.getMessage());
		} catch (FileNotFoundException e) {
			LOG.error("in error "+e.getMessage());
		} finally {
			document.close();
		}
		return pdfFileName;
	}

	private String generateActivityReportPDf() {
		// Create a new document.
		Document document = new Document(PageSize.LETTER.rotate());
		try {
			File mFile=new File(pdfFilePath);
			List<TrackHistory> trackHistoryResultset;
			if(intervalInt == 0){
				trackHistoryResultset = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
						.selectBetweenDates(vehicleIdLong, dateRange.getStart(), dateRange.getEnd());
			}else{
				trackHistoryResultset = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
						.selectBetweenDatesIntervalNotZeroWithNoLimit(vehicleIdLong, dateRange.getStart(), dateRange.getEnd(),intervalInt);
			}

			List<TrackHistory> filteredResultSet = new ArrayList<TrackHistory>();
			float distance = 0.0F;

			/** If interval is zero we need fetch only first fifteen between the given start and end dated **/
			TrackHistory prevTrackHistory = null;
			for (int j = 0; j <trackHistoryResultset.size(); j++) {
				Geometry points = trackHistoryResultset.get(j).getLocation();
				if(prevTrackHistory != null){
					Geometry prevPoints = prevTrackHistory.getLocation();
					LOG.debug("Lat1, Lng1 : "+prevPoints.getFirstPoint().getY()+", "+prevPoints.getFirstPoint().getX()+
							" Lat2, Lng2 : "+points.getFirstPoint().getY()+", "+points.getFirstPoint().getX());
					float calculatedDistance = (float) CustomCoordinates.distance(prevPoints.getFirstPoint().getY(), prevPoints.getFirstPoint().getX(),
							points.getFirstPoint().getY(), points.getFirstPoint().getX());
					trackHistoryResultset.get(j).setDistance((float)(calculatedDistance + (0.1*calculatedDistance)));
				}
				prevTrackHistory = trackHistoryResultset.get(j);
				LOG.debug("Calculated distance : "+trackHistoryResultset.get(j).getDistance());
				distance += trackHistoryResultset.get(j).getDistance();
				LOG.debug("Cumulative distance : "+distance);
				trackHistoryResultset.get(j).setDistance(distance);
				filteredResultSet.add(trackHistoryResultset.get(j));
			}
			DecimalFormat df = new DecimalFormat("0.##");
			try {
				mFile.createNewFile();
			} catch (IOException e) {
				LOG.error("in io exception "+e.getMessage());
			}
			PdfWriter.getInstance(document, new FileOutputStream(mFile));
			document.open();
			String imageUrl = EnvironmentInfo.getProperty("SERVER_URL")+"/static/img/header/fleet-logo2.png";
			try{  
				Image image = Image.getInstance(new URL(imageUrl));
				image.setAlignment(Element.HEADER);
				document.add(image);
			}catch (Exception e) {
				LOG.info("in error message "+e.getMessage());
			}
			Font titleFont = new Font();
			titleFont.setSize(20);
			titleFont.setStyle(Font.BOLDITALIC);
			Paragraph mTitle=new Paragraph(vehicleName, titleFont);
			mTitle.setAlignment(Element.ALIGN_CENTER);
			document.add(mTitle);
			Font titleFont1 = new Font();
			titleFont1.setSize(16);
			titleFont.setStyle(Font.ITALIC);
			Paragraph mParagraph=new Paragraph("Activity Report for the selected vehicle between "+startdate+" and "+enddate, titleFont1);
			document.add(mParagraph);
			PdfPTable table = new PdfPTable(activity_report_headers.length);
			table.setSpacingBefore(10);
			for (int i = 0; i < activity_report_headers.length; i++) {
				String header = activity_report_headers[i];
				PdfPCell cell = new PdfPCell();
				cell.setGrayFill(0.9f);
				cell.setPhrase(new Phrase(header.toUpperCase(), new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD)));
				table.addCell(cell);
			}
			table.completeRow();
			for (int j=0; j<filteredResultSet.size(); j++) {

				TrackHistory tHistory = filteredResultSet.get(j);
				StringBuffer location=new StringBuffer();
				Geometry points = tHistory.getLocation();
				if (Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_ACTIVITY_REPORT_ENABLED"))) {
					Address locationFetch = GeoUtils.fetchNearestLocation(tHistory.getLocation().getFirstPoint().y, 
							tHistory.getLocation().getFirstPoint().x,false);
					location.append(StringUtils.formulateAddress(locationFetch, vehicleIdLong, tHistory.getLocation().getFirstPoint().y,
							tHistory.getLocation().getFirstPoint().x).toString());
				} else {
					location.append("Activity ");
					location.append(StringUtils.addressFetchDisabled(vehicleIdLong,tHistory.getLocation().getFirstPoint().y,
							tHistory.getLocation().getFirstPoint().x).toString());
				}

				PdfPCell cell = new PdfPCell();
				cell.setPhrase(new Phrase(DateUtils.adjustToClientTime(localTimeZone,tHistory.getOccurredat()), new Font(
						Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setPhrase(new Phrase(location.toString(), new Font(Font.FontFamily.HELVETICA,
						10, Font.NORMAL)));
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setPhrase(new Phrase(String.valueOf(points.getFirstPoint().getY()), new Font(
						Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setPhrase(new Phrase(String.valueOf(points.getFirstPoint().getX()), new Font(Font.FontFamily.HELVETICA, 10,
						Font.NORMAL)));
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setPhrase(new Phrase(String.valueOf(df.format(tHistory.getSpeed())), new Font(
						Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setPhrase(new Phrase(String.valueOf(df.format(tHistory.getDistance())), new Font(
						Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
				table.addCell(cell);
				table.completeRow();
			}
			document.add(table);
		} catch (DocumentException e) {
			LOG.error("in error "+e.getMessage());
		} catch (FileNotFoundException e) {
			LOG.error("in error "+e.getMessage());
		} finally {
			document.close();
		}
		return pdfFileName;
	}
	@SuppressWarnings("unchecked")
	private String generateVehicleHistoryReportPDf() {
		// Create a new document.
		Document document = new Document(PageSize.LETTER.rotate());
		try {
			File mFile=new File(pdfFilePath);
			if(mFile.exists()){
				return pdfFileName;
			}
			positionsData=(List<IDataset>) mDataToPrint.get("activity."+vehicleId+".positions");
			try {
				mFile.createNewFile();
			} catch (IOException e) {
				LOG.error("in io exception "+e.getMessage());
			}
			PdfWriter.getInstance(document, new FileOutputStream(mFile));
			document.open();
			String imageUrl = EnvironmentInfo.getProperty("SERVER_URL")+"/static/img/header/fleet-logo2.png";
			try{  
				Image image = Image.getInstance(new URL(imageUrl));
				image.setAlignment(Element.HEADER);
				document.add(image);
			}catch (Exception e) {
				LOG.error("in error "+e.getMessage());
			}
			Font titleFont = new Font();
			titleFont.setSize(20);
			titleFont.setStyle(Font.BOLDITALIC);
			Paragraph mTitle=new Paragraph(vehicleName, titleFont);
			mTitle.setAlignment(Element.ALIGN_CENTER);
			document.add(mTitle);
			Font titleFont1 = new Font();
			titleFont1.setSize(16);
			titleFont.setStyle(Font.ITALIC);
			Paragraph mParagraph=new Paragraph("Vehicle History Report for the selected vehicle between "+startdate+" and "+enddate, titleFont1);
			document.add(mParagraph);
			PdfPTable table = new PdfPTable(vehicle_history_report_headers.length);
			table.setSpacingBefore(10);
			for (int i = 0; i < vehicle_history_report_headers.length; i++) {
				String header = vehicle_history_report_headers[i];
				PdfPCell cell = new PdfPCell();
				cell.setGrayFill(0.9f);
				cell.setPhrase(new Phrase(header.toUpperCase(), new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD)));
				table.addCell(cell);
			}
			table.completeRow();

			for (int j = 0; j < positionsData.size(); j++) {
				IDataset mObject=(IDataset) positionsData.get(j);
				PdfPCell cell = new PdfPCell();

				cell.setPhrase(new Phrase(String.valueOf(j+1), new Font(
						Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
				table.addCell(cell);

				cell.setPhrase(new Phrase(mObject.getValue("imei"), new Font(
						Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setPhrase(new Phrase(mObject.getValue("updatedtime"), new Font(Font.FontFamily.HELVETICA,
						10, Font.NORMAL)));
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setPhrase(new Phrase(String.valueOf(mObject.get("updatedbyuser")), new Font(
						Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setPhrase(new Phrase(String.valueOf(mObject.get("vehicleattended")), new Font(Font.FontFamily.HELVETICA, 10,
						Font.NORMAL)));
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setPhrase(new Phrase(String.valueOf(mObject.get("batterychanged")), new Font(
						Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
				table.addCell(cell);

				cell = new PdfPCell();
				cell.setPhrase(new Phrase(String.valueOf(mObject.get("fusechanged")), new Font(
						Font.FontFamily.HELVETICA, 10, Font.NORMAL)));
				table.addCell(cell);
				table.completeRow();
			}
			document.add(table);
		} catch (DocumentException e) {
			LOG.error("in error "+e.getMessage());
		} catch (FileNotFoundException e) {
			LOG.error("in error "+e.getMessage());
		} finally {
			document.close();
		}
		return pdfFileName;
	}

}
