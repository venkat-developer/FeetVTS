package com.i10n.db.dao;

import org.apache.log4j.Logger;

import com.i10n.db.entity.AlertOrViolation;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.AlertEmailSMSService;

/**
 * Thread for managing Alert insertion for the command packets to improve the response time to the module
 * @author DVasudeva
 *
 */
public class AlertInsertManager implements Runnable {
	
	private static Logger LOG = Logger.getLogger(AlertInsertManager.class);
	
	private AlertOrViolation alertOrViolation; 
	
	public AlertInsertManager(AlertOrViolation alertOrViolation) {
		this.alertOrViolation = alertOrViolation; 
	}

	@Override
	public void run() {
		LOG.debug("Processing Alert insertion for command packet");
		try {
			((AlertDaoImpl)DBManager.getInstance().getDao(DAOEnum.ALERT_DAO)).insert(alertOrViolation);
		} catch (OperationNotSupportedException e) {
			LOG.error("Error while inserting alert into table ",e);
		}
		AlertEmailSMSService.getInstance().notifyAlerts(alertOrViolation);
	}
	
	

}
