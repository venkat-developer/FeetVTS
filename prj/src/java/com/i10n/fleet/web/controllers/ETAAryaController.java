package com.i10n.fleet.web.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.EtaAryaDaoImpl;
import com.i10n.db.entity.EtaArya;
import com.i10n.db.tools.DBManager;

public class ETAAryaController extends SimpleFormController{
	private static Logger LOG = Logger.getLogger(ETAAryaController.class);
	public ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ModelAndView modelAndView = processRequest(request, response);
		return modelAndView;

	}
	//TODO: Manage stream closure and exception handling
	private ModelAndView processRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException, SQLException {
		ModelAndView modelAndView = null;

		if (request.getParameter("stopid") != null) {
			Long busStopId = Long.parseLong(request.getParameter("stopid"));
			System.out.print("stopid:"+busStopId);
			LOG.debug("StopID:"+busStopId);
			List<EtaArya> etaDisplayList = 
					((EtaAryaDaoImpl) DBManager.getInstance().getDao(DAOEnum.ETA_ARYA_DAO)).selectedStopIdForArya(busStopId);
			LOG.debug("After Getting etaDisplayList from selectArya()");
			String returnString = "";
			String status="";
			java.util.Date today = new java.util.Date();
			Timestamp currenttime=new java.sql.Timestamp(today.getTime());
			/**
			 * Check for vehicle approaching to the stop.
			 */
			LOG.debug("After Current Date");
			if (etaDisplayList.size() != 0) {
				LOG.debug("Inside If Condition");
				for(int i=0 ; i<etaDisplayList.size();i++){
					LOG.debug("Inside For loop");
					//List<EtaArya> routes = ((EtaDisplayDaoImp) DBManager.getInstance().getDao(DAOEnum.ETA_DISPLAY_DAO))
					//.selectedStopIdForArya(new LongPrimaryKey(etaDisplayList.get(i).getId()));
					long milliseconds=currenttime.getTime()-etaDisplayList.get(i).getrec_time().getTime();
					//180000 milliseconds is equal to 30 miniutes
					if(milliseconds < 1800000)		
						status="Available";
					else
						status="Not Available";
					LOG.debug("milliseconds:"+milliseconds);
					//returnString=returnString+"HAi";
					returnString = returnString
							// 	id.
							+ etaDisplayList.get(i).getId() + ";" +
							// Bus Number.
							etaDisplayList.get(i).getStopId() + ";" +
							// Receiving Time.
							etaDisplayList.get(i).getrec_time() + ";" +
							// Status check.
							status ;
					LOG.debug("Ret String:"+returnString);
				}
				
			} else {
				LOG.debug("ETAETAETAETAETAETA  No Stops Approaching");
				returnString = "No Stops Approaching";
			}
			LOG.debug("Return String:"+returnString);
			PrintWriter out = response.getWriter();
			out.println(returnString);
			out.close();
			out.flush();
			
		}
		return modelAndView;
	}

}
