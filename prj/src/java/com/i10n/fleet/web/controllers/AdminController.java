package com.i10n.fleet.web.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.BindException;
import java.text.ParseException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import com.i10n.fleet.util.StringUtils;

/**
 * 
 * Entry point for all the functions invoked from pages in the admin login.
 * 
 * @author joshua
 * 
 */
public class AdminController extends SimpleFormController {

	private static Logger LOG = Logger.getLogger(AdminController.class);

	public enum AdminOperation {

		ADD_USER("add_user"), EDIT_USER("edit_user"), DELETE_USER("delete_user"), ADD_VEHICLE(
				"add_vehicle"), EDIT_VEHICLE("edit_vehicle"), ASSIGN_VEHICLE(
						"assign_vehicle"), DELETE_VEHICLE("delete_vehicle"), ADD_DRIVER(
								"add_driver"), EDIT_DRIVER("edit_driver"), DELETE_DRIVER(
										"delete_driver"), ASSIGN_DRIVER("assign_driver"), ADD_HARDWARE(
												"add_hardware"), EDIT_HARDWARE("edit_hardware"), DELETE_HARDWARE(
														"delete_hardware"),ADD_GROUP("add_group"),EDIT_GROUP("edit_group"),
														ASSIGN_GROUP("assign_group"),ASSIGN_DRIVER_GROUP("assigndriver_group"),DELETE_GROUP("delete_group"),
														ADD_FUELCALIBRATION("add_fuelcalibration"),DELETE_FUELCALIBRATION("delete_fuelcalibration"),VEHICLE_HISTORY("vehicle_history"),
														ADD_ROUTESCHEDULE("add_routeschedule"),DELETE_ROUTESCHEDULE("delete_routeschedule"),ASSIGN_ROUTESCHEDULE("assign_routeschedule");

		private static Map<String, AdminOperation> LOOKUP = new HashMap<String, AdminOperation>();

		private String val;

		private AdminOperation(String val) {
			this.val = val;
		}

		public String getVal() {
			return val;
		}

		public static AdminOperation get(String val) {
			return LOOKUP.get(val);
		}

		static {
			for (AdminOperation s : EnumSet.allOf(AdminOperation.class)) {
				LOOKUP.put(s.getVal(), s);
			}
		}
	}

	/**
	 * Handles login/logout requests and checks redirects to appproaprate view.
	 * If its a logout request, it removes the session attribute and redirects
	 * to login page. If a login request handling is passed to
	 * {@link #onSubmit(Object)} which on success returns a {@link RedirectView}
	 * to {@link #getSuccessView()} page and then finally adds the currents user
	 * as a session attribute. This will be reimplemented once the user database
	 * is available.
	 * 
	 * @see SimpleFormController#handleRequest(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Override
	public ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = processRequest(request,response);
		return modelAndView;

	}
	/**
	 * Handles the login submit requests. It will validate if the user/password
	 * are matching and on success will return a {@link RedirectView} to
	 * {@link #getSuccessView()} page.
	 * 
	 */

	/**
	 * Handles the login submit requests. It will validate if the user/password
	 * are matching and on success will return a {@link RedirectView} to
	 * {@link #getSuccessView()} page.
	 * 
	 */
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
			throws Exception {
		ModelAndView modelAndView = processRequest(request,response);
		return modelAndView;

	}

