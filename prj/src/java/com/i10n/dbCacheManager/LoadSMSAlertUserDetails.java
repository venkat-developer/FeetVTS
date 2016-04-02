package com.i10n.dbCacheManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.MobileNumberDaoImp;
import com.i10n.db.entity.MobileNumber;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;

/**
 * 
 * @author Dharmaraju V
 *
 */
public class LoadSMSAlertUserDetails {

	private static final Logger LOG = Logger.getLogger(LoadSMSAlertUserDetails.class);
	// Mapping mobileId against its entity
	public ConcurrentHashMap<Long, MobileNumber> cacheSMSAlertUsers = new ConcurrentHashMap<Long, MobileNumber>();

	static private LoadSMSAlertUserDetails _instance = null;
	private LoadSMSAlertUserDetails(){};

	public static LoadSMSAlertUserDetails getInstance() {
		if(null == _instance){
			_instance = new LoadSMSAlertUserDetails();
			_instance.loadDataForMobileAlertUsers();
		}
		return _instance;
	}

	/**
	 * Retrieves MobileAlert corresponding to the mobile id specified
	 * @param mobileNumber
	 * @return
	 */
	public MobileNumber retrieve (long mobileId) {
		MobileNumber mobileNumber = cacheSMSAlertUsers.get(mobileId);
		if (mobileNumber != null) {
			return cacheSMSAlertUsers.get(mobileId);			
		}
		else {
			/* Try to retrieve from DB */
			getDataForNewlyAddedMobileUser (mobileId);
			return cacheSMSAlertUsers.get(mobileId);	
		}		
	}

	private void loadDataForMobileAlertUsers(){
		LOG.debug("Caching Mobile alert user details");
		List<MobileNumber> mobileNumberList = ((MobileNumberDaoImp)DBManager.getInstance().getDao(DAOEnum.MOBILENUMBER_DAO)).selectAll();
		processCacheUpdate(mobileNumberList);
		LOG.debug("Caching Mobile alert user details successful");
	}

	private void processCacheUpdate(List<MobileNumber> mobileNumberList) {
		if(mobileNumberList != null){
			for(MobileNumber mobileNumber : mobileNumberList){
				cacheSMSAlertUsers.put(mobileNumber.getId().getId(), mobileNumber);
			}
		}

	}

	public void getDataForNewlyAddedMobileUser(Long mobileAlertId){
		LOG.debug("Caching newly adder Mobile alert user details");
		List<MobileNumber> mobileNumberList = ((MobileNumberDaoImp)DBManager.getInstance().getDao(DAOEnum.MOBILENUMBER_DAO)).
				selectByPrimaryKey(new LongPrimaryKey(mobileAlertId));
		processCacheUpdate(mobileNumberList);
		LOG.debug("Finished caching newly added Mobile alert user details");
	}
	
	public void refresh() {
		clearCache();
		loadDataForMobileAlertUsers();
	}

	private void clearCache() {
		cacheSMSAlertUsers.clear();
	}
}



