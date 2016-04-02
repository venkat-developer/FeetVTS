package com.i10n.fleet.web.controllers;


import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.RouteScheduleDaoImpl;
import com.i10n.db.dao.VehicleToRouteScheduleDaoImp;
import com.i10n.db.entity.RouteSchedule;
import com.i10n.db.entity.VehicleToRouteSchedule;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadAclVehicleDetails;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.StringUtils;

/**
 * RouteScheduleAdmin Operations Class allows admin to add,update and delete RouteSchedules 
 * @author Gobi
 */

public class RouteScheduleAdminOperation extends SimpleFormController{
	private static final Logger LOG = Logger.getLogger(RouteScheduleAdminOperation.class);
	
	/**
	 * addRouteSchedules gets the parameters and inserts into the DataBase of RouteSchedules
	 * @param request
	 * @return routeschedule with all its requested parameters to insert
	 */
	public RouteSchedule addRouteSchedules(HttpServletRequest request){
		
		//Gets the parameters
		String rid = request.getParameter("routes");
		String sid = request.getParameter("stops");
		String time = request.getParameter("time");
		String timeWithoutSeconds="";
		LOG.info("time length:"+time);
		if(time.length() == 8){
			timeWithoutSeconds = time.substring(0, time.length()-3);
			LOG.info("Time:"+timeWithoutSeconds);
		}
		String routeScheduleId = rid+"-"+timeWithoutSeconds;							//concate the RouteID-Time and Creates A RouteScheduleID
		Time t = Time.valueOf(time);									//converts string to time
		String sequencenumber=request.getParameter("sequencenumber");
		//converts into the necessary DataTypes
		Long routeID = Long.parseLong(rid);				
		Long stopID = Long.parseLong(sid);
		int sequenceNo = Integer.parseInt(sequencenumber); 
		
		//calls The Constructor of RouteSchedule
		RouteSchedule  rs = new RouteSchedule(routeScheduleId,routeID,stopID,sequenceNo, t);
		try {
			rs = ((RouteScheduleDaoImpl)DBManager.getInstance().getDao(DAOEnum.ROUTE_SCHEDULE_DAO)).insert(rs);		//calls the insert function
			
		} catch (OperationNotSupportedException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**
	 * editRouteSchedule gets the parameter of selected RouteScheduleID from textboxes 
	 * allows to Edit and update them
	 * @param request
	 * @return true if routeschedule is null or return false
	 * @throws ParseException
	 */
	public boolean  editRouteSchedule(HttpServletRequest request) throws ParseException{
	
		boolean opSuccess = false;									//boolean value
		String temp = null;
		Time t=null;
		
		//gets the parameters
		String routeScheduleKey = request.getParameter("key");		
		String rsKey = StringUtils.stripCommas(routeScheduleKey);
		
		String routeName = request.getParameter("routename");
		String stopName = request.getParameter("stopname");
		String sequenceno = request.getParameter("sequenceno");
		String time = request.getParameter("time");
		String estimateddistance = request.getParameter("estimateddistance");
		String spanday = request.getParameter("spanday");
		
		//converts parameters to necessary datatypes
		Long routeScheduleId = Long.parseLong(rsKey);
		temp = String.valueOf(time);
		SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
		SimpleDateFormat tdf = new SimpleDateFormat("hh:mm:ss a");
		
		Date date = tdf.parse(temp);
		t = Time.valueOf(df.format(date));
		LOG.debug("time : "+t);
		
		int sequenceNo = Integer.parseInt(sequenceno);				
		int spanDay = Integer.parseInt(spanday);
		long estimatedDist = Long.parseLong(estimateddistance);
				
		
		//calls the constructor of required parameters
		
		RouteSchedule rs = new RouteSchedule(new LongPrimaryKey(routeScheduleId),routeName,stopName,sequenceNo,t,estimatedDist,spanDay);
		
		//connects to database and calls the update function
		try{
			rs = ((RouteScheduleDaoImpl)DBManager.getInstance().getDao(DAOEnum.ROUTE_SCHEDULE_DAO)).update(rs); 
		}
		catch(OperationNotSupportedException e){
			LOG.error("Error While Updating List ",e);
		}
		
		//if routeschedule is null
		if(rs==null){				
			opSuccess = true;
		}
		return opSuccess;	
	}
	

	/**
	 * deleteRouteSchedule gets the Existing RouteSchedules and allows to delete them
	 * @param request
	 * @return true if routeschedule is null
	 */
	
	public boolean deleteRouteSchedule(HttpServletRequest request){
		boolean opSuccess = false;
		RouteSchedule rs;
		String routescheduleList = request.getParameter("routescheduleList"); //gets The List Of RouteSchedule
		LOG.info("routescheduleList "+routescheduleList);
		String[] routeScheduleIds = routescheduleList.split(":");
		if(routeScheduleIds != null){										  //checks if not null
			
			for(int i = 0; i < routeScheduleIds.length;i++){				  //Runs the loop till last RouteSchedules	
				rs =new RouteSchedule();									  //calls the default constructor	
				long longvalue=Long.parseLong(routeScheduleIds[i]);
				rs.setId(new LongPrimaryKey(longvalue));
				try{
						//connects with db and calls Delete Function
						rs = ((RouteScheduleDaoImpl)DBManager.getInstance().getDao(DAOEnum.ROUTE_SCHEDULE_DAO)).delete(rs); 
						LOG.debug("Deletion Query Execution "+rs);
						if(rs == null){
						LOG.debug("Deletion Query In IF Loop "+rs);
						opSuccess = true;
				}
					
				}catch(OperationNotSupportedException e){
					LOG.error("Exception in Deletion ",e);
				}
						
			}
		}
		return opSuccess;
	}	
	/**
	 * assignRouteSchedule will Assign a routeschedule to particular vehicle
	 * @param request
	 * @return true if routeschedule is null
	 */
	@SuppressWarnings("deprecation")
	public boolean assignRouteSchedule(HttpServletRequest request) {
		LOG.info("Assign Route Schedule");
		boolean opSuccess = true;
		String vehicleid = request.getParameter("vehicleId");
		LOG.debug("Vehicleid:"+vehicleid);
		String vehicleId = StringUtils.stripCommas(vehicleid.trim());
		Long vehicle = Long.parseLong(vehicleId);
		String assignedrouschlist = request.getParameter("assignedRouteSchedules");
		String vacantrouschlist = request.getParameter("vacantRouteSchedules");
		String[] assinedrouschs = assignedrouschlist.trim().split(",");
		String[] vacantrouschs = vacantrouschlist.trim().split(",");
		
		if (assinedrouschs != null || vacantrouschs != null) {
			for (int i = 0; i < assinedrouschs.length; i++) {
				LOG.debug("assinedrouschs:"+assinedrouschs[i]);
				Long routeId = null;
				Time scheduleTime = null;
				if (!assinedrouschs[i].equals("") || assinedrouschs[i].length() != 0) {
					String[] routescheduleId = assinedrouschs[i].trim().split("-");
					routeId = Long.parseLong(routescheduleId[0]);
					 String[] sch=routescheduleId[1].split(":");
					 LOG.debug("sch[0]:"+sch[0]+"sch[1]:"+sch[1]);
					 scheduleTime =new Time(Integer.parseInt(sch[0]),Integer.parseInt(sch[1]),0);
					LOG.debug("routeId:"+routeId+"scheduleTime:"+scheduleTime);
				} else {
					continue;
				}
				VehicleToRouteSchedule vehicleAssign = new VehicleToRouteSchedule(vehicle,routeId,scheduleTime);
				try {
					vehicleAssign = ((VehicleToRouteScheduleDaoImp) DBManager.getInstance().getDao(DAOEnum.VEHICLE_TO_ROUTE_SCHEDULE_DAO)).insert(vehicleAssign);
					if (vehicleAssign == null) {
						opSuccess = false;
					}
				} catch (OperationNotSupportedException e) {
					LOG.error(e);
				}
			}

			for (int i = 0; i < vacantrouschs.length; i++) {
				LOG.debug("vacantrouschs:"+vacantrouschs[i]);
				Long routeId = null;
				Time scheduleTime = null;
				if (!vacantrouschs[i].equals("") || vacantrouschs[i].length() != 0) {
					String[] routescheduleId = vacantrouschs[i].trim().split("-");
					routeId = Long.parseLong(routescheduleId[0]);
					String[] sch=routescheduleId[1].split(":");
					 LOG.debug("sch[0]:"+sch[0]+"sch[1]:"+sch[1]);
					 scheduleTime =new Time(Integer.parseInt(sch[0]),Integer.parseInt(sch[1]),0);;
					LOG.info("In Vacant routeId:"+routeId+"scheduleTime:"+scheduleTime);
				} else {
					continue;
				}
				VehicleToRouteSchedule vehicleVacant = new VehicleToRouteSchedule(vehicle,routeId,scheduleTime);
				try {
					vehicleVacant = ((VehicleToRouteScheduleDaoImp) DBManager.getInstance().getDao(DAOEnum.VEHICLE_TO_ROUTE_SCHEDULE_DAO)).delete(vehicleVacant);
					if (vehicleVacant == null) {
						opSuccess = false;
					}
				} catch (OperationNotSupportedException e) {
					LOG.error(e);
				}
			}
			LoadAclVehicleDetails.getInstance().refresh();
		}
		return opSuccess;
	}
}

