package com.i10n.fleet.providers.mock.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.RouteDeviationDaoImp;
import com.i10n.db.entity.RouteDeviation;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.web.utils.SessionUtils;

@SuppressWarnings("deprecation")
public class RouteDeviationManager extends AbstractViolationsManager implements IRouteDeviationManager {

	private String startdate = null, enddate = null, vehicleid = null, localTimeZone = null;
	private long vehId;

	@Override
	protected String getDocumentName() {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataset getData(IDataset options) {
		localTimeZone = options.getValue("localTimeZone");
		startdate = options.getValue("startdate");
		enddate = options.getValue("enddate");
		vehicleid = options.getValue("vehicleID");
		IDataset data = getParsedData();
		return data;
	}

	private synchronized IDataset getParsedData() {
		IDataset data = new Dataset();
		IDataset ArrayData = null;
		List<RouteDeviation> resultset;
		ArrayList<Dataset> routeDevData = null;
		if (vehicleid == null) {
			resultset = ((RouteDeviationDaoImp) DBManager.getInstance().getDao(DAOEnum.ROUTE_DEVIATIONS)).selectByUserId(SessionUtils.getCurrentlyLoggedInUser().getId(), new Date(startdate),new Date(enddate));
		} else {
			String[] vehIdArray = vehicleid.split("-");
			String vId = vehIdArray[1];
			vehId = Long.parseLong(vId);
			resultset = ((RouteDeviationDaoImp) DBManager.getInstance().getDao(DAOEnum.ROUTE_DEVIATIONS)).selectByVehicleId(vehId,new Date(startdate), new Date(enddate));
		}
		RouteDeviation rdev = null;
		Long vehiId = null;
		if (null != resultset) {
			routeDevData = new ArrayList<Dataset>();
			for (int i = resultset.size() - 1; i >= 0; i--) {
				ArrayData = new Dataset();
				rdev = resultset.get(i);
				vehiId = rdev.getVehicleId();
				ArrayData.put("vehiclename", rdev.getVehicleName());
				ArrayData.put("stopname", rdev.getStopName());
				ArrayData.put("routename", rdev.getRouteName());
				ArrayData.put("estimateddistance", rdev.getEstimatedDistance());
				ArrayData.put("actualdistance", rdev.getActualDistance());
				long occurredatInMilliSeconds = DateUtils.adjustToClientTimeInMilliSeconds(localTimeZone, new Date(rdev.getOccurredAt().getTime()));
				ArrayData.put("occurredat", occurredatInMilliSeconds);
				float dev = (rdev.getActualDistance() - rdev.getEstimatedDistance());
				ArrayData.put("deviation", dev);
				routeDevData.add((Dataset) ArrayData);
				data.put("vehicle-" + vehiId, routeDevData);
			}
		}
		return data;
	}
}