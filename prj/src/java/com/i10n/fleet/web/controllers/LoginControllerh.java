package com.i10n.fleet.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.i10n.fleet.util.Constants;

/**
 * A {@link org.springframework.web.servlet.mvc.Controller} for handling login
 * and logout requests
 * 
 * @author sabarish
 * 
 */
public class LoginControllerh extends SimpleFormController {
	private static final String LOGOUT_PARAM = "logout";
	private String m_logfailView;
	private String m_logoutView;
	
	/**
	 * Handles the login submit requests. It will validate if the user/password
	 * are matching and on success will return a {@link RedirectView} to
	 * {@link #getSuccessView()} page.
	 * 
	 */
	public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView result = null;
		@SuppressWarnings("unused")
		HttpServletRequest req=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		result = super.handleRequestInternal(request, response);
		View view = result.getView();
		if (isLogout(request)) {
			doLogout(request);
			result = new ModelAndView(new RedirectView(getLogoutView()));
			}
		else{
		if (view instanceof RedirectView&& ((RedirectView) view).getUrl().equals(getSuccessView())) {
				String user = request.getParameter("user");
				String group = "default";
				User currentUserObject = ((UserDaoImp)(DBManager.getInstance().getDao(DAOEnum.USER_DAO))).getUser(user);
				if (currentUserObject.getRole() == User.UserRole.ADMIN_USER) {
					group = "admin";
				}
				WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_USER, user);
				WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_GROUP, group);
				WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_USER_OBJECT, currentUserObject);
		}
		}
		return result;
	}
	private boolean isLogout(HttpServletRequest request) {
		boolean result = false;
		String logoutParam = request.getParameter(LOGOUT_PARAM);
		if (null != logoutParam && "true".equals(logoutParam)) {
			result = true;
			
		}
		return result;
	}
	private void doLogout(HttpServletRequest request) {
		WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_USER, null);
		WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_GROUP, null);
		WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_USER_OBJECT, null);
			}

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
			WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_USER, username);
			WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_GROUP,password);
			modelAndView = new ModelAndView(new RedirectView(getLogfailView()));
		}
		return modelAndView;
	}
	public String getLogfailView(){
		return m_logfailView;
	}
	public void setLogfailView(String view){
		m_logfailView=view;
	}
	public String getLogoutView() {
		return m_logoutView;
	}
	public void setLogoutView(String view) {
		m_logoutView = view;
	}
	
}