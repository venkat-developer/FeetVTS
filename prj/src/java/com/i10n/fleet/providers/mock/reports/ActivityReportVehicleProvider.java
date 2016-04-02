package com.i10n.fleet.providers.mock.reports;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.TrackHistoryDaoImpl;
import com.i10n.db.dao.TripDaoImp;
import com.i10n.db.entity.Address;
import com.i10n.db.entity.DateRange;
import com.i10n.db.entity.TrackHistory;
import com.i10n.db.entity.Trip;
import com.i10n.db.entity.TripDistancendSpeed;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.util.Constants;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.util.GeoUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * Mock : Data Provider for Activity Report. This class will be removed in
 * future.
 * 
 * @author Prashanth
 * 
 */
public class ActivityReportVehicleProvider implements IDataProvider {

	private static Logger LOG = Logger.getLogger(ActivityReportVehicleProvider.class);

	private String enddate;
	private String startdate;
	private Long vehicleId;
	private DateRange dateRange = new DateRange();
	private String localTime,localTimeZone;

	/**
	 * @see IDataProvider#getDataset(RequestParameters)
	 */
	public IDataset getDataset(RequestParameters params) {
		IDataset result = new Dataset();
		String vehicleid = params.getRequestParameter("vehicleID");
		String vehicleidVariable = StringUtils.getValueFromKVP(vehicleid);
		vehicleId = Long.parseLong(StringUtils.stripCommas(vehicleidVariable.trim()));
		localTime = params.getRequestParameter("localTime");
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		localTimeZone = (String) WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_TIMEZone);
		startdate = params.getRequestParameter("startdate");
		enddate = params.getRequestParameter("enddate");

