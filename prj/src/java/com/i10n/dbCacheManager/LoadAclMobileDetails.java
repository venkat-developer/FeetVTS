package com.i10n.dbCacheManager;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.ACLMobileDaoImp;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.entity.ACLMobile;
import com.i10n.db.tools.DBManager;

/**
 * 
 * @author Dharmaraju V
 *
 */
public class LoadAclMobileDetails {

	private static final Logger LOG = Logger.getLogger(LoadAclMobileDetails.class);
	// Mapping user with list of vehicles assigned to it
	public ConcurrentHashMap<Long, Vector<Long>> cacheAclMobileDetails = new ConcurrentHashMap<Long, Vector<Long>>();

	static private LoadAclMobileDetails _instance = null;
	private LoadAclMobileDetails(){};

	public static LoadAclMobileDetails getInstance() {
		if(null == _instance){
			_instance = new LoadAclMobileDetails();
			_instance.loadAclMobileData();
		}
		return _instance;
	}

	public Vector<Long> retrieve (long mobileId) {
		if (cacheAclMobileDetails.get(mobileId) == null) {
			/* Try to retrieve from DB */
			getDataForNewlyAssignedMobileForTheUser (mobileId);
		}
		return cacheAclMobileDetails.get(mobileId);
	}

	/**
	 * Get the vehicles mobile numbers assigned to this vehicle
	 * @param vehicleId
	 * @return
	 */
	public Vector<Long> retrieveFromVehicleId (long vehicleId) {
		Vector<Long> mobileIdList = new Vector<Long>();
		for(Long mobileId : cacheAclMobileDetails.keySet()){
			if(cacheAclMobileDetails.get(mobileId).contains(vehicleId)){
				mobileIdList.add(mobileId);
			}
		}
		return mobileIdList;
	}

	private void loadAclMobileData(){
		LOG.debug("Caching AclMobileDetails");
		List<ACLMobile> aclMobileList = ((ACLMobileDaoImp)DBManager.getInstance().getDao(DAOEnum.ACL_MOBILE_DAO)).selectAll();
		LOG.debug(" ACL mobile List is "+aclMobileList);;
		processCacheUpdate(aclMobileList);
		LOG.debug("Caching AclMobileDetails successful");
	}

	public void getDataForNewlyAssignedMobileForTheUser(Long mobileId){
		List<ACLMobile> aclMobileList = ((ACLMobileDaoImp)DBManager.getInstance().getDao(DAOEnum.ACL_MOBILE_DAO)).selectByMobileId(mobileId);
		processCacheUpdate(aclMobileList);
	}

	private void processCacheUpdate(List<ACLMobile> aclMobileList){
		Vector<Long> vehicleIdList = null;
		if(aclMobileList != null){
			for(ACLMobile aclMobile : aclMobileList){
				LOG.debug("ACLmobile is mobile id is "+aclMobile.getMobileId());
				if(cacheAclMobileDetails.containsKey(aclMobile.getMobileId())){
					vehicleIdList = cacheAclMobileDetails.get(aclMobile.getMobileId());
					vehicleIdList.add(aclMobile.getVehicleid());
					cacheAclMobileDetails.put(aclMobile.getMobileId(), vehicleIdList);
				}else{
					vehicleIdList = new Vector<Long>();
					vehicleIdList.add(aclMobile.getVehicleid());
					cacheAclMobileDetails.put(aclMobile.getMobileId(), vehicleIdList);
				}
			}
		}
	}
	
	public void refresh() {
		clearCache();
		loadAclMobileData();
	}

	private void clearCache() {
		cacheAclMobileDetails.clear();
	}
}