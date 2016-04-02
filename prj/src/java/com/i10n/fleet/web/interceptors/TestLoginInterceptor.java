package com.i10n.fleet.web.interceptors;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.util.WebUtils;

/**
 * The Login Interceptor to be used for tests
 * 
 * Extends the behavior of LoginInterceptor behaving in "allow all" mode
 * 
 * @author intern
 * 
 */
public class TestLoginInterceptor extends LoginInterceptor {

    private Map<String, Map<String, String>> m_userAttributes;

    /**
     * Intercepts the request and checks is a session attribute with name
     * currentuser is set or not. If not set, sets it to a default value to
     * simulate allow-all behavior
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        String testUser = request.getParameter("testUser");
        if (testUser == null || testUser.isEmpty()
                || testUser.equalsIgnoreCase("undefined")) {
            testUser = "default";
        }
        Map<String, String> userAttrs = m_userAttributes.get(testUser);
        String user = userAttrs.get("username");
        String group = userAttrs.get("usergroup");
        WebUtils.setSessionAttribute(request, "currentuser", user);
        WebUtils.setSessionAttribute(request, "currentgroup", group);
        return true;
    }

    /**
     * Sets the attributes for each user created. The users and their attributes
     * are injected
     * 
     * @param mUserAttributes
     */
    public void setUserAttributes(Map<String, Map<String, String>> mUserAttributes) {
        m_userAttributes = mUserAttributes;
    }

    /**
     * Gets the user attributes that were injected
     * 
     * @return
     */

    public Map<String, Map<String, String>> getUserAttributes() {
        return m_userAttributes;
    }
}
