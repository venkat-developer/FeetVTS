package com.i10n.fleet.providers.mock;

import java.util.ArrayList;
import java.util.List;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.DriverDaoImp;
import com.i10n.db.dao.GroupValuesDaoImpl;
import com.i10n.db.dao.UserDaoImp;
import com.i10n.db.dao.VehicleDaoImpl;
import com.i10n.db.entity.Driver;
import com.i10n.db.entity.GroupValues;
import com.i10n.db.entity.User;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.providers.mock.managers.IGroupManager;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;
import com.i10n.fleet.web.utils.SessionUtils;

public class GroupManageDataProvider implements IDataProvider {

	private IGroupManager m_groupManager;

	@Override
	public IDataset getDataset(RequestParameters params) {

		IDataset result = new Dataset();
		String dataView = params.getParameter(RequestParams.dataView);
		if (dataView == null || dataView.isEmpty()) {
			dataView = "default";
		}
		if (dataView.equalsIgnoreCase("default")) {
			String groupID = params.getParameter(RequestParams.groupID);
			if (groupID == null) {
				List<GroupValues> resultset = ((GroupValuesDaoImpl) DBManager.getInstance().getDao(DAOEnum.GROUP_VALUES_DAO)).selectAll();
				IDataset groupData = new Dataset();
				if (null != resultset) {
					for (int i = 0; i < resultset.size(); i++) {
						GroupValues groupValues = resultset.get(i);

						groupData.put("group-" + (i + 1) + ".id", groupValues.getId().getId());
						groupData.put("group-" + (i + 1) + ".name", groupValues.getGroupValue());
					}
				}
				result.put("groups", groupData);
			} else if (null != groupID.trim()) {
				Long groupId = Long.parseLong(StringUtils.stripCommas(groupID));
				String groupValue = ((GroupValuesDaoImpl) DBManager.getInstance().getDao(DAOEnum.GROUP_VALUES_DAO)).getGroupValue(new LongPrimaryKey(groupId));
				result.put("name", groupValue);
			} else {
				result = new Dataset();
				result.put("Error.code", "1404");
				result.put("Error.name", "ResourceNotFoundError");
				result.put("Error.message","The requested resource was not found");
				result.put("groups", null);
			}
		}
		else if (dataView.equalsIgnoreCase("assignment")) {
			result = new Dataset();
			String groupID = params.getParameter(RequestParams.groupID);
			groupID = StringUtils.stripCommas(groupID);
			Long groupId = Long.parseLong(groupID);
			IDataset groupListDataSet = new Dataset();
			IDataset vacantVehicleData = new Dataset();
			IDataset assignVehicleData = new Dataset();
			User usr = SessionUtils.getCurrentlyLoggedInUser();

			List<User> userResultset = ((UserDaoImp)DBManager.getInstance().getDao(DAOEnum.USER_DAO)).selectAllUsers(usr.getGroupId(),usr.getId());
			User user = null;
			ArrayList<Long> userIds = new ArrayList<Long>();
			if(userResultset != null){
				for (int i = 0; i < userResultset.size(); i++) {
					user = userResultset.get(i);
					userIds.add(user.getId());
				}
			}
			userIds.add(usr.getId());
			List<Vehicle> resultset1 = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).getGroupAssignedVehicle(groupId,userIds);
			List<Vehicle> resultset = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).getGroupVacantVehicles(userIds);

