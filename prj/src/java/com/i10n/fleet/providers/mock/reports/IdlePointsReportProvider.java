package com.i10n.fleet.providers.mock.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.IdlePointsDaoImp;
import com.i10n.db.dao.TripDaoImp;
import com.i10n.db.entity.DateRange;
import com.i10n.db.entity.IdlePoints;
import com.i10n.db.entity.Trip;
import com.i10n.db.entity.Vehicle;
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
 * Class to provide necessary data for idlepoints listing on UI
 * @author Dharmaraju V
 *
 */
public class IdlePointsReportProvider implements IDataProvider {
	private IDataset m_data;
	private String startdate;
	private String enddate;
	private DateRange dateRange = new DateRange();
	private String localTime = null,localTimeZone = null;

	/**
	 * @see IDataProvider#getDataset(RequestParameters)
	 */
	@SuppressWarnings("deprecation")
	public IDataset getDataset(RequestParameters params) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		localTimeZone = (String)WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_TIMEZone);

		String vehicleId = params.getParameter(RequestParams.vehicleID);
		String vid[] = vehicleId.split("-");
		String vId = null;
		IDataset idlepointsData = null;
		for (int i = 0; i < vid.length; i++) {
			vId = vid[i];
		}

		vehicleId =StringUtils.stripCommas(vId);
		Long vehicleID = Long.parseLong(vehicleId);
		String vehicleNameRequest = params.getRequestParameter("getvehiclename");
		idlepointsData = new Dataset();
		if(vehicleNameRequest != null){
			Vehicle vehicle = LoadVehicleDetails.getInstance().retrieve(vehicleID);
			if(vehicle != null){
				idlepointsData.put("vehicle.name", vehicle.getDisplayName());
			} else {
				idlepointsData.put("vehicle.name", vehicleID);
			}
		}else{
			localTime = params.getRequestParameter("localTime");
			startdate = params.getRequestParameter("startdate");
			enddate = params.getRequestParameter("enddate");
			Date clientTime = new Date(localTime);
			dateRange=DateUtils.getModeOfReport(localTime,startdate, enddate);
			IDataset data = null;
			List<Trip> trips = ((TripDaoImp) DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).selectTripByVehicleId(vehicleID);
			Vehicle vehicle = LoadVehicleDetails.getInstance().retrieve(vehicleID);
			List<IDataset> idlePointsData = new ArrayList<IDataset>();
			for (int t = 0; t < trips.size(); t++) {
				Trip trip = trips.get(t);

				List<IdlePoints> idlePointResultset = ((IdlePointsDaoImp) DBManager.getInstance().getDao(DAOEnum.IDLE_POINTS_DAO))
				.selectAllIdlePointsBetweenDatesWithLimit(trip.getId().getId(), dateRange.getStart(),dateRange.getEnd());

				for (int j = 0; j < idlePointResultset.size(); j++) {
					IdlePoints idlePoints = idlePointResultset.get(j);

					data = new Dataset();
					String actualendTime = DateUtils.adjustToClientTime(localTimeZone, idlePoints.getEndtime());
					data.put("enddate", actualendTime);
//					if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_IDLE_REPORT_ENABLED"))){
//						Address locationFetch = GeoUtils.fetchNearestLocation(idlePoints.getIdleLocation().getFirstPoint().y, 
//								idlePoints.getIdleLocation().getFirstPoint().x,false);
//						StringBuffer location=StringUtils.formulateAddress(locationFetch, vehicleID, idlePoints.getIdleLocation().
//								getFirstPoint().y,idlePoints.getIdleLocation().getFirstPoint().x);
//						data.put("location", location.toString());
//					}else{
//						StringBuffer location=StringUtils.addressFetchDisabled(vehicleID, idlePoints.getIdleLocation().getFirstPoint().y
//								,idlePoints.getIdleLocation().getFirstPoint().x);
//						data.put("location", location.toString());
//					}
					data.put("location", idlePoints.getIdleLocation().getFirstPoint().y+":"+idlePoints.getIdleLocation().getFirstPoint().x);
					String actualstartTime = DateUtils.adjustToClientTime(localTimeZone, idlePoints.getStarttime());
					data.put("startdate", actualstartTime);
					data.put("lat", idlePoints.getIdleLocation().getFirstPoint().getY());
					data.put("lon", idlePoints.getIdleLocation().getFirstPoint().getX());
					data.put("vehiclename",vehicle.getDisplayName());
					Long startTimeInMillis = DateUtils.adjustToLocalTime(idlePoints.getStarttime(), clientTime).getTime();
					Long endTimeInMillis = DateUtils.adjustToLocalTime(idlePoints.getEndtime(), clientTime).getTime();
					String time=DateUtils.formatTimeDifference(endTimeInMillis,startTimeInMillis);
					data.put("time",time);
					idlePointsData.add((Dataset) data);
				}
			}

			idlepointsData.put("vehicle-" + vehicleID,idlePointsData);
			params.getSession().setAttribute("returnedData", idlepointsData);
		}
		synchronized (this) {
			m_data = idlepointsData;
		}
		return m_data;
	}

	/**
	 * @see IDataProvider#getName()
	 */
	public String getName() {
		return "idlepointsreport";
	}
}