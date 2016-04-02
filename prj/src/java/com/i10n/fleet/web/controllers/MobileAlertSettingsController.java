package com.i10n.fleet.web.controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.i10n.db.dao.ACLMobileDaoImp;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.MobileNumberDaoImp;
import com.i10n.db.entity.ACLMobile;
import com.i10n.db.entity.MobileNumber;
import com.i10n.db.entity.User;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadAclMobileDetails;
import com.i10n.dbCacheManager.LoadSMSAlertUserDetails;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.providers.mock.LogsDataProvider;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.utils.SessionUtils;

public class MobileAlertSettingsController extends SimpleFormController {
	private static Logger LOG = Logger.getLogger(MobileAlertSettingsController.class);
	public enum MobileAlertSettings {
		MOBILEALERTSETTINGS_ASSIGN("mobilealertsettings_assign");

		private static Map<String, MobileAlertSettings> lookup = new HashMap<String, MobileAlertSettings>();
		private String val;

		private MobileAlertSettings(String val) {
			this.val = val;
		}

		public String getVal() {
			return val;
		}

		public static MobileAlertSettings get(String val) {
			return lookup.get(val);
		}

		static {
			for (MobileAlertSettings s : EnumSet.allOf(MobileAlertSettings.class)) {
				lookup.put(s.getVal(), s);
			}
		}
	}

