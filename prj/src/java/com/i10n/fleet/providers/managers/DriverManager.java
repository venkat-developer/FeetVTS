package com.i10n.fleet.providers.managers;

import java.util.List;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.DriverDaoImp;
import com.i10n.db.entity.Driver;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;

public class DriverManager implements IDataManager {
	// IDataset result = new Dataset();
	IDataset driverattrip = new Dataset();

	@Override
	public IDataset getData(IDataset result) {
		List<Driver> resultset = ((DriverDaoImp) DBManager.getInstance().getDao(DAOEnum.DRIVER_DAO)).selectAll();
		if (null != resultset) {

			for (int i = 0; i < resultset.size(); i++) {
				IDataset driverData = new Dataset();

				Driver driver = resultset.get(i);
				driverData.put("id", driver.getId());
				driverData.put("firstname", driver.getFirstName());
				driverData.put("lastname", driver.getLastName());

				driverattrip.put("driver-" + driver.getId(), driverData);
					}
		}
		return driverattrip;
	}
}