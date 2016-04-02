package com.i10n.fleet.providers.mock;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;

import com.i10n.db.entity.BrowserDate;
import com.i10n.db.entity.DateRange;
import com.i10n.db.entity.VehicleReport;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.providers.mock.reports.VehicleReportAction;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.GraphDataHolder;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * 
 * @author Dharmaraju V
 *
 */
@SuppressWarnings("deprecation")
public class FuelGraphDataProvider implements IDataProvider {
	
	private static Logger LOG = Logger.getLogger(FuelGraphDataProvider.class);

	private static final String PROVIDER_NAME = "fuelgraph";
	private String startdate;
	private String enddate;
	private String mode;
	private DateRange dateRange = new DateRange();
	private String localTime;

	@Override
	public IDataset getDataset(RequestParameters params) {

		IDataset generatedGraph = new Dataset();
		if (null != params.getParameter(RequestParams.vehicleID)) {
			String vehi = null;
			String vehicleId = params.getParameter(RequestParams.vehicleID);
			String driverid[] = vehicleId.split("-");
			for (int i = 0; i < driverid.length; i++) {
				vehi = driverid[i];
			}
			vehicleId = StringUtils.stripCommas(vehi);
			Long id = Long.parseLong(vehicleId);

			localTime = params.getRequestParameter("localTime");
			Date clientTime = new Date(localTime);
			BrowserDate browserTime = new BrowserDate();
			browserTime.setLocalTime(clientTime);
			startdate=params.getRequestParameter("startdate");
			enddate = params.getRequestParameter("enddate");

			//Mode calculation
			Date startDate = new Date(startdate);
			Date endDate = new Date(enddate);
			
			LOG.debug("Fuel Graph Report Browser Param start date : "+startDate);
			LOG.debug("Fuel Graph Report Browser Param end date : "+endDate);
			LOG.debug("Fuel Graph Report Browser Param local time : "+clientTime);
						
			long endDateInLong = endDate.getTime();
			long startdateInLong = startDate.getTime();
			long diff = endDateInLong-startdateInLong;
			long diffDays = diff/(24L*60L*60L*1000L);

			if (diff == 86399000) {
				mode = "Today";
			} else if (diffDays == 6) {
				mode = "This Week";
			} else {
				mode = "Custom";
			}

			//depending upon mode get the dateRange
			if(!mode.equalsIgnoreCase("Custom")){
				LOG.debug("Fuel Graph : Custom Mode ");
				dateRange = StringUtils.getDateForMode(clientTime, mode);
			} else {
				LOG.debug("Fuel Graph : Default Mode ");
				dateRange.setStart(startDate);
				dateRange.setEnd(endDate);
			}
			//adjust dateRange to server time
			dateRange.setStart(DateUtils.adjustToServerTime(dateRange.getStart(), clientTime));
			dateRange.setEnd(DateUtils.adjustToServerTime(dateRange.getEnd(),clientTime));
			
			LOG.debug("Fuel Graph adjusted start date : "+dateRange.getStart());
			LOG.debug("Fuel Graph adjusted end date : "+dateRange.getEnd());


			VehicleReportAction vehicleReportAction = new VehicleReportAction();
			VehicleReport fuelGraph = null;
			// For KP client use only this block commenting out the next block and uncommenting this block  
			{
				fuelGraph = vehicleReportAction.fetchFuelReportAsEntity(id, browserTime, dateRange.getStart(), dateRange.getEnd());
				generatedGraph.put("graphurl", fuelGraph.getImageName());
				generatedGraph.put("interactiveGraph","nodata");
			}
			
			// For Non KP Client use this block
//			{
//				if(!isInternetReachable()){
//					LOG.debug("Generating birt graph");
//					fuelGraph = vehicleReportAction.fetchFuelReportAsEntity(id, browserTime, dateRange.getStart(), dateRange.getEnd());
//					generatedGraph.put("graphurl", fuelGraph.getImageName());
//					generatedGraph.put("interactiveGraph","nodata");
//				}else{
//					LOG.debug("Generating interactive graph");
//					fuelGraph = vehicleReportAction.fetchFuelReportAsEntityForInteractiveGraph(id,browserTime, dateRange.getStart(), dateRange.getEnd());
//					generatedGraph.put("graphurl", "/static/img/public_nodata_500x300.png");
//					if(fuelGraph != null){
//						generatedGraph.put("interactiveGraph", fuelGraph.getPutValueForInteractiveGraph());
//					}else{
//						generatedGraph.put("interactiveGraph","nodata");
//					}
//				}
//			}
			generatedGraph.put("type", "Fuel");
			generatedGraph.put("key", params.getParameter(RequestParams.vehicleID));
		}
		return generatedGraph;
	}

	public static boolean isInternetReachable(){
		try {
			//make a URL to a known source
			URL url = new URL("http://www.google.com");

			//open a connection to that source
			HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

			//trying to retrieve data from the source. If there
			//is no connection, this line will fail
			urlConnect.getContent();

		} catch (UnknownHostException e) {
			LOG.error(e);
			return false;
		}
		catch (IOException e) {
			LOG.error(e);
			return false;
		}
		return true;
	}

