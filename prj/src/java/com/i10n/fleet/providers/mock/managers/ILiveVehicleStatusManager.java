
package com.i10n.fleet.providers.mock.managers;

import com.i10n.fleet.datasets.IDataset;

public interface ILiveVehicleStatusManager extends IDataManager {
	
	IDataset getDataForDashboard();

}

