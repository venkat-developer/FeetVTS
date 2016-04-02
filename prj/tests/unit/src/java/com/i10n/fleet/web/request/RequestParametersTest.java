package com.i10n.fleet.web.request;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.i10n.fleet.test.AbstractFleetTestCase;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * Test Cases for testing {@link RequestParameters}
 * 
 * @author sabarish
 * 
 */
public class RequestParametersTest extends AbstractFleetTestCase {

    /**
     * Tests {@link RequestParameters} for Debug mode
     */
    @Test
    public void testParametersLoadingInDebugMode() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(RequestParams.debug.toString(), "true");
        request.addParameter(RequestParams.module.toString(), "/debug/debug");
        Map<RequestParams, String> params = new FleetParameterProcessor()
                .getParameters(request);
        params.put(RequestParams.view, "testDebugView");
        RequestParameters requestParams = new RequestParameters(request, params);
        assertEquals(requestParams.getParameter(RequestParams.valueOf("debug")), "true");
        assertEquals(requestParams.getParameter(RequestParams.module), "/debug/debug");
        assertTrue(requestParams.isDebugMode());
        assertFalse(requestParams.isVerboseMode());
        assertEquals(requestParams.getParameter(RequestParams.view), "testDebugView");
    }

    /**
     * Tests {@link RequestParameters} for Test mode
     */
    @Test
    public void testParametersLoadingInTestMode() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(RequestParams.test.toString(), "true");
        request.addParameter(RequestParams.module.toString(), "/test/test");
        Map<RequestParams, String> params = new FleetParameterProcessor()
                .getParameters(request);
        params.put(RequestParams.view, "testTestView");
        RequestParameters requestParams = new RequestParameters(request, params);
        assertEquals(requestParams.getParameter(RequestParams.valueOf("test")), "true");
        assertEquals(requestParams.getParameter(RequestParams.module), "/test/test");
        assertTrue(requestParams.isTestMode());
        assertFalse(requestParams.isDebugMode());
        assertFalse(requestParams.isVerboseMode());
        assertEquals(requestParams.getParameter(RequestParams.view), "testTestView");
    }

    /**
     * Tests {@link RequestParameters} for Verbose mode
     */
    @Test
    public void testParametersLoadingInVerboseMode() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(RequestParams.verbose.toString(), "true");
        request.addParameter("istest", "isTest");
        Map<RequestParams, String> params = new FleetParameterProcessor()
                .getParameters(request);
        params.put(RequestParams.view, "testVerboseView");
        RequestParameters requestParams = new RequestParameters(request, params);
        assertEquals(requestParams.getRequestParameter("istest"), "isTest");
        assertEquals(requestParams.getParameter(RequestParams.verbose), "true");
        assertFalse(requestParams.isDebugMode());
        assertTrue(requestParams.isVerboseMode());
        assertEquals(requestParams.getParameter(RequestParams.view), "testVerboseView");
    }

    /**
     * Tests for Parameters Parsing in {@link RequestParameters}
     */
    @Test
    public void testAllParams() {
        Map<RequestParams, String> params = new HashMap<RequestParams, String>();
        for (RequestParams param : RequestParams.values()) {
            params.put(param, param.toString());
        }
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestParameters requestParams = new RequestParameters(request, params);
        for (RequestParams param : RequestParams.values()) {
            assertEquals(requestParams.getParameter(param), param.toString());
        }
    }
}
