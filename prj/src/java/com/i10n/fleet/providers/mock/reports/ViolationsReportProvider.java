package com.i10n.fleet.providers.mock.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import com.i10n.db.dao.AlertDaoImpl;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.entity.AlertOrViolation;
import com.i10n.db.entity.DateRange;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadDriverDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.providers.managers.IAlertManager;
import com.i10n.fleet.providers.mock.AbstractGroupedDataProvider;
import com.i10n.fleet.providers.mock.managers.GroupManager;
import com.i10n.fleet.providers.mock.managers.ILiveVehicleStatusManager;
import com.i10n.fleet.providers.mock.managers.LiveVehicleStatusManager;
import com.i10n.fleet.util.Constants;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * Mock : Mock Data Provider for Violation Report . This class will be removed
 * in future.
 * 
 * @author Sabarish
 * 
 */
public class ViolationsReportProvider extends AbstractGroupedDataProvider implements IDataProvider {
	private IAlertManager m_alertManager;
	private GroupManager m_groupManager;
	private ILiveVehicleStatusManager m_liveVehicleStatus = new LiveVehicleStatusManager();
	private String startdate;
	private String enddate;
	private DateRange dateRange = new DateRange();
	private String localTime = null,localTimeZone = null;

	/**
	 * @see IDataProvider#getDataset(RequestParameters)
	 */
	@SuppressWarnings("deprecation")
	public IDataset getDataset(RequestParameters params) {
		startdate = params.getRequestParameter("startdate");
		enddate = params.getRequestParameter("enddate");

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		localTime = (String)WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_TIME);
		localTimeZone = (String)WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_TIMEZone);

		String mode="";
		Date clientTime = new Date(localTime);
		// Mode calculation
		Date startDate = new Date(startdate);
		Date endDate = new Date(enddate);
		long endDateInLong = endDate.getTime();
		long startdateInLong = startDate.getTime();
		long diff = endDateInLong - startdateInLong;
		long diffDays = diff / (24 * 60 * 60 * 1000);

		if (diff == 86399000) {
			mode = "Today";
		} else if (diffDays == 6) {
			mode = "This Week";
		} else {
			mode = "Custom";
		}

		// depending upon mode get the dateRange
		if (!mode.equalsIgnoreCase("Custom")) {
			dateRange = StringUtils.getDateForModeViolationsPage(clientTime, mode);
		} else {
			dateRange.setStart(startDate);
			dateRange.setEnd(endDate);
		}
		// adjust dateRange to server time
		IDataset vehicleStatusData = m_liveVehicleStatus.getData(getDataOptions(params));
		IDataset alertData = this.getAlertData(dateRange.getStart(), dateRange.getEnd());
		IDataset result = mergeData(vehicleStatusData,alertData);
		params.getSession().setAttribute("returnedData",result);
		return result;
		
	}

	private IDataset getAlertData(Date start, Date end) {
		IDataset data = new Dataset();
		IDataset ArrayData = null;
		ArrayList<Dataset> alertdata = null;

		List<AlertOrViolation> alertOrViolationList = ((AlertDaoImpl) DBManager.getInstance().getDao(DAOEnum.ALERT_DAO)).
				selectByUserIdAndDurationWithLimit(SessionUtils.getCurrentlyLoggedInUser().getId(), start, end);
		AlertOrViolation alert = null;
		if (alertOrViolationList.size() != 0) {
			for (int i = 0; i < alertOrViolationList.size(); i++) {
				ArrayData = new Dataset();
				alertdata = new ArrayList<Dataset>();
				alert = alertOrViolationList.get(i);
				ArrayData.put("refid", alert.getId().getId()+"");
				ArrayData.put("vehiclename", LoadVehicleDetails.getInstance().retrieve(alert.getVehicleId()).getDisplayName()+"");
				String driverName = LoadDriverDetails.getInstance().retrieve(alert.getDriverId()).getFirstName();
				ArrayData.put("drivername", driverName+"");
				String actualstartTime = DateUtils.adjustToClientTime(localTimeZone, alert.getAlertTime());
				ArrayData.put("time",actualstartTime);
//				if(alert.getAlertLocationReferenceId() == 0){
//					if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_VIOLATIONS_ENABLED"))){
//						Address address = GeoUtils.fetchNearestLocation(alert.getAlertLocation().getFirstPoint().y,
//								alert.getAlertLocation().getFirstPoint().x, false);
//						StringBuffer location=StringUtils.formulateAddress(address, alert.getVehicleId(), 
//								alert.getAlertLocation().getFirstPoint().y, alert.getAlertLocation().getFirstPoint().x);
//						if(address != null){
//							alert.setAlertLocationReferenceId(address.getId());
//							alert.setAlertLocationText(location.toString());
//							((AlertDaoImpl) DBManager.getInstance().getDao(DAOEnum.ALERT_DAO)).updateAlertLocation(alert);
//						}
//					}
				ArrayData.put("alertlocation",alert.getAlertLocation().getFirstPoint().y+":"+alert.getAlertLocation().getFirstPoint().x /*alert.getAlertLocationText()*/);
				ArrayData.put("alerttype", alert.getAlertType().toString()+"");
				ArrayData.put("alerttypevalue", alert.getAlertTypeValue()+"");
				alertdata.add((Dataset) ArrayData);
				data.put("vehicle-"+alert.getId().getId(), alertdata);
			}
		}
		return data;
	}



	/**
	 * Returns Data Options for Violation Data Managers.
	 * 
	 * @param params
	 * @return
	 */
	private IDataset getDataOptions(RequestParameters params) {
		IDataset options = new Dataset();
		options.put("filter.startdate", dateRange.getStart());
		options.put("filter.enddate", dateRange.getEnd());
		options.put("startdate", startdate);
		options.put("enddate", enddate);
		options.put("localTime", localTime);
		options.put("localTimeZone", localTimeZone);
		options.put("from", "violationsreport");
		return options;
	}

	/**
	 * Merges data of all the violations into one dataset.
	 * 
	 * @param geoFenceData
	 * @param overSpeedData
	 * @param chargerDCData
	 * @return
	 */
	private IDataset mergeData(IDataset vehicleStatusData,IDataset alertData) {
		IDataset result = new Dataset();
		result.put("vehiclestatus", expandData(vehicleStatusData));
		result.put("alertstatus", expandData(alertData));
		return result;
	}

	/**
	 * Decorates and adds Group/Vehicles info into the dataset
	 * 
	 * @param data
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private IDataset expandData(IDataset data) {
		IDataset grpData = m_groupManager.getData(null).copy();
		List<IDataset> list = new ArrayList<IDataset>();
		for (Entry<String, Object> entry : data.entrySet()) {
			list.addAll((List<IDataset>) entry.getValue());
		}
		for (IDataset listData : list) {
			String grpID = listData.getValue("groupid");
			if(grpID == null){
				grpID="nogroup";
			}
			List<IDataset> grpList = null;
			if (null == grpData.get(grpID + "." + "violations")) {
				grpList = new ArrayList<IDataset>();
				grpData.put(grpID + "." + "violations", grpList);
			} else {
				grpList = (List<IDataset>) grpData.get(grpID + "."+ "violations");
			}
			IDataset refinedSet = listData.copy();
			refinedSet.put("id", "violation-" + list.indexOf(listData));
			refinedSet.remove("groupid");
			grpList.add(refinedSet);
		}
		return grpData;
	}

	/**
	 * @see IDataProvider#getName()
	 */
	public String getName() {
		return "violationsreport";
	}

	public IAlertManager getAlertManager() {
		return m_alertManager;
	}
	/**
	 * Sets the alert Manager
	 * 
	 * @param manager
	 */
	public void setAlertManager(IAlertManager alertManager) {
		m_alertManager = alertManager;
	}
	/**
	 * Sets the Group Manager
	 * 
	 * @param manager
	 */
	public void setGroupManager(GroupManager manager) {
		m_groupManager = manager;
	}

	/**
	 * Returns the Group Manager
	 * 
	 * @return
	 */
	public GroupManager getGroupManager() {
		return m_groupManager;
	}

} 