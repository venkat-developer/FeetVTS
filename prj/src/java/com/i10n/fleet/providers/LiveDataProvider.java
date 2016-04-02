package com.i10n.fleet.providers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.EmriVehiclesBaseStationDaoImp;
import com.i10n.db.dao.TripDetailsDaoImpl;
import com.i10n.db.entity.Address;
import com.i10n.db.entity.Driver;
import com.i10n.db.entity.EmriVehiclesBaseStation;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.TripDetails;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadEmriVehiclesBaseStationDetails;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;
import com.i10n.dbCacheManager.LoadVehicleDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.hashmaps.EMRIHashMaps.ButtonCode;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.util.Constants;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.util.GeoUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.utils.SessionUtils;
import com.i10n.mina.utils.Utils;

/**
 * Class to provide the livedata on pool request.
 * @author venkat
 *
 */
public class LiveDataProvider implements IDataProvider {

	public static Logger LOG = Logger.getLogger(LiveDataProvider.class);

	@Override
	public IDataset getDataset(RequestParameters params) {
		String kmlFilesRequest=params.getRequestParameter("kmlFolderSize");
		IDataset finalresult = new Dataset();
		List<IDataset> result=new ArrayList<IDataset>();
		if(kmlFilesRequest!=null){
			String directoryPath=System.getenv("CATALINA_HOME").replace(";", "");
			File folder = new File(directoryPath+"/webapps/kml/");
			File[] listOfFiles = folder.listFiles();
			for(int i=0;i<folder.listFiles().length;i++){
				IDataset tripDetailsMap = new Dataset();
				if(listOfFiles[i].isFile()){
					if (listOfFiles[i].getName().endsWith(".kml")){
						tripDetailsMap.put("size",folder.listFiles().length);
						tripDetailsMap.put("fileName",listOfFiles[i].getName()+"");
						result.add(tripDetailsMap);
					}
				}
			}	
			finalresult.put("liveData",result);
		}else{
			TripDetailsDaoImpl tripDetailsDaoImpl = (TripDetailsDaoImpl)DBManager.getInstance().getDao(DAOEnum.TRIP_DETAILS_DAO);
			if(SessionUtils.getCurrentlyLoggedInUser()!=null){
				LongPrimaryKey userId=new LongPrimaryKey(SessionUtils.getCurrentlyLoggedInUser().getId());	
				List<TripDetails> tripDetailsList = tripDetailsDaoImpl.getActiveTripDetailsWithLiveStatusForTheUserForLiveData(userId);
				HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
				String localTimeZone = (String)WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_TIMEZone);
				if(tripDetailsList.size() != 0){
					for(int tripData =tripDetailsList.size()-1; tripData >= 0; tripData--){
						IDataset tripDetailsMap = new Dataset();
						TripDetails tripDetails = tripDetailsList.get(tripData);
						Vehicle vehicle=tripDetails.getVehicle();
						String statusofVeh = vehicle.getStatus(vehicle.getImei());
						if(statusofVeh.equalsIgnoreCase("offline") || statusofVeh.equalsIgnoreCase("offroad")){
							// Skip sending data for offline and offroad vehicles
							continue;
						}
						Driver driver=tripDetails.getDriver();
						tripDetailsMap.put("status", statusofVeh+"");
						tripDetailsMap.put("vehicleId", vehicle.getId().getId());
						tripDetailsMap.put("name", vehicle.getDisplayName() + "\n[" + driver.getFirstName() +"]");
						tripDetailsMap.put("make", vehicle.getMake()+"");
						tripDetailsMap.put("model", vehicle.getModel()+"");
						tripDetailsMap.put("drivername", driver.getFirstName()+"");
						tripDetailsMap.put("groupid", "group-" + vehicle.getGroupId());
						/* Fetch Group Name from Cache */
						Vehicle cachedVehicle = LoadVehicleDetails.getInstance().retrieve(vehicle.getId().getId());
						if (cachedVehicle!=null) {
							tripDetailsMap.put("groupname", cachedVehicle.getGroupName()+"");
						}else{
							tripDetailsMap.put("groupname", "default");
						}
						LiveVehicleStatus liveObject = LoadLiveVehicleStatusRecord.getInstance().retrieveByTripId(tripDetails.getId().getId());
						double a = liveObject.getLocation().getFirstPoint().y;
						double b = liveObject.getLocation().getFirstPoint().x;
						StringBuffer location=new StringBuffer();
						if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_PUSHLET_ENABLED"))){
							Address locationFetch = GeoUtils.fetchNearestLocation(a, b,false);
							location=StringUtils.formulateAddress(locationFetch,vehicle.getId().getId(),a,b);
						}else{
							location.append("Trip ");
							location.append(StringUtils.addressFetchDisabled(vehicle.getId().getId(),a,b).toString());
						}
//						location.append(a+":"+b);
						tripDetailsMap.put("speed", Utils.doubleForDisplay(liveObject.getMaxSpeed())+"");
						tripDetailsMap.put("maxspeed", Utils.doubleForDisplay(liveObject.getMaxSpeed())+"");
						tripDetailsMap.put("course", liveObject.getCourse()+"");
						tripDetailsMap.put("imeino", liveObject.getImei());
						tripDetailsMap.put("icon", vehicle.getVehicleIconPicId()+"");
						tripDetailsMap.put("lat", liveObject.getLocation().getFirstPoint().getY()+"");
						tripDetailsMap.put("lon", liveObject.getLocation().getFirstPoint().getX()+"");
						tripDetailsMap.put("cc", liveObject.isChargerConnected()? "Yes" : "No");
						tripDetailsMap.put("gps", StringUtils.getGPSSS(liveObject.getGpsStrength()));
						tripDetailsMap.put("gsm", StringUtils.getGSMSS(liveObject.getGsmStrength()));
						tripDetailsMap.put("battery", StringUtils.getBatteryStrength(liveObject.getBatteryVoltage()));
						
						EmriVehiclesBaseStation cachedEmriRajasthan = null;

						if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_FRS_EMRI_CLIENT"))){
							List<EmriVehiclesBaseStation> emriList = ((EmriVehiclesBaseStationDaoImp)DBManager.getInstance().
									getDao(DAOEnum.EMRI_RAJASTHAN_DAO)).selectByVehicleId(vehicle.getId().getId());
							if(emriList != null && emriList.size() > 0){
								Long emriRajasthanId = emriList.get(0).getID().getId();
								cachedEmriRajasthan = LoadEmriVehiclesBaseStationDetails.getInstance().retrieve(emriRajasthanId);
								LOG.debug("vehicleId: "+vehicle.getId().getId());
							}
							LOG.info("Latest Button Pressed:"+liveObject.getLatestButtonPressed());
							LOG.info("Button Code is:"+ButtonCode.get(liveObject.getLatestButtonPressed()));
							tripDetailsMap.put("latestbuttonpressed", ButtonCode.get(liveObject.getLatestButtonPressed())+"");
								
						} else {
							tripDetailsMap.put("latestbuttonpressed", ButtonCode.get(liveObject.getLatestButtonPressed())+"");
						}
						if(cachedEmriRajasthan != null){
							LOG.debug("District in:"+cachedEmriRajasthan.getDistrict());
							tripDetailsMap.put("district",cachedEmriRajasthan.getDistrict());
							tripDetailsMap.put("baselocation",cachedEmriRajasthan.getBaseLocation());
							tripDetailsMap.put("crewno", cachedEmriRajasthan.getCrewNo());
						}else{
							LOG.debug("District :"+"Not Available");
							tripDetailsMap.put("district","Not Available");
							tripDetailsMap.put("baselocation","Not Available");
							tripDetailsMap.put("crewno", 0);
						}
						tripDetailsMap.put("fuel", 3);
						tripDetailsMap.put("assigned", true);
						long actualTime = DateUtils.adjustToClientTimeInMilliSeconds(localTimeZone, liveObject.getLastUpdatedAt());
						String actualDateTime = DateUtils.adjustToClientTime(localTimeZone, liveObject.getLastUpdatedAt());
						long moduleupdatetime = DateUtils.adjustToClientTimeInMilliSeconds(localTimeZone, liveObject.getModuleUpdateTime());
						tripDetailsMap.put("lastupdated", actualDateTime+"");
						tripDetailsMap.put("timeinmilliseconds", actualTime+"");
						tripDetailsMap.put("location", location.toString());
						tripDetailsMap.put("moduleupdatetime", moduleupdatetime+"");
						result.add(tripDetailsMap);
					}
					finalresult.put("liveData", result);
				}
			}
		}
		return finalresult;
	}

	@Override
	public String getName() {
		return "livedata";
	}

}