	@Override
	public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = processRequest(request);
		return modelAndView;
	}

	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
	throws Exception {
		ModelAndView modelAndView = processRequest(request);
		return modelAndView;
	}

	@SuppressWarnings("deprecation")
	private ModelAndView processRequest(HttpServletRequest request)
	throws JSONException, OperationNotSupportedException {
		ModelAndView modelAndView = null;
		String action = null;
		String operation = request.getParameter("command_type");
		if (operation!=null) {

			switch (MobileAlertSettings.get(operation)) {
			case MOBILEALERTSETTINGS_ASSIGN: {
				String reportIdString = request.getParameter("userId");
				String reportIdArray[] = reportIdString.split(",");
				String regId = reportIdArray[0];
				if(reportIdArray.length > 1){
					for(int j=1; j<reportIdArray.length; j++){
						regId+=reportIdArray[j];
					}
				}
				Long reportID = Long.parseLong(regId);
				String assignedvehlist = request.getParameter("assignedVehicles");
				String vacantvehlist = request.getParameter("vacantVehicles");
				String[] assinedvehs = assignedvehlist.trim().split(",");
				String[] vacantvehs = vacantvehlist.trim().split(",");
				if (assinedvehs != null || vacantvehs != null) {
					for (int i = 0; i < assinedvehs.length; i++) {
						Long avehicles = null;
						if (!assinedvehs[i].equals("") || assinedvehs[i].length() != 0) {
							avehicles = ((ACLMobileDaoImp) DBManager.getInstance().getDao(DAOEnum.ACL_MOBILE_DAO)).getVehicleIDFromItsName(assinedvehs[i].trim());
						} else {
							continue;
						}
						ACLMobile aclMobile = new ACLMobile(avehicles, reportID);
						try {
							aclMobile = ((ACLMobileDaoImp) DBManager.getInstance().getDao(DAOEnum.ACL_MOBILE_DAO)).insert(aclMobile);
							// Updating cache
							Vector<Long> vehicleIdList = new Vector<Long>();
							vehicleIdList = LoadAclMobileDetails.getInstance().retrieve(aclMobile.getMobileId());
							vehicleIdList.add(aclMobile.getVehicleid());
							LoadAclMobileDetails.getInstance().cacheAclMobileDetails.put(aclMobile.getMobileId(), vehicleIdList);
						} catch (OperationNotSupportedException e) {
							e.printStackTrace();
						}
					}
					for (int i = 0; i < vacantvehs.length; i++) {
						Long vvehicle = null;
						if (!vacantvehs[i].equals("") || vacantvehs[i].length() != 0) {
							vvehicle = ((ACLMobileDaoImp) DBManager.getInstance().getDao(DAOEnum.ACL_MOBILE_DAO)).getVehicleIDFromItsName(vacantvehs[i].trim());
						} else {
							continue;
						}
						ACLMobile aclMobile = new ACLMobile(vvehicle, reportID);
						try {
							aclMobile = ((ACLMobileDaoImp) DBManager.getInstance().getDao(DAOEnum.ACL_MOBILE_DAO)).delete(aclMobile);
						} catch (OperationNotSupportedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			}
			action = " HAS EDITED VEHICLES FOR MOBILE SETTINGS USERS";
		} else {
			String localTime = request.getParameter("localTime");
			Date clientTime = new Date(localTime);
			LogsDataProvider log = new LogsDataProvider();
			User currentUser = SessionUtils.getCurrentlyLoggedInUser();
			Long uuid = null;
			String username = null;
			String remoteip = request.getRemoteAddr();
			Calendar cal = Calendar.getInstance();
			Date dateAdjusted = DateUtils.adjustToServerTime(cal.getTime(), clientTime);
			String name, mobilenumber, overspeeding, geofencing, chargerdisconnected = null;
			Long userid;
			String addeddata = request.getParameter("addeddata");
			JSONArray js = new JSONArray(addeddata);
			for (int i = 0; i < js.length(); i++) {
				JSONObject jso = new JSONObject(js.getString(i));
				name = jso.getString("name");
				userid = SessionUtils.getCurrentlyLoggedInUser().getId();
				mobilenumber = jso.getString("mobilenumber");
				overspeeding = jso.getString("overspeeding");
				geofencing = jso.getString("geofencing");
				chargerdisconnected = jso.getString("chargerdisconnected");
				MobileNumber report = new MobileNumber(name, userid, Long.parseLong(mobilenumber), Boolean
						.parseBoolean(overspeeding), Boolean.parseBoolean(geofencing), Boolean.parseBoolean(chargerdisconnected));
				report = ((MobileNumberDaoImp) DBManager.getInstance().getDao(DAOEnum.MOBILENUMBER_DAO)).insert(report);
				// Updating cache
				LoadSMSAlertUserDetails.getInstance().cacheSMSAlertUsers.put(report.getId().getId(), report);
			}
			String updateddata = request.getParameter("updateddata");
			String updatedItemsLen = request.getParameter("updateddatalen");
			if (Integer.parseInt(updatedItemsLen) > 0) {
				JSONArray js1 = new JSONArray(updateddata);
				for (int i = 0; i < js1.length(); i++) {
					JSONObject jso1 = new JSONObject(js1.getString(i));
					name = jso1.getString("name");
					userid = SessionUtils.getCurrentlyLoggedInUser().getId();
					mobilenumber = jso1.getString("mobilenumber");
					overspeeding = jso1.getString("overspeeding");
					geofencing = jso1.getString("geofencing");
					chargerdisconnected = jso1.getString("chargerdisconnected");
					try{
						MobileNumber report = new MobileNumber(name, userid, Long.parseLong(mobilenumber), Boolean
								.parseBoolean(overspeeding), Boolean.parseBoolean(geofencing), Boolean.parseBoolean(chargerdisconnected));	
						report = ((MobileNumberDaoImp) DBManager.getInstance().getDao(DAOEnum.MOBILENUMBER_DAO)).update(report);
						LoadSMSAlertUserDetails.getInstance().cacheSMSAlertUsers.put(report.getId().getId(), report);
					}catch(Exception e){
						LOG.error("Error while updating mobile number ",e);
					}
				}
			}
			String removeddata = request.getParameter("removeddata");
			String removedItemsLen = request.getParameter("removeddatalen");
			if (Integer.parseInt(removedItemsLen) > 0) {
				JSONArray js2 = new JSONArray(removeddata);
				for (int i = 0; i < js2.length(); i++) {
					JSONObject jso2 = new JSONObject(js2.getString(i));
					name = jso2.getString("name");
					userid = SessionUtils.getCurrentlyLoggedInUser().getId();
					mobilenumber = jso2.getString("mobilenumber");
					overspeeding = jso2.getString("overspeeding");
					geofencing = jso2.getString("geofencing");
					chargerdisconnected = jso2.getString("chargerdisconnected");
					MobileNumber report = new MobileNumber(name, userid, Long.parseLong(mobilenumber), Boolean
							.parseBoolean(overspeeding), Boolean.parseBoolean(geofencing), Boolean.parseBoolean(chargerdisconnected));
					report = ((MobileNumberDaoImp) DBManager.getInstance().getDao(DAOEnum.MOBILENUMBER_DAO)).delete(report);
					LoadSMSAlertUserDetails.getInstance().cacheSMSAlertUsers.remove(report.getId().getId());
					}
			}
			uuid = currentUser.getId();
			Long userid1 = Long.parseLong(StringUtils.stripCommas(uuid + ""));
			username = currentUser.getLogin();
			action = username.toUpperCase()+ " HAS UPDATED MOBILE ALERT SETTINGS";
			log.getupDatedlogs(userid1, dateAdjusted, remoteip, username, action);
		}
		return modelAndView;
	}
}