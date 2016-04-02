package com.i10n.fleet.providers;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.i10n.fleet.datasets.impl.Dataset;
import com.i10n.fleet.providers.impl.SkinDataProvider;
import com.i10n.fleet.providers.managers.impl.SkinManager;
import com.i10n.fleet.test.AbstractFleetTestCase;
import com.i10n.fleet.web.request.RequestParameters;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * Test Cases for testing {@link SkinDataProvider}
 * 
 * @author sabarish
 * 
 */
public class SkinDataProviderTest extends AbstractFleetTestCase {

    /**
     * Tests Data Providability of {@link SkinDataProvider}
     */
    @Test
    public void testDataProvider() {
        SkinDataProvider provider = new SkinDataProvider();
        SkinManager manager = new SkinManager();
        provider.setSkinManager(manager);
        assertEquals(manager, provider.getSkinManager());
        assertEquals("skins", provider.getName());
        Map<RequestParams, String> params = new HashMap<RequestParams, String>();
        Dataset dataset = (Dataset) provider.getDataset(new RequestParameters(
                new MockHttpServletRequest(), params));
        dataset.containsKey("default");
    }

}
