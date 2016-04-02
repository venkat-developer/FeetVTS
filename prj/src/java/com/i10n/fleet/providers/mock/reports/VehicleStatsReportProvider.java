package com.i10n.fleet.providers.mock.reports;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.TrackHistoryDaoImpl;
import com.i10n.db.dao.TripDetailsDaoImpl;
import com.i10n.db.dao.VehicleDaoImpl;
import com.i10n.db.entity.DateRange;
import com.i10n.db.entity.DriverReport;
import com.i10n.db.entity.GroupValues;
import com.i10n.db.entity.TrackHistory;
import com.i10n.db.entity.TripDetails;
import com.i10n.db.entity.User;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadGroupDetails;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.providers.mock.AbstractGroupedDataProvider;
import com.i10n.fleet.util.CustomCoordinates;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.utils.SessionUtils;
import com.i10n.mina.utils.Utils;

/**
 * Mock : Mock Data Provider for Vehicle Stats Report. This class will be
 * removed in future.
 * 
 * @author Sabarish
 * 
 */
@SuppressWarnings("unused")
public class VehicleStatsReportProvider extends AbstractGroupedDataProvider implements IDataProvider {
	private String startdate;
	private String enddate;
	private String localTime;
	private long vehicleId;
	private DateRange dateRange;
	private Date clientTime;
	private String vehicleName;
	private static Logger LOG = Logger.getLogger(VehicleStatsReportProvider.class);

	/**
	 * @see IDataProvider#getDataset(RequestParameters) This will return the
	 *      total dataset for the vehicle stats page. It involves start
	 *      location,end location.
	 */

