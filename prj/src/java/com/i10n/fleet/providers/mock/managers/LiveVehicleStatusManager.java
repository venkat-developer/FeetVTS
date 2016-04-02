
package com.i10n.fleet.providers.mock.managers;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.TripDetailsDaoImpl;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.TripDetails;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * This class provides the data for the Live vehicle status tab in the 
 * Dash board.
 * 
 * Converted to use cached data
 *
 */


public class LiveVehicleStatusManager extends AbstractViolationsManager implements ILiveVehicleStatusManager {

	private static Logger LOG = Logger.getLogger(TripDetailsDaoImpl.class);

	@Override
	protected String getDocumentName() {
		// TODO Auto-generated method stub
		return null;
	}

	public IDataset getData(IDataset options) {
		IDataset data = getParsedData();
		return data;
	}

	private synchronized IDataset getParsedData() {

		IDataset data = new Dataset();
		IDataset arrayData = null;
		ArrayList<Dataset> liveVehicleStatusData = null;

		/* Vishnu : Fetching information from cache */
		LOG.debug("Attempting to fetch data for LiveVehicleStatusManager::getParsedData from cache");
		TripDetailsDaoImpl tripDetailsDaoImpl = (TripDetailsDaoImpl)DBManager.getInstance().getDao(DAOEnum.TRIP_DETAILS_DAO);
		List<TripDetails> tripDetailsList = tripDetailsDaoImpl.getActiveTripDetailsWithLiveStatusForTheUser(new LongPrimaryKey(SessionUtils.getCurrentlyLoggedInUser().getId()));

		LiveVehicleStatus vehicleStatus = null;
		String vehicleName = null;
		Vehicle veh = new Vehicle();
		if (null != tripDetailsList) {
			for (int i = 0; i < tripDetailsList.size(); i++) {
				arrayData = new Dataset();
				liveVehicleStatusData = new ArrayList<Dataset>();
				vehicleStatus = tripDetailsList.get(i).getDynamicTripStatus();
				vehicleName = tripDetailsList.get(i).getVehicle().getDisplayName();
				arrayData.put("vehiclename", vehicleName);
				String add=vehicleStatus.getLocationString();
				if(add != null){
					arrayData.put("location",add);
				}else{
					arrayData.put("location","No Location data");
				}
				veh = tripDetailsList.get(i).getVehicle();
				LOG.debug("Speed from LiveVehicleStatus "+vehicleStatus.getMaxSpeed());
				arrayData.put("speed", vehicleStatus.getMaxSpeed()+"");
				arrayData.put("status",veh.getStatus(veh.getImei()));
				arrayData.put("charger",vehicleStatus.isChargerConnected()+"");
				arrayData.put("seatbelt","SeatBelt Alert Not Available");
				arrayData.put("ignition","Ignition Alert Not Available");
				liveVehicleStatusData.add((Dataset) arrayData);
				data.put("vehicle-" + veh.getId().getId(), liveVehicleStatusData);
			}
		}
		LOG.debug("Returning data for LiveVehicleStatusManager from cache");
		return data;
	}

	@Override
	public IDataset getDataForDashboard() {
		IDataset data = new Dataset();
		IDataset dashboardData = null;
		ArrayList<Dataset> liveVehicleStatusData = null;


		/* Vishnu : Fetching information from cache */
		LOG.debug("Attempting to fetch data for LiveVehicleStatusManager::getDataForDashboard from cache");
		TripDetailsDaoImpl tripDetailsDaoImpl = (TripDetailsDaoImpl)DBManager.getInstance().getDao(DAOEnum.TRIP_DETAILS_DAO);
		List<TripDetails> tripDetailsList = tripDetailsDaoImpl.getActiveTripDetailsWithLiveStatusForTheUserForDashboard(new LongPrimaryKey(SessionUtils.getCurrentlyLoggedInUser().getId()));

		LiveVehicleStatus vehicleStatus = null;
		String vehicleName = null;
		Vehicle veh = new Vehicle();
		if (null != tripDetailsList) {
			for (int i = 0; i < tripDetailsList.size(); i++) {
				dashboardData = new Dataset();
				liveVehicleStatusData = new ArrayList<Dataset>();
				vehicleStatus = tripDetailsList.get(i).getDynamicTripStatus();
				vehicleName = tripDetailsList.get(i).getVehicle().getDisplayName();
				dashboardData.put("vehiclename", vehicleName);
				dashboardData.put("location","Lat:"+vehicleStatus.getLocation().getFirstPoint().getX()+","+"Lon:"+vehicleStatus.getLocation().getFirstPoint().getY());
				veh = tripDetailsList.get(i).getVehicle();
				dashboardData.put("speed", vehicleStatus.getMaxSpeed()+"");
				dashboardData.put("status",veh.getStatus(veh.getImei()));
				dashboardData.put("charger",vehicleStatus.isChargerConnected()+"");
				dashboardData.put("seatbelt"," Not Available");
				dashboardData.put("ignition","Not Available");
				liveVehicleStatusData.add((Dataset) dashboardData);
				data.put("vehicle-" + veh.getId().getId(), liveVehicleStatusData);
			}
		}
		return data;
	}
}