		result.put("activity", decorateData(this.getData(getDataOptions(params))));
		return result;
	}

	private IDataset getData(IDataset Options) {
		dateRange=DateUtils.getModeOfReport(localTime, startdate, enddate);
		IDataset activityreportData = new Dataset();
		IDataset position = null;
		double distance = 0.0;
		double speed = 0;
		TrackHistoryDaoImpl trackhistoryDao = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO));
		List<TrackHistory> startResultset = trackhistoryDao.getStartDetails(vehicleId, dateRange.getStart(), dateRange.getEnd());
		List<TrackHistory> endResultset = trackhistoryDao.getEndDetails(vehicleId, dateRange.getStart(), dateRange.getEnd());
		List<Trip> activeTrips = ((TripDaoImp) DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).selectTripByVehicleId(vehicleId);
		List<TripDistancendSpeed> speedndDistanceResultset = trackhistoryDao.getTripSpeedandDistance(vehicleId, dateRange.getStart(), 
				dateRange.getEnd());
		String vehicleName = LoadVehicleDetails.getInstance().retrieve(vehicleId).getDisplayName();

		List<IDataset> positionsData = new ArrayList<IDataset>();
		double maxSpeed = Double.MIN_VALUE;

		for (int j = 0; j < startResultset.size() ; j++) {
			speed = startResultset.get(j).getSpeed();
			if (speed > maxSpeed) {
				maxSpeed = speed;
			}
			DecimalFormat df = new DecimalFormat("0.##");
			distance = Double.parseDouble(df.format(distance));
			position = new Dataset();

			Date startdate=activeTrips.get(0).getStartDate();
			Date enddate=activeTrips.get(0).getEndDate();

			position.put("tripid", startResultset.get(j).getTripid());
			position.put("startdate", DateUtils.adjustToClientTimeInMilliSeconds(localTimeZone,startdate));
			position.put("enddate", DateUtils.adjustToClientTimeInMilliSeconds(localTimeZone,enddate));
			if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_ACTIVITY_VEHICLE_REPORT_ENABLED"))){
				Address startLocation = GeoUtils.fetchNearestLocation(startResultset.get(j).getLocation().getFirstPoint().getY(),
						startResultset.get(j).getLocation().getFirstPoint().getX(),false);
				StringBuffer startlocation=new StringBuffer();
				startlocation.append(StringUtils.formulateAddress(startLocation, vehicleId, 
						startResultset.get(j).getLocation().getFirstPoint().getY(),startResultset.get(j).getLocation().getFirstPoint()
						.getX()).toString());
				position.put("startlocation", startlocation.toString());

				StringBuffer enddlocation=new StringBuffer();
				Address endLocation = GeoUtils.fetchNearestLocation(endResultset.get(j).getLocation().getFirstPoint().getY(),
						endResultset.get(j).getLocation().getFirstPoint().getX(),false);
				enddlocation.append(StringUtils.formulateAddress(endLocation, vehicleId,endResultset.get(j).getLocation().
						getFirstPoint().getY(),endResultset.get(j).getLocation().getFirstPoint().getX()).toString());
				position.put("endlocation", enddlocation.toString());

			} else {
				LOG.debug("Location fetch is disabled");
				position.put("endlocation", "No Location Available");
				position.put("startlocation", "No Location Available");

			}

			distance = startResultset.get(0).getCumulativeDistance();
			speed = startResultset.get(0).getSpeed();

			position.put("distance", speedndDistanceResultset.get(j).getDistance());
			position.put("speed", speedndDistanceResultset.get(j).getSpeed());

			positionsData.add(position);
		}

		activityreportData.put("vehicle-" + vehicleName+ ".trip-vehicle-" + vehicleName + ".track.positions.", positionsData);
		activityreportData.put("vehicle-" + vehicleName+ ".trip-vehicle-" + vehicleName + ".track.violations","");
		return activityreportData;
	}

	/**
	 * Decorates Trip Data for showing complete activity.
	 * 
	 * @param data
	 * @return
	 */
	private IDataset decorateData(IDataset data) {
		LOG.debug("In ActivityReportVehicleProvider decorateData starting");
		IDataset result = new Dataset();
		for (Entry<String, Object> vehicleEntry : data.entrySet()) {
			IDataset vehicleResult = new Dataset();
			IDataset vehicleData = (IDataset) vehicleEntry.getValue();
			List<IDataset> positions = new ArrayList<IDataset>();
			List<IDataset> idle = new ArrayList<IDataset>();
			List<IDataset> geo = new ArrayList<IDataset>();
			List<IDataset> overspeed = new ArrayList<IDataset>();
			for (Entry<String, Object> entry : vehicleData.entrySet()) {
				IDataset tripData = (IDataset) entry.getValue();
				addListToDataset(positions, tripData.get("track.positions"));
				addListToDataset(idle, tripData.get("track.violations.idle"));
				addListToDataset(geo, tripData.get("track.violations.geofencing"));
				addListToDataset(overspeed, tripData.get("track.violations.overspeeding"));
			}
			if (!positions.isEmpty()) {
				vehicleResult.put("positions", positions);
			}
			if (!idle.isEmpty()) {
				vehicleResult.put("violations.idle", idle);
			}
			if (!geo.isEmpty()) {
				vehicleResult.put("violations.geofencing", geo);
			}
			if (!overspeed.isEmpty()) {
				vehicleResult.put("violations.overspeeding", overspeed);
			}
			result.put(vehicleEntry.getKey(), vehicleResult);
		}
		LOG.debug("In ActivityReportVehicleProvider decorateData ending");
		return result;
	}

	/**
	 * Adds an object representing a list to a given list in dataset
	 * 
	 * @param data
	 * @param list
	 */
	@SuppressWarnings("unchecked")
	private void addListToDataset(List<IDataset> data, Object list) {
		if (null != list && list instanceof List) {
			data.addAll((List<IDataset>) list);
		}
	}

	/**
	 * Returns the data options for Trip Manager
	 * 
	 * @param params
	 * @return
	 */
	private IDataset getDataOptions(RequestParameters params) {
		LOG.debug("In ActivityReportVehicleProvider getDataOptions starting");
		IDataset options = new Dataset();
		String vehicleID = params.getParameter(RequestParams.vehicleID);
		if (null != vehicleID) {
			options.put("filter.vehicleid", vehicleID);
		}
		String startDate = params.getParameter(RequestParams.startdate);
		String endDate = params.getParameter(RequestParams.enddate);
		options.put("filter.startdate", startDate);
		options.put("filter.enddate", endDate);
		options.put("skip.track.violations.idle", true);
		options.put("skip.track.violations.geofencing", true);
		LOG.debug("In ActivityReportVehicleProvider getDataOptions ending");
		return options;
	}

	/**
	 * @see IDataProvider#getName()
	 */
	public String getName() {
		return "activityreportvehicle";
	}
}
