package com.i10n.fleet.web.controllers;

import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.HardwareModuleDaoImp;
import com.i10n.db.entity.HardwareModule;
import com.i10n.db.entity.LiveVehicleStatus;
import com.i10n.db.entity.User;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadHardwareModuleDetails;
import com.i10n.dbCacheManager.LoadLiveVehicleStatusRecord;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.utils.SessionUtils;

/*
 * Class implementing methods for adding deleting editing and assigning vehicles  * 
 */

public class HardwareAdminOperations extends SimpleFormController {

	private static Logger LOG = Logger.getLogger(HardwareAdminOperations.class);

	/**
	 * addHardware function allows admin to enter new HardwareModule and calls the insert function to insert values
	 * @param request
	 * @return true if hardwaremodule is successfully inserted otherwise it will return false
	 */
	public boolean addHardware(HttpServletRequest request) {
		String imei = request.getParameter("imei");
		String moduleversion = request.getParameter("modulename");
		Float moduleverse = Float.parseFloat(moduleversion.trim());
		String firmware = request.getParameter("firmwareversion");
		Float firm = Float.parseFloat(firmware.trim());
		User user = SessionUtils.getCurrentlyLoggedInUser();
		Long empid = user.getId();
		Calendar cal = Calendar.getInstance();
		Date createdt = cal.getTime();
		Date lastupdated = cal.getTime();
		String mobileNumber = request.getParameter("mobilenumber");
		String simId = request.getParameter("simid");
		String simProvider = request.getParameter("simprovider");
		int status = 1;

		if(mobileNumber == ""){
			//MobileNumber will be 0 if it is empty
			mobileNumber = "0";
		}
		if(simId == ""){
			//SimId will be 0 if it is empty
			simId = "0";
		}
		//Long mobileNo = Long.parseLong(mobileNumber); //converts mobileNumber to Long
		//calls the constructor
		HardwareModule hardwareModule = new HardwareModule(imei.trim(), moduleverse, createdt, lastupdated, status, empid, firm,mobileNumber,simId,simProvider);
		//checks for the SIM ID and MOBILE NUMBER duplication
		if(!HardwareModuleDaoImp.checkSimId(simId) && !HardwareModuleDaoImp.checkMobileNumber(mobileNumber)){
			LOG.debug("SIM Id and Mobile numbers are not exist");
			try {
				//insert the values if simId and MobileNumber is not duplicate
				hardwareModule = ((HardwareModuleDaoImp) DBManager.getInstance().getDao(DAOEnum.HARDWARE_MODULES_DAO)).insert(hardwareModule);
				updateHardwareModuleCache(hardwareModule); //updates the cache
				return true;
			} catch (OperationNotSupportedException e) {
				LOG.error("Error while inserting into DB");
			} 
		}else{
			LOG.error("Mobile number or Sim Id is already exist ");
		}
		return false;
	}
	/**
	 * editHardware function allows admin to update the values of requested IMEI
	 * @param request
	 * @return true if it is successfully updated otherwise will return false.
	 */
	public boolean editHardware(HttpServletRequest request) {
		boolean opSuccess = false;
		String id = request.getParameter("key");
		Long hardwaremoduleid = Long.parseLong(StringUtils.stripCommas(id.trim()).trim());
		String newImei = request.getParameter("imei");
		String oldImei = request.getParameter("oldimei");
		String moduleversion = request.getParameter("moduleversion");
		Float moduleverse = Float.parseFloat(moduleversion.trim());
		String firmware = request.getParameter("firmwareversion");
		Float firm = Float.parseFloat(firmware.trim());
		Calendar cal = Calendar.getInstance();
		Date lastupdated = cal.getTime();
		User user = SessionUtils.getCurrentlyLoggedInUser();
		Long empid = user.getId();
		String newMobileNumber = request.getParameter("mobilenumber");	
		String newSimId = request.getParameter("simid");
		String simProvider = request.getParameter("simprovider");
		String oldMobileNumber = request.getParameter("oldmobilenumber");
		String oldSimId = request.getParameter("oldsimid");

		if (newImei != null && moduleversion != null && firmware != null) {
			if(newMobileNumber == ""){
				//Mobile Number will be 0 if it is Empty
				newMobileNumber = "0";
			}
			if(newSimId == ""){
				//Sim Id will be 0 if it is Empty
				newSimId = "0";
			}
			if(oldMobileNumber==""){
				//Old Mobileumber will be 0 if it is Empty
				oldMobileNumber="0";
			}
			Long newMobileNo = Long.parseLong(newMobileNumber);	//converts Mobile Number into Long
			Long oldMobileNo = Long.parseLong(oldMobileNumber);
			HardwareModule hardwareModule = new HardwareModule(hardwaremoduleid, newImei.trim(), moduleverse, new Date(), 
					lastupdated, 0, empid, firm,newMobileNumber,newSimId,simProvider);
			//checks for the SIM ID and MOBILENUMBER duplication
			if(!HardwareModuleDaoImp.checkSimId(oldSimId,newSimId)){
				LOG.debug("SIM ID CHECKED");
				if(!HardwareModuleDaoImp.checkMobileNumber(oldMobileNo, newMobileNo)){
					LOG.debug("MOBILENUMBER CHECKED");
					if(!HardwareModuleDaoImp.checkImei(oldImei, newImei)){
						LOG.debug("IMEI CHECKED");
						try {
							//update the values for requested IMEI if it is not duplicate.
							LOG.debug("Update The Values");
							hardwareModule = ((HardwareModuleDaoImp) DBManager.getInstance().getDao(DAOEnum.HARDWARE_MODULES_DAO)).update(hardwareModule);
							if(hardwareModule != null){
								if(oldImei != null){
									updateLiveVehicleStatusCache(oldImei, newImei);
									updateHardwareModuleCache(hardwareModule); //updates cache of hardwaremodules
									opSuccess = true; //returns true if it updates successfully
								}
							}
							LoadLiveVehicleStatusRecord.getInstance().refresh();
						}
						catch (OperationNotSupportedException e) {
							LOG.error(e);
						}
						if (hardwareModule == null) {
							opSuccess = true;
						}

					}

				}

			}

		}
		return opSuccess;	
	}