	/**
	 * 
	 * Switch case logic
	 * 
	 * @param request
	 * @return
	 * @throws IOException 
	 * @throws ParseException 
	 */
	private ModelAndView processRequest(HttpServletRequest request,HttpServletResponse response) throws IOException, ParseException {
		ModelAndView modelAndView = null;
		String action = null;
		String operation = request.getParameter("command_type");
		LOG.info("Command Type  "+operation);
		HardwareAdminOperations hardwareAdminOperations = new HardwareAdminOperations();
		VehicleAdminOperations vehicleAdminOperations = new VehicleAdminOperations();
		DriverAdminOperations driverAdminOperations = new DriverAdminOperations();		
		UserAdminOperations userAdminOperations = new UserAdminOperations();
		GroupAdminOperations groupAdminOperations = new GroupAdminOperations();
		FuelCalibrationAdminOperations fuelcalibrationAdminOperations=new FuelCalibrationAdminOperations(); 
		RouteScheduleAdminOperation routeScheduleAdminOperations = new RouteScheduleAdminOperation();	

		switch (AdminOperation.get(operation)) {
		case ADD_USER: {
			String firstname = request.getParameter("firstname");
			if (userAdminOperations.addUser(request) != null) {
				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
			}
			action = " HAS ADDED A NEW USER "+ firstname.toUpperCase();
			StringUtils.insertLogs(action);
		}	break;

		case DELETE_USER: {
			if (userAdminOperations.deleteUser(request)) {
				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
			}
			action = " HAS DELETED USER";
			StringUtils.insertLogs(action);
		}	break;

		case EDIT_USER: {
			String firstname = request.getParameter("firstname");
			if (userAdminOperations.editUser(request)) {
				modelAndView = new ModelAndView(new RedirectView(
						getSuccessView()));
			}
			action ="HAS EDITTED USER "+ firstname.toUpperCase();
			StringUtils.insertLogs(action);
		}	break;

		case ADD_DRIVER: {
			String firstname = request.getParameter("firstname");
			if (driverAdminOperations.addDriver(request) != null) {
				modelAndView = new ModelAndView(new RedirectView(
						getSuccessView()));
			}
			action = " HAS ADDED A NEW DRIVER "+ firstname.toUpperCase();
			StringUtils.insertLogs(action);
		}	break;

		case EDIT_DRIVER: {
			String firstname = request.getParameter("firstname");
			if (driverAdminOperations.editDriver(request)) {
				modelAndView = new ModelAndView(new RedirectView(
						getSuccessView()));
			}			
			action = " HAS EDITTED DRIVER "+ firstname;
			StringUtils.insertLogs(action);
		}	break;

		case DELETE_DRIVER: {
			String driverList = request.getParameter("driverList");
			String[] driverIds = driverList.split(":");
			boolean[] successStatus= null;
			String message =" ";
			boolean deleted= false;
			if(driverList.trim().length() !=0){
				successStatus= driverAdminOperations.deleteDriver(driverIds);
				for(int i=0;i<successStatus.length ;i++){
					if(!successStatus[i]){
						message += StringUtils.stripCommas(driverIds[i])+":";
					}
					else{
						message += "0"+":";
						deleted=true;
					}
				}
				PrintWriter out = response.getWriter();
				out.println(message);
				out.close();
				out.flush();

				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
				if(deleted){
					action = "HAS DELETED DRIVER/S";
					StringUtils.insertLogs(action);
				}
				else{
					action = "FAILED IN DELETING DRIVER/S";
					StringUtils.insertLogs(action);
				}
			}
		}	break;

		case ASSIGN_DRIVER: {
			if (driverAdminOperations.assignDriver(request)) {
				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
			}
			action = "HAS ASSIGNED DRIVER/S";
			StringUtils.insertLogs(action);
		}	break;

		case ADD_VEHICLE: {			
			String displayName = request.getParameter("name");
			if (vehicleAdminOperations.addVehicle(request) != null) {
				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
				action = "HAS ADDED A NEW VEHICLE"+ displayName.toUpperCase();
				StringUtils.insertLogs(action);
			}
		}	break;

		case EDIT_VEHICLE: {
			if (vehicleAdminOperations.editVehicle(request) != null) {
				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
				action ="HAS EDITTED VEHICLE";
				StringUtils.insertLogs(action);
			}
		}	break;

		case DELETE_VEHICLE: {
			String vehicleList = request.getParameter("vehicleList");
			String[] vehicleIds = vehicleList.split(":");
			boolean[] successStatus= null;
			String message =" ";
			boolean deleted= false;
			if(vehicleList.trim().length() !=0){
				successStatus= vehicleAdminOperations.deleteVehicle(vehicleIds);
				for(int i=0;i<successStatus.length ;i++){
					if(!successStatus[i]){
						message += StringUtils.stripCommas(vehicleIds[i])+":";
					}
					else{
						message += "0"+":";
						deleted=true;
					}
				}
				PrintWriter out = response.getWriter();
				out.println(message);
				out.close();
				out.flush();

				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
				if(deleted){
					action = " HAS DELETED VEHICLE/S"+vehicleList;
					StringUtils.insertLogs(action);
				}
				else{
					action = " FAILED IN DELETING VEHICLE/S"+vehicleList;
					StringUtils.insertLogs(action);
				}
			}
		}	break;

		case ASSIGN_VEHICLE: {
			if (vehicleAdminOperations.assignVehicle(request)) {
				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
				action = "HAS ASSIGNED VEHICLE/S";
				StringUtils.insertLogs(action);
			}
		}	break;

		case ADD_GROUP: {			
			if (groupAdminOperations.addGroup(request) != null) {
				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
				action = "HAS ADDED A NEW GROUP";
				StringUtils.insertLogs(action);
			}
		}	break;

		case EDIT_GROUP: {
			if (groupAdminOperations.editGroup(request) != null) {
				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
				action ="HAS EDITTED GROUP";
				StringUtils.insertLogs(action);
			}
		}	break;

		case DELETE_GROUP: {
			String groupList = request.getParameter("groupList");
			String[] groupIds = groupList.split(":");
			boolean[] successStatus= null;
			String message =" ";
			boolean deleted= false;
			if(groupList.trim().length() !=0){
				successStatus= groupAdminOperations.deleteGroup(groupIds);
				for(int i=0;i<successStatus.length ;i++){
					if(!successStatus[i]){
						message += StringUtils.stripCommas(groupIds[i])+":";
					}
					else{
						message += "0"+":";
						deleted=true;
					}
				}
				PrintWriter out = response.getWriter();
				out.println(message);
				out.close();
				out.flush();

				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
				if(deleted){
					action = "HAS DELETED GROUP/S";
					StringUtils.insertLogs(action);
				}
				else{
					action = "FAILED IN DELETING GROUP/S";
					StringUtils.insertLogs(action);
				}
			}
		}	break;

		case ASSIGN_GROUP: {
			if (groupAdminOperations.assignVehiclestoGroup(request)) {
				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
				action = "HAS ASSIGNED VEHICLE/S TO GROUP";
				StringUtils.insertLogs(action);
			}
		}	break;

		case ASSIGN_DRIVER_GROUP: {
			if (groupAdminOperations.assignDriverstoGroup(request)) {
				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
				action = "HAS ASSIGNED DRIVER/S TO GROUP";
				StringUtils.insertLogs(action);
			}
		}	break;

		case ADD_HARDWARE: {
			String imei = request.getParameter("imei");
			if (hardwareAdminOperations.addHardware(request)) {
				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
				action ="HAS ADDED NEW HARDWARE MODULE WITH IMEI NO " + imei;
				StringUtils.insertLogs(action);
			}
		}	break;

		case EDIT_HARDWARE: {
			String imei = request.getParameter("imei");
			if (hardwareAdminOperations.editHardware(request)) {
				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
			}
			action =" HAS EDITTED HARDWARE MODULE WITH IMEI NO " + imei;
			StringUtils.insertLogs(action);
		}	break;

		case ADD_FUELCALIBRATION:{
			if(fuelcalibrationAdminOperations.addFuelValues(request)){
				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
			}
		} break;

		case DELETE_FUELCALIBRATION:{
			if(fuelcalibrationAdminOperations.deleteFuelValues(request)){
				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
			}
		} break;

		case DELETE_HARDWARE: {
			String moduleList = request.getParameter("hardwareList");
			String[] moduleIds = moduleList.split(":");
			boolean[] successStatus= null;
			String message =" ";
			boolean deleted= false;
			if(moduleList.trim().length() !=0){
				successStatus= hardwareAdminOperations.deleteHardware(moduleIds);
				for(int i=0;i<successStatus.length ;i++){
					if(!successStatus[i]){
						message += StringUtils.stripCommas(moduleIds[i])+":";
					}
					else{
						message += "0"+":";
						deleted=true;
					}
				}
				PrintWriter out = response.getWriter();
				out.println(message);
				out.close();
				out.flush();

				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
				if(deleted){
					action = "HAS DELETED MODULE/S"+moduleList;
					StringUtils.insertLogs(action);
				}
				else{
					action = "FAILED IN DELETING MODULE/S";
					StringUtils.insertLogs(action);
				}
			}
		} 	break;
		//Vehicle History Page 
		case VEHICLE_HISTORY: {
			if (vehicleAdminOperations.vehiclehistory(request) != null) {
				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
				action ="HAS EDITTED VEHICLE";
				StringUtils.insertLogs(action);
			}
		}	break;
		//Adds New RouteSchedules
		case ADD_ROUTESCHEDULE:{
			if(routeScheduleAdminOperations.addRouteSchedules(request)!=null){

				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
			}
			action = "HAS ADDED A NEW ROUTE";
			StringUtils.insertLogs(action);
			break;

		}

		//Deletes The Existing RouteSchedule
		case DELETE_ROUTESCHEDULE:{
			if(routeScheduleAdminOperations.deleteRouteSchedule(request)){
				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
			}
			action = "HAS DELETED A NEW ROUTESCHEDULE";
			StringUtils.insertLogs(action);
			break;
		}
		//Assign the Routescheduleid to vehicles
		case ASSIGN_ROUTESCHEDULE: {
			if(routeScheduleAdminOperations.assignRouteSchedule(request)){
				modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
			}
			action = "HAS ASSIGNED A NEW ROUTESCHEDULE";
			StringUtils.insertLogs(action);
			break;
		}
		default: {
			LOG.info("No case statement for this request");
		}
		}
		return modelAndView;
	}
}