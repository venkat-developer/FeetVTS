package com.i10n.fleet.web.request;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.util.WebUtils;

import com.i10n.fleet.test.AbstractFleetTestCase;
import com.i10n.fleet.util.Constants;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.web.request.RequestParameters.RequestParams;

/**
 * Tests {@link SkinParameterProcessor}
 * 
 * @author sabarish
 * 
 */
public class SkinParameterProcessorTest extends AbstractFleetTestCase {

    /**
     * @see Loads Environment befores tests.
     */
    @Before
    public void setUp() {
        EnvironmentInfo.load(this);
    }

    /**
     * Tests {@link SkinParameterProcessor} for admin user requests
     */
    @Test
    public void testAdminSkin() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_USER, "testadmin");
        WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_GROUP, "admin");
        SkinParameterProcessor processor = new SkinParameterProcessor();
        Map<RequestParams, String> parameters = processor.getParameters(request);
        assertEquals("admin", parameters.get(RequestParams.skin));
    }

    /**
     * Tests {@link SkinParameterProcessor} for default user requests
     */
    @Test
    public void testDefaultBehavior() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_USER, "testuser");
        WebUtils.setSessionAttribute(request, Constants.SESSION.ATTR_GROUP, "default");
        SkinParameterProcessor processor = new SkinParameterProcessor();
        Map<RequestParams, String> parameters = processor.getParameters(request);
        assertEquals("default", parameters.get(RequestParams.skin));
    }

    /**
     * Clears environment before every test
     */
    @After
    public void tearDown() {
        EnvironmentInfo.clear();
    }
}
