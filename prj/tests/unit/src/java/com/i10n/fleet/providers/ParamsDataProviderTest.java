package com.i10n.fleet.providers;

import java.util.Map;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.i10n.fleet.datasets.IDataset;
import com.i10n.fleet.providers.impl.ParamsDataProvider;
import com.i10n.fleet.test.AbstractFleetTestCase;
import com.i10n.fleet.web.request.FleetParameterProcessor;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * Test Cases for testing {@link ParamsDataProvider}
 * 
 * @author sabarish
 * 
 */
public class ParamsDataProviderTest extends AbstractFleetTestCase {

    /**
     * Tests Data providability of {@link ParamsDataProviderTest}
     */
    @Test
    public void testDataProvider() {
        ParamsDataProvider provider = new ParamsDataProvider();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(RequestParams.debug.toString(), "true");
        request.addParameter(RequestParams.module.toString(), "/debug/debug");
        Map<RequestParams, String> params = new FleetParameterProcessor()
                .getParameters(request);
        params.put(RequestParams.view, "testDebugView");
        RequestParameters requestParams = new RequestParameters(request, params);
        IDataset dataset = provider.getDataset(requestParams);
        assertEquals("parameters", provider.getName());
        assertEquals(dataset.get("debug"), "true");
        assertEquals(dataset.get("module"), "/debug/debug");
        assertEquals(dataset.get("view"), "testDebugView");
    }
}
