package com.i10n.fleet.providers.mock.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.StopDeviationDaoImp;
import com.i10n.db.entity.Routes;
import com.i10n.db.entity.StopDeviation;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadRoutesDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.web.utils.SessionUtils;

@SuppressWarnings("deprecation")
public class StopDeviationManager extends AbstractViolationsManager implements IStopDeviationManager {

	private String startdate = null,enddate = null,vehicleidStr = null,localTimeZone = null;
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
		vehicleidStr = options.getValue("vehicleID");
		IDataset data = getParsedData();
		return data;
	}

	private synchronized IDataset getParsedData() {

		IDataset data = new Dataset();
		IDataset ArrayData = null;
		List<StopDeviation> resultset;
		ArrayList<Dataset> stopDevData = null;
		if(vehicleidStr == null){
			resultset = ((StopDeviationDaoImp) DBManager.getInstance().getDao(DAOEnum.STOP_DEVIATIONS)).selectByUserId(SessionUtils.getCurrentlyLoggedInUser().getId(),new Date(startdate),new Date(enddate));
		}else{
			String[] vehIdArray = vehicleidStr.split("-");
			String vId = vehIdArray[1];
			vehId = Long.parseLong(vId);
			resultset = ((StopDeviationDaoImp) DBManager.getInstance().getDao(DAOEnum.STOP_DEVIATIONS)).selectByVehicleId(vehId,new Date(startdate),new Date(enddate));
		}
		StopDeviation sdev = null;
		Long vehicleid = null;
		if (null != resultset) {
			stopDevData = new ArrayList<Dataset>();
			for (int i = resultset.size() - 1; i >= 0; i--) {
				ArrayData = new Dataset();
				sdev = resultset.get(i);
				vehicleid = sdev.getVehicleId();
				ArrayData.put("vehiclename", sdev.getVehicleName());
				ArrayData.put("missedstopname", sdev.getMissedStopName());
				
				Routes route = LoadRoutesDetails.getInstance().retrieve(sdev.getRouteId());
				if(route != null){
					ArrayData.put("routename", route.getRouteName());
				}else{
					ArrayData.put("routename", "");
				}
				ArrayData.put("expectedtime", sdev.getExpectedTime());
				long occurredatInMilliSeconds = DateUtils.adjustToClientTimeInMilliSeconds(localTimeZone, new Date(sdev.getOccurredat().getTime()));
				ArrayData.put("occurredat", occurredatInMilliSeconds);
				stopDevData.add((Dataset) ArrayData);
				data.put("vehicle-" + vehicleid, stopDevData);
			}
		}
		return data;
	}
}