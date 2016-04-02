package com.i10n.fleet.web.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.DriverDaoImp;
import com.i10n.db.dao.GroupValuesDaoImpl;
import com.i10n.db.dao.VehicleDaoImpl;
import com.i10n.db.entity.GroupValues;
import com.i10n.db.entity.User;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadDriverDetails;
import com.i10n.dbCacheManager.LoadGroupDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.utils.SessionUtils;

/*
 * Class implementing methods for adding deleting editing and assigning vehicles  * 
 */

public class GroupAdminOperations extends SimpleFormController {
	
	private Logger LOG = Logger.getLogger(GroupAdminOperations.class);

	public GroupValues addGroup(HttpServletRequest request) {
		GroupValues groupValues = null;

		String groupName = request.getParameter("name");
		groupValues = new GroupValues(groupName.trim());
		User user = SessionUtils.getCurrentlyLoggedInUser();
		groupValues.setGroupId(user.getGroupId());

		try {
			groupValues = ((GroupValuesDaoImpl) DBManager.getInstance().getDao(
					DAOEnum.GROUP_VALUES_DAO)).insert(groupValues);
			if(groupValues == null){
				return null;
			}
			LoadGroupDetails.getInstance().cacheGroups.put(groupValues.getId().getId(), groupValues);
		} catch (OperationNotSupportedException e) {
			LOG.error(e);
		} catch (Exception e){
			LOG.error(e);
		}
		return groupValues;

	}

	public GroupValues editGroup(HttpServletRequest request) {

		String groupKey = request.getParameter("key");
		String gKey = StringUtils.stripCommas(groupKey.trim());
		Long groupId = Long.parseLong(gKey.trim());

		String groupName = request.getParameter("name");

		GroupValues groupValues = null;

		if (groupName != null) {
			groupValues = LoadGroupDetails.getInstance().retrieve(groupId);
			groupValues.setGroupValue(groupName);

			try {
				groupValues = ((GroupValuesDaoImpl) DBManager.getInstance()
						.getDao(DAOEnum.GROUP_VALUES_DAO)).update(groupValues);
				if(groupValues == null){
					return null;
				}
				LoadGroupDetails.getInstance().cacheGroups.put(groupId, groupValues);
			} catch (OperationNotSupportedException e) {
				LOG.error(e);
			} catch (Exception e){
				LOG.error(e);
			}
		}

		return groupValues;

	}

	public boolean assignVehiclestoGroup(HttpServletRequest request) {
		boolean opSuccess = true;
		String groupIdString = request.getParameter("groupId");
		Long groupId = Long.parseLong(StringUtils.stripCommas(groupIdString.trim()));

		String assignedVehicleList = request.getParameter("assignedVehicles");
		String vacantVehicleList = request.getParameter("vacantVehicles");

		String[] assignedVehicles = assignedVehicleList.trim().split(",");
		String[] vacantVehicles = vacantVehicleList.trim().split(",");
		if (assignedVehicles != null || vacantVehicles != null) {

			for (int i = 0; i < assignedVehicles.length; i++) {

				Long assignedVehicleID = null;
				if (!assignedVehicles[i].equals("") || assignedVehicles[i].length() != 0) {
					assignedVehicleID = Long.parseLong(assignedVehicles[i]);
				} else {
					continue;
				}
				// ACLVehicle Aclveh = new ACLVehicle(avehicles, groupId);
				boolean bool = false;
				try {
					bool = ((VehicleDaoImpl) DBManager.getInstance().getDao(
							DAOEnum.VEHICLE_DAO)).updateGroupId(assignedVehicleID, groupId);
					if (!bool) {
						opSuccess = false;
						continue;
					}
					/* Refreshing the cache */
					LoadVehicleDetails.getInstance().getDataForNewlyAddedVehicle(assignedVehicleID);
				} catch (OperationNotSupportedException e) {
					LOG.error(e);
				} catch (Exception e){
					LOG.error(e);
				}
			}

			for (int i = 0; i < vacantVehicles.length; i++) {
				// Create dummy driver

				Long vacantVehicleID = null;

				if (!vacantVehicles[i].equals("") || vacantVehicles[i].length() != 0) {
					vacantVehicleID = Long.parseLong(vacantVehicles[i]);
				} else {
					continue;
				}

				boolean bool = false;
				try {
					bool = ((VehicleDaoImpl) DBManager.getInstance().getDao(
							DAOEnum.VEHICLE_DAO)).updateVacantGroupId(vacantVehicleID);
					if (!bool) {
						opSuccess = false;
						continue;
					}
					/* Refreshing the cache */
					LoadVehicleDetails.getInstance().getDataForNewlyAddedVehicle(vacantVehicleID);
				} catch (OperationNotSupportedException e) {
					LOG.error(e);
				} catch (Exception e){
					LOG.error(e);
				}
			}
		}
		return opSuccess;
	}

