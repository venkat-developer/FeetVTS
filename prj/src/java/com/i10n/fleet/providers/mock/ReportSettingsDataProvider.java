package com.i10n.fleet.providers.mock;

import java.util.List;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.MailinglistReportDaoImp;
import com.i10n.db.dao.VehicleDaoImpl;
import com.i10n.db.entity.MailingListReport;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * Mock : Mock Data Provider for Report Settings. This class will be removed in
 * future.
 *
 * @author Sabarish
 *
 */
public class ReportSettingsDataProvider implements IDataProvider {

	private static final String SCHEDULES[] = { "Daily", "Weekly", "Monthly" };
	//    private static final int MIN_RECORDS = 5;
	private IDataset m_dataset = null;

	/**
	 * @see IDataProvider#getDataset(RequestParameters)
	 */
	public IDataset getDataset(RequestParameters params) {

		m_dataset = addRecord(params);
		return m_dataset;
	}

	/**
	 * Adds a random record into the dataset
	 *
	 * @param data
	 * @return
	 */
	private IDataset addRecord(RequestParameters params) {
		Long recordID = null;
		IDataset data = new Dataset();		
		IDataset assignedDataset = null;
		IDataset vacantDataset = null;

		String dataView = params.getRequestParameter("dataView");
		MailingListReport reportMail = null;

		if (dataView == null ) {
			List<MailingListReport> report = ((MailinglistReportDaoImp) DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_REPORT_DAO)).
			selectAllOwned();
			if(report.size() != 0){
				for (int i = 0; i < report.size(); i++) {
					IDataset record = new Dataset();
					reportMail = report.get(i);
					record.put("id", reportMail.getId().getId());
					record.put("name", reportMail.getName());
					recordID = reportMail.getId().getId();
					record.put("email", reportMail.getMailId());
					record.put("schedule", SCHEDULES[reportMail.getSchedule()]);
					record.put("vehiclestatistics", reportMail.getVehicleStatistics());
					record.put("vehiclestatus", reportMail.getVehicleStatus());
					record.put("offlinevehiclereport", reportMail.getOfflineVehicleReport());
					data.put("reports"+".report-"+recordID, record);
				}
			} else {
				IDataset record = new Dataset();
				record.put("id", "");
				record.put("name", "");
				record.put("email","");
				record.put("schedule","");
				record.put("vehiclestatistics", "");
				record.put("vehiclestatus", "");
				record.put("offlinevehiclereport","");
				data.put("reports"+".report-"+0, record);
			}

		} else if (dataView.equalsIgnoreCase("assignment")){
			assignedDataset = new Dataset();
			vacantDataset = new Dataset();
			String reportUserId = params.getRequestParameter("userID");
			String reportIdArray[] = reportUserId.split(",");
			String userId = reportIdArray[0];
			if(reportIdArray.length > 1){
				for(int i=1; i<reportIdArray.length; i++){
					userId+=reportIdArray[i];
				}
			}
			Long reportId = Long.parseLong(userId);
			Long userID = SessionUtils.getCurrentlyLoggedInUser().getId();
			List<Vehicle> assignedVehicleList = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).
			getReportAssignedVehicles(reportId, SessionUtils.getCurrentlyLoggedInUser().getGroupId());
			List<Vehicle> vacantVehicleList = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).
			getReportVacantVehicles(userID);

			Vehicle vehicle = null;
			if (vacantVehicleList.size() != 0) {
				for (int j = 0; j < vacantVehicleList.size(); j++) {
					vehicle = vacantVehicleList.get(j);

					vacantDataset.put("vehicle-" + vehicle.getId().getId()+ ".id", vehicle.getId().getId() + "");
					vacantDataset.put("vehicle-" + vehicle.getId().getId()+ ".name", vehicle.getDisplayName());
					vacantDataset.put("vehicle-" + vehicle.getId().getId()+ ".make", vehicle.getMake());
					vacantDataset.put("vehicle-" + vehicle.getId().getId()+ ".model", vehicle.getModel());
					vacantDataset.put("vehicle-" + vehicle.getId().getId()+ ".year", vehicle.getModelYear());
					vacantDataset.put("vehicle-" + vehicle.getId().getId()+ ".imei", vehicle.getImeiId());
				}
			}
			data.put("vehicle.vacant", vacantDataset);
			if (assignedVehicleList != null) {
				for (int j = 0; j < assignedVehicleList.size(); j++) {
					vehicle = assignedVehicleList.get(j);

					assignedDataset.put("vehicle-" + vehicle.getId().getId()+ ".id", vehicle.getId().getId() + "");
					assignedDataset.put("vehicle-" + vehicle.getId().getId()+ ".name", vehicle.getDisplayName());
					assignedDataset.put("vehicle-" + vehicle.getId().getId()+ ".make", vehicle.getMake());
					assignedDataset.put("vehicle-" + vehicle.getId().getId()+ ".model", vehicle.getModel());
					assignedDataset.put("vehicle-" + vehicle.getId().getId()+ ".year", vehicle.getModelYear());
					assignedDataset.put("vehicle-" + vehicle.getId().getId()+ ".imei", vehicle.getImeiId());
				}
			}
			data.put("vehicle.assigned", assignedDataset);
		}
		return data;
	}

	/**
	 * @see IDataProvider#getName()
	 */
	public String getName() {
		return "reportsettings";
	}

}