package com.i10n.dbCacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.TripDaoImp;
import com.i10n.db.entity.Trip;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;

/**
 *
 */
public class LoadTripDetails {

	private static final Logger LOG = Logger.getLogger(LoadTripDetails.class);
	static private LoadTripDetails _instance = null;

	private LoadTripDetails(){};

	public static LoadTripDetails getInstance() {
		if(null == _instance){
			_instance = new LoadTripDetails();
			_instance.loadData();
		}
		return _instance;
	}

	/**	Mapping TripId with its respective entity*/
	public ConcurrentHashMap<Long, Trip> cachedTrips = new ConcurrentHashMap<Long, Trip>();

	/**
	 * Retrieves Vehicle corresponding to the imei
	 * @param imei
	 * @return
	 */
	public Trip retrieve (long tripId) {
		// This may return null
		Trip trip = cachedTrips.get(tripId);
		if (trip != null) {
			return cachedTrips.get(tripId);			
		}
		else {
			/* Try to retrieve from DB */
			getDetailsForNewlyAddedTrips (tripId);
			return cachedTrips.get(tripId);	
		}		

	}

	/**
	 * Retrieves Vehicle corresponding to the imei
	 * @param imei
	 * @return
	 */
	public void store (Trip tripObject) {
		cachedTrips.put(tripObject.getId().getId(), tripObject);	
	}

	/**
	 * Does operation similar to table scan to fetch trip objects
	 * @param vehicleId
	 * @return
	 */
	public List<Trip> fetchAllTrips (long vehicleId) {
		List<Trip> trips= new ArrayList<Trip> ();
		for (Long tripId : cachedTrips.keySet()) {
			Trip trip = cachedTrips.get(tripId);
			if (trip.getVehicleId() == vehicleId) {
				trips.add(trip);
			}
		}
		return trips;
	}

	/**
	 * Does operation similar to table scan to fetch trip objects
	 * @param driverId
	 * @return
	 */
	public List<Trip> fetchAllTripsForDriver (long driverId) {
		List<Trip> trips= new ArrayList<Trip> ();
		for (Long tripId : cachedTrips.keySet()) {
			Trip trip = cachedTrips.get(tripId);
			if (trip.getDriverId() == driverId) {
				trips.add(trip);
			}
		}
		return trips;
	}

	private void loadData (){
		LOG.debug("Caching Trip Details ..... ");
		List<Trip> tripList = ((TripDaoImp)DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).selectAll();
		processCacheUpdate(tripList);
	}

	private void processCacheUpdate(List<Trip> tripList) {
		if(tripList != null){
			for(Trip trip : tripList){
				cachedTrips.put(trip.getId().getId(), trip);
			}
		}
	}

	public void refresh() {
		clearCache();
		loadData();
	}
	
	private void clearCache() {
		cachedTrips.clear();
	}

	public void getDetailsForNewlyAddedTrips(Long tripId){
			List<Trip> tripList = ((TripDaoImp)DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).selectByPrimaryKey(new LongPrimaryKey(tripId));
			processCacheUpdate(tripList);
	}

}
