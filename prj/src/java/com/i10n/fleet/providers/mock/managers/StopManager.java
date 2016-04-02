package com.i10n.fleet.providers.mock.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.i10n.db.entity.RouteSchedule;
import com.i10n.db.entity.Stops;
import com.i10n.db.entity.User;
import com.i10n.db.entity.VehicleToRouteSchedule;
import com.i10n.dbCacheManager.LoadAclVehicleDetails;
import com.i10n.dbCacheManager.LoadRouteSchedule;
import com.i10n.dbCacheManager.LoadRoutesDetails;
import com.i10n.dbCacheManager.LoadStopsDetails;
import com.i10n.dbCacheManager.LoadVehicleRouteAssociationDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.eta.ETAUtils;
import com.i10n.fleet.web.utils.SessionUtils;

public class StopManager extends AbstractDataManager implements IStopManager {

	private IDataset m_dataset = null;
	IDataset result = new Dataset();

	@Override
	protected String getDocumentName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IDataset getData(IDataset options) {
		if (null == m_dataset) {
			result = parseDataset();
		}
		return result;
	}

	private IDataset parseDataset() {
		User user = SessionUtils.getCurrentlyLoggedInUser();
		Long userId = user.getId();
		List<Stops> resultset = getUserAssignedStops(userId);
		IDataset stopData = new Dataset();
		Stops stop = null;

		if (null != resultset) {
			for (int i = 0; i < resultset.size(); i++) {
				stop = resultset.get(i);
				
				stopData.put("stop-" + (i + 1) + ".id", "stop-"+ stop.getId().getId());
				stopData.put("stop-" + (i + 1) + ".name", stop.getStopName());
				stopData.put("stop-" + (i + 1) + ".knownas", stop.getKnowAs());
				stopData.put("stop-" + (i + 1) + ".lat", stop.getLatPoint());
				stopData.put("stop-" + (i + 1) + ".lon", stop.getLonPoint());
				stopData.put("stop-" + (i + 1) + ".assigned", "true");
				stopData.put("stop-" + (i + 1) + ".ownerid", "owner-"+ stop.getOwnerId());
			}
		}
		result = stopData;
		return result;
	}

	private List<Stops> getUserAssignedStops(Long userId) {
		Vector<Long> vehicleIdList = LoadAclVehicleDetails.getInstance().cacheAclVehicleDetails.get(userId);
		if(vehicleIdList == null){
			return null;
		}
		List<Stops> stopList = new ArrayList<Stops>();
		for(Long vehicleId : vehicleIdList)
			for(Long routeId : LoadRoutesDetails.getInstance().cacheRoute.keySet())
				if(userId == LoadRoutesDetails.getInstance().cacheRoute.get(routeId).getOwnerId()){
					ConcurrentHashMap<VehicleToRouteSchedule, Boolean> vehicleRouteScheduleMap = LoadVehicleRouteAssociationDetails.getInstance().retrieve(vehicleId);
					if(vehicleRouteScheduleMap != null)
						for(VehicleToRouteSchedule vehicleRouteSchedule : vehicleRouteScheduleMap.keySet()){
							String routeScheduleId = ETAUtils.getRouteScheduleId(vehicleRouteSchedule);
							Vector<RouteSchedule> routeScheduleList = LoadRouteSchedule.getInstance().retrieve(routeScheduleId);
							if(routeScheduleList != null)
								for(RouteSchedule routeSchedule : routeScheduleList){
									Stops stop = LoadStopsDetails.getInstance().cacheStop.get(routeSchedule.getStopId());
									if(null != stop && !stopList.contains(stop))
										stopList.add(stop);
								}
							
						}
					}
		
		return ((stopList.size() == 0) ? null : stopList);
	}
}