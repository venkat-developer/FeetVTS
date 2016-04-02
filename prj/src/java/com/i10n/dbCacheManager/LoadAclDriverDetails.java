package com.i10n.dbCacheManager;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.ACLDriverDaoimpl;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.entity.ACLDriver;
import com.i10n.db.tools.DBManager;

/**
 * 
 * @author Dharmaraju V
 *
 */
public class LoadAclDriverDetails {

	private static final Logger LOG = Logger.getLogger(LoadAclDriverDetails.class);
	// Mapping user with list of drivers assigned to it
	public ConcurrentHashMap<Long, Vector<Long>> cacheAclDriverDetails = new ConcurrentHashMap<Long, Vector<Long>>();

	static private LoadAclDriverDetails _instance = null;
	private LoadAclDriverDetails(){};

	public static LoadAclDriverDetails getInstance() {
		if(null == _instance){
			_instance = new LoadAclDriverDetails();
			_instance.loadAclDriverData();
		}
		return _instance;
	}

	public Vector<Long> retrieve (long userId) {
		if (cacheAclDriverDetails.get(userId) == null) {
			getDataForNewlyAssignedDriverForTheUser (userId);
		}
		return cacheAclDriverDetails.get(userId);
	}

	private void loadAclDriverData(){
		LOG.debug("Caching AclDriverDetails");
		List<ACLDriver> aclDriverList = ((ACLDriverDaoimpl)DBManager.getInstance().getDao(DAOEnum.ACL_DRIVER_DAO)).selectAll();
		processCacheUpdate(aclDriverList);
		LOG.debug("Caching AclDriverDetails successful ");
	}

	public void refresh() {
		clearCache();
		loadAclDriverData();
	}
	
	private void clearCache() {
		cacheAclDriverDetails.clear();
	}

	public void getDataForNewlyAssignedDriverForTheUser(Long userId){
		LOG.debug("Caching AclDriverDetails for newly assigned driver for the user");
		List<ACLDriver> aclDriverList = ((ACLDriverDaoimpl)DBManager.getInstance().getDao(DAOEnum.ACL_DRIVER_DAO)).selectByUserId(userId);
		processCacheUpdate(aclDriverList);
	}

	public void processCacheUpdate(List<ACLDriver> aclDriverList){
		Vector<Long> driverIdList = null;
		if(aclDriverList != null){
			for(ACLDriver aclDriver: aclDriverList){
				if(cacheAclDriverDetails.containsKey(aclDriver.getUserid())){
					driverIdList = cacheAclDriverDetails.get(aclDriver.getUserid());
					driverIdList.add(aclDriver.getDriverid());
					cacheAclDriverDetails.put(aclDriver.getUserid(), driverIdList);
				}else{
					driverIdList = new Vector<Long>();
					driverIdList.add(aclDriver.getDriverid());
					cacheAclDriverDetails.put(aclDriver.getUserid(), driverIdList);
				}
			}
		}
	}
}




