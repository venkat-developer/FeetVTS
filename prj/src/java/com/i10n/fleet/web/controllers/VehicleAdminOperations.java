package com.i10n.fleet.web.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.i10n.db.dao.ACLVehicleDaoImp;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.HardwareModuleDaoImp;
import com.i10n.db.dao.LiveVehicleStatusDaoImp;
import com.i10n.db.dao.VehicleDaoImpl;
import com.i10n.db.dao.VehicleHistoryDaoImp;
import com.i10n.db.entity.ACLVehicle;
import com.i10n.db.entity.HardwareModule;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.VehicleHistory;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadAclVehicleDetails;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.StringUtils;

/*
 * Class implementing methods for adding deleting editing and assigning vehicles  * 
 */

public class VehicleAdminOperations extends SimpleFormController {

	private static Logger LOG = Logger.getLogger(VehicleAdminOperations.class);

	public Vehicle addVehicle(HttpServletRequest request) {
		Vehicle vehicle;

		String displayName = request.getParameter("name");
		String make = request.getParameter("make");
		String model = request.getParameter("model");
		String modelYear = request.getParameter("year");
		String imei = request.getParameter("imei");
		if(imei.contains(","))
			imei = imei.replace(",", "");
		Long imeiid = Long.parseLong(imei.trim());
		String VehicleIconPicId = request.getParameter("vehicleiconid");
		int vehicle_icon_pic_id = Integer.parseInt(VehicleIconPicId.trim());
		vehicle = new Vehicle(displayName.trim(), make.trim(), model.trim(), modelYear.trim(), imeiid, imei.trim(), vehicle_icon_pic_id);
		try {
			vehicle = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).insert(vehicle);
			vehicle = ((VehicleHistoryDaoImp) DBManager.getInstance().getDao(DAOEnum.VEHICLE_HISTORY_DAO)).insert(vehicle);
			updateVehicleCache(vehicle);
			LoadAclVehicleDetails.getInstance().refresh();
		} catch (OperationNotSupportedException e) {
			LOG.error("Adding Vehicle ",e);
		}
		return vehicle;
	}

	public Vehicle editVehicle(HttpServletRequest request) {
		String vehicleKey = request.getParameter("key");
		String vKey = StringUtils.stripCommas(vehicleKey);
		Long vehicleId = Long.parseLong(vKey);
		String displayName = request.getParameter("name");
		String make = request.getParameter("make");
		String model = request.getParameter("model");
		String modelYear = request.getParameter("year");
		String imei = request.getParameter("imei");
		String isOffRoadParam = request.getParameter("isoffroadid");
		boolean isOffRoad = false;
		if(isOffRoadParam.trim().equalsIgnoreCase("1")){
			isOffRoad=true;
		}else if(isOffRoadParam.trim().equalsIgnoreCase("2")){
			isOffRoad=false;
		}
		if(imei.contains(","))
			imei = imei.replace(",", "");

		Long imeiid = Long.parseLong(imei.trim());
		String VehicleIconPicId = request.getParameter("vehicleiconid");
		int vehicle_icon_pic_id = Integer.parseInt(VehicleIconPicId.trim());

		Vehicle vehicle = null;

		if (displayName != null && make != null && model != null && modelYear != null && imei != null) {
			vehicle = LoadVehicleDetails.getInstance().retrieve(vehicleId);
			vehicle.setDisplayName(displayName.trim());
			vehicle.setMake(make.trim());
			vehicle.setModel(model.trim());
			vehicle.setModelYear(modelYear.trim());
			vehicle.setImeiId(imeiid);
			vehicle.setVehicleIconPicId(vehicle_icon_pic_id);

			try {
				vehicle = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).update(vehicle);
				((LiveVehicleStatusDaoImp)DBManager.getInstance().getDao(DAOEnum.LIVE_VEHICLE_STATUS_DAO)).updateIsOffRoadStatus(isOffRoad,vehicleId);
				List<VehicleHistory> vehicleHistoryList = ((VehicleHistoryDaoImp) DBManager.getInstance().getDao(DAOEnum.VEHICLE_HISTORY_DAO)).getHistoryForSelectedVehicle(vehicleId);
				if(vehicleHistoryList.size()==0){
					vehicle = ((VehicleHistoryDaoImp) DBManager.getInstance().getDao(DAOEnum.VEHICLE_HISTORY_DAO)).insert(vehicle);
				}
				if(imeiid != 0 ){
					List<HardwareModule> hardwareModule = ((HardwareModuleDaoImp) DBManager.getInstance().
							getDao(DAOEnum.HARDWARE_MODULES_DAO)).selectByPrimaryKey(new LongPrimaryKey(imeiid));
					if(hardwareModule.size() != 0)
						vehicle.setImei(hardwareModule.get(0).getImei());
				}
				LoadVehicleDetails.getInstance().cacheVehicleRecords.put(vehicleId, vehicle);
				if(imeiid != 0 )
					LoadLiveVehicleStatusRecord.getInstance().refresh();

			} catch (OperationNotSupportedException e) {
				LOG.error("EDTING Vehicle ERROR ",e);
			}
		}

		return vehicle;
	}

	/**
	 * Vehicle History Page Data Fetch From Admin Cotroller
	 * @param request
	 * @return
	 */

	public Vehicle vehiclehistory(HttpServletRequest request) {
		String vehicleKey = request.getParameter("key");
		String vKey = StringUtils.stripCommas(vehicleKey);
		Long vehicleId = Long.parseLong(vKey);
		String displayName = request.getParameter("name");
		String status = request.getParameter("status");
		String imei = request.getParameter("imei");
		if(imei.contains(","))
			imei = imei.replace(",", "");

		String batterychangedstring  = request.getParameter("batterychanged");
		String fusechangestring = request.getParameter("fusechange");
		String vehicleattended = request.getParameter("vehicleattended");
		Vehicle vehicle = null;

		if (displayName != null && status != null && imei != null && batterychangedstring != null && fusechangestring != null && vehicleattended != null) {
			vehicle=LoadVehicleDetails.getInstance().retrieve(vehicleId);
			boolean batterychanged = Boolean.parseBoolean(batterychangedstring);
			boolean fusechange = Boolean.parseBoolean(fusechangestring);
			try {
				vehicle = ((VehicleHistoryDaoImp) DBManager.getInstance().getDao(DAOEnum.VEHICLE_HISTORY_DAO)).updateVehicleHistory(vehicle,batterychanged,fusechange,vehicleattended);
			} catch (OperationNotSupportedException e) {
				LOG.error("EDTING Vehicle ERROR ",e);
			}
		}

		return vehicle;
	}

	public boolean assignVehicle(HttpServletRequest request) {
		boolean opSuccess = true;
		String userIdString = request.getParameter("userId");
		String userId = StringUtils.stripCommas(userIdString.trim());
		Long user = Long.parseLong(userId);
		String assignedvehlist = request.getParameter("assignedVehicles");
		String vacantvehlist = request.getParameter("vacantVehicles");
		String[] assinedvehs = assignedvehlist.trim().split(",");
		String[] vacantvehs = vacantvehlist.trim().split(",");
		if (assinedvehs != null || vacantvehs != null) {
			for (int i = 0; i < assinedvehs.length; i++) {
				Long avehicles = null;
				if (!assinedvehs[i].equals("") || assinedvehs[i].length() != 0) {
					avehicles = Long.parseLong(assinedvehs[i]);
				} else {
					continue;
				}
				ACLVehicle aclVehicle = new ACLVehicle(avehicles, user);
				try {
					aclVehicle = ((ACLVehicleDaoImp) DBManager.getInstance().getDao(DAOEnum.ACL_VEHICLE_DAO)).insert(aclVehicle);
					if (aclVehicle == null) {
						opSuccess = false;
					}
				} catch (OperationNotSupportedException e) {
					LOG.error(e);
				}
			}

			for (int i = 0; i < vacantvehs.length; i++) {
				Long vvehs = null;
				if (!vacantvehs[i].equals("") || vacantvehs[i].length() != 0) {
					vvehs = Long.parseLong(vacantvehs[i]);
				} else {
					continue;
				}
				ACLVehicle aclveh = new ACLVehicle(vvehs, user);
				try {
					aclveh = ((ACLVehicleDaoImp) DBManager.getInstance().getDao(DAOEnum.ACL_VEHICLE_DAO)).delete(aclveh);
					if (aclveh == null) {
						opSuccess = false;
					}
				} catch (OperationNotSupportedException e) {
					LOG.error(e);
				}
			}
			LoadAclVehicleDetails.getInstance().refresh();
		}
		return opSuccess;
	}

	public boolean[] deleteVehicle(String[] vehicleIds) {
		Vehicle vehicle = null;
		boolean[] successStatus = new boolean[vehicleIds.length];
		java.util.Arrays.fill(successStatus, true);

		for (int i = 0; i < vehicleIds.length; i++) {
			Long vehicleId = Long.parseLong(StringUtils.stripCommas(vehicleIds[i]));
			vehicle = LoadVehicleDetails.getInstance().retrieve(vehicleId);
			try {
				vehicle = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).delete(vehicle);
				if (vehicle == null) {
					successStatus[i] = false;
				} else{
					successStatus[i] = true;
					vehicle.setDeleted(true);
					updateVehicleCache(vehicle);
				}
			} catch (OperationNotSupportedException e) {
				LOG.error(e);
			}
		}
		return successStatus;
	}

	/**
	 * Updating the vehicle cache on any alteration in vehicles table.
	 * @param vehicle
	 */
	private void updateVehicleCache(Vehicle vehicle) {
		LoadVehicleDetails.getInstance().cacheVehicleRecords.put(vehicle.getId().getId(), vehicle);
	}
}
