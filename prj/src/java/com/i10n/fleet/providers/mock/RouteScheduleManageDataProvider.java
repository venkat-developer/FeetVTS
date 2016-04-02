
package com.i10n.fleet.providers.mock;

import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.RouteScheduleDaoImpl;
import com.i10n.db.dao.RoutesDaoImp;
import com.i10n.db.dao.StopsDaoImp;
import com.i10n.db.dao.VehicleDaoImpl;
import com.i10n.db.entity.RouteSchedule;
import com.i10n.db.entity.Routes;
import com.i10n.db.entity.Stops;
import com.i10n.db.entity.User;
import com.i10n.db.entity.Vehicle;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * RouteScheduleManageDataprovider class is to perform the operations add,edit,delete routeschedule
 * it gets the necessary Data From The Database as per the requirements and displays them by calling a functions
 * Defining in the RouteScheduleDaoImp.java
 * @author Gobi
 */



public class RouteScheduleManageDataProvider implements IDataProvider{
	// Current class specific logger instance
	private static final Logger LOG = Logger.getLogger(RouteScheduleManageDataProvider.class);

	@Override
	public IDataset getDataset(RequestParameters params) {
		IDataset routeScheduleResult = new Dataset();
		String dataView = params.getParameter(RequestParams.dataView);
		if (dataView == null || dataView.isEmpty()) {
			dataView = "default";
		}
		if (dataView.equalsIgnoreCase("default")) {
			//Dataset will fetch the data and will return it to the addrouteschedule.ftm,editrouteschedule.ftm and deleterouteschedule.ftm for further operations

			String login = SessionUtils.getCurrentlyLoggedInUser().getLogin(); //will set the currently logged in user
			User user = SessionUtils.getCurrentlyLoggedInUser();
			String routeScheduleID = params.getParameter(RequestParams.routescheduleID);
			LOG.debug("RouteScheduleID "+routeScheduleID);
			routeScheduleID = StringUtils.stripCommas(routeScheduleID);
			//A logged in user can fetch the data from the database
			if(login!=null){		
				/**
				 * stopsname object will get the list of StopNames from Stops Database
				 * And stops Dataset will Display the StopNames to DropDownList of AddRouteSchedule.ftm
				 */
				//Will get the list of stopnames
				List<Stops> stopNameList = ((StopsDaoImp)DBManager.getInstance().getDao(DAOEnum.STOPS_DAO)).getStopNameList(user.getId()); 
				IDataset stops = new Dataset();
				Stops sp = null;
				if(stopNameList!=null){
					for(int i=0;i<stopNameList.size();i++){
						sp = stopNameList.get(i);
						stops.put("stop-" +(i + 1) +sp.getId().getId()+".stopid", sp.getId().getId()+"");
						stops.put("stop-" +(i + 1) +sp.getId().getId()+".stopname",sp.getStopName()+"" );
						stops.put("stop-" + (i+1)+sp.getId().getId()+".knownas", sp.getKnowAs()+"");
					}
				}
				routeScheduleResult.put("stops", stops); //return existed stoplist
				/**
				 * routename object will get the list of RouteNames from Routes Database
				 * And routes Dataset will Display the RouteNames to DropDownList of AddRouteSchedule.ftm
				 */
				//Will get the list of route names
				List<Routes> routeNameList = ((RoutesDaoImp)DBManager.getInstance().getDao(DAOEnum.ROUTES_DAO)).getRouteNameList(user.getId());
				IDataset routes = new Dataset();
				Routes rt = null;
				if(routeNameList != null){
					for(int i=0;i<routeNameList.size();i++){
						rt = routeNameList.get(i);				
						routes.put("route-"+(i+1)+rt.getId().getId()+".routeid", rt.getId().getId());
						routes.put("route-"+(i+1)+rt.getId().getId()+".routename", rt.getRouteName());
					}
				}
				routeScheduleResult.put("routes", routes); // returns existed routelist
				/**
				 * routescheduleIDList object will get the List  of All RouteScheduleID which have sequenceno = 1
				 * And routeschedules dataset will Displays The RouteScheduleIDs in the ListBox of EditROuteSchedule.ftm
				 */
				if(routeScheduleID == null){
					List<RouteSchedule> routeScheduleIDList = ((RouteScheduleDaoImpl)DBManager.getInstance().getDao(DAOEnum.ROUTE_SCHEDULE_DAO)).getRouteSchedulesIDList();
					IDataset routeSchedules  = new Dataset();

					if(routeScheduleIDList!=null){
						RouteSchedule rs = null;
						for(int i=0;i<routeScheduleIDList.size();i++){
							rs = routeScheduleIDList.get(i);
							LOG.debug("To String EDIT "+rs.toString());
							routeSchedules.put("routeschedule-"+rs.getId().getId()+".id", rs.getId().getId());
							routeSchedules.put("routeschedule-"+rs.getId().getId()+".routescheduleid", rs.getRouteScheduleId());
							routeSchedules.put("routeschedule-"+rs.getId().getId()+".routename", rs.getRouteName());
							routeSchedules.put("routeschedule-"+rs.getId().getId()+".time", rs.getExpectedTime());

						}
					}
					routeScheduleResult.put("routeSchedules", routeSchedules);	
					LOG.info("RouteSchedules ---"+routeSchedules);

					/**
					 * Vehicles list object for list all vehicles in assignrouteschedule for routeschedule
					 */

					List<Vehicle> resultset = ((VehicleDaoImpl) DBManager.getInstance().getDao(DAOEnum.VEHICLE_DAO)).selectAllOwned();
					IDataset vehicleData = new Dataset();

					if (null != resultset) {
						for (int i = 0; i < resultset.size(); i++) {
							Vehicle vehicle = resultset.get(i);

							vehicleData.put("vehicle-" + (i + 1) + ".id", vehicle.getId().getId()+"");
							vehicleData.put("vehicle-" + (i + 1) + ".name", StringUtils.removeSpecialCharacter(vehicle.getDisplayName())+"");
							vehicleData.put("vehicle-" + (i + 1) + ".make", StringUtils.removeSpecialCharacter(vehicle.getMake())+"");
							vehicleData.put("vehicle-" + (i + 1) + ".model",StringUtils.removeSpecialCharacter(vehicle.getModel())+"");
							vehicleData.put("vehicle-" + (i + 1) + ".year", vehicle.getModelYear()+"");
							vehicleData.put("vehicle-" + (i + 1) + ".imei", vehicle.getImeiId()+"");
							vehicleData.put("vehicle-" + (i + 1) + ".vehicleiconid", vehicle.getVehicleIconPicId());
						}
					}
					routeScheduleResult.put("vehicles", vehicleData);
					/**
					 * routescheduleAllIDList Object will get the List of the All The RouteScheduleID
					 * And routescheduleIDs dataset will Displays All the RouteSchedulesID with its sequence number
					 * in the Table of DeleteRouteSchedule.ftm
					 */
					IDataset routeScheduleIDs = new Dataset();
					List<RouteSchedule> routeScheduleAllIDList;
					try {
						routeScheduleAllIDList = ((RouteScheduleDaoImpl)DBManager.getInstance().getDao(DAOEnum.ROUTE_SCHEDULE_DAO)).getRouteScheduleIDAll(user.getId());
						LOG.debug("routeScheduleAllIDList Delete Dataa "+routeScheduleAllIDList);
						if(routeScheduleAllIDList.size()!=0){
							RouteSchedule rs;
							for(int i=0;i<routeScheduleAllIDList.size();i++){
								rs = routeScheduleAllIDList.get(i);
								routeScheduleIDs.put("routeschedule-"+rs.getId().getId()+".id", rs.getId().getId());
								routeScheduleIDs.put("routeschedule-"+rs.getId().getId()+".routescheduleid", rs.getRouteScheduleId());
								routeScheduleIDs.put("routeschedule-"+rs.getId().getId()+".routeid", rs.getRouteId());
								routeScheduleIDs.put("routeschedule-"+rs.getId().getId()+".stopid", rs.getStopId());
								routeScheduleIDs.put("routeschedule-"+rs.getId().getId()+".routename", rs.getRouteName());
								routeScheduleIDs.put("routeschedule-"+rs.getId().getId()+".stopname", rs.getStopName());
								routeScheduleIDs.put("routeschedule-"+rs.getId().getId()+".sequenceno", rs.getSequenceNumber());
								routeScheduleIDs.put("routeschedule-"+rs.getId().getId()+".time", rs.getExpectedTime());
								routeScheduleIDs.put("routeschedule-"+rs.getId().getId()+".estimateddistance", rs.getEstimatedDistance());
								routeScheduleIDs.put("routeschedule-"+rs.getId().getId()+".spanday", rs.getSpanDay());
							}
						}

					}catch (SQLException e1) {
						LOG.debug("Failed : "  +e1);
					}
					routeScheduleResult.put("routeScheduleIDs", routeScheduleIDs);//Returns the Result to DeleteRouteSchedule.ftm
					LOG.debug("Delete Route ScheduleID's List ------"+routeScheduleIDs);
				}
				/**
				 * rsID object will get All the Data from RouteSchedules of selected RouteScheduleID
				 * And routescheduleAttributeMap dataset will returns all data to EditRouteSchedule.ftm 
				 */
				IDataset routeScheduleAttributeMap = new Dataset();
				List<RouteSchedule> routeScheduleAllIDList;
				try {
					if(routeScheduleID != null){
						routeScheduleAllIDList = ((RouteScheduleDaoImpl)DBManager.getInstance().getDao(DAOEnum.ROUTE_SCHEDULE_DAO)).getRouteScheduleIDAll(user.getId());
						if(routeScheduleAllIDList.size()!=0){
							RouteSchedule rs;
							for(int i=0;i<routeScheduleAllIDList.size();i++){
								rs = routeScheduleAllIDList.get(i);
								LOG.debug("Route Schedule Id from DB is "+rs.getRouteScheduleId()+" selected routeScheduleID is  "+routeScheduleID);
								if(rs.getRouteScheduleId().equalsIgnoreCase(routeScheduleID)){
									LOG.debug("Route Schedule Id from DB and selected are equal");
									routeScheduleAttributeMap.put("routeschedule-"+rs.getId().getId()+".id", rs.getId().getId());
									routeScheduleAttributeMap.put("routeschedule-"+rs.getId().getId()+".routescheduleid", rs.getRouteScheduleId());
									routeScheduleAttributeMap.put("routeschedule-"+rs.getId().getId()+".routeid", rs.getRouteId());
									routeScheduleAttributeMap.put("routeschedule-"+rs.getId().getId()+".stopid", rs.getStopId());
									routeScheduleAttributeMap.put("routeschedule-"+rs.getId().getId()+".routename", rs.getRouteName());
									routeScheduleAttributeMap.put("routeschedule-"+rs.getId().getId()+".stopname", rs.getStopName());
									routeScheduleAttributeMap.put("routeschedule-"+rs.getId().getId()+".sequenceno", rs.getSequenceNumber());
									routeScheduleAttributeMap.put("routeschedule-"+rs.getId().getId()+".time", rs.getExpectedTime());
									routeScheduleAttributeMap.put("routeschedule-"+rs.getId().getId()+".estimateddistance", rs.getEstimatedDistance());
									routeScheduleAttributeMap.put("routeschedule-"+rs.getId().getId()+".spanday", rs.getSpanDay());	
								}else{
									LOG.warn("Route scheduleId is not equal to selected routeScheduleId "+routeScheduleID);
								}
							}
						}

					}else{
						LOG.debug("routeScheduleID is null:"+routeScheduleID);
					}
					routeScheduleResult.put("routeScheduleAttributeMap", routeScheduleAttributeMap);//Returns the Result to DeleteRouteSchedule.ftm
					LOG.debug("Edit Route ScheduleID's List ------"+routeScheduleAttributeMap);

				}catch (SQLException e1) {
					LOG.info("Failed : "  +e1);
				}
			}else{
				routeScheduleResult.put("Error.code", "1404");
				routeScheduleResult.put("Error.name", "ResourceNotFoundError");
				routeScheduleResult.put("Error.message", "The requested resource was not found");
			}
		}else if (dataView.equalsIgnoreCase("assignment")){
			LOG.debug("In Assignment Routeschedule");
			/*
			 * Assignment routeschedule and vacant routeschedule list
			 */
			String vehicleID = params.getParameter(RequestParams.vehicleID);
			vehicleID=StringUtils.stripCommas(vehicleID);

			Long vehicleid = Long.parseLong(vehicleID);
			IDataset vacantRouteScheduleData = new Dataset();
			IDataset assignRouteScheduleData = new Dataset();
			List<RouteSchedule> assignedVehicesResultSet = ((RouteScheduleDaoImpl)DBManager.getInstance().getDao(DAOEnum.ROUTE_SCHEDULE_DAO)).getAssignedRouteSchedules(vehicleid);
			List<RouteSchedule> vacantVehiceResultSet = ((RouteScheduleDaoImpl)DBManager.getInstance().getDao(DAOEnum.ROUTE_SCHEDULE_DAO)).getVacantRouteSchedules(vehicleid);
			RouteSchedule routeSchedule= null;
			if (null != assignedVehicesResultSet) {

				for (int i = 0; i < assignedVehicesResultSet.size(); i++) {
					routeSchedule = assignedVehicesResultSet.get(i);
					LOG.debug("Assign Routescheduleid:"+routeSchedule.getRouteScheduleId());
					assignRouteScheduleData.put("routeschedule-"+routeSchedule.getRouteScheduleId()+".routescheduleid", routeSchedule.getRouteScheduleId());
				}
			}
			if (null != vacantVehiceResultSet) {
				for (int i = 0; i < vacantVehiceResultSet.size(); i++) {
					routeSchedule = vacantVehiceResultSet.get(i);
					LOG.debug("Vacant Routescheduleid:"+routeSchedule.getRouteScheduleId());
					vacantRouteScheduleData.put("routeschedule-"+routeSchedule.getRouteScheduleId()+".routescheduleid", routeSchedule.getRouteScheduleId());
				}
			}
			routeScheduleResult.put("routeSchedules.vacant", vacantRouteScheduleData);
			routeScheduleResult.put("routeSchedules.assigned", assignRouteScheduleData);

		}
		return routeScheduleResult;	//return result with All DataSet
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "routeschedulemanage"; //return routeschedulemanage to editrouteschedule.ftm and delterouteschedule.ftm

	}
}
