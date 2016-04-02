package com.i10n.fleet.web.request;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Stores the parameters required from the HTTPRequest
 * 
 * @author sabarish
 * 
 */
public class RequestParameters {

    /**
     * Known Parameters that may be available from the request
     * 
     * @author sabarish
     * 
     */
    public enum RequestParams {
        /**
         * A Paremeter to enable debug mode
         */
        debug,
        /**
         * Parameter to enable test mode
         */
        test,
        /**
         * module to pick on debug mode
         */
        module,
        /**
         * verbose mode to put on log4js
         */
        verbose,
        /**
         * data to be viewed in debug mode
         */
        data,
        /**
         * Rwidget to be tested
         */
        widget,
        /**
         * current view
         */
        view,
        /**
         * skin
         */
        skin,
        /**
         * markup of a widget
         */
        markup,
        /**
         * Return only the widget body?
         */
        body,
        /**
         * static assets to be merged
         */
        merge,
        /**
         * Subnav page to show
         */
        subnav,
        /**
         * A Sub Page inside a Sub Nav
         */
        subpage,
        /**
         * User Name
         */
        username,
        /**
         * User Group.
         */
        usergroup,
        /**
         * vehicle id of the request
         */
        vehicleID,
        /**
         * reportID
         */
        report,
        /**
         * start date of report
         */
        startdate,
        /**
         * end date of report
         */
        enddate,
        /**
         * trip id of trip settings
         */
        tripID,
        /**
         * driver id for report.
         */
        driverID,
        /**
         * The name of a stand-alone unit to test
         */
        unitName,
        /**
         * Action required
         */
        action,
        /**
         * IP Address Param
         */
        ipaddr,
        /**
         * user id for control panel manage
         */
        userID,
        /**
         * Specifies how the client would like to look at a data
         * 
         * Eg :- Vehicle data may be viewed as a list of all vehicles or list of
         * assignedVehicles
         */

        /**
         * Hardware id for editing hardware
         */

        hardwareID,

        dataView,
        
        log, 
        ip,
        localTime,
        groupID,
        reportuserid,
        mobileuserid,
        alertuserid,
        
        /**
         * Route id for editing route
         */
        routeID,
        /**
         * stop id for control panel manage
         */
        
        stopID,
        /**
         * routeschedule id for control panel manage
         */
        ID,
        routescheduleID,

    }

    private Map<RequestParams, String> m_parameters = new HashMap<RequestParams, String>();

    private HttpServletRequest m_request = null;

    public RequestParameters(HttpServletRequest request,
            Map<RequestParams, String> parameters) {
        m_request = request;
        m_parameters.putAll(parameters);
    }

    /**
     * Returns the value of a request parameter.
     * 
     * @param key
     * @return
     */
    public String getRequestParameter(String key) {
        return m_request.getParameter(key);
    }

    /**
     * Returns the value of valid parameter along with the request parameters.
     * Ex : <li>Current View : {@link RequestParams#view}</li>
     * 
     * @param key
     * @return
     */
    public String getParameter(RequestParams key) {
        return m_parameters.get(key);
    }

    /**
     * Returns the value of valid parameter along with the request parameters.
     * Ex : <li>Current View : {@link RequestParams#view}</li>
     * 
     * @param key
     * @return
     */
    public HttpSession getSession() {
        return m_request.getSession(false);
    }
    /**
     * Checks whether debug mode is enabled
     * 
     * @return
     */
    public boolean isDebugMode() {
        boolean result = false;
        if ("true".equals(getParameter(RequestParams.debug))) {
            result = true;
        }
        return result;
    }

    /**
     * Checks whether test mode is enabled
     * 
     * @return
     */
    public boolean isTestMode() {
        boolean result = false;
        if ("true".equals(getParameter(RequestParams.test))) {
            result = true;
        }
        return result;
    }

    /**
     * Checks whether verbose mode is enabled
     * 
     * @return
     */
    public boolean isVerboseMode() {
        boolean result = false;
        if ("true".equals(getParameter(RequestParams.verbose))) {
            result = true;
        }
        return result;
    }

}
