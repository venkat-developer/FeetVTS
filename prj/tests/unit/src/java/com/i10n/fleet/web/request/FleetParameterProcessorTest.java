package com.i10n.fleet.web.request;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.i10n.fleet.test.AbstractFleetWebSpringTests;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * Tests {@link FleetParameterProcessor}
 * 
 * @author sabarish
 * 
 */
public class FleetParameterProcessorTest extends AbstractFleetWebSpringTests {

    /**
     * Tests whether Request Parameters are parsed from
     * {@link HttpServletRequest} passed
     */
    @Test
    public void testParameterParsing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(RequestParams.debug.toString(), "true");
        request.addParameter(RequestParams.module.toString(), "/debug/debug");
        FleetParameterProcessor processor = (FleetParameterProcessor) getApplicationContext()
                .getBean("parameterProcessor");
        Map<RequestParams, String> params = processor.getParameters(request);
        assertEquals(params.get(RequestParams.debug), "true");
        assertEquals(params.get(RequestParams.module), "/debug/debug");
    }

    /**
     * Tests whether the {@link FleetParameterProcessor} loads parameters from
     * the Environment
     */
    @Test
    public void testEnvironmentParameterParsing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(RequestParams.debug.toString(), "true");
        FleetParameterProcessor processor = (FleetParameterProcessor) getApplicationContext()
                .getBean("parameterProcessor");
        Map<RequestParams, String> params = processor.getParameters(request);
        assertEquals(params.get(RequestParams.skin), "default");
        assertEquals(params.get(RequestParams.debug), "true");
    }

}
