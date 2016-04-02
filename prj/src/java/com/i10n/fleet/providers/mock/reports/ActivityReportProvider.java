package com.i10n.fleet.providers.mock.reports;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.postgis.Geometry;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.TrackHistoryDaoImpl;
import com.i10n.db.entity.DateRange;
import com.i10n.db.entity.TrackHistory;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.util.Constants;
import com.i10n.fleet.util.CustomCoordinates;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;

/**
 * Mock : Data Provider for Activity Report. This class will be removed in
 * future.
 * 
 * @author Sabarish
 * 
 */
public class ActivityReportProvider implements IDataProvider {

	private static Logger LOG = Logger.getLogger(ActivityReportProvider.class);

	private String enddate;
	private String startdate, intervals, interval;
	private Long vehicleId;
	private String isFirstPage;
	private DateRange dateRange = new DateRange();
	private String localTime, localTimeZone = null;

	private int inter;

	/**
	 * @see IDataProvider#getDataset(RequestParameters)
	 */
	public IDataset getDataset(RequestParameters params) {
		IDataset result = new Dataset();
		String vehicleNameRequest = params.getRequestParameter("getvehiclename");
		String vehicleid = params.getRequestParameter("vehicleID");
		String vehicleidVariable = StringUtils.getValueFromKVP(vehicleid);
		vehicleId = Long.parseLong(StringUtils.stripCommas(vehicleidVariable.trim()));
		if(vehicleNameRequest != null){
			Vehicle vehicle = LoadVehicleDetails.getInstance().retrieve(vehicleId);
			if(vehicle != null){
				result.put("vehicle.name", vehicle.getDisplayName());
			} else {
				result.put("vehicle.name", vehicleid);
			}
		} else {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			localTimeZone = (String) WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_TIMEZone);
			localTime = params.getRequestParameter("localTime");
			startdate = params.getRequestParameter("startdate");
			enddate = params.getRequestParameter("enddate");
			intervals = params.getRequestParameter("interval");
			interval = StringUtils.stripSpace(intervals);
			inter = Integer.parseInt(interval);
			isFirstPage=params.getRequestParameter("isFirstPage");
			IDataset mDataset=this.getData();
			result.put("activity", mDataset);
			params.getSession().setAttribute("returnedData",result);
		}
		return result;
	}

	private IDataset getData() {
		dateRange=DateUtils.getModeOfReport(localTime,startdate,enddate); 
		IDataset position = null;
		IDataset vehicleData = new Dataset();
		List<TrackHistory> trackHistoryResultset;
		if(inter != 0 ){
			trackHistoryResultset = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
					.selectBetweenDatesIntervalNotZero(vehicleId, dateRange.getStart(), dateRange.getEnd(),inter);
		}else{
			trackHistoryResultset = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
					.selectBetweenDatesFroZeroInterval(vehicleId, dateRange.getStart(), dateRange.getEnd());
		}
		List<TrackHistory> filteredResultSet = new ArrayList<TrackHistory>();
		List<IDataset> positionsData = new ArrayList<IDataset>();
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
			if(isFirstPage !=null){

			}else{
				prevTrackHistory = trackHistoryResultset.get(j);
				LOG.debug("Calculated distance : "+trackHistoryResultset.get(j).getDistance());
				distance += trackHistoryResultset.get(j).getDistance();
				LOG.debug("Cumulative distance : "+distance);
				trackHistoryResultset.get(j).setDistance(distance);
			}
			filteredResultSet.add(trackHistoryResultset.get(j));
		}
		DecimalFormat df = new DecimalFormat("0.##");
		/** Data Extraction **/
		for (int i=0; i<filteredResultSet.size(); i++) {
			TrackHistory tHistory = filteredResultSet.get(i);
			position = new Dataset();
//			StringBuffer location=new StringBuffer();
//			if (Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_ACTIVITY_REPORT_ENABLED"))) {
//				Address locationFetch = GeoUtils.fetchNearestLocation(tHistory.getLocation().getFirstPoint().y, 
//						tHistory.getLocation().getFirstPoint().x,false);
//				location.append(StringUtils.formulateAddress(locationFetch, vehicleId, tHistory.getLocation().getFirstPoint().y,
//						tHistory.getLocation().getFirstPoint().x).toString());
//				position.put("location", location.toString());
//			} else {
//				location.append("Activity ");
//				location.append(StringUtils.addressFetchDisabled(vehicleId,tHistory.getLocation().getFirstPoint().y,
//						tHistory.getLocation().getFirstPoint().x).toString());
//				position.put("location", location.toString());
//			}
			position.put("location", tHistory.getLocation().getFirstPoint().y+":"+tHistory.getLocation().getFirstPoint().x);
			position.put("date", DateUtils.adjustToClientTime(localTimeZone,tHistory.getOccurredat()));
			Geometry points = tHistory.getLocation();
			position.put("lat", points.getFirstPoint().getY());
			position.put("lon", points.getFirstPoint().getX());
			position.put("distance", df.format(tHistory.getDistance()));
			position.put("speed", df.format(tHistory.getSpeed()));
			positionsData.add(position);
		}

		vehicleData.put("vehicle-" + vehicleId+ ".positions.",positionsData);
		return vehicleData;
	}



	/**
	 * @see IDataProvider#getName()
	 */
	public String getName() {
		return "activityreport";
	}
}
