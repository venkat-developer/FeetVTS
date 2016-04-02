package com.i10n.fleet.web.controllers;

import java.sql.Time;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.servlet.mvc.SimpleFormController;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.RoutesDaoImp;
import com.i10n.db.entity.Routes;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.DateUtils;
import com.i10n.fleet.util.StringUtils;

/*
 * Class implementing methods for adding deleting editing routes  * 
 */

public class RouteAdminOperations extends SimpleFormController {
	public boolean editRoute(HttpServletRequest request) {
		boolean opSuccess = true;
		String id = request.getParameter("key");
		Long rsId = Long.parseLong(StringUtils.stripCommas(id));
		String routeName = request.getParameter("routename");
		String startPoint = request.getParameter("startpoint");
		String endpoint = request.getParameter("endpoint");
		String strstime = request.getParameter("starttime");
		Time starttime = DateUtils.convertLocalTimeToRailwayTime(strstime);
		String stretime = request.getParameter("endtime");
		Time endtime = DateUtils.convertLocalTimeToRailwayTime(stretime);
		if (routeName != null && startPoint != null && endpoint != null && starttime != null && endtime != null) {
			Routes route = new Routes(rsId, routeName, startPoint, endpoint, starttime, endtime);
			try {
				route = ((RoutesDaoImp) DBManager.getInstance().getDao(DAOEnum.ROUTES_DAO)).update(route);
			}
			catch (OperationNotSupportedException e) {
				e.printStackTrace();
			}
			if (route == null) {
				opSuccess = false;
			}
		}
		return opSuccess;
	}
}
