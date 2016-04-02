package com.i10n.fleet.providers.mock.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.TimeDeviationDaoImp;
import com.i10n.db.entity.TimeDeviation;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.web.utils.SessionUtils;

public class TimeDeviationManager extends AbstractViolationsManager implements
ITimeDeviationManager {

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

	@SuppressWarnings("deprecation")
	private synchronized IDataset getParsedData() {

		IDataset data = new Dataset();
		IDataset ArrayData = null;
		ArrayList<Dataset> timeDevData = null;
		List<TimeDeviation> resultset;
		if (vehicleid == null) {
			resultset = ((TimeDeviationDaoImp) DBManager.getInstance().getDao(DAOEnum.TIME_DEVIATIONS)).selectByUserId(SessionUtils
							.getCurrentlyLoggedInUser().getId(), new Date(startdate),new Date(enddate));
		} else {
			String[] vehIdArray = vehicleid.split("-");
			String vId = vehIdArray[1];
			vehId = Long.parseLong(vId);
			resultset = ((TimeDeviationDaoImp) DBManager.getInstance().getDao(DAOEnum.TIME_DEVIATIONS)).selectByVehicleId(vehId, new Date(startdate), new Date(enddate));
		}
		TimeDeviation tdev = null;
		Long vehicleid = null;
		if (null != resultset) {
			timeDevData = new ArrayList<Dataset>();
			for (int i = resultset.size() - 1; i >= 0; i--) {
				ArrayData = new Dataset();
				tdev = resultset.get(i);
				vehicleid = tdev.getVehicleId();
				ArrayData.put("vehiclename", tdev.getVehicleName());
				ArrayData.put("stopname", tdev.getStopName());
				ArrayData.put("routename", tdev.getRouteName());
				ArrayData.put("expectedtime", tdev.getExpectedTime());
				String actualTimeStr = DateUtils.adjustToClientTime(localTimeZone, new Date(tdev.getActualTime().getTime()));
				ArrayData.put("actualtime", actualTimeStr);
				String timeArray[] = actualTimeStr.split(" ");
				String time[] = timeArray[4].split(":");
				long expHrs = tdev.getExpectedTime().getHours();
				long actHrs = Long.parseLong(time[0].trim());
				long diffHrs = actHrs - expHrs;
				long expMins = tdev.getExpectedTime().getMinutes();
				long actMins = Long.parseLong(time[1].trim());
				long diffMins = actMins - expMins;
				if (diffHrs > 0) {
					diffMins += diffHrs * 60;
				}
				ArrayData.put("deviation", diffMins);
				timeDevData.add((Dataset) ArrayData);
				data.put("vehicle-" + vehicleid, timeDevData);
			}
		}
		return data;
	}
}