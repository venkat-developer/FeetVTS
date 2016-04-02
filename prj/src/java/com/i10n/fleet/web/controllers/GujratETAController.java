package com.i10n.fleet.web.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.EtaDisplayDaoImp;
import com.i10n.db.dao.RoutesDaoImp;
import com.i10n.db.dao.StopsDaoImp;
import com.i10n.db.entity.EtaDisplay;
import com.i10n.db.entity.Routes;
import com.i10n.db.entity.Stops;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.exceptions.OperationNotSupportedException;

/**
 * Controller to return the list of vehicles approaching to the bus stop. For
 * Gujrat.
 * 
 * @author dharmaraju
 * 
 */
public class GujratETAController extends SimpleFormController {

	private static Logger LOG = Logger.getLogger(GujratETAController.class);
	
	/**
	 * @see SimpleFormController#handleRequest(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@Override
	public ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = processRequest(request, response);
		return modelAndView;

	}

	/**
	 * Funciton Procession the request for the list of vehicles approaching bus
	 * stop.
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private ModelAndView processRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		ModelAndView modelAndView = null;

		if (request.getParameter("stopid") != null) {
			Long busStopId = Long.parseLong(request.getParameter("stopid"));
			List<EtaDisplay> etaDisplayList = ((EtaDisplayDaoImp) DBManager.getInstance().getDao(DAOEnum.ETA_DISPLAY_DAO)).selectByStopIdForGujrat(busStopId);
			String returnString = "";

			/**
			 * Check for vehicle approaching to the stop.
			 */
			if (etaDisplayList.size() != 0) {
				for (int i = 0; i < etaDisplayList.size(); i++) {
					if ((etaDisplayList.get(i).getType() == 1) && !(etaDisplayList.get(i).isDeleted())) {

						List<Routes> routes = ((RoutesDaoImp) DBManager.getInstance().getDao(DAOEnum.ROUTES_DAO))
								.selectByPrimaryKey(new LongPrimaryKey(etaDisplayList.get(i).getRouteId()));
						List<Stops> stop = ((StopsDaoImp) DBManager.getInstance().getDao(DAOEnum.STOPS_DAO))
								.selectByPrimaryKey(new LongPrimaryKey(busStopId));
						returnString = returnString
								// 	Bus is arriving or Bus has arrived.
								+ etaDisplayList.get(i).getType() + "," +
								// Bus Number.
								etaDisplayList.get(i).getRouteName() + "," +
								// Source of the Route.
								routes.get(0).getStartPoint() + "," +
								// Destination of the Route.
								routes.get(0).getEndPoint() + "," +
								// Stop to which the vehicle is arriving.
								stop.get(0).getStopName() + "," +
								// Etd in minutes.
								"0#";
						/**
						 * Vehicle is arriving .. Once notified to the
						 * passengers then wait till vehicle reaches.
						 **/
						((EtaDisplayDaoImp) DBManager.getInstance().getDao(DAOEnum.ETA_DISPLAY_DAO)).deleteType(etaDisplayList.get(i));
					} else if ((etaDisplayList.get(i).getType() == 2)) {
						List<Routes> routes = ((RoutesDaoImp) DBManager.getInstance().getDao(DAOEnum.ROUTES_DAO))
								.selectByPrimaryKey(new LongPrimaryKey(etaDisplayList.get(i).getRouteId()));
						List<Stops> stop = ((StopsDaoImp) DBManager.getInstance().getDao(DAOEnum.STOPS_DAO))
								.selectByPrimaryKey(new LongPrimaryKey(busStopId));
						returnString = returnString
								// Bus is arriving or Bus has arrived.
								+ etaDisplayList.get(i).getType() + "," +
								// Bus Number.
								etaDisplayList.get(i).getRouteName() + "," +
								// Source of the Route.
								routes.get(0).getStartPoint() + "," +
								// Destination of the Route.
								routes.get(0).getEndPoint() + "," +
								// Stop to which the vehicle is arriving.
								stop.get(0).getStopName() + "," +
								// Etd in minutes.
								"0#";
						/**
						 * Vehicle has reached .. Once notified to the
						 * passengers then mark it as deleted.
						 **/
						try {
							((EtaDisplayDaoImp) DBManager.getInstance().getDao(DAOEnum.ETA_DISPLAY_DAO)).delete(etaDisplayList.get(i));
						} catch (OperationNotSupportedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						LOG.debug("ETAETAETAETAETAETA  Type Not Updated yet.");
						returnString = "No Vehicle Approaching";
					}
					
				}
			} else {
				LOG.debug("ETAETAETAETAETAETA  No Vehicle Approaching");
				returnString = "No Vehicle Approaching";
			}
			PrintWriter out = response.getWriter();
			out.println(returnString);
			out.close();
			out.flush();
		}
		return modelAndView;
	}

}
