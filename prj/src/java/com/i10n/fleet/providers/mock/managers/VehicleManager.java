package com.i10n.fleet.providers.mock.managers;

import java.util.List;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.EmriVehiclesBaseStationDaoImp;
import com.i10n.db.dao.TripDetailsDaoImpl;
import com.i10n.db.dao.VehicleDaoImpl;
import com.i10n.db.entity.EmriVehiclesBaseStation;
import com.i10n.db.entity.TripDetails;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadEmriVehiclesBaseStationDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.util.Helper;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * Mock : Vehicle Manager : Manages vehicle data.This class will be removed in
 * future
 * 
 * @author Sabarish
 * 
 */
public class VehicleManager extends AbstractDelegatedDataManager implements IVehicleManager {

	private static Logger LOG = Logger.getLogger(VehicleManager.class);

	private static final String FILE_DOCUMENT = "/mock/vehicles.xml";
	private IDataset m_dataset = null;
	IDataset result = new Dataset();
	String startdate = null;
	String enddate = null;

	/**
	 * @see IVehicleManager#getData(IDataset)
	 */
	public IDataset getData(IDataset options) {
		LOG.debug("In Vehiclemanager getdata");
		startdate = options.getValue("startdate");
		enddate = options.getValue("enddate");
		if (m_dataset == null) {
			result = parseDataset();
		}
		return result;
	}

