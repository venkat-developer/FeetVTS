
package com.i10n.fleet.web.utils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import com.i10n.db.entity.User;
import com.i10n.fleet.util.Constants;

public class SessionUtils {

	public static User getCurrentlyLoggedInUser() {
		User loggedinUser = null;
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		loggedinUser = (User)WebUtils.getSessionAttribute(request, Constants.SESSION.ATTR_USER_OBJECT);		
		return loggedinUser;
	}
	public static HttpSession GetCurrentSession(){
		HttpSession session;
		HttpServletRequest req=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		session=req.getSession();
		return session;
	}
	public static String getRemoteip(){
		HttpServletRequest req=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		String remoteIp=req.getRemoteAddr();
		return remoteIp;
	}
}

