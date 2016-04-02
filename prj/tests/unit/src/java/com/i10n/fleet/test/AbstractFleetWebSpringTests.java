package com.i10n.fleet.test;

import javax.servlet.ServletContext;

import org.junit.After;
import org.junit.Before;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.AbstractSpringContextTests;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.i10n.fleet.test.util.PropertyLoader;
import com.i10n.fleet.util.EnvironmentInfo;
import com.i10n.fleet.web.context.FleetXmlWebApplicationContext;

/**
 * Abstract class for all the tests that need {@link XmlWebApplicationContext}
 * loaded from context xml's
 * 
 * @see AbstractSpringContextTests
 * @author sabarish
 * 
 */
public abstract class AbstractFleetWebSpringTests extends AbstractSpringContextTests {

    /**
     * These files will require change when extra database operations will be
     * added.This will then use mock datasource instead of actual datasource
     */
    private static final String[] DEFAULT_CONFIG_FILES = new String[] {
            "classpath:/WEB-INF/applicationContext.xml",
            "classpath:/WEB-INF/fleetcheck-servlet.xml" };
    private XmlWebApplicationContext m_webCtx = null;

    private MockServletContext m_servletContext = new MockServletContext("");

    /**
     * @throws java.lang.Exception
     */
    @Before
    protected void setUp() throws Exception {
        PropertyLoader.load();
        EnvironmentInfo.load(this);
        if (null == m_webCtx) {

            m_webCtx = new FleetXmlWebApplicationContext();
            m_webCtx.setConfigLocations(getConfigLocations());
            m_webCtx.setServletContext(getServletContext());
            m_webCtx.refresh();
        }
    }

    protected String[] getConfigLocations() {
        return DEFAULT_CONFIG_FILES;
    }

    protected XmlWebApplicationContext getApplicationContext() {
        return m_webCtx;
    }

    public MockHttpServletRequest getRequest() {
        MockHttpServletRequest req = new MockHttpServletRequest(m_webCtx
                .getServletContext());
        req.setMethod("GET");
        req.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, m_webCtx);
        return req;
    }

    @Override
    protected ConfigurableApplicationContext loadContext(Object arg0) throws Exception {
        return getApplicationContext();
    }

    protected ServletContext getServletContext() {
        return m_servletContext;
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        EnvironmentInfo.clear();
        super.tearDown();
    }
}
