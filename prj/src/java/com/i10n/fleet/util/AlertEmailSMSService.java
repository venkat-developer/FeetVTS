package com.i10n.fleet.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.i10n.db.entity.AlertOrViolation;

/**
 * This class is responsible for asynchronously
 * sending out the alerts to subscribed users
 * by email and SMS
 * 
 * @author vishnu
 *
 */
public class AlertEmailSMSService {
	
	private static Logger LOG = Logger.getLogger(AlertEmailSMSService.class);
	
	private BlockingQueue<AlertOrViolation> alertQueue;
	
	private static AlertEmailSMSService instance = null;
	
	/**
	 * Private constructor
	 */
	private AlertEmailSMSService () {
		alertQueue = new ArrayBlockingQueue<AlertOrViolation>(1000);
	}
	
	/**
	 * Singleton access
	 * @return
	 */
	public static AlertEmailSMSService getInstance () {
		if (instance == null) {
			instance = new AlertEmailSMSService();
		}
		return instance;
	}

	
	/**
	 * Receive alerts and alert users by SMS and email
	 */
	public void notifyAlerts (AlertOrViolation alert) {
		LOG.debug("Inserting alert into the queue. Size = "+(alertQueue.size()+1));
		alertQueue.offer(alert);
	}
	
	public AlertOrViolation retrieve() throws InterruptedException {
		LOG.debug("Retrieving alert from the queue. Size = "+(alertQueue.size()-1));
		return alertQueue.take();
	}
	
	

}
