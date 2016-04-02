package com.i10n.fleet.providers.mock;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.postgis.Point;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.EtaDisplayDaoImp;
import com.i10n.db.dao.RouteDeviationDaoImp;
import com.i10n.db.dao.RouteTrackDaoImp;
import com.i10n.db.dao.StopDeviationDaoImp;
import com.i10n.db.dao.StopHistoryDaoImp;
import com.i10n.db.dao.StudentHistoryDaoImp;
import com.i10n.db.dao.TimeDeviationDaoImp;
import com.i10n.db.dao.TrackHistoryDaoImpl;
import com.i10n.db.dao.TripMissDeviationDaoImp;
import com.i10n.db.entity.EtaDisplay;
import com.i10n.db.entity.RouteDeviation;
import com.i10n.db.entity.RouteSchedule;
import com.i10n.db.entity.RouteTrack;
import com.i10n.db.entity.Routes;
import com.i10n.db.entity.StopDeviation;
import com.i10n.db.entity.StopHistory;
import com.i10n.db.entity.Stops;
import com.i10n.db.entity.TimeDeviation;
import com.i10n.db.entity.TrackHistory;
import com.i10n.db.entity.TripMissDeviation;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.VehicleToRouteSchedule;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadActiveStopHistoryDetails;
import com.i10n.dbCacheManager.LoadActiveVehicleStopHistoryDetails;
import com.i10n.dbCacheManager.LoadEtaDisplayDetails;
import com.i10n.dbCacheManager.LoadRouteSchedule;
import com.i10n.dbCacheManager.LoadRouteTrackDetails;
import com.i10n.dbCacheManager.LoadRoutesDetails;
import com.i10n.dbCacheManager.LoadSASRouteDeviation;
import com.i10n.dbCacheManager.LoadSASStopDeviation;
import com.i10n.dbCacheManager.LoadSASTimeDeviations;
import com.i10n.dbCacheManager.LoadStopsDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.dbCacheManager.LoadVehicleRouteAssociationDetails;
import com.i10n.fleet.eta.ETAUtils;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.CustomCoordinates;
import com.i10n.fleet.util.EnvironmentInfo;

@SuppressWarnings("deprecation")
public class EtaDisplayDataHelper {

	private static Logger LOG = Logger.getLogger(EtaDisplayDataHelper.class);

	// StopHistory DAO
	private StopHistoryDaoImp stopHistoryDao = (StopHistoryDaoImp) DBManager
			.getInstance().getDao(DAOEnum.STOPHISTORY_DAO);

	// StudentHistory DAO
	@SuppressWarnings("unused")
	private StudentHistoryDaoImp studentHistoryDao = (StudentHistoryDaoImp) DBManager
	.getInstance().getDao(DAOEnum.STUDENT_HISTORY_DAO);

	// SAS TimeDeviation DAO
	private TimeDeviationDaoImp timeDeviationDao = (TimeDeviationDaoImp) DBManager
			.getInstance().getDao(DAOEnum.TIME_DEVIATIONS_DAO);

	// SAS TimeDeviation DAO
	private RouteDeviationDaoImp routeDeviationDao = (RouteDeviationDaoImp) DBManager
			.getInstance().getDao(DAOEnum.ROUTE_DEVIATIONS_DAO);

	// SAS TimeDeviation DAO
	private StopDeviationDaoImp stopDeviationDao = (StopDeviationDaoImp) DBManager
			.getInstance().getDao(DAOEnum.STOP_DEVIATIONS_DAO);

	// SAS Trip Miss DAO
	private TripMissDeviationDaoImp tripMissDao = (TripMissDeviationDaoImp) DBManager
			.getInstance().getDao(DAOEnum.TRIP_MISS_DEVIATIONS_DAO);

	// SAS EtaDisplay DAO
	private EtaDisplayDaoImp etaDisplayDao = (EtaDisplayDaoImp) DBManager
			.getInstance().getDao(DAOEnum.ETA_DISPLAY_DAO);

	// ETA RouteTrack DAO
	private RouteTrackDaoImp routeTrackDao = (RouteTrackDaoImp) DBManager
			.getInstance().getDao(DAOEnum.ROUTE_TRACK_DAO);

