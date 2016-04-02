package com.i10n.fleet.web.controllers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.validation.BindException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import com.i10n.db.dao.DAOEnum;
import com.i10n.db.dao.UserDaoImp;
import com.i10n.db.entity.User;
import com.i10n.db.tools.DBManager;
import com.i10n.fleet.providers.mock.LogsDataProvider;
import com.i10n.fleet.util.Constants;
import com.i10n.fleet.util.StringUtils;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * A {@link org.springframework.web.servlet.mvc.Controller} for handling login
 * and logout requests
 * 
 * @author sabarish
 * 
 */
public class LoginController extends SimpleFormController {
	
	private static Logger LOG = Logger.getLogger(LoginController.class);
	
	private String m_logoutView;
	private static final String LOGOUT_PARAM = "logout";
	private String m_logfailView;

	/**
	 * Handles login/logout requests and checks redirects to appproaprate view.
	 * If its a logout request, it removes the session attribute and redirects
	 * to login page. If a login request handling is passed to
	 * {@link #onSubmit(Object)} which on success returns a {@link RedirectView}
	 * to {@link #getSuccessView()} page and then finally adds the currents user
	 * as a session attribute. This will be reimplemented once the user database
	 * is available.
	 * 
	 * @see SimpleFormController#handleRequest(HttpServletRequest,
	 *      HttpServletResponse)
	 */
	@SuppressWarnings("deprecation")
	@Override
	public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView result = null;
		HttpServletRequest req=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String time = req.getParameter("localTime");
		String localTimeZone = req.getParameter("localTimeZone");
		if (isLogout(request)) {
			doLogout(request);
			result = new ModelAndView(new RedirectView(getLogoutView()));
		}
		else {
			Date date = new Date(time);            
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy  HH:mm:ss", Locale.US);
			String localTime = df.format(date);                  
			result = super.handleRequestInternal(request, response);
			View view = result.getView();
			if (view instanceof RedirectView
					&& ((RedirectView) view).getUrl().equals(getSuccessView())) {
				String user = request.getParameter("user");
				String group = "default";
				User currentUserObject = ((UserDaoImp)(DBManager.getInstance().getDao(DAOEnum.USER_DAO))).getUser(user);

				if (currentUserObject.getRole() == User.UserRole.ADMIN_USER) {
					group = "admin";
				}
				WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_USER, user);
				WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_GROUP, group);
				WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_USER_OBJECT, currentUserObject);
				WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_TIME, localTime);
				WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_TIMEZone, localTimeZone);
				String type="Logged in";
				this.Loginlog(request,type);
			}
		}
		return result;
	}

	/**
	 * Handles the login submit requests. It will validate if the user/password
	 * are matching and on success will return a {@link RedirectView} to
	 * {@link #getSuccessView()} page.
	 * 
	 */
	@Override
	protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors)
	throws Exception {
		String username = request.getParameter("user");
		String password = request.getParameter("password");
		ModelAndView modelAndView = null;
		boolean userAuthenticated = ((UserDaoImp)(DBManager.getInstance().getDao(DAOEnum.USER_DAO))).authenticateUser(username, password);
		if(userAuthenticated){
			modelAndView = new ModelAndView(new RedirectView(getSuccessView()));
			}else{
			modelAndView = new ModelAndView(new RedirectView(getLogfailView()));
			}
		return modelAndView;
	}

	private void doLogout(HttpServletRequest request) {
		WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_USER, null);
		WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_GROUP, null);
		}

	private boolean isLogout(HttpServletRequest request) {
		boolean result = false;
		String type="logged out";
		String logoutParam = request.getParameter(LOGOUT_PARAM);
		if (null != logoutParam && "true".equals(logoutParam)) {
			result = true;
			this.Loginlog(request, type);
		}
		return result;
	}
	
	public void Loginlog(HttpServletRequest request,String type ){
		try{
			Long uid = null;
			String action = null;
			String username = request.getParameter("user");
			User currentUser = null;
			if(SessionUtils.getCurrentlyLoggedInUser() != null){
				currentUser = SessionUtils.getCurrentlyLoggedInUser();
				if(username == null){
					username = currentUser.getLogin();
				}
				String remoteip=request.getRemoteAddr();
				LogsDataProvider log=new LogsDataProvider();
				// Insertion of user login information to the logs table
				Calendar cal = Calendar.getInstance();
				Date currentdate = cal.getTime();
				uid = currentUser.getId();
				Long userid = Long.parseLong(StringUtils.stripCommas(uid + ""));
				action = username.toUpperCase() +""+ " HAS "+type.toUpperCase();
				log.getupDatedlogs(userid, currentdate, remoteip, username, action);
				LOG.debug("Successfully logging the LOGIN relateds with message : "+action);
			} else {
				LOG.error("Session is NULL");
			}
		} catch (Exception e){
			LOG.error(e);
		}
	}

	public String getLogfailView(){
		return m_logfailView;
	}

	public void setLogfailView(String view){
		m_logfailView=view;
	}

	/**
	 * Returns the View to be used after logout
	 * 
	 * @return
	 */
	public String getLogoutView() {
		return m_logoutView;
	}

	/**
	 * Sets the View to be used after logout
	 * 
	 * @param view
	 */
	public void setLogoutView(String view) {
		m_logoutView = view;
	}

}