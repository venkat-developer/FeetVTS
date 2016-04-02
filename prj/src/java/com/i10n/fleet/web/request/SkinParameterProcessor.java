package com.i10n.fleet.web.request;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.util.WebUtils;

import com.i10n.fleet.util.Constants;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.util.EnvironmentInfo.EnvironmentParams;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * Implementation of {@link IParameterProcessor} and child processor of
 * {@link FleetParameterProcessor}. Supplies a map of parameters related to
 * skin. Checks the default skin for the current user. In Future this will be
 * changed to use the Data Layer to get the user/skin relationships
 * 
 * @author sabarish
 * 
 */
public class SkinParameterProcessor implements IParameterProcessor {

    /**
     * @see IParameterProcessor#getParameters(HttpServletRequest)
     */
    public Map<RequestParams, String> getParameters(HttpServletRequest request) {
        Map<RequestParams, String> result = new HashMap<RequestParams, String>();
        String currentSkin = EnvironmentInfo.getProperty(EnvironmentParams.DEFAULT_SKIN);
        if (request.getParameter("skin") != null) {
            currentSkin = request.getParameter("skin");
            /**
             * This is just a temporary measure to view any skin. will be
             * removed in future.
             */
        }
        else {

            String group = (String) WebUtils.getSessionAttribute(request,
                    Constants.SESSION.ATTR_GROUP);
            if ("admin".equals(group)) {
                currentSkin = "admin";
            }
        }
        result.put(RequestParams.skin, currentSkin);
        return result;
    }

}
