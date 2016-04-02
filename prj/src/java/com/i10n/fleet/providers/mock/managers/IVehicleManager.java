package com.i10n.fleet.providers.mock.managers;

import com.i10n.fleet.datasets.IDataset;

/**
 * Mock : An interface for {@link VehicleManager}. This interface will be
 * removed in future
 * 
 * @author Sabarish
 * 
 */
public interface IVehicleManager extends IDataManager {


IDataset getVacantVehiclesData(IDataset options);
IDataset getVacantVehicleForDashBoard();
IDataset getStatusDataForDashboard();
IDataset getVehicleHealthDataForDahsboard();
IDataset getParseDataset(IDataset vehicleOptions);

}