	// TrackHistory Dao
	private TrackHistoryDaoImpl trackHistoryDao = (TrackHistoryDaoImpl) DBManager
			.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO);


	/**
	 * This function process the ETA for the current update from the module associated to the vehicle
	 * @param vehicleId
	 * @param tripId
	 * @param IMEI
	 * @param trackHistoryId
	 * @param bulkDataTime
	 * @param moduleUpdateTime
	 * @param latitude
	 * @param longitude
	 * @param isPing
	 * @param statement
	 */
	public void processETA(long vehicleId, long tripId, String IMEI, long trackHistoryId, Date bulkDataTime, Date moduleUpdateTime,
			double latitude, double longitude, boolean isPing){
		LOG.debug("MBMC : Processing ETA for Vehicle Id "+vehicleId);
		//	Check whether the Vehicle Route Association is cached or not
		ConcurrentHashMap<VehicleToRouteSchedule,Boolean> routeListForTheVehicle = LoadVehicleRouteAssociationDetails.getInstance()
				.retrieve(vehicleId);

		if(routeListForTheVehicle == null){
			LOG.debug("ETAETA : Vehicle route Association for vehicle "+vehicleId+" is not cached");
			return;
		} else{
			LOG.debug("ETAETA : Vehicle route Association for vehicle "+vehicleId+" is cached");
		}

		//	VehicleRouteAssociation is  cached now or already cached

		VehicleToRouteSchedule activeVehicleToRouteSchedule = getActiveRouteForVehicle(routeListForTheVehicle, vehicleId); 

		if (activeVehicleToRouteSchedule == null) {
			if(isPing){
				LOG.debug("ETAETA : This is a ping packet and no route is active for the Vehicle : "+vehicleId);
				return;
			}
			// No Route active and hence Check for any route starting
			RouteSchedule routeSchedule =  getRouteStartingDetails(routeListForTheVehicle, vehicleId, bulkDataTime, 
					latitude, longitude);
			LOG.debug("MBMC : routeSchedule is "+routeSchedule);
			if (routeSchedule != null) {
				LOG.debug("MBMC : routeSchedule is not null ");
				Long routeId = routeSchedule.getRouteId();

				// A route is ready to start. Spawn the route and make it active
				updateStopHistory(vehicleId, routeSchedule, 0L, bulkDataTime, trackHistoryId);
				activeVehicleToRouteSchedule = getActiveRouteForVehicle(LoadVehicleRouteAssociationDetails.getInstance()
						.retrieve(vehicleId), vehicleId);

				{
					// Client Specific process
					if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_MBMC_CLIENT"))){
						updateETADisplayTime(vehicleId, activeVehicleToRouteSchedule, tripId, latitude, longitude);
						// Insert into Routetrack Table
						insertOrUpdateRouteTrack(IMEI, vehicleId, routeId, trackHistoryId, bulkDataTime, 0);
					}
				}

			} else {
				// No route spawning at this time
				LOG.debug("ETAETA : No Route Starting for Vehicle : "+vehicleId);
			}
		} else {
			// Route is already active. Check for the updates to be given/done
			Routes routeEntity = LoadRoutesDetails.getInstance().retrieve(activeVehicleToRouteSchedule.getRouteId());
			if(routeEntity == null){
				// Route is not cached. Hence can't proceed further
				LOG.debug("MBMC : routeEntity is Null "+routeEntity);
				return;
			}
			Long routeId = activeVehicleToRouteSchedule.getRouteId();
			LOG.debug("MBMC : route ID is "+routeId);
			if (isRouteToBeDeactivated(activeVehicleToRouteSchedule, routeEntity, bulkDataTime)) {
				LOG.debug("MBMC : isRouteToBeDeactivatedis true");
				// Time out of the route range, hence deactivating the route
				deactivateRoute(vehicleId, activeVehicleToRouteSchedule.getRouteId());
				insertOrUpdateRouteTrack(IMEI, vehicleId, activeVehicleToRouteSchedule.getRouteId(), 0, bulkDataTime, trackHistoryId);
				return;
			}

			if(isPing){
				LOG.debug("ETAETA : This is a ping packet and Route :"+activeVehicleToRouteSchedule.getRouteId()+" is still active for the Vehicle : "+vehicleId);
				return;
			}

			RouteSchedule routeSchedule = getNextStopReachedDetails(vehicleId, activeVehicleToRouteSchedule, latitude, longitude);

			if (routeSchedule != null) {

				// Next Stop is reached. Do necessary updates 
				// Update Stop History table with latest update
				StopHistory prevStop = getLatestStopHistoryEntity(vehicleId, routeId);

				float actualDistance = trackHistoryDao.getCumulativeDistanceBetweenStops(tripId, prevStop.getTrackHistoryId());

				updateStopHistory(vehicleId, routeSchedule, actualDistance, bulkDataTime, trackHistoryId);

				{
					// Client Specific processing 
					if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_MBMC_CLIENT"))){
						/**
						 * Delete old eta entries 
						 */
						deleteOlderEtaEntriesForThisVehicleAndRoute(routeSchedule, vehicleId, routeId);
					}
				}

				if (isLastStop(vehicleId, routeSchedule)) {
					// Deactivate the route on reaching last stop.
					deactivateRoute(vehicleId, routeId);
					{
						// Client Specific processing 
						if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_BMC_CLIENT"))){
							// Insert into Routetrack Table
							insertOrUpdateRouteTrack(IMEI, vehicleId, routeId, 0, bulkDataTime, trackHistoryId);
						}
					}

				} else {
					// Last stop not yet reached.

					{
						// Client Specific processing 
						if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_BMC_CLIENT"))|| 
								Boolean.valueOf(EnvironmentInfo.getProperty("IS_MBMC_CLIENT"))){
							//	Sending Data to Display.
							updateETADisplayTime(vehicleId, activeVehicleToRouteSchedule, tripId, latitude, longitude);
						}
					}
				}
			} else {
				//	Next stop not yet reached
				LOG.debug("ETAETA : Next stop not yet reached for Vehicle : "+vehicleId+" and Route : "+routeId);

				{
					// Client Specific processing 
					if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_BMC_CLIENT")) || 
							Boolean.valueOf(EnvironmentInfo.getProperty("IS_MBMC_CLIENT"))){
						StopHistory prevStop = getLatestStopHistoryEntity(vehicleId, routeId);
						if(prevStop != null){
							/**
							 * Delete old eat entries
							 */
							//deleteOlderEtaEntriesForThisVehicleAndRoute(prevStop.getStopId(), vehicleId, routeId, statement);
						}
						updateETADisplayTime(vehicleId, activeVehicleToRouteSchedule, tripId, latitude, longitude);
					}
				}
			}
		}
	}


	private void deleteOlderEtaEntriesForThisVehicleAndRoute(RouteSchedule routeSchedule, long vehicleId, Long routeId) {
		LOG.debug("ETAETA : Got Sequence number for deleting older entries : "+routeSchedule.getSequenceNumber());
		etaDisplayDao.deleteOnStopReaching(vehicleId, routeId, routeSchedule.getSequenceNumber());
		updateEtaDisplayCacheOnStopReaching(vehicleId, routeId, routeSchedule.getSequenceNumber());
	}

	private void updateEtaDisplayCacheOnStopReaching(long vehicleId, Long routeId, int sequenceNumber) {
		//		LoadEtaDisplayDetails.getInstance().refresh();
		LOG.debug("ETAETA : Updating Etadisplay cache on stop reaching for Vehicle : "+vehicleId+", Route : "+routeId+
				" and Stop with sequenceNumber : "+sequenceNumber);
		ConcurrentHashMap<Long, ConcurrentHashMap<Long, EtaDisplay>> routeList = LoadEtaDisplayDetails.getInstance().retrieve(vehicleId);
		if(routeList == null){
			LOG.error("ETAETA : RouteList Null. No EtaDisplay entity to update in cache for Vehicle : "+vehicleId);
			return;
		}
		ConcurrentHashMap<Long, EtaDisplay> stopList = routeList.get(routeId);
		if(stopList == null){
			LOG.error("ETAETA : StopList Null. No EtaDisplay entity to update in cache for Vehicle : "+vehicleId);
			return;
		}
		for(Long stopId : stopList.keySet()){
			EtaDisplay etaDisplay = stopList.get(stopId);
			if(etaDisplay.getSequneceNumber() <= sequenceNumber){
				stopList.remove(stopId);
				if(stopList.size() == 0){
					routeList.remove(routeId);
					if(routeList.size() == 0){
						LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.remove(vehicleId);
					}else{
						LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.put(vehicleId, routeList);
					}
				}
				routeList.put(routeId, stopList);
				LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.put(vehicleId, routeList);
			}
		}
	}

	public static Vector<EtaDisplay> getEtaDisplayList(long vehicleId) {
		Vector<EtaDisplay> etaDisplayList = new Vector<EtaDisplay>();
		ConcurrentHashMap<Long, ConcurrentHashMap<Long, EtaDisplay>> routeList = LoadEtaDisplayDetails.getInstance().retrieve(vehicleId);
		ConcurrentHashMap<Long, EtaDisplay> stopList = null;
		if(routeList == null){
			LOG.error("ETAETA : Route List is empty for Vehicle : "+vehicleId);
			return null;
		}
		for(Long routeId : routeList.keySet()){
			stopList = routeList.get(routeId);
			if(stopList == null){
				LOG.error("ETAETA : Stop List is empty for Vehicle : "+vehicleId+" and Route : "+routeId);
				continue;
			}
			for(Long stopId : stopList.keySet()){
				etaDisplayList.add(stopList.get(stopId));
			}
		}
		return etaDisplayList;
	}

	/**
	 * Function to send updated time to the ETA Display.
	 * @param activeVehicleToRouteSchedule 
	 * @param vehicleId 
	 * @param tripId 
	 * @param longitude 
	 * @param latitude 
	 * @param statement 
	 */
	private void updateETADisplayTime(long vehicleId, VehicleToRouteSchedule activeVehicleToRouteSchedule, long tripId, double latitude, double longitude) {
		LOG.debug("MBMC : updating ETADisplayTime for "+vehicleId+" routeId "+activeVehicleToRouteSchedule.getRouteId());
		/**
		 * Step 1 - Get the stops which are not yet reached.
		 * Step 2 - Predict the reaching time to all the unreached stops based on average speed
		 * and distance between lat long. Step 3 - If the time is less than ten
		 * minutes then send it to ETA display.
		 */
		Long routeId = activeVehicleToRouteSchedule.getRouteId();
		LOG.debug("ETAETA : Processing EtaDisplay Time for Vehicle : "+vehicleId+", Route : "+routeId);
		StopHistory latestStopHistoryEntity = getLatestStopHistoryEntity(vehicleId, routeId);
		StopHistory firstStopHistory = getFirstStopHistoryFromCache(vehicleId, routeId);
		Vector<RouteSchedule> nextStopDetails = getAllNextStops(activeVehicleToRouteSchedule, latestStopHistoryEntity);
		if(nextStopDetails == null){
			LOG.error("ETAETA : Unlikely event of nextStopDetails being null");
			return;
		}

		Vector<RouteTrack> routeTrackList = LoadRouteTrackDetails.getInstance().retrieve(routeId);
		if(routeTrackList == null){
			LOG.error("ETAETA : No RouteTrack list cached for route "+routeId);
		}

		Routes routeEntity = LoadRoutesDetails.getInstance().retrieve(routeId);
		Calendar stopHistoryCal = Calendar.getInstance();
		Date firstStopHistoryStartTime = new Date(firstStopHistory.getExpectedTime().getTime());
		stopHistoryCal.setTime(firstStopHistoryStartTime);
		Date day1range1 = new Date();
		Date day1range2 = new Date();
		Date day2range1 = new Date();
		Date day2range2 = new Date();
		Date day3range1 = new Date();
		Date day3range2 = new Date();

		for (int m = 0; m < 3; m++) {
			stopHistoryCal.add(Calendar.DATE, -((m + 1) * routeEntity.getSpanningDays()));
			stopHistoryCal.add(Calendar.MINUTE, -15);
			switch (m) {
			case 0:{ 
				day1range1 = stopHistoryCal.getTime();
				stopHistoryCal.add(Calendar.MINUTE, 30);
				day1range2 = stopHistoryCal.getTime();
			}
			break;
			case 1:{ 
				day2range1 = stopHistoryCal.getTime();
				stopHistoryCal.add(Calendar.MINUTE, 30);
				day2range2 = stopHistoryCal.getTime();
			}
			break;
			case 2:{ 
				day3range1 = stopHistoryCal.getTime();
				stopHistoryCal.add(Calendar.MINUTE, 30);
				day3range2 = stopHistoryCal.getTime();
			}
			break;
			default:
				break;
			}
		}
		for (int m = 0; m < 3; m++) {
			List<Long> processedStopIdList = new ArrayList<Long>();
			for (int i = 0; i < nextStopDetails.size(); i++) {
				double distanceToNextStop = 0;
				double arrivalTime = 0;
				double avgSpeed;
				Stops stopEntity = LoadStopsDetails.getInstance().retrieve(nextStopDetails.get(i).getStopId());
				if(stopEntity == null){
					/**	Stop entity is not cached and hence skipping the further processing.*/
					continue;
				}
				long stopId = stopEntity.getId().getId(); 
				if(processedStopIdList.contains(stopId)){
					continue;
				}else{
					processedStopIdList.add(stopId);
				}

				/**
				 * Fetch the nearest location packet and get the time from that
				 * packet for arrival time calculation.
				 */
				TrackHistory trackHistoryCurrentTimeEntity = null;
				if(routeTrackList != null){
					Iterator<RouteTrack> routeTrackIterator = routeTrackList.iterator();
					while(routeTrackIterator.hasNext()){
						RouteTrack routeTrack = routeTrackIterator.next();

						// Checking with the same route schedule for the same time instance in last 3 days
						switch (m) {
						case 0:
							if(!((routeTrack.getRouteStartDate().getTime() >= day1range1.getTime()) && 
									(routeTrack.getRouteStartDate().getTime() <= day1range2.getTime()))){
								continue;
							}							
							break;
						case 1:
							if(!((routeTrack.getRouteStartDate().getTime() >= day2range1.getTime()) && 
									(routeTrack.getRouteStartDate().getTime() <= day2range2.getTime()))){
								continue;
							}							
							break;
						case 2:
							if(!((routeTrack.getRouteStartDate().getTime() >= day3range1.getTime()) && 
									(routeTrack.getRouteStartDate().getTime() <= day3range2.getTime()))){
								continue;
							}							
							break;
						default:
							break;
						}

						if(!(trackHistoryCurrentTimeEntity == null)){
							LOG.debug("ETAETA : Fetching the nearest packet to the current location for Vehicle : "+vehicleId
									+", Route : "+routeId+" and Stop : "+stopId);
							trackHistoryCurrentTimeEntity = trackHistoryDao.fetchNearestLocatedPacketForTheCurrentLocation(tripId,
									latitude, longitude, routeTrack.getStartTrackHistoryId(),routeTrack.getEndTrackHistoryId());
							if(trackHistoryCurrentTimeEntity == null){
								LOG.debug("ETAETA : Nearest packet doesnt exists for current location Vehicle : "+vehicleId
										+", Route : "+routeId+" and Stop : "+stopId);
							}else{
								LOG.debug("ETAETA : Nearest packet exists for the current location for Vehicle : "+vehicleId
										+", Route : "+routeId+" and Stop : "+stopId);
							}
						}

						LOG.debug("ETAETA : Fetching the nearest packet for Vehicle : "+vehicleId+", Route : "+
								routeId+" and Stop : "+stopId);
						TrackHistory trackHistoryStopTimeEntity = trackHistoryDao.fetchNearestLocatedPacketForTheStop(tripId,
								stopEntity.getLatPoint(),stopEntity.getLonPoint(), routeTrack.getStartTrackHistoryId(), 
								routeTrack.getEndTrackHistoryId());
						if(trackHistoryStopTimeEntity == null){
							LOG.debug("ETAETA : Nearest packet doesnt exist for stop for Vehicle : "+vehicleId+", Route : "+
									routeId+" and Stop : "+stopId);
						}else{
							LOG.debug("ETAETA : Neareast packet exists for stop for  Vehicle : "+vehicleId+", Route : "+
									routeId+" and Stop : "+stopId);
						}

						if ((trackHistoryStopTimeEntity != null) && (trackHistoryCurrentTimeEntity != null)) {
							if (trackHistoryCurrentTimeEntity.getId().getId() > trackHistoryStopTimeEntity.getId().getId()) {
								LOG.debug("ETAETA : Current Location entity id > stop time entity Id for Vehicle : "+vehicleId+", Route : "+
										routeId+" and Stop : "+stopId);
								continue;
							}
							LOG.debug("ETAETA : Nearest packet exists for both stop and current location for Vehicle : "+vehicleId+", Route : "+
									routeId+" and Stop : "+stopId);
							Calendar diffCal1 = Calendar.getInstance();
							diffCal1.setTime(trackHistoryStopTimeEntity.getOccurredat());
							LOG.debug("ETAETA : StopPacketTime :"+ diffCal1.getTime().toString()
									+ " Trackhistory Id :"+ trackHistoryStopTimeEntity.getId().getId()+" for Vehicle : "+vehicleId+", Route : "+
									routeId+" and Stop : "+stopId);

							Calendar diffCal2 = Calendar.getInstance();
							diffCal2.setTime(trackHistoryCurrentTimeEntity.getOccurredat());
							LOG.debug("ETAETA : LocationPacketTime :"+ diffCal2.getTime().toString()+ " Trackhistory Id :"
									+ trackHistoryCurrentTimeEntity.getId().getId()+" for Vehicle : "+vehicleId+", Route : "+
									routeId+" and Stop : "+stopId);
							double tempArrivalTime = arrivalTime;

							if (arrivalTime == 0) {
								arrivalTime = ((diffCal1.getTimeInMillis() - diffCal2.getTimeInMillis()) / (60 * 1000)); // in minutes
							} else {
								arrivalTime = (arrivalTime + ((diffCal1.getTimeInMillis() - diffCal2.getTimeInMillis()) / (60 * 1000))) / 2;
							}
							if (arrivalTime < 0) {
								arrivalTime = tempArrivalTime;
							}
						}else{
							LOG.debug("ETAETA : Nearest packet doesnot exists for both stop and current location for Vehicle : "+
									vehicleId+", Route : "+routeId+" and Stop : "+stopId);
						}
					}
				}
				LOG.debug("ETAETA : Arrivaltime :"+arrivalTime+" for Vehicle : "+vehicleId+", Route : "+
						routeId+" and Stop : "+stopId);
				distanceToNextStop = CustomCoordinates.distance(new Point(latitude, longitude),
						new Point(stopEntity.getLatPoint(),stopEntity.getLonPoint())); 
				if (arrivalTime == 0) {
					avgSpeed = trackHistoryDao.getAvgSpeedForTheVehicleForLast15minutes(vehicleId);
					LOG.debug("ETAETA : Average speed since last 15 minutes : "+ avgSpeed+"for Vehicle : "+
							vehicleId+", Route : "+routeId+" and Stop : "+stopId);

					if (avgSpeed != 0) {
						// Predict the reaching time to next stops.
						LOG.debug("ETAETA : Predicting the arrivalTime for Vehicle : "+vehicleId+" in Route : "+routeId+" for Stop : "+stopId);
						arrivalTime = predictArrivalTime(avgSpeed,distanceToNextStop);
					} else {
						// Get Average Speed from last 10 TrackHistory entries.
						avgSpeed = trackHistoryDao.getAvgSpeedForTheVehicleFromLast10Entries(vehicleId);
						LOG.debug("ETAETA : Average speed from last ten entries for the trackHistory : "+
								avgSpeed+"for Vehicle : "+vehicleId+", Route : "+routeId+" and Stop : "+stopId);
						if (avgSpeed != 0) {
							LOG.debug("ETAETA : Predicting the arrivalTime for Vehicle : "+vehicleId+" in Route : "+routeId+" for Stop : "+stopId);
							arrivalTime = predictArrivalTime(avgSpeed,distanceToNextStop);
						} else {
							LOG.debug("ETAETA : Average Speed is zero for Vehicle : "+vehicleId+", Route : "+routeId+" and Stop : "+stopId);
							return;
						}
					}
					LOG.debug("ETAETA : Arrivaltime since last 15 minutes : "+arrivalTime+" for Vehicle : "+
							vehicleId+", Route : "+routeId+" and Stop : "+stopId);
				}

				// Update EtaDisplay table with integer value of predicted time.
				LOG.debug("ETAETA : Final arrivalTime : " + arrivalTime+" for Vehicle : "+vehicleId+", Route : "+
						routeId+" and Stop : "+stopId);
				if (((int) arrivalTime) != 0) {
					if (((int) arrivalTime) < 60) {
						/*EtaDisplay entity = */insertOrUpdateETA(vehicleId, routeId, stopEntity.getId().getId(),arrivalTime, 
								routeEntity.getRouteName(), nextStopDetails.get(i).getSequenceNumber());

					}
				}
			}
		}
	}

	/**
	 * Returns the list of the stops after the stop with the specified sequence number
	 * @param activeVehicleToRouteSchedule 
	 * 
	 * @param latestStopHistoryEntity
	 * @return
	 */
	private Vector<RouteSchedule> getAllNextStops(VehicleToRouteSchedule activeVehicleToRouteSchedule, StopHistory latestStopHistoryEntity) {
		LOG.debug("ETAETA : Getting all next stops next to the latest stop reached for the Vehicle : "+
				latestStopHistoryEntity.getVehicleId()+" and Route : "+latestStopHistoryEntity.getRouteId());
		Vector<RouteSchedule> nextStopDetailsList = LoadRouteSchedule.getInstance().
				retrieve(getRouteScheduleId(activeVehicleToRouteSchedule));
		if(nextStopDetailsList == null){
			return null;
		}

		return filterUnwantedVehicleRouteStopDetailsEntities(nextStopDetailsList, latestStopHistoryEntity);
	}

	/**
	 * Insert or Update ETA.
	 * 
	 * @param stopid
	 * @param arrivalTime
	 * @param arrivalTime2 
	 * @param long1 
	 * @param routeName
	 * @param statement 
	 * @return
	 */
	private EtaDisplay insertOrUpdateETA(long vehicleId, long routeId, long stopid, double arrivalTime, String routeName,int sequenceNumber) {
		EtaDisplay etaDisplay = selectETADetailsOfVehiclesRouteWithStopId(vehicleId, routeId, stopid);
		LOG.debug("MBMC : inserting/updating ETA for vehicle Id "+vehicleId+" route id "+routeId+" route Name "+routeName);
		if (etaDisplay != null) {
			LOG.debug("MBMC Updating ETA for vehicle Id "+vehicleId+" route Id "+routeId);
			etaDisplay.setArrivalTime((int) arrivalTime);
			updateArrivalTime(etaDisplay);
			return etaDisplay;
		} else {
			LOG.debug("MBMC Inserting ETA for vehicle Id "+vehicleId+" route Id "+routeId);
			EtaDisplay etaDisplayEntity = new EtaDisplay(vehicleId, routeId, stopid,
					(int) arrivalTime, routeName, 0, false, sequenceNumber);
			insertArrivalTime(etaDisplayEntity);
			return etaDisplayEntity;
		}
	}

	/** Returns the EtaDisplay entity for the particular vehicle route and stop association.
	 * @param vehicleId 
	 * @param routeId */
	private EtaDisplay selectETADetailsOfVehiclesRouteWithStopId(long vehicleId, long routeId, long stopId) {
		LOG.debug("ETAETA : Seletcting Etadetails for the vehicle : "+vehicleId+", route : "+routeId+" and stop : "+stopId);
		return LoadEtaDisplayDetails.getInstance().retrieve(vehicleId, routeId, stopId);
	}

	/**
	 * Function to start updating ETA.
	 * 
	 * @param etaDisplay
	 * @param statement 
	 */
	private void insertArrivalTime(EtaDisplay etaDisplay) {
		LOG.debug("ETAETA : Inserting etadisplay for Vehicle : "+etaDisplay.getVehicleId()+", Route : "+etaDisplay.getRouteId()
				+", Stop : "+etaDisplay.getStopId());
		try {
			etaDisplay = etaDisplayDao.insert(etaDisplay);
		} catch (OperationNotSupportedException e) {
			LOG.error(e);
		}
		/**	Update Etadisplay cache.*/
		insertArrivalTimeInEtaDisplayCache(etaDisplay);
	}

	/**
	 * Function to update ETA.
	 * 
	 * @param etaDisplay
	 * @param statement 
	 */
	private void updateArrivalTime(EtaDisplay etaDisplay) {
		LOG.debug("ETAETA : Updating latest arrival time for Vehicle : "+etaDisplay.getVehicleId()+", Route : "+etaDisplay.getRouteId()
				+", Stop : "+etaDisplay.getStopId());
		etaDisplay = etaDisplayDao.updateEtaDisplay(etaDisplay);
		/**	Update Etadisplay cache.*/
		updateArrivalTimeInEtaDisplayCache(etaDisplay);
	}

	/** Inserting EtaDisplay cache with latest entry for the current vehicle.*/
	private void insertArrivalTimeInEtaDisplayCache(EtaDisplay entity) {
		LOG.debug("ETAETA : Trying to insert the etadisplay in cache  for Vehcile : "+entity.getVehicleId()+
				", Route : "+entity.getRouteId()+", Stop : "+entity.getStopId());
		EtaDisplay etaDisplay = LoadEtaDisplayDetails.getInstance().retrieve(entity.getVehicleId(), entity.getRouteId(), entity.getStopId());

		if(etaDisplay == null){
			LOG.debug("ETAETA : Inserting the EtaDisplay cache for Vehcile : "+entity.getVehicleId()+ ", Route : "+entity.getRouteId()
					+", Stop : "+entity.getStopId());
			ConcurrentHashMap<Long, ConcurrentHashMap<Long, EtaDisplay>> routeList = 
					LoadEtaDisplayDetails.getInstance().retrieve(entity.getVehicleId());
			ConcurrentHashMap<Long, EtaDisplay> stopList ;
			if(routeList == null){
				routeList = new ConcurrentHashMap<Long, ConcurrentHashMap<Long,EtaDisplay>>();
			}
			stopList = routeList.get(entity.getRouteId());
			if (stopList ==  null) {
				stopList = new ConcurrentHashMap<Long, EtaDisplay>();						
			}
			stopList.put(entity.getStopId(), entity);					
			routeList.put(entity.getRouteId(), stopList);
			LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.put(entity.getVehicleId(), routeList);
			return;
		}else{
			LOG.debug("ETAETA : Already updated the etadisplay cache for Vehicle : "+entity.getVehicleId()+ ", Route : "+entity.getRouteId()
					+", Stop : "+entity.getStopId());
		}
	}

	/**	Updating EtaDisplay cache with recently calculated arrival time.*/
	private void updateArrivalTimeInEtaDisplayCache(EtaDisplay entity) {
		LOG.debug("ETAETA : Updating Etadisplay cache with lastest arrival time for Vehicle : "+entity.getVehicleId()+
				", Route : "+entity.getRouteId()+" and Stop : "+entity.getStopId());
		EtaDisplay etaDisplay = LoadEtaDisplayDetails.getInstance().retrieve(entity.getVehicleId(), entity.getRouteId(), entity.getStopId());
		if(etaDisplay != null){
			ConcurrentHashMap<Long, ConcurrentHashMap<Long, EtaDisplay>> routeList = 
					LoadEtaDisplayDetails.getInstance().retrieve(entity.getVehicleId());
			ConcurrentHashMap<Long, EtaDisplay> stopList = routeList.get(entity.getRouteId());
			entity.setType(etaDisplay.getType());
			stopList.put(entity.getStopId(), entity);
			routeList.put(entity.getRouteId(), stopList);
			LoadEtaDisplayDetails.getInstance().cacheEtaDisplayDetails.put(entity.getVehicleId(), routeList);
		}
	}

	/**
	 * Function to know the reaching time of the vehicle to the next stop.
	 * 
	 * @param avgSpeed
	 * @param distanceToNextStop
	 * @return
	 */
	private double predictArrivalTime(double avgSpeed, double distanceToNextStop) {
		// average speed in km/hr
		// distance in kms
		return (distanceToNextStop / avgSpeed) * 60;// in mins
	}

	/**
	 * Stop receiving updates by making route as inactive.
	 * @param statement 
	 * @param routeId 
	 * @param vehicleId 
	 */
	private void deactivateRoute(long vehicleId, Long routeId) {
		stopHistoryDao.deactivateRoute(vehicleId, routeId);
		etaDisplayDao.deleteOnRouteDeactivation(vehicleId, routeId);
		/**	Update related cache.*/
		updateCacheOnRouteDeactivation(vehicleId, routeId);
	}


	/** Updating stophistory, activevehiclestophistory and vehicletoroutesassociation caches on route deactivation.
	 * @param routeId 
	 * @param vehicleId */
	private void updateCacheOnRouteDeactivation(long vehicleId, Long routeId) {
		LOG.debug("ETAETA : Updating Related cache on deactivating Route : "+routeId+" for Vehicle : "+vehicleId);
		/** Removing EtaDisplay entries from cache.*/

		LOG.debug("ETAETA : Updating EtaDisplay cache on deactivating Route : "+routeId+" for Vehicle : "+vehicleId);
		LoadEtaDisplayDetails.getInstance().refresh();

		/** Update stopHistory status*/
		LOG.debug("ETAETA : Updating StopHistory cache on deactivating Route : "+routeId+" for Vehicle : "+vehicleId);
		LoadActiveStopHistoryDetails.getInstance().refresh();

		/** Update activevehiclestophistory cache*/
		LOG.debug("ETAETA : Updating ActiveVehicleStopHistory cache on deactivating Route : "+routeId+" for Vehicle : "+vehicleId);
		LoadActiveVehicleStopHistoryDetails.getInstance().refresh();

		/** Updating vehicletoroutesassociation cache.*/
		LOG.debug("ETAETA : Updating VehicletoRouteAssociation cache on deactivating Route : "+routeId+" for Vehicle : "+vehicleId);
		LoadVehicleRouteAssociationDetails.getInstance().refresh();

		LOG.debug("ETAETA : Updating SAS Violation cache on deactivating Route : "+routeId+" for Vehicle : "+vehicleId);
		LoadSASRouteDeviation.getInstance().removeEntriesForVehicle(vehicleId);
		LoadSASStopDeviation.getInstance().removeEntriesForVehicle(vehicleId);
		LoadSASTimeDeviations.getInstance().removeEntriesForVehicle(vehicleId);

	}

	/**
	 * Process the updates for the specific stop
	 * @param vehicleId
	 * @param routeSchedule
	 * @param actualDistance
	 * @param bulkDataTime
	 * @param trackHistoryId
	 * @param statement
	 */
	private void updateStopHistory(long vehicleId, RouteSchedule routeSchedule, float actualDistance, Date bulkDataTime, 
			Long trackHistoryId) {
		Long routeId = routeSchedule.getRouteId();
		Long stopId = routeSchedule.getStopId();
		LOG.debug("ETAETA : Updating StopHistory for Vehicle : "+vehicleId+", Route : "+routeId+" and Stop : "+stopId);
		StopHistory firstStopHistory = getFirstStopHistoryFromCache(vehicleId, routeId);
		Calendar cal = Calendar.getInstance();
		Timestamp expectedTime = null;
		if (firstStopHistory == null) {
			/** This is the start point of the route.*/
			cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), routeSchedule.getExpectedTime()
					.getHours(), routeSchedule.getExpectedTime().getMinutes(),0);
			cal.set(Calendar.MILLISECOND, 0);
			expectedTime = new Timestamp(cal.getTime().getTime());
			StopHistory stopHistoryEntity = new StopHistory(stopId, vehicleId, routeId, routeSchedule.getRouteScheduleId(), expectedTime, 
					new Timestamp(bulkDataTime.getTime()), routeSchedule.getEstimatedDistance(), actualDistance, 
					true, routeSchedule.getSequenceNumber(), trackHistoryId);
			stopHistoryEntity = stopHistoryDao.insertIntoStopHistory(stopHistoryEntity);
			/** Update related cache*/
			updateStopHistoryCache(stopHistoryEntity);
			updateActiveVehicleStopHistoryCache(stopHistoryEntity.getId().getId(), vehicleId, routeId);
			/** Activate the route in cache.*/
			updateVehicleToRouteAssociationCache(vehicleId, routeSchedule);
			/*	Checking for Trip Miss*/
			checkForTripMiss(vehicleId, routeSchedule, bulkDataTime);
		} else {
			LOG.debug("MBMC : inserting next stop details into Stop history for "+vehicleId+" route Id : "+routeId+" stop Id : "+stopId);
			LOG.debug("ETAETA : Inserting new entry into the stophistory table for vehicle : "+
					vehicleId+", route : "+routeId+" and stop : "+stopId);
			/** The route has already started and this is one of the stops in the route.*/
			cal.setTime(new Date(firstStopHistory.getActualTime().getTime()));
			cal.add(Calendar.DATE, (routeSchedule.getSpanDay() - 1));
			cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), routeSchedule.getExpectedTime()
					.getHours(), routeSchedule.getExpectedTime().getMinutes(),0);
			cal.set(Calendar.MILLISECOND, 0);
			expectedTime = new Timestamp(cal.getTime().getTime());
			StopHistory stopHistoryEntity = new StopHistory(stopId, vehicleId, routeId, routeSchedule.getRouteScheduleId(), expectedTime, 
					new Timestamp(bulkDataTime.getTime()), routeSchedule.getEstimatedDistance(), actualDistance, 
					true, routeSchedule.getSequenceNumber(), trackHistoryId);
			stopHistoryEntity = stopHistoryDao.insertIntoStopHistory(stopHistoryEntity);
			/**	Update related cache.*/
			updateStopHistoryCache(stopHistoryEntity);
			updateActiveVehicleStopHistoryCache(stopHistoryEntity.getId().getId(), vehicleId, routeId);
		}

		if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_SAS_VIOLATIONS_ENABLED"))){
			//	Checking for any occurrences of SAS violations
			checkSASViolations(vehicleId, routeSchedule, bulkDataTime);
		}
	}

	/**
	 * 1) Get all first point value of the each trip
	 * 2) Check for its value in stophistory
	 * 3) If not present make an entry as trip miss
	 * @param vehicleId
	 * @param routeSchedule
	 * @param statement
	 * @param bulkDataTime 
	 */
	private void checkForTripMiss(Long vehicleId, RouteSchedule routeSchedule, Date bulkDataTime) {
		LOG.debug("TRIPMISS : Checking for Trip Miss report update for vehicle : "+vehicleId+" route : "+routeSchedule);
		ConcurrentHashMap<VehicleToRouteSchedule, Boolean> vehicleRouteStatusMap = 
				LoadVehicleRouteAssociationDetails.getInstance().retrieve(vehicleId);
		Vector<RouteSchedule> firstRouteScheduleList = getAllFirstVehicleRouteStopDetailsList(vehicleRouteStatusMap);
		LOG.debug("TRIPMISS : Total Trips for for this vehicle is : "+firstRouteScheduleList.size());
		Calendar cal = Calendar.getInstance();
		Date currentTime = Calendar.getInstance().getTime();
		Timestamp expectedTime = null;
		for(RouteSchedule routeScheduleInstance : firstRouteScheduleList){
			//	Check for the trips 2 hours earlier than the current time
			if(routeScheduleInstance.getExpectedTime().getHours() <= (currentTime.getHours()-2)){
				cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), routeScheduleInstance.getExpectedTime()
						.getHours(), routeScheduleInstance.getExpectedTime().getMinutes(),0);
				cal.set(Calendar.MILLISECOND, 0);

				expectedTime = new Timestamp(cal.getTime().getTime());
				if(!isCurrentTripStarted(expectedTime, vehicleId,routeScheduleInstance)){
					LOG.debug("TRIPMISS : Trip miss has been discovered. Check for DB update");
					//	Trip Missed. Check for already notified status
					if(!currentTripMissAlreadyNotified(expectedTime, vehicleId, routeScheduleInstance)){
						LOG.debug("TRIPMISS : Updating in DB as earlier update was not there");
						//	Notify this trip miss report by inserting value in required table (violation_tripmiss)
						TripMissDeviation tripMiss = new TripMissDeviation(vehicleId, routeSchedule.getRouteId(),  
								expectedTime, new Timestamp(bulkDataTime.getTime()));
						try {
							tripMiss = tripMissDao.insert(tripMiss);
						} catch (OperationNotSupportedException e) {
							LOG.error(e);
						}
					}else{
						LOG.debug("TRIPMISS : Skipping updating in DB as earlier update was done");
					}
				} else {
					LOG.debug("TRIPMISS : Trip Started for time : "+expectedTime.toString());
				}
			}
		}
	}

	private Vector<RouteSchedule> getAllFirstVehicleRouteStopDetailsList(
			ConcurrentHashMap<VehicleToRouteSchedule, Boolean> vehicleRouteStatus) {
		LOG.debug("TRIPMISS : Fetching all the first stops");
		Vector<RouteSchedule> firstRouteScheduleList = new Vector<RouteSchedule>();
		for(VehicleToRouteSchedule vehicleToRouteSchedule: vehicleRouteStatus.keySet()){
			String routeScheduleId = ETAUtils.getRouteScheduleId(vehicleToRouteSchedule);
			Vector<RouteSchedule> routeScheduleList = LoadRouteSchedule.getInstance().retrieve(routeScheduleId);
			if(routeScheduleList != null){
				for(RouteSchedule routeSchedule : routeScheduleList){
					if(routeSchedule.getSequenceNumber() == 1){
						firstRouteScheduleList.add(routeSchedule);
						break;
					}
				}
			}
		}
		Collections.sort(firstRouteScheduleList, new Comparator<RouteSchedule>() {
			public int compare(RouteSchedule o1, RouteSchedule o2) {
				return o1.getExpectedTime().compareTo(o2.getExpectedTime());
			}});
		return firstRouteScheduleList;
	}

	private boolean currentTripMissAlreadyNotified(Timestamp expectedTime, Long vehicleId, 
			RouteSchedule routeSchedule) {
		LOG.debug("TRIPMISS : Checking for trip miss report already notified form vehicle : "+vehicleId+" route : "
				+routeSchedule.getRouteId()+" expectedtime : "+expectedTime.toString());
		List<TripMissDeviation> tripMissReportList = tripMissDao.selectFromVehicleIdRouteIdExpecteTime(vehicleId, routeSchedule.getRouteId(), expectedTime);
		if(tripMissReportList.size() != 0){
			if(tripMissReportList.size() > 1){
				LOG.error("TRIPMISS : Duplicate entries for Trip Miss for vehicle : "+vehicleId+" route : "
						+routeSchedule.getRouteId()+" expectedtime  : "+expectedTime);
			}
			return true;
		}
		return false;
	}

	private boolean isCurrentTripStarted(Timestamp expectedTime, Long vehicleId, RouteSchedule routeSchedule) {
		LOG.debug("TRIPMISS : Check for current trip start");
		boolean tripStarted = false;
		try{
			tripStarted = stopHistoryDao.checkForTripStarted(vehicleId, routeSchedule.getRouteId(), expectedTime);
		} catch (Exception e){
			LOG.error("TRIPMISS : Error while Current trip start check",e);
		}
		return tripStarted;
	}
	/** 
	 * Updating the cache with route status active.
	 */
	private void updateVehicleToRouteAssociationCache(long vehicleId, RouteSchedule routeSchedule) {
		LOG.debug("ETAETA : Updating Vehicle to route association cache on starting the Route : "+routeSchedule.getRouteId()
				+" for Vehicle : "+vehicleId);
		ConcurrentHashMap<VehicleToRouteSchedule, Boolean> routeStatusList = 
				LoadVehicleRouteAssociationDetails.getInstance().retrieve(vehicleId);
		if(routeStatusList == null){
			LOG.debug("Returned RouteStatusList is null");
			return;
		}
		for(VehicleToRouteSchedule vehicleToRouteSchedule : routeStatusList.keySet()){
			if(routeSchedule.getRouteScheduleId().trim().equalsIgnoreCase(getRouteScheduleId(vehicleToRouteSchedule).trim())){
				if(!routeStatusList.get(vehicleToRouteSchedule)){
					routeStatusList.put(vehicleToRouteSchedule, true);
					LoadVehicleRouteAssociationDetails.getInstance().cacheVehicleToRouteAssociation.put(vehicleId, routeStatusList);
					LOG.debug("Successfully updated VehicleToRouteAssociationCache");
					return;
				}
			}
		}
	}

	/** Caching association of route active vehicle with its  stophistory ids 
	 * @param routeId 
	 * @param vehicleId */
	private void updateActiveVehicleStopHistoryCache(Long stopHistoryId, Long vehicleId, Long routeId) {
		LOG.debug("ETAETA : Updating active vehicle stop history cache for Vehicle : "+vehicleId+" and Route : "+routeId);
		Vector<Long> stopHistoryIdList = LoadActiveVehicleStopHistoryDetails.getInstance().retrieve(vehicleId, routeId);
		if(stopHistoryIdList == null){
			/** Vehicle stophistory ids mapping is not cached. Hence returning back without any update.*/
			return;
		}
		if(stopHistoryIdList.contains(stopHistoryId)){
			/** Cache is already updated with the latest stophistory id.*/
			return;
		}else{
			/** Updating the cache with latest stophistory id.*/
			stopHistoryIdList.add(stopHistoryId);
		}
		LoadActiveVehicleStopHistoryDetails.getInstance().cacheActiveVehicleStopHistories.put((vehicleId+"-"+routeId).
				replace(" ", "").trim(), stopHistoryIdList);
		LoadActiveVehicleStopHistoryDetails.getInstance().toString();
	}

	/** Updating stopHistory cache on stophistory table updation */ 
	private void updateStopHistoryCache(StopHistory stopHistoryEntity) {
		LOG.debug("ETAETA : Updating stopHistory cache for the Vehicle : "+stopHistoryEntity.getVehicleId()+", route : "
				+stopHistoryEntity.getRouteId());
		LoadActiveStopHistoryDetails.getInstance().cacheStopHistory.put(stopHistoryEntity.getId().getId(), stopHistoryEntity);

	}

	/**
	 * This function returns first stophistory entry for the active route of the current vehicle
	 * 
	 * @param vehicleId
	 * @param routeId
	 * @return
	 */
	private StopHistory getFirstStopHistoryFromCache(Long vehicleId, Long routeId) {
		return stopHistoryDao.getFirstStopHistoryOfTheRoute(routeId, vehicleId);
	}

	/**
	 * Function to insert a routetrack entry for starting off the route
	 * 
	 * @param IMEI
	 * @param vehicleId
	 * @param routeId
	 * @param tripId
	 * @param startTrackHistoryId
	 * @param bulkDataTime
	 * @param endTrackHistoryId
	 * @param statement
	 */
	public void insertOrUpdateRouteTrack( String IMEI, long vehicleId, Long routeId, long startTrackHistoryId,  
			Date bulkDataTime, long endTrackHistoryId) {
		LOG.debug("Route track updation for "+vehicleId+" route Id "+routeId);
		RouteTrack routeTrack = null;
		if (endTrackHistoryId == 0) {
			LOG.debug("MBMC : In inserting route tarck endTrackHistoryId is ZERO "+endTrackHistoryId);
			routeTrack = new RouteTrack(bulkDataTime, IMEI, routeId, /*tripId,*/ startTrackHistoryId, endTrackHistoryId);
			insertIntoRouteTrack(routeTrack, vehicleId, routeId);
		} else {
			routeTrack = selectByRouteIdAndIMEI(vehicleId, routeId, IMEI);
			if (routeTrack != null) {
				LOG.debug("MBMC : updateIntoRouteTrack route tarck endTrackHistoryId non-zero "+endTrackHistoryId);
				routeTrack.setEndTrackHistoryId(endTrackHistoryId);
				updateIntoRouteTrack(routeTrack, vehicleId, routeId, IMEI);
			}
		}
	}

	/**
	 * Return the routetrack entity for corresponding routeid and tripid
	 * 
	 * @param vehicleId
	 * @param routeId
	 * @param IMEI
	 * @return
	 */
	private RouteTrack selectByRouteIdAndIMEI(long vehicleId, Long routeId, String IMEI) {
		LOG.debug("ETAETA : Selecting the routeTrack entity for the cache for EndTrackhistoryId updation for the Vehicle :"+vehicleId
				+" and Route : "+routeId);

		Vector<RouteTrack> routeTrackList = LoadRouteTrackDetails.getInstance().retrieve(routeId);
		if(routeTrackList == null){
			LOG.error("ETAETA : No RouteTrack list cached for route "+routeId);
			return null;
		}
		for(RouteTrack routeTrack : routeTrackList){
			if(routeTrack.getIMEI().equals(IMEI) && routeTrack.getEndTrackHistoryId() == 0){
				LOG.debug("MBMC : routeTrack returning For imei : "+routeTrack.getIMEI()+" , route Id "+routeTrack.getRouteId());
				return routeTrack;
			}
		}
		return null;
	}

	/**
	 * Function to insert into RouteTrack.
	 * @param routeTrack
	 * @param statement 
	 * @param routeId 
	 * @param vehicleId 
	 */
	private void insertIntoRouteTrack(RouteTrack routeTrack, long vehicleId, Long routeId) {
		routeTrack = routeTrackDao.insertIntoRouteTrack(routeTrack);
		/**	Update related cache.*/
		insertIntoRouteTrackCache(routeTrack, vehicleId, routeId);
	}

	/** Insert the latest entry of routeTrack entity into the cache.
	 * @param routeId 
	 * @param vehicleId */
	private void insertIntoRouteTrackCache(RouteTrack routeTrackEntity, Long vehicleId, Long routeId) {
		LOG.debug("ETAETA : Inserting RouteTrack entity into cache on starting the Route : "+routeId+" for Vehicle : "+vehicleId);
		Vector<RouteTrack> routeTrackList = LoadRouteTrackDetails.getInstance().retrieve(routeId);
		if(routeTrackList == null){
			LOG.error("ETAETA : No RouteTrack list cached for route "+routeId);
			return;
		}
		routeTrackList.add(routeTrackEntity);
		LoadRouteTrackDetails.getInstance().cacheRouteTrack.put(routeId, routeTrackList);	
		LoadRouteTrackDetails.getInstance().toString();
	}

	/**
	 * Function to update RouteTrack.
	 * @param routeTrack
	 * @param statement 
	 * @param routeId 
	 * @param vehicleId 
	 * @param IMEI 
	 */
	private void updateIntoRouteTrack(RouteTrack routeTrack, long vehicleId, Long routeId, String IMEI) {
		routeTrack = routeTrackDao.updateRouteTrack(routeTrack);
		/**	Update related cache.*/
		updateIntoRouteTrackCache(routeTrack, vehicleId, routeId, IMEI);
	}
	/** Update endTrackHistoryId in the routeTrack cache for the corresponding route and trip.
	 * @param IMEI 
	 * @param routeId 
	 * @param vehicleId */
	private void updateIntoRouteTrackCache(RouteTrack routeTrackEntity, long vehicleId, Long routeId, String IMEI) {
		LOG.debug("ETAETA : Updating into the Route Track cache for Vehicle : "+vehicleId+", Route : "+routeId+" on reaching last stop");
		Vector<RouteTrack> routeTrackList = LoadRouteTrackDetails.getInstance().retrieve(routeId);
		if(routeTrackList == null){
			LOG.error("ETAETA : No RouteTrack list cached for route "+routeId);
			return;
		}
		Iterator<RouteTrack> routeTrackIterator = routeTrackList.iterator();
		while(routeTrackIterator.hasNext()){
			RouteTrack routeTrack = routeTrackIterator.next();
			if(routeTrack.getIMEI().equals(IMEI) && routeTrack.getEndTrackHistoryId() == 0 ){
				routeTrackIterator.remove();
				routeTrackList.add(routeTrackEntity);
				return;
			}
		}

	}

	/**
	 * Check for route,time and stop deviations.
	 * @param vehicleId 
	 * @param statement 
	 * @param bulkDataTime 
	 */
	private void checkSASViolations(Long vehicleId, RouteSchedule routeSchedule, Date bulkDataTime) {
		LOG.debug("ETAETA : Checking for SAS violations for Vehicle : "+vehicleId+", Route : "+routeSchedule.getRouteId()+" and Stop : "+routeSchedule.getStopId());
		StopHistory stopHistory = getRowForDeviationCheck(vehicleId, routeSchedule.getRouteId(), routeSchedule.getStopId());

		if (stopHistory != null) {
			checkForTimeDeviation(stopHistory);
			checkForRouteDeviation(stopHistory, bulkDataTime);
		}
		checkForStopDeviation(vehicleId, routeSchedule, bulkDataTime);
	}

	/** Returns the particular stophistory entity for deviation check.
	 * @param stopId 
	 * @param routeId 
	 * @param vehicleId */
	private StopHistory getRowForDeviationCheck(Long vehicleId, Long routeId, Long stopId) {
		LOG.debug("ETAETA : Getting the recently inserted stopHistory row for deviation check for Vehicle : "
				+vehicleId+", Route : "+routeId+" and Stop : "+stopId);
		for(Long stopHistoryId : LoadActiveStopHistoryDetails.getInstance().cacheStopHistory.keySet()){
			StopHistory stopHistoryEntity = LoadActiveStopHistoryDetails.getInstance().cacheStopHistory.get(stopHistoryId);
			if(stopHistoryEntity.getVehicleId() == vehicleId && stopHistoryEntity.getRouteId() == routeId && 
					stopHistoryEntity.getStopId() == stopId && stopHistoryEntity.isActive()){
				return stopHistoryEntity;
			}
		}
		return null;
	}

	/**
	 * Check for missing stop.
	 * @param statement 
	 * @param bulkDataTime 
	 * @param stopId 
	 * @param routeId 
	 * @param vehicleId 
	 */
	private void checkForStopDeviation(Long vehicleId, RouteSchedule routeSchedule, Date bulkDataTime) {
		Long routeId = routeSchedule.getRouteId();
		Long stopId = routeSchedule.getStopId();
		LOG.debug("ETAETA : Checking for StopDeviation for Vehicle : "+vehicleId+", Route : "+routeId+" and Stop : "+stopId);
		StopHistory firstStopHistoryEntity = getFirstStopHistoryFromCache(vehicleId, routeId);
		List<Long> stopHistoryIdList = new Vector<Long>();
		stopHistoryIdList = LoadActiveVehicleStopHistoryDetails.getInstance().retrieve(vehicleId, routeId);
		if(stopHistoryIdList != null){
			Collections.sort(stopHistoryIdList);
			int flag = 1;

			for (Long stopHistoryId : stopHistoryIdList) {
				StopHistory stopHistoryEntity = LoadActiveStopHistoryDetails.getInstance().retrieve(stopHistoryId);
				if(stopHistoryEntity == null){
					/** Stop history is not cached. Hence skipping to next entry of stop history. */
					continue;
				}
				if (flag != stopHistoryEntity.getSeqNo()) {
					// Stop Missed.
					Vector<RouteSchedule> routeScheduleList = LoadRouteSchedule.getInstance().retrieve(routeSchedule.getRouteScheduleId());
					if(routeScheduleList == null){
						/**	Vehicle route stop association is not cached. Hence skipping the further processing.*/
						continue;
					}
					for(RouteSchedule schedule: routeScheduleList){
						if(schedule.getSequenceNumber() == flag){
							Stops stopEntity = LoadStopsDetails.getInstance().retrieve(stopId);
							if(stopEntity == null){
								/** Stop entity is not cache and hence skipping the further processing.*/
								continue;
							}
							// Check whether violation already updated to the table.
							StopDeviation stopDeviation = checkForStopDeviationAlreadyReported(vehicleId, routeId, stopId);
							if (stopDeviation == null) {
								// Violation not yet reported ..
								Vehicle vehicleEntity = LoadVehicleDetails.getInstance().retrieve(vehicleId);
								if(vehicleEntity == null){
									/** Vehicle entity is not cached and hence skip further processing.*/
									continue;
								}
								Calendar cal = Calendar.getInstance();
								cal.setTime(firstStopHistoryEntity.getMbmcExpectedTime());
								cal.set(Calendar.MILLISECOND, 0);
								cal.add(Calendar.DATE, schedule.getSpanDay()-1);
								Date stopExpectedTime = cal.getTime();
								StopDeviation entity = new StopDeviation(vehicleId, vehicleEntity.getDisplayName(), stopEntity.getId().getId(),
										stopEntity.getStopName(), routeId, new Timestamp(stopExpectedTime.getTime()), 
										new Timestamp(bulkDataTime.getTime()));
								try {
									entity = stopDeviationDao.insert(entity);
								} catch (OperationNotSupportedException e) {
									LOG.error(e);
								}
								/** Update stop deviation cache.*/
								updateSASStopDeviationCache(entity);
							} else {
								// Already violation has been reported ...
							}
						} else{
							continue;
						}
					}
				}
				flag++;
			}
		}
	}

	/**	The SAS Stop Deviation cache is being updated with the latest entry.*/
	private void updateSASStopDeviationCache(StopDeviation entity) {
		LOG.debug("ETAETA : Updating StopDeviation cache for Vehicle : "+entity.getVehicleId()+", Route : "+entity.getRouteId()
				+" and Stop : "+entity.getStopId());
		LoadSASStopDeviation.getInstance().cacheSASStopDeviation.put(entity.getId().getId(), entity);

	}

	/** Checking whether the stop deviation already reported.
	 * @param stopId 
	 * @param routeId 
	 * @param vehicleId */
	private StopDeviation checkForStopDeviationAlreadyReported(Long vehicleId, Long routeId, Long stopId) {
		LOG.debug("ETAETA : Checking for StopDeviation already being reported for Vehicle : "
				+vehicleId+", Route : "+routeId+" and Stop : "+stopId);
		StopHistory stopHistoryEntity = getFirstStopHistoryFromCache(vehicleId, routeId);
		for(Long stopDeviationId : LoadSASStopDeviation.getInstance().cacheSASStopDeviation.keySet()){
			StopDeviation stopDeviationEntity = LoadSASStopDeviation.getInstance().retrieve(stopDeviationId);
			if(stopDeviationEntity != null && stopDeviationEntity.getVehicleId() == vehicleId && stopDeviationEntity.getRouteId() == routeId 
					&& stopDeviationEntity.getStopId() == stopId && stopDeviationEntity.getOccurredat().getTime()
					> stopHistoryEntity.getActualTime().getTime()){
				return stopDeviationEntity;
			}
		}
		return null;
	}

	/**
	 * Route Deviation
	 * @param statement 
	 * @param bulkDataTime 
	 */
	private void checkForRouteDeviation(StopHistory stopHistory, Date bulkDataTime) {
		Long vehicleId = stopHistory.getVehicleId();
		Long routeId = stopHistory.getRouteId();
		Long stopId = stopHistory.getStopId();
		LOG.debug("ETAETA : Checking for RouteDeviation for Vehicle : "+vehicleId+", Route : "+routeId+" and Stop : "+stopId);

		if (stopHistory.getActualDistance() > (stopHistory.getEstimateDistance() + 100)) {
			// Route Violated
			// Check whether the violation already reported or not.
			RouteDeviation routeDeviation = checkForRouteDeviationAlreadyReported(vehicleId, routeId, stopId);
			if (routeDeviation == null) {
				// Violation not yet reported ..
				Vehicle vehicleEntity = LoadVehicleDetails.getInstance().retrieve(vehicleId);
				if(vehicleEntity == null){
					LOG.error("Vehicle "+vehicleId+" not cached");
					/**	Vehicle entity is not cached and hence returning back with now further processing.*/
					return;
				}
				Routes routeEntity = LoadRoutesDetails.getInstance().retrieve(routeId);
				if(routeEntity == null){
					return;
				}
				Stops stopEntity = LoadStopsDetails.getInstance().retrieve(stopId);
				if(stopEntity == null){
					return;
				}
				RouteDeviation entity = new RouteDeviation( vehicleId, vehicleEntity.getDisplayName(), stopId, stopEntity.getStopName(), 
						routeId, routeEntity.getRouteName(), stopHistory.getEstimateDistance(), stopHistory.getActualDistance(),
						new Timestamp(bulkDataTime.getTime()));
				LOG.debug("ETAETA : RouteDeviation violation not reported yet for Vehicle : "
						+vehicleId+", Route : "+routeId+" and Stop : "+stopId);
				try {
					entity = routeDeviationDao.insert(entity);
				} catch (OperationNotSupportedException e) {
					LOG.error(e);
				}
				/**	Update route deviation cache.*/
				updateSASRouteDeviationCache(entity);
			} else {
				LOG.debug("ETAETA :  RouteDeviation violation already reported for Vehicle : "
						+vehicleId+", Route : "+routeId+" and Stop : "+stopId);
			}
		}
	}

	/**
	 * The SAS Route Deviation cache is being updated with the latest entry
	 * @param entity
	 */
	private void updateSASRouteDeviationCache(RouteDeviation entity) {
		LOG.debug("ETAETA : Updating RouteDeviation violation cache for Vehicle : "
				+entity.getVehicleId()+", Route : "+entity.getRouteId()+" and Stop : "+entity.getStopId());
		LoadSASRouteDeviation.getInstance().cacheSASRouteDeviation.put(entity.getId().getId(), entity);
	}

	/**	Checking whether the route deviation already reported.
	 * @param stopId 
	 * @param routeId 
	 * @param vehicleId */
	private RouteDeviation checkForRouteDeviationAlreadyReported(Long vehicleId, Long routeId, Long stopId) {
		LOG.debug("ETAETA : Checking for RouteDeviation violation already being reported for Vehicle : "
				+vehicleId+", Route : "+routeId+" and Stop : "+stopId);
		StopHistory stopHistoryEntity = getFirstStopHistoryFromCache(vehicleId, routeId);
		for(Long routeDeviationId : LoadSASRouteDeviation.getInstance().cacheSASRouteDeviation.keySet()){
			RouteDeviation routeDeviationEntity = LoadSASRouteDeviation.getInstance().retrieve(routeDeviationId);
			if(routeDeviationEntity != null && routeDeviationEntity.getVehicleId() == vehicleId && 
					routeDeviationEntity.getRouteId() == routeId && routeDeviationEntity.getStopId() == stopId && 
					routeDeviationEntity.getOccurredAt().getTime() > stopHistoryEntity.getActualTime().getTime()){
				return routeDeviationEntity;
			}
		}
		return null;
	}

	/**
	 * Time deviation checking.
	 * @param statement 
	 */
	private void checkForTimeDeviation(StopHistory stopHistory) {
		Long vehicleId = stopHistory.getVehicleId();
		Long routeId = stopHistory.getRouteId();
		Long stopId = stopHistory.getStopId();
		LOG.debug("ETAETA : Checking for TimeDeviation violation for Vehicle : "
				+vehicleId+", Route : "+routeId+" and Stop : "+stopId);
		Date actualTime = new Date(stopHistory.getActualTime().getTime());
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(stopHistory.getExpectedTime().getTime()));
		cal.add(Calendar.SECOND, 59);
		Date expectedTime = cal.getTime();

		if (actualTime.getTime() > expectedTime.getTime()) {
			/** Time Violated.*/
			TimeDeviation timeDeviation = checkForTimeDeviationAlreadyReported(vehicleId, routeId, stopId);
			if (timeDeviation == null) {
				LOG.debug("ETAETA : Time deviation violation not yet reported. Hence reporting now for Vehicle : "
						+vehicleId+", Route : "+routeId+" and Stop : "+stopId);
				Vehicle vehicleEntity = LoadVehicleDetails.getInstance().retrieve(vehicleId);
				if(vehicleEntity == null){
					LOG.error("Vehicle "+vehicleId+" not cached");
					/**	Vehicle entity is not cached and hence returning back with now further processing.*/
					return;
				}
				Routes routeEntity = LoadRoutesDetails.getInstance().retrieve(routeId);
				if(routeEntity == null){
					return;
				}
				Stops stopEntity = LoadStopsDetails.getInstance().retrieve(stopId);
				if(stopEntity == null){
					return;
				}
				TimeDeviation entity = new TimeDeviation( vehicleId, vehicleEntity.getDisplayName(), stopId, stopEntity.getStopName(),
						routeId, routeEntity.getRouteName(), stopHistory.getExpectedTime(), stopHistory.getActualTime());
				try {
					entity = timeDeviationDao.insert(entity);
				} catch (OperationNotSupportedException e) {
					LOG.error(e);
				}
				/**	Update the cache.*/
				updateSASTimeDeviationCache(entity);
			} else {
				LOG.debug("ETAETA :  Time deviation violation already reported for Vehicle : "
						+vehicleId+", Route : "+routeId+" and Stop : "+stopId);
			}
		}
	}

	/** The SAS Time Deviation cache is being updated with latest entry.*/
	private void updateSASTimeDeviationCache(TimeDeviation entity) {
		LOG.debug("ETAETA : Updatind TimeDeviation violation cache for Vehicle : "
				+entity.getVehicleId()+", Route : "+entity.getRouteId()+" and Stop : "+entity.getStopId());
		LoadSASTimeDeviations.getInstance().cacheSASTimeDeviation.put(entity.getId().getId(), entity);
	}

	/**	Checking whether the time deviation already reported.
	 * @param stopId 
	 * @param routeId 
	 * @param vehicleId */
	private TimeDeviation checkForTimeDeviationAlreadyReported(Long vehicleId, Long routeId, Long stopId) {
		LOG.debug("ETAETA : Checking for TimeDeviation already reported for Vehicle : "
				+vehicleId+", Route : "+routeId+" and Stop : "+stopId);
		StopHistory stopHistoryEntity = getFirstStopHistoryFromCache(vehicleId, routeId);
		for(Long timeDeviationId : LoadSASTimeDeviations.getInstance().cacheSASTimeDeviation.keySet()){
			TimeDeviation timeDeviationEntity = LoadSASTimeDeviations.getInstance().retrieve(timeDeviationId);
			if(timeDeviationEntity != null && timeDeviationEntity.getVehicleId() == vehicleId && 
					timeDeviationEntity.getRouteId() == routeId && timeDeviationEntity.getStopId() == stopId && 
					timeDeviationEntity.getActualTime().getTime() > stopHistoryEntity.getActualTime().getTime()){
				return timeDeviationEntity;
			}
		}
		return null;
	}

	/**
	 * Check whether its the last stop. 
	 * @param routeSchedule 
	 * @param vehicleId 
	 * 
	 * @return
	 */
	private boolean isLastStop(long vehicleId, RouteSchedule routeSchedule) {
		Long stopId = routeSchedule.getStopId();
		LOG.debug("ETAETA : Checking for the Last stop reaching for Vehicle : "+vehicleId+" and Route : "+
				routeSchedule+" on reaching Stop : "+stopId);
		int lastSequenceNo = getLastStopSequence(vehicleId, routeSchedule);
		Vector<RouteSchedule> routeScheduleList = LoadRouteSchedule.getInstance().retrieve(routeSchedule.getRouteScheduleId());
		if(routeScheduleList == null){
			LOG.error("ETAETA : Vehicle Route Stop details list not cached for Vehicle : "+vehicleId+" and Route : "+routeSchedule);
			return false;
		}
		for(RouteSchedule schedule : routeScheduleList){
			if((schedule.getStopId() == stopId) && (schedule.getSequenceNumber() == lastSequenceNo)){
				LOG.debug("ETAETA : Vehicle :"+vehicleId+" has reached the last stop : "+stopId+" for the Route : "+routeSchedule);
				return true;
			}
		}
		return false;
	}

	/** Returns the last/max sequence number of the route for the vehicle.
	 * @param routeSchedule 
	 * @param vehicleId */
	private int getLastStopSequence(long vehicleId, RouteSchedule routeSchedule) {
		LOG.debug("ETAETA : Getting the last stop sequence for Route : "+routeSchedule+" for Vehicle : "+vehicleId);
		int lastSequenceNumber = 0;
		Vector<RouteSchedule> routeScheduleList = LoadRouteSchedule.getInstance().retrieve(routeSchedule.getRouteScheduleId());
		if(routeScheduleList == null){
			LOG.error("ETAETA : Unlikely event of vehicleRouteStopDetailsList being null while fetching last stop sequence");
			return 0;
		}
		lastSequenceNumber = routeScheduleList.get(0).getSequenceNumber();
		for(RouteSchedule schedule : routeScheduleList){
			if(lastSequenceNumber < schedule.getSequenceNumber()){
				lastSequenceNumber = schedule.getSequenceNumber();
			}
		}
		LOG.debug("ETAETA : Last stop sequence after processing is : "+lastSequenceNumber);
		return lastSequenceNumber;
	}

	/**
	 * Check whether vehicle has reached next stop or not
	 * 
	 * 1) Get the details for the recently reached stop(From stopHistory table)
	 * 2) Filter the VehicleRouteStopDetailsList based on the recently reached stop 
	 * 3) Get the stopEntity and check the Vehicle reachability to the stop 
	 * 4) Return true if reached else return false
	 * @param vehicleToRouteSchedule 
	 * @param vehicleId 
	 * @param longitude 
	 * @param latitude 
	 * @return true or false
	 */
	private RouteSchedule getNextStopReachedDetails(long vehicleId, VehicleToRouteSchedule vehicleToRouteSchedule, double latitude, double longitude) {

		StopHistory latestStopHistoryEntity = getLatestStopHistoryEntity(vehicleId, vehicleToRouteSchedule.getRouteId());
		Vector<RouteSchedule> routeScheduleList =  LoadRouteSchedule.getInstance().retrieve(getRouteScheduleId(vehicleToRouteSchedule));
		if(routeScheduleList == null){
			LOG.error("ETAETA : RouteSchedule had to be cached");
			return null;
		}

		List<Long> processedStopIdsList = new ArrayList<Long>();

		for(RouteSchedule routeSchedule : 
			filterUnwantedVehicleRouteStopDetailsEntities(routeScheduleList, latestStopHistoryEntity)){ 
			Stops stopEntity = LoadStopsDetails.getInstance().retrieve(routeSchedule.getStopId());
			if(stopEntity == null){
				LOG.error("ETAETA : Stop "+routeSchedule.getStopId()+" is not cached");
				continue;
			}
			if(!processedStopIdsList.contains(stopEntity.getId().getId())){
				LOG.debug("ETAETA : Stop : "+stopEntity.getId().getId()+
						" is not processed for stop range check hence proceed with range check");
				processedStopIdsList.add(stopEntity.getId().getId());
			}else {
				LOG.debug("ETAETA : Stop : "+stopEntity.getId().getId()+
						" is already processed for stop range check hence skip to next stop");
				continue;
			}
			if (isVehicleWithinTheStopRange(stopEntity, latitude, longitude)) {
				// Next stop reached
				LOG.debug("ETAETA : NextStop reached details : "+routeSchedule.toString());
				return routeSchedule;
			} else {
				LOG.debug("ETAETA : Vehicle : "+vehicleId+" not in stop range of stop : "+stopEntity.getId().getId());
			}
		}
		return null;

	}

	/**
	 * @param routeScheduleList
	 * @param latestStopHistoryEntity
	 * @return List of VehicleRouteStopDetails entities which are filtered based on recently reached Stop for the Vehicle
	 */
	private Vector<RouteSchedule> filterUnwantedVehicleRouteStopDetailsEntities(
			Vector<RouteSchedule> routeScheduleList, StopHistory latestStopHistoryEntity) {
		LOG.debug("ETAETA : Getting the last stop sequence for Route : "+latestStopHistoryEntity.getRouteId()
				+" for Vehicle : "+latestStopHistoryEntity.getVehicleId());

		// Sort the VehicleStopDetailsVector so that earliest time appears first
		Collections.sort(routeScheduleList, new Comparator<RouteSchedule>() {
			public int compare(RouteSchedule o1, RouteSchedule o2) {
				return o1.getExpectedTime().compareTo(o2.getExpectedTime());
			}});

		// Identify the last stop
		Vector<RouteSchedule> copyOfRouteScheduleList = new Vector<RouteSchedule>();

		int i = 0 ;
		LOG.debug("ETAETA : Filtered RouteSchedule List ");
		for(RouteSchedule routeSchedule : routeScheduleList){
			if ((routeSchedule.getSequenceNumber() > latestStopHistoryEntity.getSeqNo()) /*&& 
					isVehicleRouteStopDetailsEntityTimeGreaterThanStopEntityTime(routeSchedule, latestStopHistoryEntity)*/) {
				// Filtered list of all stops above the latest stop reached time
				LOG.debug("ETAETA : "+(i+1)+" : "+routeSchedule.toString());
				copyOfRouteScheduleList.add(i++, routeSchedule);
			}
		}
		LOG.debug("Returning the RouteScheduleList with size : "+copyOfRouteScheduleList.size());
		return copyOfRouteScheduleList;
	}

	/** Returns the stopHistory entity which is the latest entry for the current vehicles active route.
	 * @param routeId 
	 * @param vehicleId 
	 */
	private StopHistory getLatestStopHistoryEntity(long vehicleId, Long routeId) {
		return stopHistoryDao.getLatestStopHistoryForTheVehicle(vehicleId, routeId);
	}

	/**
	 * Check whether any Route assigned to current vehicle is starting or not
	 * 
	 * @param routeStatusListForTheVehicle
	 * @param vehicleId
	 * @param bulkDataTime
	 * @param latitude
	 * @param longitude
	 * @return
	 */
	private RouteSchedule getRouteStartingDetails(ConcurrentHashMap<VehicleToRouteSchedule, Boolean> routeStatusListForTheVehicle, long vehicleId,
			Date bulkDataTime, double latitude, double longitude) {

		for(VehicleToRouteSchedule vehicleToRouteSchedule : routeStatusListForTheVehicle.keySet()){
			Long routeId = vehicleToRouteSchedule.getRouteId();
			Routes routeEntity = LoadRoutesDetails.getInstance().retrieve(routeId);
			if(routeEntity == null){
				LOG.error("ETAETA : Route : "+routeId+" is not cached for the vehicle : "+vehicleId);
				/**	Route not cached. Skip to next route.*/
				continue;
			}
			String routeScheduleId = getRouteScheduleId(vehicleToRouteSchedule);
			Vector<RouteSchedule> routeScheduleList = LoadRouteSchedule.getInstance().retrieve(routeScheduleId);
			if(routeScheduleList == null){
				LOG.error("ETAETA : RouteSchedule list for vehicle : "+vehicleId+ " & route : "+routeId+" is not cached");
				/**	VehicleRouteStopAssociation is not cached. Skip to next route.*/
				continue;
			}
			for(RouteSchedule routeSchedule: routeScheduleList){
				if(routeSchedule.getSequenceNumber() == 1){
					/**	This is the starting point. Proceed with processing.*/
				}else {
					/** Not the starting point of the route. Hence skip to next stop.*/
					continue;
				}
				Calendar cal = Calendar.getInstance();
				cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), routeSchedule.getExpectedTime().
						getHours(), routeSchedule.getExpectedTime().getMinutes(), 0);
				cal.set(Calendar.MILLISECOND, 0);
				// Giving the flexibility of -15 and +90 minutes to the scheduled time of the vehicle  
				cal.add(Calendar.MINUTE, -15);
				Date startTimeRange1 = cal.getTime();
				cal.add(Calendar.MINUTE, 105);
				Date startTimeRange2 = cal.getTime();
				LOG.debug("ETAETA : RouteTime range1 :"+ startTimeRange1.toLocaleString());
				LOG.debug("ETAETA : RouteTime range2 :"+ startTimeRange2.toLocaleString());
				if ((startTimeRange1.getTime() <= bulkDataTime.getTime()) && (bulkDataTime.getTime() <= startTimeRange2.getTime())) {
					LOG.info("ETAETA : Module time within route time for vehicle : "+vehicleId+" and route : "+routeId);
					Stops stopEntity = LoadStopsDetails.getInstance().retrieve(routeSchedule.getStopId());
					if(stopEntity == null){
						/** Stop is not cached. Hence skip to next route.*/
						LOG.error("ETAETA : Starting Stop :"+routeSchedule.getStopId()+" for Route :"+
								routeSchedule.getRouteId()+" and Vehicle : "+vehicleId+" had to be cached");
						break;
					}
					if (isVehicleWithinTheStopRange(stopEntity, latitude, longitude)) {
						//	Activating the route
						LOG.info("ETAETA : Starting the Route :  "+ routeSchedule.toString());
						return routeSchedule;
					} else {
						LOG.warn("ETAETA : Vehicle : "+vehicleId+" is out of range of first stop : "+stopEntity.getId().getId()
								+" for route : "+routeId);
					}
				}else{
					LOG.warn("ETAETA : Module time out of range for the Vehicle : "+vehicleId+" and Route : "+routeId);
				}
			}
		}
		return null;
	}

	private String getRouteScheduleId(VehicleToRouteSchedule vehicleToRouteSchedule) {
		// Formulate the id in the format : "RouteId-HOurs:Minutes" where hours and minutes are two in length
		String hours = vehicleToRouteSchedule.getScheduleTime().getHours()+"";
		String minutes = vehicleToRouteSchedule.getScheduleTime().getMinutes()+"";
		if(hours.length() == 1)
			hours = "0"+hours;
		if(minutes.length() == 1)
			minutes = "0"+minutes;

		String formulatedRouteScheduleId = vehicleToRouteSchedule.getRouteId()+"-"+hours+":"+minutes;
		return formulatedRouteScheduleId.trim();
	}

	/**
	 * Function to know whether the vehicle has reached the stop or not.
	 * 
	 * @param stopEntity
	 * @param longitude 
	 * @param latitude 
	 * @return
	 */
	private boolean isVehicleWithinTheStopRange(Stops stopEntity, double latitude, double longitude) {

		double stopLat = stopEntity.getLatPoint();
		double stopLon = stopEntity.getLonPoint();

		double vehicleLat = latitude;
		double vehicleLon = longitude;

		double d = CustomCoordinates.distance(new Point(stopLat,stopLon), new Point(vehicleLat,vehicleLon));

		// Is the vehicle within 150 meters range of the stop.
		if (Math.abs(d) >= 0 && Math.abs(d) <= 0.15) {
			LOG.info("MBMC : vehicle is with in the stop range ");
			return true;
		}
		return false;
	}

	/**
	 * Check whether any Route is already started or not
	 * 
	 * @param routeListForTheVehicle
	 * @param vehicleId
	 * @return VehicleToRouteSchedule entity for which there is an active active
	 */
	private VehicleToRouteSchedule getActiveRouteForVehicle(ConcurrentHashMap<VehicleToRouteSchedule, Boolean> routeListForTheVehicle, long vehicleId) {
		for(VehicleToRouteSchedule vehicleToRouteSchedule : routeListForTheVehicle.keySet()){
			LOG.debug("MBMC : getActiveRouteForVehicle for Key set is : "+vehicleToRouteSchedule.getRouteId()+" ");
			if(routeListForTheVehicle.get(vehicleToRouteSchedule)){
				LOG.debug("ETAETA : Route : "+vehicleToRouteSchedule.getRouteId()+" with schedule "
						+vehicleToRouteSchedule.getScheduleTime().toString()+" is active for Vehicle : "+vehicleId);
				return vehicleToRouteSchedule;
			}else{
				LOG.debug("MBMC : vehicleToRouteSchedule was not present in cache for Schedule Id : "+vehicleToRouteSchedule);
			}
		}
		LOG.debug("ETAETA : No route active for vehicle : "+vehicleId);
		return null;
	}

	/**
	 * Returns true if the current time exceeds the route end time by one hour
	 * @param activeVehicleToRouteSchedule 
	 * 
	 * @param route
	 * @param bulkDataTime 
	 * @return
	 */
	private boolean isRouteToBeDeactivated(VehicleToRouteSchedule activeVehicleToRouteSchedule, Routes route, Date bulkDataTime) {
		LOG.debug("ETAETA : Checking for the current route : "+route.getId().getId()+" to be deactivated for Vehicle : "
				+activeVehicleToRouteSchedule.getVehicleId());
		StopHistory firstStopHistoryEntity = getFirstStopHistoryFromCache(activeVehicleToRouteSchedule.getVehicleId(), route.getId().getId());
		if (firstStopHistoryEntity != null) {
			LOG.debug("MBMC : firstStopHistoryEntity is not null "+firstStopHistoryEntity.toString());
			Date routeStartDate = new Date(firstStopHistoryEntity.getMbmcActualTime().getTime());
			Calendar cal = Calendar.getInstance();
			LOG.debug("MBMC : routeStartDate in milliseconds "+routeStartDate.getTime()+" routeStartDate is date "+routeStartDate);
			cal.setTimeInMillis(routeStartDate.getTime());
			cal.add(Calendar.DATE, (route.getSpanningDays()-1));
			Date routeEndDate = cal.getTime();
			RouteSchedule lastStopCorrespondingToTheRoute = getLastStopForCurrentTrip(activeVehicleToRouteSchedule);
			if(lastStopCorrespondingToTheRoute == null){
				LOG.error("ETAETA : LastStopCorrespondingToTheRoute has returned null for Route : "+route.getId().getId()+" and Vehicle : "
						+activeVehicleToRouteSchedule.getVehicleId());
				return false;
			}
			// Setting Route end date to 90 minutes more than the last stop reaching time
			routeEndDate.setHours(lastStopCorrespondingToTheRoute.getExpectedTime().getHours());
			routeEndDate.setMinutes(lastStopCorrespondingToTheRoute.getExpectedTime().getMinutes());
			cal.setTimeInMillis(routeEndDate.getTime());
			cal.add(Calendar.MINUTE, 90);
			routeEndDate = new Date(cal.getTimeInMillis());
			LOG.debug("MBMC : routeEndDate in milliseconds "+routeEndDate.getTime()+" routeEndDate is date "+routeEndDate);

			if (bulkDataTime.getTime() >= routeEndDate.getTime()) {
				LOG.info("ETAETA : BulkDataTime :"+bulkDataTime.toString()+" exceeding the Routetime :"+routeEndDate.toString()+
						". Hence deactivating the Route : "+route.getId().getId()+" for Vehicle : "+activeVehicleToRouteSchedule.getVehicleId());
				return true;
			} else{
				LOG.debug("ETAETA : BulkDataTime :"+bulkDataTime.toString()+" not exceeding the Routetime :"+routeEndDate.toString()+
						". Hence not deactivating the Route : "+route.getId().getId()+" for Vehicle : "+activeVehicleToRouteSchedule.getVehicleId());
			}
		}else{
			LOG.error("MBMC : first stop history was null "+firstStopHistoryEntity);
		}
		return false;
	}

	/**
	 * 
	 * @param vehicleId
	 * @param routeId
	 * @return Last stop's vehicleRoute entity for the current trip
	 */
	private RouteSchedule getLastStopForCurrentTrip (VehicleToRouteSchedule vehicleToRouteSchedule) {
		Long routeId = vehicleToRouteSchedule.getRouteId();
		Long vehicleId = vehicleToRouteSchedule.getVehicleId();
		LOG.debug("ETAETA : Getting the last stop for Route : "+routeId+" for Vehicle : "+vehicleId);
		Vector<RouteSchedule> routeScheduleList = LoadRouteSchedule.getInstance().retrieve(getRouteScheduleId(vehicleToRouteSchedule));
		if(routeScheduleList == null){
			LOG.error("ETAETA : Unlikely event of vehicleRouteStopDetailsList being null while fetching last stop sequence");
			return null;
		}

		// Sort the VehicleStopDetailsVector so that earliest time appears first
		Collections.sort(routeScheduleList, new Comparator<RouteSchedule>() {
			public int compare(RouteSchedule o1, RouteSchedule o2) {
				return o1.getExpectedTime().compareTo(o2.getExpectedTime());
			}});

		// Identify the last stop
		RouteSchedule lastStop = new RouteSchedule();
		lastStop.setSequenceNumber(-1);

		for(RouteSchedule schedule : routeScheduleList){
			// Filtered list of all stops above the starting time
			if (schedule.getSequenceNumber() > lastStop.getSequenceNumber()) {
				lastStop = schedule;	
			}
		}

		if(lastStop.getSequenceNumber() != -1){
			LOG.debug("ETAETA : Last Stop selected : "+lastStop.toString());
			return lastStop;
		}
		LOG.error("ETAETA : Error while getting last stop");
		return null;
	}
}
