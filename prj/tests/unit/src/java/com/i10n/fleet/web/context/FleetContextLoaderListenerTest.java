package com.i10n.fleet.web.context;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.junit.Test;
import org.springframework.mock.web.MockServletContext;

import com.i10n.fleet.test.AbstractFleetTestCase;
import com.i10n.fleet.util.EnvironmentInfo;

/**
 * TestCases to test {@link FleetContextLoaderListener}
 * 
 * @author sabarish
 * 
 */
public class FleetContextLoaderListenerTest extends AbstractFleetTestCase {

    private ServletContext m_servletContext = null;
   

    /**
     * Tests the Custom {@link FleetContextLoaderListener} for context Loading
     */
    @Test
    public void testContextLoading() {
    	
        assertFalse(EnvironmentInfo.isLoaded());
        FleetContextLoaderListener listener = new FleetContextLoaderListener();
        listener.contextInitialized(new ServletContextEvent(getServletContext()));
    assertTrue(EnvironmentInfo.isLoaded());  
    }

    private ServletContext getServletContext() {
        if (null == m_servletContext) {
            String path = "file:"
                    + new File(getClass().getResource("/WEB-INF/applicationContext.xml")
                            .getFile()).getParentFile().getParent();
            m_servletContext = new MockServletContext(path);
        }
        return m_servletContext;
    }

}
