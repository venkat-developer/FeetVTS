package com.i10n.dbCacheManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.GroupValuesDaoImpl;
import com.i10n.db.entity.GroupValues;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;

/**
 * 
 * @author Dharmaraju V
 *
 */
public class LoadGroupDetails {

	private static Logger LOG = Logger.getLogger(LoadGroupDetails.class);

	private static LoadGroupDetails _instance = null;

	public ConcurrentHashMap<Long/*Group instance id */, GroupValues/*Group details*/> cacheGroups = 
			new ConcurrentHashMap<Long, GroupValues>(); 

	public static LoadGroupDetails getInstance() {
		if(_instance == null){
			_instance = new LoadGroupDetails();
			_instance.loadGroupDetails();
		}
		return _instance;
	}

	public GroupValues retrieve(Long id){
		if(id == null){
			return null;
		}
		GroupValues group = cacheGroups.get(id);
		if(group == null){
			getDetailsOfUncachedGroups(id);
			group = cacheGroups.get(id);
		}
		return group;
	}

	private void loadGroupDetails(){
		LOG.debug("Caching group vales details");
		List<GroupValues> groupValueList = ((GroupValuesDaoImpl)DBManager.getInstance().getDao(DAOEnum.GROUP_VALUES_DAO)).selectAll();
		processCacheUpdate(groupValueList);
		LOG.debug("Caching group vales details successful");
	}

	private void processCacheUpdate(List<GroupValues> groupValueList) {
		if(groupValueList != null){
			for(GroupValues groupValue : groupValueList){
				cacheGroups.put(groupValue.getId().getId(), groupValue);				
			}
		}
	}

	public void refresh() {
		clearCache();
		loadGroupDetails();
	}

	private void clearCache() {
		cacheGroups.clear();
	}

	public void getDetailsOfUncachedGroups(Long id){
		LOG.debug("Caching newly added group vales details");
		List<GroupValues> groupValueList = ((GroupValuesDaoImpl)DBManager.getInstance().getDao(DAOEnum.GROUP_VALUES_DAO)).selectByPrimaryKey(new LongPrimaryKey(id));
		processCacheUpdate(groupValueList);
	}

}
