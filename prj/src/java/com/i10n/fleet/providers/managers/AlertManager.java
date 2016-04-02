package com.i10n.fleet.providers.managers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.i10n.db.dao.AlertDaoImpl;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.entity.AlertOrViolation;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadDriverDetails;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.Helper;
import com.i10n.fleet.web.utils.SessionUtils;
import com.ibm.icu.text.DateFormat;


@SuppressWarnings("deprecation")
public class AlertManager implements IAlertManager {
	private static Logger LOG = Logger.getLogger(AlertManager.class);

	@SuppressWarnings("unused")
	private String localTimeZone = null, startdate = null,
	enddate = null;
	DateFormat formatter;
	Date date = null;

	
	@Override
	public IDataset getData(IDataset options) {
		IDataset data = null;

		localTimeZone = options.getValue("localTimeZone");
		startdate = options.getValue("startdate");
		enddate = options.getValue("enddate");

		if (options.getValue("from").equals("dashboard")) {
			data = getParsedData();
		} else if (options.getValue("from").equals("violationsreport")) {
			data = getParsedDataset(new Date(startdate), new Date(enddate));
		}
		return data;
	}

	private IDataset getParsedDataset(Date startDate, Date endDate) {
		IDataset data = new Dataset();
		/*IDataset ArrayData = null;
		ArrayList<Dataset> alertdata = null;
		List<AlertOrViolation> resultset = null;*/ 
//			((AlertDaoImpl) DBManager.getInstance().getDao(DAOEnum.ALERT_DAO)).selectByUserIdBasedOnDuration(SessionUtils.getCurrentlyLoggedInUser().getId(), startDate, endDate);
		/*AlertOrViolation alert = null;
		if (null != resultset) {
			for (int i = 0; i < resultset.size(); i++) {
				ArrayData = new Dataset();
				alertdata = new ArrayList<Dataset>();
				alert = resultset.get(i);
				ArrayData.put("id", alert.getId().getId());
				LiveVehicleStatus liveVehicleStatus = LoadLiveVehicleStatusRecord.retrieveByVehicleId(alert.getVehicleId());
				String vehicleName = LoadVehicleDetails.getInstance().retrieve(liveVehicleStatus.getVehicleId()).getDisplayName();
				ArrayData.put("vehiclename", vehicleName);
				String driverName = LoadDriverDetails.getInstance().retrieve(liveVehicleStatus.getDriverId()).getFirstName();
				ArrayData.put("drivername", driverName);
				String actualTime = StringUtils.adjustToClientTime(localTimeZone,alert.getAlertTime());
				ArrayData.put("time",actualTime );
				// TODO : Commented for new architecture implementation
//				ArrayData.put("alerttype", alert.getType());
				ArrayData.put("location",alert.getAlertLocationText());
				alertdata.add((Dataset) ArrayData);
			}
		}*/
		return data;
	}

	private synchronized IDataset getParsedData() {
		IDataset data = new Dataset();
		IDataset ArrayData = null;
		ArrayList<Dataset> alertData = null;

		List<AlertOrViolation> resultset = ((AlertDaoImpl) DBManager.getInstance().getDao(DAOEnum.ALERT_DAO)).selectAlertsByUserId(SessionUtils.getCurrentlyLoggedInUser().getId());
		AlertOrViolation alert = null;
		if (null != resultset) {
			LOG.debug("ResultSet Size:"+resultset.size());
			for (int i = 0; i < resultset.size(); i++) {
				ArrayData = new Dataset();
				alertData = new ArrayList<Dataset>();
				alert = resultset.get(i);
				ArrayData.put("id", alert.getId().getId());
				LiveVehicleStatus liveVehicleStatus = LoadLiveVehicleStatusRecord.getInstance().retrieveByVehicleId(alert.getVehicleId());
				String vehicleName = LoadVehicleDetails.getInstance().retrieve(liveVehicleStatus.getVehicleId()).getDisplayName();
				ArrayData.put("vehiclename", vehicleName);
				String driverName = LoadDriverDetails.getInstance().retrieve(liveVehicleStatus.getDriverId()).getFirstName();
				ArrayData.put("drivername", driverName);
				ArrayData.put("time",DateUtils.convertJavaDateToJsDate(alert.getAlertTime()));
				ArrayData.put("alerttype", alert.getAlertType().toString());
				ArrayData.put("location",alert.getAlertLocationText());
				alertData.add((Dataset) ArrayData);
				data.put("vehicle-" + alert.getVehicleId(), alertData);
			}
		} else {
			ArrayData = new Dataset();
			alertData = new ArrayList<Dataset>();
			ArrayData.put("id", 1);
			ArrayData.put("vehiclename", "AlertTesting");
			ArrayData.put("drivername", "AlertTesting");
			ArrayData.put("time",new Date());
			ArrayData.put("alerttype", "AlertTesting");
			ArrayData.put("location","AlertTesting");
			alertData.add((Dataset) ArrayData);
			data.put("vehicle-1", alertData);
		}
		Helper.printDataSet(data);
		return data;
	}
}