	@Override
	public String getName() {
		return PROVIDER_NAME;
	}
	

	/**
	 * Computes avg of items in queue
	 * @param data
	 * @return
	 */
	public static float getAverageOfQueue (Queue<Float> data) {
		// empty queue
		if (data.size()==0) {
			return 0;
		}

		float avg=0;
		for (Float val : data) {
			avg+=val;
		}
		return avg / (float) data.size();
	}
	
	/**
	 * Checks if val is between start and end
	 * @param start
	 * @param end
	 * @param val
	 * @return
	 */
	public static boolean isWithin (float start, float end, float val) {
		if (val>=start && val<=end) {
			return true;
		}
		if (val>=end && val<=start) {
			return true;
		}
		return false;
	}

	
	/**
	 * 
	 * The function is designed to eliminate spike from signal data to the 
	 * user
	 * 
	 * @param inputData
	 * @return
	 */
	
	private static final int SPIKE_ELIMINATE_SCAN_LEFT_WINDOW = 5;
	private static final int SPIKE_ELIMINATE_SCAN_RIGHT_WINDOW = 5;
	
	public static ArrayList<GraphDataHolder> eliminateSpikes (ArrayList<GraphDataHolder> inputData) {
		// Empty input data
		if (inputData == null) {
			return new ArrayList<GraphDataHolder>();
		}
		else if (inputData.size()<5) {
			// No point in processing if the data size is so small
			return inputData;
		}
		
		ArrayBlockingQueue<Float> scanLeft = new ArrayBlockingQueue<Float>(SPIKE_ELIMINATE_SCAN_LEFT_WINDOW);
		ArrayBlockingQueue<Float> scanRight = new ArrayBlockingQueue<Float>(SPIKE_ELIMINATE_SCAN_RIGHT_WINDOW);

		// initialized for the first time
		for (int i=0; i<SPIKE_ELIMINATE_SCAN_LEFT_WINDOW; i++) {
			scanLeft.add(inputData.get(0).getValue());
		}
		for (int i=0; i<SPIKE_ELIMINATE_SCAN_RIGHT_WINDOW; i++) {
			scanRight.add(inputData.get(2).getValue());
		}

		// Scan and process
		for (int i=1; i<inputData.size()-1; i++) {
			GraphDataHolder data = inputData.get(i);
			float leftAvg = getAverageOfQueue(scanLeft);
			float rightAvg = getAverageOfQueue(scanRight);
			scanLeft.remove();
			scanLeft.add(data.getValue());
			
			int scanRightIndex = i+SPIKE_ELIMINATE_SCAN_RIGHT_WINDOW;
			if (scanRightIndex>=inputData.size()) {
				scanRightIndex = inputData.size() -1 ;
			}
			
			scanRight.remove();
			scanRight.add(inputData.get(scanRightIndex).getValue());
			
			if (!isWithin(leftAvg, rightAvg, data.getValue())) {
				data.setValue( ( (leftAvg+rightAvg)/2) );
			}
		}
		return inputData;		
	}
	/**
	 * 
	 * The function is designed to render a smooth graph to the 
	 * user
	 * 
	 * @param inputData
	 * @return
	 */
	private static final int MOVING_AVG_WINDOW = 3;

	public static ArrayList<GraphDataHolder>  smoothenGraph (ArrayList<GraphDataHolder> inputData) {
		// Empty input data
		if (inputData == null || inputData.size()==0) {
			return new ArrayList<GraphDataHolder>();
		}
		// initialize
	    double[] movingAverage = new double[MOVING_AVG_WINDOW];
	    for (int i=0; i<MOVING_AVG_WINDOW; i++) {
	    	movingAverage[i]=inputData.get(0).getValue();
	    }
	    
	    // calculate moving average
	    float sum = inputData.get(0).getValue()*MOVING_AVG_WINDOW;
        for (int i = 0; i<inputData.size(); i++) {
            sum -= movingAverage[i % MOVING_AVG_WINDOW];
            movingAverage[i % MOVING_AVG_WINDOW] = inputData.get(i).getValue();
            sum += movingAverage[i % MOVING_AVG_WINDOW];
            inputData.get(i).setValue( sum / (float)MOVING_AVG_WINDOW);
        }
        return inputData;
	}
	
	/**
	 * Testing function
	 * @param args
	 */
	/*public static void main(String[] args) {
		ArrayList<GraphDataHolder> testData = new ArrayList<GraphDataHolder>();

		for (int i=40; i<50; i++) {
			GraphDataHolder data = new GraphDataHolder(i);
			testData.add(data);
		}
		//testData.get(4).setValue(60);
		
		FuelGraphDataProvider fdp = new FuelGraphDataProvider();
		ArrayList<GraphDataHolder> newData = fdp.smoothenGraph(testData);
		
		for (int i=0; i<10; i++) {
			System.out.println(newData.get(i).getValue());
		}
		
	}*/
	
}