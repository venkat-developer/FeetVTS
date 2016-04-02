package com.i10n.dbCacheManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.UserDaoImp;
import com.i10n.db.entity.User;
import com.i10n.db.entity.primarykey.LongPrimaryKey;
import com.i10n.db.tools.DBManager;

/**
 * 
 * @author Dharmaraju V
 *
 */
public class LoadUserDetails {

	private static final Logger LOG = Logger.getLogger(LoadUserDetails.class);
	// Mapping userid with its details 
	public ConcurrentHashMap<Long, User> cacheUserDetails = new ConcurrentHashMap<Long, User>();

	static private LoadUserDetails _instance = null;
	private LoadUserDetails(){
		
	};

	public static LoadUserDetails getInstance() {
		if(null == _instance){
			_instance = new LoadUserDetails();
			_instance.loadUserData();
		}
		return _instance;
	}


	public User retrieve (long userId) {
		User user = cacheUserDetails.get(userId);
		if (user == null) {
			/* Try to retrieve from DB */
			getDataForNewlyAddedUser (userId);
			user = cacheUserDetails.get(userId);
		}
		return user;
	}

	public void loadUserData(){
		LOG.debug("Caching User Details");
		List<User> userList =null;
		try{
			userList = ((UserDaoImp)DBManager.getInstance().getDao(DAOEnum.USER_DAO)).selectAll();	
		}catch(Exception e){
			LOG.error("Error while loading the User data ",e);
		}
		LOG.debug("Caching User Details list is "+userList);
		processCacheUpdate(userList);
		LOG.debug("Caching User Details sucqcessful");
	}

	private void processCacheUpdate(List<User> userList) {
		if(userList != null){
			LOG.debug("User List is not null "+userList.size());
			for(User user : userList){
				cacheUserDetails.put(user.getId(), user);
			}
		}else{
			LOG.debug("User List is not null ");
		}
	}

	public void getDataForNewlyAddedUser(Long userId){
		LOG.debug("Caching UserDetails for newly added user");
		List<User> userList = ((UserDaoImp)DBManager.getInstance().getDao(DAOEnum.USER_DAO)).selectByPrimaryKey(new LongPrimaryKey(userId));
		processCacheUpdate(userList);
	}
	
	public void refresh() {
		clearCache();
		loadUserData();
	}

	private void clearCache() {
		cacheUserDetails.clear();
	}
}




