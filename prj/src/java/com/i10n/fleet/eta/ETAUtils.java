package com.i10n.fleet.eta;

import org.apache.log4j.Logger;

import com.i10n.db.entity.VehicleToRouteSchedule;

/**
 * Maintaining the generalised utilities under one wrap 
 * @author Dharmaraju V
 *
 */
public class ETAUtils {
	
	private static Logger LOG = Logger.getLogger(ETAUtils.class);
	
	/**
	 * Formulate the RouteSchedule ID for other usage
	 * @param vehicleToRouteSchedule
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String getRouteScheduleId(VehicleToRouteSchedule vehicleToRouteSchedule) {
		// Formulate the id in the format : "RouteId-HOurs:Minutes" where hours and minutes are two in length
		String hours = vehicleToRouteSchedule.getScheduleTime().getHours()+"";
		String minutes = vehicleToRouteSchedule.getScheduleTime().getMinutes()+"";
		if(hours.length() == 1)
			hours = "0"+hours;
		if(minutes.length() == 1)
			minutes = "0"+minutes;

		String formulatedRouteScheduleId = vehicleToRouteSchedule.getRouteId()+"-"+hours+":"+minutes;
		LOG.debug("Formulated RouteSchedule Id : "+formulatedRouteScheduleId);
		return formulatedRouteScheduleId.trim();
	}

}
