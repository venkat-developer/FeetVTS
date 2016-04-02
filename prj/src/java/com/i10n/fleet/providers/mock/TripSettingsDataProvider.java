package com.i10n.fleet.providers.mock;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.DriverDaoImp;
import com.i10n.db.dao.TripDaoImp;
import com.i10n.db.dao.VehicleDaoImpl;
import com.i10n.db.entity.Address;
import com.i10n.db.entity.Driver;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.Trip;
import com.i10n.db.entity.User;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadDriverDetails;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;
import com.i10n.dbCacheManager.LoadTripDetails;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.util.Constants;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.util.GeoUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * Mock : Data Provider for Trip Settings. This class will be removed in future.
 * 
 * @author Sabarish
 * 
 */
public class TripSettingsDataProvider implements IDataProvider {

	private static Logger LOG = Logger.getLogger(TripSettingsDataProvider.class);
	
	/**
	 * @see IDataProvider#getDataset(RequestParameters)
	 */
	
	public IDataset getDataset(RequestParameters params) {
		LOG.debug("Getting data for Tripsettings");
		IDataset result = new Dataset();
		IDataset tripDetailDataset = null;
		String tripIdString = params.getRequestParameter("tripID");
		Trip trip = null;

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String localTimeZone = (String) WebUtils.getSessionAttribute(request,Constants.SESSION.ATTR_TIMEZone);

		if (tripIdString != null) {
			tripIdString = StringUtils.getValueFromKVP(tripIdString);
			Long tripId = Long.parseLong(StringUtils.stripCommas(tripIdString));
			tripDetailDataset = new Dataset();
			trip = LoadTripDetails.getInstance().retrieve(tripId);

			tripDetailDataset.put("trip-" + trip.getId().getId() + ".id", trip.getId().getId()+ "");
			tripDetailDataset.put("trip-" + trip.getId().getId()+ ".name", trip.getTripName());
			
			Vehicle vehicle = LoadVehicleDetails.getInstance().retrieve(trip.getVehicleId());

			tripDetailDataset.put("trip-" + trip.getId().getId()+ ".vehiclemake", vehicle.getMake() + "");
			tripDetailDataset.put("trip-" + trip.getId().getId()+ ".vehiclemodel", vehicle.getModel() + "");

			Driver driver = LoadDriverDetails.getInstance().retrieve(trip.getDriverId());

			tripDetailDataset.put("trip-" + trip.getId().getId()+ ".drivername", driver.getFirstName());
			tripDetailDataset.put("trip-" + trip.getId().getId()+ ".driverlastname", driver.getLastName());
			tripDetailDataset.put("trip-" + trip.getId().getId()+ ".startdate", trip.getStartDate() + "");

			LiveVehicleStatus liveVehicle = LoadLiveVehicleStatusRecord.getInstance().retrieveByTripId(trip.getId().getId());
			if(liveVehicle == null){
				LoadLiveVehicleStatusRecord.getInstance().refresh();
				liveVehicle = LoadLiveVehicleStatusRecord.getInstance().retrieveByTripId(trip.getId().getId());
			}
			Address add = null;
			
			tripDetailDataset.put("trip-" + trip.getId().getId()+ ".location", "No trip location");
			if(liveVehicle != null){
				long actualTime = DateUtils.adjustToClientTimeInMilliSeconds(localTimeZone, liveVehicle.getLastUpdatedAt());
				tripDetailDataset.put("trip-" + trip.getId().getId()+ ".lastupdated", actualTime);
				if (Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_TRIP_SETTINGS_ENABLED"))) {
					double latitude = liveVehicle.getLocation().getFirstPoint().getY();
					double longitude = liveVehicle.getLocation().getFirstPoint().getX();
					add = GeoUtils.fetchNearestLocation(latitude, longitude,false);
					StringBuffer location= StringUtils.formulateAddress(add, trip.getVehicleId(), latitude, longitude);
					tripDetailDataset.put("trip-" + trip.getId().getId()+ ".location", location.toString());
				}
			} else{
				tripDetailDataset.put("trip-" + trip.getId().getId()+ ".lastupdated", "");
			}
			tripDetailDataset.put("trip-" + trip.getId().getId()+ ".distance", trip.getCumulativeDistance() + "");

			String status = "";

			if ((trip.getEndDate() != null) && (!trip.isActiveTrip())) {
				status = "stopped";
			} else if ((trip.getEndDate() == null) && (trip.isActiveTrip())) {
				status = "started";
			} else if ((trip.getEndDate() == null) && (!trip.isActiveTrip())) {
				status = "paused";
			} else if ((trip.getEndDate() != null) && (trip.isActiveTrip())) {
				status = "stopped";
			}

			tripDetailDataset.put("trip-" + trip.getId().getId()+ ".status", status);
			tripDetailDataset.put("trip-" + trip.getId().getId()+ ".maxspeed", trip.getSpeedLimit() + "");
			tripDetailDataset.put("trip-" + trip.getId().getId()+ ".idlepointlimit", trip.getIdlePointsTimeLimit() + "");
			result.put("trips", tripDetailDataset);
		} else {
			tripDetailDataset = new Dataset();
			List<Trip> resultset = ((TripDaoImp) DBManager.getInstance().getDao(DAOEnum.TRIP_DAO)).selectAllActiveTrips();
			for (int i = 0; i < resultset.size(); i++) {
				trip = resultset.get(i);

				tripDetailDataset.put("trip-" + trip.getId().getId()+ ".id", trip.getId().getId() + "");
				tripDetailDataset.put("trip-" + trip.getId().getId()+ ".name", trip.getTripName());
				tripDetailDataset.put("trip-" + trip.getId().getId()+ ".vehiclemake", "");
				tripDetailDataset.put("trip-" + trip.getId().getId()+ ".vehiclemodel", "");
				tripDetailDataset.put("trip-" + trip.getId().getId()+ ".drivername", "");
				tripDetailDataset.put("trip-" + trip.getId().getId()+ ".driverlastname", "");
				tripDetailDataset.put("trip-" + trip.getId().getId()+ ".startdate", "");
				tripDetailDataset.put("trip-" + trip.getId().getId()+ ".location", "");
				tripDetailDataset.put("trip-" + trip.getId().getId()+ ".lastupdated", "");
				tripDetailDataset.put("trip-" + trip.getId().getId()+ ".distance", "");
				tripDetailDataset.put("trip-" + trip.getId().getId()+ ".status", "");
				tripDetailDataset.put("trip-" + trip.getId().getId()+ ".maxspeed", "");
				tripDetailDataset.put("trip-" + trip.getId().getId()+ ".idlepointlimit", "");
			}
			result.put("trips", tripDetailDataset);
		}

		IDataset vehicleData = null;
		User user = SessionUtils.getCurrentlyLoggedInUser();
		Long userId = user.getId();
		List<Vehicle> vacantVehicleResultset = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).getVacantVehicles(userId);

