package com.i10n.fleet.providers.mock.reports;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.VehicleHistoryDaoImp;
import com.i10n.db.entity.DateRange;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.VehicleHistory;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.util.Constants;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * Mock : Data Provider for Activity Report. This class will be removed in
 * future.
 * 
 * @author Prashanth
 * 
 */
public class VehicleHistoryProvider implements IDataProvider {

	private static Logger LOG = Logger.getLogger(VehicleHistoryProvider.class);

	private String enddate;
	private String startdate;
	private Long vehicleId;
	private DateRange dateRange = new DateRange();
	private String localTime,localTimeZone = null;

	/**
	 * @see IDataProvider#getDataset(RequestParameters)
	 */
	public IDataset getDataset(RequestParameters params) {
		IDataset result = new Dataset();
		String vehicleNameRequest = params.getRequestParameter("getvehiclename");
		String vehicleid = params.getRequestParameter("vehicleID");
		String vehicleidVariable = StringUtils.getValueFromKVP(vehicleid);
		vehicleId = Long.parseLong(StringUtils.stripCommas(vehicleidVariable.trim()));
		vehicleId = Long.parseLong(StringUtils.stripCommas(vehicleidVariable.trim()));
		if(vehicleNameRequest != null){
			Vehicle vehicle = LoadVehicleDetails.getInstance().retrieve(vehicleId);
			if(vehicle != null){
				result.put("vehicle.name", vehicle.getDisplayName());
			} else {
				result.put("vehicle.name", vehicleid);
			}
		} else {
			localTime = params.getRequestParameter("localTime");
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			localTimeZone = (String) WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_TIMEZone);
			startdate = params.getRequestParameter("startdate");
			enddate = params.getRequestParameter("enddate");

			result.put("activity", decorateData(this.getData(getDataOptions(params))));
			params.getSession().setAttribute("returnedData",result);
		}
		return result;
	}

	private IDataset getData(IDataset Options) {
		LOG.info("IN Vehicle History Vehicle Data ");
		dateRange=DateUtils.getModeOfReport(localTime, startdate, enddate);
		IDataset activityreportData = new Dataset();
		IDataset position = null;
		//		String vehicleName = LoadVehicleDetails.getInstance().retrieve(vehicleId).getDisplayName();
		List<VehicleHistory> vehicleHistoryList = ((VehicleHistoryDaoImp) DBManager.getInstance().
				getDao(DAOEnum.VEHICLE_HISTORY_DAO)).getHistoryForSelectedVehicleForUIDisplay(vehicleId,dateRange.getStart(), dateRange.getEnd());
		LOG.info("Vehicle History List "+vehicleHistoryList);
		List<IDataset> positionsData = new ArrayList<IDataset>();
		LOG.info("Vehicle History Size "+vehicleHistoryList.size());

		if(vehicleHistoryList.size()!=0){
			for (int i = 0; i < vehicleHistoryList.size() ; i++){
				position = new Dataset();
				position.put("index",i+1);
				position.put("imei", vehicleHistoryList.get(i).getImei());
				position.put("updatedtime",DateUtils.adjustToClientTime(localTimeZone, vehicleHistoryList.get(i).getUpdatedtime()));
				position.put("updatedbyuser", vehicleHistoryList.get(i).getUpdatedbyuser());
				position.put("vehicleattended",vehicleHistoryList.get(i).getVehicleattended());
				position.put("batterychanged", vehicleHistoryList.get(i).isBattrychanged() ? "Yes" : "No");
				position.put("fusechanged", vehicleHistoryList.get(i).isFusechanged() ? "Yes" : "No");
				position.put("status", vehicleHistoryList.get(i).getVehiclestatus());
				LOG.debug("POSITION DATA "+position);
				positionsData.add(position);
			}
		}
		LOG.debug(""+positionsData.size());
		activityreportData.put("vehicle-" + vehicleId+ ".trip-vehicle-" + vehicleId + ".track.positions.", positionsData);
		activityreportData.put("vehicle-" + vehicleId+ ".trip-vehicle-" + vehicleId + ".track.violations","");
		LOG.info("Activity Report Data "+activityreportData);
		return activityreportData;
	}

	/**
	 * Decorates Trip Data for showing complete activity.
	 * 
	 * @param data
	 * @return
	 */
	private IDataset decorateData(IDataset data) {
		LOG.info("In ActivityReportVehicleProvider decorateData starting");
		IDataset result = new Dataset();
		for (Entry<String, Object> vehicleEntry : data.entrySet()) {
			IDataset vehicleResult = new Dataset();
			IDataset vehicleData = (IDataset) vehicleEntry.getValue();
			List<IDataset> positions = new ArrayList<IDataset>();
			List<IDataset> idle = new ArrayList<IDataset>();
			List<IDataset> geo = new ArrayList<IDataset>();
			List<IDataset> overspeed = new ArrayList<IDataset>();
			for (Entry<String, Object> entry : vehicleData.entrySet()) {
				IDataset tripData = (IDataset) entry.getValue();
				addListToDataset(positions, tripData.get("track.positions"));
				addListToDataset(idle, tripData.get("track.violations.idle"));
				addListToDataset(geo, tripData.get("track.violations.geofencing"));
				addListToDataset(overspeed, tripData.get("track.violations.overspeeding"));
			}
			if (!positions.isEmpty()) {
				vehicleResult.put("positions", positions);
			}
			if (!idle.isEmpty()) {
				vehicleResult.put("violations.idle", idle);
			}
			if (!geo.isEmpty()) {
				vehicleResult.put("violations.geofencing", geo);
			}
			if (!overspeed.isEmpty()) {
				vehicleResult.put("violations.overspeeding", overspeed);
			}
			result.put(vehicleEntry.getKey(), vehicleResult);
		}
		LOG.debug("In ActivityReportVehicleProvider decorateData ending");
		return result;
	}

	/**
	 * Adds an object representing a list to a given list in dataset
	 * 
	 * @param data
	 * @param list
	 */
	@SuppressWarnings("unchecked")
	private void addListToDataset(List<IDataset> data, Object list) {
		if (null != list && list instanceof List) {
			data.addAll((List<IDataset>) list);
		}
	}

	/**
	 * Returns the data options for Trip Manager
	 * 
	 * @param params
	 * @return
	 */
	private IDataset getDataOptions(RequestParameters params) {
		LOG.debug("In ActivityReportVehicleProvider getDataOptions starting");
		IDataset options = new Dataset();
		String vehicleID = params.getParameter(RequestParams.vehicleID);
		if (null != vehicleID) {
			options.put("filter.vehicleid", vehicleID);
		}
		String startDate = params.getParameter(RequestParams.startdate);
		String endDate = params.getParameter(RequestParams.enddate);
		options.put("filter.startdate", startDate);
		options.put("filter.enddate", endDate);
		options.put("skip.track.violations.idle", true);
		options.put("skip.track.violations.geofencing", true);
		LOG.debug("In ActivityReportVehicleProvider getDataOptions ending");
		return options;
	}

	/**
	 * @see IDataProvider#getName()
	 */
	public String getName() {
		return "activityreportvehicle";
	}
}
