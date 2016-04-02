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
import com.i10n.db.entity.EtaDisplay;
import com.i10n.db.tools.DBManager;

/**
 * Controller to return the list of vehicles approaching to the bus stop. 
 * @author dharmaraju
 *
 */
public class ETAController extends SimpleFormController {

	private static Logger LOG = Logger.getLogger(ETAController.class);

	/**
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
	 * Funciton Procession the request for the list of vehicles approaching bus stop.
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	private ModelAndView processRequest(HttpServletRequest request,HttpServletResponse response) throws IOException {
		ModelAndView modelAndView = null;
	
		if(request.getParameter("stopid")!=null){
			Long busStopId = Long.parseLong(request.getParameter("stopid"));
	    	List<EtaDisplay> etaDisplayList = ((EtaDisplayDaoImp)DBManager.getInstance().getDao(DAOEnum.ETA_DISPLAY_DAO)).selectByStopId(busStopId);
	    	String returnString = "";
	    	if(etaDisplayList.size() != 0){
	    		LOG.debug("ETAETAETAETAETAETA Vehicle Approaching");
	    		for(int i=0 ; i<etaDisplayList.size();i++){
	    			returnString =returnString+"	"+ (i+1)+"	"+etaDisplayList.get(i).getRouteName()+"	"+etaDisplayList.get(i).getArrivalTime()+" minutes";
	    		}
	    	}else{
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
