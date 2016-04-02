package com.i10n.fleet.web.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * An implementation of {@link IParameterProcessor}
 * 
 * @author sabarish
 * 
 */
public class FleetParameterProcessor implements IParameterProcessor {

    private List<IParameterProcessor> m_parameterProcessors;

    /**
     * Parses Request and Environment parameters to generate the Parameter map
     * 
     * @see IParameterProcessor#getParameters(HttpServletRequest)
     */
    public Map<RequestParams, String> getParameters(HttpServletRequest request) {
        Map<RequestParams, String> result = new HashMap<RequestParams, String>();
        addRequestParams(request, result);
        addChildParams(request, result);
        return result;

    }

    private void addRequestParams(HttpServletRequest request,
            Map<RequestParams, String> parameters) {
        for (RequestParams param : RequestParams.values()) {
            String value = request.getParameter(param.toString());
            if (null != value) {
                parameters.put(param, value);
            }
        }
    }

    private void addChildParams(HttpServletRequest request,
            Map<RequestParams, String> parameters) {
        List<IParameterProcessor> processors = getParameterProcessors();
        if (null != processors) {
            for (IParameterProcessor processor : processors) {
                parameters.putAll(processor.getParameters(request));
            }
        }
    }

    /**
     * Returns The list of child parameter processors.
     * 
     * @return
     */
    public List<IParameterProcessor> getParameterProcessors() {
        return m_parameterProcessors;
    }

    /**
     * Sets the list of child parameter processors.
     * 
     * @param processors
     */
    public void setParameterProcessors(List<IParameterProcessor> processors) {
        m_parameterProcessors = processors;
    }
}