	public boolean[] deleteHardware(String[] moduleIds) {
		HardwareModule module;
		boolean[] successStatus = new boolean[moduleIds.length];
		java.util.Arrays.fill(successStatus, true);
		Calendar cal = Calendar.getInstance();
		Date lastupdated = cal.getTime();
		for (int i = 0; i < moduleIds.length; i++) {
			module = new HardwareModule(Long.parseLong(StringUtils.stripCommas(moduleIds[i].trim()).trim()), "", 0, new Date(), 
					lastupdated, 0, 0L, 0);
			try {
				module = ((HardwareModuleDaoImp) DBManager.getInstance().getDao(DAOEnum.HARDWARE_MODULES_DAO)).delete(module);
				if (module == null) {
					successStatus[i] = false;
				} else {
					successStatus[i] = true;
					/** Deleting from live vehicle status cache **/
					LoadLiveVehicleStatusRecord.cacheLiveVehicleStatus.remove(module.getImei());
				}
			} catch (OperationNotSupportedException e) {
				e.printStackTrace();
			}
		}
		return successStatus;
	}

	/**
	 * Updating Livevehicle status cache on any alteration in hardwaremodules table 
	 * @param imei
	 */
	private void updateLiveVehicleStatusCache(String oldImei, String newImei) {
		//LiveVehicleStatus liveVehicleStatus = LoadLiveVehicleStatusRecord.retrieve(oldImei);
		LoadLiveVehicleStatusRecord.cacheLiveVehicleStatus.remove(oldImei);
		LiveVehicleStatus liveVehicleStatusForNewImei=LoadLiveVehicleStatusRecord.getInstance().retrieve(newImei);
		LoadLiveVehicleStatusRecord.store(newImei, liveVehicleStatusForNewImei);
		LOG.debug("Updated Cache After Editing Imei is :: "+oldImei+" With New IMei is "+newImei);

	}

	/**
	 * updating HardwareModule status cache on any alteration in hardwaremodules table
	 */
	private void updateHardwareModuleCache(HardwareModule hardwareModule){
		LOG.debug("SIM Id is "+hardwareModule.getSimId()+" Id is "+hardwareModule.getId().getId());
		LoadHardwareModuleDetails.getInstance().cacheHardwareModules.put(hardwareModule.getId().getId(),hardwareModule);
		HardwareModule hModules=LoadHardwareModuleDetails.getInstance().cacheHardwareModules.get(hardwareModule.getId().getId());
		LOG.debug("From Map Value is "+hModules.toString());
	}
}