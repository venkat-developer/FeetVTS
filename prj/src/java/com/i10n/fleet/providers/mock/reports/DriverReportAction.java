package com.i10n.fleet.providers.mock.reports;

import java.awt.Color;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import com.i10n.db.entity.DriverReport;
import com.i10n.db.entity.TrackHistory;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.providers.mock.FuelGraphDataProvider;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.GraphDataHolder;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * @author joshua
 *
 */
public class DriverReportAction {

	/**
	 * This function used to draw the speed chart for every driver.
	 * It will return the image as driver entity which is acessed by the 
	 * DriverReportprovide Class
	 * @param driverID
	 * @param browserTime
	 * @param startdate
	 * @param enddate
	 * @return
	 */
	public DriverReport fetchDriverReportAsEntity(Long driverID,BrowserDate browserTime, Date startdate,Date enddate) {
		DriverReport driver = new DriverReport();

		String filename = null;
		// Create and populate an XYSeries Collection
		XYSeries dataSeries = new XYSeries("Speed");
		TrackHistoryDaoImpl trackHistoryDao = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO));
		List<TrackHistory> speeds = trackHistoryDao.getDriverSpeedList(driverID, startdate, enddate);
		if (speeds.size() != 0) {
			for(int i=0;i<speeds.size();i++){
				TrackHistory tHistory = speeds.get(i);
				dataSeries.add(DateUtils.adjustToLocalTime(tHistory.getOccurredat(),
						browserTime.getLocalTime()).getTime(), tHistory.getSpeed());
			}

			ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
			try {
				XYSeriesCollection xyDataset = new XYSeriesCollection(dataSeries);
				// Create tooltip and URL generators
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy  HH:mm:ss", Locale.UK);
				StandardXYToolTipGenerator ttg = new StandardXYToolTipGenerator(StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
						sdf, NumberFormat.getInstance());
				// Create the chart object
				ValueAxis timeAxis = new DateAxis("Time");
				NumberAxis valueAxis = new NumberAxis("Speed(Kmph)");
				valueAxis.setAutoRangeIncludesZero(false);
				// override default
				StandardXYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.LINES + StandardXYItemRenderer.SHAPES, ttg);
				renderer.setShapesFilled(true);
				XYPlot plot = new XYPlot(xyDataset, timeAxis, valueAxis, renderer);
				JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, plot, false);

				chart.setBackgroundPaint(Color.WHITE);
				chart.getTitle().setPaint(Color.MAGENTA); 
				// Write the chart image to the temporary directory

				filename = ServletUtilities.saveChartAsPNG(chart, 800, 400,info, SessionUtils.GetCurrentSession());

			} catch (Exception graphex) {
				filename = "public_error_500x300.png";
			}
			String imageMap = ChartUtilities.getImageMap(filename, info);
			driver.setImageName("/fleet/chart/DisplayChart?filename="+ filename);
			driver.setImageMap(imageMap);
		} else {
			driver.setImageName("/static/img/public_nodata_500x300.png");
		}
		return driver;
	}


	public DriverReport fetchSpeedReportAsEntityForInteractiveGraph(Long driverID,BrowserDate browserTime, Date startdate, Date enddate) {
		DriverReport driverReport = null;
		List<TrackHistory> speedList = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO)).getDriverSpeedList(driverID, startdate, enddate);

		if(speedList.size() != 0){
			ArrayList<String> speedData = new ArrayList<String>();
			ArrayList<GraphDataHolder> speedRawData = new ArrayList<GraphDataHolder>();

			for (int i = 0; i < speedList.size(); i++) {
				TrackHistory tHistory = speedList.get(i);
				Date date = DateUtils.adjustToLocalTime(tHistory.getOccurredat(), browserTime.getLocalTime());
				speedRawData.add((new GraphDataHolder((int) tHistory.getSpeed(), date)));
			}
			FuelGraphDataProvider.eliminateSpikes(speedRawData);
			for (int i = 0; i < speedList.size(); i++) {
				speedData.add(speedRawData.get(i).toString());
			}

			driverReport = new DriverReport();
			driverReport.setPutValueForInteractiveGraph(speedData);
		}

		return driverReport;
	}
}