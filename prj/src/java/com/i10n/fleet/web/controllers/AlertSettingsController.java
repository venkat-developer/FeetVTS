package com.i10n.fleet.web.controllers;

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

import com.i10n.db.dao.ACLAlertsDAOImp;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.MailinglistAlertDaoImp;
import com.i10n.db.entity.ACLAlerts;
import com.i10n.db.entity.MailingListAlert;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadAclAlertDetails;
import com.i10n.dbCacheManager.LoadMailingAlertUserDetails;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.utils.SessionUtils;

public class AlertSettingsController extends SimpleFormController {
	private static Logger LOG = Logger.getLogger(AlertSettingsController.class);
	public enum AlertSettings {
		ALERTSETTINGS_ASSIGN("alertsettings_assign");

		private static Map<String, AlertSettings> lookup = new HashMap<String, AlertSettings>();

		private String val;

		private AlertSettings(String val) {
			this.val = val;
		}

		public String getVal() {
			return val;
		}

		public static AlertSettings get(String val) {
			return lookup.get(val);
		}

		static {
			for (AlertSettings s : EnumSet.allOf(AlertSettings.class)) {
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

	private ModelAndView processRequest(HttpServletRequest request)
	throws JSONException, OperationNotSupportedException {
		ModelAndView modelAndView = null;
		String action = null;
		String operation = request.getParameter("command_type");
		if (operation!=null) {
			switch (AlertSettings.get(operation)) {
			case ALERTSETTINGS_ASSIGN: {
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
							avehicles = ((ACLAlertsDAOImp) DBManager.getInstance().getDao(DAOEnum.ACL_ALERTS_DAO)).getVehicleIDFromItsName(assinedvehs[i].trim());
						} else {
							continue;
						}
						ACLAlerts aclAlerts = new ACLAlerts(avehicles, reportID);
						try {
							aclAlerts = ((ACLAlertsDAOImp) DBManager.getInstance().getDao(DAOEnum.ACL_ALERTS_DAO)).insert(aclAlerts);
							// Updating cache
							Vector<Long> vehicleIdList = new Vector<Long>();
							vehicleIdList = LoadAclAlertDetails.getInstance().retrieve(aclAlerts.getalertUserId());
							vehicleIdList.add(aclAlerts.getVehicleId());
							LoadAclAlertDetails.getInstance().cacheAclAlertsDetails.put(aclAlerts.getalertUserId(), vehicleIdList);
						} catch (OperationNotSupportedException e) {
							e.printStackTrace();
						}
					}
					for (int i = 0; i < vacantvehs.length; i++) {
						Long vvehicle = null;
						if (!vacantvehs[i].equals("") || vacantvehs[i].length() != 0) {
							vvehicle = ((ACLAlertsDAOImp) DBManager.getInstance().getDao(DAOEnum.ACL_ALERTS_DAO)).getVehicleIDFromItsName(vacantvehs[i].trim());
						} else {
							continue;
						}
						ACLAlerts aclAlerts = new ACLAlerts(vvehicle, reportID);
						try {
							aclAlerts = ((ACLAlertsDAOImp) DBManager.getInstance().getDao(DAOEnum.ACL_ALERTS_DAO)).delete(aclAlerts);
						} catch (OperationNotSupportedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			}
			action = " HAS EDITED VEHICLES FOR ALERT SETTINGS USERS";
		} else {
			String name, email, overspeeding, geofencing, chargerdisconnected,ignition = null;
			Long userid;
			String addeddata = request.getParameter("addeddata");
			JSONArray js = new JSONArray(addeddata);
			for (int i = 0; i < js.length(); i++) {
				JSONObject jso = new JSONObject(js.getString(i));
				name = jso.getString("name");
				userid = SessionUtils.getCurrentlyLoggedInUser().getId();
				email = jso.getString("email");
				overspeeding = jso.getString("overspeeding");
				geofencing = jso.getString("geofencing");
				ignition=jso.getString("ignition");
				chargerdisconnected = jso.getString("chargerdisconnected");
				MailingListAlert report = new MailingListAlert(name, userid,email, Boolean.parseBoolean(overspeeding), Boolean
						.parseBoolean(geofencing), Boolean.parseBoolean(chargerdisconnected),Boolean.parseBoolean(ignition));
				report = ((MailinglistAlertDaoImp) DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_ALERT_DAO)).insert(report);
				// Updating cache
				LoadMailingAlertUserDetails.getInstance().cacheEmailAlertUsers.put(report.getId().getId(), report);
			}
			String updateddata = request.getParameter("updateddata");
			String updatedItemsLen = request.getParameter("updateddatalen");
			if (Integer.parseInt(updatedItemsLen) > 0) {
				JSONArray js1 = new JSONArray(updateddata);
				for (int i = 0; i < js1.length(); i++) {
					JSONObject jso1 = new JSONObject(js1.getString(i));
					name = jso1.getString("name");
					userid = SessionUtils.getCurrentlyLoggedInUser().getId();
					email = jso1.getString("email");
					overspeeding = jso1.getString("overspeeding");
					geofencing = jso1.getString("geofencing");
					chargerdisconnected = jso1.getString("chargerdisconnected");
					ignition=jso1.getString("ignition");
					try{
						MailingListAlert report = new MailingListAlert(name, userid, email, Boolean.parseBoolean(overspeeding),
								Boolean.parseBoolean(geofencing), Boolean.parseBoolean(chargerdisconnected), Boolean.parseBoolean(ignition));
						report = ((MailinglistAlertDaoImp) DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_ALERT_DAO)).update(report);
						LoadMailingAlertUserDetails.getInstance().cacheEmailAlertUsers.put(report.getId().getId(),report);	
					}catch (Exception e) {
						LOG.error("Error while updating alert settigs ",e);
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
					email = jso2.getString("email");
					overspeeding = jso2.getString("overspeeding");
					geofencing = jso2.getString("geofencing");
					chargerdisconnected = jso2.getString("chargerdisconnected");
					ignition=jso2.getString("ignition");
					MailingListAlert report = new MailingListAlert(name, userid, email, Boolean.parseBoolean(overspeeding),
							Boolean.parseBoolean(geofencing), Boolean.parseBoolean(chargerdisconnected),Boolean.parseBoolean(ignition));
					report = ((MailinglistAlertDaoImp) DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_ALERT_DAO)).delete(report);
					LoadMailingAlertUserDetails.getInstance().cacheEmailAlertUsers.remove(report.getId().getId());
					}
			}
			action = " HAS UPDATED ALERT SETTINGS";
		}
		StringUtils.insertLogs(action);
		return modelAndView;
	}
}