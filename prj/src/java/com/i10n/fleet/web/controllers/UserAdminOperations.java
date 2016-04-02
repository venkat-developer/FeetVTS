package com.i10n.fleet.web.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.mvc.SimpleFormController;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.UserDaoImp;
import com.i10n.db.entity.User;
import com.i10n.db.tools.DBManager;
import com.i10n.dbCacheManager.LoadAclDriverDetails;
import com.i10n.dbCacheManager.LoadAclVehicleDetails;
import com.i10n.dbCacheManager.LoadTripDetails;
import com.i10n.dbCacheManager.LoadUserDetails;
import com.i10n.fleet.exceptions.OperationNotSupportedException;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.utils.SessionUtils;

/*
 * Class implementing methods for adding deleting editing and assigning vehicles  * 
 */

public class UserAdminOperations extends SimpleFormController{

	private static final Logger LOG = Logger.getLogger(UserAdminOperations.class);

	/**
	 * 
	 * @param request
	 * @return
	 * <b>User Entity </b> on success <br>
	 * <b>null</b> on failure
	 */
	public User addUser(HttpServletRequest request){
		LOG.debug("Adding new user");

		User currentUser = SessionUtils.getCurrentlyLoggedInUser();
		String login = request.getParameter("loginid");
		String password = request.getParameter("passwd");
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
	
		User user = new User(login.trim(), password.trim(), currentUser.getId(), firstname.trim(), lastname.trim(), currentUser.getGroupId());
		try {
			user = ((UserDaoImp) DBManager.getInstance().getDao(
					DAOEnum.USER_DAO)).insert(user);
			if(user == null){
				LOG.error("Insertion was a failure ");
				return null;
			} else {
				// Update cache on successful DB update
				LOG.debug("Updating cache on successful DB insertion");
				LoadUserDetails.getInstance().cacheUserDetails.put(user.getId(), user);
			}
		} catch (OperationNotSupportedException e) {
			LOG.error(e);
		} catch (Exception e){
			LOG.error(e);
		}
		return user;
		
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 * <b>true</b> on success <br>
	 * <b>false</b> on failure
	 */
	public boolean editUser(HttpServletRequest request){
		boolean opSuccess = true;
		
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		String password = request.getParameter("passwd");
		String offroadCount = request.getParameter("offroadcount");
		String noGPRSCount = request.getParameter("nogprscount");
		String key = request.getParameter("key");
		Long id = Long.parseLong(StringUtils.stripCommas(key.trim()).trim());
		if ( password != null) {
			User user = LoadUserDetails.getInstance().retrieve(id);
			user.setFirstname(firstname);
			user.setLastname(lastname);
			user.setPassword(password);
			user.setOwnerId(SessionUtils.getCurrentlyLoggedInUser().getId());
			user.setOffroadCount(Integer.parseInt(offroadCount.trim()));
			user.setNoGPRSCount(Integer.parseInt(noGPRSCount.trim()));
			try {
				user = ((UserDaoImp) DBManager.getInstance().getDao(
						DAOEnum.USER_DAO)).update(user);
				if (user == null) {
					opSuccess = false;
				} else {
					// Update cache on successful DB update
					LOG.debug("Updating cache on successful DB update");
					LoadUserDetails.getInstance().cacheUserDetails.put(id, user);
				}
			} catch (OperationNotSupportedException e) {
				LOG.error(e);
			} catch (Exception e){
				LOG.error("Handling general exception while editing user details",e);
			}
		}
		return opSuccess;
		
	}
	

	/**
	 * @param request
	 * @return
	 * <b>true</b> on success <br>
	 * <b>false</b> on failure
	 */
	public boolean deleteUser(HttpServletRequest request) {
		User user;
		boolean opSuccess = true;
		String userList = request.getParameter("userList");
		String[] userIds = userList.split(":");
		if (userIds != null) {
			for (int i = 0; i < userIds.length; i++) {
				// Create dummy user
				user = new User(Long.parseLong(StringUtils.stripCommas(userIds[i].trim()).trim()), "", "", -1L);
				try {
					user = ((UserDaoImp) DBManager.getInstance().getDao(DAOEnum.USER_DAO)).delete(user);
					if (user == null) {
						opSuccess = false;
					} else {
						// Update cache on successful DB update
						LOG.debug("Updating cache on successful DB update");
						LoadTripDetails.getInstance().refresh();
						LoadAclVehicleDetails.getInstance().refresh();
						LoadAclDriverDetails.getInstance().refresh();
						LoadUserDetails.getInstance().cacheUserDetails.remove(user.getId());
					}
				} catch (OperationNotSupportedException e) {
					 LOG.error(e);
				} catch (Exception e){
					LOG.error("Handling general exception while deleting user details",e);
				}
			}
		}
		return opSuccess;
	}
}
