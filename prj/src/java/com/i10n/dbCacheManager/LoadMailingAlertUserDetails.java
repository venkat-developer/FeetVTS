package com.i10n.dbCacheManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.MailinglistAlertDaoImp;
import com.i10n.db.entity.MailingListAlert;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;

/**
 * 
 * @author Dharmaraju V
 *
 */
public class LoadMailingAlertUserDetails {

	private static final Logger LOG = Logger.getLogger(LoadMailingAlertUserDetails.class);
	public ConcurrentHashMap<Long, MailingListAlert> cacheEmailAlertUsers = new ConcurrentHashMap<Long, MailingListAlert>();

	static private LoadMailingAlertUserDetails _instance = null;
	private LoadMailingAlertUserDetails(){};

	public static LoadMailingAlertUserDetails getInstance() {
		if(null == _instance){
			_instance = new LoadMailingAlertUserDetails();
			_instance.loadDataForEmailAlertUsers();
		}
		return _instance;
	}

	/**
	 * Retrieves EmailAlert corresponding to the imei
	 * @param imei
	 * @return
	 */
	public MailingListAlert retrieve (long alertUserId) {
		MailingListAlert mailingListAlert = cacheEmailAlertUsers.get(alertUserId);
		if (mailingListAlert != null) {
			return cacheEmailAlertUsers.get(alertUserId);			
		}
		else {
			/* Try to retrieve from DB */
			getDataForNewlyAddedEmailUser (alertUserId);
			return cacheEmailAlertUsers.get(alertUserId);	
		}		
	}

	private void loadDataForEmailAlertUsers(){
		LOG.debug("Caching Email alert user details");
		List<MailingListAlert> mailingListAlertList = ((MailinglistAlertDaoImp)DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_ALERT_DAO)).selectAll();
		processCacheUpdate(mailingListAlertList);
		LOG.debug("Caching Email alert user details successful");
	}

	private void processCacheUpdate(List<MailingListAlert> mailingListAlertList) {
		if(mailingListAlertList != null){
			for(MailingListAlert mailingListAlert : mailingListAlertList){
				cacheEmailAlertUsers.put(mailingListAlert.getId().getId(), mailingListAlert);
			}
		}
	}

	public void getDataForNewlyAddedEmailUser(Long id){
		LOG.debug("Caching newly adder Email alert user details");
		List<MailingListAlert> mailingListAlertList = ((MailinglistAlertDaoImp)DBManager.getInstance().getDao(DAOEnum.MAILINGLIST_ALERT_DAO)).
				selectByPrimaryKey(new LongPrimaryKey(id));
		processCacheUpdate(mailingListAlertList);
	}
	
	public void refresh() {
		clearCache();
		loadDataForEmailAlertUsers();
	}

	private void clearCache() {
		cacheEmailAlertUsers.clear();
	}
}



