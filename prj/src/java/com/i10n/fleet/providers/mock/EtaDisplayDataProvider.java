package com.i10n.fleet.providers.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.entity.EtaDisplay;
import com.i10n.db.entity.RouteSchedule;
import com.i10n.db.entity.Routes;
import com.i10n.db.entity.StopHistory;
import com.i10n.db.entity.Stops;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.VehicleToRouteSchedule;
import com.i10n.dbCacheManager.LoadActiveStopHistoryDetails;
import com.i10n.dbCacheManager.LoadRouteSchedule;
import com.i10n.dbCacheManager.LoadRoutesDetails;
import com.i10n.dbCacheManager.LoadStopsDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.dbCacheManager.LoadVehicleRouteAssociationDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.eta.ETAUtils;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.web.request.RequestParameters;

/**
 * Class to provide data necessary for EtaDisplay.
 * 
 * @author swathi ep, dharmaraju v
 * 
 */
public class EtaDisplayDataProvider implements IDataProvider {

	private static final Logger LOG = Logger.getLogger(EtaDisplayDataProvider.class);

	@Override
	public IDataset getDataset(RequestParameters params) {
		IDataset result = new Dataset();
		IDataset etaDisplayDataset = new Dataset();
		IDataset stopData = null;
		String stopList = params.getRequestParameter("stopList");
		LOG.debug("Received the request for the stops : "+stopList);
		String[] selectedStops = stopList.split(":");
		List<IDataset> stopsList = new ArrayList<IDataset>();
		for(String stop : selectedStops){
			Long stopId = Long.parseLong(stop.substring(5, stop.length()));
			// get the routes to which the given stops are assigned
			List<RouteSchedule> assignedRoutes = getRoutesAssignedToTheStop(stopId);
			for (RouteSchedule routeSchedule : assignedRoutes) {
				VehicleToRouteSchedule activeRouteSchedule = getActiveRouteSchedule(routeSchedule.getRouteId());
				if (activeRouteSchedule != null) {
					// first confirm that the current stop has not been reached yet
					if (!isCurrentStopReached(activeRouteSchedule.getVehicleId(), activeRouteSchedule.getRouteId(), stopId)) {

						Vehicle vehicleEntity = LoadVehicleDetails.getInstance().retrieve(activeRouteSchedule.getVehicleId());
						if(vehicleEntity == null){
							LOG.error("Vehicle : "+activeRouteSchedule.getVehicleId()+" is not cached ");
							/** Vehicle not cached hence skipping to next iteration.*/
							continue;
						}

						Routes routeEntity = LoadRoutesDetails.getInstance().retrieve(activeRouteSchedule.getRouteId());
						if(routeEntity == null){
							LOG.error("Route : "+activeRouteSchedule.getRouteId()+" is not cached ");
							/** Route not cached hence skipping to next iteration.*/
							continue;
						}

						Stops stopEntity = LoadStopsDetails.getInstance().retrieve(stopId);
						if(stopEntity == null){
							LOG.error("Stop : "+stopId+" is not cached ");
							/** Stop not cached hence skipping to next iteration.*/
							continue;
						}
						String stopName = stopEntity.getStopName();
						int currentSequenceNumber = getCurrentSequenceNumber(activeRouteSchedule, stopId);
						if(currentSequenceNumber == 0 ){
							LOG.error("Incorrect sequence number");
							return result;
						}
						Vector<RouteSchedule> next3Stops = getNext3Stops(activeRouteSchedule, stopId, currentSequenceNumber);
						
						ArrayList<String> stopNames = new ArrayList<String>();
						for(int s=0 ; s < next3Stops.size(); s++){
							Stops stopCache = LoadStopsDetails.getInstance().retrieve((next3Stops.get(s).getStopId()));
							if(stopCache == null){
								continue;
							}
							stopNames.add(stopCache.getStopName());
							if(stopNames.size() == 3){
								break;
							}
						}

						// get the previous stop sequence of the selectedstop
						int prevStopSeq = currentSequenceNumber-1;
						// check whether the given stop is an start point
						if (prevStopSeq != 0) {
							// check if the vehicle has reached the stop previous to selected one
								stopData = new Dataset();
								EtaDisplay etaDisplays = selectETAByVehicleIdRouteIdAndStopId(activeRouteSchedule.getVehicleId(),
										activeRouteSchedule.getRouteId(),stopId);
								stopData.put("vehiclename",vehicleEntity.getDisplayName());
								stopData.put("routename", routeEntity.getRouteName()+" ("+routeEntity.getEndPoint()+")");
								stopData.put("stopname", stopName);
								stopData.put("stopnames", stopNames);
								if(etaDisplays != null){
									stopData.put("expectedtime",etaDisplays.getArrivalTime());
								}else{
									stopData.put("expectedtime","Not Available");
								}
								stopsList.add(stopData);
						}
					}
				} else {
					LOG.debug("Route : "+routeSchedule.getRouteId()+" not active");
				}
			}
		}
		etaDisplayDataset.put("etadata", stopsList);
		result = etaDisplayDataset;
		return result;
	}