	public boolean assignDriverstoGroup(HttpServletRequest request) {
		boolean opSuccess = true;
		String groupIdSting = request.getParameter("groupId");
		Long groupId = Long.parseLong(StringUtils.stripCommas(groupIdSting.trim()));

		String assignedDriverList = request.getParameter("assignedDrivers");
		String vacantDriverList = request.getParameter("vacantDrivers");

		String[] assignedDrivers = assignedDriverList.trim().split(",");
		String[] vacantDrivers = vacantDriverList.trim().split(",");
		if (assignedDrivers != null || vacantDrivers != null) {

			for (int i = 0; i < assignedDrivers.length; i++) {

				Long assignedDriverID = null;
				if (!assignedDrivers[i].equals("") || assignedDrivers[i].length() != 0) {
					assignedDriverID = Long.parseLong(assignedDrivers[i]);
				} else {
					continue;
				}
				// ACLVehicle Aclveh = new ACLVehicle(adrivers, groupId);
				boolean bool = false;
				try {
					bool = ((DriverDaoImp) DBManager.getInstance().getDao(
							DAOEnum.DRIVER_DAO)).updateGroupId(assignedDriverID, groupId);
					if (!bool) {
						opSuccess = false;
						continue;
					}
					LoadDriverDetails.getInstance().retrieveIMEIDataFromDBIfNotInMemory(assignedDriverID);
				} catch (OperationNotSupportedException e) {
					LOG.error(e);
				} catch (Exception e){
					LOG.error(e);
				}
			}

			for (int i = 0; i < vacantDrivers.length; i++) {
				// Create dummy driver

				Long vacantDriverID = null;

				if (!vacantDrivers[i].equals("") || vacantDrivers[i].length() != 0) {
					vacantDriverID = Long.parseLong(vacantDrivers[i]);
				} else {
					continue;
				}

				boolean bool = false;
				try {
					bool = ((DriverDaoImp) DBManager.getInstance().getDao(
							DAOEnum.DRIVER_DAO)).updateVacantGroupId(vacantDriverID);
					if (!bool) {
						opSuccess = false;
						continue;
					}
					LoadDriverDetails.getInstance().retrieveIMEIDataFromDBIfNotInMemory(vacantDriverID);
				} catch (OperationNotSupportedException e) {
					LOG.error(e);
				} catch (Exception e){
					LOG.error(e);
				}
			}

		}
		return opSuccess;
	}

	public boolean[] deleteGroup(String[] groupIds) {
		GroupValues groupValues = null;

		boolean[] successStatus = new boolean[groupIds.length];
		java.util.Arrays.fill(successStatus, true);

		for (int i = 0; i < groupIds.length; i++) {
			Long groupID = Long.parseLong(StringUtils
					.stripCommas(groupIds[i]));
			groupValues = LoadGroupDetails.getInstance().retrieve(groupID);
			try {

				groupValues = ((GroupValuesDaoImpl) DBManager.getInstance()
						.getDao(DAOEnum.GROUP_VALUES_DAO)).delete(groupValues);
				if (groupValues == null) {
					successStatus[i] = false;
				} else {
					successStatus[i] = true;
					groupValues.setDeletedStatus(true);
					LoadGroupDetails.getInstance().cacheGroups.put(groupID, groupValues);
				}

			} catch (OperationNotSupportedException e) {
				LOG.error(e);
			} catch (Exception e){
				LOG.error(e);
			}
		}
		return successStatus;
	}
}