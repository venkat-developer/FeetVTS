package com.i10n.fleet.providers.mock.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.postgis.Geometry;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.TrackHistoryDaoImpl;
import com.i10n.db.dao.TripDaoImp;
import com.i10n.db.entity.TrackHistory;
import com.i10n.db.entity.Trip;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;

/**
 * Mock : Trips Manager : manages trips data. This class will be removed in
 * future
 * 
 * @author Sabarish
 * 
 */
public class TripManager extends AbstractDataManager implements ITripManager {

	private static final Logger LOG = Logger.getLogger(TripManager.class);

	private static final String FILE_DOCUMENT = "/mock/trips.xml";
	private IDataset m_data;

	String startdate=null,enddate=null;
	/**
	 * @see AbstractDataManager#getDocumentName()
	 */
	@Override
	protected String getDocumentName() {
		return FILE_DOCUMENT;
	}

	/**
	 * @see IDataManager#getData(IDataset)
	 */
	@Override
	public IDataset getData(IDataset options) {
		startdate = options.getValue("startdate");
		enddate = options.getValue("enddate");
		IDataset data = getParsedData();
		IDataset filteredData = filterData(data, options);
		return new Dataset(Collections.unmodifiableMap(filteredData));
	}

	/**
	 * filters the data based on the options given.
	 * 
	 * @param data
	 * @param options
	 * @return
	 */
	private IDataset filterData(IDataset data, IDataset options) {
		IDataset result = new Dataset();
		if (null != options) {
			options = options.copy();
			IDataset filterData = data;
			String vehicleID = options.getValue("filter.vehicleid");
			if (null != vehicleID) {
				IDataset vehicleData = data.getDataset(vehicleID);
				filterData = new Dataset();
				if (null != vehicleData) {
					filterData.put(vehicleID, vehicleData);
				}
				options.getDataset("filter").remove("vehicleid");
			}
			for (Entry<String, Object> entry : filterData.entrySet()) {
				IDataset processedData = filterOnTime((IDataset) entry.getValue(), options);
				options.getDataset("filter").remove("startdate");
				options.getDataset("filter").remove("enddate");
				result.put(entry.getKey(), processResult(processedData, options));
			}
		}
		return result;
	}

