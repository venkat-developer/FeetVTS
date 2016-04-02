package com.i10n.fleet.web.request;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * An interface for ParameterProcessing necessary for
 * {@link com.i10n.fleet.web.controllers.ViewController} to add parameters which
 * are specific to a request and needed by the
 * {@link com.i10n.fleet.providers.impl.IDataProvider}
 * 
 * @author sabarish
 * 
 */
public interface IParameterProcessor {
    /**
     * Returns the {@link Map} of {@link RequestParams} mapped to Parameter
     * Values after parsing necessary parameters
     * 
     * @param request
     * @return
     */
    Map<RequestParams, String> getParameters(HttpServletRequest request);
}
