package com.i10n.fleet.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.IDataProvider;
import com.i10n.fleet.test.AbstractFleetTestCase;
import com.i10n.fleet.web.controllers.ViewController;
import com.i10n.fleet.web.request.FleetParameterProcessor;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * Test Cases for testing {@link ViewController}
 * 
 * @author sabarish
 * 
 */
public class ViewControllerTest extends AbstractFleetTestCase {
    /**
     * Tests the dashboard controller. This is a sample test case will change
     * with further implementation of dashboard
     */
    @Test
    public void testDebug() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET",
                "/view/dashboard/");
        MockHttpServletResponse response = new MockHttpServletResponse();
        doTestController(request, response, "dashboard", "/base");
    }

    /**
     * Tests the {@link ViewController} for debug mode
     */
    @Test
    public void testDashBoardDebug() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET",
                "/view/dashboard/");
        request.setParameter(RequestParams.debug.toString(), "true");
        request.setParameter(RequestParams.module.toString(), "/debug/debug");
        MockHttpServletResponse response = new MockHttpServletResponse();
        doTestController(request, response, "dashboard", "/debug/debug");
    }

    /**
     * Tests the {@link ViewController} for debug mode
     */
    @Test
    public void testDashBoardTest() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET",
                "/view/dashboard/");
        request.setParameter(RequestParams.test.toString(), "true");
        request.setParameter(RequestParams.module.toString(), "/test/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        doTestController(request, response, "dashboard", "/test/test");
    }

    /**
     * Tests the {@link ViewController} for debug mode
     */
    @Test
    public void testDashBoardMarkup() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET",
                "/view/dashboard/");
        request.setParameter(RequestParams.markup.toString(), "Header");
        MockHttpServletResponse response = new MockHttpServletResponse();
        doTestController(request, response, "dashboard", "/blocks/markup");
    }

    /**
     * Tests whether the {@link ViewController} loads properly from the
     * {@link List} of {@link IDataProvider} supplied
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testDataProviderLoading() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET",
                "/view/dashboard/");
        ViewController controller = new ViewController();
        controller.setParameterProcessor(new FleetParameterProcessor());
        List<IDataProvider> providers = new ArrayList<IDataProvider>();
        IDataProvider dataProvider = new IDataProvider() {

            public IDataset getDataset(RequestParameters params) {
                IDataset dataset = new Dataset();
                dataset.put("testParam", "testValue");
                return dataset;
            }

            public String getName() {
                return "test";
            }

        };

        providers.add(dataProvider);
        controller.setDataProviders(providers);
        MockHttpServletResponse response = new MockHttpServletResponse();
        ModelAndView view;
        try {
            view = controller.handleRequest(request, response);
            Map<Object, Object> map = (Map<Object, Object>) view.getModel().get("test");
            assertNotNull(map);
            assertEquals(map.get("testParam"), "testValue");
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("Caught exception while handling request /view/dashboard/ : \n"
                    + e.getMessage());
        }

    }

    /**
     * Used for test cases to test Controllers for the scenarios
     * 
     * @param request
     * @param response
     * @param viewName
     * @param module
     */
    public void doTestController(MockHttpServletRequest request,
            MockHttpServletResponse response, String viewName, String module) {

        ViewController controller = new ViewController(viewName);
        controller.setParameterProcessor(new FleetParameterProcessor());
        try {
            ModelAndView view = controller.handleRequest(request, response);
            assertEquals(view.getViewName(), module);
            assertEquals(controller.getViewName(), viewName);
        }
        catch (Exception e) {
            fail("Caught exception while handling request /view/dashboard/ : \n"
                    + e.getMessage());
        }
    }
}
