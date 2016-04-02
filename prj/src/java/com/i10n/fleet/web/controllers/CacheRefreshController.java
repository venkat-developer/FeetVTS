package com.i10n.fleet.web.controllers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.i10n.dbCacheManager.CacheManager;
import com.i10n.dbCacheManager.LoadEmriVehiclesBaseStationDetails;
import com.i10n.dbCacheManager.LoadImeiSequenceMap;
import com.i10n.fleet.util.EnvironmentInfo;

/**
 * Controller class to process the ad-hoc cache refresh request
 * Request format http://hostname/fleet/control/cr/?category=X&entity=Y
 * X = 0 : Fundamentals
 * X = 1 : Alerts
 * X = 2 : ETA
 * X = 3 : Others 
 * 
 * Y = 0 : All entities
 * Y = 1 : ImeiSequenceMapEntity
 * Y = 2 : VehicleBaseStationEntity
 * @author DVasudeva
 *
 */
public class CacheRefreshController extends SimpleFormController {

	private static Logger LOG = Logger.getLogger(CacheRefreshController.class);
	
	private static final int FUNDAMENTAL_CACHE = 0;
	private static final int ALERTS_CACHE = 1;
	private static final int ETA_CACHE = 2;
	private static final int OTHERS_CACHE = 3;
	
	private static final int ALL_ENTITIES = 0;
	private static final int IMEI_SEQUENCE_MAP_ENTITY = 1;
	private static final int VEHICLE_BASE_STATION_ENTITY = 2;
	

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
	 * @param request
	 * @return 
	 * <b>SUCCESS </b> on successful cache refresh processing
	 * <b>FAILURE </b> on error
	 * <b>INVALID_REQUEST </b> on error with the request format
	 * @throws IOException
	 */
	private ModelAndView processRequest(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String returnString = "INVALID_REQUEST";
		LOG.info("Processing Cache refresh request");
		String category = request.getParameter("category");
		if (category != null) {
			LOG.info("Category : "+category);
			returnString = processCacheRefreshByCategory(request, Integer.parseInt(category));
		}
		LOG.info("ReturnString : "+returnString);
		PrintWriter out = response.getWriter();
		out.println(returnString);
		out.close();
		out.flush();
		return null;
	}

	/**
	 * 
	 * @param request 
	 * @param refreshCategory
	 * @return SUCCESS or FAILURE
	 */
	private String processCacheRefreshByCategory(HttpServletRequest request, int refreshCategory) {
		try{
			switch (refreshCategory) {
			case FUNDAMENTAL_CACHE:{
				CacheManager.refreshFundamentalCacheList();
			}
			break;
			case ALERTS_CACHE:{
				CacheManager.refreshAlertsCacheList();
			}
			break;

			case ETA_CACHE:{
				CacheManager.refreshETACacheList();
			}
			break;

			case OTHERS_CACHE:{
				String entity = request.getParameter("entity");
				if(entity == null){
					CacheManager.refreshOtherCacheList();	
				} else {
					LOG.info("Entity : "+entity);
					switch (Integer.parseInt(entity)) {
					case ALL_ENTITIES:{
						CacheManager.refreshOtherCacheList();	
					}
						break;
					
					case IMEI_SEQUENCE_MAP_ENTITY:{
						LOG.debug("Refreshing ImeiSequenceMap");
						LoadImeiSequenceMap.getInstance().refresh();
						LOG.debug("Successfully refreshed ImeiSequenceMap");
					}
						break;

					case VEHICLE_BASE_STATION_ENTITY:{
						if(Boolean.valueOf(EnvironmentInfo.getProperty("IS_FRS_EMRI_CLIENT"))){
							LOG.debug("Refreshing EmriVehiclesBaseStation");
							LoadEmriVehiclesBaseStationDetails.getInstance().refresh();
							LOG.debug("Successfully refreshed EmriVehiclesBaseStation");
						} else{
							LOG.debug("Not an FRS Client");
						}
					}
						break;
						
					default:
						LOG.error("INVALID Entity request");
						return "INVALID_REQUEST";
					}
				}
			}
			break;

			default:
				LOG.error("INVALID Category request");
				return "INVALID_REQUEST";
			}
		} catch(Exception e){
			LOG.error("Error while refreshing cache",e);
			return "FAILURE";
		}
		
		return "SUCCESS";
	}
}