		Vehicle vehicle = null;

		vehicleData = new Dataset();
		for (int i = 0; i < vacantVehicleResultset.size(); i++) {
			vehicle = vacantVehicleResultset.get(i);
			vehicleData.put("vehicle-" + vehicle.getId().getId() + ".id","vehicle-" + vehicle.getId().getId());
			vehicleData.put("vehicle-" + vehicle.getId().getId() + ".name",vehicle.getDisplayName());
		}
		result.put("vehicles.vacant", vehicleData);

		List<Driver> vacantDriverResultset = ((DriverDaoImp) DBManager.getInstance().getDao(DAOEnum.DRIVER_DAO)).getVacantDrivers(userId);
		IDataset driverData = null;
		Driver driver = null;

		driverData = new Dataset();
		for (int i = 0; i < vacantDriverResultset.size(); i++) {
			driver = vacantDriverResultset.get(i);
			driverData.put("driver-" + driver.getId().getId() + ".id", driver.getId().getId()+ "");
			driverData.put("driver-" + driver.getId().getId() + ".name", driver.getFirstName()+ " " + driver.getLastName());
		}
		result.put("drivers.vacant", driverData);
		LOG.debug("Successfully sending data for Tripsettings");
		return result;
	}

	/**
	 * @see IDataProvider#getName()
	 */
	public String getName() {
		return "tripsettings";
	}

}