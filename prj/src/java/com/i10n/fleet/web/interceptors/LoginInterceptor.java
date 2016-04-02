package com.i10n.fleet.web.interceptors;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

/**
 * A {@link HandlerInterceptorAdapter} that intercepts all the requests and
 * checks if a user is logged in . if not will redirect to login page
 * 
 * @author sabarish
 * 
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    public static final String MODE_ALLOW_ALL = "allowall";
    public static final String MODE_DEFAULT = "default";

    /**
     * View that is to be redirected when no user is logged int
     */
    private String m_failureView;
    /**
     * List of URL Patterns that are to be escaped handling
     */
    private List<String> m_escapeViews;

    /**
     * Mode of the interceptor
     */
    private String m_mode = MODE_DEFAULT;

    /**
     * Intercepts the request and checks is a session attribute with name
     * currentuser is set or not. it escapes the handling of all the urls
     * patterns given in {@link #m_escapeViews}
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        boolean result = true;
        if (!MODE_ALLOW_ALL.equals(getMode())) {
            if (!isEscapeURL(request.getRequestURI())) {
                String user = (String) WebUtils.getSessionAttribute(request,
                        "currentuser");
                if (null == user) {
                    throw new ModelAndViewDefiningException(new ModelAndView(
                            new RedirectView(getFailureView())));
                }
            }
        }
        return result;
    }

    /**
     * Checks if the specified url matches one of the escape patterns
     * 
     * @param url
     * @return
     */
    private boolean isEscapeURL(String url) {
        boolean result = false;
        List<String> views = getEscapeViews();
        for (String view : views) {
            Pattern pattern = Pattern.compile(view.trim());
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Returns the view that is to be redirected when a user is not logged in
     * 
     * @return
     */
    public String getFailureView() {
        return m_failureView;
    }

    /**
     * Sets the failure view - {@link #getFailureView()}
     * 
     * @param view
     */
    public void setFailureView(String view) {
        m_failureView = view;
    }

    /**
     * Returns the list of views that the interceptor should not process
     * 
     * @return
     */
    public List<String> getEscapeViews() {
        return m_escapeViews;
    }

    /**
     * Sets the escapeViews - {@link #getEscapeViews()}
     * 
     * @param views
     */
    public void setEscapeViews(List<String> views) {
        m_escapeViews = views;
    }

    /**
     * Sets the mode of the interceptor based on the value interceptor behaves
     * differently <li>allowall --> Passes all the requests by default, without
     * any checks</li> <li>default --> Default behaviour , will check if a user
     * is logged in before passing</li>
     * 
     * @param mode
     */
    public void setMode(String mode) {
        m_mode = mode;
    }

    /**
     * returns the mode of the interceptor
     * 
     * @return
     */
    public String getMode() {
        return m_mode;
    }
}