	public IDataset getParseDataset(IDataset options) {
		LOG.debug("In vehiclemanager getParsedataset starting ");
		IDataset vehicleData = new Dataset();
		startdate = options.getValue("startdate");
		enddate = options.getValue("enddate");
		String location;

		/* Vishnu : Fetching information from cache */
		LOG.debug("Attempting to fetch data for VehicleManager::getParseDataset from cache");
		TripDetailsDaoImpl tripDetailsDaoImpl = (TripDetailsDaoImpl)DBManager.getInstance().getDao(DAOEnum.TRIP_DETAILS_DAO);
		List<TripDetails> tripDetailsList = tripDetailsDaoImpl.getActiveTripDetailsWithLiveStatusForTheUser(new LongPrimaryKey(SessionUtils.getCurrentlyLoggedInUser().getId()));

		Vehicle vehicle = null;
		Long vehicleId = null;
		if (tripDetailsList.size() != 0) {
			for (int i = 0; i < tripDetailsList.size(); i++) {
				vehicle = tripDetailsList.get(i).getVehicle();
				vehicleId = vehicle.getId().getId();
				vehicleData.put("vehicle-" + vehicleId + ".id", "vehicle-"+ vehicleId);
				vehicleData.put("vehicle-" + vehicleId + ".name", vehicle.getDisplayName());
				vehicleData.put("vehicle-" + vehicleId + ".make", vehicle.getMake());
				vehicleData.put("vehicle-" + vehicleId + ".model", vehicle.getModel());
				vehicleData.put("vehicle-" + vehicleId + ".groupid", "group-"+ vehicle.getGroupId());
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".status",vehicle.getStatus(vehicle.getImei()));
				EmriVehiclesBaseStation cachedEmriRajasthan =null;
				if(Boolean.parseBoolean(EnvironmentInfo.getProperty("IS_FRS_EMRI_CLIENT"))){
					List<EmriVehiclesBaseStation> emriRajasthanList = ((EmriVehiclesBaseStationDaoImp)DBManager.getInstance().getDao(DAOEnum.EMRI_RAJASTHAN_DAO)).
							selectByVehicleId(vehicle.getId().getId());
					if(emriRajasthanList != null){
						LOG.debug("EMRI Rajasthan List Size "+emriRajasthanList.size());
						if(emriRajasthanList.size()!=0){
							LOG.debug("In EMRI Rajastjan List"+emriRajasthanList.size());
							cachedEmriRajasthan = LoadEmriVehiclesBaseStationDetails.getInstance().retrieve(emriRajasthanList.get(0).getVehicleId());	
							LOG.debug("Cached Rajasthan "+cachedEmriRajasthan);
						}
					}
					if(cachedEmriRajasthan != null){
						LOG.debug("District in:"+cachedEmriRajasthan.getDistrict());
						LOG.debug("Dispaly Name "+vehicle.getDisplayName());
						vehicleData.put("vehicle-" + vehicleId + ".displaydata",vehicle.getDisplayName());
						vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".district",cachedEmriRajasthan.getDistrict()+"");
						vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".baselocation",cachedEmriRajasthan.getBaseLocation());
						vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".crewno",cachedEmriRajasthan.getCrewNo());
					}else{
						LOG.debug("District :"+"Not Available");
						LOG.debug("Dispaly Name "+vehicle.getDisplayName());
						vehicleData.put("vehicle-" + vehicleId + ".displaydata",vehicle.getDisplayName());
						vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".district","Not Available");
						vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".baselocation","Not Available");
						vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".crewno", 0);
					}
				}
				else{
					location = "No Location Available";
					vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".groupname", vehicle.getVehicleGroupValue());
					vehicleData.put("vehicle-" + vehicleId + ".location", location);
					vehicleData.put("vehicle-" + vehicleId + ".speed",vehicle.getMaxSpeed());
					if(vehicle.getDriverFirstName() != null) {
						if(vehicle.getDriverLastName() !=  null){ 
							vehicleData.put("vehicle-" + vehicleId + ".drivername", vehicle.getDriverFirstName()+ " " + vehicle.getDriverLastName());
						}else {
							vehicleData.put("vehicle-" + vehicleId + ".drivername", vehicle.getDriverFirstName());
						}
					} else{
						vehicleData.put("vehicle-" + vehicleId + ".drivername", "" );
					}
					if(vehicle.getDriverFirstName() != null){
						LOG.debug("vehicle-" + vehicleId + ".displaydata"+vehicle.getDisplayName() + "[" + vehicle.getDriverFirstName()+ "]");
						vehicleData.put("vehicle-" + vehicleId + ".displaydata",vehicle.getDisplayName() + "[" + vehicle.getDriverFirstName()+ "]");
						LOG.debug("vehicle-" + vehicleId + ".displaydata"+vehicle.getDisplayName() + "[" + vehicle.getDriverFirstName()+ "]");
					} else {
						LOG.debug("In else Display Name "+vehicle.getDisplayName());
						vehicleData.put("vehicle-" + vehicleId + ".displaydata",vehicle.getDisplayName());
					}

					vehicleData.put("vehicle-" +vehicleId + ".icon",vehicle.getVehicleIconPicId());
				}
				if(vehicle.getDriverLastName() != null) {
					LOG.debug("Mobile Number is:"+vehicle.getDriverLastName());
					vehicleData.put("vehicle-" + vehicleId + ".mobilenumber", vehicle.getDriverLastName());
				} else{
					LOG.debug("Mobile Number is:"+0);
					vehicleData.put("vehicle-" + vehicleId + ".mobilenumber", 0 );
				}
			}

		}

		LOG.debug("In vehiclemanager getParsedataset ending ");
		Helper.printDataSet(vehicleData);
		return vehicleData;
	}

	public IDataset getVacantVehiclesData(IDataset options) {

		/*IDataset vehicleData = new Dataset();
		List<Vehicle> resultset = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).getVacantVehicles(SessionUtils.getCurrentlyLoggedInUser().getId());
		Vehicle vehicle = null;
		if (null != resultset) {
			for (int i = 0; i < resultset.size(); i++) {
				vehicle = resultset.get(i);

				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".id","vehicle-" + vehicle.getId().getId());
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".name",vehicle.getDisplayName());
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".make",vehicle.getMake());
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".model",vehicle.getModel());
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".groupid", "group-" + vehicle.getGroupId());

				List<GroupValues> groupValueresultset = ((GroupValuesDaoImpl) DBManager.getInstance().getDao(DAOEnum.GROUP_VALUES_DAO))
						.selectByPrimaryKey(new LongPrimaryKey(vehicle.getGroupId()));
				GroupValues groupValues = groupValueresultset.get(0);

				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".groupname", groupValues.getGroupValue());
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".location", "Bommanahalli");
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".speed",65 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".driverid", "driver-121");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".drivername", "Driver 121");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".driverstatus", "offline");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".driverfirstname", "Driver 121");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".driverlastname", "Driver 121");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".driverlicense", "license-121");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".drivermaxspeed", 67 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".driveravgspeed", 65 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".driverdistance", 61 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".driverassigned", "true");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".drivergroupid", "group-" + vehicle.getGroupId());
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".drivergroupname", groupValues.getGroupValue());
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".status",vehicle.getStatus(vehicle.getId().getId()));
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".icon","bike");
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".lat",12.932283 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".lon",77.159281 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".chargerdc", "true");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".startlocation", "koramangala");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".startfuel", 3 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".distance", 61 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".maxspeed", 67 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".imei",vehicle.getImeiid());
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".year",2010 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".geofenceid", "");
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".gps",1 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".gsm",1 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".battery", "0");
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".fuel","3");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".assigned", "true");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".lastupdated", "Wed Oct 07i 15:53:26 IST 2009");
			}
		}
		return vehicleData;*/
		return null;
	}

	/**
	 * parses the document and sets the global dataset to the parsed data
	 */
	private IDataset parseDataset() {
		LOG.debug("In vehicle manager parseDataset starting");
		IDataset vehicleData = new Dataset();
		String location;

		/* Vishnu : Fetching information from cache */
		LOG.debug("Attempting to fetch data for VehicleManager::parseDataset from cache");
		TripDetailsDaoImpl tripDetailsDaoImpl = (TripDetailsDaoImpl)DBManager.getInstance().getDao(DAOEnum.TRIP_DETAILS_DAO);
		List<TripDetails> tripDetailsList = tripDetailsDaoImpl.getActiveTripDetailsWithLiveStatusForTheUser(new LongPrimaryKey(SessionUtils.getCurrentlyLoggedInUser().getId()));


		Vehicle vehicle = null;
		if (tripDetailsList.size() != 0 ) {
			for (int i = 0; i < tripDetailsList.size(); i++) {
				vehicle = tripDetailsList.get(i).getVehicle();
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".id","vehicle-" + vehicle.getId().getId());
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".name",vehicle.getDisplayName());
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".make",vehicle.getMake());
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".model",vehicle.getModel());
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".groupid", "group-" + vehicle.getGroupId());
				location = "No Location Available";

				//				if(!DataProcessorParameters.IS_ADDRESS_FETCH_ENABLED){
				//					LOG.debug("Location fetch is disabled");
				//					location = "No Location Available";
				//				}else{
				//					LOG.debug("Location fetch is enabled");
				//					double x = vehicle.getVehicleLocation().getFirstPoint().getY();
				//					double y = vehicle.getVehicleLocation().getFirstPoint().getX();
				//
				//					Address locationFetch = GeoUtils.fetchNearestLocation(x, y);
				//					if(locationFetch != null){
				//						location = locationFetch.toString();
				//					}else{
				//						location = "No Location Available";
				//					}
				//				}
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".groupname", vehicle.getVehicleGroupValue());
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".location", location);
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".speed",vehicle.getMaxSpeed());
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".driverid", vehicle.getDriverId());
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".drivername", vehicle.getDriverFirstName()+" "+vehicle.getDriverLastName());
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".driverstatus", "offline");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".driverfirstname", vehicle.getDriverFirstName());
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".driverlastname", vehicle.getDriverLastName());
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".driverlicense", "1" );
				LOG.debug("MaxSpeed "+vehicle.getMaxSpeed()+" For Imei is "+vehicle.getImei());
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".drivermaxspeed", vehicle.getMaxSpeed());
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".driveravgspeed", vehicle.getMaxSpeed());
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".driverdistance", 61 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".driverassigned", "true");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".drivergroupid", "group-" + vehicle.getDriverGroupId());
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".drivergroupname", vehicle.getDriverGroupValue());
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".status",vehicle.getStatus(vehicle.getImei()));
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".icon","bike");
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".lat",12.932283 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".lon",77.159281 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".chargerdc", "true");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".startlocation", "koramangala");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".startfuel", 3 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".distance", 61 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".maxspeed", 67 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".imei",vehicle.getImeiId());
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".year",2010 + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".geofenceid", "");
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".gps",gpsSignalLevel(vehicle.getGpsStrength())+ "");
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".gsm",gsmSignalLevel(vehicle.getGsmStrength())+ "");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".battery", batterySignalLevel(vehicle.getBatteryVoltage())+ "");
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".fuel",fuelSignalLevel(vehicle.getFuelAd()) + "");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".assigned", "true");
				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".lastupdated", vehicle.getLastUpdatedAt()+ "");
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".trip","trip-vehicle-"+ vehicle.getTripId());
			}
		}
		LOG.debug("In vehicle manager parseDataset Ending");
		return vehicleData;
	}

	public int gpsSignalLevel(float sLevel) {
		int level = 1;
		if (sLevel >= 0.1 && sLevel <= 0.9) {
			level = 3;
		} else if (sLevel >= 1.0 && sLevel <= 1.3) {
			level = 2;
		} else if (sLevel >= 1.4 && sLevel <= 1.5) {
			level = 1;
		} else {
			level = 0;
		}
		return level;
	}

	public int gsmSignalLevel(float sLevel) {
		int level = 1;
		if (sLevel > 30) {
			level = 3;
		} else if (sLevel >= 25.0 && sLevel <= 30.0) {
			level = 2;
		} else if (sLevel >= 20.0 && sLevel < 25.0) {
			level = 1;
		} else {
			level = 0;
		}
		return level;
	}

	public int batterySignalLevel(float sLevel) {
		int level = 1;
		if (sLevel > 4000.00) {
			level = 3;
		} else if (sLevel >= 3900.00 && sLevel <= 4000.00) {
			level = 2;
		} else if (sLevel >= 3700.00 && sLevel < 3900.00) {
			level = 1;
		} else {
			level = 0;
		}
		return level;
	}

	public int fuelSignalLevel(int sLevel) {
		int level = 2;
		if (sLevel > 1000) {
			level = 3;
		} else if (sLevel >= 700 && sLevel <= 1000) {
			level = 2;
		} else if (sLevel >= 500 && sLevel <= 700) {
			level = 1;
		} else {
			level = 0;
		}
		return level;
	}

	/**
	 * @see AbstractDataManager#getDocumentName()
	 */
	protected String getDocumentName() {
		return FILE_DOCUMENT;
	}

	/**
	 * @see AbstractDelegatedDataManager#getDataID()
	 */
	@Override
	protected String getDataID() {
		return "vehicle";
	}

	/**
	 * This method will return the Vacant vehicle Date to the dashboard
	 */
	@Override
	public IDataset getVacantVehicleForDashBoard() {

		IDataset vehicleData = new Dataset();
		List<Vehicle> resultset = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).getVacantVehicles(SessionUtils.getCurrentlyLoggedInUser().getId());
		Vehicle vehicle = null;
		if (null != resultset) {
			for (int i = 0; i < resultset.size(); i++) {
				vehicle = resultset.get(i);
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".id","vehicle-" + vehicle.getId().getId());
				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".name",vehicle.getDisplayName());
				//				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".make",vehicle.getMake());
				//				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".model",vehicle.getModel());
				//				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".groupid", "group-" + vehicle.getGroupId());
				//				vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".location", "Bommanahalli");
				//				vehicleData.put("vehicle-" + vehicle.getId().getId() + ".speed",65 + "");
			}
		}
		return vehicleData;

	}
	/**
	 * This method will return the data to the vehicleStatus in dashboard
	 */
	@Override
	public IDataset getStatusDataForDashboard() {
		IDataset vehicleData = new Dataset();
		/* Vishnu : Fetching information from cache */
		LOG.debug("Attempting to fetch status data for VehicleManager::parseDataset from cache");
		TripDetailsDaoImpl tripDetailsDaoImpl = (TripDetailsDaoImpl)DBManager.getInstance().getDao(DAOEnum.TRIP_DETAILS_DAO);
		List<TripDetails> tripDetailsList = tripDetailsDaoImpl.getActiveTripDetailsWithLiveStatusForTheUserForDashboard(new LongPrimaryKey(SessionUtils.getCurrentlyLoggedInUser().getId()));

		Vehicle vehicle = null;
		for(TripDetails trip : tripDetailsList) {
			vehicle = trip.getVehicle();
			vehicleData.put("vehicle-" + vehicle.getId().getId() + ".id","vehicle-" + vehicle.getId().getId());
			vehicleData.put("vehicle-" + vehicle.getId().getId() + ".name",vehicle.getDisplayName());
			vehicleData.put("vehicle-" + vehicle.getId().getId() + ".make",vehicle.getMake());
			vehicleData.put("vehicle-" + vehicle.getId().getId() + ".model",vehicle.getModel());
			vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".groupid", "group-" + vehicle.getGroupId());
			vehicleData.put("vehicle-" + vehicle.getId().getId() + ".status",vehicle.getStatus(vehicle.getImei()));
		}
		return vehicleData;
	}

	/**
	 * This Method will return the data to the vehicleHealth widget in dashboard
	 */

	@Override
	public IDataset getVehicleHealthDataForDahsboard() {
		IDataset vehicleData = new Dataset();
		/* Vishnu : Fetching information from cache */
		LOG.debug("Attempting to fetch health data for VehicleManager::parseDataset from cache");
		TripDetailsDaoImpl tripDetailsDaoImpl = (TripDetailsDaoImpl)DBManager.getInstance().getDao(DAOEnum.TRIP_DETAILS_DAO);
		List<TripDetails> tripDetailsList = tripDetailsDaoImpl.getActiveTripDetailsWithLiveStatusForTheUserForDashboard(new LongPrimaryKey(SessionUtils.getCurrentlyLoggedInUser().getId()));

		Vehicle vehicle = null;
		for(TripDetails trip : tripDetailsList) {
			vehicle = trip.getVehicle();
			vehicleData.put("vehicle-" + vehicle.getId().getId() + ".id","vehicle-" + vehicle.getId().getId());
			vehicleData.put("vehicle-" + vehicle.getId().getId() + ".name",vehicle.getDisplayName());
			vehicleData.put("vehicle-" + vehicle.getId().getId() + ".make",vehicle.getMake());
			vehicleData.put("vehicle-" + vehicle.getId().getId() + ".model",vehicle.getModel());
			vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".groupid", "group-" + vehicle.getGroupId());
			vehicleData.put("vehicle-" + vehicle.getId().getId() + ".gps",gpsSignalLevel(vehicle.getGpsStrength())+ "");
			vehicleData.put("vehicle-" + vehicle.getId().getId() + ".gsm",gsmSignalLevel(vehicle.getGsmStrength())+ "");
			vehicleData.put("vehicle-" + vehicle.getId().getId()+ ".battery", batterySignalLevel(vehicle.getBatteryVoltage())+ "");
			vehicleData.put("vehicle-" + vehicle.getId().getId() + ".fuel",fuelSignalLevel(vehicle.getFuelAd()) + "");
		}
		return vehicleData;
	}
}
