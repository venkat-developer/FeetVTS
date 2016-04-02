package com.i10n.fleet.web.controllers;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import com.i10n.db.dao.ACLReportsDaoImp;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.MailinglistReportDaoImp;
import com.i10n.db.entity.ACLReports;
import com.i10n.db.entity.MailingListReport;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.providers.mock.ReportsDataProvider;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.utils.SessionUtils;

public class ReportSettingsController extends SimpleFormController {
	
	private static Logger LOG = Logger.getLogger(ReportSettingsController.class);

	public enum ReportSettings {

		REPORTSETTINGS_ASSIGN("reportsettings_assign");

		private static Map<String, ReportSettings> lookup = new HashMap<String, ReportSettings>();
		private String val;

		private ReportSettings(String val) {
			this.val = val;
		}

		public String getVal() {
			return val;
		}

		public static ReportSettings get(String val) {
			return lookup.get(val);
		}

		static {
			for (ReportSettings s : EnumSet.allOf(ReportSettings.class)) {
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
		ReportsDataProvider reportoption=new ReportsDataProvider();
		String action = null;
		String operation = request.getParameter("command_type");
		if (operation!=null) {
			if(ReportSettings.get(operation)!=null){
				switch (ReportSettings.get(operation)) {
				case REPORTSETTINGS_ASSIGN: {
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
								avehicles = ((ACLReportsDaoImp) DBManager.getInstance().getDao(DAOEnum.ACL_REPORTS_DAO)).getVehicleIDFromItsName(assinedvehs[i].trim());
							} else {
								continue;
							}
							ACLReports aclReport = new ACLReports(avehicles, reportID);
							try {
								aclReport = ((ACLReportsDaoImp) DBManager.getInstance().getDao(DAOEnum.ACL_REPORTS_DAO)).insert(aclReport);
							} catch (OperationNotSupportedException e) {
								e.printStackTrace();
							}
						}
						for (int i = 0; i < vacantvehs.length; i++) {
							Long vvehicle = null;
							if (!vacantvehs[i].equals("") || vacantvehs[i].length() != 0) {
								vvehicle = ((ACLReportsDaoImp) DBManager.getInstance().getDao(DAOEnum.ACL_REPORTS_DAO)).getVehicleIDFromItsName(vacantvehs[i].trim());
							} else {
								continue;
							}
							ACLReports aclReport = new ACLReports(vvehicle, reportID);
							try {
								aclReport = ((ACLReportsDaoImp) DBManager.getInstance().getDao(DAOEnum.ACL_REPORTS_DAO)).delete(aclReport);
							} catch (OperationNotSupportedException e) {
								e.printStackTrace();
							}
						}
					}
				}
				}
			}
			action = " HAS EDITED VEHICLES REPORT SETTINGS USERS";
			if(operation.equalsIgnoreCase("violation"))
				LOG.debug("this is under if");
			{
				if(reportoption.upadteStatus(request)!= null){
					modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
					action = "HAS ADDED A NEW VEHICLE";
					StringUtils.insertLogs(action);
				}
			}
		}
		else {
			String localTime = request.getParameter("localTime");
			Date clientTime = new Date(localTime);
			Calendar cal = Calendar.getInstance();
			Date dateAdjusted = DateUtils.adjustToServerTime(cal.getTime(), clientTime);
			Timestamp currDate = new Timestamp(dateAdjusted.getTime());
			String name, email, schedule, vehiclestatistics, vehiclestatus, offlinevehiclereport = null;
			Long userid;
			int sch = 0;
			String addeddata = request.getParameter("addeddata");
			JSONArray js = new JSONArray(addeddata);
			for (int i = 0; i < js.length(); i++) {
				JSONObject jso = new JSONObject(js.getString(i));
				name = jso.getString("name");
				userid = SessionUtils.getCurrentlyLoggedInUser().getId();
				email = jso.getString("email");
				schedule = jso.getString("schedule");
				if (schedule.equals("Daily"))
					sch = 0;
				else if (schedule.equals("Weekly"))
					sch = 1;
				else if (schedule.equals("Monthly"))
					sch = 2;
				vehiclestatistics = jso.getString("vehiclestatistics");
				vehiclestatus = jso.getString("vehiclestatus");
				offlinevehiclereport = jso.getString("offlinevehiclereport");
				MailingListReport report = new MailingListReport(name, userid, email, sch, currDate, Boolean
						.parseBoolean(vehiclestatistics), Boolean.parseBoolean(vehiclestatus), Boolean.parseBoolean(offlinevehiclereport));
				report = ((MailinglistReportDaoImp) DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_REPORT_DAO)).insert(report);
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
					schedule = jso1.getString("schedule");

					if (schedule.equals("Daily"))
						sch = 0;
					else if (schedule.equals("Weekly"))
						sch = 1;
					else if (schedule.equals("Monthly"))
						sch = 2;

					vehiclestatistics = jso1.getString("vehiclestatistics");
					vehiclestatus = jso1.getString("vehiclestatus");
					offlinevehiclereport = jso1.getString("offlinevehiclereport");
					MailingListReport report = new MailingListReport(name, userid, email, sch, currDate, Boolean
							.parseBoolean(vehiclestatistics), Boolean.parseBoolean(vehiclestatus), Boolean.parseBoolean(offlinevehiclereport));
					report = ((MailinglistReportDaoImp) DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_REPORT_DAO)).update(report);
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
					schedule = jso2.getString("schedule");

					if (schedule.equals("Daily"))
						sch = 0;
					else if (schedule.equals("Weekly"))
						sch = 1;
					else if (schedule.equals("Monthly"))
						sch = 2;
					vehiclestatistics = jso2.getString("vehiclestatistics");
					vehiclestatus = jso2.getString("vehiclestatus");
					offlinevehiclereport = jso2.getString("offlinevehiclereport");
					MailingListReport report = new MailingListReport(name, userid, email, sch, currDate, Boolean
							.parseBoolean(vehiclestatistics), Boolean.parseBoolean(vehiclestatus), Boolean.parseBoolean(offlinevehiclereport));
					report = ((MailinglistReportDaoImp) DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_REPORT_DAO)).delete(report);
				}
			}
			action = " HAS UPDATED REPORT SETTINGS";
		}
		StringUtils.insertLogs(action);
		return modelAndView;
	}
}