package com.i10n.fleet.web.controllers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.i10n.db.entity.User;
import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.web.request.IParameterProcessor;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;
import com.i10n.fleet.web.utils.SessionUtils;

/**
 * failure controller for handling failure views.
 * 
 * @author hemanth
 * 
 */
public class FailureControllerh implements Controller {
	private static final Logger LOG = Logger.getLogger(FailureControllerh.class);
	/**
     * default constructor
     */
    public FailureControllerh() {
        super();
    }

    /**
     * Non-default constructor to be used when instantiating prototype
     * controllers
     * 
     * @param viewName
     */
    public FailureControllerh(String viewName) {
        m_viewName = viewName;
    }

    private String m_viewName;
    private IParameterProcessor m_parameterProcessor = null;
    private List<? extends IDataProvider> m_dataProviders = null;

    /**
     * See
     * {@link Controller#handleRequest(HttpServletRequest, HttpServletResponse)}
     */
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView result = null;
        RequestParameters params = getRequestParams(request);
        IDataset dataset = new Dataset();
        if (null != m_dataProviders) {
            for (IDataProvider provider : m_dataProviders) {
                IDataset data = provider.getDataset(params);
                if (null != data) {
                    dataset.put(provider.getName(), data);
                }
            }
        }
        result = resolveView(params, dataset);
        return result;
    }

    /**
     * Uses {@link IParameterProcessor} injected to parse the
     * Request/Environment parameters along with it's own params to create a new
     * {@link RequestParameters} object
     * 
     * @param request
     * @return
     */
    private RequestParameters getRequestParams(HttpServletRequest request) {
    	Map<RequestParams, String> parameters = getParameterProcessor().getParameters(
                request);
        if (null == parameters.get(RequestParams.view)) {
            parameters.put(RequestParams.view, m_viewName);
            
        }
        RequestParameters requestParam = new RequestParameters(request, parameters);
        return requestParam;
    }

    /**
     * Resolves based on the {@link RequestParameters} : params , whether the
     * currenct view is basic view or a failure view.
     * 
     * @param params
     * @param dataset
     * @return
     */
    private ModelAndView resolveView(RequestParameters params, IDataset dataset) {
    	ModelAndView result = null;
        if (null != params.getParameter(RequestParams.module)) {
        	result = new ModelAndView(params.getParameter(RequestParams.module), dataset);
            }
        else if (null != params.getParameter(RequestParams.markup)) {
        	result = new ModelAndView("/blocks/markup", dataset);
        }
        else {  
        	User user = SessionUtils.getCurrentlyLoggedInUser();
        	if(user!=null){
        		LOG.fatal("Redirecting to Failureh since user is Not null");
        	result = new ModelAndView("/failureh", dataset);
        	}else{
        		LOG.fatal("Redirecting to loginh since user is  Null");
            	result = new ModelAndView("/loginh", dataset);
           }
        	   
           }
        return result;
    }

    /**
     * Sets the Data providers for the controller. Should be used only while
     * dependency injection
     * 
     * @param providers
     */
    public void setDataProviders(List<? extends IDataProvider> providers) {
        m_dataProviders = providers;
    }

    /**
     * Returns the name of the view.
     * 
     * @return
     */
    public String getViewName() {
        return m_viewName;
    }

    /**
     * Sets the name of the view.
     * 
     * @param name
     */
    public void setViewName(String name) {
        m_viewName = name;
    }

    /**
     * Returns the injected {@link IParameterProcessor}
     * 
     * @return
     */
    public IParameterProcessor getParameterProcessor() {
        return m_parameterProcessor;
    }

    /**
     * Injects the {@link IParameterProcessor}
     * 
     * @param parameterProcessor
     */
    public void setParameterProcessor(IParameterProcessor parameterProcessor) {
        m_parameterProcessor = parameterProcessor;
    }

}