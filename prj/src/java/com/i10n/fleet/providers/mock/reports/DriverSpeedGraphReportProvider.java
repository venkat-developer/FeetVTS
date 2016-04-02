package com.i10n.fleet.providers.mock.reports;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.TrackHistoryDaoImpl;
import com.i10n.db.entity.BrowserDate;
import com.i10n.db.entity.DateRange;
import com.i10n.db.entity.Driver;
import com.i10n.db.entity.DriverReport;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadDriverDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.providers.mock.managers.IDriverManager;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;
import com.i10n.mina.utils.Utils;

/**
 * Data Provider for Driver Report.
 * @author Sabarish
 */
@SuppressWarnings("deprecation")
public class DriverSpeedGraphReportProvider implements IDataProvider {
	private static Logger LOG = Logger.getLogger(DriverSpeedGraphReportProvider.class);
	private IDriverManager m_driverManager;
	private String startdate;
	private String enddate;
	private DateRange dateRange = new DateRange();
	private String localTime;

	/**
	 * @see IDataProvider#getDataset(RequestParameters)
	 * This method will return the data with the chart image
	 * to the view controller
	 */
	
	public IDataset getDataset(RequestParameters params) {
		LOG.debug("In DriverSpeedGraphReportProvider getDataset starting");
		IDataset result = new Dataset();
		BrowserDate browserTime = new BrowserDate();
		if (null != params.getParameter(RequestParams.driverID)) {
			String dri = null;
			String did = params.getParameter(RequestParams.driverID);
			dri=StringUtils.getValueFromKVP(did);
			did = StringUtils.stripCommas(dri);
			Long driverId = Long.parseLong(did);
			localTime = params.getRequestParameter("localTime");
			startdate = params.getRequestParameter("startdate");
			enddate = params.getRequestParameter("enddate");
			Date clientTime = new Date(localTime);
			browserTime.setLocalTime(clientTime);
					
			DriverReportAction dr = new DriverReportAction();
			dateRange=DateUtils.getModeOfReport(localTime,startdate, enddate);
			List<DriverReport> statisticsResult = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
					.getAvgAndMaxSpeedAndCumulativeDistanceForDriver(driverId, dateRange.getStart(), dateRange.getEnd());
			Double avgSpeed = 0d;
			Double maxSpeed = 0d;
			if (statisticsResult != null){
				DriverReport element = statisticsResult.get(0);
				maxSpeed = element.getMaxspeed();
				avgSpeed = element.getAvgspeed();
			}

			float distance = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
					.getCumulativeDistanceForDriver(driverId, dateRange.getStart(), dateRange.getEnd());

			IDataset driverData = new Dataset();
			Driver driver = LoadDriverDetails.getInstance().retrieve(driverId);
			if(driver == null){
				LOG.error("Driver : "+driverId+"is not cached ");
				return new Dataset();
			}
			driverData.put("driver-" + driver.getId().getId() + ".id","driver-" + driver.getId().getId());
			driverData.put("driver-" + driver.getId().getId() + ".name",driver.getFirstName());
			driverData.put("driver-" + driver.getId().getId() + ".status","idle");
			driverData.put("driver-" + driver.getId().getId()+ ".firstname", driver.getFirstName());
			driverData.put("driver-" + driver.getId().getId() + ".lastname",driver.getLastName());
			driverData.put("driver-" + driver.getId().getId() + ".license",driver.getLicenseno());
			driverData.put("driver-" + driver.getId().getId() + ".maxspeed", Utils.doubleForDisplay(maxSpeed)+"");
			driverData.put("driver-" + driver.getId().getId() + ".avgspeed", Utils.doubleForDisplay(avgSpeed)+"");
			driverData.put("driver-" + driver.getId().getId() + ".distance", Utils.doubleForDisplay(distance)+"");
			DriverReport speedGraph = null;
			
			// For KP client use only this block commenting out the next block and uncommenting this block
			{
				speedGraph = dr.fetchDriverReportAsEntity(driverId, browserTime, dateRange.getStart(), dateRange.getEnd());
				result.put("graphurl", speedGraph.getImageName());
				result.put("interactiveGraph", "nodata");
			}
			
			// For Non KP Client use this block
//			{
//				if(!SpeedGraphReportProvider.isInternetReachable()){
//					speedGraph = dr.fetchDriverReportAsEntity(driverId, browserTime, dateRange.getStart(), dateRange.getEnd());
//					result.put("graphurl", speedGraph.getImageName());
//					result.put("interactiveGraph", "nodata");
//				}else{
//					speedGraph = dr.fetchSpeedReportAsEntityForInteractiveGraph(driverId, browserTime, dateRange.getStart(), dateRange.getEnd());
//					result.put("graphurl",  "/static/img/public_nodata_500x300.png");
//					if(speedGraph != null){
//						result.put("interactiveGraph", speedGraph.getPutValueForInteractiveGraph());
//					}else{
//						result.put("interactiveGraph", "");
//					}
//				}
//			}
			
			result.put("key", driver.getFirstName());
			result.put("type", "Driver");
			result.put("drivers", driverData);
		}
		LOG.debug("In DriverReportProvider getDataset ending");
		return result;
	}


	/**
	 * @see IDataProvider#getName()
	 */
	public String getName() {
		return "driverspeedgraphreport";
	}

	/**
	 * Returns the Driver Manager
	 * 
	 * @return
	 */
	public IDriverManager getDriverManager() {
		return m_driverManager;
	}

	/**
	 * Sets the Driver Manager
	 * 
	 * @param manager
	 */
	public void setDriverManager(IDriverManager manager) {
		m_driverManager = manager;
	}

}