	@SuppressWarnings("deprecation")
	@Override
	public IDataset getDataset(RequestParameters params) {
		IDataset vehicledata = new Dataset();
		localTime = params.getRequestParameter("localTime");
		Date clientTime = new Date(localTime);
		startdate = params.getRequestParameter("startdate");
		enddate = params.getRequestParameter("enddate");
		String vehicleid = params.getRequestParameter("vehicleID");
		if(!vehicleid.equals("null")){
			String vehicleidVariable = StringUtils.getValueFromKVP(vehicleid);
			vehicleId = Long.parseLong(StringUtils.stripCommas(vehicleidVariable.trim()));	
		}else{
			vehicleId=0L;
		}
		vehicleName=params.getRequestParameter("vehicleName");
		dateRange=DateUtils.getModeOfReport(localTime,startdate,enddate);
		Boolean hasTrip;
		User user = SessionUtils.getCurrentlyLoggedInUser();
		Long uid = user.getId();
		Vehicle veh = null;
		List<TripDetails> tripList = ((TripDetailsDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRIP_DETAILS_DAO))
					.getActiveTripDetailsWithLiveStatusForTheUserForStats(uid,vehicleName);
		if (tripList != null) {
			for (int i = 0; i < tripList.size(); i++) {
				Long vid=tripList.get(i).getVehicle().getId().getId();
				if (vid != null) {
					veh = tripList.get(i).getVehicle();
					hasTrip = ((VehicleDaoImpl) DBManager.getInstance()
							.getDao(DAOEnum.VEHICLE_DAO)).isInvolvedInTrip(veh.getId().getId());
					if (hasTrip == true) {
						vehicledata.put("vehicle-" + veh.getId().getId() + ".id", "vehicle-"+ veh.getId().getId() + "");
						vehicledata.put("vehicle-" + veh.getId().getId() + ".name", veh.getDisplayName());
						vehicledata.put("vehicle-" + veh.getId().getId() + ".status", "idle");
						vehicledata.put("vehicle-" + veh.getId().getId() + ".imei", veh.getImeiId()+ "");
						vehicledata.put("vehicle-" + veh.getId().getId() + ".year", veh.getModelYear()+ "");
						vehicledata.put("vehicle-" + veh.getId().getId() + ".geofenceid","region-6");
						vehicledata.put("vehicle-" + veh.getId().getId() + ".groupid","groupid-"+ veh.getGroupId()+ "");


						List<TrackHistory> trackEntries = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
								.selectBetweenDates(veh.getId().getId(), dateRange.getStart(), dateRange.getEnd());

						List<DriverReport> statisticsResult = ((TrackHistoryDaoImpl) DBManager.getInstance().getDao(DAOEnum.TRACK_HISTORY_DAO))
								.getAvgAndMaxSpeedAndCumulativeDistanceForVehicle(veh.getId().getId(), dateRange.getStart(), dateRange.getEnd());

						if (statisticsResult.size() != 0 && trackEntries.size() != 0){

							DriverReport element = statisticsResult.get(0);
							GroupValues groupEntity = LoadGroupDetails.getInstance().retrieve(veh.getGroupId());//vehicleGroupValueresultset.get(0);
							// This function will return the start location of the vehicle from the track history table
							TrackHistory firstTrackPoint = trackEntries.get(0);//vehiclestartlocation.get(0);
							double a = firstTrackPoint.getLocation().getFirstPoint().getY();
							double b = firstTrackPoint.getLocation().getFirstPoint().getX();
							StringBuffer startlocation= new StringBuffer();
							startlocation.append(a+":"+b);
							LOG.debug("Start Location Latitude "+a+" Longitude "+b);

							//							if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_VEHICLE_STATS_ENABLED"))){
							//								Address locationFetch = GeoUtils.fetchNearestLocation(a, b, false);
							//								startlocation=StringUtils.formulateAddress(locationFetch,veh.getId().getId(),a, b);
							//							}else {
							//								startlocation.append("STATS ");
							//								startlocation.append(StringUtils.addressFetchDisabled(veh.getId().getId(),a, b).toString());
							//							}
							TrackHistory lastTrackPoint = trackEntries.get(trackEntries.size()-1);//tracklist.get(0);
							double x = lastTrackPoint.getLocation().getFirstPoint().getY();
							double y = lastTrackPoint.getLocation().getFirstPoint().getX();
							LOG.debug("End Location Location Latitude "+x+" Longitude "+y);
							StringBuffer endLoaction=new StringBuffer();
							//							if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_VEHICLE_STATS_ENABLED"))){
							//								Address startLocationFetch = GeoUtils.fetchNearestLocation(a, b, false);
							//								startlocation=StringUtils.formulateAddress(startLocationFetch,veh.getId().getId(),a, b);
							//								Address endLocationFetch = GeoUtils.fetchNearestLocation(x, y,false);
							//								endLoaction=StringUtils.formulateAddress(endLocationFetch, veh.getId().getId(), x, y);
							//							} else {
							//								startlocation.append("STATS ");
							//								startlocation.append(StringUtils.addressFetchDisabled(veh.getId().getId(),a, b).toString());
							//								endLoaction.append("STATS ");
							//								endLoaction.append(StringUtils.addressFetchDisabled(veh.getId().getId(), x, y).toString());
							//							}
							endLoaction.append(x+":"+y);
							String idleDuration=" ";
							if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_ADDRESS_FETCH_VEHICLE_STATS_ENABLED"))){
								if(groupEntity != null){
									vehicledata.put("vehicle-" + veh.getId().getId() + ".groupname",groupEntity.getGroupValue());
								} else {
									vehicledata.put("vehicle-" + veh.getId().getId() + ".groupname","");
								}
								vehicledata.put("vehicle-" + veh.getId().getId()+ ".startlocation", startlocation.toString());
								vehicledata.put("vehicle-" + veh.getId().getId()+ ".starttime", firstTrackPoint.getOccurredat()+"");
								vehicledata.put("vehicle-" + veh.getId().getId() + ".startfuel",firstTrackPoint.getFuel() + "");
								vehicledata.put("vehicle-" + veh.getId().getId() + ".chargerdc",lastTrackPoint.isChargerConnected());
								vehicledata.put("vehicle-" + veh.getId().getId() + ".endlocation",endLoaction.toString());
								vehicledata.put("vehicle-" + veh.getId().getId()+ ".endtime", lastTrackPoint.getOccurredat()+"");
								vehicledata.put("vehicle-" + veh.getId().getId() + ".fuel",lastTrackPoint.getFuel() + "");
								vehicledata.put("vehicle-" + veh.getId().getId() + ".maxspeed", 
										Utils.doubleForDisplay(element.getMaxspeed()) + "");
								vehicledata.put("vehicle-" + veh.getId().getId() + ".avgspeed", 
										Utils.doubleForDisplay(element.getAvgspeed()) + "");
								vehicledata.put("vehicle-" + veh.getId().getId() + ".distance",
										Utils.doubleForDisplay(element.getDistance()) + "");
								vehicledata.put("vehicle-" + veh.getId().getId() + ".maxspeed", Utils.doubleForDisplay(element.getMaxspeed()) + "");
								vehicledata.put("vehicle-" + veh.getId().getId() + ".avgspeed", Utils.doubleForDisplay(element.getAvgspeed()) + "");
								float distance = 0;
								TrackHistory prevTrackHistory = null;
								for(TrackHistory trackEntry : trackEntries){
									if(prevTrackHistory != null){
										float airDistance = (float) CustomCoordinates.distance(prevTrackHistory.getLocation().getFirstPoint().getY(), prevTrackHistory.getLocation().getFirstPoint().getX(),
												trackEntry.getLocation().getFirstPoint().getY(), trackEntry.getLocation().getFirstPoint().getX());
										distance += (airDistance + (0.1 * airDistance));
									}
									prevTrackHistory = trackEntry;
								}
								vehicledata.put("vehicle-" + veh.getId().getId() + ".distance", Utils.doubleForDisplay(distance) + "");
								vehicledata.put("vehicle-" + veh.getId().getId() + ".idleDuration",idleDuration);
							}
						}else {
							vehicledata.put("vehicle-" + veh.getId().getId() + ".fuel", 0+"");
							vehicledata.put("vehicle-" + veh.getId().getId()+ ".starttime", "");
							vehicledata.put("vehicle-" + veh.getId().getId()+ ".endtime", "");
							vehicledata.put("vehicle-" + veh.getId().getId() + ".maxspeed",0.0 + "");
							vehicledata.put("vehicle-" + veh.getId().getId() + ".avgspeed",0.0 + "");
							vehicledata.put("vehicle-" + veh.getId().getId() + ".distance",0.0 + "");
							vehicledata.put("vehicle-" + veh.getId().getId()+ ".idleDuration","Not Available");
							vehicledata.put("vehicle-" + veh.getId().getId() + ".startlocation","No Location Available");
							vehicledata.put("vehicle-" + veh.getId().getId() + ".endlocation","No Location Available");
						}
					}
				}
			}
		}
		IDataset gdata = new Dataset();
		gdata.put("group-0.id", "0");
		gdata.put("group-0.name", "northzone");
		gdata.put("group-0.vehicles", vehicledata);
		params.getSession().setAttribute("returnedData",gdata);
		return gdata;
	}
	/**
	 * @see IDataProvider#getName()
	 */
	public String getName() {
		return "vehiclestatsreport";
	}
}