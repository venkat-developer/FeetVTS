package com.i10n.fleet.providers.mock.reports;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.i10n.db.entity.BrowserDate;
import com.i10n.db.entity.DateRange;
import com.i10n.db.entity.VehicleReport;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;
/**
 * 
 * @update dharmaraju
 *
 */
@SuppressWarnings("deprecation")
public class SpeedGraphReportProvider implements IDataProvider {

	private static Logger LOG = Logger.getLogger(SpeedGraphReportProvider.class);
	private String startdate;
	private String enddate;
	private DateRange dateRange = new DateRange();
	private String localTime;


	public IDataset getDataset(RequestParameters params) {
		LOG.debug("In  SpeedGraphReportProvider getDataSet starting");
		IDataset result = new Dataset();
		if (null != params.getParameter(RequestParams.vehicleID)) {
			String dri = null;
			String did = params.getParameter(RequestParams.vehicleID);
			String driverid[] = did.split("-");
			for (int i = 0; i < driverid.length; i++) {
				dri = driverid[i];
			}
			did = StringUtils.stripCommas(dri);
			Long id = Long.parseLong(did);

			localTime = params.getRequestParameter("localTime");
			Date clientTime = new Date(localTime);
			BrowserDate browserTime = new BrowserDate();
			browserTime.setLocalTime(clientTime);

			startdate = params.getRequestParameter("startdate");
			enddate = params.getRequestParameter("enddate");
			dateRange=DateUtils.getModeOfReport(localTime,startdate,enddate);
			VehicleReportAction vehicleReportAction = new VehicleReportAction();
			VehicleReport speedGraph = null;
			
			// For KP client use only this block commenting out the next block and uncommenting this block
			{
				speedGraph = vehicleReportAction.fetchSpeedReportAsEntity(id, browserTime, dateRange.getStart(), dateRange.getEnd());
				result.put("graphurl", speedGraph.getImageName());
				result.put("interactiveGraph", "nodata");
			}
			
			// For Non KP Client use this block
//			{
//				if(!isInternetReachable()){
//					speedGraph = vehicleReportAction.fetchSpeedReportAsEntity(id, browserTime, dateRange.getStart(), dateRange.getEnd());
//					result.put("graphurl", speedGraph.getImageName());
//					result.put("interactiveGraph", "nodata");
//				}else{
//					speedGraph = vehicleReportAction.fetchSpeedReportAsEntityForInteractiveGraph(id, browserTime, dateRange.getStart(), dateRange.getEnd());
//					result.put("graphurl",  "/static/img/public_nodata_500x300.png");
//					if(speedGraph != null){
//						result.put("interactiveGraph", speedGraph.getPutValueForInteractiveGraph());
//					}else{
//						result.put("interactiveGraph", "");
//					}
//				}
//			}
			result.put("type", "Speed");
			result.put("key", params.getParameter(RequestParams.vehicleID));
		}
		LOG.debug("In  SpeedGraphReportProvider getDataSet ending");
		return result;
	}

	public static boolean isInternetReachable() {
		try {
			//make a URL to a known source
			URL url = new URL("http://www.google.com");

			//open a connection to that source
			HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

			//trying to retrieve data from the source. If there
			//is no connection, this line will fail
			urlConnect.getContent();

		} catch (UnknownHostException e) {
			LOG.error("",e);
			return false;
		}
		catch (IOException e) {
			LOG.error("",e);
			return false;
		}
		return true;
	}

	public String getName() {
		return "speedgraph";
	}

}