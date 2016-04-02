package com.i10n.dbCacheManager;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import com.i10n.db.dao.ACLAlertsDAOImp;
import com.i10n.db.dao.DAOEnum;
import com.i10n.db.entity.ACLAlerts;
import com.i10n.db.tools.DBManager;

/**
 * 
 * @author Dharmaraju V
 *
 */
public class LoadAclAlertDetails {

	// Mapping user with list of drivers assigned to it
	public ConcurrentHashMap<Long, Vector<Long>> cacheAclAlertsDetails = new ConcurrentHashMap<Long, Vector<Long>>();

	static private LoadAclAlertDetails _instance = null;
	private LoadAclAlertDetails(){};

	public static LoadAclAlertDetails getInstance() {
		if(null == _instance){
			_instance = new LoadAclAlertDetails();
			_instance.loadAclAlertsData();
		}
		return _instance;
	}


	public Vector<Long> retrieve (long userId) {
		Vector<Long> alertsIdList = cacheAclAlertsDetails.get(userId);
		if (alertsIdList == null) {
			/* Try to retrieve from DB */
			getDataForNewlyAssignedAlertsForTheUser (userId);
		}
		return cacheAclAlertsDetails.get(userId);
	}

	private void loadAclAlertsData(){
		List<ACLAlerts> aclAlertList = ((ACLAlertsDAOImp)DBManager.getInstance().getDao(DAOEnum.ACL_ALERTS_DAO)).selectAll();
		processCacheUpdate(aclAlertList);
	}

	public void getDataForNewlyAssignedAlertsForTheUser(Long alertuserId){
		List<ACLAlerts> aclAlertList = ((ACLAlertsDAOImp)DBManager.getInstance().getDao(DAOEnum.ACL_ALERTS_DAO)).selectByAlertUserId(alertuserId);
		processCacheUpdate(aclAlertList);
	}

	private void processCacheUpdate(List<ACLAlerts> aclAlertList){
		Vector<Long> vehicleIdList = null;
		if(aclAlertList != null){
			for(ACLAlerts alcAlert : aclAlertList){
				if(cacheAclAlertsDetails.containsKey(alcAlert.getalertUserId())){
					vehicleIdList = cacheAclAlertsDetails.get(alcAlert.getalertUserId());
					vehicleIdList.add(alcAlert.getVehicleId());
					cacheAclAlertsDetails.put(alcAlert.getalertUserId(), vehicleIdList);
				}else{
					vehicleIdList = new Vector<Long>();
					vehicleIdList.add(alcAlert.getVehicleId());
					cacheAclAlertsDetails.put(alcAlert.getalertUserId(), vehicleIdList);
				}	
			}
		}
	}
	
	public void refresh() {
		clearCache();
		loadAclAlertsData();
	}

	private void clearCache() {
		cacheAclAlertsDetails.clear();
	}
}