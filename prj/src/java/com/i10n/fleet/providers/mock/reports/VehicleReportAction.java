package com.i10n.fleet.providers.mock.reports;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.servlet.ServletUtilities;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.TrackHistoryDaoImpl;
import com.i10n.db.entity.BrowserDate;
import com.i10n.db.entity.TrackHistory;
import com.i10n.db.entity.VehicleReport;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.providers.mock.FuelGraphDataProvider;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.GraphDataHolder;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * @update Hemant
 * @author joshua
 * 
 */

public class VehicleReportAction {
	
	private static Logger LOG = Logger.getLogger(VehicleReportAction.class);
	public VehicleReport fetchSpeedReportAsEntity(Long vehicleId,BrowserDate browserTime,
			Date startdate, Date enddate) {
		VehicleReport vehicle = new VehicleReport();
		
		String filename = null;
		XYSeries dataSeries = new XYSeries("Speed");

		List<TrackHistory> speeds = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
				.getVehicleSpeedList(vehicleId, startdate, enddate);
		if (speeds.size() != 0) {
			float trackHistroySpeedValue = 0;
			for (int i = 0; i < speeds.size(); i++) {
				TrackHistory tHistory = speeds.get(i);
				Date date = DateUtils.adjustToLocalTime(tHistory.getOccurredat(), browserTime.getLocalTime());
				trackHistroySpeedValue = tHistory.getSpeed();
				dataSeries.add(date.getTime(), trackHistroySpeedValue);
			}
			ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
			try {
				XYSeriesCollection xyDataset = new XYSeriesCollection(
						dataSeries);
				// Create tooltip and URL generators

				StandardXYToolTipGenerator ttg = new StandardXYToolTipGenerator();
				// Create the chart object
				ValueAxis timeAxis = new DateAxis("Time");
				NumberAxis valueAxis = new NumberAxis("Speed ( Kmph)");
				valueAxis.setAutoRangeIncludesZero(false);
				// override default
				StandardXYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES+ StandardXYItemRenderer.SHAPES, ttg);
				renderer.setShapesFilled(true);
				XYPlot plot = new XYPlot(xyDataset, timeAxis, valueAxis,renderer);
				JFreeChart chart = new JFreeChart("",JFreeChart.DEFAULT_TITLE_FONT, plot, false);
				chart.setBackgroundPaint(Color.WHITE);
				chart.getTitle().setPaint(Color.GREEN);
				// Write the chart image to the temporary directory
				filename = ServletUtilities.saveChartAsPNG(chart, 800, 400,info, SessionUtils.GetCurrentSession());
			} catch (Exception graphex) {
				filename = "public_error_500x300.png";
			}

			String imageMap = ChartUtilities.getImageMap(filename, info);
			vehicle.setImageName("/fleet/chart/DisplayChart?filename="+ filename);
			vehicle.setImageMap(imageMap);
		} else {
			vehicle.setImageName("/static/img/public_nodata_500x300.png");
		}
		return vehicle;
	}

	
	public VehicleReport fetchFuelReportAsEntity(Long vehicleID,BrowserDate browserTime,Date startdate, Date enddate) {
		VehicleReport vehicle = new VehicleReport();
		String filename = null;

		// Create and populate an XYSeries Collection
		XYSeries dataSeries = new XYSeries("Fuel");

		List<TrackHistory> fuels = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO)).getVehicleFuelList(vehicleID, startdate, enddate);
		if (fuels.size() != 0) {
			float trackHistroyFuelValue = 0;
			for (int i = 0; i < fuels.size(); i++) {
				TrackHistory tHistory = fuels.get(i);
				Date date = DateUtils.adjustToLocalTime(tHistory.getOccurredat(), browserTime.getLocalTime());
				trackHistroyFuelValue = tHistory.getFuel();
				dataSeries.add(date.getTime(), trackHistroyFuelValue);
			}
			ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
			try {
				XYSeriesCollection xyDataset = new XYSeriesCollection(dataSeries);
				// Create tooltip and URL generators
				StandardXYToolTipGenerator ttg = new StandardXYToolTipGenerator();
				// Create the chart object
				ValueAxis timeAxis = new DateAxis("Time");
				NumberAxis valueAxis = new NumberAxis("Fuel ( Kmph)");
				valueAxis.setAutoRangeIncludesZero(false);
				// override default
				StandardXYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES + StandardXYItemRenderer.SHAPES, ttg);
				renderer.setShapesFilled(true);
				XYPlot plot = new XYPlot(xyDataset, timeAxis, valueAxis, renderer);
				JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, false);

				chart.setBackgroundPaint(Color.WHITE);
				chart.getTitle().setPaint(Color.GREEN);
				// Write the chart image to the temporary directory
				filename = ServletUtilities.saveChartAsPNG(chart, 800, 400,info, SessionUtils.GetCurrentSession());
			} catch (Exception graphex) {
				filename = "public_error_500x300.png";
			}
			String imageMap = ChartUtilities.getImageMap(filename, info);
			vehicle.setImageName("/fleet/chart/DisplayChart?filename="+ filename);
			vehicle.setImageMap(imageMap);
		} else {
			vehicle.setImageName("/static/img/public_nodata_500x300.png");
		}
		return vehicle;
	}
	
	public VehicleReport fetchSpeedReportAsEntityForInteractiveGraph(Long vehicleID,BrowserDate browserTime, Date startdate, Date enddate) {
		VehicleReport vehicleReport = null;
		List<TrackHistory> speedList = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO)).getVehicleSpeedList(vehicleID, startdate, enddate);

		if(speedList.size() != 0){
			ArrayList<String> speedData = new ArrayList<String>();
			ArrayList<GraphDataHolder> speedRawData = new ArrayList<GraphDataHolder>();
			
			for (int i = 0; i < speedList.size(); i++) {
				TrackHistory tHistory = speedList.get(i);
				Date date = DateUtils.adjustToLocalTime(tHistory.getOccurredat(), browserTime.getLocalTime());
				speedRawData.add((new GraphDataHolder((int) tHistory.getSpeed(), date)));
			}
			
			LOG.debug("Printing graph values before eliminating spikes");
			printGraphValues(speedRawData);
			FuelGraphDataProvider.eliminateSpikes(speedRawData);
			LOG.debug("Printing graph values after eliminating spikes ");
			printGraphValues(speedRawData);

			for (int i = 0; i < speedList.size(); i++) {
				speedData.add(speedRawData.get(i).toString());
			}
			
			vehicleReport = new VehicleReport();
			vehicleReport.setPutValueForInteractiveGraph(speedData);
		}

		return vehicleReport;
	}
	
	public VehicleReport fetchFuelReportAsEntityForInteractiveGraph(Long vehicleID,BrowserDate browserTime, Date startdate, Date enddate) {
		VehicleReport vehicleReport = null;
		List<TrackHistory> fuels = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO)).getVehicleFuelList(vehicleID, startdate, enddate);

		if(fuels.size() != 0){
			ArrayList<GraphDataHolder> fuelRawData = new ArrayList<GraphDataHolder>();
			ArrayList<String> fuelData = new ArrayList<String>();
					
			for (int i = 0; i < fuels.size(); i++) {
				TrackHistory tHistory = fuels.get(i);
				Date date = DateUtils.adjustToLocalTime(tHistory.getOccurredat(), browserTime.getLocalTime());
				fuelRawData.add(new GraphDataHolder(tHistory.getFuel(), date));
			}

			FuelGraphDataProvider.eliminateSpikes(fuelRawData);
			FuelGraphDataProvider.smoothenGraph(fuelRawData);
			
			for (int i = 0; i < fuels.size(); i++) {
				fuelData.add(fuelRawData.get(i).toString());
			}
			
			vehicleReport = new VehicleReport();
			vehicleReport.setPutValueForInteractiveGraph(fuelData);
		}
		
		return vehicleReport;
	}
	
	private void printGraphValues (ArrayList<GraphDataHolder> inputData) {
		int i = 1;
		for (GraphDataHolder data : inputData) {
			LOG.debug("GraphValue "+(i++)+" : "+data.toString());
		}
	}
	

}
