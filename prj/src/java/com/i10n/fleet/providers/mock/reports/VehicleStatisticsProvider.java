package com.i10n.fleet.providers.mock.reports;

import java.text.DecimalFormat;
import java.util.List;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.TrackHistoryDaoImpl;
import com.i10n.db.entity.DateRange;
import com.i10n.db.entity.DriverReport;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.providers.mock.managers.IVehicleManager;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;
import com.i10n.mina.utils.Utils;

/**
 * Mock : Mock Data Provider for Vehicle Statistics widget in Vehicle Report.
 * This calss will be removed in the future.
 * 
 * @author irk
 */
public class VehicleStatisticsProvider implements IDataProvider {

	
	private IVehicleManager m_vehicleManager;
	private IDataset m_dataset;
	private String startdate;
	private String enddate;
	
	private String localTime;

	/**
	 * @see IDataProvider#getDataset(RequestParameters)
	 */
	

	@Override
	public IDataset getDataset(RequestParameters params) {
		IDataset results = new Dataset();
		results.put("vehiclestatistics", this.getData(params));
		return results;
	}
		
	private IDataset getData(RequestParameters params) {
		String vehicleId = params.getParameter(RequestParams.vehicleID);
		String vid[] = vehicleId.split("-");
		String vId = null;
		for (int i = 0; i < vid.length; i++) {
			vId = vid[i];
		}
		vehicleId = StringUtils.stripCommas(vId);
		Long vehicleID = Long.parseLong(vehicleId);
		
		localTime = params.getRequestParameter("localTime");
		startdate = params.getRequestParameter("startdate");
		enddate = params.getRequestParameter("enddate");
		IDataset vehicleData = null;
		//Mode calculation
		DateRange dateRange = new DateRange();
		dateRange=DateUtils.getModeOfReport(localTime,startdate, enddate);
		Vehicle vehicle = LoadVehicleDetails.getInstance().retrieve(Long.parseLong(vehicleId));
		if (null != vehicle) {
			vehicleData = new Dataset();
			vehicleData.put("vehicle-" + vehicle.getId().getId() + ".id","vehicle-" + vehicle.getId().getId());
			vehicleData.put("vehicle-" + vehicle.getId().getId() + ".name",vehicle.getDisplayName());
			Double avgSpeed = 0d;
			Double maxSpeed = 0d;
			List<DriverReport> statisticsResult = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
			.getAvgAndMaxSpeedAndCumulativeDistanceForVehicle(vehicleID,dateRange.getStart(), dateRange.getEnd());

			if (statisticsResult != null){
				DriverReport element = statisticsResult.get(0);
				maxSpeed = element.getMaxspeed();
				avgSpeed = element.getAvgspeed();
			}
			
			float distance = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
					.getCumulativeDistanceForVehicle(vehicleID, dateRange.getStart(), dateRange.getEnd());
			
			DecimalFormat df = new DecimalFormat("0.##");
			vehicleData.put("vehicle-" + vehicle.getId().getId() + ".speed",Utils.doubleForDisplay(avgSpeed)+"");
			vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".distance", df.format(distance)+"");
			vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".maxspeed", Utils.doubleForDisplay(maxSpeed)+"");
			m_dataset = vehicleData;
		}
		return m_dataset;
	}

	/**
	 * @see IDataProvider#getName()
	 */
	@Override
	public String getName() {
		return "vehiclestatisticsreport";
	}

	public IVehicleManager getVehicleManager() {
		return m_vehicleManager;
	}

	public void setVehicleManager(IVehicleManager vehicleManager) {
		this.m_vehicleManager = vehicleManager;
	}
}