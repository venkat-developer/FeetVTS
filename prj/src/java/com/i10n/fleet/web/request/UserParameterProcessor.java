package com.i10n.fleet.web.request;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.WebUtils;

import com.i10n.fleet.util.Constants;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * Implementation of {@link IParameterProcessor} and a child processor of
 * {@link FleetParameterProcessor}. Supplies a map of parameters related to the
 * user logged in.
 * 
 * @author sabarish
 * 
 */
public class UserParameterProcessor implements IParameterProcessor {

    /**
     * @see IParameterProcessor#getParameters(HttpServletRequest)
     */
    public Map<RequestParams, String> getParameters(HttpServletRequest request) {
        Map<RequestParams, String> result = new HashMap<RequestParams, String>();
        String usergroup = (String) WebUtils.getSessionAttribute(request,
                Constants.SESSION.ATTR_GROUP);
        String username = (String) WebUtils.getSessionAttribute(request,
                Constants.SESSION.ATTR_USER);
        result.put(RequestParams.username, username);
        result.put(RequestParams.usergroup, usergroup);
        return result;
    }

}
