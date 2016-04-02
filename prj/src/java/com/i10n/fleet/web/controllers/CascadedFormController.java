package com.i10n.fleet.web.controllers;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * A single Controller that allow cascading of form controllers based on url the
 * form submits.
 * 
 * @author Sabarish
 */
public class CascadedFormController implements Controller {

    private Map<String, Controller> m_handlers;

    /**
     * Returns the handlers of the cascade
     */
    public Map<String, Controller> getHandlers() {
        return m_handlers;
    }

    /**
     * Sets the handlers to cascade
     */
    public void setHandlers(Map<String, Controller> handlers) {
        m_handlers = handlers;
    }

    /**
     * Handles the request and resolves which {@link Controller} should get the
     * control
     */
    @Override
    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ModelAndView result = null;
        Controller handler = resolveForm(request.getRequestURI());
        if (null != handler) {
            result = handler.handleRequest(request, response);
          
        }
        return result;
    }

    /**
     * Resolves the url to return the {@link Controller} which should get the
     * control
     */
    private Controller resolveForm(String uri) {
      
        Controller result = null;
        Pattern pattern = Pattern.compile(".*/form/([^/]*)(/.*)?$");
        Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            String formName = matcher.group(1);
            
            result = getHandlers().get(formName);
         }
        return result;
    }

}
