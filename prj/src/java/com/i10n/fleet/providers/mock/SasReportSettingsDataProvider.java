package com.i10n.fleet.providers.mock;

import java.util.List;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.DriverDaoImp;
import com.i10n.db.dao.VehicleDaoImpl;
import com.i10n.db.entity.Address;
import com.i10n.db.entity.Driver;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.User;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.util.GeoUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * Mock : Data Provider for Sas Report Settings. This class will be removed in future.
 * 
 * @author Madhu Shree M
 * 
 */
public class SasReportSettingsDataProvider implements IDataProvider{

	public IDataset getDataset(RequestParameters params) {
		IDataset result = new Dataset();
		IDataset assignDataset=new Dataset();
		Vehicle vehicle=null;
		Driver driver=null;
		User user=SessionUtils.getCurrentlyLoggedInUser();
		Long userId=user.getId();
		List<Vehicle> vehicleset=((VehicleDaoImpl)DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).getAssignedVehicles(userId);
		if(null!=vehicleset){
			for(int i=0;i<vehicleset.size();i++){
				vehicle=vehicleset.get(i);
				Long vehicleid=vehicle.getId().getId();
				assignDataset.put("vehicle-"+vehicleid+".id",+vehicleid);
				assignDataset.put("vehicle-"+vehicleid+".name",vehicle.getDisplayName());
				assignDataset.put("vehicle-"+vehicleid+".make",vehicle.getMake());
				assignDataset.put("vehicle-"+vehicleid+".model", vehicle.getModel());
				LiveVehicleStatus liveVehicleStatus = LoadLiveVehicleStatusRecord.getInstance().retrieveByVehicleId(vehicleid);
				StringBuffer location=new StringBuffer();
				if(liveVehicleStatus != null){
					double x = liveVehicleStatus.getLocation().getFirstPoint().getY();
					double y = liveVehicleStatus.getLocation().getFirstPoint().getX();
						Address address = null;
						if (Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_SAS_ENABLED"))) {
							address = GeoUtils.fetchNearestLocation(x,y,false);
							location=StringUtils.formulateAddress(address, vehicleid, x, y);
						} else {
							location.append("SAS ");
							location.append(StringUtils.addressFetchDisabled(vehicleid,x, y).toString());
						}
				}
				assignDataset.put("vehicle-"+vehicleid+".location", location.toString());
				List<Driver> driverset=((DriverDaoImp)DBManager.getInstance().getDao(DAOEnum.DRIVER_DAO)).getAssignedDriver(userId);
				driver=driverset.get(0);
				assignDataset.put("vehicle-"+vehicleid+".driverid",driver.getId().getId());
				assignDataset.put("vehicle-"+vehicleid+".drivername",driver.getFirstName()+ " "+driver.getLastName());
				assignDataset.put("vehicle-"+vehicleid+".driverlastname", driver.getLastName());
				assignDataset.put("vehicle-"+vehicleid+".status", vehicle.getStatus(LoadVehicleDetails.getInstance().
						retrieve(vehicleid).getImei()));
			}
		}
		result.put("trips", assignDataset);
		return result;
	}

	/**
	 * @see IDataProvider#getName()
	 */
	public String getName() {
		return "sasreportsettings";
	}
}