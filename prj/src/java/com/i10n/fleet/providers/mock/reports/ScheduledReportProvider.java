package com.i10n.fleet.providers.mock.reports;

import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.entity.RouteSchedule;
import com.i10n.db.entity.VehicleToRouteSchedule;
import com.i10n.dbCacheManager.LoadRouteSchedule;
import com.i10n.dbCacheManager.LoadRoutesDetails;
import com.i10n.dbCacheManager.LoadStopsDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.dbCacheManager.LoadVehicleRouteAssociationDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.eta.ETAUtils;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

public class ScheduledReportProvider implements IDataProvider {
	
	private static Logger LOG = Logger.getLogger(ScheduledReportProvider.class);

	private Long vehicleId;
	

	@Override
	public IDataset getDataset(RequestParameters params) {
		IDataset result = new Dataset();
		String vehicleid = params.getRequestParameter("vehicleID");
		String vehicleidVariable = StringUtils.getValueFromKVP(vehicleid);
		vehicleId = Long.parseLong(StringUtils.stripCommas(vehicleidVariable
				.trim()));
		result.put("scheduledreport", this.getData(getDataOptions(params)));
		return result;
	}

	private IDataset getData(IDataset Options) {
		IDataset scheduledreportData = new Dataset();
		IDataset stopData = null;
		Vector<RouteSchedule> routeScheduleList = getRouteScheduleList(vehicleId);
		if (routeScheduleList.size() != 0) {
			Vector<IDataset> stopList = new Vector<IDataset>();
			for (RouteSchedule routeSchedule : routeScheduleList){
				stopData = new Dataset();
				String stopName = LoadStopsDetails.getInstance().cacheStop.get(routeSchedule.getStopId()).getStopName();
				String vehicleName = LoadVehicleDetails.getInstance().cacheVehicleRecords.get(vehicleId).getDisplayName();
				String routeName = LoadRoutesDetails.getInstance().cacheRoute.get(routeSchedule.getRouteId()).getRouteName();
				stopData.put("vehiclename", vehicleName);
				stopData.put("routename", routeName);
				stopData.put("stopname", stopName);
				stopData.put("sequenceno", routeSchedule.getSequenceNumber()+ "");
				stopData.put("expectedtime", routeSchedule.getExpectedTime());
				stopList.add(stopData);
			}
			scheduledreportData.put("vehicle-" + vehicleId + ".routes",stopList);
		}
		return scheduledreportData;
	}

	/**
	 * Get the list of the schedule of the given vehicle
	 * @param vehicleId
	 * @return
	 */
	private Vector<RouteSchedule> getRouteScheduleList(Long vehicleId) {
		ConcurrentHashMap<VehicleToRouteSchedule, Boolean> vehicleRouteScheduleStatusMap = 
				LoadVehicleRouteAssociationDetails.getInstance().retrieve(vehicleId);
		LOG.debug("Getting the Schedule list for the vehicle : "+vehicleId);
		Vector<RouteSchedule> returnList = new Vector<RouteSchedule>();
		
		if(vehicleRouteScheduleStatusMap != null){
			for(VehicleToRouteSchedule vehicleToRouteSchedule : vehicleRouteScheduleStatusMap.keySet()){
				String routeScheduleId = ETAUtils.getRouteScheduleId(vehicleToRouteSchedule);
				Vector<RouteSchedule> routeScheduleList = LoadRouteSchedule.getInstance().retrieve(routeScheduleId);
				if(routeScheduleList != null){
					returnList.addAll(routeScheduleList);
				}
			}
		}
		LOG.debug("Returning the list of size : "+returnList.size());
		return returnList;
	}

	private IDataset getDataOptions(RequestParameters params) {
		IDataset options = new Dataset();
		String vehicleID = params.getParameter(RequestParams.vehicleID);
		if (null != vehicleID) {
			options.put("filter.vehicleid", vehicleID);
		}
		return options;
	}

	@Override
	public String getName() {
		return "scheduledreport";
	}

}