	private int getCurrentSequenceNumber(VehicleToRouteSchedule activeRouteSchedule, Long stopId) {

		LOG.debug("Getting the sequence number for the stop : "+stopId+" with route : "+activeRouteSchedule.getRouteId());
		String routeScheduleId = ETAUtils.getRouteScheduleId(activeRouteSchedule);
		Vector<RouteSchedule> routeScheduleList = LoadRouteSchedule.getInstance().retrieve(routeScheduleId);
		if(routeScheduleList == null){
			LOG.debug("RouteSchedule not cached for the id : "+routeScheduleId);
			return 0;
		}
		/*
		 * Get Current sequence number for the cache
		 */
		for(RouteSchedule routeSchedule : routeScheduleList){
			if(routeSchedule.getStopId() == stopId){
				LOG.debug("Returning the sequence number : "+routeSchedule.getSequenceNumber());
				return routeSchedule.getSequenceNumber();
			}
		}
		LOG.warn("Stop missing from the list for the route");
		return 0;
	}

	private VehicleToRouteSchedule getActiveRouteSchedule(long routeId) {
		for(Long vehicleId : LoadVehicleRouteAssociationDetails.getInstance().cacheVehicleToRouteAssociation.keySet()){
			ConcurrentHashMap<VehicleToRouteSchedule,Boolean> routeStatusHashMap = 
					LoadVehicleRouteAssociationDetails.getInstance().retrieve(vehicleId);
			for(VehicleToRouteSchedule vehicleRouteSchedule : routeStatusHashMap.keySet()){
				if(vehicleRouteSchedule.getRouteId() == routeId){
					if(routeStatusHashMap.get(vehicleRouteSchedule)){
						LOG.debug("Route : "+routeId+" is active for Vehicle : "+vehicleId);
						return vehicleRouteSchedule;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns the EtaDisplay entity for the particular vehicle route and stop.
	 * @param vehicleId
	 * @param routeId
	 * @param stopId
	 * @return
	 */
	private EtaDisplay selectETAByVehicleIdRouteIdAndStopId(long vehicleId, long routeId, long stopId) {
		Vector<EtaDisplay> etaDisplayList = EtaDisplayDataHelper.getEtaDisplayList(vehicleId);
		if(etaDisplayList == null){
			return null;
		}
		for(EtaDisplay etaDisplay : etaDisplayList){
			if(etaDisplay.getRouteId() == routeId && etaDisplay.getStopId() == stopId){
				return etaDisplay;
			}
		}
		return null;
	}

	/**
	 * Returns next 3 stops from current stop.
	 * 
	 * @param vehicleToRouteSchedule
	 * @param stopId
	 * @param currentSequenceNumber
	 * @return
	 */
	private Vector<RouteSchedule> getNext3Stops(VehicleToRouteSchedule vehicleToRouteSchedule, Long stopId, int currentSequenceNumber) {
		LOG.debug("NEXT3STOPS : Getting Next 3 Stops for Vehicle : "+vehicleToRouteSchedule.getVehicleId()+" and Route : "
				+vehicleToRouteSchedule.getRouteId());
		String routeScheduleId = ETAUtils.getRouteScheduleId(vehicleToRouteSchedule);
		Vector<RouteSchedule> routeScheduleList = LoadRouteSchedule.getInstance().retrieve(routeScheduleId);
		if(routeScheduleList == null){
			return new Vector<RouteSchedule>();
		}
		Vector<RouteSchedule> routeScheduleListToReturn = new Vector<RouteSchedule>();
		ArrayList<Long> stopIdLookUpList = new ArrayList<Long>();

		for(RouteSchedule routeSchedule : routeScheduleList){
			if((routeSchedule.getSequenceNumber() <= (routeSchedule.getSequenceNumber()+3)) 
					&& (routeSchedule.getSequenceNumber() > currentSequenceNumber)){
				if(!stopIdLookUpList.contains(routeSchedule.getStopId())){
					stopIdLookUpList.add(routeSchedule.getStopId());
					routeScheduleListToReturn.add(routeSchedule);
				}
			}
		}
		stopIdLookUpList.clear();
		return routeScheduleListToReturn;
	}


	/**
	 * Returns true if the current stop of the route of the vehicle is reached. Else returns false.
	 * @param vehicleId
	 * @param routeId
	 * @param stopId
	 * @return
	 */
	private boolean isCurrentStopReached(long vehicleId, long routeId, long stopId) {

		for(Long stopHistoryId : LoadActiveStopHistoryDetails.getInstance().cacheStopHistory.keySet()){
			StopHistory stopHistory = LoadActiveStopHistoryDetails.getInstance().cacheStopHistory.get(stopHistoryId);
			if(stopHistory.getVehicleId() == vehicleId && stopHistory.getRouteId() == routeId && 
					stopHistory.getStopId() == stopId && stopHistory.isActive()){
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the list of routes associated with the vehicle assigned to the stop.
	 * @param stopId
	 * @return
	 */
	private Vector<RouteSchedule> getRoutesAssignedToTheStop(Long stopId) {
		LOG.debug("Getting the route assigned list for the stop : "+stopId);
		
		ArrayList<Long> routeIdLookUpList = new ArrayList<Long>();
		Vector<RouteSchedule> routeScheduleReturnList = new Vector<RouteSchedule>();

		for(String routeScheduleId : LoadRouteSchedule.getInstance().cacheRouteSchedule.keySet()){
			for(RouteSchedule routeSchedule : LoadRouteSchedule.getInstance().retrieve(routeScheduleId)){
				if(routeSchedule.getStopId() == stopId){
					if(!routeIdLookUpList.contains(routeSchedule.getRouteId())){
						routeIdLookUpList.add(routeSchedule.getRouteId());
						routeScheduleReturnList.add(routeSchedule);
						break;
					}

				}
			}
		}

		routeIdLookUpList.clear();
		return routeScheduleReturnList;
	}

	@Override
	public String getName() {
		return "etadisplay";
	}

}
