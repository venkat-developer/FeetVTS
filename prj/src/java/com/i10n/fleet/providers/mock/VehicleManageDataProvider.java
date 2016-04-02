package com.i10n.fleet.providers.mock;

import java.util.List;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.HardwareModuleDaoImp;
import com.i10n.db.dao.TripDaoImp;
import com.i10n.db.dao.UserDaoImp;
import com.i10n.db.dao.VehicleDaoImpl;
import com.i10n.db.entity.HardwareModule;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.Trip;
import com.i10n.db.entity.User;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.providers.mock.managers.IVehicleManager;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;
import com.i10n.fleet.web.utils.SessionUtils;

public class VehicleManageDataProvider implements IDataProvider {

	private static Logger LOG = Logger.getLogger(VehicleManageDataProvider.class);
	private IVehicleManager m_vehicleManager;

	@Override
	public IDataset getDataset(RequestParameters params) {
		IDataset result = new Dataset();
		String dataView = params.getParameter(RequestParams.dataView);
		if (dataView == null || dataView.isEmpty()) {
			dataView = "default";
		}

		//dataview of default is taken for adding, deleting and editing vehicle
		HardwareModule hrd = null;
		List<HardwareModule> Imei = ((HardwareModuleDaoImp) DBManager.getInstance().getDao(DAOEnum.HARDWARE_MODULES_DAO)).getIMEI();
		IDataset vacantimei = new Dataset();
		if (dataView.equalsIgnoreCase("default")) {
			String vehicleID = params.getParameter(RequestParams.vehicleID);
			if (vehicleID == null) {
				List<Vehicle> resultset = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).selectAllOwned();
				IDataset vehicleData = new Dataset();

				if (null != resultset) {
					for (int i = 0; i < resultset.size(); i++) {
						Vehicle vehicle = resultset.get(i);

						vehicleData.put("vehicle-" + (i + 1) + ".id", vehicle.getId().getId()+"");
						vehicleData.put("vehicle-" + (i + 1) + ".name", StringUtils.removeSpecialCharacter(vehicle.getDisplayName())+"");
						vehicleData.put("vehicle-" + (i + 1) + ".make", StringUtils.removeSpecialCharacter(vehicle.getMake())+"");
						vehicleData.put("vehicle-" + (i + 1) + ".model",StringUtils.removeSpecialCharacter(vehicle.getModel())+"");
						vehicleData.put("vehicle-" + (i + 1) + ".year", vehicle.getModelYear()+"");
						vehicleData.put("vehicle-" + (i + 1) + ".imei", vehicle.getImeiId()+"");
						vehicleData.put("vehicle-" + (i + 1) + ".vehicleiconid", vehicle.getVehicleIconPicId());
					}
				}
				result.put("vehicles", vehicleData);
				if(null!=Imei){
					for (int j = 0; j < Imei.size(); j++) {
						hrd = Imei.get(j);

						vacantimei.put("imei-" + (j + 1) + ".imeiid", hrd.getId().getId()+"");
						vacantimei.put("imei-" + (j + 1) + ".vacantimei", hrd.getImei()+"");
					}
				}
				result.put("vacantimei", vacantimei);

				IDataset userAttributeMap = new Dataset();
				List<User> users = ((UserDaoImp) DBManager.getInstance().getDao(DAOEnum.USER_DAO))
						.getUsersForOwner(SessionUtils.getCurrentlyLoggedInUser().getId());
				if (null != users) {
					for (int i = 0; i < users.size(); i++) {
						User user = users.get(i);
						userAttributeMap.put("user-" + user.getId() + ".id", user.getId()+"");
						userAttributeMap.put("user-" + user.getId()+ ".loginid", user.getLogin()+"");
					}
				}
				result.put("users", userAttributeMap);

			} else if (null != vehicleID.trim()) {
				Long vehicleId = Long.parseLong(StringUtils.stripCommas(vehicleID));
				List<Trip> vehicleTrip = ((TripDaoImp) DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).getActiveTrips(vehicleId);
				if(vehicleTrip.size() > 0){
					LiveVehicleStatus selectedLiveVehicleStatus = LoadLiveVehicleStatusRecord.getInstance().retrieveByVehicleId(vehicleId);
					if(selectedLiveVehicleStatus != null){
						result.put("isoffroadid", selectedLiveVehicleStatus.isOffroad()?1:2);
					}
				}
				Vehicle vehicle = LoadVehicleDetails.getInstance().retrieve(vehicleId);
				result.put("name", StringUtils.removeSpecialCharacter(vehicle.getDisplayName())+"");
				result.put("make", StringUtils.removeSpecialCharacter(vehicle.getMake())+"");
				result.put("model", StringUtils.removeSpecialCharacter(vehicle.getModel())+"");
				result.put("year", vehicle.getModelYear()+"");
				result.put("imei", vehicle.getImei()+"");
				result.put("imeiid", vehicle.getImeiId()+"");
				result.put("vehicleiconid", vehicle.getVehicleIconPicId()+"");
				if(null!=Imei){
					for (int j = 0; j < Imei.size(); j++) {
						hrd = Imei.get(j);

						vacantimei.put("imei-" + (j + 1) + ".imeiid", hrd.getId().getId()+"");
						vacantimei.put("imei-" + (j + 1) + ".vacantimei", hrd.getImei()+"");
					}
					result.put("vacantimei", vacantimei);
				}
			} else {
				result = new Dataset();
				result.put("Error.code", "1404");
				result.put("Error.name", "ResourceNotFoundError");
				result.put("Error.message", "The requested resource was not found");
				result.put("vehicles", m_vehicleManager.getData(getVehicleOptions()));
			}

		}
		//for managing part of vehicle the following code is run
		else if (dataView.equalsIgnoreCase("assignment")) {
			result = new Dataset();
			String userID = params.getParameter(RequestParams.userID);
			userID=StringUtils.stripCommas(userID);

			Long userid = Long.parseLong(userID);
			IDataset userAttributeMap = new Dataset();
			IDataset vacantVehicleData = new Dataset();
			IDataset assignVehicleData = new Dataset();
			List<Vehicle> assignedVehicesResultSet = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).getAssignedVehicles(userid);
			List<Vehicle> vacantVehiceResultSet = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).getVacantVehicle(userid);

			if (null != assignedVehicesResultSet) {
				Vehicle vehicle= null;

				for (int i = 0; i < assignedVehicesResultSet.size(); i++) {
					vehicle = assignedVehicesResultSet.get(i);

					assignVehicleData.put("vehicle-" + (i + 1) + ".id",vehicle.getId().getId()+"");
					assignVehicleData.put("vehicle-" + (i + 1) + ".name",StringUtils.removeSpecialCharacter(vehicle.getDisplayName())+"");
					assignVehicleData.put("vehicle-" + (i + 1) + ".make",StringUtils.removeSpecialCharacter(vehicle.getMake())+"");
					assignVehicleData.put("vehicle-" + (i + 1) + ".model",StringUtils.removeSpecialCharacter(vehicle.getModel())+"");
					assignVehicleData.put("vehicle-" + (i + 1) + ".year",vehicle.getModelYear()+"");
					assignVehicleData.put("vehicle-" + (i + 1) + ".imei",vehicle.getImeiId()+"");
				}

				for (int i = 0; i < vacantVehiceResultSet.size(); i++) {
					vehicle = vacantVehiceResultSet.get(i);
					vacantVehicleData.put("vehicle-" + (i + 1) + ".id", vehicle.getId().getId()+"");
					vacantVehicleData.put("vehicle-" + (i + 1) + ".name",StringUtils.removeSpecialCharacter(vehicle.getDisplayName())+"");
					vacantVehicleData.put("vehicle-" + (i + 1) + ".make",StringUtils.removeSpecialCharacter(vehicle.getMake())+"");
					vacantVehicleData.put("vehicle-" + (i + 1) + ".model",StringUtils.removeSpecialCharacter(vehicle.getModel())+"");
					vacantVehicleData.put("vehicle-" + (i + 1) + ".year",vehicle.getModelYear()+"");
					vacantVehicleData.put("vehicle-" + (i + 1) + ".imei",vehicle.getImeiId()+"");
				}
				result.put("vehicle.vacant", vacantVehicleData);
				result.put("vehicle.assigned", assignVehicleData);
			}
			else {
				LOG.error("error handling");
				result = new Dataset();
				result.put("Error.code", "1404");
				result.put("Error.name", "ResourceNotFoundError");
				result.put("Error.message", "The requested resource was not found");
				result.put("users", userAttributeMap);
			}
		}

		return result;
	}

	@Override
	public String getName() {
		return "vehiclemanage";
	}

	/**
	 * Filters out the un-necessary vehicle data
	 *
	 * @return
	 */
	private IDataset getVehicleOptions() {
		IDataset result = new Dataset();
		result.put("filter.assigned", "true");
		result.put("skip.trip", true);
		return result;
	}

	public IDataset setError(IDataset result) {
		result.put("Error.code", "1404");
		result.put("Error.name", "ResourceNotFoundError");
		result.put("Error.message", "The requested resource was not found");
		return result;
	}

	public void setVehicleManager(IVehicleManager mVehicleManager) {
		m_vehicleManager = mVehicleManager;
	}

	public IVehicleManager getVehicleManager() {
		return m_vehicleManager;
	}

}
