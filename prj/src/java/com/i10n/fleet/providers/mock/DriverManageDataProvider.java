package com.i10n.fleet.providers.mock;

import java.util.Iterator;
import java.util.List;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.DriverDaoImp;
import com.i10n.db.dao.UserDaoImp;
import com.i10n.db.entity.Driver;
import com.i10n.db.entity.User;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.providers.mock.managers.IDriverManager;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;
import com.i10n.fleet.web.utils.SessionUtils;

public class DriverManageDataProvider implements IDataProvider {

	private IDriverManager m_driverManager;

	@Override
	public IDataset getDataset(RequestParameters params) {
		/*
		 * Fetch the data from the database and send it as a Dataset to the
		 * EditDriver.ftm,DeleteDriver.ftm
		 */
		IDataset result = new Dataset();

		// If the dataview is Null then it will set as Default

		String dataView = params.getParameter(RequestParams.dataView);

		if (dataView == null || dataView.isEmpty()) {
			dataView = "default";
		}
		if (dataView.equalsIgnoreCase("default")) {
			String driverid = params.getParameter(RequestParams.driverID);
			String driverID = StringUtils.stripCommas(driverid);
			if (driverID == null) {
				List<Driver> resultset = ((DriverDaoImp) DBManager.getInstance().getDao(DAOEnum.DRIVER_DAO)).selectAllOwned();
				IDataset driverData = new Dataset();

				if (null != resultset) {
					for (int i = 0; i < resultset.size(); i++) {
						Driver driver = resultset.get(i);

						driverData.put("driver-" + (i + 1) + ".id", driver.getId().getId());
						driverData.put("driver-" + (i + 1) + ".firstname",StringUtils.removeSpecialCharacter(driver.getFirstName()));
						driverData.put("driver-" + (i + 1) + ".lastname",StringUtils.removeSpecialCharacter(driver.getLastName()));
						driverData.put("driver-" + (i + 1) + ".license", driver.getLicenseno());
						driverData.put("driver-" + (i + 1) + ".photo", driver.getPhoto());
						driverData.put("driver-" + (i + 1) + ".status","vacant");
					}
				}
				result.put("drivers", driverData);
				IDataset userAttributeMap = new Dataset();
				List<User> users = ((UserDaoImp) DBManager.getInstance().getDao(DAOEnum.USER_DAO)).getUsersForOwner(SessionUtils.getCurrentlyLoggedInUser().getId());
				if (null != users) {
					for (int i = 0; i < users.size(); i++) {
						User user = users.get(i);
						userAttributeMap.put("user-" + (i + 1) + ".id", user.getId());
						userAttributeMap.put("user-" + (i + 1) + ".name", user.getLogin());
					}
					result.put("users", userAttributeMap);
				}
			}
			// This will be called when u select a driver from the side panel list drivers
			else if (null != driverID) {
				Long did = Long.parseLong(StringUtils.stripCommas(driverID));
				LongPrimaryKey Driverid = new LongPrimaryKey(did);
				List<Driver> resultset = ((DriverDaoImp) DBManager.getInstance().getDao(DAOEnum.DRIVER_DAO)).selectByPrimaryKey(Driverid);
				for (Iterator<Driver> iter = resultset.iterator(); iter.hasNext();) {
					Driver element = (Driver) iter.next();
					result.put("firstname", StringUtils.removeSpecialCharacter(element.getFirstName()));
					result.put("lastname", StringUtils.removeSpecialCharacter(element.getLastName()));
					result.put("license", element.getLicenseno());
					result.put("photo", element.getPhoto());
				}
			}
			/*else {
				result = new Dataset();
				result.put("Error.code", "1404");
				result.put("Error.name", "ResourceNotFoundError");
				result.put("Error.message","The requested resource was not found");
				result.put("drivers", m_driverManager.getData(new Dataset()));
			}*/
		}
		else if (dataView.equalsIgnoreCase("assignment")) {
			result = new Dataset();
			IDataset driverData = new Dataset();
			IDataset vacantdriverData = new Dataset();
			String userID = params.getParameter(RequestParams.userID);
			userID=StringUtils.stripCommas(userID);
			Long userid = Long.parseLong(userID);

			// Get the assigned drivers from acldriver table for the particular user.
			List<Driver> assignedDriverList = ((DriverDaoImp) DBManager.getInstance().getDao(DAOEnum.DRIVER_DAO)).getAssignedDriver(userid);

			// Get the vacant drivers Id when the user selected in the list
			List<Driver> vacantDriverList = ((DriverDaoImp) DBManager.getInstance().getDao(DAOEnum.DRIVER_DAO)).getNonAsssignedDrivers(userid);

			// Checks weather the user already assigned or first time.
			// if its first time i will load all the drivers or load the assigned
			// drivers and vacant drivers

			if (assignedDriverList.size() != 0) {
				for (int i = 0; i < assignedDriverList.size(); i++) {
					Driver driver = assignedDriverList.get(i);
					
					driverData.put("driver-" + (i + 1) + ".id", driver.getId().getId());
					driverData.put("driver-" + (i + 1) + ".name", StringUtils.removeSpecialCharacter(driver.getFirstName()));           
				}
				
				if(vacantDriverList.size() != 0)
					for (int i = 0; i < vacantDriverList.size(); i++) {
						Driver driver = vacantDriverList.get(i);

						vacantdriverData.put("driver-" + (i + 1) + ".id",driver.getId().getId());
						vacantdriverData.put("driver-" + (i + 1) + ".name",StringUtils.removeSpecialCharacter(driver.getFirstName()));
					}
				result.put("driver.assigned", driverData);
				result.put("driver.vacant", vacantdriverData);
			} else {
				// Get all the driver from the drivers table to display the driver list first time
				List<Driver> allDriver = ((DriverDaoImp) DBManager.getInstance().getDao(DAOEnum.DRIVER_DAO)).selectAll();
				
				for (int i = 0; i < allDriver.size(); i++) {
					Driver driver = allDriver.get(i);

					driverData.put("driver-" + (i + 1) + ".id", driver.getId().getId());
					driverData.put("driver-" + (i + 1) + ".name", StringUtils.removeSpecialCharacter(driver.getFirstName()));
				}
				result.put("driver.vacant", driverData);
				result.put("driver.assigned", vacantdriverData);
			}
		}
		return result;
	}

	@Override
	public String getName() {
		return "drivermanage";
	}

	public void setDriverManager(IDriverManager mDriverManager) {
		m_driverManager = mDriverManager;
	}

	public IDriverManager getDriverManager() {
		return m_driverManager;
	}

}