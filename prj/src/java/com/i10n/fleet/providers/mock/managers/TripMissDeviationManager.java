package com.i10n.fleet.providers.mock.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.TripMissDeviationDaoImp;
import com.i10n.db.entity.Routes;
import com.i10n.db.entity.TripMissDeviation;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadRoutesDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.web.utils.SessionUtils;

@SuppressWarnings("deprecation")
public class TripMissDeviationManager extends AbstractViolationsManager implements ITripMissDeviationManager {
	
	public static Logger LOG = Logger.getLogger(TripMissDeviationManager.class);
	
	@Override
	protected String getDocumentName() {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataset getData(IDataset options) {
		IDataset data = getParsedData(options);
		return data;
	}

	private synchronized IDataset getParsedData(IDataset options) {
		LOG.debug("TRIPMISS : Checking for trip miss data");
		IDataset data = new Dataset();
		IDataset ArrayData = null;
		List<TripMissDeviation> resultset = null;
		ArrayList<Dataset> tripMissDeviationData = null;
		
		String startdate = null, enddate = null, vehicleIdString = null, localTimeZone = null;
		
		localTimeZone = options.getValue("localTimeZone");
		startdate = options.getValue("startdate");
		enddate = options.getValue("enddate");
		vehicleIdString = options.getValue("vehicleID");
		
		if(vehicleIdString == null){
			LOG.debug("TRIPMISS : Getting the violation report based on userid");
			resultset = ((TripMissDeviationDaoImp) DBManager.getInstance().getDao(DAOEnum.TRIP_MISS_DEVIATIONS_DAO)).
					selectByUserId(SessionUtils.getCurrentlyLoggedInUser().getId(),new Date(startdate),new Date(enddate));
		}else{
			LOG.debug("TRIPMISS : Getting the violation report based on vehicleid");
			String[] vehIdArray = vehicleIdString.split("-");
			LOG.debug("TRIPMISS : Getting the violation report based on vehicleid  : "+Long.parseLong(vehIdArray[1]));
			resultset = ((TripMissDeviationDaoImp) DBManager.getInstance().getDao(DAOEnum.TRIP_MISS_DEVIATIONS_DAO)).
					selectByVehicleId(Long.parseLong(vehIdArray[1]),new Date(startdate),new Date(enddate));
		}
		TripMissDeviation tripMissDeviation = null;
		Long vehicleId = null;
		if (null != resultset) {
			tripMissDeviationData = new ArrayList<Dataset>();
			for (int i = 0; i < resultset.size(); i++) {
				ArrayData = new Dataset();
				tripMissDeviation = resultset.get(i);
				vehicleId = tripMissDeviation.getVehicleId();
				Vehicle vehicle = LoadVehicleDetails.getInstance().retrieve(vehicleId);
				if(vehicle != null){
					ArrayData.put("vehiclename", vehicle.getDisplayName()+"");
				}else{
					ArrayData.put("vehiclename", "");
				}
				
				Routes route = LoadRoutesDetails.getInstance().retrieve(tripMissDeviation.getRouteId());
				if(route != null){
					ArrayData.put("routename", route.getRouteName()+" ("+route.getEndPoint()+")");
				}else{
					ArrayData.put("routename", "");
				}
				ArrayData.put("expectedtime", DateUtils.adjustToClientTimeInMilliSeconds(localTimeZone, new Date(tripMissDeviation.getExpectedTime().getTime())));
				long occurredatInMilliSeconds = DateUtils.adjustToClientTimeInMilliSeconds(localTimeZone, new Date(tripMissDeviation.getOccurredAt().getTime()));
				ArrayData.put("occurredat", occurredatInMilliSeconds);
				tripMissDeviationData.add((Dataset) ArrayData);
			}
			data.put("vehicle-" + vehicleId, tripMissDeviationData);
		}
		return data;
	}
}