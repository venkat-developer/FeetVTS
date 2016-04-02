package com.i10n.dbCacheManager;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.ACLVehicleDaoImp;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.entity.ACLVehicle;
import com.i10n.db.tools.DBManager;

/**
 * 
 * @author Dharmaraju V
 *
 */
public class LoadAclVehicleDetails {

	private static final Logger LOG = Logger.getLogger(LoadAclVehicleDetails.class);
	// Mapping user with list of vehicles assigned to it
	public ConcurrentHashMap<Long, Vector<Long>> cacheAclVehicleDetails = new ConcurrentHashMap<Long, Vector<Long>>();

	static private LoadAclVehicleDetails _instance = null;
	
	private LoadAclVehicleDetails(){};

	public static LoadAclVehicleDetails getInstance() {
		if(null == _instance){
			_instance = new LoadAclVehicleDetails();
			_instance.loadAclVehicleData();
		}
		return _instance;
	}

	
	public Vector<Long> retrieve (long userId) {
		Vector<Long> vehicleIdList = cacheAclVehicleDetails.get(userId);
		if (vehicleIdList != null) {
			return cacheAclVehicleDetails.get(userId);			
		}
		else {
			/* Try to retrieve from DB */
			getDataForNewlyAssignedVehicleForTheUser (userId);
			return cacheAclVehicleDetails.get(userId);	
		}		
	}

	/**
	 * Reload data completely
	 */
	public void refresh() {
		clearCache();
		loadAclVehicleData();
	}

	private void clearCache() {
		cacheAclVehicleDetails.clear();
	}

	private void loadAclVehicleData(){
			LOG.debug("Caching AclVehicleDetails");
			List<ACLVehicle> aclVehicleList = ((ACLVehicleDaoImp)DBManager.getInstance().getDao(DAOEnum.ACL_VEHICLE_DAO)).selectAll();
			processCacheUpdate(aclVehicleList);
			LOG.debug("Caching AclVehicleDetails successful");
	}

	public void getDataForNewlyAssignedVehicleForTheUser(Long userId){
			LOG.debug("Caching AclVehicleDetails for newly assigned vehicle for the user");
			List<ACLVehicle> aclVehicleList = ((ACLVehicleDaoImp)DBManager.getInstance().getDao(DAOEnum.ACL_VEHICLE_DAO)).selectByUserId(userId);
			processCacheUpdate(aclVehicleList);
	}
	
	private void processCacheUpdate(List<ACLVehicle> aclVehicleList){
		Vector<Long> vehicleIdList = null;
		if(aclVehicleList != null ){
		for(ACLVehicle aclVehicle : aclVehicleList){
			if(cacheAclVehicleDetails.containsKey(aclVehicle.getUserid())){
				vehicleIdList = cacheAclVehicleDetails.get(aclVehicle.getUserid());
				vehicleIdList.add(aclVehicle.getVehicleid());
				cacheAclVehicleDetails.put(aclVehicle.getUserid(), vehicleIdList);
			}else{
				vehicleIdList = new Vector<Long>();
				vehicleIdList.add(aclVehicle.getVehicleid());
				cacheAclVehicleDetails.put(aclVehicle.getUserid(), vehicleIdList);
			}
		}
		}
	}
}