			if ((resultset1 != null) || (resultset != null)) {
				for (int i = 0; i < resultset1.size(); i++) {
					Vehicle assgnvehicle = resultset1.get(i);
					assignVehicleData.put("vehicle-" + assgnvehicle.getId().getId() + ".id",assgnvehicle.getId().getId());
					assignVehicleData.put("vehicle-" + assgnvehicle.getId().getId() + ".name",StringUtils.removeSpecialCharacter(assgnvehicle.getDisplayName()));
					assignVehicleData.put("vehicle-" + assgnvehicle.getId().getId() + ".make",StringUtils.removeSpecialCharacter(assgnvehicle.getMake()));
					assignVehicleData.put("vehicle-" + assgnvehicle.getId().getId() + ".model",StringUtils.removeSpecialCharacter(assgnvehicle.getModel()));
					assignVehicleData.put("vehicle-" + assgnvehicle.getId().getId() + ".year",assgnvehicle.getModelYear());
					assignVehicleData.put("vehicle-" + assgnvehicle.getId().getId() + ".imei",assgnvehicle.getImeiId());
				}
				for (int i = 0; i < resultset.size(); i++) {
					Vehicle vacantvehicle = resultset.get(i);
					vacantVehicleData.put("vehicle-" + vacantvehicle.getId().getId() + ".id", vacantvehicle.getId().getId());
					vacantVehicleData.put("vehicle-" + vacantvehicle.getId().getId() + ".name",StringUtils.removeSpecialCharacter(vacantvehicle.getDisplayName()));
					vacantVehicleData.put("vehicle-" + vacantvehicle.getId().getId() + ".make",StringUtils.removeSpecialCharacter(vacantvehicle.getMake()));
					vacantVehicleData.put("vehicle-" + vacantvehicle.getId().getId() + ".model",StringUtils.removeSpecialCharacter(vacantvehicle.getModel()));
					vacantVehicleData.put("vehicle-" + vacantvehicle.getId().getId() + ".year",vacantvehicle.getModelYear());
					vacantVehicleData.put("vehicle-" + vacantvehicle.getId().getId() + ".imei",vacantvehicle.getImeiId());
				}
				result.put("groupvehicles.vacant", vacantVehicleData);
				result.put("groupvehicles.assigned", assignVehicleData);
			}
			else {
				result = new Dataset();
				result.put("Error.code", "1404");
				result.put("Error.name", "ResourceNotFoundError");
				result.put("Error.message","The requested resource was not found");
				result.put("grouplists", groupListDataSet);
			}
		}
		else if (dataView.equalsIgnoreCase("assignmentdriver")) {
			result = new Dataset();
			String groupID = params.getParameter(RequestParams.groupID);
			groupID = StringUtils.stripCommas(groupID);
			Long groupId = Long.parseLong(groupID);
			IDataset groupListDataSet = new Dataset();
			IDataset vacantDriverData = new Dataset();
			IDataset assignDriverData = new Dataset();
			User usr = SessionUtils.getCurrentlyLoggedInUser();
			List<User> userResultset = ((UserDaoImp)DBManager.getInstance().getDao(DAOEnum.USER_DAO)).selectAllUsers(usr.getGroupId(),usr.getId());

			User user = null;
			ArrayList<Long> userIds = new ArrayList<Long>();
			if(userResultset != null){
				for (int i = 0; i < userResultset.size(); i++) {
					user = userResultset.get(i);
					userIds.add(user.getId());
				}
			}
			userIds.add(usr.getId());
			List<Driver> resultset1 = ((DriverDaoImp) DBManager.getInstance().getDao(DAOEnum.DRIVER_DAO)).getGroupAssignedDriver(groupId,userIds);
			List<Driver> resultset = ((DriverDaoImp) DBManager.getInstance().getDao(DAOEnum.DRIVER_DAO)).getGroupVacantDriver(userIds);

			if ((resultset1 != null) || (resultset != null)) {
				for (int i = 0; i < resultset1.size(); i++) {
					Driver assgndriver = resultset1.get(i);
					
					assignDriverData.put("driver-" + assgndriver.getId().getId() + ".id",assgndriver.getId().getId());
					assignDriverData.put("driver-" + assgndriver.getId().getId() + ".name",StringUtils.removeSpecialCharacter(assgndriver.getFirstName()));
				}
				for (int i = 0; i < resultset.size(); i++) {
					Driver vacantdriver = resultset.get(i);

					vacantDriverData.put("driver-" + vacantdriver.getId().getId() + ".id", vacantdriver.getId().getId());
					vacantDriverData.put("driver-" + vacantdriver.getId().getId() + ".name",StringUtils.removeSpecialCharacter(vacantdriver.getFirstName()));
				}
				result.put("groupdrivers.vacant", vacantDriverData);
				result.put("groupdrivers.assigned", assignDriverData);
			}
			else {
				result = new Dataset();
				result.put("Error.code", "1404");
				result.put("Error.name", "ResourceNotFoundError");
				result.put("Error.message","The requested resource was not found");
				result.put("grouplists", groupListDataSet);
			}
		}
		return result;
	}

	@Override
	public String getName() {
		return "groupmanage";
	}

	public IDataset setError(IDataset result) {
		result.put("Error.code", "1404");
		result.put("Error.name", "ResourceNotFoundError");
		result.put("Error.message", "The requested resource was not found");
		return result;
	}

	public void setGroupManager(IGroupManager mGroupManager) {
		m_groupManager = mGroupManager;
	}

	public IGroupManager getGroupManager() {
		return m_groupManager;
	}

}