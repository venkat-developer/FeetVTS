package com.i10n.fleet.providers.mock;

import java.util.List;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.MailinglistAlertDaoImp;
import com.i10n.db.dao.VehicleDaoImpl;
import com.i10n.db.entity.MailingListAlert;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.web.request.RequestParameters;

/**
 * Mock : Mock Data Provider for Alert Settings. This class will be removed in
 * future.
 * 
 * @author Sabarish
 * 
 */
public class AlertSettingsDataProvider implements IDataProvider {

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
		IDataset data = new Dataset();
		Long recordID = null;
		IDataset assignedDataset = new Dataset();
		IDataset vacantDataset = new Dataset();
		MailingListAlert alertMail = null;
		String dataView = params.getRequestParameter("dataView");

		if(dataView == null){
			List<MailingListAlert> report = ((MailinglistAlertDaoImp) DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_ALERT_DAO)).selectAllOwned();
			if(report.size() != 0){
				for (int i = 0; i < report.size(); i++) {
					IDataset record = new Dataset();
					alertMail = report.get(i);
					record.put("id", alertMail.getId().getId());
					record.put("name", alertMail.getName());
					recordID = alertMail.getId().getId();
					record.put("email", alertMail.getMailId());
					record.put("overspeeding", alertMail.getOverSpeeding());
					record.put("geofencing", alertMail.getGeoFencing());
					record.put("chargerdisconnected", alertMail.getChargerDisConnected());
					record.put("ignition",alertMail.isIgnition());
					data.put("reports"+".report-"+recordID, record);
				}
			} else {
				IDataset record = new Dataset();
				record.put("id", "");
				record.put("name", "");
				record.put("email", "");
				record.put("overspeeding", "");
				record.put("geofencing", "");
				record.put("chargerdisconnected", "");
				data.put("reports"+".report-0", record);
			}
		}else if (dataView.equalsIgnoreCase("assignment")) {
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
			Long userID = Long.parseLong(userId);
			List<Vehicle> assignedVehicleList = ((VehicleDaoImpl)DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).getAlertAssignedVehicles(userID);
			List<Vehicle> vacantVehicleList = ((VehicleDaoImpl)DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).getAlertVacantVehicles(userID);

			Vehicle vehicle = null;
			if (vacantVehicleList != null) {
				for (int j = 0; j < vacantVehicleList.size(); j++) {
					vehicle = vacantVehicleList.get(j);
					vacantDataset.put("vehicle-" + vehicle.getId().getId()+ ".id",  vehicle.getId().getId()+"");
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
					assignedDataset.put("vehicle-" + vehicle.getId().getId()+ ".id",  vehicle.getId().getId()+"");
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
		return "alertsettings";
	}
} 