	/**
	 * Filters the Trip Data on Start Date and End Date.
	 * 
	 * @param processedData
	 * @param options
	 * @return
	 */
	@SuppressWarnings( { "deprecation" })
	private IDataset filterOnTime(IDataset processedData, IDataset options) {
		IDataset result = processedData;
		if (!options.getBoolean("skip.track")) {
			String startDateStr = options.getValue("filter.startdate");
			String endDateStr = options.getValue("filter.enddate");
			if (null != startDateStr && null != endDateStr) {
				result = new Dataset();
				Date startDate = new Date(startDateStr);
				Date endDate = new Date(endDateStr);
				for (Entry<String, Object> tripEntry : processedData.entrySet()) {
					String tripKey = tripEntry.getKey();
					IDataset tripDataset = (IDataset) tripEntry.getValue();
					for (Entry<String, Object> entry : tripDataset.entrySet()) {
						String key = entry.getKey();
						if ("track".equals(key)) {
							IDataset data = new Dataset();
							if (null != result.get(tripKey)) {
								data = result.getDataset(tripKey);
							} else {
								result.put(tripKey, data);
							}
							IDataset value = (IDataset) entry.getValue();
							addFilteredTrackData(data, "positions", value,options, startDate, endDate);
							addFilteredTrackData(data, "violations.idle", value, options, startDate, endDate);
							addFilteredTrackData(data, "violations.geofencing", value, options, startDate, endDate);
							addFilteredTrackData(data,"violations.overspeeding", value, options, startDate, endDate);
						} else {
							result.put(tripKey + "." + key, entry.getValue());
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Adds filtered data to the given dataset based on options and startDate
	 * and endDate.
	 * 
	 * @param data
	 * @param key
	 * @param input
	 * @param options
	 * @param startDate
	 * @param endDate
	 */
	@SuppressWarnings("unchecked")
	private void addFilteredTrackData(IDataset data, String key, IDataset input, IDataset options, Date startDate, Date endDate) {
		if (!options.getBoolean("skip.track." + key) && null != input.get(key)) {
			List<IDataset> filteredData = filterIndividualOnTime((List<IDataset>) input.get(key), startDate, endDate);
			data.put("track." + key, filteredData);
		}
	}

	/**
	 * Filters each individual entry on time.
	 * 
	 * @param data
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private List<IDataset> filterIndividualOnTime(List<IDataset> data, Date startDate, Date endDate) {
		List<IDataset> result = new ArrayList<IDataset>();
		for (IDataset entry : data) {
			String startDateStr = entry.getValue("date");
			if (null != startDateStr) {
				Date curDate = new Date(Long.parseLong(startDateStr));
				if (curDate.after(startDate) && curDate.before(endDate)) {
					result.add(entry);
				}
			}
		}
		return result;
	}

	/**
	 * parses the document to return a {@link IDataset} based on the
	 * document.Probable victim of Double Checked Locking. Should not be used in
	 * prod.
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private synchronized IDataset getParsedData() {
		IDataset vehiclemapreportData = new Dataset();
		IDataset position = null;
		List<Trip> resultset = ((TripDaoImp) DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).selectAll();
		Trip trip = null;
		if (null != resultset) {
			for (int i = 0; i < resultset.size(); i++) {
				trip = resultset.get(i);
				vehiclemapreportData.put("vehicle-" + trip.getVehicleId()+ ".trip-vehicle-" + trip.getVehicleId() + ".id", trip.getId().getId()+ "");
				vehiclemapreportData.put("vehicle-" + trip.getVehicleId()+ ".trip-vehicle-" + trip.getVehicleId() + ".active","true");
				vehiclemapreportData.put("vehicle-" + trip.getVehicleId()+ ".trip-vehicle-" + trip.getVehicleId() + ".name",trip.getTripName());
				vehiclemapreportData.put("vehicle-" + trip.getVehicleId() + ".trip-vehicle-"+ trip.getVehicleId() + ".startdate", trip.getStartDate()+ "");
				vehiclemapreportData.put("vehicle-" + trip.getVehicleId()+ ".trip-vehicle-" + trip.getVehicleId()+ ".lastupdated", "Wed Oct 07 15:46:26 IST 2009");
				vehiclemapreportData.put("vehicle-" + trip.getVehicleId()+ ".trip-vehicle-" + trip.getVehicleId() + ".location","Bommanahalli");
				vehiclemapreportData.put("vehicle-" + trip.getVehicleId()+ ".trip-vehicle-" + trip.getVehicleId() + ".distance",
						((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO)).getCumulativeDistance(trip.getId().getId())+ "");
				vehiclemapreportData.put("vehicle-" + trip.getVehicleId()+ ".trip-vehicle-" + trip.getVehicleId() + ".status","started");
				vehiclemapreportData.put("vehicle-" + trip.getVehicleId()+ ".trip-vehicle-" + trip.getVehicleId() + ".maxspeed",
						((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO)).getMaxSpeed(trip.getId().getId())+ "");
				vehiclemapreportData.put("vehicle-" + trip.getVehicleId()+ ".trip-vehicle-" + trip.getVehicleId()+ ".idlepointlimit", trip.getIdlePointsTimeLimit()+ "");
				vehiclemapreportData.put("vehicle-" + trip.getVehicleId()+ ".trip-vehicle-" + trip.getVehicleId() + ".driver",trip.getDriverId() + "");

				List<IDataset> positionsData = new ArrayList<IDataset>();
				List<TrackHistory> trackHistoryResultset = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
						.selectAllBetweenDates(trip.getId().getId(), new Date(startdate),new Date(enddate));

				for (int j = 0; j < trackHistoryResultset.size(); j++) {
					TrackHistory trackHistory = trackHistoryResultset.get(j);
					position = new Dataset();
					position.put("date", trackHistory.getOccurredat().getTime());
					position.put("distance", trackHistory.getDistance());
					
					Geometry points = trackHistory.getLocation();
					position.put("lat", points.getFirstPoint().y);
					position.put("location", " ");
					position.put("lon", points.getFirstPoint().x);
					position.put("speed", trackHistory.getSpeed());
					LOG.debug("VehicleId and MaxSpeed "+trackHistory.getId() +trackHistory.getSpeed());
					positionsData.add(position);
				}
				vehiclemapreportData.put("vehicle-" + trip.getVehicleId()+ ".trip-vehicle-" + trip.getVehicleId()+ ".track.positions.", positionsData);
				vehiclemapreportData.put("vehicle-" + trip.getVehicleId()+ ".trip-vehicle-" + trip.getVehicleId()+ ".track.violations", "");
			}
			m_data = vehiclemapreportData;
		}
		return m_data;
	}
}
