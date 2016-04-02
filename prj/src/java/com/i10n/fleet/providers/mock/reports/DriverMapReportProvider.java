package com.i10n.fleet.providers.mock.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.IdlePointsDaoImp;
import com.i10n.db.dao.TrackHistoryDaoImpl;
import com.i10n.db.entity.Address;
import com.i10n.db.entity.DateRange;
import com.i10n.db.entity.IdlePoints;
import com.i10n.db.entity.TrackHistory;
import com.i10n.db.entity.Trip;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadTripDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.providers.mock.managers.IDriverManager;
import com.i10n.fleet.util.Constants;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.util.GeoUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * Data Provider for Driver Map Report.
 * 
 */
public class DriverMapReportProvider implements IDataProvider {

	private static Logger LOG = Logger.getLogger(DriverMapReportProvider.class);
	private IDriverManager m_driverManager;
	private Long driverId;
	private String startdate;
	private String enddate;
	private DateRange dateRange = new DateRange();
	private String localTime;
	private String localTimezone;


	/**
	 * 
	 * Gets the data corresponding to the timeframe and the driver selected
	 * 
	 * @see IDataProvider#getDataset(RequestParameters)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public IDataset getDataset(RequestParameters params) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		IDataset options = getTripsOptions(params);
		IDataset tripsData = new Dataset();

		String driverid = params.getRequestParameter("vehicleID");

		driverid = StringUtils.getValueFromKVP(driverid);

		// Ensures that the no commas are present in driver id
		driverId = Long.parseLong(StringUtils.stripCommas(driverid));
		localTime = params.getRequestParameter("localTime");
		Date clientTime = new Date(localTime);
		localTimezone = (String)WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_TIMEZone);
		startdate = params.getRequestParameter("startdate");
		enddate = params.getRequestParameter("enddate");
		dateRange=DateUtils.getModeOfReport(localTime, startdate, enddate);
		if (!options.isEmpty()) {

			IDataset drivermapreportData = null;
			IDataset position = null;
			IDataset idlepoint = null;
			List<Trip> resultset = LoadTripDetails.getInstance().fetchAllTripsForDriver(driverId);
			List<TrackHistory> trackHistoryResultset = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
			.selectBetweenDatesByDriverId(driverId, dateRange.getStart(), dateRange.getEnd());

			Trip trip = null;
			TrackHistory track = null;

			List<TrackHistory> disAndSpeedResultSet = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
			.getCumulativeDistanceAndMaxSpeedBetweenDatesByDriverId(driverId,dateRange.getStart(), dateRange.getEnd());
			List<IdlePoints> idlepoints = ((IdlePointsDaoImp) DBManager.getInstance().getDao(DAOEnum.IDLE_POINTS_DAO))
			.selectAllIdlePointsBetweenDatesForMapreportByDriverId(driverId, dateRange.getStart(), dateRange.getEnd());

			if (null != resultset) {
				drivermapreportData = new Dataset();
				for (int i = 0; i < resultset.size(); i++) {
					trip = resultset.get(i);

					drivermapreportData.put("driver-" + driverId+ ".trip-driver-" + driverId + ".id", trip.getId().getId()+ "");
					drivermapreportData.put("driver-" + driverId+ ".trip-driver-" + driverId + ".active", "true");
					drivermapreportData.put("driver-" + driverId+ ".trip-driver-" + driverId + ".name", trip.getTripName());
					drivermapreportData.put("driver-" + driverId+ ".trip-driver-" + driverId + ".startdate",
							DateUtils.adjustToLocalTime(trip.getStartDate(),clientTime)+ "");
					drivermapreportData.put("driver-" + driverId+ ".trip-driver-" + driverId + ".lastupdated","Wed Oct 07 15:46:26 IST 2009");
					for (int k = 0; k < disAndSpeedResultSet.size(); k++) {
						track = disAndSpeedResultSet.get(k);
						if (track.getTripid() == trip.getId().getId()) {
							drivermapreportData.put("driver-" + driverId+ ".trip-driver-" + driverId+ ".distance", track.getDistance());
							drivermapreportData.put("driver-" + driverId+ ".trip-driver-" + driverId+ ".maxspeed", track.getSpeed());
						}
					}
					drivermapreportData.put("driver-" + driverId+ ".trip-driver-" + driverId + ".status","started");
					drivermapreportData.put("driver-" + driverId+ ".trip-driver-" + driverId + ".idlepointlimit",trip.getIdlePointsTimeLimit() + "");
					drivermapreportData.put("driver-" + driverId+ ".trip-driver-" + driverId + ".driver", trip.getDriverId()+ "");
					List<IDataset> idlepointsdata = new ArrayList<IDataset>();

					if (!idlepoints.isEmpty()) {
						for (int k = 0; k < idlepoints.size(); k++) {
							IdlePoints idlpnt = idlepoints.get(k);

							idlepoint = new Dataset();
							Long startDateLong = DateUtils.adjustToLocalTime(idlpnt.getStarttime(), clientTime).getTime();
							Long endDateLong = DateUtils.adjustToLocalTime(idlpnt.getEndtime(), clientTime).getTime();
							long idletimeDiff = endDateLong - startDateLong;

							long difDays = idletimeDiff / (24 * 60 * 60 * 1000);
							long diffHours = idletimeDiff / (60 * 60 * 1000);
							if (diffHours >= 24) {
								diffHours = diffHours % 24;
							}
							long diffMinutes = idletimeDiff / (60 * 1000);
							if (diffMinutes >= 60) {
								diffMinutes = diffMinutes % 60;
							}
							long diffSeconds = idletimeDiff / (1000);
							if (diffSeconds >= 60) {
								diffSeconds = diffSeconds % 60;
							}

							double a = idlpnt.getIdleLocation().getFirstPoint().y;
							double b = idlpnt.getIdleLocation().getFirstPoint().x;
							//							if ((difDays > 1) || (diffHours > 1) || (diffMinutes >= IDLE_TIME)) {
							idlepoint.put("id", idlpnt.getId().getId());
							idlepoint.put("lat", idlpnt.getIdleLocation().getFirstPoint().y);
							idlepoint.put("lon", idlpnt.getIdleLocation().getFirstPoint().x);
							StringBuffer location=new StringBuffer();
							if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_DRIVER_MAP_ENABLED"))){
								Address locationFetch = GeoUtils.fetchNearestLocationFromDB(a, b);
								location.append(StringUtils.formulateAddressByDriverId(locationFetch, driverId, a, b));
								idlepoint.put("locationname", location.toString());

							}else{
								location.append("VMap ");
								location.append(StringUtils.addressFetchDisabledVyDriverId(driverId,a,b).toString());
								idlepoint.put("locationname", location.toString());
							}
							idlepoint.put("starttime", DateUtils.adjustToClientTimeInMilliSeconds(localTimezone, idlpnt.getStarttime()));
							idlepoint.put("endtime", DateUtils.adjustToClientTimeInMilliSeconds(localTimezone, idlpnt.getEndtime()));
							String idlehours = difDays + "Days "+ diffHours + "Hrs " + diffMinutes+ "Mins " + diffSeconds + "Secs";
							idlepoint.put("idlehours", idlehours);
							idlepoint.put("tripid", idlpnt.getTripid());
							idlepointsdata.add(idlepoint);
						}
						drivermapreportData.put("driver-" + driverId+ ".trip-driver-" + driverId+ ".idle.positions.", idlepointsdata);
					}else{
						drivermapreportData.put("driver-" + driverId+ ".trip-driver-" + driverId+ ".idle.positions.", idlepointsdata);
					}
					drivermapreportData.put("idlepoints", idlepoints);

					List<IDataset> positionsData = new ArrayList<IDataset>();

					if (!trackHistoryResultset.isEmpty()) {
						float prevDistance=trackHistoryResultset.get(0).getDistance();
						for (int j = 0; j < trackHistoryResultset.size(); j++) {

							TrackHistory trackHistory = trackHistoryResultset.get(j);
							position = new Dataset();
							float distance=trackHistory.getDistance();
							if(distance-prevDistance<10){

								position.put("date", DateUtils.adjustToClientTimeInMilliSeconds(localTimezone ,trackHistory.getOccurredat()));
								position.put("distance", trackHistory.getDistance());
								position.put("lat", trackHistory.getLocation().getFirstPoint().y);
								position.put("lon", trackHistory.getLocation().getFirstPoint().x);
								if(j==0 || j == (trackHistoryResultset.size()-1)){
									double a = trackHistory.getLocation().getFirstPoint().y;
									double b = trackHistory.getLocation().getFirstPoint().x;
									if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_DRIVER_MAP_ENABLED"))){
										Address locationFetch = GeoUtils.fetchNearestLocation(a, b,false);
										StringBuffer location=StringUtils.formulateAddress(locationFetch, driverId, a, b);
										position.put("location", location.toString());
									}else{
										StringBuffer location=new StringBuffer();
										location.append("VMap ");
										location.append(StringUtils.addressFetchDisabled(driverId, a, b).toString());
										position.put("location",location.toString());
									}
								}
								position.put("speed", trackHistory.getSpeed());
								position.put("gpssignal", trackHistory.getGpsSignal());
								position.put("gsmsignal", trackHistory.getGsmSignal());

								positionsData.add(position);
							}
							prevDistance=distance;
						}
						drivermapreportData.put("driver-" + driverId+ ".trip-driver-" + driverId+ ".track.positions.", positionsData);
					}
					drivermapreportData.put("driver-" + driverId+ ".trip-driver-" + driverId+ ".track.violations", "");
				}
				tripsData = drivermapreportData;
			}
		}
		LOG.debug("Successfully sending data for driver map report");
		return tripsData;
	}


	/**
	 * returns the data options for trip manager.
	 * 
	 * @param params
	 * @return
	 */
	private IDataset getTripsOptions(RequestParameters params) {
		IDataset options = new Dataset();
		String driverID = params.getParameter(RequestParams.driverID);
		if (null != driverID) {
			options.put("filter.driverid", driverID);
			options.put("startdate", startdate);
			options.put("enddate", enddate);
		}
		return options;
	}

	/**
	 * @see IDataProvider#getName()
	 */
	public String getName() {
		return "drivermapreport";
	}

	/**
	 * Returns the Driver Manager
	 * 
	 * @return
	 */
	public IDriverManager getDriverManager() {
		return m_driverManager;
	}

	/**
	 * Sets the Driver Manager
	 * 
	 * @param manager
	 */
	public void setDriverManager(IDriverManager manager) {
		m_driverManager = manager;
	}
}
